package io.github.ismoy.imagepickerkmp.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import io.github.ismoy.imagepickerkmp.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.core.I18nKonfig.Errors.invalid_context_error
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
    onCropPending: () -> Unit
) {
    val context = LocalContext.current
    val activity = context
    val invalidContextMsg =invalid_context_error
    if (activity !is ComponentActivity) {
        onError(Exception(invalidContextMsg))
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
        mimeTypeMismatchMessage = mimeTypeMismatchMessage
    )
    GalleryPickerLauncherContent(config)
}