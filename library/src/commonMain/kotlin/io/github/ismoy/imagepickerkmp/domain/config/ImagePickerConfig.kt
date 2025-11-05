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
 * @param customPermissionHandler Custom handler for permission configuration
 * @param customConfirmationView Custom composable for photo confirmation view
 * @param customDeniedDialog Custom dialog when permission is denied
 * @param customSettingsDialog Custom dialog for opening settings
 * @param skipConfirmation If true, automatically confirms the photo without showing confirmation screen
 * @param cancelButtonTextIOS Text for the cancel button in iOS permission dialogs (default: "Cancel")
 * @param onCancelPermissionConfigIOS Callback executed when user cancels the permission configuration dialog on iOS
 */
data class PermissionAndConfirmationConfig(
    val customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    val customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    val customDeniedDialog: (@Composable ((onRetry: () -> Unit) -> Unit))? = null,
    val customSettingsDialog: (@Composable ((onOpenSettings: () -> Unit) -> Unit))? = null,
    val skipConfirmation: Boolean = false,
    val cancelButtonTextIOS: String? = "Cancel",
    val onCancelPermissionConfigIOS: (() -> Unit)? = null
)

@Suppress("EndOfSentenceFormat")
/**
 * Configuration for gallery settings
 */
data class GalleryConfig(
    val allowMultiple: Boolean = false,
    val mimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL),
    val selectionLimit: Int = 30,
    val includeExif: Boolean = false // Include EXIF metadata including GPS location data
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
    val preference: CapturePhotoPreference = CapturePhotoPreference.BALANCED,
    val captureButtonSize: Dp = 72.dp,
    val compressionLevel: CompressionLevel? = CompressionLevel.MEDIUM,
    val includeExif: Boolean = false,
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
    val cameraCallbacks: CameraCallbacks = CameraCallbacks(),
    val galleryConfig: GalleryConfig = GalleryConfig()
)

/**
 * Configuration for camera permission dialogs.
 * 
 * @param titleDialogConfig Title text for the permission configuration dialog
 * @param descriptionDialogConfig Description text for the permission configuration dialog
 * @param btnDialogConfig Button text for the permission configuration dialog
 * @param titleDialogDenied Title text when permission is denied
 * @param descriptionDialogDenied Description text when permission is denied
 * @param btnDialogDenied Button text when permission is denied
 * @param customDeniedDialog Custom composable dialog when permission is denied
 * @param customSettingsDialog Custom composable dialog for opening settings
 * @param cancelButtonText Text for the cancel button (iOS only, default: "Cancel")
 * @param onCancelPermissionConfigIOS Callback when user cancels permission config dialog (iOS only)
 */
data class CameraPermissionDialogConfig(
    val titleDialogConfig: String,
    val descriptionDialogConfig: String,
    val btnDialogConfig: String,
    val titleDialogDenied: String,
    val descriptionDialogDenied: String,
    val btnDialogDenied: String,
    val customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)? = null,
    val customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)? = null,
    val cancelButtonText: String? = "Cancel",
    val onCancelPermissionConfigIOS: (() -> Unit)? = null
)
