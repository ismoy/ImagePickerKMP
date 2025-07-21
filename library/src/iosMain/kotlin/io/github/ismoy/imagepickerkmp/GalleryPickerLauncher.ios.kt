package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.ismoy.imagepickerkmp.GalleryPhotoHandler.PhotoResult

@Suppress("LongParameterList")
@Composable
actual fun GalleryPickerLauncher(
    context: Any?,
    onPhotosSelected: (List<PhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    allowMultiple: Boolean,
    mimeTypes: List<String>,
    selectionLimit: Long
) {
    LaunchedEffect(Unit) {
        if (allowMultiple) {
            val selectedImages = mutableListOf<PhotoResult>()
            GalleryPickerOrchestrator.launchGallery(
                onPhotoSelected = { result ->
                    selectedImages.add(result)
                    onPhotosSelected(selectedImages.toList())
                },
                onError = onError,
                allowMultiple = true,
                selectionLimit = selectionLimit
            )
        } else {
            GalleryPickerOrchestrator.launchGallery(
                onPhotoSelected = { result -> onPhotosSelected(listOf(result)) },
                onError = onError,
                allowMultiple = false,
                selectionLimit = 1
            )
        }
    }
}

