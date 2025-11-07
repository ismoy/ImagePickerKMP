package io.github.ismoy.imagepickerkmp.presentation.ui.components.gallery

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

/**
 * Utilities for file operations in gallery picker.
 */
internal object GalleryFileUtils {
    
    /**
     * Get the MIME type of a file from its URI.
     */
    fun getFileMimeType(context: Context, uri: Uri): String? {
        return context.contentResolver.getType(uri)
    }
    
    /**
     * Get the file name from a URI.
     */
    fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        val cursor = try {
            context.contentResolver.query(uri, null, null, null, null)
        } catch (e: Exception) {
            println(" Error querying file name: ${e.message}")
            null
        }
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    result = it.getString(displayNameIndex)
                }
            }
        }
        return result
    }
    
    /**
     * Convert bytes to kilobytes.
     */
    fun bytesToKB(bytes: Long): Long = maxOf(1L, bytes / 1024)
}
