import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SourcesJar
import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    id("com.vanniktech.maven.publish")
    id("maven-publish")
    alias(libs.plugins.kover)
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.dokka") version "2.1.0"
    kotlin("plugin.serialization") version "1.9.22"
}

version = "1.1.3"

kover {
    reports {
        filters {
            excludes {
                annotatedBy("androidx.compose.runtime.Composable")
                classes(
                    "io.github.ismoy.imagepickerkmp.generated.*",
                    "*ActualResourceCollectors*",
                    "*ExpectResourceCollectors*",
                    "*Res*",
                    "*String0*",
                    "*StringResource0*"
                )
                classes(
                    "io.github.ismoy.imagepickerkmp.gallery.*",
                    "io.github.ismoy.imagepickerkmp.camera.*",
                    "io.github.ismoy.imagepickerkmp.ui.*",
                    "io.github.ismoy.imagepickerkmp.domain.utils.JvmFilePickerUtilsKt",
                    "io.github.ismoy.imagepickerkmp.domain.utils.JvmFilePicker*",
                    "io.github.ismoy.imagepickerkmp.domain.utils.CreateFileChooserKt",
                    "io.github.ismoy.imagepickerkmp.domain.utils.CreateGalleryPhotoResultKt",
                    "io.github.ismoy.imagepickerkmp.picker.RememberImagePickerKMPKt",
                    "io.github.ismoy.imagepickerkmp.extensions.*",
                    "io.github.ismoy.imagepickerkmp.crop.ApplyCropKt",
                    "io.github.ismoy.imagepickerkmp.crop.ApplyCropUtilsKt",
                    "io.github.ismoy.imagepickerkmp.crop.ApplyCropUtilsKt\$*",
                    "io.github.ismoy.imagepickerkmp.crop.CreateCircularBitmapKt",
                    "io.github.ismoy.imagepickerkmp.crop.DrawCropHandlesKt",
                    "io.github.ismoy.imagepickerkmp.ui.OpenAppSettingsKt",
                    "io.github.ismoy.imagepickerkmp.logger.PhotoLogger"
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
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
                }
            }
        }
    }

    jvm {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
                }
            }
        }
    }
    
    js(IR) {
        browser {
            testTask {
                enabled = false
            }
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
                enabled = false
            }
        }
        
        binaries.library()
        compilations["main"].packageJson {
            name = "imagepickerkmp"
            version = project.version.toString()

            customField("description", "ImagePicker KMP imagepickerkmp-photo with camera support for React/Vue/Angular applications")
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
            
            customField("main", "ImagePickerKMP-bundle.js")
            customField("types", "ImagePickerKMP-bundle.d.ts")
            customField("module", "ImagePickerKMP-bundle.js")
            customField("browser", "ImagePickerKMP-bundle.js")
            
            customField("files", arrayOf(
                "kotlin/",
                "*.md",
                "package.json",
                "ImagePickerKMP-bundle.js",
                "ImagePickerKMP-bundle.d.ts"
            ))
            
            customField("engines", mapOf(
                "node" to ">=14.0.0"
            ))
            
            customField("sideEffects", false)
            
            customField("scripts", mapOf(
                "test" to "echo \"No test specified\"",
                "build" to "echo \"Already built\"",
                "prepublishOnly" to "echo \"Package ready for publishing\""
            ))
            
            customField("peerDependencies", mapOf(
                "react" to ">=16.8.0"
            ))
            customField("peerDependenciesMeta", mapOf(
                "react" to mapOf("optional" to true)
            ))
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
            baseName = "imagepickerkmp"
            isStatic = true
            binaryOption("bundleId", "io.github.ismoy.imagepickerkmp")
            export(libs.compose.runtime)
        }
        target.mavenPublication {}
    }
    withSourcesJar(true)
    sourceSets {
        commonMain.dependencies {
            api(project(":imagepicker-core"))
            api(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.animation)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.io.core)
            implementation(libs.coil.compose)
            implementation(libs.material.icons.core)
            implementation(libs.material.icons.extended)
            implementation(libs.components.resources)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.accompanist.permissions)
            implementation(libs.core)
            implementation(libs.androidx.ui)
            implementation(libs.androidx.ui.tooling.preview)
            implementation(libs.androidx.exifinterface)
        }
        val iosResourcesDir =
            project.findProperty("iosResourcesDir") as? String ?: "src/iosMain/resources"
        iosMain {
            resources.srcDirs(iosResourcesDir)
            tasks.withType<ProcessResources> {
                duplicatesStrategy = DuplicatesStrategy.INCLUDE
            }
        }
        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
        }
        jsMain.dependencies {

        }
        wasmJsMain.dependencies {

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
                
                implementation("org.jetbrains.kotlin:kotlin-test:2.3.20")
            }
        }
        val androidInstrumentedTest by getting {}
        
        val jvmTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test:2.3.20")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
                implementation("junit:junit:4.13.2")
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test:2.3.20")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    
    buildFeatures {
        compose = true
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
            "ImagePickerKMP is the leading Kotlin Multiplatform (KMP) imagepickerkmp-photo for image picking and camera capture " +
                    "on Android and iOS. It provides a simple, unified API that integrates seamlessly with Jetpack Compose " +
                    "Multiplatform and SwiftUI. ImagePickerKMP includes customizable UI components, automatic permission " +
                    "handling, and high-quality camera and gallery support. This imagepickerkmp-photo is the best choice for developers " +
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
    publishToMavenCentral(automaticRelease = false)
    if (project.hasProperty("signing.keyId")) {
        signAllPublications()
    }
    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
            sourcesJar = SourcesJar.Sources()
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
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("io.insert-koin:koin-test-junit4:3.5.0")
    
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("org.mockito:mockito-android:5.8.0")
    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
}

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
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
    }
}

configurations.all {
    resolutionStrategy {
        eachDependency {
            if (requested.group == "io.github.ismoy" && requested.name == "imagepickerkmp-video") {
                throw GradleException(
                    "ISOLATION VIOLATION: Image module (imagepickerkmp-photo) cannot depend on " +
                    "imagepickerkmp-video. Image and Video modules must remain independent."
                )
            }
        }
    }
}
