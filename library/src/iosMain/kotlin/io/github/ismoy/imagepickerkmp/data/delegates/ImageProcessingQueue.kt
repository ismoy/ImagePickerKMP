package io.github.ismoy.imagepickerkmp.data.delegates

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
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
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
    private val allowedMimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL),
    private val onComplete: (results: List<GalleryPhotoResult>, mismatchedCount: Int) -> Unit,
    private val onError: (Exception) -> Unit
) {

    private val results = mutableListOf<GalleryPhotoResult>()
    private var processedCount = 0
    private var successCount = 0
    private var mismatchedCount = 0
    private var currentIndex = 0
    private val totalCount = pickerResults.size
    private var isProcessing = false

    fun start() {
        println("ImageProcessingQueue: Starting to process $totalCount images")
        if (totalCount == 0) {
            onComplete(emptyList(), 0)
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

        // For GIFs, request the original GIF file directly to preserve animation.
        // "public.image" causes iOS to transcode GIFs into a static JPEG/PNG frame.
        val isGifRequest = pickerResult.itemProvider.hasItemConformingToTypeIdentifier("com.compuserve.gif")
        val typeIdentifier = if (isGifRequest) "com.compuserve.gif" else "public.image"
        pickerResult.itemProvider.loadFileRepresentationForTypeIdentifier(
            typeIdentifier
        ) { url, error ->
            if (error != null || url == null) {
                dispatch_async(dispatch_get_main_queue()) {
                    processedCount++
                    checkAndFinish()
                }
                return@loadFileRepresentationForTypeIdentifier
            }

            if (!urlMatchesMimeTypes(url, allowedMimeTypes)) {
                dispatch_async(dispatch_get_main_queue()) {
                    mismatchedCount++
                    processedCount++
                    checkAndFinish()
                }
                return@loadFileRepresentationForTypeIdentifier
            }

            val imageData = NSData.dataWithContentsOfURL(url)
            if (imageData == null) {
                dispatch_async(dispatch_get_main_queue()) {
                    processedCount++
                    checkAndFinish()
                }
                return@loadFileRepresentationForTypeIdentifier
            }
            // GIFs must bypass the JPEG compression pipeline to preserve animation frames.
            if (isGifRequest) {
                processGifDataInBackground(imageData, pickerResult, index)
            } else {
                processImageDataInBackground(imageData, pickerResult, index)
            }
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
                    galleryResult = createFallbackResult(imageData, pickerResult, index)
                }
            }

            dispatch_async(dispatch_get_main_queue()) {
                galleryResult?.let {
                    results.add(it)
                    successCount++
                }
                processedCount++
                checkAndFinish()
            }
        }
    }

    /**
     * Saves animated GIF bytes as-is, bypassing the JPEG compression pipeline.
     * Transcoding a GIF through UIImage/UIImageJPEGRepresentation discards all animation
     * frames and produces a static image.
     */
    private fun processGifDataInBackground(imageData: NSData, pickerResult: PHPickerResult, index: Int) {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT.toLong(), 0u)) {
            var galleryResult: GalleryPhotoResult? = null
            autoreleasepool {
                try {
                    galleryResult = processGifFromData(imageData, pickerResult)
                } catch (e: Exception) {
                    // Fall back to static processing rather than dropping the result entirely
                    galleryResult = createFallbackResult(imageData, pickerResult, index)
                }
            }

            dispatch_async(dispatch_get_main_queue()) {
                galleryResult?.let {
                    results.add(it)
                    successCount++
                }
                processedCount++
                checkAndFinish()
            }
        }
    }

    private fun checkAndFinish() {
        if (processedCount >= totalCount) {
            isProcessing = false
            println("ImageProcessingQueue: Completed. Selected: $totalCount, Processed: $successCount, Mismatched: $mismatchedCount")
            onComplete(results.toList(), mismatchedCount)
        }
    }

    private fun urlMatchesMimeTypes(url: NSURL, allowedMimeTypes: List<MimeType>): Boolean {
        if (allowedMimeTypes.any { it == MimeType.IMAGE_ALL }) return true

        val pathExtension = url.pathExtension?.lowercase() ?: return true
        val actualMimeType = extensionToMimeType(pathExtension)

        return allowedMimeTypes.any { allowed ->
            when {
                allowed.value.endsWith("/*") -> actualMimeType.startsWith(allowed.value.removeSuffix("*"))
                else -> actualMimeType.equals(allowed.value, ignoreCase = true)
            }
        }
    }

    private fun extensionToMimeType(extension: String): String {
        return when (extension) {
            "jpg", "jpeg" -> "image/jpeg"
            "png"         -> "image/png"
            "gif"         -> "image/gif"
            "webp"        -> "image/webp"
            "bmp"         -> "image/bmp"
            "heic"        -> "image/heic"
            "heif"        -> "image/heif"
            "pdf"         -> "application/pdf"
            else          -> "image/$extension"
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

    /**
     * Saves animated GIF bytes directly to disk, bypassing the JPEG/WebP compression pipeline.
     *
     * Transcoding GIF data through UIImage + UIImageJPEGRepresentation discards all animation
     * frames and produces a static image. Instead, the original GIF bytes are written verbatim
     * to a temporary .gif file so that the URI retained in [GalleryPhotoResult] points to a
     * valid animated GIF that can be displayed by any GIF-capable renderer.
     */
    private fun processGifFromData(imageData: NSData, pickerResult: PHPickerResult): GalleryPhotoResult? {
        val fileName = "${NSUUID().UUIDString}.gif"
        val tempURL = ImageProcessor.saveDataToTempDirectory(imageData, fileName)
            ?: throw Exception("Failed to save GIF to temp directory")
        // Read dimensions from the first frame via UIImage (does not affect the saved file)
        val previewImage = UIImage.imageWithData(imageData)
        val width = previewImage?.size?.useContents { width.toInt() } ?: 0
        val height = previewImage?.size?.useContents { height.toInt() } ?: 0
        return GalleryPhotoResult(
            uri = tempURL.absoluteString ?: "",
            width = width,
            height = height,
            fileName = fileName,
            fileSize = imageData.length.toLong() / 1024,
            mimeType = MimeType.IMAGE_GIF.value,
            exif = null  // EXIF is not meaningful for GIFs
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

    private fun createFallbackResult(imageData: NSData, pickerResult: PHPickerResult, index: Int): GalleryPhotoResult? {
        return try {
            println("ImageProcessingQueue: Attempting fallback processing for image $index")

            val assetIdentifier = pickerResult.assetIdentifier
            if (assetIdentifier != null) {
                val fetchResult = PHAsset.fetchAssetsWithLocalIdentifiers(
                    listOf(assetIdentifier),
                    null
                )
                val asset = fetchResult.firstObject() as? PHAsset

                asset?.let { phAsset ->
                    val tempURL = ImageProcessor.saveDataToTempDirectory(imageData, "fallback_$index.jpg")

                    if (tempURL != null) {
                        val exifData = if (includeExif) {
                            try {
                                ExifDataExtractor.extractExifDataFromAsset(phAsset)
                            } catch (e: Exception) {
                                null
                            }
                        } else {
                            null
                        }

                        GalleryPhotoResult(
                            uri = tempURL.absoluteString ?: "",
                            width = phAsset.pixelWidth.toInt(),
                            height = phAsset.pixelHeight.toInt(),
                            fileName = tempURL.lastPathComponent ?: "fallback_$index.jpg",
                            fileSize = imageData.length.toLong() / 1024,
                            mimeType = "image/jpeg",
                            exif = exifData
                        )
                    } else {
                        println("ImageProcessingQueue: Failed to save fallback image $index")
                        null
                    }
                }
            } else {
                println("ImageProcessingQueue: No asset identifier for fallback image $index")
                null
            }
        } catch (e: Exception) {
            println("ImageProcessingQueue: Fallback also failed for image $index: ${e.message}")
            null
        }
    }
}
