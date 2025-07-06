package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.CameraPhotoHandler.PhotoResult
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.Foundation.temporaryDirectory
import platform.Foundation.writeToURL
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

@OptIn(ExperimentalForeignApi::class)
object ImageProcessor {

    fun processImage(image: UIImage): PhotoResult {
        val jpegData = convertToJPEG(image)
        val fileURL = saveImageToDisk(jpegData)
        return createPhotoResult(image, fileURL)
    }

    private fun convertToJPEG(image: UIImage): NSData {
        return UIImageJPEGRepresentation(image, 0.9)
            ?: throw PhotoCaptureException("Failed to convert image to JPEG")
    }

    private fun saveImageToDisk(jpegData: NSData): NSURL {
        val fileManager = NSFileManager.defaultManager
        val tempDirectoryURL = fileManager.temporaryDirectory
        val fileName = "${NSUUID().UUIDString()}.jpg"
        val fileURL = tempDirectoryURL.URLByAppendingPathComponent(fileName)!!
        val success = jpegData.writeToURL(fileURL, true)
        if (!success) {
            throw PhotoCaptureException("Failed to save image to disk")
        }
        return fileURL
    }

    private fun createPhotoResult(image: UIImage, fileURL: NSURL): PhotoResult {
        val size = image.size
        val uri = fileURL.absoluteString
        if (uri.isNullOrEmpty()) {
            throw PhotoCaptureException("Failed to get valid URI for saved image")
        }

        val result = PhotoResult(
            uri = uri,
            width = size.useContents { width.toInt() },
            height = size.useContents { height.toInt() }
        )
        return result
    }
}