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
    id("com.vanniktech.maven.publish")
    alias(libs.plugins.kover)
}

version = "0.0.3"

kover {
    reports {
        filters {
            excludes {
                annotatedBy("androidx.compose.runtime.Composable")
                classes(
                    "*ActualResourceCollectors*",
                    "*String0*",
                    "*Res*"
                )
                classes(
                    "io.github.ismoy.imagepickerkmp.core.logger.MediaLogger",
                    "io.github.ismoy.imagepickerkmp.core.filesystem.FileSystemManager",
                    "io.github.ismoy.imagepickerkmp.core.permissions.PermissionManager"
                )
                classes(
                    "io.github.ismoy.imagepickerkmp.core.language.*"
                )
                classes(
                    "io.github.ismoy.imagepickerkmp.core.CoreInitializer*",
                    "io.github.ismoy.imagepickerkmp.core.CoreServicesHolder*",
                    "io.github.ismoy.imagepickerkmp.core.filesystem.AndroidFileSystemManager*",
                    "io.github.ismoy.imagepickerkmp.core.filesystem.AndroidPlatformFile*",
                    "io.github.ismoy.imagepickerkmp.core.filesystem.IosFileSystemManager*",
                    "io.github.ismoy.imagepickerkmp.core.filesystem.IosPlatformFile*",
                    "io.github.ismoy.imagepickerkmp.core.permissions.AndroidPermissionManager*",
                    "io.github.ismoy.imagepickerkmp.core.permissions.IosPermissionManager*",
                    "io.github.ismoy.imagepickerkmp.core.uri.*"
                )
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

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

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

    jvm {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_21)
                }
            }
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(IR) {
        browser {
            testTask {
                enabled = false
            }
        }
        nodejs {
            testTask {
                enabled = false
            }
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            testTask {
                enabled = false
            }
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        target.binaries.framework {
            baseName = "imagepickerCore"
            isStatic = true
            binaryOption("bundleId", "io.github.ismoy.imagepickerkmp.core")
        }
    }

    withSourcesJar(true)

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
        }
        androidMain.dependencies {
            implementation("androidx.core:core-ktx:1.13.1")
            implementation("com.ionspin.kotlin:bignum:0.3.10")
        }
        commonTest.dependencies {
            implementation(libs.kotest.framework.engine)
            implementation(libs.kotest.property)
            implementation(libs.kotest.assertions.core)
            implementation("org.jetbrains.kotlin:kotlin-test:2.3.20")

        }
        val jvmTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test:2.3.20")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
                implementation("io.kotest:kotest-runner-junit5:5.9.1")
            }
        }
    }
}

android {
    namespace = "io.github.ismoy.imagepickerkmp.core"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

mavenPublishing {
    coordinates(
        groupId = "io.github.ismoy",
        artifactId = "imagepicker-core",
        version = project.version.toString()
    )
    pom {
        name.set("ImagePickerKMP Core")
        description.set("Shared infrastructure for the ImagePickerKMP ecosystem: permissions, filesystem, URI, camera/gallery interfaces, and logger.")
        inceptionYear.set("2026")
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
            if (mavenPublication != null) {
                if (mavenPublication.name == "kotlinMultiplatform") {
                    mavenPublication.artifactId = "imagepicker-core"
                }
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

