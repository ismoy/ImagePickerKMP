package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.ismoy.imagepickerkmp.GalleryPhotoHandler.PhotoResult

@Composable
actual fun GalleryPickerLauncher(
    context: Any?,
    onPhotosSelected: (List<PhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    allowMultiple: Boolean,
    mimeTypes: List<String>
) {
    LaunchedEffect(Unit) {
        if (allowMultiple) {
            // Para selección múltiple, PHPickerDelegate ya maneja múltiples imágenes
            val selectedImages = mutableListOf<PhotoResult>()
            GalleryPickerOrchestrator.launchGallery(
                onPhotoSelected = { result ->

                    selectedImages.add(result)
                    // Enviar la lista completa cada vez que se agrega una imagen
                    onPhotosSelected(selectedImages.toList())
                },
                onError = onError,
                allowMultiple = true
            )
        } else {
            // Para selección única
            GalleryPickerOrchestrator.launchGallery(
                onPhotoSelected = { result -> onPhotosSelected(listOf(result)) },
                onError = onError,
                allowMultiple = false
            )
        }
    }
}

