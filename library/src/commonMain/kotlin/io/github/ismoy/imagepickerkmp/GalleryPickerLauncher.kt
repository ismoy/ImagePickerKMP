package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.GalleryPhotoHandler.PhotoResult

@Composable
expect fun GalleryPickerLauncher(
    context: Any?,
    onPhotosSelected: (List<PhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    allowMultiple: Boolean = false,
    mimeTypes: List<String> = listOf("image/*")
) 