package io.github.ismoy.imagepickerkmp.domain.utils.exif

import android.content.Context
import android.net.Uri
import androidx.exifinterface.media.ExifInterface

internal object ExifInterfaceHelper {

    fun createFromUri(context: Context, uri: Uri): ExifInterface? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                println(" Failed to open InputStream for URI: $uri")
                return null
            }

            ExifInterface(inputStream)
        } catch (e: Exception) {
            println(" Failed to create ExifInterface: ${e.javaClass.simpleName}: ${e.message}")
            null
        }
    }
}
