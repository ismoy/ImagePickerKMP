package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.domain.utils.JvmFilePicker

@Suppress("FunctionNaming")
@Composable
actual fun ImagePickerLauncher(
    config: ImagePickerConfig
) {
    // On JVM, we can only access the file gallery
    // No camera is available, so we launch the file selector directly
    
    JvmFilePicker(
        onPhotosSelected = { galleryResults ->
            if (galleryResults.size > 1 && config.onPhotosSelected != null) {
                config.onPhotosSelected.invoke(galleryResults)
            } else if (galleryResults.size == 1) {
                val galleryResult = galleryResults.first()
                val photoResult = PhotoResult(
                    uri = galleryResult.uri,
                    width = galleryResult.width,
                    height = galleryResult.height,
                    fileName = galleryResult.fileName,
                    fileSize = galleryResult.fileSize
                )
                config.onPhotoCaptured(photoResult)
            } else {
                config.onDismiss()
            }
        },
        onError = { exception ->
            config.onError(exception)
        },
        onDismiss = {
            config.onDismiss()
        },
        allowMultiple = config.onPhotosSelected != null,
        mimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG),
        selectionLimit = 30,
        fileFilterDescription = "Images"
    )
}
