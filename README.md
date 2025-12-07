# ImagePickerKMP
**Cross-platform Image Picker & Camera Library for Kotlin Multiplatform**

Easily capture or select images on Android, iOS, Desktop, and Web — all with a single API.  
Built with **Compose Multiplatform**, designed for **simplicity, performance, and flexibility**.

<p align="center">
  <!--<a href="https://github.com/ismoy/ImagePickerKMP/actions"><img src="https://github.com/ismoy/ImagePickerKMP/workflows/CI/badge.svg" alt="CI"></a>-->
 <!-- <a href="https://codecov.io/gh/ismoy/ImagePickerKMP"><img src="https://codecov.io/gh/ismoy/ImagePickerKMP/branch/main/graph/badge.svg" alt="Code Coverage"></a>-->
  <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License"></a>
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-2.1.21-blue.svg" alt="Kotlin"></a>
</p>

<p align="center">
  <a href="https://search.maven.org/artifact/io.github.ismoy/imagepickerkmp"><img src="https://img.shields.io/maven-central/v/io.github.ismoy/imagepickerkmp.svg?label=Maven%20Central" alt="Maven Central"></a>
  <a href="https://www.npmjs.com/package/imagepickerkmp"><img src="https://img.shields.io/npm/v/imagepickerkmp.svg?label=NPM" alt="NPM Version"></a>
  <a href="https://www.npmjs.com/package/imagepickerkmp"><img src="https://img.shields.io/npm/dt/imagepickerkmp.svg" alt="NPM Downloads"></a>
  <a href="https://github.com/ismoy/ImagePickerKMP/releases"><img src="https://img.shields.io/github/v/release/ismoy/ImagePickerKMP?label=GitHub%20Release" alt="GitHub Release"></a>
  <a href="https://github.com/ismoy/ImagePickerKMP/stargazers"><img src="https://img.shields.io/github/stars/ismoy/ImagePickerKMP?style=social" alt="GitHub Repo stars"></a>
    <a href="https://github.com/ismoy/ImagePickerKMP/commits/main"><img src="https://img.shields.io/github/last-commit/ismoy/ImagePickerKMP" alt="GitHub last commit"></a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Compose%20Multiplatform-green" alt="Compose Multiplatform">
  <img src="https://img.shields.io/badge/Platform-Android-green" alt="Android">
  <img src="https://img.shields.io/badge/Platform-iOS-blue" alt="iOS">
  <img src="https://img.shields.io/badge/Platform-Desktop-orange" alt="Desktop">
  <img src="https://img.shields.io/badge/Platform-JS-yellow" alt="JavaScript">
  <img src="https://img.shields.io/badge/Platform-WASM-purple" alt="WebAssembly">
 <!-- <a href="https://github.com/ismoy/ImagePickerKMP/actions/workflows/detekt.yml">
  <img src="https://github.com/ismoy/ImagePickerKMP/actions/workflows/detekt.yml/badge.svg?branch=main" alt="Detekt"></a>-->
</p>

---
## Example

###  **Complete Example App**
**[ ImagePickerKMP-Example →](https://github.com/ismoy/CameraKMP)**

Full-featured sample application showcasing:
- All library features and configurations

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
        config = GalleryPickerConfig(
            includeExif = true // Enable EXIF data extraction
        ),
        onPhotosSelected = { photos ->
            selectedImages = photos
            showGallery = false
            // Access EXIF data for each selected photo
            photos.forEach { photo ->
                val exifData = photo.exif
                println("Camera: ${exifData?.camera}")
                println("Location: ${exifData?.location}")
            }
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
-  **EXIF Metadata** - GPS, camera info, timestamps (Android/iOS)
-  **PDF Support** - Select PDF documents alongside images
-  **Extension Functions** - Easy image processing (`loadPainter()`, `loadBytes()`, `loadBase64()`)
-  **Permission Handling** - Automatic permission management
-  **Async Processing** - Non-blocking UI with coroutines
-  **Format Support** - JPEG, PNG, HEIC, HEIF, WebP, GIF, BMP, PDF

##  Platform Support

| Platform | Minimum Version | Camera | Gallery | Crop | EXIF | Status |
|----------|----------------|--------|---------|------|------|--------|
| Android  | API 21+       | ✅ | ✅ | ✅ | ✅ | ✅ |
| iOS      | iOS 12.0+     | ✅ | ✅ | ✅ | ✅ | ✅ |
| Desktop  | JDK 11+       | ❌ | ✅ | ✅ | ❌ | ✅ |
| JS/Web      | Modern Browsers| ❌ | ✅ | ✅ | ❌ | ✅ |
| Wasm/Web      | Modern Browsers| ✅ |

---

##  Live Demos

###  **Online Demos**
**[ View Interactive Demos →](https://ismoy.github.io/ImagePickerKMP-Demos)**

Experience ImagePickerKMP in action:
-  **Mobile Demos** - Android & iOS camera/gallery functionality
-  **Desktop Demo** - File picker and image processing
-  **Web Demo** - React integration with WebRTC camera
-  **Crop Demo** - Interactive image cropping across platforms


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

### EXIF Metadata Extraction
```kotlin
ImagePickerLauncher(
    config = ImagePickerConfig(
        onPhotoCaptured = { result ->
            result.exif?.let { exif ->
                println(" Location: ${exif.latitude}, ${exif.longitude}")
                println(" Camera: ${exif.cameraModel}")
                println(" Taken: ${exif.dateTaken}")
            }
        },
        cameraCaptureConfig = CameraCaptureConfig(
            includeExif = true  // Android/iOS only
        )
    )
)

```
### EXIF Metadata Extraction
```kotlin
GalleryPickerLauncher(
    allowMultiple = true,
    mimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG),
    includeExif = true  // Android/iOS only
)
```
### Multiple Selection with Filtering
```kotlin
// Images only
GalleryPickerLauncher(
    allowMultiple = true,
    mimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG),
    enableCrop = true
)

// Images and PDFs
GalleryPickerLauncher(
    allowMultiple = true,
    mimeTypes = listOf(
        MimeType.IMAGE_JPEG,
        MimeType.IMAGE_PNG,
        MimeType.APPLICATION_PDF  // PDF support
    )
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

### Experimental Cloud OCR
Need to extract text from images or documents? Try the new experimental OCR functionality:

```kotlin
 var isOCRActive = remember { mutableStateOf(false) }
 var resultOCR by remember { mutableStateOf<OCRResult?>(null) }
@OptIn(ExperimentalOCRApi::class)
  if (isOCRActive) {
   ImagePickerLauncherOCR(
      config = ImagePickerOCRConfig(
       scanMode = ScanMode.Cloud(
        provider = CloudOCRProvider.Gemini("${your_gemini_api_key}")
        ),
      onOCRCompleted = { result ->
        resultOCR = result
         isOCRActive=false
        },
        onError = {
        isOCRActive =false
        println("Error en OCR: $it")
         },
       onCancel = {
        isOCRActive =false
        },
       directCameraLaunch = false // IOS,
        allowedMimeTypes =listOf(MimeType.APPLICATION_PDF, MimeType.IMAGE_ALL),
         )
       )
    }
    
    Button(onClick{
      isOCRActive =true
    }){
      Text("Click me")
    }
```
### More Options

#### SCustom OCR service with simple authentication:
```kotlin
CloudOCRProvider.Custom(
    name = "MyCompany OCR",
    baseUrl = "https://api.mycompany.com/ocr/analyze",
    apiKey = "abc123def456",
    requestFormat = RequestFormat.MULTIPART_FORM
)
```

#### Service without authentication (local or public):
```kotlin
CloudOCRProvider.Custom(
    name = "Local OCR Server",
    baseUrl = "http://localhost:8080/api/ocr",
    apiKey = null, // Sin API key
    requestFormat = RequestFormat.JSON
)
```

#### Service with custom headers:
```kotlin
CloudOCRProvider.Custom(
    name = "Enterprise OCR Service",
    baseUrl = "https://enterprise-ocr.internal.com/v2/extract",
    headers = mapOf(
        "X-API-Version" to "2.1",
        "X-Client-ID" to "mobile-app",
        "Authorization" to "Bearer $token",
        "User-Agent" to "ImagePickerKMP/1.0"
    ),
    requestFormat = RequestFormat.MULTIPART_FORM,
    model = "enterprise-model-v3"
)
```
#### Service using JSON request format:
```kotlin
CloudOCRProvider.Custom(
    name = "JSON OCR API",
    baseUrl = "https://api.json-ocr.com/v1/process",
    apiKey = "json-api-key-123",
    headers = mapOf(
        "Content-Type" to "application/json"
    ),
    requestFormat = RequestFormat.JSON
)
```
### `RequestFormat` Options
- **RequestFormat.MULTIPART_FORM**: For APIs expecting `multipart/form-data`.
- **RequestFormat.JSON**: For APIs expecting JSON with a Base64-encoded image.

---

### Fields available in CloudOCRProvider.Custom`
- **name**: Descriptive name of your OCR service.
- **baseUrl**: Base URL of your OCR API.
- **apiKey** (optional): API key, may be `null`.
- **headers** (optional): Custom HTTP headers.
- **requestFormat**: Request format (`MULTIPART_FORM` o `JSON`).
- **model** (optional): Specific model to use.



**Supported Providers**: Gemini, OpenAI, Claude, Azure, Ollama, and custom services.

---


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
### Smart Gallery vs File Explorer Detection
- **Images**: Opens native Android gallery for photos
- **PDFs**: Opens file explorer for document access  
- **Mixed Types**: Automatically chooses best picker for content type
- **Automatic Detection**: No configuration needed - works out of the box!

**[Complete React Integration Guide →](REACT_INTEGRATION_GUIDE.md)**

##  Support & Contributing

-  [Report Issues](https://github.com/ismoy/ImagePickerKMP/issues)
-  [Discord Community](https://discord.gg/EjSQTeyh)
-  [Contributing Guide](docs/CONTRIBUTING.md)
-  [Email Support](mailto:belizairesmoy72@gmail.com)

---


**Made with ❤️ for the Kotlin Multiplatform community**  
*⭐ Star this repo if it helped you!*
