import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeCompiler)
    id("com.vanniktech.maven.publish") version "0.30.0"
    id("maven-publish")
    id("jacoco")
    id("io.gitlab.arturbosch.detekt")
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport")
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("test")
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "**/databinding/*",
        "**/android/databinding/*",
        "**/androidx/databinding/*",
        "**/BR.*"
    )
    classDirectories.setFrom(
        files(
            fileTree("$buildDir/tmp/kotlin-classes/debug") {
                exclude(fileFilter)
            }
        )
    )
    sourceDirectories.setFrom(files("src/commonMain/kotlin", "src/androidMain/kotlin"))
    executionData.setFrom(
        fileTree("$buildDir") {
            include("jacoco/*.exec")
        }
    )
}

tasks.register<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn("jacocoTestReport")
    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.04".toBigDecimal()
            }
        }
        rule {
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.00".toBigDecimal()
            }
        }
    }
    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "**/databinding/*",
        "**/android/databinding/*",
        "**/androidx/databinding/*",
        "**/BR.*"
    )
    classDirectories.setFrom(
        files(
            fileTree("$buildDir/tmp/kotlin-classes/debug") {
                exclude(fileFilter)
            }
        )
    )
    sourceDirectories.setFrom(files("src/commonMain/kotlin", "src/androidMain/kotlin"))
    executionData.setFrom(files("$buildDir/jacoco/test.exec"))
}

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    kotlin.applyDefaultHierarchyTemplate()
    androidTarget {
        publishLibraryVariants("release")
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        target.binaries.framework {
            baseName = "imagepickerkmp"
            isStatic = true
            binaryOption("bundleId", "io.github.ismoy.imagepickerkmp")
            export(libs.compose.runtime)
        }
        target.mavenPublication {}
    }
    withSourcesJar(true)
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.compose.runtime)
                implementation(libs.compose.ui)
                implementation(libs.compose.animation)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material)
                implementation(libs.kotlinx.coroutines.core.v1102)
                implementation("io.coil-kt.coil3:coil-compose:3.2.0")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.camera.core)
                implementation(libs.androidx.camera.camera2)
                implementation(libs.androidx.camera.lifecycle)
                implementation(libs.androidx.camera.view)
                implementation(libs.accompanist.permissions)
                implementation(libs.core)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.androidx.ui)
                implementation(libs.androidx.ui.tooling.preview)
                implementation("androidx.compose.material:material-icons-extended:1.5.4")
            }
        }
        val iosResourcesDir =
            project.findProperty("iosResourcesDir") as? String ?: "src/iosMain/resources"
        iosMain {
            resources.srcDirs(iosResourcesDir)
            tasks.withType<ProcessResources> {
                duplicatesStrategy = DuplicatesStrategy.INCLUDE
            }
            resources.srcDir("src/commonMain/resources")
            dependencies {}
        }
        
        tasks.register("copyLocalizationResources") {
            dependsOn("linkReleaseFrameworkIosArm64")
            
            doLast {
                val frameworkDir = file("build/xcode-frameworks/konan/iosArm64/imagepickerkmp.framework")
                val resourcesDir = file("src/iosMain/resources")
                
                if (frameworkDir.exists() && resourcesDir.exists()) {
                    copy {
                        from(resourcesDir)
                        into("${frameworkDir}/Resources")
                        include("**/*.lproj/**")
                    }
                    println("✅ Localization resources copied to framework")
                } else {
                    println("⚠️  Framework or resources directory not found")
                }
            }
        }
        all {
            languageSettings {
                optIn("kotlin.ExperimentalMultiplatform")
                optIn("kotlin.ExperimentalUnsignedTypes")
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
                implementation("androidx.compose.ui:ui-test-junit4:1.5.4")
                implementation("androidx.test:core:1.5.0")
                implementation("androidx.test.ext:junit:1.1.5")
                implementation("androidx.compose.material3:material3:1.2.0")
            }
        }
        val androidInstrumentedTest by getting {}
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

android {
    namespace = "io.github.ismoy"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

// --- Load gradle.local.properties if exists (for local sensitive data) ---
val localPropertiesFile = rootProject.file("gradle.local.properties")
if (localPropertiesFile.exists()) {
    println("Loading gradle.local.properties")
    localPropertiesFile.forEachLine { line ->
        if (line.isNotBlank() && !line.trim().startsWith("#")) {
            val (key, value) = line.split("=", limit = 2)
            project.extra.set(key.trim(), value.trim())
        }
    }
}

mavenPublishing{
    coordinates(
        groupId = "io.github.ismoy",
        artifactId = "imagepickerkmp",
        version = "1.0.0"
    )
    pom {
        name.set("ImagePickerKMP")
        description.set(
            "ImagePickerKMP is a modern Kotlin Multiplatform library for image picking and camera capture, " +
                    "designed for Android and iOS. It offers seamless integration with Jetpack Compose and SwiftUI, " +
                    "customizable UI components, automatic permission handling, and a unified API. " +
                    "Add high-quality camera and gallery features to your KMP app with minimal effort."
        )
        inceptionYear.set("2025")
        url.set("https://github.com/ismoy/ImagePickerKMP")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://github.com/ismoy/ImagePickerKMP/tree/develop?tab=License-1-ov-file")
            }
        }
        developers {
            developer {
                id.set("ismoy")
                name.set("Ismoy Belizaire")
                email.set("belizairesmoy72@gmail.com")
            }
        }
        scm {
            url.set("https://github.com/ismoy/ImagePickerKMP")
        }
    }
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
}
afterEvaluate {
    publishing {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://github.com/ismoy/ImagePickerKMP")
                credentials {
                    username = System.getenv("GITHUB_ACTOR") ?: ""
                    password = System.getenv("GITHUB_TOKEN") ?: ""
                }
            }
        }

        publications.forEach { publication ->
            val mavenPublication = publication as? MavenPublication
            if (mavenPublication != null) {
                if (mavenPublication.name == "kotlinMultiplatform") {
                    mavenPublication.artifactId = "imagepickerkmp"
                } else {
                    println("Leaving platform-specific artifactId: ${mavenPublication.artifactId}")
                }

                println("Configured publication: ${mavenPublication.name}, artifactId: ${mavenPublication.artifactId}")
            }
        }
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("junit:junit:4.13.2")
}

// Detekt configuration
detekt {
    config.setFrom(files("${rootProject.projectDir}/detekt.yml"))
    source.setFrom(
        files(
            "src/commonMain/kotlin",
            "src/androidMain/kotlin",
            "src/iosMain/kotlin"
        )
    )
    buildUponDefaultConfig = true
    allRules = false
    disableDefaultRuleSets = false
    debug = false
    parallel = true
    ignoreFailures = false
    reports {
        html.enabled = true
        xml.enabled = true
        txt.enabled = true
        sarif.enabled = true
    }
}
