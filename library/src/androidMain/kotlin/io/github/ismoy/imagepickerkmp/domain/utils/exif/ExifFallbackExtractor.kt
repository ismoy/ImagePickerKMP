package io.github.ismoy.imagepickerkmp.domain.utils.exif

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import io.github.ismoy.imagepickerkmp.domain.models.ExifData

/**
 * Fallback strategies for extracting EXIF data when direct extraction fails.
 */
internal object ExifFallbackExtractor {
    
    /**
     * Attempts to extract EXIF data using multiple fallback methods.
     * Useful for gallery photos from external sources (WhatsApp, Bluetooth, etc.)
     */
    fun extractWithFallbacks(context: Context, uri: Uri): ExifData? {
        ExifInterfaceHelper.createFromUri(context, uri)?.let { exif ->
            return ExifDataParser.parseExifData(exif)
        }
        
        extractFromTempFile(context, uri)?.let { return it }
        
        extractFromMediaStore(context, uri)?.let { return it }
        
        return null
    }
    
    private fun extractFromTempFile(context: Context, uri: Uri): ExifData? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            
            inputStream.use { stream ->
                val tempFile = java.io.File.createTempFile(
                    "exif_fallback_${System.currentTimeMillis()}",
                    ".jpg",
                    context.cacheDir
                )
                
                java.io.FileOutputStream(tempFile).use { outputStream ->
                    stream.copyTo(outputStream)
                }
                
                val tempUri = Uri.fromFile(tempFile)
                val exifData = ExifInterfaceHelper.createFromUri(context, tempUri)?.let {
                    ExifDataParser.parseExifData(it)
                }
                
                tempFile.delete()
                exifData
            }
        } catch (e: Exception) {
            println(" Temp file extraction failed: ${e.message}")
            null
        }
    }
    
    private fun extractFromMediaStore(context: Context, uri: Uri): ExifData? {
        return try {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    val filePath = cursor.getString(columnIndex)
                    
                    if (!filePath.isNullOrEmpty()) {
                        val file = java.io.File(filePath)
                        if (file.exists()) {
                            ExifInterfaceHelper.createFromUri(context, Uri.fromFile(file))?.let {
                                return ExifDataParser.parseExifData(it)
                            }
                        }
                    }
                }
                null
            }
        } catch (e: Exception) {
            println(" MediaStore extraction failed: ${e.message}")
            null
        }
    }
}
