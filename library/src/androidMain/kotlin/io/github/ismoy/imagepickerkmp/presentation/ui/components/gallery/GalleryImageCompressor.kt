package io.github.ismoy.imagepickerkmp.presentation.ui.components.gallery

import android.content.Context
import android.graphics.Bitmap
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel

/**
 * Compressor for images from gallery.
 */
internal object GalleryImageCompressor {
    
    /**
     * Compress bitmap to byte array with quality based on compression level.
     */
    fun compressBitmapToByteArray(
        bitmap: Bitmap,
        compressionLevel: CompressionLevel
    ): ByteArray {
        val quality = (compressionLevel.toQualityValue() * 100).toInt()
        val outputStream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return outputStream.toByteArray()
    }
    
    /**
     * Create temporary file for compressed image.
     */
    fun createTempImageFile(context: Context, imageBytes: ByteArray): java.io.File? {
        return try {
            val tempFile = java.io.File.createTempFile(
                "compressed_gallery_${System.currentTimeMillis()}",
                ".jpg",
                context.cacheDir
            )
            java.io.FileOutputStream(tempFile).use { outputStream ->
                outputStream.write(imageBytes)
            }
            tempFile
        } catch (e: Exception) {
            println(" Error creating temp file: ${e.message}")
            null
        }
    }
}
