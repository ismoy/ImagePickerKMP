<p align="center">
  <a href="https://github.com/ismoy/ImagePickerKMP/actions"><img src="https://github.com/ismoy/ImagePickerKMP/workflows/CI/badge.svg" alt="CI"></a>
  <a href="https://codecov.io/gh/ismoy/ImagePickerKMP"><img src="https://codecov.io/gh/ismoy/ImagePickerKMP/branch/main/graph/badge.svg" alt="Code Coverage"></a>
  <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License"></a>
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-1.9.0-blue.svg" alt="Kotlin"></a>
</p>

<p align="center">
  <a href="https://kotlinlang.org/docs/multiplatform.html"><img src="https://img.shields.io/badge/Platform-Android%20%7C%20iOS-lightgrey.svg" alt="Platform"></a>
  <a href="https://search.maven.org/artifact/io.github.ismoy/imagepickerkmp"><img src="https://img.shields.io/maven-central/v/io.github.ismoy/imagepickerkmp.svg?label=Maven%20Central" alt="Maven Central"></a>
  <a href="https://github.com/ismoy/ImagePickerKMP/releases"><img src="https://img.shields.io/github/v/release/ismoy/ImagePickerKMP?label=GitHub%20Release" alt="GitHub Release"></a>
  <a href="https://github.com/ismoy/ImagePickerKMP/stargazers"><img src="https://img.shields.io/github/stars/ismoy/ImagePickerKMP?style=social" alt="GitHub Repo stars"></a>
</p>

<p align="center">
  <a href="https://github.com/ismoy/ImagePickerKMP/commits/main"><img src="https://img.shields.io/github/last-commit/ismoy/ImagePickerKMP" alt="GitHub last commit"></a>
  <a href="https://github.com/ismoy/ImagePickerKMP/pulls"><img src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square" alt="PRs Welcome"></a>
  <a href="https://discord.gg/EjSQTeyh"><img src="https://img.shields.io/discord/1393705692484993114.svg?label=Discord&logo=discord&color=7289da" alt="Discord"></a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Compose%20Multiplatform-green" alt="Compose Multiplatform">
  <img src="https://img.shields.io/badge/Platform-Android-green" alt="Android">
  <img src="https://img.shields.io/badge/Platform-iOS-blue" alt="iOS">
  <img src="https://img.shields.io/codecov/c/github/ismoy/ImagePickerKMP" alt="Coverage Status">
  <a href="https://github.com/ismoy/ImagePickerKMP/actions/workflows/detekt.yml"><img src="https://github.com/ismoy/ImagePickerKMP/actions/workflows/detekt.yml/badge.svg?branch=main" alt="Detekt"></a>
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

## Demo **Android** And **IOS**

<p align="center">
  <a href="https://github.com/ismoy/ImagePickerKMP/blob/develop/demo_video_library.gif">
    <img src="https://raw.githubusercontent.com/ismoy/ImagePickerKMP/develop/demo_video_library.gif" alt="Demo ImagePickerKMP" width="80%">
  </a>
</p>

---

## About ImagePickerKMP
- **Cross-platform**: Works seamlessly on Android and iOS
- **Camera Integration**: Direct camera access with photo capture
- **Customizable UI**: Custom dialogs and confirmation views
- **Permission Handling**: Smart permission management for both platforms
- **Easy Integration**: Simple API with Compose Multiplatform
- **Highly Configurable**: Extensive customization options

---

## Quick Start – Kotlin Multiplatform Image Picker Integration

### Installation

### Using ImagePickerKMP in Kotlin Multiplatform / Compose Multiplatform

#### Step 1: Add the dependency
<p>In your <code>commonMain</code> <code>build.gradle.kts</code>:</p>

```kotlin
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.2")//lastversion
}
```
#### iOS Permissions Setup
<p>Don't forget to configure iOS-specific permissions in your <code>Info.plist</code> file:</p>

```xml
<key>NSCameraUsageDescription</key>
<string>We need access to the camera to capture a photo.</string>
```
#### Step 2: Launch the Camera
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
            },
            // Custom dialog only IOS optional 
            customPickerDialog = { onTakePhoto, onSelectFromGallery, onCancel ->
                MyCustomBottomSheet(
                    onTakePhoto = onTakePhoto,
                    onSelectFromGallery = onSelectFromGallery,
                    onDismiss = {
                        isPickerSheetVisible = false
                        onCancel()
                        showCameraPicker = false
                    }
                )
            },
            // Only Android optional 
            cameraCaptureConfig = CameraCaptureConfig(
                permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                    customConfirmationView = { photoResult, onConfirm, onRetry ->
                        YourCustomConfirmationView(
                            result = photoResult,
                            onConfirm = onConfirm,
                            onRetry = onRetry
                        )
                    },
                    // Optional: custom permission handler
                    customPermissionHandler = { config ->
                        // Handle permissions here
                    }
                )
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
        }
        allowMultiple = true, // False for single selection
        mimeTypes = listOf("image/jpeg", "image/png") ,// Optional: filter by type
        // Only Android optional 
        cameraCaptureConfig = CameraCaptureConfig(
            permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                customConfirmationView = { photoResult, onConfirm, onRetry ->
                    YourCustomConfirmationView(
                        result = photoResult,
                        onConfirm = onConfirm,
                        onRetry = onRetry
                    )
                }
            )
        )
    )
}

Button(onClick = { showGallery = true }) {
    Text("Choose from Gallery")
}
```
For more customization (confirmation views, MIME filtering, etc.), [check out the integration guide for KMP.](https://github.com/ismoy/ImagePickerKMP/blob/main/docs/INTEGRATION_GUIDE.md#kotlin-multiplatform--compose-multiplatform)

### Using ImagePickerKMP in Android Native (Jetpack Compose)
<p>Even if you're not using KMP, you can use ImagePickerKMP in pure Android projects with Jetpack Compose.</p>

#### Step 1: Add the dependency
```kotlin
implementation("io.github.ismoy:imagepickerkmp:1.0.2")
```
#### Step 2: Camera Launcher Example
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
            },
            // Custom dialog only IOS optional 
            customPickerDialog = { onTakePhoto, onSelectFromGallery, onCancel ->
                MyCustomBottomSheet(
                    onTakePhoto = onTakePhoto,
                    onSelectFromGallery = onSelectFromGallery,
                    onDismiss = {
                        isPickerSheetVisible = false
                        onCancel()
                        showCameraPicker = false
                    }
                )
            },
            // Only Android optional 
            cameraCaptureConfig = CameraCaptureConfig(
                permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                    customConfirmationView = { photoResult, onConfirm, onRetry ->
                        YourCustomConfirmationView(
                            result = photoResult,
                            onConfirm = onConfirm,
                            onRetry = onRetry
                        )
                    },
                    // Optional: custom permission handler
                    customPermissionHandler = { config ->
                        // Handle permissions here
                    }
                )
            )
        )
    )
}

Button(onClick = { showCamera = true }) {
    Text("Take Photo")
}
```

#### Step 3: Gallery Picker Example
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
        mimeTypes = listOf("image/jpeg", "image/png"), // Optional: filter by type
        // Only Android optional 
        cameraCaptureConfig = CameraCaptureConfig(
            permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                customConfirmationView = { photoResult, onConfirm, onRetry ->
                    YourCustomConfirmationView(
                        result = photoResult,
                        onConfirm = onConfirm,
                        onRetry = onRetry
                    )
                }
            )
        )
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

<p align="center">
  <strong>Cross-platform compatibility with intelligent context management</strong>
</p>

- **Android:** The library automatically manages the context using `LocalContext.current`. No need to pass context manually.
- **iOS:** Context is not required as the library uses native iOS APIs.

| Platform                | Minimum Version | Status |
|-------------------------|----------------|--------|
| Android                 | API 21+        | ✅     |
| iOS                     | iOS 12.0+      | ✅     |
| Compose Multiplatform   | 1.5.0+         | ✅     |

## Why Choose ImagePickerKMP?

<p align="center">
  <strong>The most comprehensive and developer-friendly image picker for Kotlin Multiplatform</strong>
</p>

### Comparison with Other Libraries

| Feature                        | ImagePickerKMP | Peekaboo        | KMPImagePicker   |
|--------------------------------|----------------|-----------------|------------------|
| **Compose Multiplatform Support** | ✅ Native      | ❌ Android only  | ⚠️ Limited       |
| **UI Customization**           | ✅ Full control | ⚠️ Basic         | ⚠️ Basic         |
| **Unified Permissions**        | ✅ Smart handling | ❌ Manual      | ⚠️ Platform-specific |
| **Error Handling**             | ✅ Comprehensive | ⚠️ Basic        | ⚠️ Basic         |
| **Camera Integration**         | ✅ Direct access | ✅ Direct access | ⚠️ Gallery only  |
| **Gallery Support**            | ✅ Multi-select  | ✅ Multi-select  | ✅ Multi-select   |
| **Cross-platform API**         | ✅ Single codebase | ❌ Platform-specific | ⚠️ Partial  |

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

## Platform-Specific Integration

### Android Native (Jetpack Compose)

For detailed Android integration guide, see: [Android Integration Guide](docs/INTEGRATION_GUIDE.md#android-native-jetpack-compose)

### iOS Native (Swift/SwiftUI)

For detailed iOS integration guide, see: [iOS Integration Guide](docs/INTEGRATION_GUIDE.md#ios-native-swiftswiftui)

### Kotlin Multiplatform/Compose Multiplatform

For detailed KMP integration guide, see: [Kotlin Multiplatform Integration Guide](docs/INTEGRATION_GUIDE.md#kotlin-multiplatform--compose-multiplatform)

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

## License

<p align="center">
  This project is licensed under the <strong>MIT License</strong><br>
  See the <a href="docs/LICENSE">LICENSE</a> file for details.
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
  <a href="https://discord.gg/EjSQTeyh">💬 Discord</a>
</p>

## Changelog

<p align="center">
  See <a href="docs/CHANGELOG.md">CHANGELOG.md</a> for a complete list of changes and updates.
</p>

---

<p align="center">
  <strong>Made with ❤️ for the Kotlin Multiplatform community</strong><br>
  <em>Star ⭐ this repo if it helped you!</em>
</p>

<p align="center">
  <a href="https://github.com/ismoy/ImagePickerKMP/stargazers">⭐ Star on GitHub</a> • 
  <a href="https://github.com/ismoy/ImagePickerKMP/fork">🍴 Fork</a> • 
  <a href="https://github.com/ismoy/ImagePickerKMP/issues/new">🐛 Report Bug</a> • 
  <a href="https://github.com/ismoy/ImagePickerKMP/issues/new">💡 Request Feature</a>
</p>
