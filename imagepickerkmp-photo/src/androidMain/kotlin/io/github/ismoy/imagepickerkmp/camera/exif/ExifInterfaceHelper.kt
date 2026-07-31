package io.github.ismoy.imagepickerkmp.camera.exif

import android.content.Context
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import io.github.ismoy.imagepickerkmp.logger.PhotoLogger

internal object ExifInterfaceHelper {

    fun createFromUri(context: Context, uri: Uri): ExifInterface? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                PhotoLogger.debug("Failed to open InputStream for URI")
                return null
            }
            ExifInterface(inputStream)
        } catch (e: Exception) {
            PhotoLogger.debug("Failed to create ExifInterface: ${e.javaClass.simpleName}")
            null
        }
    }
}
