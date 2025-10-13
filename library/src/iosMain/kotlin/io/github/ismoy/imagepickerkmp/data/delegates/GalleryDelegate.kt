package io.github.ismoy.imagepickerkmp.data.delegates

import io.github.ismoy.imagepickerkmp.data.processors.ImageProcessor
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIImage
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject

/**
 * Delegate for handling UIImagePickerController events and photo selection results on iOS.
 *
 * This class processes selected images from the gallery and communicates results or errors to the caller.
 */
@OptIn(ExperimentalForeignApi::class)
class GalleryDelegate(
    private val onImagePicked: (GalleryPhotoResult) -> Unit,
    private val onDismiss: () -> Unit,
    private val compressionLevel: CompressionLevel? = null
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        if (image != null) {
            processSelectedImage(image, picker)
        } else {
            dismissPicker(picker)
        }
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        onDismiss()
        dismissPicker(picker)
    }

    private fun processSelectedImage(image: UIImage, picker: UIImagePickerController) {
        try {
            logDebug("Processing selected image...")
            logDebug("CompressionLevel received: $compressionLevel")
            
            val processedData = if (compressionLevel != null) {
                logDebug("Using compression with level: $compressionLevel")
                ImageProcessor.processImageForGallery(image, compressionLevel)
            } else {
                logDebug("No compression - using original quality")
                UIImageJPEGRepresentation(image, 1.0)
            }
            
            if (processedData != null) {
                val tempURL = ImageProcessor.saveImageToTempDirectory(processedData)
                if (tempURL != null) {
                    val fileSizeInBytes = processedData.length.toLong()
                    val fileSizeInKB = bytesToKB(fileSizeInBytes)
                    val galleryResult = GalleryPhotoResult(
                        uri = tempURL.absoluteString ?: "",
                        width = image.size.useContents { width.toInt() },
                        height = image.size.useContents { height.toInt() },
                        fileName = tempURL.lastPathComponent,
                        fileSize = fileSizeInKB
                    )
                    logDebug("Final result - File size: ${fileSizeInKB}KB (${fileSizeInBytes} bytes)")
                    onImagePicked(galleryResult)
                } else {
                    logDebug("Error: Failed to save image to temp directory")
                }
            } else {
                logDebug("Error: Failed to process image data")
            }
        } catch (e: Exception) {
            logDebug("Error processing image: ${e.message}")
        } finally {
            dismissPicker(picker)
        }
    }

    private fun bytesToKB(bytes: Long): Long = maxOf(1L, bytes / 1024)

    private fun logDebug(message: String) {
        println(" iOS GalleryDelegate: $message")
    }

    private fun dismissPicker(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, null)
    }
}
