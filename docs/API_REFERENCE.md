This document is also available in Spanish: [API_REFERENCE.es.md](API_REFERENCE.es.md)

# API Reference

Complete API documentation for the ImagePickerKMP library.

## Table of Contents

- [Main Components](#main-components)
- [Data Classes](#data-classes)
- [Enums](#enums)
- [Configuration](#configuration)
- [Platform-specific APIs](#platform-specific-apis)

## 📸 Photo Capture – Specific Documentation

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

## 🖼️ Gallery Image Selection – Specific Documentation

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

## 🗜️ Image Compression – Specific Documentation

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
            // result.uri contains the compressed image
            val fileSizeKB = (result.fileSize ?: 0) / 1024
            println("Compressed image size: ${fileSizeKB}KB")
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
            val fileSizeKB = (photo.fileSize ?: 0) / 1024
            println("Original: ${photo.fileName}")
            println("Compressed size: ${fileSizeKB}KB")
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

Main composable for launching the image picker.

```kotlin
@Composable
expect fun ImagePickerLauncher(
    config: ImagePickerConfig
)
```

#### Parameters

- `config: ImagePickerConfig` - Complete image picker configuration

---

### GalleryPickerLauncher

Composable for selecting images from gallery.

```kotlin
@Composable
expect fun GalleryPickerLauncher(
    onPhotosSelected: (List<GalleryPhotoHandler.PhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit = {},
    allowMultiple: Boolean = false,
    mimeTypes: List<String> = listOf("image/*"),
    selectionLimit: Long = SELECTION_LIMIT
)
```

#### Parameters

- `onPhotosSelected` - Callback with the list of selected images
- `onError` - Callback to handle errors
- `onDismiss` - Callback when user cancels
- `allowMultiple` - Allows multiple selection (default: `false`)
- `mimeTypes` - List of allowed MIME types
- `selectionLimit` - Maximum selection limit

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

## Data Classes

### CameraPhotoHandler.PhotoResult

Represents the result of a photo capture from camera.

```kotlin
data class PhotoResult(
    val uri: String,
    val width: Int,
    val height: Int,
    val fileName: String? = null,
    val fileSize: Long? = null
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
    val fileSize: Long? = null
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
    val compressionLevel: CompressionLevel? = null, // null = no compression
    val uiConfig: UiConfig = UiConfig(),
    val cameraCallbacks: CameraCallbacks = CameraCallbacks(),
    val permissionAndConfirmationConfig: PermissionAndConfirmationConfig = PermissionAndConfirmationConfig(),
    val galleryConfig: GalleryConfig = GalleryConfig()
)
```

**Parameters:**
- `preference` - Photo capture quality preference
- `captureButtonSize` - Size of the capture button
- `compressionLevel` - **NEW**: Automatic image compression level (null = disabled, MEDIUM = recommended)
- `uiConfig` - UI customization configuration
- `cameraCallbacks` - Camera lifecycle callbacks
- `permissionAndConfirmationConfig` - Permission and confirmation dialogs
- `galleryConfig` - Gallery selection configuration

**Image Compression Examples:**

```kotlin
// No compression (default)
CameraCaptureConfig()

// Medium compression (recommended)
CameraCaptureConfig(
    compressionLevel = CompressionLevel.MEDIUM
)

// High compression for storage optimization
CameraCaptureConfig(
    compressionLevel = CompressionLevel.HIGH
)

// Low compression for maximum quality
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
                        // Show user-friendly error message
                    }
                    else -> {
                        println("Unknown error: ${exception.message}")
                        // Handle generic error
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
    // Photo capture operation
    capturePhoto()
} catch (e: PhotoCaptureException) {
    println("Error capturing photo: ${e.message}")
    // Handle error appropriately
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
    // Request permissions
    requestCameraPermission()
} catch (e: PermissionDeniedException) {
    println("Permissions denied: ${e.message}")
    // Show dialog to go to settings
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

---

## 📄 OCR Text Recognition – Specific Documentation

## Description
The OCR (Optical Character Recognition) functionality in ImagePickerKMP allows developers to extract text from images using both local and cloud-based analysis. It integrates seamlessly with the existing camera and gallery features to provide a complete text extraction solution.

---

## Basic OCR example with local analysis

```kotlin
ImagePickerKMP.scanOCR(
    mode = ScanMode.Local,
    onResult = { result ->
        // Handle the OCR result
        println("Extracted text: ${result.text}")
        println("Lines found: ${result.lines.size}")
    },
    onError = { exception ->
        // Handle errors
        println("OCR Error: ${exception.message}")
    }
)
```

---

## Advanced OCR example with cloud analysis (Gemini API)

```kotlin
ImagePickerKMP.scanOCR(
    mode = ScanMode.Cloud(apiKey = "YOUR_GEMINI_API_KEY"),
    onResult = { result ->
        // Handle advanced OCR result
        println("Text: ${result.text}")
        println("Language: ${result.language}")
        println("Confidence: ${result.confidence}")
        println("Metadata: ${result.metadata}")
    },
    onError = { exception ->
        // Handle errors
        println("Cloud OCR Error: ${exception.message}")
    }
)
```

---

## OCR with Composable UI integration

```kotlin
@Composable
fun OCRExample() {
    var showScanner by remember { mutableStateOf(false) }
    var ocrResult by remember { mutableStateOf<OCRResult?>(null) }
    
    Column {
        Button(onClick = { showScanner = true }) {
            Text("Scan Text")
        }
        
        ocrResult?.let { result ->
            Text("Result: ${result.text}")
        }
    }
    
    if (showScanner) {
        OCRScanner(
            mode = ScanMode.Local,
            onResult = { result ->
                ocrResult = result
                showScanner = false
            },
            onError = { error ->
                showScanner = false
            },
            onDismiss = { showScanner = false }
        )
    }
}
```

---

## Direct OCR analysis from existing image URI

```kotlin
// Analyze existing image
ImagePickerKMP.scanOCRFromUri(
    imageUri = "file://path/to/image.jpg",
    mode = ScanMode.Cloud("API_KEY"),
    onResult = { result ->
        println("Text found: ${result.text}")
    },
    onError = { error ->
        println("Analysis failed: ${error.message}")
    }
)

// Or using suspend function
suspend fun analyzeImage() {
    try {
        val result = scanOCR(
            mode = ScanMode.Local,
            imageUri = "content://..."
        )
        println("OCR Result: ${result.text}")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}
```

---

## OCR Data Classes

### ScanMode

```kotlin
sealed class ScanMode {
    object Local : ScanMode()                    // Offline OCR analysis
    data class Cloud(val apiKey: String) : ScanMode()  // Cloud OCR with Gemini API
}
```

### OCRResult

```kotlin
data class OCRResult(
    val text: String,                        // Complete extracted text
    val lines: List<String>,                 // Individual text lines
    val language: String? = null,            // Detected language (if available)
    val confidence: Float? = null,           // Analysis confidence (0.0 to 1.0)
    val metadata: Map<String, Any>? = null   // Additional analysis data
)
```

---

## Platform Support

| Platform | Local OCR | Cloud OCR | Technology |
|----------|-----------|-----------|------------|
| Android | ✅ ML Kit Text Recognition | ✅ Gemini API | Offline + Online |
| iOS | ✅ VisionKit Text Recognition | ✅ Gemini API | Offline + Online |

---

## OCR Functions

### ImagePickerKMP.scanOCR

Main OCR function that handles camera/gallery integration and text analysis.

```kotlin
suspend fun scanOCR(
    mode: ScanMode,
    onResult: (OCRResult) -> Unit,
    onError: (Throwable) -> Unit
)
```

#### Parameters

- `mode: ScanMode` - Analysis mode (Local or Cloud with API key)
- `onResult: (OCRResult) -> Unit` - Callback with successful OCR result
- `onError: (Throwable) -> Unit` - Callback with error information

### ImagePickerKMP.scanOCRFromUri

Direct OCR analysis from existing image URI.

```kotlin
suspend fun scanOCRFromUri(
    imageUri: String,
    mode: ScanMode,
    onResult: (OCRResult) -> Unit,
    onError: (Throwable) -> Unit
)
```

#### Parameters

- `imageUri: String` - URI of the image to analyze
- `mode: ScanMode` - Analysis mode (Local or Cloud with API key)
- `onResult: (OCRResult) -> Unit` - Callback with successful OCR result
- `onError: (Throwable) -> Unit` - Callback with error information

### OCRScanner Composable

Composable UI component for OCR integration.

```kotlin
@Composable
fun OCRScanner(
    mode: ScanMode,
    onResult: (OCRResult) -> Unit,
    onError: (Throwable) -> Unit,
    onDismiss: () -> Unit = {}
)
```

#### Parameters

- `mode: ScanMode` - Analysis mode (Local or Cloud with API key)
- `onResult: (OCRResult) -> Unit` - Callback with successful OCR result
- `onError: (Throwable) -> Unit` - Callback with error information
- `onDismiss: () -> Unit` - Callback when picker is dismissed

---

## OCR Exceptions

### OCRException

Base exception for OCR-related errors.

```kotlin
open class OCRException(message: String, cause: Throwable? = null) : Exception(message, cause)
```

### LocalOCRException

Exception for local OCR analysis failures.

```kotlin
class LocalOCRException(message: String, cause: Throwable? = null) : OCRException(message, cause)
```

### CloudOCRException

Exception for cloud OCR analysis failures.

```kotlin
class CloudOCRException(message: String, cause: Throwable? = null) : OCRException(message, cause)
```

### InvalidAPIKeyException

Exception for invalid or missing API keys.

```kotlin
class InvalidAPIKeyException(message: String = "Invalid or missing API key") : OCRException(message)
```

---

## Setup Requirements

### For Local OCR

- **Android**: Automatic (ML Kit included in dependencies)
- **iOS**: Automatic (VisionKit is part of the system)

### For Cloud OCR

1. Get a Gemini API key from [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Use it in your application:

```kotlin
val apiKey = "AIzaSyC..." // Your Gemini API key
ImagePickerKMP.scanOCR(ScanMode.Cloud(apiKey)) { result ->
    // Process result
}
```

---

## Code References

- **library/src/commonMain/kotlin/io/github/ismoy/imagepickerkmp/ocr/ImagePickerOCR.kt**: Main OCR API
- **library/src/commonMain/kotlin/io/github/ismoy/imagepickerkmp/ocr/OCRScanner.kt**: Composable UI integration
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/data/analyzers/LocalOCRAnalyzer.kt**: Android ML Kit implementation
- **library/src/iosMain/kotlin/io/github/ismoy/imagepickerkmp/data/analyzers/LocalOCRAnalyzer.kt**: iOS VisionKit implementation
- **library/src/commonMain/kotlin/io/github/ismoy/imagepickerkmp/data/integration/GeminiService.kt**: Gemini API integration