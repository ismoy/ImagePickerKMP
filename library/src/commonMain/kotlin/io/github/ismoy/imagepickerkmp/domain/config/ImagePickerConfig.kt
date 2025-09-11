package io.github.ismoy.imagepickerkmp.domain.config

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult

@Suppress("EndOfSentenceFormat")
/**
 * Configuration for UI styling
 */
data class UiConfig(
    val buttonColor: Color? = null,
    val iconColor: Color? = null,
    val buttonSize: Dp? = null,
    val flashIcon: ImageVector? = null,
    val switchCameraIcon: ImageVector? = null,
    val galleryIcon: ImageVector? = null
)
@Suppress("EndOfSentenceFormat")
/**
 * Configuration for camera callbacks
 */
data class CameraCallbacks(
    val onCameraReady: (() -> Unit)? = null,
    val onCameraSwitch: (() -> Unit)? = null,
    val onPermissionError: ((Exception) -> Unit)? = null,
    val onGalleryOpened: (() -> Unit)? = null
)

@Suppress("EndOfSentenceFormat","MaxLineLength")
/**
 * Configuration for permissions and confirmation
 */
data class PermissionAndConfirmationConfig(
    val customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    val customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    val customDeniedDialog: (@Composable ((onRetry: () -> Unit) -> Unit))? = null,
    val customSettingsDialog: (@Composable ((onOpenSettings: () -> Unit) -> Unit))? = null
)

@Suppress("EndOfSentenceFormat")
/**
 * Configuration for gallery settings
 */
data class GalleryConfig(
    val allowMultiple: Boolean = false,
    val mimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL),
    val selectionLimit: Int = 30
)

@Suppress("EndOfSentenceFormat")
/**
 * Configuration for crop functionality
 */
data class CropConfig(
    val enabled: Boolean = false,
    val aspectRatioLocked: Boolean = false,
    val circularCrop: Boolean = false,
    val squareCrop: Boolean = true,
    val freeformCrop: Boolean = false
)

@Suppress("EndOfSentenceFormat")
/**
 * Main configuration for camera capture
 */
data class CameraCaptureConfig(
    val preference: CapturePhotoPreference = CapturePhotoPreference.QUALITY,
    val captureButtonSize: Dp = 72.dp,
    val compressionLevel: CompressionLevel? = null, // null = no compression, MEDIUM = recommended
    val uiConfig: UiConfig = UiConfig(),
    val cameraCallbacks: CameraCallbacks = CameraCallbacks(),
    val permissionAndConfirmationConfig: PermissionAndConfirmationConfig = PermissionAndConfirmationConfig(),
    val galleryConfig: GalleryConfig = GalleryConfig(),
    val cropConfig: CropConfig = CropConfig()
)

/**
 * Configuration class for launching the image picker.
 */
data class ImagePickerConfig(
    val onPhotoCaptured: (PhotoResult) -> Unit,
    val onPhotosSelected: ((List<GalleryPhotoResult>) -> Unit)? = null,
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

    val cameraCaptureConfig: CameraCaptureConfig = CameraCaptureConfig(),
    val directCameraLaunch: Boolean = false,
    val enableCrop: Boolean = false
)

/**
 * Configuration for the camera preview UI and callbacks.
 */
data class CameraPreviewConfig(
    val captureButtonSize: Dp = 72.dp,
    val uiConfig: UiConfig = UiConfig(),
    val cameraCallbacks: CameraCallbacks = CameraCallbacks()
)

/**
 * Configuration for camera capture preference.
 */
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
