package io.github.ismoy.imagepickerkmp.data.processors

import io.github.ismoy.imagepickerkmp.domain.exceptions.PhotoCaptureException
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSNumber
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.Foundation.temporaryDirectory
import platform.Foundation.writeToURL
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

/**
 * Handles image processing operations for iOS, including conversion, saving, and result creation.
 *
 * Provides methods to process images from the camera or gallery, convert them to JPEG,
 * save them to disk, and return result data classes for use in the library.
 */
@OptIn(ExperimentalForeignApi::class)
object ImageProcessor {

    fun processImage(image: UIImage, quality: Double = 0.9): PhotoResult {
        val jpegData = convertToJPEG(image, quality)
        val fileURL = saveImageToDisk(jpegData)
        return createCameraPhotoResult(image, fileURL)
    }

    fun processImageForGallery(image: UIImage, quality: Double = 0.9): GalleryPhotoResult {
        val jpegData = convertToJPEG(image, quality)
        val fileURL = saveImageToDisk(jpegData)
        return createGalleryPhotoResult(image, fileURL)
    }

    private fun convertToJPEG(image: UIImage, quality: Double): NSData {
        return UIImageJPEGRepresentation(image, quality)
            ?: throw PhotoCaptureException("Failed to convert image to JPEG")
    }

    private fun saveImageToDisk(jpegData: NSData): NSURL {
        val fileManager = NSFileManager.defaultManager
        val tempDirectoryURL = fileManager.temporaryDirectory
        val fileName = "${NSUUID().UUIDString()}.jpg"
        val fileURL = tempDirectoryURL.URLByAppendingPathComponent(fileName)!!
        val success = jpegData.writeToURL(fileURL, true)
        if (!success) {
            throw io.github.ismoy.imagepickerkmp.domain.exceptions.PhotoCaptureException("Failed to save image to disk")
        }
        return fileURL
    }

    private fun createCameraPhotoResult(image: UIImage, fileURL: NSURL): PhotoResult {
        val size = image.size
        val uri = fileURL.absoluteString
        if (uri.isNullOrEmpty()) {
            throw PhotoCaptureException("Failed to get valid URI for saved image")
        }
        return PhotoResult(
            uri = uri,
            width = size.useContents { width.toInt() },
            height = size.useContents { height.toInt() }
        )
    }

    private fun createGalleryPhotoResult(image: UIImage, fileURL: NSURL): GalleryPhotoResult {
        val size = image.size
        val uri = fileURL.absoluteString
        if (uri.isNullOrEmpty()) {
            throw PhotoCaptureException("Failed to get valid URI for saved image")
        }
        val fileSize = getFileSize(fileURL)
        return GalleryPhotoResult(
            uri = uri,
            width = size.useContents { width.toInt() },
            height = size.useContents { height.toInt() },
            fileName = fileURL.lastPathComponent,
            fileSize = fileSize
        )
    }

    private fun getFileSize(fileURL: NSURL): Long? {
        return try {
            val fileManager = NSFileManager.defaultManager
            val attrs = fileManager.attributesOfItemAtPath(fileURL.path!!, null)
            (attrs?.get("NSFileSize") as? NSNumber)?.longValue
        } catch (e: Exception) {
            println("Error getting file size: \\${e.message}")
            null
        }
    }
}
