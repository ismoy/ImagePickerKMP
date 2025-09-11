<p align="center">
  <a href="https://github.com/ismoy/ImagePickerKMP/actions"><img src="https://github.com/ismoy/ImagePickerKMP/workflows/CI/badge.svg" alt="CI"></a>
  <a href="https://codecov.io/gh/ismoy/ImagePickerKMP"><img src="https://codecov.io/gh/ismoy/ImagePickerKMP/branch/main/graph/badge.svg" alt="Code Coverage"></a>
  <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License"></a>
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-2.1.21-blue.svg" alt="Kotlin"></a>
</p>

<p align="center">
  <a href="https://search.maven.org/artifact/io.github.ismoy/imagepickerkmp"><img src="https://img.shields.io/maven-central/v/io.github.ismoy/imagepickerkmp.svg?label=Maven%20Central" alt="Maven Central"></a>
  <a href="https://github.com/ismoy/ImagePickerKMP/releases"><img src="https://img.shields.io/github/v/release/ismoy/ImagePickerKMP?label=GitHub%20Release" alt="GitHub Release"></a>
  <a href="https://github.com/ismoy/ImagePickerKMP/stargazers"><img src="https://img.shields.io/github/stars/ismoy/ImagePickerKMP?style=social" alt="GitHub Repo stars"></a>
    <a href="https://github.com/ismoy/ImagePickerKMP/commits/main"><img src="https://img.shields.io/github/last-commit/ismoy/ImagePickerKMP" alt="GitHub last commit"></a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Compose%20Multiplatform-green" alt="Compose Multiplatform">
  <img src="https://img.shields.io/badge/Platform-Android-green" alt="Android">
  <img src="https://img.shields.io/badge/Platform-iOS-blue" alt="iOS">
  <a href="https://github.com/ismoy/ImagePickerKMP/actions/workflows/detekt.yml">
  <img src="https://github.com/ismoy/ImagePickerKMP/actions/workflows/detekt.yml/badge.svg?branch=main" alt="Detekt"></a>
</p>

---

<h1 align="center">ImagePickerKMP</h1>

<p align="center">
  <strong>Cross‑platform Image Picker & Camera Library (Android & iOS)</strong><br>
  Built with <strong>Kotlin Multiplatform</strong> + <strong>Compose Multiplatform</strong> + <strong>Kotlin/Native</strong>
</p>

<p align="center">
  <a href="README.es.md">Español</a> • 
  <a href="https://github.com/ismoy/ImagePickerKMP">GitHub</a> • 
  <a href="https://search.maven.org/artifact/io.github.ismoy/imagepickerkmp">Maven Central</a> • 
  <a href="https://discord.gg/EjSQTeyh">Discord</a>
</p>

---
## Demos Android & iOS

### Android
| Camera Usage | Crop Mode |
|--------------|-----------|
| <a href="https://raw.githubusercontent.com/ismoy/ImagePickerKMP/develop/docs/androidCameraDemo.gif"><img src="https://raw.githubusercontent.com/ismoy/ImagePickerKMP/develop/docs/androidCameraDemo.gif" alt="Android Camera Demo" width="250"></a> | <a href="https://raw.githubusercontent.com/ismoy/ImagePickerKMP/develop/docs/androidCrop.gif"><img src="https://raw.githubusercontent.com/ismoy/ImagePickerKMP/develop/docs/androidCrop.gif" alt="Android Crop Demo" width="250"></a> |

### iOS
| Camera Usage | Crop Mode |
|--------------|-----------|
| <a href="https://raw.githubusercontent.com/ismoy/ImagePickerKMP/develop/docs/iosCameraDemo.gif"><img src="https://raw.githubusercontent.com/ismoy/ImagePickerKMP/develop/docs/iosCameraDemo.gif" alt="iOS Camera Demo" width="250"></a> | <a href="https://raw.githubusercontent.com/ismoy/ImagePickerKMP/develop/docs/iosCrop.gif"><img src="https://raw.githubusercontent.com/ismoy/ImagePickerKMP/develop/docs/iosCrop.gif" alt="iOS Crop Demo" width="250"></a> |

---

## About ImagePickerKMP
- **Cross-platform**: Works seamlessly on Android and iOS
- **Camera Integration**: Direct camera access with photo capture
- **Gallery Selection**: Pick images from device gallery with compression support
- **Advanced Image Cropping**: Cross-platform crop functionality with automatic context management
- **Automatic Image Compression**: Optimize image size while maintaining quality
- **Configurable Compression Levels**: LOW, MEDIUM, HIGH compression options
- **Async Processing**: Non-blocking UI with Kotlin Coroutines integration
- **Multiple Format Support**: JPEG, PNG, HEIC, HEIF, WebP, GIF, BMP
- **Customizable UI**: Custom dialogs and confirmation views
- **Permission Handling**: Smart permission management for both platforms
- **Easy Integration**: Simple API with Compose Multiplatform
- **Enhanced User Experience**: Improved layout system with proper zoom and aspect ratio handling
- **Highly Configurable**: Extensive customization options

---

## Quick Start – Kotlin Multiplatform Image Picker Integration

### Installation

### Using ImagePickerKMP in Kotlin Multiplatform / Compose Multiplatform
#### Step 1: Add the dependency
<p>In your <code>commonMain</code> <code>build.gradle.kts</code>:</p>

```kotlin
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.24-beta")//lastversion
}
```
### Using ImagePickerKMP in Android Native (Jetpack Compose)
<p>Even if you're not using KMP, you can use ImagePickerKMP in pure Android projects with Jetpack Compose.</p>

#### Step 1: Add the dependency
```kotlin
implementation("io.github.ismoy:imagepickerkmp:1.0.24-beta")
```
#### iOS Permissions Setup
<p>Don't forget to configure iOS-specific permissions in your <code>Info.plist</code> file:</p>

```xml
<key>NSCameraUsageDescription</key>
<string>We need access to the camera to capture a photo.</string>
```
<h1>Basic Use</h1>

#### Step 2: Launch the Camera
```kotlin
 var showCamera by remember { mutableStateOf(false) }
 var capturedPhoto by remember { mutableStateOf<PhotoResult?>(null) }
```
```kotlin
if (showCamera) {
    ImagePickerLauncher(
        config = ImagePickerConfig(
            enableCrop = false, // Set to true if you want Crop option
            onPhotoCaptured = { result ->
                capturedPhoto = result
                // Now you can access result.fileSize for camera captures too!
                println("Camera photo size: ${result.fileSize}KB")
                showCamera = false
            },
            onError = {
                showCamera = false
            },
            onDismiss = {
                showImagePicker = false // Reset state when user doesn't select anything
            },
            directCameraLaunch = false, // Set to true if you want to launch the camera directly Only IOS
            // It is possible to compress images, by default it is with low compression in the library
            cameraCaptureConfig = CameraCaptureConfig(
                compressionLevel = CompressionLevel.HIGH
            )

        )
    )
}
```
#### Step 3: Pick from the Gallery
```kotlin
var showGallery by remember { mutableStateOf(false) }
var selectedImages by remember { mutableStateOf<List<GalleryPhotoHandler.PhotoResult>>(emptyList()) }
```
```kotlin
if (showGallery) {
    GalleryPickerLauncher(
        onPhotosSelected = { photos ->
            selectedImages = photos
            showGallery = false
        },
        onError = { error ->
            showGallery = false
        },
        onDismiss = {
            println("User cancelled or dismissed the picker")
            showGallery = false // Reset state when user doesn't select anything
        },
        enableCrop = false, // Set to true if you want Crop option 
        allowMultiple = true, // False for single selection
        mimeTypes = listOf(MimeType.IMAGE_PNG) ,// Optional: filter by type
    )
}

Button(onClick = { showGallery = true }) {
    Text("Choose from Gallery")
}
```

### For more customization (confirmation views, MIME filtering, etc.), [check out the integration guide for KMP.](https://github.com/ismoy/ImagePickerKMP/blob/main/docs/INTEGRATION_GUIDE.md)

## Image Compression
**Automatically optimize image size while maintaining quality with configurable compression levels.**

### Compression Levels
- **LOW**: 95% quality, max 2560px dimension - Best quality, larger files
- **MEDIUM**: 75% quality, max 1920px dimension - Balanced quality/size
- **HIGH**: 50% quality, max 1280px dimension - Smaller files, good for storage

### Supported Image Formats
- **JPEG** (image/jpeg) - Full compression support
- **PNG** (image/png) - Full compression support
- **HEIC** (image/heic) - Full compression support
- **HEIF** (image/heif) - Full compression support
- **WebP** (image/webp) - Full compression support
- **GIF** (image/gif) - Full compression support
- **BMP** (image/bmp) - Full compression support

## Platform Support
<p align="center">
  <strong>Cross-platform compatibility with intelligent context management and enhanced crop functionality</strong>
</p>

- **Android:** The library automatically manages the context using `LocalContext.current`. No need to pass context manually.
- **iOS:** Context is not required as the library uses native iOS APIs. Enhanced crop coordinate calculations ensure consistent behavior with Android.
- **Cross-platform Crop:** Unified `applyCrop` function with automatic context management and consistent coordinate calculations across platforms.

### Recent Improvements
- **🔄 Automatic Context Management**: The `applyCrop` function is now `@Composable` and handles Android context automatically
- **🖼️ Enhanced iOS Crop Accuracy**: Fixed coordinate calculations for precise image cropping on iOS
- **🎯 Improved Layout System**: Resolved z-index conflicts and zoom overlay issues for better user experience
- **📱 Better Aspect Ratio Support**: Enhanced handling of vertical aspect ratios (like 9:16) with improved space management

| Platform                | Minimum Version | Status |
|-------------------------|----------------|--------|
| Android                 | API 21+        | ✅     |
| iOS                     | iOS 12.0+      | ✅     |
| Compose Multiplatform   | 1.5.0+         | ✅     |

## Why Choose ImagePickerKMP?

<p align="center">
  <strong>The most comprehensive and developer-friendly image picker for Kotlin Multiplatform</strong>
</p>

<h1>ImagePickerKMP</h1>

| Feature                        | ImagePickerKMP            
|--------------------------------|----------------|
| **Compose Multiplatform Support** | ✅ Native      
| **UI Customization**           | ✅ Full control 
| **Unified Permissions**        | ✅ Smart handling 
| **Error Handling**             | ✅ Comprehensive 
| **Camera Integration**         | ✅ Direct access 
| **Gallery Support**            | ✅ Multi-select 
| **Cross-platform API**         | ✅ Single codebase

### Key Advantages

- ** Compose Multiplatform Native**: Built specifically for Compose Multiplatform, ensuring consistent behavior across platforms
- ** Full UI Customization**: Complete control over dialogs, confirmation views, and camera UI
- ** Smart Permission Management**: Unified permission handling with intelligent fallbacks
- ** Performance Optimized**: Efficient image processing and memory management
- ** Developer Friendly**: Simple API with comprehensive error handling

## Requirements

### Android
- Minimum SDK: 21
- Kotlin 1.8+
- Compose Multiplatform

### iOS
- iOS 12.0+
- Xcode 14+
- Kotlin Multiplatform

## Documentation

<p align="center">
  <strong>Comprehensive guides and references for every aspect of ImagePickerKMP</strong>
</p>

-  [Integration Guide](docs/INTEGRATION_GUIDE.md) - Complete setup and integration guide
-  [Customization Guide](docs/CUSTOMIZATION_GUIDE.md) - UI and behavior customization
-  [Internationalization Guide](docs/I18N_GUIDE.md) - Multi-language support guide
-  [Permissions Guide](docs/PERMISSION.md) - Permission handling details
-  [Coverage Guide](docs/COVERAGE_GUIDE.md) - Code coverage and testing guide
-  [Notifications Setup](docs/NOTIFICATIONS_SETUP.md) - Discord notifications setup
-  [API Reference](docs/API_REFERENCE.md) - Complete API documentation
-  [Examples](docs/EXAMPLES.md) - Code examples and use cases

## Contributing

<p align="center">
  <strong>We welcome contributions from the community!</strong><br>
  See our <a href="docs/CONTRIBUTING.md">Contributing Guide</a> for details.
</p>

---

## Support & Community

<p align="center">
  <strong>Get help, report issues, or join our community</strong>
</p>

<p align="center">
  <a href="mailto:belizairesmoy72@gmail.com">📧 Email</a> • 
  <a href="https://github.com/ismoy/ImagePickerKMP/issues">🐛 Issues</a> • 
  <a href="https://github.com/ismoy/ImagePickerKMP/wiki">📖 Wiki</a> • 
  <a href="https://discord.com/channels/1393705692484993114/1393706133864190133">💬 Discord</a>
</p>

<p align="center">
  <strong>Made with ❤️ for the Kotlin Multiplatform community</strong><br>
  <em>Star ⭐ this repo if it helped you!</em>
</p>
