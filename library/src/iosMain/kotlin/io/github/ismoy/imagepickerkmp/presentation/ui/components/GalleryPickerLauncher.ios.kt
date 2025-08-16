package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.ismoy.imagepickerkmp.data.orchestrators.GalleryPickerOrchestrator
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult

@Suppress("LongParameterList")
@Composable
actual fun GalleryPickerLauncher(
    onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    allowMultiple: Boolean,
    mimeTypes: List<String>,
    selectionLimit: Long,
    cameraCaptureConfig: CameraCaptureConfig?
) {
    LaunchedEffect(Unit) {
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
                selectionLimit = selectionLimit
            )
        } else {
            GalleryPickerOrchestrator.launchGallery(
                onPhotoSelected = { result -> onPhotosSelected(listOf(result)) },
                onError = onError,
                onDismiss = onDismiss,
                allowMultiple = false,
                selectionLimit = 1
            )
        }
    }
}
