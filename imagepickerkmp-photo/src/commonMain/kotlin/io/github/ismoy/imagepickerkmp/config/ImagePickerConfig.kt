package io.github.ismoy.imagepickerkmp.config

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.MimeType
import io.github.ismoy.imagepickerkmp.picker.PhotoResult

data class PermissionAndConfirmationConfig(
    val customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    val customDeniedDialog: (@Composable (onRetry: () -> Unit, onDismiss: () -> Unit) -> Unit)? = null,
    val customSettingsDialog: (@Composable (onOpenSettings: () -> Unit, onDismiss: () -> Unit) -> Unit)? = null,
    val cancelButtonTextIOS: String? = null,
    val onCancelPermissionConfigIOS: (() -> Unit)? = null
)

data class GalleryConfig(
    val allowMultiple: Boolean = false,
    val mimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL),
    val selectionLimit: Int = 30,
    val includeExif: Boolean = false,
    val redactGpsData: Boolean = true,
    val mimeTypeMismatchMessage: String? = null
)

data class CropConfig(
    val enabled: Boolean = false,
    val aspectRatioLocked: Boolean = false,
    val circularCrop: Boolean = true,
    val squareCrop: Boolean = true,
    val freeformCrop: Boolean = false
)

data class CameraCaptureConfig(
    val compressionLevel: CompressionLevel? = CompressionLevel.LOW,
    val includeExif: Boolean = false,
    val redactGpsData: Boolean = true,
    val permissionAndConfirmationConfig: PermissionAndConfirmationConfig = PermissionAndConfirmationConfig(),
    val cropConfig: CropConfig = CropConfig()
)

internal data class ImagePickerConfig(
    val onPhotoCaptured: (PhotoResult) -> Unit,
    val onError: (Exception) -> Unit,
    val onDismiss: () -> Unit = {},
    val cameraCaptureConfig: CameraCaptureConfig = CameraCaptureConfig(),
    val enableCrop: Boolean = false,
    val onCropPending: () -> Unit = {}
)

internal data class CameraPermissionDialogConfig(
    val titleDialogConfig: String,
    val descriptionDialogConfig: String,
    val btnDialogConfig: String,
    val titleDialogDenied: String,
    val descriptionDialogDenied: String,
    val btnDialogDenied: String,
    val customDeniedDialog: (@Composable (onRetry: () -> Unit, onDismiss: () -> Unit) -> Unit)? = null,
    val customSettingsDialog: (@Composable (onOpenSettings: () -> Unit, onDismiss: () -> Unit) -> Unit)? = null,
    val cancelButtonText: String,
    val onCancelPermissionConfigIOS: (() -> Unit)? = null
)
