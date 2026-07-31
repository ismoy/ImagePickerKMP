package io.github.ismoy.imagepickerkmp.ui

import io.github.ismoy.imagepickerkmp.gallery.PHPickerDelegate
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.picker.MimeType
import platform.UIKit.UIAdaptivePresentationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.UIKit.presentationController

internal fun presentPickerViewController(
    viewController: UIViewController,
    onPhotoSelected: (GalleryPhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    selectionLimit: Long,
    compressionLevel: CompressionLevel? = null,
    includeExif: Boolean = false,
    mimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL),
    mimeTypeMismatchMessage: String? = null,
    onPhotosSelected: ((List<GalleryPhotoResult>) -> Unit)? = null
) {
    try {
        val pickerViewController = createPHPickerController(
            onPhotoSelected,
            onError,
            onDismiss,
            selectionLimit,
            compressionLevel,
            includeExif,
            mimeTypes,
            mimeTypeMismatchMessage,
            onPhotosSelected
        )
        val pickerDelegate = pickerViewController.delegate as? PHPickerDelegate
        viewController.presentViewController(pickerViewController, animated = true) {
            pickerDelegate?.let { d ->
                pickerViewController.presentationController
                    ?.setDelegate(d as UIAdaptivePresentationControllerDelegateProtocol)
            }
        }
    } catch (e: Exception) {
        onError(Exception("Failed to present PHPicker: ${e.message}"))
    }
}