
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
        GalleryPickerOrchestrator.launchGallery(
            onPhotoSelected = { result -> onPhotosSelected(listOf(result)) },
            onError = onError
        )
    }
}
