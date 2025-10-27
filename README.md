# ImagePickerKMP

[![CI](https://github.com/ismoy/ImagePickerKMP/workflows/CI/badge.svg)](https://github.com/ismoy/ImagePickerKMP/actions)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.ismoy/imagepickerkmp.svg?label=Maven%20Central)](https://search.maven.org/artifact/io.github.ismoy/imagepickerkmp)
[![NPM](https://img.shields.io/npm/v/imagepickerkmp.svg?label=NPM)](https://www.npmjs.com/package/imagepickerkmp)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.21-blue.svg)](https://kotlinlang.org)

**Cross-platform Image Picker & Camera Library for Kotlin Multiplatform**

Supports Android, iOS, Desktop, and Web with a unified API. Built with Compose Multiplatform.

##  Quick Start

### Installation

**Kotlin Multiplatform:**
```kotlin
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:{latest-version}")
}
```

**React/JavaScript:**
```bash
npm install imagepickerkmp
```

### Basic Usage

**Camera Capture:**
```kotlin
var showCamera by remember { mutableStateOf(false) }
var capturedPhoto by remember { mutableStateOf<PhotoResult?>(null) }

if (showCamera) {
    ImagePickerLauncher(
        config = ImagePickerConfig(
            onPhotoCaptured = { result ->
                capturedPhoto = result
                showCamera = false
            },
            onError = { showCamera = false },
            onDismiss = { showCamera = false }
        )
    )
}

Button(onClick = { showCamera = true }) {
    Text("Take Photo")
}
```

**Gallery Selection:**
```kotlin
var showGallery by remember { mutableStateOf(false) }
var selectedImages by remember { mutableStateOf<List<PhotoResult>>(emptyList()) }

if (showGallery) {
    GalleryPickerLauncher(
        onPhotosSelected = { photos ->
            selectedImages = photos
            showGallery = false
        },
        onError = { showGallery = false },
        onDismiss = { showGallery = false },
        allowMultiple = true
    )
}

Button(onClick = { showGallery = true }) {
    Text("Choose from Gallery")
}
```
## ⚠️ Important Usage Note

**Camera Preview Not Showing?** Some developers have reported that the camera usage indicator appears, but the preview doesn't show up. This happens when `ImagePickerLauncher` is not placed inside a visible container composable.

**✅ Correct usage:**
```kotlin
Box(modifier = Modifier.fillMaxSize()) {
    if (showCamera) {
        ImagePickerLauncher(
            config = ImagePickerConfig(
                onPhotoCaptured = { /* handle image */ },
                onDismiss = { showCamera = false }
            )
        )
    }
}
```

**❌ Incorrect usage:**
```kotlin
if (showCamera) {
    ImagePickerLauncher(
        config = ImagePickerConfig(
            onPhotoCaptured = { /* handle image */ },
            onDismiss = { showCamera = false }
        )
    )
}
```

> **💡 Always wrap the camera launcher inside a composable container (Box, Column, Row) and control its visibility with state.**
> 
> *Thanks to [@rnstewart](https://github.com/rnstewart) and other contributors for pointing this out! 🙏*


##  Key Features

-  **Cross-platform** - Android, iOS, Desktop, Web
-  **Camera & Gallery** - Direct access with unified API
-  **Image Cropping** - Built-in crop functionality
-  **Smart Compression** - Configurable quality levels
-  **Extension Functions** - Easy image processing (`loadPainter()`, `loadBytes()`, `loadBase64()`)
-  **Permission Handling** - Automatic permission management
-  **Async Processing** - Non-blocking UI with coroutines
-  **Format Support** - JPEG, PNG, HEIC, HEIF, WebP, GIF, BMP

##  Platform Support

| Platform | Minimum Version | Status |
|----------|----------------|--------|
| Android  | API 21+       | ✅ |
| iOS      | iOS 12.0+     | ✅ |
| Desktop  | JDK 11+       | ✅ |
| Web      | Modern Browsers| ✅ |

---

##  Live Demos & Examples

###  **Online Demos**
**[ View Interactive Demos →](https://ismoy.github.io/ImagePickerKMP-Demos)**

Experience ImagePickerKMP in action:
-  **Mobile Demos** - Android & iOS camera/gallery functionality
-  **Desktop Demo** - File picker and image processing  
-  **Web Demo** - React integration with WebRTC camera
-  **Crop Demo** - Interactive image cropping across platforms

###  **Complete Example App** 
**[ ImagePickerKMP-Example →](https://github.com/ismoy/CameraKMP)**

Full-featured sample application showcasing:
- All library features and configurations
- Real-world implementation patterns  
- Platform-specific optimizations
- Best practices and tips

---

##  Documentation

###  **Complete Guides**
| Resource | Description |
|----------|-------------|
| [ Integration Guide](docs/INTEGRATION_GUIDE.md) | Complete setup and configuration |
| [ Customization Guide](docs/CUSTOMIZATION_GUIDE.md) | UI customization and theming |
| [ React Guide](REACT_INTEGRATION_GUIDE.md) | Web development setup |
| [ Permissions Guide](library/PERMISSION.md) | Platform permissions |
| [ API Reference](docs/API_REFERENCE.md) | Complete API documentation |

## Advanced Configuration

### Image Compression
```kotlin
ImagePickerLauncher(
    config = ImagePickerConfig(
        cameraCaptureConfig = CameraCaptureConfig(
            compressionLevel = CompressionLevel.HIGH, // LOW, MEDIUM, HIGH
            skipConfirmation = true
        )
    )
)
```

### Multiple Selection with Filtering
```kotlin
GalleryPickerLauncher(
    allowMultiple = true,
    mimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG),
    enableCrop = true
)
```

### iOS Permissions Setup
Add to your `Info.plist`:
```xml
<key>NSCameraUsageDescription</key>
<string>Camera access needed to take photos</string>
<key>NSPhotoLibraryUsageDescription</key>
<string>Photo library access needed to select images</string>
```

##  Extension Functions

Process images easily with built-in extension functions:

```kotlin
ImagePickerLauncher(
    config = ImagePickerConfig(
        onPhotoCaptured = { result ->
            val imageBytes = result.loadBytes()        // ByteArray for file operations
            val imagePainter = result.loadPainter()    // Painter for Compose UI
            val imageBitmap = result.loadImageBitmap() // ImageBitmap for graphics
            val imageBase64 = result.loadBase64()      // Base64 string for APIs
        }
    )
)
```

##  React/Web Integration

ImagePickerKMP is available as an NPM package for web development:

```bash
npm install imagepickerkmp
```

Features:
-  **WebRTC Camera Access** - Mobile & desktop camera support
-  **TypeScript Support** - Full type definitions included
-  **Drag & Drop** - File picker with drag and drop
-  **React Components** - Ready-to-use React components
-  **Cross-Framework** - Works with React, Vue, Angular, Vanilla JS

**[Complete React Integration Guide →](REACT_INTEGRATION_GUIDE.md)**

##  Support & Contributing

-  [Report Issues](https://github.com/ismoy/ImagePickerKMP/issues)
-  [Discord Community](https://discord.gg/EjSQTeyh)  
-  [Contributing Guide](docs/CONTRIBUTING.md)
-  [Email Support](mailto:belizairesmoy72@gmail.com)

---

**Made with ❤️ for the Kotlin Multiplatform community**  
*⭐ Star this repo if it helped you!*
