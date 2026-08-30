package io.github.ismoy.imagepickerkmp.scanner

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import androidx.exifinterface.media.ExifInterface
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import io.github.ismoy.imagepickerkmp.scanner.utils.LoggerFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import kotlin.coroutines.resume

/**
 * Android implementation for static barcode scanning.
 *
 * Image decoding and enhancement run off the UI thread. Decoding is sampled before
 * allocation, and one ML Kit client is reused and closed for the full operation.
 */
internal class AndroidStaticCodeScanner : StaticCodeScanner {
    private val logger = LoggerFactory.getLogger()

    override suspend fun scanImage(imageBytes: ByteArray): String? = withContext(Dispatchers.Default) {
        if (imageBytes.isEmpty()) return@withContext null

        val bitmap = decodeSampledBitmap(imageBytes) ?: return@withContext null
        val scanner = BarcodeScanning.getClient()
        try {
            scanWithEnhancements(
                scanner = scanner,
                bitmap = bitmap,
                rotationDegrees = getRotationFromExif(imageBytes)
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            logger.error("StaticScanner", "Unable to scan image", exception)
            null
        } finally {
            scanner.close()
            bitmap.recycleSafely()
        }
    }

    private suspend fun scanWithEnhancements(
        scanner: BarcodeScanner,
        bitmap: Bitmap,
        rotationDegrees: Int
    ): String? {
        scanBitmap(scanner, bitmap, rotationDegrees)?.let { return it }

        for ((minLuma, maxLuma) in CONTRAST_PASSES) {
            val stretched = createStretchedBitmap(bitmap, minLuma, maxLuma, invert = false)
            try {
                scanBitmap(scanner, stretched, rotationDegrees)?.let { return it }
            } finally {
                stretched.recycleSafely()
            }

            val inverted = createStretchedBitmap(bitmap, minLuma, maxLuma, invert = true)
            try {
                scanBitmap(scanner, inverted, rotationDegrees)?.let { return it }
            } finally {
                inverted.recycleSafely()
            }
        }
        return null
    }

    private fun getRotationFromExif(imageBytes: ByteArray): Int = try {
        ByteArrayInputStream(imageBytes).use { inputStream ->
            when (
                ExifInterface(inputStream).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            ) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        }
    } catch (_: Exception) {
        0
    }

    private fun decodeSampledBitmap(imageBytes: ByteArray): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        val options = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight)
        }
        val decoded = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options) ?: return null
        val largestDimension = maxOf(decoded.width, decoded.height)
        if (largestDimension <= MAX_DECODED_DIMENSION) return decoded

        val scale = MAX_DECODED_DIMENSION.toFloat() / largestDimension.toFloat()
        val scaled = decoded.scale(
            width = (decoded.width * scale).toInt().coerceAtLeast(1),
            height = (decoded.height * scale).toInt().coerceAtLeast(1),
            filter = true
        )
        if (scaled !== decoded) decoded.recycleSafely()
        return scaled
    }

    private fun calculateInSampleSize(width: Int, height: Int): Int {
        var sampleSize = 1
        while (maxOf(width / sampleSize, height / sampleSize) > MAX_DECODED_DIMENSION) {
            sampleSize *= 2
        }
        return sampleSize
    }

    private suspend fun scanBitmap(
        scanner: BarcodeScanner,
        bitmap: Bitmap,
        rotationDegrees: Int
    ): String? = suspendCancellableCoroutine { continuation ->
        scanner.process(InputImage.fromBitmap(bitmap, rotationDegrees))
            .addOnSuccessListener { barcodes ->
                if (continuation.isActive) {
                    continuation.resume(
                        barcodes.firstOrNull { !it.rawValue.isNullOrBlank() }?.rawValue
                    )
                }
            }
            .addOnFailureListener { exception ->
                logger.error("StaticScanner", "ML Kit could not process image", exception)
                if (continuation.isActive) continuation.resume(null)
            }
    }

    private fun Bitmap.recycleSafely() {
        if (!isRecycled) recycle()
    }

    private fun createStretchedBitmap(
        source: Bitmap,
        minLuma: Float,
        maxLuma: Float,
        invert: Boolean
    ): Bitmap {
        val destination = createBitmap(source.width, source.height, source.config ?: Bitmap.Config.ARGB_8888)
        val saturationMatrix = ColorMatrix().apply { setSaturation(0f) }
        val range = maxLuma - minLuma
        val contrast = if (range > 0) 255f / range else 255f
        val translation = -minLuma * contrast
        saturationMatrix.postConcat(
            ColorMatrix(
                floatArrayOf(
                    contrast, 0f, 0f, 0f, translation,
                    0f, contrast, 0f, 0f, translation,
                    0f, 0f, contrast, 0f, translation,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        )
        if (invert) {
            saturationMatrix.postConcat(
                ColorMatrix(
                    floatArrayOf(
                        -1f, 0f, 0f, 0f, 255f,
                        0f, -1f, 0f, 0f, 255f,
                        0f, 0f, -1f, 0f, 255f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            )
        }
        Canvas(destination).drawBitmap(source, 0f, 0f, Paint().apply {
            colorFilter = ColorMatrixColorFilter(saturationMatrix)
        })
        return destination
    }

    private companion object {
        const val MAX_DECODED_DIMENSION = 1_920
        val CONTRAST_PASSES = listOf(
            100f to 255f,
            150f to 255f,
            190f to 255f,
            220f to 255f,
            0f to 150f
        )
    }
}
