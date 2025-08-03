[![CI](https://github.com/ismoy/ImagePickerKMP/workflows/CI/badge.svg)](https://github.com/ismoy/ImagePickerKMP/actions)
[![Code Coverage](https://codecov.io/gh/ismoy/ImagePickerKMP/branch/main/graph/badge.svg)](https://codecov.io/gh/ismoy/ImagePickerKMP)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-lightgrey.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.ismoy/imagepickerkmp.svg?label=Maven%20Central)](https://search.maven.org/artifact/io.github.ismoy/imagepickerkmp)
[![GitHub Release](https://img.shields.io/github/v/release/ismoy/ImagePickerKMP?label=GitHub%20Release)](https://github.com/ismoy/ImagePickerKMP/releases)
[![GitHub Repo stars](https://img.shields.io/github/stars/ismoy/ImagePickerKMP?style=social)](https://github.com/ismoy/ImagePickerKMP/stargazers)
[![GitHub last commit](https://img.shields.io/github/last-commit/ismoy/ImagePickerKMP)](https://github.com/ismoy/ImagePickerKMP/commits/main)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](https://github.com/ismoy/ImagePickerKMP/pulls)
[![Discord](https://img.shields.io/discord/1393705692484993114.svg?label=Discord&logo=discord&color=7289da)](https://discord.gg/EjSQTeyh)
![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-green)
![Android](https://img.shields.io/badge/Platform-Android-green)
![iOS](https://img.shields.io/badge/Platform-iOS-blue)
![Coverage Status](https://img.shields.io/codecov/c/github/ismoy/ImagePickerKMP)
[![Detekt](https://github.com/ismoy/ImagePickerKMP/actions/workflows/detekt.yml/badge.svg?branch=main)](https://github.com/ismoy/ImagePickerKMP/actions/workflows/detekt.yml)

## 🎥 Demo **Android** And **IOS**

[![Demo ImagePickerKMP](https://raw.githubusercontent.com/ismoy/ImagePickerKMP/develop/demo_video_library.gif)](https://github.com/ismoy/ImagePickerKMP/blob/develop/demo_video_library.gif)

*Watch the demo above to see ImagePickerKMP in action - camera capture, gallery selection, and custom UI in action!*

### 📱 Demo Features Showcased:
- **📸 Camera Capture**: Direct camera access with flash control
- **🔄 Camera Switching**: Seamless front/back camera toggle
- **🎨 Custom UI**: Personalized confirmation dialogs
- **📁 Gallery Selection**: Multi-image selection from gallery
- **⚡ Performance**: Smooth, responsive interactions
- **🔒 Permissions**: Smart permission handling

# ImagePickerKMP
**Cross‑platform Image Picker & Camera Library (Android & iOS)**  
Built with **Kotlin Multiplatform** + **Compose Multiplatform** + **Kotlin/Native**.

Este documento también está disponible en español: [README.es.md](README.es.md)

## Features – Camera, Image Picker & Gallery for Android & iOS

- 📱 **Cross-platform**: Works on Android and iOS
- 📸 **Camera Integration**: Direct camera access with photo capture
- 🎨 **Customizable UI**: Custom dialogs and confirmation views
- 🔒 **Permission Handling**: Smart permission management for both platforms
- 🎯 **Easy Integration**: Simple API with Compose Multiplatform
- 🔧 **Highly Configurable**: Extensive customization options

## Quick Start – Kotlin Multiplatform Image Picker Integration

### Installation

## Using ImagePickerKMP in Kotlin Multiplatform / Compose Multiplatform

### Step 1: Add the dependency
In your `commonMain` `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.2")//lastversion
}
```
### Don’t forget to configure iOS-specific permissions in your Info.plist file:
```
<key>NSCameraUsageDescription</key>
<string>We need access to the camera to capture a photo.</string>
```
### Step 2: Launch the Camera
```kotlin
 var showCamera by remember { mutableStateOf(false) }
 var capturedPhoto by remember { mutableStateOf<CameraPhotoHandler.PhotoResult?>(null) }
```
```kotlin
if (showCamera) {
    ImagePickerLauncher(
        config = ImagePickerConfig(
            onPhotoCaptured = { result ->
                capturedPhoto = result
                showCamera = false
            },
            onError = {
                showCamera = false
            },
            onDismiss = { 
                showImagePicker = false // Reset state when user doesn't select anything
            }
        )
    )
}
```
### Step 3: Pick from the Gallery
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
        }
        allowMultiple = true, // False for single selection
        mimeTypes = listOf("image/jpeg", "image/png") // Optional: filter by type
    )
}

Button(onClick = { showGallery = true }) {
    Text("Choose from Gallery")
}
```
For more customization (confirmation views, MIME filtering, etc.), [check out the integration guide for KMP.](https://github.com/ismoy/ImagePickerKMP/blob/main/docs/INTEGRATION_GUIDE.md#kotlin-multiplatform--compose-multiplatform)

### Using ImagePickerKMP in Android Native (Jetpack Compose)
Even if you’re not using KMP, you can use ImagePickerKMP in pure Android projects with Jetpack Compose.

### Step 1: Add the dependency
```kotlin
implementation("io.github.ismoy:imagepickerkmp:1.0.2")
```
### Step 2: Camera Launcher Example
```kotlin
var showCamera by remember { mutableStateOf(false) }
var capturedPhoto by remember { mutableStateOf<CameraPhotoHandler.PhotoResult?>(null) }
```
```kotlin
if (showCamera) {
    ImagePickerLauncher(
        config = ImagePickerConfig(
            onPhotoCaptured = { result ->
                capturedPhoto = result
                showCamera = false
            },
            onError = {
                showCamera = false
            },
           onDismiss = {
            showCamera = false
           }
        )
    )
}

Button(onClick = { showCamera = true }) {
    Text("Take Photo")
}
```

### Step 3: Gallery Picker Example
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
            showCamera = false
           },
        allowMultiple = true, // False for single selection
        mimeTypes = listOf("image/jpeg", "image/png") // Optional: filter by type
    )
}

Button(onClick = { showGallery = true }) {
    Text("Choose from Gallery")
}
```
See the [Android Native integration guide](https://github.com/ismoy/ImagePickerKMP/blob/main/docs/INTEGRATION_GUIDE.md#android-native-jetpack-compose) for more usage details.

- On **Android**, the user will see the system gallery picker.
- On **iOS**, the native gallery picker is used. On iOS 14+, multiple selection is supported. The system handles permissions and limited access natively.
- The callback `onPhotosSelected` always receives a list, even for single selection.
- You can use `allowMultiple` to enable or disable multi-image selection.
- The `mimeTypes` parameter is optional and lets you filter selectable file types.

## GalleryPickerLauncher Dismiss Fix

The `GalleryPickerLauncher` now includes an `onDismiss` callback to handle cases where users dismiss the picker without selecting any images. This resolves the issue where the picker couldn't be reopened after being dismissed.

The `onDismiss` callback is triggered when:
- User cancels the selection dialog (Android)
- User taps "Cancel" (iOS)
- User cancels camera permission request (iOS)
- User cancels the camera interface (taps "Cancel" or "X") (iOS)

## Platform Support

- **Android:** The library automatically manages the context using `LocalContext.current`. No need to pass context manually.
- **iOS:** Context is not required as the library uses native iOS APIs.

| Platform                | Minimum Version | Status |
|-------------------------|----------------|--------|
| Android                 | API 21+        | ✅     |
| iOS                     | iOS 12.0+      | ✅     |
| Compose Multiplatform   | 1.5.0+         | ✅     |

## Why Choose ImagePickerKMP?

### 🆚 Comparison with Other Libraries

| Feature                        | ImagePickerKMP | Peekaboo        | KMPImagePicker   |
|--------------------------------|----------------|-----------------|------------------|
| **Compose Multiplatform Support** | ✅ Native      | ❌ Android only  | ⚠️ Limited       |
| **UI Customization**           | ✅ Full control | ⚠️ Basic         | ⚠️ Basic         |
| **Unified Permissions**        | ✅ Smart handling | ❌ Manual      | ⚠️ Platform-specific |
| **Error Handling**             | ✅ Comprehensive | ⚠️ Basic        | ⚠️ Basic         |
| **Camera Integration**         | ✅ Direct access | ✅ Direct access | ⚠️ Gallery only  |
| **Gallery Support**            | ✅ Multi-select  | ✅ Multi-select  | ✅ Multi-select   |
| **Cross-platform API**         | ✅ Single codebase | ❌ Platform-specific | ⚠️ Partial  |

### 🎯 Key Advantages

- **🔄 Compose Multiplatform Native**: Built specifically for Compose Multiplatform, ensuring consistent behavior across platforms
- **🎨 Full UI Customization**: Complete control over dialogs, confirmation views, and camera UI
- **🔒 Smart Permission Management**: Unified permission handling with intelligent fallbacks
- **⚡ Performance Optimized**: Efficient image processing and memory management
- **🛠️ Developer Friendly**: Simple API with comprehensive error handling

## Requirements

### Android
- Minimum SDK: 21
- Kotlin 1.8+
- Compose Multiplatform

### iOS
- iOS 12.0+
- Xcode 14+
- Kotlin Multiplatform

## Platform-Specific Integration

### Android Native (Jetpack Compose)

For detailed Android integration guide, see: [Android Integration Guide](docs/INTEGRATION_GUIDE.md#android-native-jetpack-compose)

### iOS Native (Swift/SwiftUI)

For detailed iOS integration guide, see: [iOS Integration Guide](docs/INTEGRATION_GUIDE.md#ios-native-swiftswiftui)

### Kotlin Multiplatform/Compose Multiplatform

For detailed KMP integration guide, see: [Kotlin Multiplatform Integration Guide](docs/INTEGRATION_GUIDE.md#kotlin-multiplatform--compose-multiplatform)

## Documentation

- [Integration Guide](docs/INTEGRATION_GUIDE.md) - Complete setup and integration guide
- [Customization Guide](docs/CUSTOMIZATION_GUIDE.md) - UI and behavior customization
- [Internationalization Guide](docs/I18N_GUIDE.md) - Multi-language support guide
- [Permissions Guide](docs/PERMISSION.md) - Permission handling details
- [Coverage Guide](docs/COVERAGE_GUIDE.md) - Code coverage and testing guide
- [Notifications Setup](docs/NOTIFICATIONS_SETUP.md) - Discord notifications setup
- [API Reference](docs/API_REFERENCE.md) - Complete API documentation
- [Examples](docs/EXAMPLES.md) - Code examples and use cases

## Contributing

We welcome contributions! See our [Contributing Guide](docs/CONTRIBUTING.md) for details.

## License

This project is licensed under the MIT License - see the [LICENSE](docs/LICENSE) file for details.

## Support

- 📧 Email: belizairesmoy72@gmail.com
- 🐛 Issues: [GitHub Issues](https://github.com/ismoy/ImagePickerKMP/issues)
- 📖 Documentation: [Wiki](https://github.com/ismoy/ImagePickerKMP/wiki)
- 💬 Discord: [Community Channel](https://discord.gg/EjSQTeyh)

## Changelog

See [CHANGELOG.md](docs/CHANGELOG.md) for a complete list of changes and updates.

---

**Made with ❤️ for the Kotlin Multiplatform community**
