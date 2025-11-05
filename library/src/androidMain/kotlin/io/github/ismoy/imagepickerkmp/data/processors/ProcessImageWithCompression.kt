package io.github.ismoy.imagepickerkmp.data.processors

import android.graphics.Bitmap
import androidx.core.graphics.scale
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel

 fun processImageWithCompression(
    bitmap: Bitmap,
    compressionLevel: CompressionLevel
): Bitmap {
    val maxDimension = when (compressionLevel) {
        CompressionLevel.HIGH -> 1280
        CompressionLevel.MEDIUM -> 1920
        CompressionLevel.LOW -> 2560
    }

    val currentMaxDimension = maxOf(bitmap.width, bitmap.height)

    return if (currentMaxDimension > maxDimension) {
        val scale = maxDimension.toFloat() / currentMaxDimension.toFloat()
        val targetWidth = (bitmap.width * scale).toInt()
        val targetHeight = (bitmap.height * scale).toInt()

        val resizedBitmap = bitmap.scale(targetWidth, targetHeight, false)
        if (resizedBitmap != bitmap) {
            bitmap.recycle()
        }
        resizedBitmap
    } else {
        bitmap
    }
}