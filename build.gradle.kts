plugins {
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    id("org.jlleitschuh.gradle.ktlint") version "13.0.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    alias(libs.plugins.kover)
    id("com.vanniktech.maven.publish") version "0.36.0" apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
}

version = "1.1.0"

val moduleReportPaths = listOf(
    "imagepicker-core/build/reports/kover/report.xml",
    "imagepickerkmp-photo/build/reports/kover/report.xml",
    "imagepickerkmp-video/build/reports/kover/report.xml",
    "imagepickerkmp-audio/build/reports/kover/report.xml",
    "imagepickerkmp-audio-player/build/reports/kover/report.xml",
    "imagepickerkmp-scanner/build/reports/kover/report.xml",
    "imagepickerkmp-video-player/build/reports/kover/report.xml"
)

// Step 1: generate per-module XML reports
val generateModuleReports by tasks.registering {
    group = "verification"
    description = "Generates a Kover XML report for every module."
    dependsOn(
        ":imagepicker-core:koverXmlReport",
        ":imagepickerkmp-photo:koverXmlReport",
        ":imagepickerkmp-video:koverXmlReport",
        ":imagepickerkmp-audio:koverXmlReport",
        ":imagepickerkmp-audio-player:koverXmlReport",
        ":imagepickerkmp-scanner:koverXmlReport",
        ":imagepickerkmp-video-player:koverXmlReport"
    )
}

// Step 1b: generate per-module HTML reports (for local browsing)
val generateModuleHtmlReports by tasks.registering {
    group = "verification"
    description = "Generates a Kover HTML report for every module (open in browser)."
    dependsOn(
        ":imagepicker-core:koverHtmlReport",
        ":imagepickerkmp-photo:koverHtmlReport",
        ":imagepickerkmp-video:koverHtmlReport",
        ":imagepickerkmp-audio:koverHtmlReport",
        ":imagepickerkmp-audio-player:koverHtmlReport",
        ":imagepickerkmp-scanner:koverHtmlReport",
        ":imagepickerkmp-video-player:koverHtmlReport"
    )
    doLast {
        val reports = moduleReportPaths.map {
            it.replace("report.xml", "html/index.html")
        }.map { rootProject.file(it) }.filter { it.exists() }
        println("\n📊 Kover HTML reports:")
        reports.forEach { println("   file://${it.absolutePath}") }
    }
}

// Step 2: merge all XML files into one aggregated report
val koverMergedXmlReport by tasks.registering {
    group = "verification"
    description = "Merges per-module Kover XML reports into build/reports/kover/report.xml."
    dependsOn(generateModuleReports)

    val outputFile = layout.buildDirectory.file("reports/kover/report.xml")
    outputs.file(outputFile)

    doLast {
        val found = moduleReportPaths
            .map { rootProject.file(it) }
            .filter { it.exists() }

        if (found.isEmpty()) {
            logger.warn("koverMergedXmlReport: no per-module report.xml files found — skipping merge.")
            return@doLast
        }
        logger.lifecycle("koverMergedXmlReport: merging ${found.size} report(s)…")

        val doc = javax.xml.parsers.DocumentBuilderFactory.newInstance()
            .newDocumentBuilder().newDocument()
        val root = doc.createElement("report").also {
            it.setAttribute("name", "ImagePickerKMP")
            doc.appendChild(it)
        }

        var totalMissed = 0L
        var totalCovered = 0L

        found.forEach { file ->
            val src = javax.xml.parsers.DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(file).documentElement

            // Copy every <package> element into the merged doc
            val packages = src.getElementsByTagName("package")
            for (i in 0 until packages.length) {
                root.appendChild(doc.importNode(packages.item(i), true))
            }

            // Accumulate LINE counters from the top-level <counter> elements
            val counters = src.getElementsByTagName("counter")
            for (i in 0 until counters.length) {
                val c = counters.item(i) as org.w3c.dom.Element
                if (c.getAttribute("type") == "LINE" && c.parentNode == src) {
                    totalMissed  += c.getAttribute("missed").toLongOrNull()  ?: 0L
                    totalCovered += c.getAttribute("covered").toLongOrNull() ?: 0L
                }
            }
        }

        // Append aggregated LINE counter
        val aggCounter = doc.createElement("counter").also {
            it.setAttribute("type", "LINE")
            it.setAttribute("missed",  totalMissed.toString())
            it.setAttribute("covered", totalCovered.toString())
            root.appendChild(it)
        }

        val total = totalMissed + totalCovered
        val pct = if (total > 0) totalCovered * 100.0 / total else 0.0
        logger.lifecycle(
            "koverMergedXmlReport: aggregated %.1f%% (%d/%d lines covered)".format(pct, totalCovered, total)
        )

        // Write output
        val outFile = outputFile.get().asFile
        outFile.parentFile.mkdirs()
        val transformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer().also {
            it.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes")
            it.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8")
        }
        transformer.transform(
            javax.xml.transform.dom.DOMSource(doc),
            javax.xml.transform.stream.StreamResult(outFile)
        )
        logger.lifecycle("koverMergedXmlReport: merged report written → ${outFile.absolutePath}")
    }
}
