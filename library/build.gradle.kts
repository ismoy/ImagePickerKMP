import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeCompiler)
    id("com.vanniktech.maven.publish") version "0.30.0"
    id("maven-publish")
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
            dependencies {}
        }
        all {
            languageSettings {
                optIn("kotlin.ExperimentalMultiplatform")
                optIn("kotlin.ExperimentalUnsignedTypes")
            }
        }
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
}

mavenPublishing{
    coordinates(
        groupId = "io.github.ismoy",
        artifactId = "imagepickerkmp",
        version = "1.0.19"
    )
    pom {
        name.set("ImagePickerKMP")
        description.set("BelZSpeedScan is a comprehensive multiplatform barcode and QR code scanning" +
                " library for Android and iOS. It features optimized camera handling, automatic " +
                "distance adjustment, support for codes with reflective surfaces, and customizable " +
                "UI components with Compose. Easily integrate high-performance code scanning into " +
                "your Kotlin Multiplatform, Android, or iOS applications with a unified API.")
        inceptionYear.set("2025")
        url.set("https://github.com/ismoy/ImagePickerKMP")
        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("https://github.com/ismoy/ImagePickerKMP/blob/main/LICENSE")
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
    signAllPublications()
}
afterEvaluate {
    publishing {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/ismoy/BelZSpeedScan")
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
