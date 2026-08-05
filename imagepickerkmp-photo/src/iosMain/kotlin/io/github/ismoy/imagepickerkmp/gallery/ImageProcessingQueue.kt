package io.github.ismoy.imagepickerkmp.gallery

import io.github.ismoy.imagepickerkmp.camera.ImageProcessor
import io.github.ismoy.imagepickerkmp.camera.ImageOptimizer
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.picker.MimeType
import io.github.ismoy.imagepickerkmp.camera.ExifDataExtractor
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.useContents
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.Foundation.dataWithContentsOfURL
import platform.PhotosUI.PHPickerResult
import kotlin.native.runtime.GC
import kotlin.native.runtime.NativeRuntimeApi
import platform.UIKit.UIImage
import platform.darwin.dispatch_async
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

    companion object {
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
        val initialBatch = minOf(MAX_CONCURRENT, totalCount)
        for (i in 0 until initialBatch) {
            loadNextImage()
        }
    }

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
                        processGifFromURL(url)
                    } else {
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

    @OptIn(NativeRuntimeApi::class)
    private fun advanceOrFinish() {
        if (processedCount >= totalCount) {
            onComplete(results.toList(), mismatchedCount)
            GC.collect()
        } else if (nextIndex < totalCount) {
            loadNextImage()
        }
    }

    private fun processImageFromURL(url: NSURL): GalleryPhotoResult? {
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
            fileSize = processed.data.length.toLong(),
            mimeType = "image/jpeg",
            exif = exifData
        )
    }

    private fun processGifFromURL(url: NSURL): GalleryPhotoResult? {
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
            fileSize = gifData.length.toLong(),
            mimeType = MimeType.IMAGE_GIF.value,
            exif = null
        )
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
                    fileSize = imageData.length.toLong(),
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
