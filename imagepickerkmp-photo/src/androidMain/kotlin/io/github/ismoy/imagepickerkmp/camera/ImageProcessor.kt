package io.github.ismoy.imagepickerkmp.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.graphics.scale
import androidx.exifinterface.media.ExifInterface
import io.github.ismoy.imagepickerkmp.camera.FileManager
import io.github.ismoy.imagepickerkmp.camera.HighPerformanceConfig
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.NUMBER_ONE
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.NUMBER_ONE_THOUSAND_TWENTY_FOR
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.NUMBER_TWO
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.NUMBER_TWO_THOUSAND_FORTY_EIGHT
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.NUMBER_ZERO
import io.github.ismoy.imagepickerkmp.picker.ImageProcessingException
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.PhotoResult
import io.github.ismoy.imagepickerkmp.logger.PhotoLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

internal class ImageProcessor(
    private val context: Context,
    private val fileManager: FileManager
) {

    suspend fun processImage(
        imageFile: File,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false,
        redactGpsData: Boolean = true
    ): PhotoResult = withContext(Dispatchers.Default) {
        try {
            val exifData = if (includeExif) {
                try {
                    val raw = ExifDataExtractor.extractExifData(context, Uri.fromFile(imageFile))
                    if (redactGpsData) raw?.withRedactedGps() else raw
                } catch (e: Exception) {
                    PhotoLogger.debug("Failed to extract EXIF: ${e.message}")
                    null
                }
            } else null

            val isHighEnd = HighPerformanceConfig.isHighEndDevice(context)
            val options = BitmapFactory.Options().apply {
                inPreferredConfig = if (isHighEnd) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(imageFile.absolutePath, options)
            options.inSampleSize = calculateInSampleSize(
                srcWidth = options.outWidth,
                srcHeight = options.outHeight,
                isHighEnd = isHighEnd
            )
            options.inJustDecodeBounds = false

            val rawBitmap = BitmapFactory.decodeFile(imageFile.absolutePath, options)
                ?: throw ImageProcessingException("Failed to decode captured image.")

            val orientedBitmap = correctOrientation(rawBitmap, imageFile)
            if (orientedBitmap !== rawBitmap) rawBitmap.recycle()

            var outputBitmap: Bitmap? = null
            try {
                val resizedBitmap = if (compressionLevel != null) {
                    resizeForCompression(orientedBitmap, compressionLevel)
                } else {
                    orientedBitmap
                }
                if (resizedBitmap !== orientedBitmap) orientedBitmap.recycle()

                outputBitmap = resizedBitmap
                val quality = compressionLevel?.toJpegQuality() ?: 100

                val outputFile = fileManager.createImageFile()
                FileOutputStream(outputFile).use { out ->
                    outputBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
                }

                val result = PhotoResult(
                    uri = fileManager.fileToUriString(outputFile),
                    width = outputBitmap.width,
                    height = outputBitmap.height,
                    fileName = outputFile.name,
                    fileSize = outputFile.length(),
                    exif = exifData
                )

                outputBitmap.recycle()
                outputBitmap = null
                imageFile.delete()

                result
            } catch (e: Exception) {
                outputBitmap?.let { if (!it.isRecycled) it.recycle() }
                throw e
            }
        } catch (e: Exception) {
            throw ImageProcessingException("Failed to process image: ${e.message}", e)
        }
    }

    private fun correctOrientation(bitmap: Bitmap, file: File): Bitmap {
        return try {
            val exif = ExifInterface(file.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            val degrees = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90  -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> 0f
                ExifInterface.ORIENTATION_FLIP_VERTICAL   -> 180f
                ExifInterface.ORIENTATION_TRANSPOSE        -> 90f
                ExifInterface.ORIENTATION_TRANSVERSE       -> 270f
                else -> 0f
            }
            val flipH = orientation == ExifInterface.ORIENTATION_FLIP_HORIZONTAL ||
                        orientation == ExifInterface.ORIENTATION_TRANSPOSE
            val flipV = orientation == ExifInterface.ORIENTATION_FLIP_VERTICAL ||
                        orientation == ExifInterface.ORIENTATION_TRANSVERSE

            if (degrees == 0f && !flipH && !flipV) return bitmap

            val matrix = Matrix().apply {
                if (flipH) postScale(-1f, 1f)
                if (flipV) postScale(1f, -1f)
                if (degrees != 0f) postRotate(degrees)
            }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            PhotoLogger.debug("Could not read EXIF orientation: ${e.message}")
            bitmap
        }
    }

    private fun resizeForCompression(bitmap: Bitmap, compressionLevel: CompressionLevel): Bitmap {
        val maxDimension = compressionLevel.toMaxDimension()
        val currentMax = maxOf(bitmap.width, bitmap.height)
        if (currentMax <= maxDimension) return bitmap
        val scale = maxDimension.toFloat() / currentMax.toFloat()
        val targetWidth = (bitmap.width * scale).toInt()
        val targetHeight = (bitmap.height * scale).toInt()
        val resized = bitmap.scale(targetWidth, targetHeight, true)
        if (resized !== bitmap) bitmap.recycle()
        return resized
    }

    private fun calculateInSampleSize(srcWidth: Int, srcHeight: Int, isHighEnd: Boolean): Int {
        val maxDimension = if (isHighEnd) NUMBER_TWO_THOUSAND_FORTY_EIGHT else NUMBER_ONE_THOUSAND_TWENTY_FOR
        var inSampleSize = NUMBER_ONE
        if (srcWidth <= NUMBER_ZERO || srcHeight <= NUMBER_ZERO) return inSampleSize
        val maxSrc = maxOf(srcWidth, srcHeight)
        while (maxSrc / (inSampleSize * NUMBER_TWO) >= maxDimension) {
            inSampleSize *= NUMBER_TWO
        }
        return inSampleSize
    }
}
