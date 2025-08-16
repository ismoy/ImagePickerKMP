package io.github.ismoy.imagepickerkmp.data.delegates

import io.github.ismoy.imagepickerkmp.data.processors.ImageProcessor
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import platform.UIKit.UIImage
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject

/**
 * Delegate for handling UIImagePickerController events and photo selection results on iOS.
 *
 * This class processes selected images from the gallery and communicates results or errors to the caller.
 */
class GalleryDelegate(
    private val onPhotoSelected: (GalleryPhotoResult) -> Unit,
    private val onError: (Exception) -> Unit,
    private val onDismiss: () -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
            ?: run {
                onError(Exception("No image selected"))
                dismissPicker(picker)
                return
            }
        processSelectedImage(image, picker)
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        onDismiss()
        dismissPicker(picker)
    }

    private fun processSelectedImage(image: UIImage, picker: UIImagePickerController) {
        try {
            val photoResult = ImageProcessor.processImageForGallery(image)
            onPhotoSelected(photoResult)
        } catch (e: Exception) {
            onError(Exception("Failed to process image: ${e.message}"))
        } finally {
            dismissPicker(picker)
        }
    }

    private fun dismissPicker(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
    }
}
