package io.github.ismoy.imagepickerkmp.data.processors

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import io.github.ismoy.imagepickerkmp.data.camera.CameraController.CameraType
import io.github.ismoy.imagepickerkmp.domain.exceptions.ImageProcessingException
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.domain.config.HighPerformanceConfig
import io.github.ismoy.imagepickerkmp.domain.utils.ExifDataExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import androidx.core.graphics.scale


internal class ImageProcessor(
    private val context: Context,
    private val fileManager: io.github.ismoy.imagepickerkmp.data.managers.FileManager,
    private val orientationCorrector: ImageOrientationCorrector,
    private val processingScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) {

    fun processImage(
        imageFile: File,
        cameraType: CameraType,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false,
        onPhotoCaptured: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        processingScope.launch {
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
                    inPreferredConfig = if (HighPerformanceConfig.isHighEndDevice(context)) {
                        Bitmap.Config.ARGB_8888
                    } else {
                        Bitmap.Config.RGB_565
                    }
                    inJustDecodeBounds = false
                    inSampleSize = 1
                }

                val originalBitmap = BitmapFactory.decodeFile(correctedImageFile.absolutePath, options)
                    ?: run {
                        withContext(Dispatchers.Main) {
                            onError(ImageProcessingException("Failed to decode captured image."))
                        }
                        return@launch
                    }

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

                    val fileSizeInBytes = finalFile.length()
                    val result = PhotoResult(
                        uri = fileManager.fileToUriString(finalFile),
                        width = processedBitmap.width,
                        height = processedBitmap.height,
                        fileName = finalFile.name,
                        fileSize = fileSizeInBytes,
                        exif = exifData
                    )
                    if (processedBitmap != originalBitmap) {
                        originalBitmap.recycle()
                    }
                    processedBitmap.recycle()
                    processedBitmap = null
                    if (compressionLevel != null && finalFile != correctedImageFile) {
                        correctedImageFile.delete()
                    }
                    if (correctedImageFile != imageFile) {
                        imageFile.delete()
                    }

                    withContext(Dispatchers.Main) {
                        onPhotoCaptured(result)
                    }
                } catch (e: Exception) {
                    if (processedBitmap != null && processedBitmap != originalBitmap) {
                        processedBitmap.recycle()
                    }
                    if (!originalBitmap.isRecycled) originalBitmap.recycle()
                    throw e 
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(ImageProcessingException("Failed to process image: ${e.message}", e))
                }
            }
        }
    }

    private fun logDebug(message: String) {
        println(" Android ImageProcessor: $message")
    }
}
