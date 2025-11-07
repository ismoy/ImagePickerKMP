package io.github.ismoy.imagepickerkmp.presentation.ui.components.gallery

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.graphics.scale
import io.github.ismoy.imagepickerkmp.data.camera.CameraController
import io.github.ismoy.imagepickerkmp.data.processors.ImageOrientationCorrector
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.ExifData
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger
import io.github.ismoy.imagepickerkmp.domain.utils.ExifDataExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Processor for image files from gallery.
 */
internal object GalleryImageProcessor {
    
    /**
     * Process selected image with compression and EXIF extraction.
     */
    suspend fun processSelectedImageSuspend(
        context: Context,
        uri: Uri,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false
    ): GalleryPhotoResult? {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = GalleryFileUtils.getFileName(context, uri)
                val mimeType = GalleryFileUtils.getFileMimeType(context, uri)
                
                if (compressionLevel == null && !includeExif) {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val originalBytes = inputStream?.readBytes()
                    inputStream?.close()
                    
                    if (originalBytes != null) {
                        return@withContext createFallbackResult(
                            uri,
                            originalBytes,
                            fileName,
                            mimeType,
                            null
                        )
                    }
                    return@withContext null
                }
                
                val exifData = if (includeExif) {
                    extractExifDataIfNeeded(context, uri, mimeType, true)
                } else {
                    null
                }
                
                if (compressionLevel != null) {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val originalBytes = inputStream?.readBytes()
                    inputStream?.close()
                    
                    if (originalBytes != null) {
                        val processedBitmap = processBitmapFromGallery(
                            context, 
                            uri, 
                            originalBytes, 
                            compressionLevel
                        )
                        
                        if (processedBitmap != null) {
                            return@withContext createResultFromBitmap(
                                context,
                                processedBitmap,
                                fileName,
                                mimeType,
                                exifData,
                                compressionLevel
                            )
                        }
                        
                        return@withContext createFallbackResult(
                            uri,
                            originalBytes,
                            fileName,
                            mimeType,
                            exifData
                        )
                    }
                } else {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val originalBytes = inputStream?.readBytes()
                    inputStream?.close()
                    
                    if (originalBytes != null) {
                        return@withContext createFallbackResult(
                            uri,
                            originalBytes,
                            fileName,
                            mimeType,
                            exifData
                        )
                    }
                }
                
                null
            } catch (e: Exception) {
                logDebug(" Error processing selected image: ${e.message}")
                null
            }
        }
    }
    
    /**
     * Extract EXIF data if needed and if file is an image.
     */
    private fun extractExifDataIfNeeded(
        context: Context,
        uri: Uri,
        mimeType: String?,
        includeExif: Boolean
    ): ExifData? {
        return if (includeExif && mimeType?.startsWith("image/") == true) {
            try {
                ExifDataExtractor.extractExifDataWithFallbacks(context, uri)
            } catch (e: Exception) {
                logDebug(" Error extracting EXIF data: ${e.message}")
                null
            }
        } else {
            if (!includeExif) {
                logDebug(" EXIF extraction disabled by configuration")
            } else {
                logDebug(" EXIF extraction skipped - Not an image file: $mimeType")
            }
            null
        }
    }

    fun processBitmapFromGallery(
        context: Context,
        uri: Uri,
        originalBytes: ByteArray,
        compressionLevel: CompressionLevel? = null
    ): Bitmap? {
        return try {
            val tempFile = java.io.File.createTempFile(
                "gallery_temp_${System.currentTimeMillis()}",
                ".jpg",
                context.cacheDir
            )
            
            java.io.FileOutputStream(tempFile).use { outputStream ->
                outputStream.write(originalBytes)
            }
            
            val orientationCorrector = ImageOrientationCorrector()
            val correctedFile = orientationCorrector.correctImageOrientation(
                tempFile, 
                CameraController.CameraType.BACK
            )
            
            val correctedBitmap = BitmapFactory.decodeFile(correctedFile.absolutePath)
            
            tempFile.delete()
            if (correctedFile != tempFile) {
                correctedFile.delete()
            }
            
            val finalBitmap = if (correctedBitmap != null && compressionLevel != null) {
                processImageWithCompression(correctedBitmap, compressionLevel)
            } else {
                correctedBitmap
            }
            
            finalBitmap
        } catch (e: Exception) {
            logDebug(" Error processing gallery image with orientation correction: ${e.message}")
            val bitmap = BitmapFactory.decodeByteArray(originalBytes, 0, originalBytes.size)
            if (bitmap != null && compressionLevel != null) {
                processImageWithCompression(bitmap, compressionLevel)
            } else {
                bitmap
            }
        }
    }

    private fun processImageWithCompression(
        bitmap: Bitmap,
        compressionLevel: CompressionLevel
    ): Bitmap {
        val maxDimension = when (compressionLevel) {
            CompressionLevel.HIGH -> 1280
            CompressionLevel.MEDIUM -> 1280
            CompressionLevel.LOW -> 1920
        }
        
        val currentMaxDimension = maxOf(bitmap.width, bitmap.height)
        
        return if (currentMaxDimension > maxDimension) {
            val scale = maxDimension.toFloat() / currentMaxDimension.toFloat()
            val targetWidth = (bitmap.width * scale).toInt()
            val targetHeight = (bitmap.height * scale).toInt()
            val resizedBitmap = bitmap.scale(targetWidth, targetHeight)
            if (resizedBitmap != bitmap) {
                bitmap.recycle()
            }
            resizedBitmap
        } else {
            bitmap
        }
    }

    private fun createResultFromBitmap(
        context: Context,
        bitmap: Bitmap,
        fileName: String?,
        mimeType: String?,
        exifData: ExifData?,
        compressionLevel: CompressionLevel?
    ): GalleryPhotoResult? {
        val bytes = if (compressionLevel != null) {
            GalleryImageCompressor.compressBitmapToByteArray(bitmap, compressionLevel)
        } else {
            GalleryImageCompressor.compressBitmapToByteArray(bitmap, CompressionLevel.LOW)
        }
        
        val tempFile = GalleryImageCompressor.createTempImageFile(context, bytes)
        
        if (tempFile != null) {
            val fileSizeInKB = GalleryFileUtils.bytesToKB(bytes.size.toLong())
            val finalFileName = if (compressionLevel != null) "compressed_$fileName" else fileName
            
            return GalleryPhotoResult(
                uri = Uri.fromFile(tempFile).toString(),
                width = bitmap.width,
                height = bitmap.height,
                fileName = finalFileName,
                fileSize = fileSizeInKB,
                mimeType = mimeType,
                exif = exifData
            )
        }
        return null
    }
    
    /**
     * Create fallback result when bitmap processing fails.
     */
    private fun createFallbackResult(
        uri: Uri,
        originalBytes: ByteArray,
        fileName: String?,
        mimeType: String?,
        exifData: ExifData?
    ): GalleryPhotoResult {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeByteArray(originalBytes, 0, originalBytes.size, options)
        val width = options.outWidth
        val height = options.outHeight
        val fileSizeInKB = GalleryFileUtils.bytesToKB(originalBytes.size.toLong())
        
        return GalleryPhotoResult(
            uri = uri.toString(),
            width = width,
            height = height,
            fileName = fileName,
            fileSize = fileSizeInKB,
            mimeType = mimeType,
            exif = exifData
        )
    }
    
    private fun logDebug(message: String) {
        DefaultLogger.logDebug(" GalleryImageProcessor: $message")
    }
}
