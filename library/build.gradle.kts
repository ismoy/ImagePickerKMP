import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.gradle.api.publish.maven.tasks.PublishToMavenLocal
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    id("com.vanniktech.maven.publish") version "0.33.0"
    id("maven-publish")
    id("jacoco")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.dokka") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.22"
}

version = rootProject.version

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
        "**/domain/models/**",
        "**/domain/utils/**",
        "**/domain/exceptions/**",
        "**/presentation/viewModel/**",
        "**/presentation/resources/**"
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
    targetHierarchy.default()
    
    androidTarget {
        publishLibraryVariants("release")
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    
    // JS target for web compatibility and NPM publishing
    js(IR) {
        browser {
            testTask {
                enabled = false // Disable tests due to skiko.mjs dependency issues
            }
            // Configuration for web development
            webpackTask {
                cssSupport {
                    enabled.set(true)
                }
            }
            runTask {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
        nodejs {
            testTask {
                enabled = false // Disable Node.js tests due to skiko.mjs dependency issues
            }
        }
        
        // Generate library for NPM package usage
        binaries.library()
        
        // Complete package.json configuration for NPM
        compilations["main"].packageJson {
            // Basic package information
            name = "imagepickerkmp"
            version = project.version.toString() // Automatically takes the project version
            
            // Metadata
            customField("description", "ImagePicker KMP library with camera support for React/Vue/Angular applications")
            customField("keywords", arrayOf(
                "image-picker", 
                "camera", 
                "react", 
                "vue", 
                "angular",
                "kotlin", 
                "multiplatform",
                "webrtc",
                "photo-capture",
                "file-picker"
            ))
            customField("author", mapOf(
                "name" to "ismoy",
                "url" to "https://github.com/ismoy"
            ))
            customField("license", "MIT")
            customField("homepage", "https://github.com/ismoy/ImagePickerKMP")
            customField("repository", mapOf(
                "type" to "git",
                "url" to "https://github.com/ismoy/ImagePickerKMP.git"
            ))
            customField("bugs", mapOf(
                "url" to "https://github.com/ismoy/ImagePickerKMP/issues"
            ))
            
            // File configuration - point to the single bundle
            customField("main", "ImagePickerKMP-bundle.js")
            customField("types", "ImagePickerKMP-bundle.d.ts")
            customField("module", "ImagePickerKMP-bundle.js")
            customField("browser", "ImagePickerKMP-bundle.js")
            
            // Files included in the package
            customField("files", arrayOf(
                "kotlin/",
                "*.md",
                "package.json",
                "ImagePickerKMP-bundle.js",
                "ImagePickerKMP-bundle.d.ts"
            ))
            
            // Engines and compatibility
            customField("engines", mapOf(
                "node" to ">=14.0.0"
            ))
            
            // Configuration for bundlers
            customField("sideEffects", false)
            
            // Useful NPM scripts
            customField("scripts", mapOf(
                "test" to "echo \"No test specified\"",
                "build" to "echo \"Already built\"",
                "prepublishOnly" to "echo \"Package ready for publishing\""
            ))
            
            // Peer dependencies for React (optional)
            customField("peerDependencies", mapOf(
                "react" to ">=16.8.0"
            ))
            customField("peerDependenciesMeta", mapOf(
                "react" to mapOf("optional" to true)
            ))
        }
    }
    
    // WASM target for modern web compatibility
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            // Disable tests temporarily due to skiko.mjs resolution issues in Compose Multiplatform WASM
            testTask {
                enabled = false
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
                implementation("org.jetbrains.compose.material:material-icons-core:1.7.3")
                implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
                implementation(libs.components.resources)
                // OCR dependencies
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
                // Ktor dependencies for HTTP client
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)
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
                implementation(libs.androidx.material.icons.extended)
                // EXIF interface for metadata extraction
                implementation(libs.androidx.exifinterface)
                 // Ktor Android engine
                implementation(libs.ktor.client.okhttp)
            }
        }
        val iosResourcesDir =
            project.findProperty("iosResourcesDir") as? String ?: "src/iosMain/resources"
        iosMain {
            resources.srcDirs(iosResourcesDir)
            tasks.withType<ProcessResources> {
                duplicatesStrategy = DuplicatesStrategy.INCLUDE
            }
            dependencies {
                // Ktor iOS engine
                implementation(libs.ktor.client.darwin)
            }
        }
        
        val jvmMain by getting {
            dependencies {
                implementation(libs.compose.ui)
                implementation(libs.compose.material)
                implementation(libs.compose.foundation)
                implementation(libs.compose.runtime)
                implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.0")
            }
        }
        
        val jsMain by getting {
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.ui)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material)
                implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
            }
        }
        
        val wasmJsMain by getting {
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.ui)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material)
                implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
            }
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
                    println("Localization resources copied to framework")
                } else {
                    println("WARNING: Framework or resources directory not found")
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
                
                implementation("io.mockk:mockk:1.13.8")
                implementation("io.mockk:mockk-android:1.13.8")
                
                // Coroutines test
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
                
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
        
        val jvmTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test:2.1.21")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
                implementation("junit:junit:4.13.2")
            }
        }
        
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
                test.ignoreFailures = true
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
        version = project.version.toString()
    )
    pom {
        name.set("ImagePickerKMP")
        description.set(
            "ImagePickerKMP is the leading Kotlin Multiplatform (KMP) library for image picking and camera capture " +
                    "on Android and iOS. It provides a simple, unified API that integrates seamlessly with Jetpack Compose " +
                    "Multiplatform and SwiftUI. ImagePickerKMP includes customizable UI components, automatic permission " +
                    "handling, and high-quality camera and gallery support. This library is the best choice for developers " +
                    "who need a reliable Kotlin image picker and camera solution across platforms."
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
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.insert-koin:koin-test-junit4:3.5.0")
    
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("org.mockito:mockito-android:5.8.0")
    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
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

// ========================================
// SPECIFIC TASKS FOR NPM PUBLISHING
// ========================================

// Directory where JS packages are generated
val jsPackageOutputDir = layout.buildDirectory.dir("js/packages/ImagePickerKMP-library")

// Task to prepare the NPM package
tasks.register<Copy>("prepareNpmPackage") {
    group = "npm"
    description = "Prepares the NPM package with additional files"
    
    dependsOn("jsJar", "compileKotlinJs")
    
    // Copy additional files to package
    from(rootProject.file("README.md")) {
        into(".")
    }
    from(rootProject.file("LICENSE")) {
        into(".")
    }
    from(projectDir.resolve("src/jsMain/resources")) {
        into("resources")
        include("**/*")
    }
    
    // Copy the main JavaScript library file and package.json
    from(layout.buildDirectory.dir("dist/js/productionLibrary")) {
        into("kotlin")
        include("ImagePickerKMP-library.js")
    }
    from(layout.buildDirectory.dir("dist/js/productionLibrary")) {
        into(".")
        include("package.json")
    }
    
    // Create NPM-specific files
    doLast {
        val packageDir = jsPackageOutputDir.get().asFile
        
        // Create NPM-specific README
        val npmReadme = File(packageDir, "README.md")
        if (!npmReadme.exists()) {
            npmReadme.writeText("""
# ImagePickerKMP - JavaScript Package

A Kotlin Multiplatform library for image picking with camera support, compiled to JavaScript for use in web applications.

## Quick Start

```javascript
// Import the library
const { createImagePicker, isMobileDevice, hasCameraSupport } = require('imagepickerkmp');

// Create picker instance
const picker = createImagePicker();

// Use in your application
const images = await picker.openPicker({
  allowMultiple: true,
  preferCamera: true
});
```

## Documentation

For complete documentation, examples, and integration guides, visit:
https://github.com/ismoy/ImagePickerKMP

## Features

- Camera capture with WebRTC
- File selection
- Mobile-first design
- Desktop compatibility
- React/Vue/Angular ready
- TypeScript definitions included

## License

MIT License - see LICENSE file for details
            """.trimIndent())
        }
        
        val dtsFile = File(packageDir, "kotlin/imagepickerkmp.d.ts")
        dtsFile.parentFile.mkdirs()
        dtsFile.writeText("""
declare module 'imagepickerkmp' {
  export interface PhotoResult {
    uri: string;
    width: number;
    height: number;
    fileName: string;
    fileSize: number;
  }
  
  export interface ImagePickerOptions {
    allowMultiple?: boolean;
    maxImages?: number;
    preferCamera?: boolean;
    quality?: number;
    maxWidth?: number;
    maxHeight?: number;
    mimeTypes?: string[];
  }
  
  export interface CameraOptions {
    quality?: number;
    maxWidth?: number;
    maxHeight?: number;
    facingMode?: 'user' | 'environment';
  }
  
  export interface FilePickerOptions {
    allowMultiple?: boolean;
    mimeTypes?: string[];
    maxFileSize?: number;
  }
  
  export interface ImagePickerInstance {
    openPicker(options?: ImagePickerOptions): Promise<PhotoResult[]>;
    capturePhoto(options?: CameraOptions): Promise<PhotoResult[]>;
    selectFiles(options?: FilePickerOptions): Promise<PhotoResult[]>;
    cleanup(): void;
  }
  
  export function createImagePicker(): ImagePickerInstance;
  export function isMobileDevice(): boolean;
  export function hasCameraSupport(): boolean;
}
            """.trimIndent())
    }
    
    into(jsPackageOutputDir)
}

tasks.register("validateNpmPackage") {
    group = "npm"
    description = "Validates that the NPM package is complete and correct"
    
    dependsOn("prepareNpmPackage")
    
    doLast {
        val packageDir = jsPackageOutputDir.get().asFile
        val packageJson = File(packageDir, "package.json")
        val mainJs = File(packageDir, "kotlin/ImagePickerKMP-library.js")
        val typeDefs = File(packageDir, "kotlin/imagepickerkmp.d.ts")
        
        println("Validating NPM package...")
        
        val requiredFiles = listOf(
            packageJson to "package.json",
            mainJs to "kotlin/ImagePickerKMP-library.js", 
            typeDefs to "kotlin/imagepickerkmp.d.ts"
        )
        
        var allValid = true
        requiredFiles.forEach { (file, name) ->
            if (file.exists()) {
                println("OK: $name")
            } else {
                println("MISSING: $name")
                allValid = false
            }
        }
        
        if (mainJs.exists()) {
            val sizeKB = mainJs.length() / 1024
            println("Bundle size: ${sizeKB}KB")
            if (sizeKB > 500) {
                println("WARNING: Bundle size is large (${sizeKB}KB)")
            }
        }
        
        if (allValid) {
            println("NPM Package validation passed!")
            println("Package location: $packageDir")
        } else {
            throw GradleException("NPM Package validation failed!")
        }
    }
}

tasks.register<Exec>("publishNpmLocal") {
    group = "npm"
    description = "Publishes the package to local NPM for testing"
    
    dependsOn("buildNpmZeroConfig")
    
    // Use production library directory with the bundle
    val productionLibDir = layout.buildDirectory.dir("dist/js/productionLibrary").get().asFile
    workingDir(productionLibDir)
    commandLine("npm", "pack")
    
    doLast {
        val packageDir = productionLibDir
        val tarFiles = packageDir.listFiles { _, name -> name.endsWith(".tgz") }
        
        if (tarFiles?.isNotEmpty() == true) {
            println(" Package created: ${tarFiles.first().name}")
            println(" Location: ${tarFiles.first().absolutePath}")
            println(" To install locally run:")
            println(" npm install ${tarFiles.first().absolutePath}")
            println("")
            println(" Or create a test project:")
            println("   mkdir test-project && cd test-project")
            println("   npm init -y")
            println("   npm install ${tarFiles.first().absolutePath}")
        } else {
            println(" No .tgz files found in ${packageDir}")
        }
    }
}

tasks.register<Exec>("publishNpm") {
    group = "npm"
    description = "Publishes the package to the public NPM registry"
    
    dependsOn("buildNpmZeroConfig")
    
    // Use production library directory with the bundle
    val productionLibDir = layout.buildDirectory.dir("dist/js/productionLibrary").get().asFile
    workingDir(productionLibDir)
    commandLine("npm", "publish")
    
    doFirst {
        println(" Publishing to NPM registry...")
        println(" From: ${productionLibDir}")
    }
    
    doLast {
        println(" Package published successfully!")
        println(" Install with: npm install imagepickerkmp")
    }
}

// Task to clean NPM packages
tasks.register<Delete>("cleanNpmPackages") {
    group = "npm" 
    description = "Cleans generated NPM packages"
    
    delete(layout.buildDirectory.dir("js"))
}

// Helper task to show package information
tasks.register("npmPackageInfo") {
    group = "npm"
    description = "Shows information about the generated NPM package"
    
    dependsOn("prepareNpmPackage")
    
    doLast {
        val packageDir = jsPackageOutputDir.get().asFile
        val packageJson = File(packageDir, "package.json")
        
        if (packageJson.exists()) {
            println("Package NPM Information:")
            println("Location: $packageDir")
            println("Package.json preview:")
            println(packageJson.readText().lines().take(20).joinToString("\n"))
        } else {
            println("Package not found. Run 'prepareNpmPackage' first.")
        }
    }
}

// Task to verify version synchronization
tasks.register("checkVersionSync") {
    group = "verification"
    description = "Verifies that all versions are synchronized"
    
    doLast {
        val projectVersion = project.version.toString()
        val rootVersion = rootProject.version.toString()
        
        println("Checking version synchronization...")
        println("Root project: $rootVersion")
        println("Library project: $projectVersion")
        
        // Check Maven configuration
        println("Maven coordinates: io.github.ismoy:imagepickerkmp:$projectVersion")
        
        // Verify versions match
        if (projectVersion == rootVersion) {
            println("All versions are synchronized")
            println("NPM will automatically take version: $projectVersion")
        } else {
            println("WARNING: Versions are desynchronized!")
            println("   Root: $rootVersion")
            println("   Library: $projectVersion")
        }
    }
}

// Task to generate zero-config NPM bundle
tasks.register<Exec>("createZeroConfigBundle") {
    group = "npm"
    description = "Creates a zero-config NPM bundle with all dependencies included"
    
    dependsOn("jsBrowserProductionLibraryDistribution")
    
    workingDir = projectDir.parentFile
    commandLine("node", "create-bundle.js")
    
    doFirst {
        println("Generating zero-config bundle...")
    }
    
    doLast {
        // Try both possible locations for the bundle - relative to project root
        val rootBuildDir = File(projectDir.parentFile, "build")
        val bundleFile1 = File(rootBuildDir, "js/packages/ImagePickerKMP-library/ImagePickerKMP-bundle.js")
        val bundleFile2 = File(rootBuildDir, "js/packages/library-library/ImagePickerKMP-bundle.js")
        
        val bundleFile = when {
            bundleFile1.exists() -> bundleFile1
            bundleFile2.exists() -> bundleFile2
            else -> null
        }
        
        if (bundleFile != null && bundleFile.exists()) {
            val sizeKB = bundleFile.length() / 1024
            println("Zero-config bundle found!")
            println("Size: ${sizeKB} KB")
            println("Location: ${bundleFile.absolutePath}")
            
            // Copy to the production library directory for npm publishing
            val productionLibDir = layout.buildDirectory.dir("dist/js/productionLibrary").get().asFile
            val targetBundle = File(productionLibDir, "ImagePickerKMP-bundle.js")
            productionLibDir.mkdirs()
            bundleFile.copyTo(targetBundle, overwrite = true)
            println("Bundle copied to production library directory: ${targetBundle.absolutePath}")
            
            // Also copy TypeScript declarations if they exist
            val bundleTypeFile1 = File(rootBuildDir, "js/packages/ImagePickerKMP-library/ImagePickerKMP-bundle.d.ts")
            val bundleTypeFile2 = File(rootBuildDir, "js/packages/library-library/ImagePickerKMP-bundle.d.ts")
            
            val bundleTypeFile = when {
                bundleTypeFile1.exists() -> bundleTypeFile1
                bundleTypeFile2.exists() -> bundleTypeFile2
                else -> null
            }
            
            if (bundleTypeFile != null && bundleTypeFile.exists()) {
                val targetTypeFile = File(productionLibDir, "ImagePickerKMP-bundle.d.ts")
                bundleTypeFile.copyTo(targetTypeFile, overwrite = true)
                println("Bundle TypeScript declarations copied: ${targetTypeFile.absolutePath}")
            }
        } else {
            println("Warning: Bundle file not found in any expected location")
            println("Checked: ${bundleFile1.absolutePath}")
            println("Checked: ${bundleFile2.absolutePath}")
        }
    }
}

// Main task for zero-config NPM
tasks.register("buildNpmZeroConfig") {
    group = "npm"
    description = "Builds a complete zero-config NPM package"
    
    dependsOn("createZeroConfigBundle")
    
    doLast {
        val packageDir = layout.buildDirectory.dir("js/packages/${project.name}-library").get().asFile
        println("Zero-config NPM package ready!")
        println("Location: ${packageDir.absolutePath}")
        println("Install with: npm install ${packageDir.absolutePath}")
        println("")
        println("Usage example:")
        println("   // ES6 Import")
        println("   import ImagePickerKMP from 'imagepickerkmp';")
        println("")  
        println("   // CommonJS")
        println("   const ImagePickerKMP = require('imagepickerkmp');")
        println("")
        println("   // Usage")
        println("   ImagePickerKMP.ImagePickerLauncher(onSuccess, onError, onCancel);")
        println("")
        println("Test: open ${packageDir.absolutePath}/test-zero-config.html")
    }
}

// Add clean dependencies
tasks.named("clean") {
    dependsOn("cleanNpmPackages")
}

// AUTOMATIC CONFIGURATION: Run buildNpmZeroConfig automatically
// Make 'assemble' always generate the NPM bundle
tasks.named("assemble") {
    finalizedBy("buildNpmZeroConfig")
}

// Make 'build' also generate the NPM bundle
tasks.named("build") {
    finalizedBy("buildNpmZeroConfig")
}

// Make 'publishToMavenLocal' generate the NPM bundle before publishing
tasks.withType<PublishToMavenLocal> {
    dependsOn("buildNpmZeroConfig")
}
