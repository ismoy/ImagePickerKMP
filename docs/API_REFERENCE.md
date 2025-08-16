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
    val height: Int
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
    val uiConfig: UiConfig = UiConfig(),
    val cameraCallbacks: CameraCallbacks = CameraCallbacks(),
    val permissionAndConfirmationConfig: PermissionAndConfirmationConfig = PermissionAndConfirmationConfig(),
    val galleryConfig: GalleryConfig = GalleryConfig()
)
```

### PermissionAndConfirmationConfig

Configuration for permissions and confirmation.

```kotlin
data class PermissionAndConfirmationConfig(
    val customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    val customConfirmationView: (@Composable (CameraPhotoHandler.PhotoResult, (CameraPhotoHandler.PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    val customDeniedDialog: (@Composable ((onRetry: () -> Unit) -> Unit))? = null,
    val customSettingsDialog: (@Composable ((onOpenSettings: () -> Unit) -> Unit))? = null
)
```

#### Parameters

- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Custom permission handler for text-based customization
- `customConfirmationView: (@Composable (...) -> Unit)?` - Custom composable for photo confirmation
- `customDeniedDialog: (@Composable ((onRetry: () -> Unit) -> Unit))?` - Custom composable dialog when permission is denied
- `customSettingsDialog: (@Composable ((onOpenSettings: () -> Unit) -> Unit))?` - Custom composable dialog for opening settings

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