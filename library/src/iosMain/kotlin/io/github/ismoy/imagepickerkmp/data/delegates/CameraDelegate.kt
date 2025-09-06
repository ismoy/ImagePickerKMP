package io.github.ismoy.imagepickerkmp.data.delegates

import io.github.ismoy.imagepickerkmp.data.processors.ImageProcessor
import io.github.ismoy.imagepickerkmp.domain.exceptions.PhotoCaptureException
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
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
 * Delegate for handling UIImagePickerController events and photo capture results on iOS.
 *
 * This class processes captured images and communicates results or errors to the caller.
 */
@OptIn(ExperimentalForeignApi::class)
class CameraDelegate(
    private val onPhotoCaptured: (PhotoResult) -> Unit,
    private val onError: (Exception) -> Unit,
    private val onDismiss: () -> Unit,
    private val compressionLevel: CompressionLevel? = null
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
            ?: run {
                onError(PhotoCaptureException("No image captured"))
                dismissPicker(picker)
                return
            }
        processCapturedImage(image, picker)
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        onDismiss()
        dismissPicker(picker)
    }

    private fun processCapturedImage(image: UIImage, picker: UIImagePickerController) {
        try {
            logDebug("Processing captured image...")
            logDebug("CompressionLevel received: $compressionLevel")
            
            val processedData = if (compressionLevel != null) {
                logDebug("Using compression with level: $compressionLevel")
                ImageProcessor.processImage(image, compressionLevel)
            } else {
                logDebug("No compression - using original quality")
                UIImageJPEGRepresentation(image, 1.0)
            }
            
            if (processedData != null) {
                val tempURL = ImageProcessor.saveImageToTempDirectory(processedData)
                if (tempURL != null) {
                    val fileSizeInBytes = processedData.length.toLong()
                    val fileSizeInKB = bytesToKB(fileSizeInBytes)
                    val photoResult = PhotoResult(
                        uri = tempURL.absoluteString ?: "",
                        width = image.size.useContents { width.toInt() },
                        height = image.size.useContents { height.toInt() },
                        fileName = tempURL.lastPathComponent,
                        fileSize = fileSizeInKB
                    )
                    logDebug("Final result - Image saved to: ${photoResult.uri}")
                    logDebug("Final result - File size: ${fileSizeInKB}KB (${fileSizeInBytes} bytes)")
                    onPhotoCaptured(photoResult)
                } else {
                    onError(PhotoCaptureException("Failed to save processed image"))
                }
            } else {
                onError(PhotoCaptureException("Failed to process image"))
            }
        } catch (e: Exception) {
            logDebug("Error processing image: ${e.message}")
            onError(PhotoCaptureException("Failed to process image: ${e.message}"))
        } finally {
            dismissPicker(picker)
        }
    }

    private fun bytesToKB(bytes: Long): Long = maxOf(1L, bytes / 1024)

    private fun logDebug(message: String) {
        println("iOS CameraDelegate: $message")
    }

    private fun dismissPicker(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, null)
    }
}
