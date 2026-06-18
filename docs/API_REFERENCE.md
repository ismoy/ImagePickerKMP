This document is also available in Spanish: [API_REFERENCE.es.md](API_REFERENCE.es.md)

# API Reference

Complete API documentation for the ImagePickerKMP library.

## Table of Contents

- [rememberImagePickerKMP](#rememberimagePickerkmp)
- [Main Components](#main-components)
- [Data Classes](#data-classes)
- [Enums](#enums)
- [Configuration](#configuration)
- [Extension Functions](#extension-functions)
- [Platform-specific APIs](#platform-specific-apis)

---

## rememberImagePickerKMP {#rememberimagePickerkmp}

> **Available since:** `1.0.35-alpha1` · All platforms

`rememberImagePickerKMP` is the entry point for Compose. It returns an `ImagePickerKMPState` — a stable state holder that replaces manual `showCamera`/`showGallery` booleans. **No `Render()` or additional composable is required** — the picker self-manages when you call `launchCamera()` or `launchGallery()`.

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
| `result: ImagePickerResult` | Reactive state. Starts at `Idle`. Observe with `when`. Updates automatically when the picker opens, the user selects or cancels. |
| `launchCamera(cameraCaptureConfig?, enableCrop?, onDismiss?, onError?)` | Opens the camera. All parameters are optional and override the global config only for this launch. |
| `launchGallery(allowMultiple?, mimeTypes?, selectionLimit?, enableCrop?, includeExif?, redactGpsData?, mimeTypeMismatchMessage?, cameraCaptureConfig?, onDismiss?, onError?)` | Opens the gallery. All parameters are optional and override the global `GalleryConfig` only for this launch. |
| `reset()` | Resets `result` to `Idle` and closes any active picker. |

> ⚠️ **There is no `Render()` or `launchPicker()`** in this API. The picker is managed internally.

### ImagePickerResult

```kotlin
sealed class ImagePickerResult {
    data object Idle        : ImagePickerResult()   // Initial state / after reset()
    data object Loading     : ImagePickerResult()   // Picker open, waiting for action
    data class  Success(val photos: List<PhotoResult>) : ImagePickerResult()
    data object Dismissed   : ImagePickerResult()   // User closed without selecting
    data class  Error(val exception: Exception) : ImagePickerResult()
}
```

`Success` also exposes `val first: PhotoResult?` — first photo (useful for camera capture).

### Full Example

```kotlin
@Composable
fun MyScreen(innerPadding: PaddingValues) {

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
                Text("Camera")
            }
            Button(onClick = { picker.launchGallery() }, modifier = Modifier.weight(1f)) {
                Text("Gallery")
            }
        }
    }
}
```

### Per-Launch Override

```kotlin
// Override only for this button — does not affect the global config
Button(onClick = {
    picker.launchGallery(
        allowMultiple = true,
        mimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG),
        selectionLimit = 5,
        includeExif = true
    )
}) { Text("Pick up to 5 images") }

// Camera with HIGH compression and crop, only for this tap
Button(onClick = {
    picker.launchCamera(
        cameraCaptureConfig = CameraCaptureConfig(compressionLevel = CompressionLevel.HIGH),
        enableCrop = true
    )
}) { Text("Camera HD") }
```

---

## Photo Capture – Specific Documentation

### Description

The photo capture functionality in ImagePickerKMP allows developers to integrate a modern, customizable, and cross-platform camera experience into their applications. It includes flash control, camera switching, preview, confirmation, and complete UI customization.

---

### Basic photo capture example

```kotlin
val picker = rememberImagePickerKMP()

Button(onClick = { picker.launchCamera() }) { Text("Camera") }

when (val result = picker.result) {
    is ImagePickerResult.Success -> {
        result.first?.let { photo ->
            println("Photo captured: ${photo.uri}")
        }
    }
    is ImagePickerResult.Error -> println("Error: ${result.exception.message}")
    is ImagePickerResult.Dismissed -> println("User cancelled")
    else -> {}
}
```

---

### Advanced example: Confirmation customization (Android only)

```kotlin
val picker = rememberImagePickerKMP(
    config = ImagePickerKMPConfig(
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

Button(onClick = { picker.launchCamera() }) { Text("Camera") }
```

### Example: Skip confirmation screen (Android only)

```kotlin
val picker = rememberImagePickerKMP(
    config = ImagePickerKMPConfig(
        cameraCaptureConfig = CameraCaptureConfig(
            preference = CapturePhotoPreference.QUALITY,
            permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                skipConfirmation = true
            )
        )
    )
)

Button(onClick = { picker.launchCamera() }) { Text("Camera (no confirmation)") }
```

---

### User experience
- **Preview**: User sees the camera in real-time.
- **Flash control**: Button to toggle between Auto, On, Off (visual icons).
- **Camera switching**: Button to toggle between rear and front camera.
- **Capture**: Central button to take the photo.
- **Confirmation**: Modern view to accept or retry the photo, with customizable texts and icons.

---

### Notes and recommendations
- The permission system is managed automatically.
- You can completely customize the confirmation UI.
- Default texts are in English, but you can easily localize them.
- Flash only works in `BALANCED` or `QUALITY` quality modes.
- If you need even more control, implement your own `customConfirmationView`.

---

### Code references in the project
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/CameraCapturePreview.kt**: Preview logic and camera controls.
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/ImageConfirmationViewWithCustomButtons.kt**: Photo confirmation UI.
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/CameraController.kt**: Camera control logic, flash and camera switching.
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/CameraXManager.kt**: Abstraction for camera management.

---

## Gallery Image Selection – Specific Documentation

### Description

The gallery functionality in ImagePickerKMP allows developers to integrate a modern and customizable experience for selecting images from the device gallery. It supports single or multiple selection, file type filters, and custom confirmation.

---

### Basic image selection example

```kotlin
val picker = rememberImagePickerKMP()

Button(onClick = { picker.launchGallery() }) { Text("Gallery") }

when (val result = picker.result) {
    is ImagePickerResult.Success -> {
        println("Selected images: ${result.photos}")
    }
    is ImagePickerResult.Error -> println("Error: ${result.exception.message}")
    is ImagePickerResult.Dismissed -> println("User cancelled selection")
    else -> {}
}
```

---

### Advanced example: Multiple selection with filters

```kotlin
val picker = rememberImagePickerKMP(
    config = ImagePickerKMPConfig(
        galleryConfig = GalleryConfig(
            allowMultiple = true,
            mimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG),
            selectionLimit = 30
        )
    )
)

Button(onClick = { picker.launchGallery() }) { Text("Gallery") }

// Or with per-launch override:
Button(onClick = {
    picker.launchGallery(
        allowMultiple = true,
        mimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG),
        selectionLimit = 5
    )
}) { Text("Gallery (5 max)") }
```

---

### Relevant parameters (via `GalleryConfig` or per-launch overrides)

- **allowMultiple**: Allows selecting multiple images (default: `false`)
- **mimeTypes**: List of allowed MIME types (default: `listOf("image/*")`)
- **selectionLimit**: Maximum selection limit (default: `30`)
- **includeExif**: Extract EXIF metadata from selected images
- **redactGpsData**: Strip GPS coordinates from EXIF before delivery

---

### User experience
- **Gallery selector**: User can choose one image or several (if `allowMultiple` is enabled)
- **Filters**: File type filters can be applied using `mimeTypes`
- **Limits**: A maximum selection limit can be set with `selectionLimit`

---

### Notes and recommendations
- The permission system is managed automatically
- Multiple selection is supported on both platforms (Android and iOS)
- MIME types allow filtering by specific image formats
- Selection limit helps control application performance

---

## Image Compression – Specific Documentation

### Description

The image compression functionality in ImagePickerKMP automatically optimizes image size while maintaining acceptable quality. It works for both camera capture and gallery selection, with configurable compression levels and async processing.

---

### Features
- **Automatic compression**: Applies compression transparently during image processing
- **Configurable levels**: LOW, MEDIUM, HIGH compression options
- **Multi-format support**: JPEG, PNG, HEIC, HEIF, WebP, GIF, BMP
- **Async processing**: Non-blocking UI with Kotlin Coroutines
- **Smart optimization**: Combines dimension scaling + quality compression
- **Memory efficient**: Proper bitmap recycling and cleanup

---

### Compression Levels

| Level | Quality | Max Dimension | Use Case |
|-------|---------|---------------|----------|
| LOW | 95% | 2560px | High-quality sharing, professional use |
| MEDIUM | 75% | 1920px | **Recommended** - Social media, general use |
| HIGH | 50% | 1280px | Storage optimization, thumbnails |

---

### Camera with Compression

```kotlin
val picker = rememberImagePickerKMP(
    config = ImagePickerKMPConfig(
        cameraCaptureConfig = CameraCaptureConfig(
            compressionLevel = CompressionLevel.MEDIUM
        )
    )
)

Button(onClick = { picker.launchCamera() }) { Text("Camera") }

when (val result = picker.result) {
    is ImagePickerResult.Success -> {
        result.first?.let { photo ->
            val fileSizeKB = (photo.fileSize ?: 0) / 1024.0
            println("Compressed image size: ${String.format("%.2f", fileSizeKB)}KB")
            println("Exact size: ${photo.fileSize} bytes")
        }
    }
    else -> {}
}
```

---

### Gallery with Compression

```kotlin
val picker = rememberImagePickerKMP(
    config = ImagePickerKMPConfig(
        galleryConfig = GalleryConfig(
            allowMultiple = true,
            mimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG)
        ),
        cameraCaptureConfig = CameraCaptureConfig(
            compressionLevel = CompressionLevel.HIGH
        )
    )
)

Button(onClick = { picker.launchGallery() }) { Text("Gallery") }

when (val result = picker.result) {
    is ImagePickerResult.Success -> {
        result.photos.forEach { photo ->
            val fileSizeKB = (photo.fileSize ?: 0) / 1024.0
            println("Original: ${photo.fileName}")
            println("Compressed size: ${String.format("%.2f", fileSizeKB)}KB")
            println("Exact size: ${photo.fileSize} bytes")
        }
    }
    else -> {}
}
```

---

### Compression Process

1. **Image Loading**: Original image is loaded from camera/gallery
2. **Dimension Scaling**: Image is resized if larger than max dimension
3. **Quality Compression**: JPEG compression is applied based on level
4. **Temporary File**: Compressed image is saved to app cache
5. **Result Delivery**: New URI with compressed image is returned

---

### Platform Support

| Platform | Camera Compression | Gallery Compression | Async Processing |
|----------|-------------------|---------------------|------------------|
| Android | ✅ | ✅ | ✅ Coroutines |
| iOS | ✅ | ✅ | ✅ Coroutines |

---

### Performance Considerations

- **Memory Usage**: Original bitmaps are recycled after compression
- **Processing Time**: Runs on background threads (Dispatchers.IO)
- **Storage**: Compressed images are stored in app cache directory
- **Quality**: Smart balance between file size and visual quality

---

### Code References
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/data/processors/ImageProcessor.kt**: Camera compression logic
- **library/src/commonMain/kotlin/io/github/ismoy/imagepickerkmp/domain/models/CompressionLevel.kt**: Compression level definitions

---

## Main Components

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

#### Smart Picker Selection (Android)

The gallery picker automatically chooses the appropriate picker based on requested MIME types:

- **Images only** (`image/*`): Opens native Android gallery using `MediaStore`
- **PDFs included** (`application/pdf`): Opens file explorer for document access
- **Mixed types**: Uses file explorer for maximum compatibility

This ensures users get the expected interface for their content type without configuration.

#### Automatic Detection Behavior

| Detected MIME Types | `forceGalleryOnly` | Result |
|--------------------|--------------------|--------|
| Images only (`image/*`) | `true` | Native gallery |
| Contains `application/pdf` | `false` | File explorer |
| Mixed types (image + others) | `false` | File explorer |
| Non-image types | `false` | File explorer |

#### Configuration Example

```kotlin
val picker = rememberImagePickerKMP(
    config = ImagePickerKMPConfig(
        galleryConfig = GalleryConfig(
            mimeTypes = listOf(MimeType.APPLICATION_PDF),
            includeExif = true
        )
    )
)

Button(onClick = { picker.launchGallery() }) { Text("Select PDF") }
```

---

## Data Classes

### PhotoResult

Represents the result of a photo capture from camera or gallery selection.

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

#### PhotoResult Extensions

**File Path Access**

```kotlin
// Convert to kotlinx.io Path for cross-platform file operations (v1.0.38+)
val path: Path? = photoResult.toPath()

// Get absolute file system path as String (v1.0.40+)
val absolutePath: String? = photoResult.absolutePath
```

**Extensions:**
- `fun PhotoResult.toPath(): Path?` — *(Available since v1.0.38)* Converts the photo's URI to a `kotlinx.io.files.Path` for cross-platform file operations. Returns `null` if conversion fails. Requires `kotlinx-io` dependency.
- `val PhotoResult.absolutePath: String?` — *(Available since v1.0.40)* Returns the absolute file system path as a String. Platform-specific implementation (Android uses ContentResolver, iOS uses URL.path, Desktop/Web use direct path extraction).

**Example usage:**

```kotlin
val picker = rememberImagePickerKMP()

when (val result = picker.result) {
    is ImagePickerResult.Success -> {
        result.first?.let { photo ->
            // Using kotlinx.io Path (cross-platform, v1.0.38+)
            photo.toPath()?.let { path ->
                println("File path: $path")
            }
            
            // Using absolute path String (platform-specific, v1.0.40+)
            photo.absolutePath?.let { path ->
                println("Absolute path: $path")
            }
        }
    }
    else -> {}
}
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
val picker = rememberImagePickerKMP(
    config = ImagePickerKMPConfig(
        cameraCaptureConfig = CameraCaptureConfig(includeExif = true)
    )
)

when (val result = picker.result) {
    is ImagePickerResult.Success -> {
        result.first?.exif?.let { exif ->
            println("GPS: ${exif.latitude}, ${exif.longitude}")
            println("Camera: ${exif.cameraModel}")
            println("Date: ${exif.dateTaken}")
            println("Settings: ISO ${exif.iso}, f/${exif.aperture}")
        }
    }
    else -> {}
}
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
    val selectionLimit: Int = 30,
    val includeExif: Boolean = false,
    val redactGpsData: Boolean = true
)
```

---

## Configuration

### CameraCaptureConfig

Configuration for camera capture.

```kotlin
data class CameraCaptureConfig(
    val preference: CapturePhotoPreference = CapturePhotoPreference.BALANCED,
    val captureButtonSize: Dp = 72.dp,
    val compressionLevel: CompressionLevel? = CompressionLevel.MEDIUM,
    val includeExif: Boolean = false,
    val redactGpsData: Boolean = true,
    val uiConfig: UiConfig = UiConfig(),
    val cameraCallbacks: CameraCallbacks = CameraCallbacks(),
    val permissionAndConfirmationConfig: PermissionAndConfirmationConfig = PermissionAndConfirmationConfig(),
    val cropConfig: CropConfig = CropConfig(),
    val cameraScaleType: CameraScaleType = CameraScaleType.FILL_CENTER
)
```

**Parameters:**
- `preference` - Photo capture quality preference
- `captureButtonSize` - Size of the capture button
- `compressionLevel` - Automatic image compression level (`null` = disabled, `MEDIUM` = recommended)
- `includeExif` - Extract EXIF metadata including GPS, camera model, timestamps (Android/iOS only)
- `redactGpsData` - Strip GPS coordinates from EXIF before delivery (default `true`). Effective only when `includeExif = true`
- `uiConfig` - UI customization configuration
- `cameraCallbacks` - Camera lifecycle callbacks
- `permissionAndConfirmationConfig` - Permission and confirmation dialogs
- `cropConfig` - Interactive crop UI configuration
- `cameraScaleType` - How the camera preview is scaled inside its viewport (Android only). Defaults to `CameraScaleType.FILL_CENTER`

**Camera scale type examples:**

```kotlin
// Default — fills the viewport, crops the feed to fit
CameraCaptureConfig()

// Letterbox — full camera feed visible, matches captured image framing
CameraCaptureConfig(
    cameraScaleType = CameraScaleType.FIT_CENTER
)

// Fill from top-left
CameraCaptureConfig(
    cameraScaleType = CameraScaleType.FILL_START
)
```

### PermissionAndConfirmationConfig

Configuration for permissions and post-capture confirmation screen.

```kotlin
data class PermissionAndConfirmationConfig(
    val customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    val customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    val customDeniedDialog: (@Composable (onRetry: () -> Unit, onDismiss: () -> Unit) -> Unit)? = null,
    val customSettingsDialog: (@Composable (onOpenSettings: () -> Unit, onDismiss: () -> Unit) -> Unit)? = null,
    val skipConfirmation: Boolean = false,
    val cancelButtonTextIOS: String? = "Cancel",
    val onCancelPermissionConfigIOS: (() -> Unit)? = null,
    val confirmationImageContentScale: ContentScale = ContentScale.Crop
)
```

#### Parameters

- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Custom permission handler for text-based customization
- `customConfirmationView: (@Composable (...) -> Unit)?` - Custom composable for photo confirmation
- `customDeniedDialog: (@Composable (...) -> Unit)?` - Custom composable dialog when permission is denied. Always call `onRetry` or `onDismiss` on user interaction
- `customSettingsDialog: (@Composable (...) -> Unit)?` - Custom composable dialog for opening system settings. Always call `onOpenSettings` or `onDismiss` on user interaction
- `skipConfirmation: Boolean` - If `true`, delivers the captured photo directly without showing the confirmation screen (Android)
- `cancelButtonTextIOS: String?` - Text label for the cancel button in the iOS permission alert. Defaults to `"Cancel"`
- `onCancelPermissionConfigIOS: (() -> Unit)?` - Callback invoked when the user taps cancel in the iOS permission alert
- `confirmationImageContentScale: ContentScale` - How the captured photo is scaled in the post-capture confirmation preview. Accepts any Compose `ContentScale` value. Defaults to `ContentScale.Crop`

**Confirmation image scale examples:**

```kotlin
// Default — crop to fill the preview area
PermissionAndConfirmationConfig()

// Fit — show the entire photo letterboxed
PermissionAndConfirmationConfig(
    confirmationImageContentScale = ContentScale.Fit
)

// Fill width
PermissionAndConfirmationConfig(
    confirmationImageContentScale = ContentScale.FillWidth
)
```

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

### CropConfig

Configuration for interactive crop UI.

```kotlin
data class CropConfig(
    // Crop shape, aspect ratio, freeform options
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

## Enums

### CameraScaleType

Controls how the camera preview is scaled inside its viewport on Android. Mirrors `androidx.camera.view.PreviewView.ScaleType`.

```kotlin
enum class CameraScaleType {
    FILL_CENTER, // Fill viewport, center-crop any overflow (default)
    FILL_START,  // Fill viewport, align to top-left, crop overflow
    FILL_END,    // Fill viewport, align to bottom-right, crop overflow
    FIT_CENTER,  // Letterbox — entire feed visible, centered
    FIT_START,   // Letterbox — entire feed visible, aligned to top-left
    FIT_END      // Letterbox — entire feed visible, aligned to bottom-right
}
```

**Notes:**
- `FILL_*` values crop the camera feed to fill the entire viewport. The visible viewfinder area may be narrower than the captured image.
- `FIT_*` values show the entire camera feed with letterboxing. The viewfinder framing matches the captured image exactly.
- Currently applied on Android only. iOS uses the system camera UI where preview and capture framing already match.
- Used in `CameraCaptureConfig.cameraScaleType`. Defaults to `FILL_CENTER`.

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

### PhotoCaptureException

Exception thrown when an error occurs during photo capture or processing.

```kotlin
class PhotoCaptureException(message: String) : Exception(message)
```

#### Usage example

```kotlin
val picker = rememberImagePickerKMP()

when (val result = picker.result) {
    is ImagePickerResult.Error -> {
        when (result.exception) {
            is PhotoCaptureException -> {
                println("Photo capture error: ${result.exception.message}")
            }
            else -> {
                println("Unknown error: ${result.exception.message}")
            }
        }
    }
    else -> {}
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

---

## Platform-specific APIs

### CameraCaptureView (Android)

For direct Android usage, `CameraCaptureView` is available as a public composable that renders the camera preview with controls. This is used internally by `rememberImagePickerKMP` but can be consumed directly for advanced Android-only integrations.

### Platform Support Summary

| Feature | Android | iOS | Desktop | Web/Wasm |
|---------|---------|-----|---------|----------|
| Camera capture | ✅ | ✅ | ❌ | ❌ |
| Gallery selection | ✅ | ✅ | ✅ | ✅ |
| EXIF metadata | ✅ | ✅ | ❌ | ❌ |
| Image compression | ✅ | ✅ | ❌ | ❌ |
| Crop UI | ✅ | ✅ | ❌ | ❌ |
| Custom confirmation | ✅ | ❌ | ❌ | ❌ |
| Camera scale type | ✅ | ❌ | ❌ | ❌ |
