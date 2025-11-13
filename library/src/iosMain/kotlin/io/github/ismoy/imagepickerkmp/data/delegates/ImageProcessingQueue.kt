package io.github.ismoy.imagepickerkmp.data.delegates

import io.github.ismoy.imagepickerkmp.data.delegates.ImageOptimizer
import io.github.ismoy.imagepickerkmp.data.processors.ImageProcessor
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.ExifData
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import io.github.ismoy.imagepickerkmp.domain.utils.ExifDataExtractor
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.useContents
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.Photos.PHAsset
import platform.PhotosUI.PHPickerResult
import platform.UIKit.UIImage
import platform.darwin.DISPATCH_QUEUE_PRIORITY_DEFAULT
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_get_main_queue


@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal class ImageProcessingQueue(
    private val pickerResults: List<PHPickerResult>,
    private val compressionLevel: CompressionLevel?,
    private val includeExif: Boolean,
    private val onComplete: (List<GalleryPhotoResult>) -> Unit,
    private val onError: (Exception) -> Unit
) {
    
    private val results = mutableListOf<GalleryPhotoResult>()
    private var processedCount = 0
    private var currentIndex = 0
    private val totalCount = pickerResults.size
    private var isProcessing = false
    
    fun start() {
        println("ImageProcessingQueue: Starting to process $totalCount images")
        if (totalCount == 0) {
            onComplete(emptyList())
            return
        }
        
        isProcessing = true
        processNextImage()
    }
    
    private fun processNextImage() {
        if (currentIndex >= totalCount || !isProcessing) {
            return
        }
        
        val index = currentIndex
        currentIndex++
        
        val pickerResult = pickerResults[index]

        pickerResult.itemProvider.loadFileRepresentationForTypeIdentifier(
            "public.image"
        ) { url, error ->
            processedCount++
            if (error != null || url == null) {
                println("ImageProcessingQueue: Failed to load image $index: ${error?.localizedDescription}")
                checkAndFinish()
                return@loadFileRepresentationForTypeIdentifier
            }
            val imageData = NSData.dataWithContentsOfURL(url)
            if (imageData == null) {
                checkAndFinish()
                return@loadFileRepresentationForTypeIdentifier
            }
            processImageDataInBackground(imageData, pickerResult, index)
        }
        processNextImage()
    }
    
    private fun processImageDataInBackground(imageData: NSData, pickerResult: PHPickerResult, index: Int) {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT.toLong(), 0u)) {
            var galleryResult: GalleryPhotoResult? = null
            autoreleasepool {
                try {
                    galleryResult = processImageFromData(imageData, pickerResult)
                } catch (e: Exception) {
                    println("ImageProcessingQueue: Error processing image $index: ${e.message}")
                    e.printStackTrace()
                }
            }
            
            dispatch_async(dispatch_get_main_queue()) {
                galleryResult?.let { 
                    results.add(it)
                }
                
                checkAndFinish()
            }
        }
    }
    private fun checkAndFinish() {
        if (processedCount >= totalCount) {
            isProcessing = false
            onComplete(results.toList())
        }
    }

    private fun processImageFromData(imageData: NSData, pickerResult: PHPickerResult): GalleryPhotoResult? {
        val uiImage = UIImage.imageWithData(imageData)
            ?: throw Exception("Failed to create UIImage from ${imageData.length} bytes of data")
        
        val processedData = ImageOptimizer.processImage(uiImage, compressionLevel)
            ?: throw Exception("Failed to process image")
        
        val tempURL = ImageProcessor.saveImageToTempDirectory(processedData)
            ?: throw Exception("Failed to save processed image")
        
        val finalImage = UIImage.imageWithData(processedData)
            ?: throw Exception("Failed to load processed image")
        
        val finalWidth = finalImage.size.useContents { width.toInt() }
        val finalHeight = finalImage.size.useContents { height.toInt() }
        val finalSizeKB = processedData.length.toLong() / 1024
        
        val exifData = if (includeExif) {
            extractExifDataFromOriginal(pickerResult, imageData)
        } else {
            null
        }
        
        return GalleryPhotoResult(
            uri = tempURL.absoluteString ?: "",
            width = finalWidth,
            height = finalHeight,
            fileName = tempURL.lastPathComponent,
            fileSize = finalSizeKB,
            mimeType = "${MimeType.IMAGE_WEBP}",
            exif = exifData
        )
    }
    private fun extractExifDataFromOriginal(pickerResult: PHPickerResult, originalData: NSData): ExifData? {
        return try {
            val assetIdentifier = pickerResult.assetIdentifier
            if (assetIdentifier != null) {
                val fetchResult = PHAsset.fetchAssetsWithLocalIdentifiers(
                    listOf(assetIdentifier),
                    null
                )
                val asset = fetchResult.firstObject() as? PHAsset
                asset?.let { ExifDataExtractor.extractExifDataFromAsset(it) }
            } else {
                println("No asset identifier available, skipping EXIF extraction")
                null
            }
        } catch (e: Exception) {
            println("EXIF extraction failed: ${e.message}")
            null
        }
    }
}
