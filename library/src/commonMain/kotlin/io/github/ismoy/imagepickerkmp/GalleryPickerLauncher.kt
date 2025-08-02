
package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.GalleryPhotoHandler.PhotoResult
import io.github.ismoy.imagepickerkmp.ImagePickerUiConstants.SELECTION_LIMIT

@Suppress("LongParameterList")
@Composable
expect fun GalleryPickerLauncher(
    onPhotosSelected: (List<PhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit = {},
    allowMultiple: Boolean = false,
    mimeTypes: List<String> = listOf("image/*"),
    selectionLimit: Long = SELECTION_LIMIT
)

