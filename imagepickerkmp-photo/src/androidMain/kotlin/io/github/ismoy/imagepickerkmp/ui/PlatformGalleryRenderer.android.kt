package io.github.ismoy.imagepickerkmp.ui

import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import io.github.ismoy.imagepickerkmp.I18nKonfig.Errors.invalid_context_error
import io.github.ismoy.imagepickerkmp.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.picker.MimeType

@Suppress("ReturnCount","LongParameterList")
@Composable
internal actual fun PlatformGalleryRenderer(
    onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    allowMultiple: Boolean,
    mimeTypes: List<MimeType>,
    selectionLimit: Long,
    cameraCaptureConfig: CameraCaptureConfig?,
    enableCrop: Boolean,
    fileFilterDescription: String,
    includeExif: Boolean,
    mimeTypeMismatchMessage: String?,
    compressionLevel: CompressionLevel?,
    onCropPending: () -> Unit
) {
    val context = LocalContext.current
    if (LocalActivityResultRegistryOwner.current == null) {
        onError(Exception(invalid_context_error))
        return
    }

    val config = GalleryPickerConfig(
        context = context,
        onPhotosSelected = onPhotosSelected,
        onError = onError,
        onDismiss = onDismiss,
        allowMultiple = allowMultiple,
        mimeTypes = mimeTypes.map { it.value },
        cameraCaptureConfig = cameraCaptureConfig,
        enableCrop = enableCrop,
        includeExif = includeExif,
        selectionLimit = selectionLimit.toInt(),
        compressionLevel = compressionLevel,
        mimeTypeMismatchMessage = mimeTypeMismatchMessage
    )
    GalleryPickerLauncherContent(config)
}