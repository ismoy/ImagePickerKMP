import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SourcesJar
import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kover)
    alias(libs.plugins.i18nkonfig)
    id("com.vanniktech.maven.publish")
}

version = "1.0.0"

kover {
    reports {
        filters {
            excludes {
                annotatedBy("androidx.compose.runtime.Composable")
                classes(
                    "io.github.ismoy.imagepickerkmp.scanner.AndroidCameraXBinder*",
                    "io.github.ismoy.imagepickerkmp.scanner.AndroidScannerCaptureManager*",
                    "io.github.ismoy.imagepickerkmp.scanner.AndroidScannerFlashManager*",
                    "io.github.ismoy.imagepickerkmp.scanner.AndroidScannerSoundManager*",
                    "io.github.ismoy.imagepickerkmp.scanner.AndroidStaticCodeScanner*",
                    "io.github.ismoy.imagepickerkmp.scanner.MLKitBarcodeAnalyzer*",
                    "io.github.ismoy.imagepickerkmp.scanner.MLKitBarcodeFormatMapper*",
                    "io.github.ismoy.imagepickerkmp.scanner.PlatformScannerDependencies*",
                    "io.github.ismoy.imagepickerkmp.scanner.CreateScannerCaptureManager*",
                    "io.github.ismoy.imagepickerkmp.scanner.CreateStaticCodeScanner*",
                    "io.github.ismoy.imagepickerkmp.scanner.PlatformUtils*",
                    "io.github.ismoy.imagepickerkmp.scanner.ScannerBuilder*",
                    "io.github.ismoy.imagepickerkmp.scanner.ui.*",
                    "io.github.ismoy.imagepickerkmp.scanner.utils.BeepSoundKt*",
                    "io.github.ismoy.imagepickerkmp.scanner.utils.BeepSound_androidKt*",
                    "io.github.ismoy.imagepickerkmp.scanner.utils.BeepSound_jvmKt*",
                    "io.github.ismoy.imagepickerkmp.scanner.utils.TimeUtilsKt*",
                    "io.github.ismoy.imagepickerkmp.scanner.utils.TimeUtils_androidKt*",
                    "io.github.ismoy.imagepickerkmp.scanner.utils.TimeUtils_jvmKt*",
                    "io.github.ismoy.imagepickerkmp.scanner.utils.LoggerKt",
                    "io.github.ismoy.imagepickerkmp.scanner.camera.ScannerCameraStateHolder*",
                    "io.github.ismoy.imagepickerkmp.scanner.camera.ScannerInactivityManager*",
                    "io.github.ismoy.imagepickerkmp.scanner.picker.RememberScannerPickerKt",
                    "io.github.ismoy.imagepickerkmp.scanner.utils.ActiveScanningOverlayKt*",
                    "io.github.ismoy.imagepickerkmp.scanner.utils.InactiveScanningOverlayKt*",
                    "io.github.ismoy.imagepickerkmp.scanner.utils.EnterpriseInactiveOverlayKt*",
                    "io.github.ismoy.imagepickerkmp.scanner.I18nKonfig",
                    "io.github.ismoy.imagepickerkmp.scanner.I18nKonfig$*",
                    "*ActualResourceCollectors*",
                    "*Res*"                )
            }
        }
        total {
            xml { onCheck = true }
            html { onCheck = true }
            verify {
                onCheck = true
                rule {
                    bound {
                        minValue = 90
                        coverageUnits = CoverageUnit.LINE
                        aggregationForGroup = AggregationType.COVERED_PERCENTAGE
                    }
                }
            }
        }
    }
}

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate()

    androidTarget {
        publishLibraryVariants("release")
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_21)
                }
            }
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        target.binaries.framework {
            baseName = "imagepickerkmpScanner"
            isStatic = true
            binaryOption("bundleId", "io.github.ismoy.imagepickerkmp.scanner")
        }
    }

    jvm {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }
            }
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(IR) {
        browser { testTask { enabled = false } }
        nodejs { testTask { enabled = false } }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser { testTask { enabled = false } }
    }

    withSourcesJar(true)

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":imagepicker-core"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
            implementation(libs.material.icons.extended)
            implementation(libs.jetbrains.compose.ui.tooling.preview)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.camera.core)
            implementation(libs.androidx.camera.camera2)
            implementation(libs.androidx.camera.lifecycle)
            implementation(libs.androidx.camera.view)
            implementation(libs.zxing.android.embedded)
            implementation(libs.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.androidx.ui)
            implementation(libs.androidx.ui.tooling.preview)
            implementation(libs.barcode.scanning)
            implementation(libs.startup.runtime)
            implementation(libs.browser)
            implementation(libs.androidx.exifinterface)
        }
        jvmMain.dependencies {}
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.kotest.runner.junit5)
            }
        }
        jsMain.dependencies {}
        wasmJsMain.dependencies {}
        commonTest.dependencies {
            implementation(libs.kotest.framework.engine)
            implementation(libs.kotest.property)
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotlin.test)
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.junit)
                implementation(libs.mockk)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.androidx.test.core)
                implementation(libs.robolectric)
                implementation(libs.kotest.runner.junit5)
            }
        }
    }
}

dependencies {
    debugImplementation(libs.jetbrains.compose.ui.tooling)
}

android {
    namespace = "io.github.ismoy.imagepickerkmp.scanner"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "io.github.ismoy" && requested.name == "imagepickerkmp") {
            throw GradleException(
                "Module isolation violation: ':imagepickerkmp-scanner' must not depend on " +
                    "'io.github.ismoy:imagepickerkmp'. The imagepickerkmp-scanner module is independent and may only " +
                    "depend on ':imagepicker-core'. Remove the offending dependency."
            )
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = "io.github.ismoy",
        artifactId = "imagepickerkmp-scanner",
        version = project.version.toString()
    )
    pom {
        name.set("ImagePickerKMP Scanner")
        description.set("Barcode and QR code scanning for the ImagePickerKMP ecosystem. Supports Android and iOS via Kotlin Multiplatform.")
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
            connection.set("scm:git:git://github.com/ismoy/ImagePickerKMP.git")
            developerConnection.set("scm:git:git://github.com/ismoy/ImagePickerKMP.git")
        }
    }
    // Maven Central is released manually outside this workflow.
    publishToMavenCentral(automaticRelease = false)
    if (project.hasProperty("signing.keyId")) {
        signAllPublications()
    }
    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Empty(),
            sourcesJar = SourcesJar.Sources()
        )
    )
}

afterEvaluate {
    publishing {
        publications.forEach { publication ->
            val mavenPublication = publication as? MavenPublication
            if (mavenPublication?.name == "kotlinMultiplatform") {
                mavenPublication.artifactId = "imagepickerkmp-scanner"
            }
        }
    }
}

val localPropertiesFile = rootProject.file("gradle.local.properties")
if (localPropertiesFile.exists()) {
    val localProperties = Properties()
    localProperties.load(localPropertiesFile.inputStream())
    localProperties.forEach { key: Any, value: Any ->
        project.extra.set(key.toString(), value.toString())
        System.setProperty(key.toString(), value.toString())
    }
}

i18nKonfig {
    packageName = "io.github.ismoy.imagepickerkmp.scanner"
}
