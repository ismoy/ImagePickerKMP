package io.github.ismoy.imagepickerkmp.presentation.ui.components.gallery

import android.content.Context
import android.graphics.Bitmap
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel

/**
 * Compressor for images from gallery.
 * Optimized with adaptive compression based on image size.
 */
internal object GalleryImageCompressor {

    fun compressBitmapToByteArray(
        bitmap: Bitmap,
        compressionLevel: CompressionLevel
    ): ByteArray {
        val pixels = bitmap.width * bitmap.height
        
        val quality = when {
            pixels > 1_500_000 -> 40
            pixels > 800_000 -> 50
            else -> 65
        }
        
        val finalQuality = when (compressionLevel) {
            CompressionLevel.HIGH -> 50
            CompressionLevel.MEDIUM -> 60
            CompressionLevel.LOW -> 70
        }
        
        val appliedQuality = minOf(quality, finalQuality)
        
        val outputStream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, appliedQuality, outputStream)
        return outputStream.toByteArray()
    }
    

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
