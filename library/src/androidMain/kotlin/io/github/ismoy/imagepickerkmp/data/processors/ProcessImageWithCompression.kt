package io.github.ismoy.imagepickerkmp.data.processors

import android.graphics.Bitmap
import androidx.core.graphics.scale
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel


internal fun processImageWithCompression(
    bitmap: Bitmap,
    compressionLevel: CompressionLevel
): Bitmap {
    if (compressionLevel == CompressionLevel.LOW) return bitmap

    val maxDimension = when (compressionLevel) {
        CompressionLevel.HIGH   -> 1920
        CompressionLevel.MEDIUM -> 3840
        CompressionLevel.LOW    -> Int.MAX_VALUE
    }

    val currentMaxDimension = maxOf(bitmap.width, bitmap.height)

    return if (currentMaxDimension > maxDimension) {
        val scale = maxDimension.toFloat() / currentMaxDimension.toFloat()
        val targetWidth = (bitmap.width * scale).toInt()
        val targetHeight = (bitmap.height * scale).toInt()

        val resizedBitmap = bitmap.scale(targetWidth, targetHeight, true)
        if (resizedBitmap != bitmap) {
            bitmap.recycle()
        }
        resizedBitmap
    } else {
        bitmap
    }
}