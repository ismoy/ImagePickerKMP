This document is also available in Spanish: [API_REFERENCE.es.md](API_REFERENCE.es.md)

# API Reference

Complete API documentation for the ImagePickerKMP library.

## Table of Contents

- [API v2 vs Legacy API — Migration Guide](#api-v2-vs-legacy-api--migration-guide)
- [rememberImagePickerKMP (Recommended)](#rememberimagePickerkmp)
- [Main Components](#main-components)
- [Data Classes](#data-classes)
- [Enums](#enums)
- [Configuration](#configuration)
- [Platform-specific APIs](#platform-specific-apis)

---

## API v2 vs Legacy API — Migration Guide

> **TL;DR:** Use `rememberImagePickerKMP` for all new code. `ImagePickerLauncher` and `GalleryPickerLauncher` are **deprecated** (`@Deprecated(level = WARNING)`) and will be removed in a future major release. Existing code keeps working without changes.

### At-a-glance comparison

| Feature | Legacy API (v1) ⚠️ Deprecated | New API (v2) ✅ Recommended |
|---|---|---|
| Camera | `ImagePickerLauncher(config = ImagePickerConfig(...))` | `picker.launchCamera()` |
| Gallery | `GalleryPickerLauncher(onPhotosSelected = { }, ...)` | `picker.launchGallery()` |
| Result | Callbacks: `onPhotoCaptured`, `onDismiss`, `onError` | Reactive: `when (picker.result) { is Success -> }` |
| State management | Manual `showCamera`/`showGallery` booleans | Automatic via `ImagePickerKMPState` |
| Per-launch overrides | Not supported | Every `launch*()` parameter is optional override |
| Reset | Call `onDismiss` callback | `picker.reset()` |
| Config class | `ImagePickerConfig` + `GalleryPickerConfig` | `ImagePickerKMPConfig` (unified) |
| `Render()` call needed | ❌ Legacy required wrapper composables | ❌ No `Render()` in new API either |

### Migration table

| Legacy pattern | New API equivalent |
|---|---|
| `var showCamera by remember { mutableStateOf(false) }` | *(remove — not needed)* |
| `showCamera = true` | `picker.launchCamera()` |
| `showGallery = true` | `picker.launchGallery()` |
| `onPhotoCaptured = { result -> ... }` | `is ImagePickerResult.Success -> result.photos` |
| `onDismiss = { showCamera = false }` | `is ImagePickerResult.Dismissed -> ...` |
| `onError = { e -> ... }` | `is ImagePickerResult.Error -> result.exception` |
| `ImagePickerConfig(cameraCaptureConfig = ...)` | `ImagePickerKMPConfig(cameraCaptureConfig = ...)` |
| `GalleryPickerConfig(includeExif = true)` | `ImagePickerKMPConfig(galleryConfig = GalleryConfig(includeExif = true))` |
| `GalleryPickerLauncher(allowMultiple = true)` | `picker.launchGallery(allowMultiple = true)` |
| `GalleryPickerLauncher(selectionLimit = 5)` | `picker.launchGallery(selectionLimit = 5)` |
| `GalleryPickerLauncher(mimeTypes = listOf(...))` | `picker.launchGallery(mimeTypes = listOf(...))` |

### Code comparison

**Legacy — camera capture (still works, deprecated):**
```kotlin
var showCamera by remember { mutableStateOf(false) }

if (showCamera) {
    ImagePickerLauncher(  // ⚠️ Deprecated
        config = ImagePickerConfig(
            onPhotoCaptured = { result -> /* use result */ showCamera = false },
            onDismiss = { showCamera = false },
            onError = { showCamera = false }
        )
    )
}
Button(onClick = { showCamera = true }) { Text("Camera") }
```

**New API — camera capture (recommended):**
```kotlin
val picker = rememberImagePickerKMP()

Button(onClick = { picker.launchCamera() }) { Text("Camera") }

when (val result = picker.result) {
    is ImagePickerResult.Success   -> result.first?.let { /* use photo */ }
    is ImagePickerResult.Dismissed -> { /* user cancelled */ }
    is ImagePickerResult.Error     -> Text("Error: ${result.exception.message}")
    is ImagePickerResult.Loading   -> CircularProgressIndicator()
    is ImagePickerResult.Idle      -> { /* initial state */ }
}
```

**Legacy — gallery selection (still works, deprecated):**
```kotlin
var showGallery by remember { mutableStateOf(false) }

if (showGallery) {
    GalleryPickerLauncher(  // ⚠️ Deprecated
        onPhotosSelected = { photos -> selectedImages = photos; showGallery = false },
        onDismiss = { showGallery = false },
        onError = { showGallery = false },
        allowMultiple = true,
        mimeTypes = listOf(MimeType.IMAGE_JPEG)
    )
}
Button(onClick = { showGallery = true }) { Text("Gallery") }
```

**New API — gallery selection (recommended):**
```kotlin
val picker = rememberImagePickerKMP(
    config = ImagePickerKMPConfig(
        galleryConfig = GalleryConfig(allowMultiple = true, selectionLimit = 10)
    )
)

Button(onClick = { picker.launchGallery() }) { Text("Gallery") }
// or with per-launch override:
Button(onClick = { picker.launchGallery(allowMultiple = true, selectionLimit = 5) }) { Text("Gallery (5 max)") }

when (val result = picker.result) {
    is ImagePickerResult.Success -> result.photos.forEach { /* use each photo */ }
    else -> { /* handle other states */ }
}
```

> **Note — internal architecture:** `rememberImagePickerKMP` calls `ImagePickerLauncher` / `GalleryPickerLauncher` internally as platform-specific rendering layers. The internal call site uses `@Suppress("DEPRECATION")` so consumers of the new API see no compiler warnings. Only developers who call the legacy functions directly see the migration warning.

---

## rememberImagePickerKMP (Recommended) {#rememberimagePickerkmp}

> **Available since:** `1.0.35-alpha1` · All platforms

`rememberImagePickerKMP` es el punto de entrada **recomendado** para Compose. Retorna un `ImagePickerKMPState` — un state holder estable que reemplaza los booleans manuales `showCamera`/`showGallery`. **No requiere ningún `Render()` ni composable adicional** — el picker se auto-gestiona al invocar `launchCamera()` o `launchGallery()`.

### Signature

```kotlin
@Composable
fun rememberImagePickerKMP(
    config: ImagePickerKMPConfig = ImagePickerKMPConfig()
): ImagePickerKMPState
```

### ImagePickerKMPConfig

```kotlin
data class ImagePickerKMPConfig(
    val cameraCaptureConfig: CameraCaptureConfig = CameraCaptureConfig(),
    val galleryConfig: GalleryConfig = GalleryConfig(),
    val enableCrop: Boolean = false,
    val cropConfig: CropConfig = CropConfig(),
    val uiConfig: UiConfig = UiConfig(),
    val permissionAndConfirmationConfig: PermissionAndConfirmationConfig = PermissionAndConfirmationConfig()
)
```

| Property | Type | Default | Description |
|---|---|---|---|
| `cameraCaptureConfig` | `CameraCaptureConfig` | defaults | Camera behaviour, compression, EXIF, UI styling |
| `galleryConfig` | `GalleryConfig` | defaults | Multi-select, MIME types, selection limit, EXIF |
| `enableCrop` | `Boolean` | `false` | Show crop UI after every capture / selection |
| `cropConfig` | `CropConfig` | defaults | Crop shape, aspect ratio, freeform |
| `uiConfig` | `UiConfig` | defaults | Custom button colors and icons |
| `permissionAndConfirmationConfig` | `PermissionAndConfirmationConfig` | defaults | Custom permission dialogs and confirmation screen |

### ImagePickerKMPState

| Method / Property | Description |
|---|---|
| `result: ImagePickerResult` | Estado reactivo. Empieza en `Idle`. Observar con `when`. Se actualiza automáticamente cuando el picker abre, el usuario selecciona o cancela. |
| `launchCamera(cameraCaptureConfig?, enableCrop?, onDismiss?, onError?)` | Abre la cámara. Todos los parámetros son opcionales y sobreescriben el config global solo para este lanzamiento. |
| `launchGallery(allowMultiple?, mimeTypes?, selectionLimit?, enableCrop?, includeExif?, redactGpsData?, mimeTypeMismatchMessage?, cameraCaptureConfig?, onDismiss?, onError?)` | Abre la galería. Todos los parámetros son opcionales y sobreescriben el `GalleryConfig` global solo para este lanzamiento. |
| `reset()` | Resetea `result` a `Idle` y cierra cualquier picker activo. |

> ⚠️ **No existe `Render()` ni `launchPicker()`** en esta API. El picker se gestiona internamente.

### ImagePickerResult

```kotlin
sealed class ImagePickerResult {
    data object Idle        : ImagePickerResult()   // Estado inicial / tras reset()
    data object Loading     : ImagePickerResult()   // Picker abierto, esperando acción
    data class  Success(val photos: List<PhotoResult>) : ImagePickerResult()
    data object Dismissed   : ImagePickerResult()   // Usuario cerró sin seleccionar
    data class  Error(val exception: Exception) : ImagePickerResult()
}
```

`Success` also exposes `val first: PhotoResult?` — primera foto (útil en captura de cámara).

### Ejemplo real completo

```kotlin
@Composable
fun MyScreen(innerPadding: PaddingValues) {

    // No se necesita Render() ni booleans manuales
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            enableCrop = false,
            galleryConfig = GalleryConfig(
                allowMultiple = true,
                selectionLimit = 10,
                includeExif = true,
                redactGpsData = true,
                mimeTypes = listOf(MimeType.IMAGE_JPEG)
            )
        )
    )

    val result = picker.result

    Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            when (result) {
                is ImagePickerResult.Loading -> CircularProgressIndicator()
                is ImagePickerResult.Success -> {
                    val photos = result.photos
                    if (photos.size == 1) {
                        photos.first().loadPainter()?.let { Image(it, contentDescription = null) }
                    } else {
                        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                            items(photos) { photo ->
                                photo.loadPainter()?.let { Image(it, contentDescription = null) }
                            }
                        }
                    }
                }
                is ImagePickerResult.Error     -> Text("Error: ${result.exception.message}")
                is ImagePickerResult.Dismissed,
                is ImagePickerResult.Idle      -> Text("No image selected")
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Button(onClick = { picker.launchCamera() }, modifier = Modifier.weight(1f)) {
                Text("Cámara")
            }
            Button(onClick = { picker.launchGallery() }, modifier = Modifier.weight(1f)) {
                Text("Galería")
            }
        }
    }
}
```

### Override por lanzamiento

```kotlin
// Override solo para este botón — no afecta el config global
Button(onClick = {
    picker.launchGallery(
        allowMultiple = true,
        mimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG),
        selectionLimit = 5,
        includeExif = true
    )
}) { Text("Pick up to 5 images") }

// Cámara con compresión HIGH y crop, solo para este tap
Button(onClick = {
    picker.launchCamera(
        cameraCaptureConfig = CameraCaptureConfig(compressionLevel = CompressionLevel.HIGH),
        enableCrop = true
    )
}) { Text("Camera HD") }
```

---

##  Photo Capture – Specific Documentation

## Description
The photo capture functionality in ImagePickerKMP allows developers to integrate a modern, customizable, and cross-platform camera experience into their applications. It includes flash control, camera switching, preview, confirmation, and complete UI customization.

---

## Basic photo capture example for Android and iOS

```kotlin
ImagePickerLauncher(
    config = ImagePickerConfig(
        onPhotoCaptured = { result ->
            // Handle the captured photo result
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            // Handle errors
            println("Error: ${exception.message}")
        },
        onDismiss = {
            // Handle when user cancels
            println("User cancelled")
        }
    )
)
```

---

## Advanced example: Confirmation customization (Android only)

```kotlin
ImagePickerLauncher(
    config = ImagePickerConfig(
        onPhotoCaptured = { result ->
            // Handle the captured photo result
            cameraPhoto = result
            showCameraPicker = false
        },
        onError = { exception ->
            // Handle errors
            showCameraPicker = false
        },
        onDismiss = {
            // Handle cancellation
            showCameraPicker = false
        },
        // Custom confirmation view (Android only)
        cameraCaptureConfig = CameraCaptureConfig(
            preference = CapturePhotoPreference.QUALITY,
            permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                customConfirmationView = { photoResult, onConfirm, onRetry ->
                    MyCustomConfirmationView(
                        result = photoResult,
                        onConfirm = onConfirm,
                        onRetry = onRetry
                    )
                }
            )
        )
    )
)
```

## Example: Skip confirmation screen (Android only)

```kotlin
ImagePickerLauncher(
    config = ImagePickerConfig(
        onPhotoCaptured = { result ->
            // Photo is captured automatically without confirmation
            cameraPhoto = result
            showCameraPicker = false
        },
        onError = { exception ->
            // Handle errors
            showCameraPicker = false
        },
        onDismiss = {
            // Handle cancellation
            showCameraPicker = false
        },
        // Configuration to skip confirmation
        cameraCaptureConfig = CameraCaptureConfig(
            preference = CapturePhotoPreference.QUALITY,
            permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                skipConfirmation = true // NEW! Skips the confirmation screen
            )
        )
    )
)
```

---

## Relevant parameters

- **onPhotoCaptured**: Callback with the photo result (`CameraPhotoHandler.PhotoResult`)
- **onError**: Callback to handle errors (`Exception`)
- **onDismiss**: Callback when user cancels
- **preference**: Photo quality preference (`CapturePhotoPreference.FAST`, `BALANCED`, `QUALITY`)
- **customConfirmationView**: Composable to customize confirmation UI (Android only)
- **customPickerDialog**: Composable to customize selection dialog (iOS only)

---

## User experience
- **Preview**: User sees the camera in real-time.
- **Flash control**: Button to toggle between Auto, On, Off (visual icons).
- **Camera switching**: Button to toggle between rear and front camera.
- **Capture**: Central button to take the photo.
- **Confirmation**: Modern view to accept or retry the photo, with customizable texts and icons.

---

## Notes and recommendations
- The permission system is managed automatically.
- You can completely customize the confirmation UI.
- Default texts are in English, but you can easily localize them.
- Flash only works in `BALANCED` or `QUALITY` quality modes.
- If you need even more control, implement your own `customConfirmationView`.

---

## Code references in the project
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/CameraCapturePreview.kt**: Preview logic and camera controls.
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/ImageConfirmationViewWithCustomButtons.kt**: Photo confirmation UI.
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/CameraController.kt**: Camera control logic, flash and camera switching.
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/CameraXManager.kt**: Abstraction for camera management.

---

##  Gallery Image Selection – Specific Documentation

## Description
The gallery functionality in ImagePickerKMP allows developers to integrate a modern and customizable experience for selecting images from the device gallery. It supports single or multiple selection, file type filters, and custom confirmation.

---
## Main Components

### ImagePickerLauncher

## Basic image selection example

```kotlin
GalleryPickerLauncher(
    onPhotosSelected = { results ->
        // Handle selected images
        println("Selected images: ${results}")
    },
    onError = { exception ->
        // Handle errors
        println("Error: ${exception.message}")
    },
    onDismiss = {
        // Handle when user cancels
        println("User cancelled selection")
    }
)
```

---

## Advanced example: Multiple selection with filters

```kotlin
GalleryPickerLauncher(
    onPhotosSelected = { results ->
        // Handle multiple selected images
        selectedImages = results
        showGalleryPicker = false
    },
    onError = { exception ->
        // Handle errors
        showGalleryPicker = false
    },
    onDismiss = {
        // Handle cancellation
        showGalleryPicker = false
    },
    allowMultiple = true, // Allow multiple selection
    mimeTypes = listOf("image/jpeg", "image/png"), // Filter by file types
    selectionLimit = 30 // Selection limit (maximum 30 images) iOS only, Android has no limit
)
```

---

## Relevant parameters

- **onPhotosSelected**: Callback with the list of selected images (`List<GalleryPhotoHandler.PhotoResult>`)
- **onError**: Callback to handle errors (`Exception`)
- **onDismiss**: Callback when user cancels
- **allowMultiple**: Allows selecting multiple images (default: `false`)
- **mimeTypes**: List of allowed MIME types (default: `listOf("image/*")`)
- **selectionLimit**: Maximum selection limit (default: `30`)

---

## User experience
- **Gallery selector**: User can choose one image or several (if `allowMultiple` is enabled)
- **Filters**: File type filters can be applied using `mimeTypes`
- **Limits**: A maximum selection limit can be set with `selectionLimit`

---

## Notes and recommendations
- The permission system is managed automatically
- Multiple selection is supported on both platforms (Android and iOS)
- MIME types allow filtering by specific image formats
- Selection limit helps control application performance

---

## Code references in the project
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/GalleryPickerLauncher.android.kt**: Image selection logic on Android.
- **library/src/commonMain/kotlin/io/github/ismoy/imagepickerkmp/GalleryPickerLauncher.kt**: Cross-platform abstraction for gallery.
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/ImageConfirmationViewWithCustomButtons.kt**: Image confirmation UI.

---

##  Image Compression – Specific Documentation

## Description
The image compression functionality in ImagePickerKMP automatically optimizes image size while maintaining acceptable quality. It works for both camera capture and gallery selection, with configurable compression levels and async processing.

---

## Features
- **Automatic compression**: Applies compression transparently during image processing
- **Configurable levels**: LOW, MEDIUM, HIGH compression options
- **Multi-format support**: JPEG, PNG, HEIC, HEIF, WebP, GIF, BMP
- **Async processing**: Non-blocking UI with Kotlin Coroutines
- **Smart optimization**: Combines dimension scaling + quality compression
- **Memory efficient**: Proper bitmap recycling and cleanup

---

## Compression Levels

| Level | Quality | Max Dimension | Use Case |
|-------|---------|---------------|----------|
| LOW | 95% | 2560px | High-quality sharing, professional use |
| MEDIUM | 75% | 1920px | **Recommended** - Social media, general use |
| HIGH | 50% | 1280px | Storage optimization, thumbnails |

---

## Camera with Compression

```kotlin
ImagePickerLauncher(
    config = ImagePickerConfig(
        onPhotoCaptured = { result ->
            val fileSizeKB = (result.fileSize ?: 0) / 1024.0
            println("Compressed image size: ${String.format("%.2f", fileSizeKB)}KB")
            println("Exact size: ${result.fileSize} bytes")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        },
        cameraCaptureConfig = CameraCaptureConfig(
            compressionLevel = CompressionLevel.MEDIUM
        )
    )
)
```

---

## Gallery with Compression

```kotlin
GalleryPickerLauncher(
    onPhotosSelected = { results ->
        results.forEach { photo ->
            val fileSizeKB = (photo.fileSize ?: 0) / 1024.0
            println("Original: ${photo.fileName}")
            println("Compressed size: ${String.format("%.2f", fileSizeKB)}KB")
            println("Exact size: ${photo.fileSize} bytes")
        }
    },
    onError = { exception ->
        println("Error: ${exception.message}")
    },
    allowMultiple = true,
    mimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG),
    cameraCaptureConfig = CameraCaptureConfig(
        compressionLevel = CompressionLevel.HIGH // Optimize for storage
    )
)
```

---

## Compression Process

1. **Image Loading**: Original image is loaded from camera/gallery
2. **Dimension Scaling**: Image is resized if larger than max dimension
3. **Quality Compression**: JPEG compression is applied based on level
4. **Temporary File**: Compressed image is saved to app cache
5. **Result Delivery**: New URI with compressed image is returned

---

## Platform Support

| Platform | Camera Compression | Gallery Compression | Async Processing |
|----------|-------------------|---------------------|------------------|
| Android | ✅ | ✅ | ✅ Coroutines |
| iOS | ✅ | ✅ | ✅ Coroutines |

---

## Performance Considerations

- **Memory Usage**: Original bitmaps are recycled after compression
- **Processing Time**: Runs on background threads (Dispatchers.IO)
- **Storage**: Compressed images are stored in app cache directory
- **Quality**: Smart balance between file size and visual quality

---

## Code References
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/data/processors/ImageProcessor.kt**: Camera compression logic
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/presentation/ui/components/GalleryPickerLauncher.android.kt**: Gallery compression implementation
- **library/src/commonMain/kotlin/io/github/ismoy/imagepickerkmp/domain/models/CompressionLevel.kt**: Compression level definitions

---

### ImagePickerLauncher

> ⚠️ **Deprecated** — `ImagePickerLauncher` is marked `@Deprecated(level = WARNING)`. It still compiles and runs normally, but the compiler will show a migration warning. **Migrate to [`rememberImagePickerKMP`](#rememberimagePickerkmp) for new code.**

Main composable for launching the image picker.

```kotlin
@Deprecated("Use rememberImagePickerKMP() instead.")
@Composable
expect fun ImagePickerLauncher(
    config: ImagePickerConfig
)
```

#### Parameters

- `config: ImagePickerConfig` - Complete image picker configuration

---

### GalleryPickerLauncher

> ⚠️ **Deprecated** — `GalleryPickerLauncher` is marked `@Deprecated(level = WARNING)`. It still compiles and runs normally, but the compiler will show a migration warning. **Migrate to [`rememberImagePickerKMP`](#rememberimagePickerkmp) for new code.**

Composable for selecting images from gallery with intelligent picker selection for Android.

```kotlin
@Composable
expect fun GalleryPickerLauncher(
    config: GalleryPickerConfig = GalleryPickerConfig(),
    onPhotosSelected: (List<PhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit = {},
    allowMultiple: Boolean = false,
    mimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL),
    selectionLimit: Long = SELECTION_LIMIT
)
```

#### Parameters

- `config` - Configuration for gallery picker behavior and Android-specific settings
- `onPhotosSelected` - Callback with the list of selected images
- `onError` - Callback to handle errors
- `onDismiss` - Callback when user cancels
- `allowMultiple` - Allows multiple selection (default: `false`)
- `mimeTypes` - List of allowed MIME types
- `selectionLimit` - Maximum selection limit

#### Smart Picker Selection (Android)

The `GalleryPickerLauncher` automatically chooses the appropriate picker based on requested MIME types:

- **Images only** (`image/*`): Opens native Android gallery using `MediaStore`
- **PDFs included** (`application/pdf`): Opens file explorer for document access
- **Mixed types**: Uses file explorer for maximum compatibility

This ensures users get the expected interface for their content type without configuration.

#### Configuration Example

```kotlin
GalleryPickerLauncher(
    config = GalleryPickerConfig(
        includeExif = true,
        androidGalleryConfig = AndroidGalleryConfig(
            forceGalleryOnly = false,
            localOnly = true
        )
    ),
    mimeTypes = listOf(MimeType.APPLICATION_PDF),
    onPhotosSelected = { results ->
        // Handle selected files
    }
)
```

---

### RequestCameraPermission

Composable for managing camera permissions.

```kotlin
@Composable
expect fun RequestCameraPermission(
    dialogConfig: CameraPermissionDialogConfig,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parameters

- `dialogConfig: CameraPermissionDialogConfig` - Permission dialog configuration
- `onPermissionPermanentlyDenied: () -> Unit` - Callback when permission is permanently denied
- `onResult: (Boolean) -> Unit` - Callback with permission result
- `customPermissionHandler: (() -> Unit)?` - Custom permission handler

---

### AndroidGalleryConfig (Android)

Configuration specific to Android gallery picker behavior.

```kotlin
data class AndroidGalleryConfig(
    val forceGalleryOnly: Boolean = true,
    val localOnly: Boolean = true
) {
    companion object {
        fun forMimeTypes(mimeTypes: List<MimeType>): AndroidGalleryConfig
        fun forMimeTypeStrings(mimeTypes: List<String>): AndroidGalleryConfig
    }
}
```

#### Properties

- `forceGalleryOnly` - **Forces gallery vs file explorer usage**
  - `true`: Uses `Intent.ACTION_PICK` + `MediaStore` (opens native gallery)
  - `false`: Uses `ActivityResultContracts.GetContent()` (may open file explorer)
  - **Default**: `true`, but automatically adjusted based on MIME types

- `localOnly` - **Include only local images**
  - `true`: Adds `EXTRA_LOCAL_ONLY` to intent (no cloud storage)
  - `false`: Allows images from cloud storage
  - **Default**: `true`

#### Convenience Methods

```kotlin
//  Automatic configuration based on MIME types
val autoConfig = AndroidGalleryConfig.forMimeTypes(listOf(MimeType.APPLICATION_PDF))
// Result: forceGalleryOnly = false (uses file explorer for PDFs)

//  Manual configuration
GalleryPickerLauncher(
    // ... other parameters ...
    androidGalleryConfig = AndroidGalleryConfig(
        forceGalleryOnly = false, // Force file explorer
        localOnly = true
    )
)
```

#### Automatic Detection Behavior

| Detected MIME Types | `forceGalleryOnly` | Result |
|--------------------|--------------------|--------|
| Images only (`image/*`) | `true` | Native gallery |
| Contains `application/pdf` | `false` | File explorer |
| Mixed types (image + others) | `false` | File explorer |
| Non-image types | `false` | File explorer |

---

## Data Classes

### CameraPhotoHandler.PhotoResult

Represents the result of a photo capture from camera.

```kotlin
data class PhotoResult(
    val uri: String,
    val width: Int,
    val height: Int,
    val fileName: String? = null,
    val fileSize: Long? = null,
    val mimeType: String? = null,
    val exif: ExifData? = null  // EXIF metadata (Android/iOS only)
)
```

### GalleryPhotoHandler.PhotoResult

Represents the result of an image selected from gallery.

```kotlin
data class PhotoResult(
    val uri: String,
    val width: Int,
    val height: Int,
    val fileName: String? = null,
    val fileSize: Long? = null, 
    val mimeType: String? = null,
    val exif: ExifData? = null 
)
```

### ImagePickerConfig

Main configuration for the image picker.

```kotlin
data class ImagePickerConfig(
    val onPhotoCaptured: (CameraPhotoHandler.PhotoResult) -> Unit,
    val onPhotosSelected: ((List<GalleryPhotoHandler.PhotoResult>) -> Unit)? = null,
    val onError: (Exception) -> Unit,
    val onDismiss: () -> Unit = {},
    val dialogTitle: String = "Select option",
    val takePhotoText: String = "Take photo",
    val selectFromGalleryText: String = "Select from gallery",
    val cancelText: String = "Cancel",
    val customPickerDialog: (@Composable (
        onTakePhoto: () -> Unit,
        onSelectFromGallery: () -> Unit,
        onCancel: () -> Unit
    ) -> Unit)? = null,
    val cameraCaptureConfig: CameraCaptureConfig = CameraCaptureConfig()
)
```

### CameraCaptureConfig

Configuration for camera capture.

```kotlin
data class CameraCaptureConfig(
    val preference: CapturePhotoPreference = CapturePhotoPreference.QUALITY,
    val captureButtonSize: Dp = 72.dp,
    val compressionLevel: CompressionLevel? = null, 
    val includeExif: Boolean = false, 
    val uiConfig: UiConfig = UiConfig(),
    val cameraCallbacks: CameraCallbacks = CameraCallbacks(),
    val permissionAndConfirmationConfig: PermissionAndConfirmationConfig = PermissionAndConfirmationConfig(),
    val galleryConfig: GalleryConfig = GalleryConfig()
)
```

**Parameters:**
- `preference` - Photo capture quality preference
- `captureButtonSize` - Size of the capture button
- `compressionLevel` - Automatic image compression level (null = disabled, MEDIUM = recommended)
- `includeExif` - **NEW**: Extract EXIF metadata including GPS, camera model, timestamps (Android/iOS only)
- `uiConfig` - UI customization configuration
- `cameraCallbacks` - Camera lifecycle callbacks
- `permissionAndConfirmationConfig` - Permission and confirmation dialogs
- `galleryConfig` - Gallery selection configuration

**Image Compression Examples:**

```kotlin
CameraCaptureConfig()

CameraCaptureConfig(
    compressionLevel = CompressionLevel.MEDIUM
)

CameraCaptureConfig(
    compressionLevel = CompressionLevel.HIGH
)

CameraCaptureConfig(
    compressionLevel = CompressionLevel.LOW
)
```

### PermissionAndConfirmationConfig

Configuration for permissions and confirmation.

```kotlin
data class PermissionAndConfirmationConfig(
    val customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    val customConfirmationView: (@Composable (CameraPhotoHandler.PhotoResult, (CameraPhotoHandler.PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    val customDeniedDialog: (@Composable ((onRetry: () -> Unit) -> Unit))? = null,
    val customSettingsDialog: (@Composable ((onOpenSettings: () -> Unit) -> Unit))? = null,
    val skipConfirmation: Boolean = false
)
```

#### Parameters

- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Custom permission handler for text-based customization
- `customConfirmationView: (@Composable (...) -> Unit)?` - Custom composable for photo confirmation
- `customDeniedDialog: (@Composable ((onRetry: () -> Unit) -> Unit))?` - Custom composable dialog when permission is denied
- `customSettingsDialog: (@Composable ((onOpenSettings: () -> Unit) -> Unit))?` - Custom composable dialog for opening settings
- `skipConfirmation: Boolean` - If true, automatically confirms the photo without showing confirmation screen (Android only)

### UiConfig

Configuration for user interface styling.

```kotlin
data class UiConfig(
    val buttonColor: Color? = null,
    val iconColor: Color? = null,
    val buttonSize: Dp? = null,
    val flashIcon: ImageVector? = null,
    val switchCameraIcon: ImageVector? = null,
    val galleryIcon: ImageVector? = null
)
```

### CameraCallbacks

Configuration for camera callbacks.

```kotlin
data class CameraCallbacks(
    val onCameraReady: (() -> Unit)? = null,
    val onCameraSwitch: (() -> Unit)? = null,
    val onPermissionError: ((Exception) -> Unit)? = null,
    val onGalleryOpened: (() -> Unit)? = null
)
```

### ExifData

Contains comprehensive EXIF metadata extracted from images. **Available on Android and iOS only.**

```kotlin
data class ExifData(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val altitude: Double? = null,
    
    val dateTaken: String? = null,
    val dateTime: String? = null,
    val digitizedTime: String? = null,
    val originalTime: String? = null,
    
    val cameraModel: String? = null,
    val cameraManufacturer: String? = null,
    val software: String? = null,
    val owner: String? = null,
    
    val orientation: String? = null,
    val colorSpace: String? = null,
    val whiteBalance: String? = null,
    val flash: String? = null,
    val focalLength: String? = null,
    val aperture: String? = null,
    val shutterSpeed: String? = null,
    val iso: String? = null,
    val imageWidth: Int? = null,
    val imageHeight: Int? = null
)
```

**Example Usage:**

```kotlin
ImagePickerLauncher(
    config = ImagePickerConfig(
        onPhotoCaptured = { result ->
            result.exif?.let { exif ->
                println(" GPS: ${exif.latitude}, ${exif.longitude}")
                println(" Camera: ${exif.cameraModel}")
                println(" Date: ${exif.dateTaken}")
                println(" Settings: ISO ${exif.iso}, f/${exif.aperture}")
            }
        },
        cameraCaptureConfig = CameraCaptureConfig(
            includeExif = true 
        )
    )
)

GalleryPickerLauncher(
    onPhotosSelected = { results ->
        results.forEachIndexed { index, result ->
            println("Image $index:")
            result.exif?.let { exif ->
                println("   Location: ${exif.latitude}, ${exif.longitude}")
                println("   Camera: ${exif.cameraModel}")
                println("   Date: ${exif.dateTaken}")
            } ?: println("   No EXIF data available")
        }
    },
    allowMultiple = true,
    includeExif = true 
)
```

**Platform Support:**
- ✅ **Android**: Full support via `androidx.exifinterface`
- ✅ **iOS**: Full support via native ImageIO framework
- ❌ **Desktop/Web/Wasm**: Not supported (returns null)

### GalleryConfig

Configuration for image gallery.

```kotlin
data class GalleryConfig(
    val allowMultiple: Boolean = false,
    val mimeTypes: List<String> = listOf("image/*"),
    val selectionLimit: Int = 30
)
```

### CameraPreviewConfig

Configuration for camera preview and callbacks.

```kotlin
data class CameraPreviewConfig(
    val captureButtonSize: Dp = 72.dp,
    val uiConfig: UiConfig = UiConfig(),
    val cameraCallbacks: CameraCallbacks = CameraCallbacks()
)
```

#### Properties

- `captureButtonSize: Dp` - Size of capture button (default 72.dp)
- `uiConfig: UiConfig` - User interface configuration
- `cameraCallbacks: CameraCallbacks` - Camera callbacks

#### Example

```kotlin
val cameraPreviewConfig = CameraPreviewConfig(
    captureButtonSize = 80.dp,
    uiConfig = UiConfig(
        buttonColor = Color.Blue,
        iconColor = Color.White
    ),
    cameraCallbacks = CameraCallbacks(
        onCameraReady = { println("Camera ready") },
        onCameraSwitch = { println("Camera switched") }
    )
)
```

### CameraPermissionDialogConfig

Configuration for camera permission dialogs.

```kotlin
data class CameraPermissionDialogConfig(
    val titleDialogConfig: String,
    val descriptionDialogConfig: String,
    val btnDialogConfig: String,
    val titleDialogDenied: String,
    val descriptionDialogDenied: String,
    val btnDialogDenied: String,
    val customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)? = null,
    val customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)? = null
)
```

#### Properties

- `titleDialogConfig: String` - Title for configuration dialog
- `descriptionDialogConfig: String` - Description for configuration dialog
- `btnDialogConfig: String` - Button text for configuration dialog
- `titleDialogDenied: String` - Title for denial dialog
- `descriptionDialogDenied: String` - Description for denial dialog
- `btnDialogDenied: String` - Button text for denial dialog
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Custom dialog for retry
- `customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?` - Custom dialog for settings

#### Example

```kotlin
val dialogConfig = CameraPermissionDialogConfig(
    titleDialogConfig = "Camera permission required",
    descriptionDialogConfig = "Camera permission is required to capture photos. Please grant it in settings",
    btnDialogConfig = "Open settings",
    titleDialogDenied = "Camera permission denied",
    descriptionDialogDenied = "Camera permission is required to capture photos. Please grant the permissions",
    btnDialogDenied = "Grant permission"
)
```

### PermissionConfig

Configuration for permission dialogs.

```kotlin
data class PermissionConfig(
    val titleDialogConfig: String = "Camera permission required",
    val descriptionDialogConfig: String = "Camera permission is required to capture photos. Please grant it in settings",
    val btnDialogConfig: String = "Open settings",
    val titleDialogDenied: String = "Camera permission denied",
    val descriptionDialogDenied: String = "Camera permission is required to capture photos. Please grant the permissions",
    val btnDialogDenied: String = "Grant permission"
)
```

---

## Utilities

### ImagePickerLogger

Interface for logging messages within the ImagePicker library.

```kotlin
interface ImagePickerLogger {
    fun log(message: String)
}
```

#### Default implementation

```kotlin
object DefaultLogger : ImagePickerLogger {
    override fun log(message: String) {
        println(message)
    }
}
```

#### Usage example

```kotlin
class CustomLogger : ImagePickerLogger {
    override fun log(message: String) {
        Log.d("ImagePicker", message)
    }
}
```

### ImagePickerUiConstants

Constants for ImagePicker user interface.

```kotlin
object ImagePickerUiConstants {
    const val ORIENTATION_ROTATE_90 = 90f
    const val ORIENTATION_ROTATE_180 = 180f
    const val ORIENTATION_ROTATE_270 = 270f
    const val ORIENTATION_FLIP_HORIZONTAL_X = -1f
    const val ORIENTATION_FLIP_HORIZONTAL_Y = 1f
    const val ORIENTATION_FLIP_VERTICAL_X = 1f
    const val ORIENTATION_FLIP_VERTICAL_Y = -1f
    const val SYSTEM_VERSION_10 = 10.0
    const val DELAY_TO_TAKE_PHOTO = 60L
    const val SELECTION_LIMIT = 30L
}
```

### RequestCameraPermission

Composable for handling camera permissions.

```kotlin
@Composable
expect fun RequestCameraPermission(
    dialogConfig: CameraPermissionDialogConfig,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parameters

- `dialogConfig: CameraPermissionDialogConfig` - Permission dialog configuration
- `onPermissionPermanentlyDenied: () -> Unit` - Callback when permission is permanently denied
- `onResult: (Boolean) -> Unit` - Callback with permission result
- `customPermissionHandler: (() -> Unit)?` - Custom permission handler

#### Example

```kotlin
@Composable
fun CustomPermissionHandler() {
    val dialogConfig = CameraPermissionDialogConfig(
        titleDialogConfig = "Camera permission required",
        descriptionDialogConfig = "Please enable camera access in settings",
        btnDialogConfig = "Open settings",
        titleDialogDenied = "Permission denied",
        descriptionDialogDenied = "Camera permission is required",
        btnDialogDenied = "Grant permission"
    )
    
    RequestCameraPermission(
        dialogConfig = dialogConfig,
        onPermissionPermanentlyDenied = {
            println("Permission permanently denied")
        },
        onResult = { granted ->
            println("Permission granted: $granted")
        },
        customPermissionHandler = null
    )
}
```

---

## Exceptions

### PhotoCaptureException

Exception thrown when an error occurs during photo capture or processing.

```kotlin
class PhotoCaptureException(message: String) : Exception(message)
```

#### Description

Used to signal camera failures or image processing in the ImagePicker library.

#### Usage example

```kotlin
@Composable
fun ErrorHandlingExample() {
    ImagePickerLauncher(
        config = ImagePickerConfig(
            onPhotoCaptured = { result -> /* ... */ },
            onError = { exception ->
                when (exception) {
                    is PhotoCaptureException -> {
                        println("Photo capture error: ${exception.message}")
                    }
                    else -> {
                        println("Unknown error: ${exception.message}")
                    }
                }
            }
        )
    )
}
```

#### Usage example

```kotlin
try {
    capturePhoto()
} catch (e: PhotoCaptureException) {
    println("Error capturing photo: ${e.message}")
}
```

### ImagePickerException

Base exception for ImagePicker library errors.

```kotlin
open class ImagePickerException(message: String) : Exception(message)
```

### PermissionDeniedException

Exception thrown when permissions are denied.

```kotlin
class PermissionDeniedException(message: String) : ImagePickerException(message)
```

#### Usage example

```kotlin
try {
    requestCameraPermission()
} catch (e: PermissionDeniedException) {
    println("Permissions denied: ${e.message}")
}
```

---

## Enums

### CompressionLevel

Represents different compression levels for image processing.

```kotlin
enum class CompressionLevel {
    LOW,    // Low compression - maintains high quality but larger file size (95% quality, 2560px max)
    MEDIUM, // Medium compression - balanced quality and file size (75% quality, 1920px max)
    HIGH    // High compression - smaller file size but lower quality (50% quality, 1280px max)
}
```

**Quality Mapping:**
- `LOW`: 95% quality, maximum dimension 2560px - Best for high-quality sharing
- `MEDIUM`: 75% quality, maximum dimension 1920px - Recommended for most use cases
- `HIGH`: 50% quality, maximum dimension 1280px - Best for storage optimization

### CapturePhotoPreference

Represents photo capture preferences.

```kotlin
enum class CapturePhotoPreference {
    FAST,    // Fast capture with lower quality
    BALANCED, // Balance between speed and quality
    QUALITY   // Maximum quality (slower)
}
```

### StringResource

String resources used in the library.

```kotlin
enum class StringResource {
    CAMERA_PERMISSION_REQUIRED,
    CAMERA_PERMISSION_DESCRIPTION,
    OPEN_SETTINGS,
    CAMERA_PERMISSION_DENIED,
    CAMERA_PERMISSION_DENIED_DESCRIPTION,
    GRANT_PERMISSION,
    CAMERA_PERMISSION_PERMANENTLY_DENIED,
    IMAGE_CONFIRMATION_TITLE,
    ACCEPT_BUTTON,
    RETRY_BUTTON,
    SELECT_OPTION_DIALOG_TITLE,
    TAKE_PHOTO_OPTION,
    SELECT_FROM_GALLERY_OPTION,
    CANCEL_OPTION,
    PREVIEW_IMAGE_DESCRIPTION,
    HD_QUALITY_DESCRIPTION,
    SD_QUALITY_DESCRIPTION,
    INVALID_CONTEXT_ERROR,
    PHOTO_CAPTURE_ERROR,
    GALLERY_SELECTION_ERROR,
    PERMISSION_ERROR,
    GALLERY_PERMISSION_REQUIRED,
    GALLERY_PERMISSION_DESCRIPTION,
    GALLERY_PERMISSION_DENIED,
    GALLERY_PERMISSION_DENIED_DESCRIPTION,
    GALLERY_GRANT_PERMISSION,
    GALLERY_BTN_SETTINGS
}
```

---

## Exceptions

### ImagePickerException

Represents a specific exception from ImagePickerKMP.

```kotlin
class ImagePickerException(message: String) : Exception(message)
```

---

## Main Configuration

### ImagePickerConfig

Main configuration class for launching the image picker.

```kotlin
data class ImagePickerConfig(
    val onPhotoCaptured: (CameraPhotoHandler.PhotoResult) -> Unit,
    val onPhotosSelected: ((List<GalleryPhotoHandler.PhotoResult>) -> Unit)? = null,
    val onError: (Exception) -> Unit,
    val onDismiss: () -> Unit = {},
    val dialogTitle: String = "Select option",
    val takePhotoText: String = "Take photo",
    val selectFromGalleryText: String = "Select from gallery",
    val cancelText: String = "Cancel",
    val customPickerDialog: (
        @Composable (
            onTakePhoto: () -> Unit,
            onSelectFromGallery: () -> Unit,
            onCancel: () -> Unit
        ) -> Unit
    )? = null,
    val cameraCaptureConfig: CameraCaptureConfig = CameraCaptureConfig()
)
```

#### Properties

- `onPhotoCaptured: (CameraPhotoHandler.PhotoResult) -> Unit` - Callback when a photo is captured
- `onPhotosSelected: ((List<GalleryPhotoHandler.PhotoResult>) -> Unit)?` - Callback for multiple gallery selection
- `onError: (Exception) -> Unit` - Callback when an error occurs
- `onDismiss: () -> Unit` - Callback when picker is closed
- `dialogTitle: String` - Selection dialog title
- `takePhotoText: String` - Text for camera option
- `selectFromGalleryText: String` - Text for gallery option
- `cancelText: String` - Text for cancel
- `customPickerDialog: (@Composable (...) -> Unit)?` - Custom dialog for iOS
- `cameraCaptureConfig: CameraCaptureConfig` - Camera capture configuration

#### Example

```kotlin
val config = ImagePickerConfig(
    onPhotoCaptured = { result ->
        println("Photo captured: ${result.uri}")
    },
    onPhotosSelected = { results ->
        println("${results.size} photos selected")
    },
    onError = { exception ->
        println("Error: ${exception.message}")
    },
    onDismiss = {
        println("Picker closed")
    },
    dialogTitle = "Select option", // Title for the dialog for iOS
    takePhotoText = "Take photo", // Text for the camera option for iOS
    selectFromGalleryText = "Select from gallery", // Text for the gallery option for iOS
    cancelText = "Cancel", // Text for cancel for iOS
     // Custom dialog for iOS
    customPickerDialog = { onTakePhoto, onSelectFromGallery, onCancel ->
        Column {
            Button(onClick = onTakePhoto) {
                Text("Camera")
            }
            Button(onClick = onSelectFromGallery) {
                Text("Gallery")
            }
            Button(onClick = onCancel) {
                Text("Cancel")
            }
        }
    },
    cameraCaptureConfig = CameraCaptureConfig(
        preference = CapturePhotoPreference.HIGH_QUALITY,
        permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
            customConfirmationView = { photoResult, onConfirm, onRetry ->
                // Custom confirmation view
            }
        )
    )
)
```

### ImagePickerLauncher

The main composable function for launching the image picker.

```kotlin
@Composable
fun ImagePickerLauncher(
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit = {},
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parameters

- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback when a photo is captured
- `onError: (Exception) -> Unit` - Callback when an error occurs
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Custom permission handling
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Custom confirmation view
- `preference: CapturePhotoPreference?` - Photo capture preferences

