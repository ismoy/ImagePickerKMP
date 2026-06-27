package io.github.ismoy.imagepickerkmp.data.delegates

import io.github.ismoy.imagepickerkmp.data.processors.ImageProcessor
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
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
import platform.PhotosUI.PHPickerResult
import platform.UIKit.UIImage
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue


/**
 * Processes images selected from PHPicker with **bounded parallelism** to balance
 * speed and memory usage.
 *
 * Up to [MAX_CONCURRENT] images are loaded and processed simultaneously. Each image
 * is decoded, resized, compressed, saved to disk, and released within an autoreleasepool
 * before its slot is freed for the next image. This keeps peak memory bounded to
 * approximately MAX_CONCURRENT images worth of data (~45-60MB) regardless of how many
 * images are selected.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal class ImageProcessingQueue(
    private val pickerResults: List<PHPickerResult>,
    private val compressionLevel: CompressionLevel?,
    private val includeExif: Boolean,
    private val allowedMimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL),
    private val onComplete: (results: List<GalleryPhotoResult>, mismatchedCount: Int) -> Unit,
    private val onError: (Exception) -> Unit
) {

    companion object {
        // Process up to 3 images concurrently — good balance of speed vs memory on iOS devices
        private const val MAX_CONCURRENT = 3
    }

    private val results = mutableListOf<GalleryPhotoResult>()
    private var processedCount = 0
    private var successCount = 0
    private var mismatchedCount = 0
    private var nextIndex = 0
    private var activeCount = 0
    private val totalCount = pickerResults.size

    fun start() {
        if (totalCount == 0) {
            onComplete(emptyList(), 0)
            return
        }
        // Kick off initial batch
        val initialBatch = minOf(MAX_CONCURRENT, totalCount)
        for (i in 0 until initialBatch) {
            loadNextImage()
        }
    }

    /**
     * Loads one image from the item provider and begins processing it.
     * Called from the main queue to manage concurrency.
     */
    private fun loadNextImage() {
        if (nextIndex >= totalCount) return

        val index = nextIndex
        nextIndex++
        activeCount++

        val pickerResult = pickerResults[index]

        val isGifRequest = pickerResult.itemProvider.hasItemConformingToTypeIdentifier("com.compuserve.gif")
        val typeIdentifier = if (isGifRequest) "com.compuserve.gif" else "public.image"

        pickerResult.itemProvider.loadFileRepresentationForTypeIdentifier(
            typeIdentifier
        ) { url, error ->
            // NOTE: `url` points to a temporary file that iOS deletes as soon as this
            // completion handler returns, so all reads of it must happen synchronously here.
            if (error != null || url == null) {
                onImageDone(null)
                return@loadFileRepresentationForTypeIdentifier
            }

            if (!urlMatchesMimeTypes(url, allowedMimeTypes)) {
                dispatch_async(dispatch_get_main_queue()) {
                    mismatchedCount++
                    activeCount--
                    processedCount++
                    advanceOrFinish()
                }
                return@loadFileRepresentationForTypeIdentifier
            }

            var galleryResult: GalleryPhotoResult? = null
            autoreleasepool {
                try {
                    galleryResult = if (isGifRequest) {
                        // GIFs must preserve raw bytes to keep animation frames
                        processGifFromURL(url)
                    } else {
                        // ImageIO reads only the bytes needed for downsampling, directly from disk.
                        processImageFromURL(url)
                    }
                } catch (_: Exception) {
                    autoreleasepool {
                        galleryResult = createFallbackResultFromURL(url, index)
                    }
                }
            }
            onImageDone(galleryResult)
        }
    }

    private fun onImageDone(result: GalleryPhotoResult?) {
        dispatch_async(dispatch_get_main_queue()) {
            result?.let {
                results.add(it)
                successCount++
            }
            activeCount--
            processedCount++
            advanceOrFinish()
        }
    }

    /**
     * Called on main queue after each image completes. Starts the next image if there
     * are more to process, or finalizes when all are done.
     */
    private fun advanceOrFinish() {
        if (processedCount >= totalCount) {
            onComplete(results.toList(), mismatchedCount)
        } else if (nextIndex < totalCount) {
            // Fill the slot that was just freed
            loadNextImage()
        }
    }

    // ─── Image Processing ────────────────────────────────────────────────────────

    private fun processImageFromURL(url: NSURL): GalleryPhotoResult? {
        // ImageIO downsamples directly from disk — no full-resolution bitmap or full-file
        // NSData is ever allocated.
        val processed = ImageOptimizer.processImageFromURL(url, compressionLevel)
            ?: throw Exception("Failed to process image")

        val tempURL = ImageProcessor.saveImageToTempDirectory(processed.data)
            ?: throw Exception("Failed to save processed image")

        val exifData = if (includeExif) {
            ExifDataExtractor.extractExifData(url.path ?: "")
        } else {
            null
        }

        return GalleryPhotoResult(
            uri = tempURL.absoluteString ?: "",
            width = processed.width,
            height = processed.height,
            fileName = tempURL.lastPathComponent,
            fileSize = processed.data.length.toLong() / 1024,
            mimeType = "image/jpeg",
            exif = exifData
        )
    }

    private fun processGifFromURL(url: NSURL): GalleryPhotoResult? {
        // GIFs need their raw bytes preserved verbatim to keep animation frames.
        val gifData = NSData.dataWithContentsOfURL(url)
            ?: throw Exception("Failed to read GIF data")

        val fileName = "${NSUUID().UUIDString}.gif"
        val tempURL = ImageProcessor.saveDataToTempDirectory(gifData, fileName)
            ?: throw Exception("Failed to save GIF to temp directory")

        var width = 0
        var height = 0
        autoreleasepool {
            val previewImage = UIImage.imageWithData(gifData)
            width = previewImage?.size?.useContents { this.width.toInt() } ?: 0
            height = previewImage?.size?.useContents { this.height.toInt() } ?: 0
        }

        return GalleryPhotoResult(
            uri = tempURL.absoluteString ?: "",
            width = width,
            height = height,
            fileName = fileName,
            fileSize = gifData.length.toLong() / 1024,
            mimeType = MimeType.IMAGE_GIF.value,
            exif = null
        )
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

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

    private fun createFallbackResultFromURL(url: NSURL, index: Int): GalleryPhotoResult? {
        return try {
            val imageData = NSData.dataWithContentsOfURL(url) ?: return null
            val tempURL = ImageProcessor.saveDataToTempDirectory(imageData, "fallback_$index.jpg")

            if (tempURL != null) {
                var width = 0
                var height = 0
                autoreleasepool {
                    val img = UIImage.imageWithData(imageData)
                    width = img?.size?.useContents { this.width.toInt() } ?: 0
                    height = img?.size?.useContents { this.height.toInt() } ?: 0
                }

                GalleryPhotoResult(
                    uri = tempURL.absoluteString ?: "",
                    width = width,
                    height = height,
                    fileName = tempURL.lastPathComponent ?: "fallback_$index.jpg",
                    fileSize = imageData.length.toLong() / 1024,
                    mimeType = "image/jpeg",
                    exif = null
                )
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}
