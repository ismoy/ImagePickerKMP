package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.ismoy.imagepickerkmp.data.orchestrators.GalleryPickerOrchestrator
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType

@Suppress("LongParameterList")
@Composable
actual fun GalleryPickerLauncher(
    onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    allowMultiple: Boolean,
    mimeTypes: List<MimeType>,
    selectionLimit: Long,
    cameraCaptureConfig: CameraCaptureConfig?
) {
    LaunchedEffect(Unit) {
        val compressionLevel = cameraCaptureConfig?.compressionLevel
        
        if (allowMultiple) {
            val selectedImages = mutableListOf<GalleryPhotoResult>()
            GalleryPickerOrchestrator.launchGallery(
                onPhotoSelected = { result ->
                    selectedImages.add(result)
                    onPhotosSelected(selectedImages.toList())
                },
                onError = onError,
                onDismiss = onDismiss,
                allowMultiple = true,
                selectionLimit = selectionLimit,
                compressionLevel = compressionLevel
            )
        } else {
            GalleryPickerOrchestrator.launchGallery(
                onPhotoSelected = { result -> onPhotosSelected(listOf(result)) },
                onError = onError,
                onDismiss = onDismiss,
                allowMultiple = false,
                selectionLimit = 1,
                compressionLevel = compressionLevel
            )
        }
    }
}
