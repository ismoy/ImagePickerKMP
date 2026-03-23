package io.github.ismoy.imagepickerkmp.data.processors.exif

import android.content.Context
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger

internal object ExifInterfaceHelper {

    fun createFromUri(context: Context, uri: Uri): ExifInterface? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                DefaultLogger.logDebug("Failed to open InputStream for URI")
                return null
            }
            ExifInterface(inputStream)
        } catch (e: Exception) {
            DefaultLogger.logDebug("Failed to create ExifInterface: ${e.javaClass.simpleName}")
            null
        }
    }
}
