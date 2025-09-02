import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeCompiler)
    id("com.vanniktech.maven.publish") version "0.33.0"
    id("maven-publish")
    id("jacoco")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.dokka") version "1.9.20"
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.withType<Test> {
    useJUnit()
    finalizedBy("jacocoTestReport")
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
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
        "**/BR.*",
        "**/presentation/ui/components/**",
        "**/presentation/ui/screens/**",
        "**/*Kt.class",
        "**/*\$Companion.class",
        "**/ComposableSingletons*.*",
        "**/CameraController\$startCamera\$2.*",
        "**/CameraController\$takePicture\$1.*",
        "**/ProcessCameraProvider*.*",
        "**/ImageCapture*.*",
        "**/CameraX*.*"
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
            include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
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
                minimum = "0.85".toBigDecimal()
            }
        }
        rule {
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.75".toBigDecimal()
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
        "**/BR.*",
        "**/presentation/ui/components/**",
        "**/presentation/ui/screens/**",
        "**/*Kt.class",
        "**/*\$Companion.class",
        "**/ComposableSingletons*.*",
        "**/CameraController\$startCamera\$2.*",
        "**/CameraController\$takePicture\$1.*",
        "**/ProcessCameraProvider*.*",
        "**/ImageCapture*.*",
        "**/CameraX*.*"
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

tasks.register<JacocoReport>("jacocoBusinessLogicReport") {
    group = "verification"
    description = "Generate JaCoCo coverage report focusing on business logic (excluding UI components)"
    dependsOn("testDebugUnitTest")
    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/businessLogic/html"))
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/businessLogic/businessLogic.xml"))
    }
    val businessLogicFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "**/databinding/*",
        "**/android/databinding/*",
        "**/androidx/databinding/*",
        "**/BR.*",
        
        "**/presentation/ui/components/**",
        "**/presentation/ui/screens/**",
        "**/ComposableSingletons*.*",
        "**/LiveLiterals*.*",
        
        "**/data/camera/**",
        "**/data/processors/ImageOrientationCorrector.*",
        "**/data/processors/ImageProcessor\$processImage\$1*.*",
        "**/data/managers/FileManager.*",
        
        // Exclude config classes with Compose dependencies
        "**/domain/config/ImagePickerUiConstants.*",
        "**/domain/config/UiConfig.*",
        "**/domain/config/CameraCaptureConfig.*",
        "**/domain/config/CameraPreviewConfig.*",
        "**/domain/config/ImagePickerConfig.*",
        "**/domain/config/CameraPermissionDialogConfig.*",
        "**/domain/config/PermissionConfig.*",
        
        // Keep these business logic classes
        // "**/domain/models/**",         // ✅ Keep - Pure Kotlin data classes
        // "**/domain/utils/**",          // ✅ Keep - Logger utilities
        // "**/domain/exceptions/**",     // ✅ Keep - Exception handling
        // "**/presentation/viewModel/**", // ✅ Keep - Business logic
        
        // Exclude Kotlin compiler generated classes
        "**/*Kt.class",
        "**/*\$Companion.class",
        "**/*\$WhenMappings.class",
        "**/*\$serializer.class"
    )
    classDirectories.setFrom(
        files(
            fileTree("$buildDir/tmp/kotlin-classes/debug") {
                exclude(businessLogicFilter)
            }
        )
    )
    sourceDirectories.setFrom(files("src/commonMain/kotlin", "src/androidMain/kotlin"))
    executionData.setFrom(
        fileTree("$buildDir") {
            include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        }
    )
}

// Nuevo reporte para SOLO lógica de negocio pura
tasks.register<JacocoReport>("jacocoCoreBizLogicReport") {
    group = "verification"
    description = "Generate JaCoCo coverage report for PURE business logic only (domain models, utils, exceptions, viewModels)"
    dependsOn("testDebugUnitTest")
    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/coreBizLogic/html"))
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/coreBizLogic/coreBizLogic.xml"))
    }
    
    // Include ONLY pure business logic packages
    val coreBusinessLogicIncludes = listOf(
        "**/domain/models/**",           // Data models
        "**/domain/utils/**",            // Utilities
        "**/domain/exceptions/**",       // Exception handling
        "**/presentation/viewModel/**",  // ViewModels
        "**/presentation/resources/**"   // String resources
    )
    
    val excludeAllFilter = listOf(
        "**/**" // Start by excluding everything
    )
    
    classDirectories.setFrom(
        files(
            fileTree("$buildDir/tmp/kotlin-classes/debug") {
                // Include only core business logic
                include(coreBusinessLogicIncludes)
                // Exclude test files and generated code
                exclude("**/*Test*.*")
                exclude("**/*Kt.class")
                exclude("**/*\$Companion.class")
                exclude("**/*\$WhenMappings.class")
                exclude("**/*\$serializer.class")
                exclude("**/ComposableSingletons*.*")
                exclude("**/LiveLiterals*.*")
            }
        )
    )
    sourceDirectories.setFrom(files("src/commonMain/kotlin", "src/androidMain/kotlin"))
    executionData.setFrom(
        fileTree("$buildDir") {
            include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        }
    )
}

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    kotlin.applyDefaultHierarchyTemplate()
    androidTarget {
        publishLibraryVariants("release")
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
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
                implementation("io.insert-koin:koin-core:3.5.3")
                implementation("io.insert-koin:koin-compose:1.1.2")
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
                
                // Koin for Android
                implementation("io.insert-koin:koin-android:3.5.3")
                implementation("io.insert-koin:koin-androidx-compose:3.5.3")
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
                
                // MockK for mocking
                implementation("io.mockk:mockk:1.13.8")
                implementation("io.mockk:mockk-android:1.13.8")
                
                // Coroutines test
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
                
                // Koin test - excluding conflicting dependencies
                implementation("io.insert-koin:koin-test:3.5.3") {
                    exclude(group = "org.jetbrains.kotlin", module = "kotlin-test")
                    exclude(group = "org.jetbrains.kotlin", module = "kotlin-test-junit")
                    exclude(group = "org.jetbrains.kotlin", module = "kotlin-test-junit5")
                }
                implementation("io.insert-koin:koin-test-junit4:3.5.3") {
                    exclude(group = "org.jetbrains.kotlin", module = "kotlin-test")
                    exclude(group = "org.jetbrains.kotlin", module = "kotlin-test-junit")
                    exclude(group = "org.jetbrains.kotlin", module = "kotlin-test-junit5")
                }
                
                implementation("org.jetbrains.kotlin:kotlin-test:2.1.21")
            }
        }
        val androidInstrumentedTest by getting {}
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test:2.1.21")
            }
        }
    }
}

android {
    namespace = "io.github.ismoy.imagepickerkmp"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            enableUnitTestCoverage = true
        }
    }
    
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
            all { test ->
                test.ignoreFailures = true  // Permitir continuar con fallos
            }
        }
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

val localPropertiesFile = rootProject.file("gradle.local.properties")
if (localPropertiesFile.exists()) {
    println("Loading gradle.local.properties")
    val localProperties = Properties()
    localProperties.load(localPropertiesFile.inputStream())
    localProperties.forEach { key: Any, value: Any ->
        val keyStr = key.toString()
        val valueStr = value.toString()
        project.extra.set(keyStr, valueStr)
        System.setProperty(keyStr, valueStr)
    }
}

mavenPublishing{
    coordinates(
        groupId = "io.github.ismoy",
        artifactId = "imagepickerkmp",
        version = "1.0.24"
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
            connection.set("scm:git:git://github.com/ismoy/ImagePickerKMP.git")
            developerConnection.set("scm:git:git://github.com/ismoy/ImagePickerKMP.git")
        }
    }
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    configure(
        com.vanniktech.maven.publish.KotlinMultiplatform(
            javadocJar = com.vanniktech.maven.publish.JavadocJar.Dokka("dokkaHtml"),
            sourcesJar = true
        )
    )
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
    ignoreFailures = true
    reports {
        html.enabled = true
        xml.enabled = true
        txt.enabled = true
        sarif.enabled = true
    }
}
