package io.github.ismoy.imagepickerkmp.data.processors

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.github.ismoy.imagepickerkmp.data.camera.CameraController.CameraType
import io.github.ismoy.imagepickerkmp.domain.exceptions.ImageProcessingException
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Handles image processing operations such as orientation correction and photo result creation.
 * 
 * SOLID: Single Responsibility - Only handles image processing operations
 * SOLID: Dependency Inversion - Dependencies injected via constructor
 */
class ImageProcessor(
    private val fileManager: io.github.ismoy.imagepickerkmp.data.managers.FileManager,
    private val orientationCorrector: ImageOrientationCorrector
) {

    fun processImage(
        imageFile: File,
        cameraType: CameraType,
        compressionLevel: CompressionLevel? = null,
        onPhotoCaptured: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val correctedImageFile = orientationCorrector.correctImageOrientation(imageFile, cameraType)
                val originalBitmap = BitmapFactory.decodeFile(correctedImageFile.absolutePath)
                
                if (originalBitmap != null) {
                    val processedBitmap = if (compressionLevel != null) {
                        processImageWithCompression(originalBitmap, compressionLevel)
                    } else {
                        originalBitmap
                    }
                    
                    val finalFile = if (compressionLevel != null) {
                        logDebug("Applying compression level: $compressionLevel")
                        saveCompressedImage(processedBitmap, correctedImageFile, compressionLevel)
                    } else {
                        logDebug("No compression applied")
                        correctedImageFile
                    }
                    
                    val fileSizeInBytes = finalFile.length()
                    val fileSizeInKB = bytesToKB(fileSizeInBytes)
                    val result = PhotoResult(
                        uri = fileManager.fileToUriString(finalFile),
                        width = processedBitmap.width,
                        height = processedBitmap.height,
                        fileName = finalFile.name,
                        fileSize = fileSizeInKB
                    )
                    
                    logDebug("Image processed successfully - File size: ${fileSizeInKB}KB (${fileSizeInBytes} bytes)")
                    
                    // Clean up bitmaps
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
    
    private fun processImageWithCompression(
        bitmap: Bitmap,
        compressionLevel: CompressionLevel
    ): Bitmap {
        // Correct logic: HIGH compression = smaller image, LOW compression = larger image
        val maxDimension = when (compressionLevel) {
            CompressionLevel.HIGH -> 1280  // Más compresión = imagen más pequeña
            CompressionLevel.MEDIUM -> 1920 // Compresión media
            CompressionLevel.LOW -> 2560   // Menos compresión = imagen más grande
        }
        
        val currentMaxDimension = maxOf(bitmap.width, bitmap.height)
        
        return if (currentMaxDimension > maxDimension) {
            val scale = maxDimension.toFloat() / currentMaxDimension.toFloat()
            val targetWidth = (bitmap.width * scale).toInt()
            val targetHeight = (bitmap.height * scale).toInt()
            
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
            if (resizedBitmap != bitmap) {
                bitmap.recycle()
            }
            resizedBitmap
        } else {
            bitmap
        }
    }
    
    private fun saveCompressedImage(
        bitmap: Bitmap,
        originalFile: File,
        compressionLevel: CompressionLevel
    ): File {
        val outputFile = File(originalFile.parentFile, "compressed_${originalFile.name}")
        val quality = (compressionLevel.toQualityValue() * 100).toInt()
        
        FileOutputStream(outputFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        }
        
        return outputFile
    }

    // Utility functions for better code practices
    private fun bytesToKB(bytes: Long): Long = maxOf(1L, bytes / 1024)

    private fun logDebug(message: String) {
        println("🔧 Android ImageProcessor: $message")
    }
}
