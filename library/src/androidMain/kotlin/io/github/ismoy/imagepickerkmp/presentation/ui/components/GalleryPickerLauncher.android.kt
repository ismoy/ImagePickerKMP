package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPickerConfig
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import io.github.ismoy.imagepickerkmp.presentation.resources.StringResource
import io.github.ismoy.imagepickerkmp.presentation.resources.getStringResource
import io.github.ismoy.imagepickerkmp.presentation.ui.components.gallery.GalleryPickerLauncherContent

@Suppress("ReturnCount","LongParameterList")
@Composable
actual fun GalleryPickerLauncher(
    onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    allowMultiple: Boolean,
    mimeTypes: List<MimeType>,
    selectionLimit: Long,
    cameraCaptureConfig: CameraCaptureConfig?,
    enableCrop: Boolean,
    fileFilterDescription: String,
    includeExif: Boolean
) {
    val context = LocalContext.current
    val activity = context
    if (activity !is ComponentActivity) {
        onError(Exception(getStringResource(StringResource.INVALID_CONTEXT_ERROR)))
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
        includeExif = includeExif
    )
    GalleryPickerLauncherContent(config)
}