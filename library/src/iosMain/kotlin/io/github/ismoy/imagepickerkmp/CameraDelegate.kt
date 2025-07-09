package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.CameraPhotoHandler.PhotoResult
import platform.UIKit.UIImage
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject


class CameraDelegate(
    private val onPhotoCaptured: (PhotoResult) -> Unit,
    private val onError: (Exception) -> Unit
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
        dismissPicker(picker)
    }

    private fun processCapturedImage(image: UIImage, picker: UIImagePickerController) {
        try {
            val photoResult = ImageProcessor.processImage(image)
            onPhotoCaptured(photoResult)
        } catch (e: Exception) {
            onError(PhotoCaptureException("Failed to process image: ${e.message}"))
        } finally {
            dismissPicker(picker)
        }
    }

    private fun dismissPicker(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
    }
}