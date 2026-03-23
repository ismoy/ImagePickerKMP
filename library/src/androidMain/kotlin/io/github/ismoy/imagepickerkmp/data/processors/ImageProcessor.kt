package io.github.ismoy.imagepickerkmp.data.processors

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import io.github.ismoy.imagepickerkmp.data.managers.FileManager
import io.github.ismoy.imagepickerkmp.data.models.CameraType
import io.github.ismoy.imagepickerkmp.domain.config.HighPerformanceConfig
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_ONE
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_ONE_THOUSAND_TWENTY_FOR
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_TWO
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_TWO_THOUSAND_FORTY_EIGHT
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_ZERO
import io.github.ismoy.imagepickerkmp.domain.exceptions.ImageProcessingException
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

internal class ImageProcessor(
    private val context: Context,
    private val fileManager: FileManager,
    private val orientationCorrector: ImageOrientationCorrector
) {

    suspend fun processImage(
        imageFile: File,
        cameraType: CameraType,
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
                    logDebug("Failed to extract EXIF: ${e.message}")
                    null
                }
            } else null

            val correctedImageFile = orientationCorrector.correctImageOrientation(imageFile, cameraType)

            val isHighEnd = HighPerformanceConfig.isHighEndDevice(context)
            val options = BitmapFactory.Options().apply {
                inPreferredConfig = if (isHighEnd) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(correctedImageFile.absolutePath, options)
            options.inSampleSize = calculateInSampleSize(
                srcWidth = options.outWidth,
                srcHeight = options.outHeight,
                isHighEnd = isHighEnd
            )
            options.inJustDecodeBounds = false

            val originalBitmap = BitmapFactory.decodeFile(correctedImageFile.absolutePath, options)
                ?: throw ImageProcessingException("Failed to decode captured image.")

            var processedBitmap: Bitmap? = null
            try {
                processedBitmap = if (compressionLevel != null) {
                    processImageWithCompression(originalBitmap, compressionLevel)
                } else {
                    originalBitmap
                }

                val finalFile = if (compressionLevel != null) {
                    saveCompressedImage(processedBitmap, correctedImageFile, compressionLevel)
                } else {
                    correctedImageFile
                }

                val result = PhotoResult(
                    uri = fileManager.fileToUriString(finalFile),
                    width = processedBitmap.width,
                    height = processedBitmap.height,
                    fileName = finalFile.name,
                    fileSize = finalFile.length(),
                    exif = exifData
                )

                if (processedBitmap != originalBitmap) originalBitmap.recycle()
                processedBitmap.recycle()
                processedBitmap = null
                if (compressionLevel != null && finalFile != correctedImageFile) correctedImageFile.delete()
                if (correctedImageFile != imageFile) imageFile.delete()

                result
            } catch (e: Exception) {
                if (processedBitmap != null && processedBitmap != originalBitmap) processedBitmap.recycle()
                if (!originalBitmap.isRecycled) originalBitmap.recycle()
                throw e
            }
        } catch (e: Exception) {
            throw ImageProcessingException("Failed to process image: ${e.message}", e)
        }
    }

    private fun logDebug(message: String) {
        DefaultLogger.logDebug(message)
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

