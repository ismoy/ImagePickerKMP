# ImagePickerKMP
**Cross-platform Image Picker & Camera Library for Kotlin Multiplatform**

Easily capture or select images on Android, iOS, Desktop, and Web — all with a single API.  
Built with **Compose Multiplatform**, designed for **simplicity, performance, and flexibility**.

<img referrerpolicy="no-referrer-when-downgrade" src="https://static.scarf.sh/a.png?x-pxid=b9cde436-d518-45b7-9a4a-7b4e084aeffa" />

<p align="center">
  <img src="https://raw.githubusercontent.com/ismoy/CameraKMP/main/thumnailImagePickerKMP.png" alt="ImagePickerKMP Banner" width="100%">
</p>

<p align="center">
  <a href="https://github.com/ismoy/ImagePickerKMP/actions"><img src="https://github.com/ismoy/ImagePickerKMP/workflows/CI/badge.svg" alt="CI"></a>
  <a href="https://codecov.io/gh/ismoy/ImagePickerKMP"><img src="https://codecov.io/gh/ismoy/ImagePickerKMP/branch/main/graph/badge.svg" alt="Code Coverage"></a>
  <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License"></a>
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-2.3.20-blue.svg" alt="Kotlin"></a>
  <!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
  <a href="#contributors"><img src="https://img.shields.io/github/all-contributors/ismoy/ImagePickerKMP?color=ee8449&style=flat-square" alt="All Contributors"></a>
  <!-- ALL-CONTRIBUTORS-BADGE:END -->
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
  <a href="https://github.com/ismoy/ImagePickerKMP/actions/workflows/detekt.yml">
  <img src="https://github.com/ismoy/ImagePickerKMP/actions/workflows/detekt.yml/badge.svg?branch=main" alt="Detekt"></a>
</p>

---

<p align="center">
  <a href="https://imagepickerkmp.dev/">
    <img src="https://img.shields.io/badge/%20Full%20Documentation-Visit%20Docs%20Site-0ea5e9?style=for-the-badge&logoColor=white" alt="Documentation Site">
  </a>
  &nbsp;
  <a href="https://github.com/sponsors/ismoy">
    <img src="https://img.shields.io/badge/Sponsor-%E2%9D%A4-red?style=for-the-badge&logo=github" alt="Sponsor">
  </a>
</p>

> **ImagePickerKMP saves you 2 weeks of native Android/iOS/Web integration work.**  
> It's free and open source. If your app or company benefits from it, consider sponsoring to keep it maintained and updated with every new KMP/Compose release.  
> [→ Become a sponsor](https://github.com/sponsors/ismoy)

---
## Example

###  **Complete Example App**
**[ ImagePickerKMP-Example →](https://github.com/ismoy/CameraKMP)**

Full-featured sample application showcasing:
- All library features and configurations

##  Quick Start

### ⚠️ Requirements

| Requirement | Minimum version |
|---|---|
| **Kotlin** | **2.3.20** *(breaking change — see [CHANGELOG](docs/CHANGELOG.md))* |
| Compose Multiplatform | 1.10.3 |
| Ktor | 3.4.1 |
| Android `minSdk` | 24 |
| Android `compileSdk` | 36 |

> **Note:** This library is compiled with Kotlin 2.3.20. Projects using Kotlin < 2.3.x will get an ABI incompatibility error at compile time. If you need Kotlin 2.1.x support, use a previous version of this library.

### Installation

**Kotlin Multiplatform:**
```kotlin
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.35-alpha1")
}
```

**React/JavaScript:**
```bash
npm install imagepickerkmp
```

### New — `rememberImagePickerKMP` (recommended)

The modern, idiomatic Compose API. A single state holder — no manual booleans, no `Render()` call needed.

```kotlin
@Composable
fun basicUsageScreen() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(
                allowMultiple = true,
                selectionLimit = 20
            )
        )
    )
    val result = picker.result

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Basic Usage") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { picker.launchCamera() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Camera")
                    }
                    Button(
                        onClick = { picker.launchGallery() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Gallery")
                    }
                }
            }
        }
    ){scaffoldPadding->
        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when (result) {

                    is ImagePickerResult.Loading -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = "Loading...",
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }

                    is ImagePickerResult.Success -> {
                        // Result here
                    }

                    is ImagePickerResult.Error -> {
                        Text(
                            text = "Error: ${result.exception.message}",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    is ImagePickerResult.Dismissed -> {
                        Text("Selection cancelled", color = Color.Gray)
                    }

                    is ImagePickerResult.Idle -> {
                        Text("Press a button to get started", color = Color.Gray)
                    }
                }
            }
        }
    }
}
```

**Per-launch overrides:**
```kotlin
// Override gallery options for a single launch
picker.launchGallery(
    allowMultiple = true,
    selectionLimit = 5,
    mimeTypes = listOf(MimeType.IMAGE_JPEG),
    includeExif = true
)

// Override camera options for a single launch
picker.launchCamera(
    cameraCaptureConfig = CameraCaptureConfig(compressionLevel = CompressionLevel.HIGH),
    enableCrop = false
)
```

---

## New API vs Legacy API — Migration Guide

> **TL;DR:** Use `rememberImagePickerKMP` for all new code. The legacy `ImagePickerLauncher` / `GalleryPickerLauncher` are **deprecated** and will be removed in a future major release.

### Side-by-side comparison

| | Legacy API (v1) — Deprecated | New API (v2) — Recommended |
|---|---|---|
| Camera | `ImagePickerLauncher(config = ...)` | `picker.launchCamera()` |
| Gallery | `GalleryPickerLauncher(...)` | `picker.launchGallery()` |
| Result handling | Callbacks (`onPhotoCaptured`, `onDismiss`, `onError`) | Reactive `when (picker.result)` |
| State management | Manual `showCamera`, `showGallery` booleans | Automatic via `ImagePickerKMPState` |
| Per-launch config | Not supported | Override any param on each `launch*()` call |
| Reset | Call `onDismiss` callback | `picker.reset()` |
| Configuration | `ImagePickerConfig` + `GalleryPickerConfig` | `ImagePickerKMPConfig` (unified) |

### Migration table

| Legacy pattern | New API equivalent |
|---|---|
| `showCamera = true` | `picker.launchCamera()` |
| `showGallery = true` | `picker.launchGallery()` |
| `onPhotoCaptured = { result -> ... }` | `is ImagePickerResult.Success -> result.photos` |
| `onDismiss = { showCamera = false }` | `is ImagePickerResult.Dismissed -> ...` |
| `onError = { e -> ... }` | `is ImagePickerResult.Error -> result.exception` |
| `ImagePickerConfig(cameraCaptureConfig = ...)` | `ImagePickerKMPConfig(cameraCaptureConfig = ...)` |
| `GalleryPickerConfig(includeExif = true)` | `ImagePickerKMPConfig(galleryConfig = GalleryConfig(includeExif = true))` |
| `allowMultiple = true` in `GalleryPickerLauncher` | `picker.launchGallery(allowMultiple = true)` |

### Legacy API (still functional, migration recommended)

The legacy API still works and will **not break** existing apps. You will see a compiler **warning** recommending migration to `rememberImagePickerKMP`.

**Camera Capture (legacy):**
```kotlin
var showCamera by remember { mutableStateOf(false) }
var capturedPhoto by remember { mutableStateOf<PhotoResult?>(null) }

if (showCamera) {
    ImagePickerLauncher(  // Deprecated — migrate to rememberImagePickerKMP
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

**Gallery Selection (legacy):**
```kotlin
var showGallery by remember { mutableStateOf(false) }
var selectedImages by remember { mutableStateOf<List<PhotoResult>>(emptyList()) }

if (showGallery) {
    GalleryPickerLauncher(  // Deprecated — migrate to rememberImagePickerKMP
        config = GalleryPickerConfig(includeExif = true),
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


## Key Features

- **`rememberImagePickerKMP`** — New idiomatic API: single state holder, `launchCamera()` / `launchGallery()` with per-launch overrides, reactive result via `ImagePickerResult` (Idle → Loading → Success/Dismissed/Error). No `Render()`, no manual booleans.
- **Cross-platform** — Android, iOS, Desktop, Web
- **Camera & Gallery** — Direct access with unified API
- **Image Cropping** — Built-in crop functionality
- **Smart Compression** — Configurable quality levels
- **EXIF Metadata** — GPS, camera info, timestamps (Android/iOS)
- **PDF Support** — Select PDF documents alongside images
- **Extension Functions** — Easy image processing (`loadPainter()`, `loadBytes()`, `loadBase64()`)
- **Permission Handling** — Automatic permission management
- **Async Processing** — Non-blocking UI with coroutines
- **Format Support** — JPEG, PNG, HEIC, HEIF, WebP, GIF, BMP, PDF

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

## React/Web Integration

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

## ⚠️ Known Issues & Troubleshooting

### iOS build fails with `_OBJC_CLASS_$_CLLocation` linker error

If your iOS build fails with:

```
ld: Undefined symbols: _OBJC_CLASS_$_CLLocation
linker command failed with exit code 1
```

**Android and JVM Desktop work fine**, but iOS fails during the linking phase.

**Fix:** Add `CoreLocation.framework` manually in Xcode:

1. Select your app target → **Build Phases → Link Binary With Libraries**
2. Click **+**, search for **CoreLocation**, and click **Add**
3. Clean (**⇧⌘K**) and rebuild

> No code changes needed. See [FAQ](docs/FAQ.md#ios-build-fails-with-linker-error-_objc_class_cllocation-or-corelocationframework-not-found) and [Integration Guide](docs/INTEGRATION_GUIDE.md) for full details.

##  Support & Contributing

-  [Report Issues](https://github.com/ismoy/ImagePickerKMP/issues)
-  [Discord Community](https://discord.gg/EjSQTeyh)
-  [Contributing Guide](docs/CONTRIBUTING.md)
-  [Email Support](mailto:belizairesmoy72@gmail.com)

---

## Sponsors & Backers

ImagePickerKMP is free and open source. Maintaining it across **Android, iOS, Desktop, Web and WASM** with every Kotlin/Compose Multiplatform release takes real time and effort.

If this library saves you time or money in production, please consider supporting it:

| Tier | Amount | Benefit |
|------|--------|---------|
| ☕ Coffee | $5/mo | Name in the backers list |
| 🥈 Silver | $25/mo | Logo in README + priority issue response |
<p align="center">
  <a href="https://github.com/sponsors/ismoy">
    <img src="https://img.shields.io/badge/Sponsor%20ImagePickerKMP-%E2%9D%A4-red?style=for-the-badge&logo=github" alt="Sponsor">
  </a>
</p>

### Current Sponsors

<!-- SPONSORS-LIST:START - Do not remove or modify this section -->
<table>
  <tr>
    <td align="center">
      <a href="https://github.com/james-codersHT">
        <img src="https://github.com/james-codersHT.png" width="80px" alt="james-codersHT"/><br/>
        <sub><b>james-codersHT</b></sub>
      </a>
    </td>
  </tr>
</table>
<!-- SPONSORS-LIST:END -->

> Sponsors get their name/logo displayed here. [→ Become a sponsor](https://github.com/sponsors/ismoy)

---

## Contributors

Thanks to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%">
        <a href="https://github.com/ismoy">
          <img src="https://avatars.githubusercontent.com/u/72107070?v=4" width="100px;" alt="ismoy"/><br />
          <sub><b>ismoy</b></sub>
        </a><br />
        <a href="https://github.com/ismoy/ImagePickerKMP/commits?author=ismoy" title="Contributions">💻 📖 🚧 🎨 🤔</a>
      </td>
      <td align="center" valign="top" width="14.28%">
        <a href="https://github.com/medAndro">
          <img src="https://avatars.githubusercontent.com/u/88672474?v=4" width="100px;" alt="medAndro"/><br />
          <sub><b>medAndro</b></sub>
        </a><br />
        <a href="https://github.com/ismoy/ImagePickerKMP/commits?author=medAndro" title="Contributions">💻 🐛</a>
      </td>
      <td align="center" valign="top" width="14.28%">
        <a href="https://github.com/daniil-pastuhov">
          <img src="https://avatars.githubusercontent.com/u/8494442?v=4" width="100px;" alt="daniil-pastuhov"/><br />
          <sub><b>daniil-pastuhov</b></sub>
        </a><br />
        <a href="https://github.com/ismoy/ImagePickerKMP/commits?author=daniil-pastuhov" title="Contributions">💻</a>
      </td>
      <td align="center" valign="top" width="14.28%">
        <a href="https://github.com/YaminMahdi">
          <img src="https://avatars.githubusercontent.com/u/48239104?v=4" width="100px;" alt="YaminMahdi"/><br />
          <sub><b>YaminMahdi</b></sub>
        </a><br />
        <a href="https://github.com/ismoy/ImagePickerKMP/commits?author=YaminMahdi" title="Contributions">💻</a>
      </td>
    </tr>
  </tbody>
</table>
<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->
<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!

---

**Made with ❤️ for the Kotlin Multiplatform community**  
*Star this repo if it helped you!*
