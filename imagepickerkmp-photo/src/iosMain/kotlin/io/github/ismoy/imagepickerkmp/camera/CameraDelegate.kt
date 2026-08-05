package io.github.ismoy.imagepickerkmp.camera

import io.github.ismoy.imagepickerkmp.logger.PhotoLogger
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.PhotoCaptureException
import io.github.ismoy.imagepickerkmp.picker.PhotoResult
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.native.runtime.GC
import kotlin.native.runtime.NativeRuntimeApi

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
        picker.dismissViewControllerAnimated(true) {
            CoroutineScope(Dispatchers.Default).launch {
                processCapturedImage(image, picker)
            }
        }
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true) {
            onDismiss()
        }
    }

    @OptIn(NativeRuntimeApi::class, BetaInteropApi::class)
    private fun processCapturedImage(image: UIImage, picker: UIImagePickerController) {
        try {
            autoreleasepool {
                val processedData = if (compressionLevel != null) {
                    ImageProcessor.processImage(image, compressionLevel)
                } else {
                    UIImagePNGRepresentation(image)
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
                    dispatch_async(dispatch_get_main_queue()) {
                        onPhotoCaptured(photoResult)
                    }
                } else {
                    dispatch_async(dispatch_get_main_queue()) {
                        onError(PhotoCaptureException("Failed to save processed image"))
                    }
                }
            } else {
                dispatch_async(dispatch_get_main_queue()) {
                    onError(PhotoCaptureException("Failed to process image"))
                }
            }
            }
        } catch (e: Exception) {
            logDebug("Error processing image: ${e.message}")
            dispatch_async(dispatch_get_main_queue()) {
                onError(PhotoCaptureException("Failed to process image: ${e.message}"))
            }
        } finally {
            GC.collect()
        }
    }

    private fun logDebug(message: String) {
        PhotoLogger.debug("iOS CameraDelegate: $message")
    }

    private fun dismissPicker(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, null)
    }
}
