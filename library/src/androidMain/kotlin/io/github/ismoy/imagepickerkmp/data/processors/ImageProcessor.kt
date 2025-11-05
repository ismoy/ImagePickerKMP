package io.github.ismoy.imagepickerkmp.data.processors

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.ContextCompat
import io.github.ismoy.imagepickerkmp.data.camera.CameraController.CameraType
import io.github.ismoy.imagepickerkmp.domain.exceptions.ImageProcessingException
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.domain.config.HighPerformanceConfig
import io.github.ismoy.imagepickerkmp.domain.utils.ExifDataExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.scale

/**
 * Handles image processing operations such as orientation correction and photo result creation.
 * 
 * SOLID: Single Responsibility - Only handles image processing operations
 * SOLID: Dependency Inversion - Dependencies injected via constructor
 */
class ImageProcessor(
    private val context: Context,
    private val fileManager: io.github.ismoy.imagepickerkmp.data.managers.FileManager,
    private val orientationCorrector: ImageOrientationCorrector
) {

    fun processImage(
        imageFile: File,
        cameraType: CameraType,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false,
        onPhotoCaptured: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val exifData = if (includeExif) {
                    try {
                        val originalUri = Uri.fromFile(imageFile)
                        ExifDataExtractor.extractExifData(context, originalUri)
                    } catch (e: Exception) {
                        logDebug("Failed to extract EXIF: ${e.message}")
                        null
                    }
                } else {
                    null
                }

                val correctedImageFile = orientationCorrector.correctImageOrientation(imageFile, cameraType)

                val options = BitmapFactory.Options().apply {
                    inPreferredConfig = if (HighPerformanceConfig.isHighEndDevice()) {
                        Bitmap.Config.ARGB_8888
                    } else {
                        Bitmap.Config.RGB_565
                    }
                    inJustDecodeBounds = false
                    inSampleSize = 1
                }

                val originalBitmap = BitmapFactory.decodeFile(correctedImageFile.absolutePath, options)

                if (originalBitmap != null) {
                    val processedBitmap = if (compressionLevel != null) {
                        processImageWithCompression(originalBitmap, compressionLevel)
                    } else {
                        originalBitmap
                    }

                    val finalFile = if (compressionLevel != null) {
                        saveCompressedImage(processedBitmap, correctedImageFile, compressionLevel)
                    } else {
                        correctedImageFile
                    }

                    val fileSizeInBytes = finalFile.length()
                    val fileSizeInKB = bytesToKB(fileSizeInBytes)
                    val result = PhotoResult(
                        uri = fileManager.fileToUriString(finalFile),
                        width = processedBitmap.width,
                        height = processedBitmap.height,
                        fileName = finalFile.name,
                        fileSize = fileSizeInKB,
                        exif = exifData
                    )
                    if (processedBitmap != originalBitmap) {
                        originalBitmap.recycle()
                    }
                    processedBitmap.recycle()

                    withContext(Dispatchers.Main) {
                        onPhotoCaptured(result)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onError(ImageProcessingException("Failed to decode captured image."))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(ImageProcessingException("Failed to process image: ${e.message}", e))
                }
            }
        }
    }

    private fun bytesToKB(bytes: Long): Long = maxOf(1L, bytes / 1024)

    private fun logDebug(message: String) {
        println(" Android ImageProcessor: $message")
    }
}
