package io.github.ismoy.imagepickerkmp.gallery

import android.content.Context
import android.net.Uri
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.GALLERY_PROCESSOR_TAG
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.IMAGE_PREFIX_TEXT
import io.github.ismoy.imagepickerkmp.logger.PhotoLogger
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.GalleryPhotoResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object GalleryFileProcessor {

    suspend fun processSelectedFile(
        context: Context,
        uri: Uri,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false
    ): GalleryPhotoResult? = withContext(Dispatchers.IO) {
        try {
            val mimeType = GalleryFileUtils.getFileMimeType(context, uri)
            
            if (mimeType?.startsWith(IMAGE_PREFIX_TEXT) == true) {
                GalleryImageProcessor.processSelectedImageSuspend(context, uri, compressionLevel, includeExif)
            } else {
                PhotoLogger.debug("$GALLERY_PROCESSOR_TAG: Non-image file selected.")
                null
            }
        } catch (e: Exception) {
            PhotoLogger.debug("$GALLERY_PROCESSOR_TAG${e.message}")
            null
        }
    }
}
