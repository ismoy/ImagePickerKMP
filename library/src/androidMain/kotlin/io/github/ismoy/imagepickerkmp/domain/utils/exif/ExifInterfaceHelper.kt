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
     * 
     * Note: The caller is responsible for closing the InputStream if needed.
     * ExifInterface internally handles the file descriptor.
     */
    fun createFromUri(context: Context, uri: Uri): ExifInterface? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                println(" Failed to open InputStream for URI: $uri")
                return null
            }
            
            // Don't close the stream immediately - ExifInterface needs it for thumbnails
            // The stream will be garbage collected when ExifInterface is no longer used
            ExifInterface(inputStream)
        } catch (e: Exception) {
            println(" Failed to create ExifInterface: ${e.javaClass.simpleName}: ${e.message}")
            null
        }
    }
}
