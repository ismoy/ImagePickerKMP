package io.github.ismoy.imagepickerkmp.camera

import io.github.ismoy.imagepickerkmp.camera.ImageProcessor
import io.github.ismoy.imagepickerkmp.picker.PhotoCaptureException
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.PhotoResult
import io.github.ismoy.imagepickerkmp.logger.PhotoLogger
import io.github.ismoy.imagepickerkmp.camera.ExifDataExtractor
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIImage
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
internal class CameraDelegate(
    private val onPhotoCaptured: (PhotoResult) -> Unit,
    private val onError: (Exception) -> Unit,
    private val onDismiss: () -> Unit,
    private val compressionLevel: CompressionLevel? = null,
    private val includeExif: Boolean = false
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
        picker.dismissViewControllerAnimated(true) {
            onDismiss()
        }
    }

    private fun processCapturedImage(image: UIImage, picker: UIImagePickerController) {
        try {
            val processedData = if (compressionLevel != null) {
                ImageProcessor.processImage(image, compressionLevel)
            } else {
                UIImageJPEGRepresentation(image, 1.0)
            }
            
            if (processedData != null) {
                val tempURL = ImageProcessor.saveImageToTempDirectory(processedData)
                if (tempURL != null) {
                    val fileSizeInBytes = processedData.length.toLong()
                    val exifData = if (includeExif) {
                        val path = tempURL.path ?: ""
                        ExifDataExtractor.extractExifData(path)
                    } else {
                        logDebug(" EXIF extraction disabled")
                        null
                    }
                    
                    val photoResult = PhotoResult(
                        uri = tempURL.absoluteString ?: "",
                        width = image.size.useContents { width.toInt() },
                        height = image.size.useContents { height.toInt() },
                        fileName = tempURL.lastPathComponent,
                        fileSize = fileSizeInBytes,
                        exif = exifData
                    )
                    // Dismiss the picker FIRST, then deliver the result in the completion
                    // handler. This prevents "Unbalanced calls to begin/end appearance
                    // transitions" when crop opens a Dialog immediately after capture.
                    picker.dismissViewControllerAnimated(true) {
                        onPhotoCaptured(photoResult)
                    }
                } else {
                    onError(PhotoCaptureException("Failed to save processed image"))
                    dismissPicker(picker)
                }
            } else {
                onError(PhotoCaptureException("Failed to process image"))
                dismissPicker(picker)
            }
        } catch (e: Exception) {
            logDebug("Error processing image: ${e.message}")
            onError(PhotoCaptureException("Failed to process image: ${e.message}"))
            dismissPicker(picker)
        }
    }

    private fun logDebug(message: String) {
        PhotoLogger.debug("iOS CameraDelegate: $message")
    }

    private fun dismissPicker(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, null)
    }
}
