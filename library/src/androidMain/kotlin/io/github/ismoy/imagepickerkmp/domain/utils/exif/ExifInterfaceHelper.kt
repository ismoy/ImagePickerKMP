package io.github.ismoy.imagepickerkmp.domain.utils.exif

import android.content.Context
import android.net.Uri
import androidx.exifinterface.media.ExifInterface

/**
 * Helper for creating ExifInterface instances from different sources.
 */
internal object ExifInterfaceHelper {
    
    /**
     * Creates an ExifInterface from a URI.
     * Returns null if creation fails.
     */
    fun createFromUri(context: Context, uri: Uri): ExifInterface? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                println(" Failed to open InputStream for URI: $uri")
                return null
            }
            
            inputStream.use { stream ->
                ExifInterface(stream)
            }
        } catch (e: Exception) {
            println(" Failed to create ExifInterface: ${e.javaClass.simpleName}: ${e.message}")
            null
        }
    }
}
