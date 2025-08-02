package io.github.ismoy.imagepickerkmp

import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UIViewController
import io.github.ismoy.imagepickerkmp.GalleryPhotoHandler.PhotoResult

/**
 * Presents the gallery picker interface on iOS and handles photo selection results.
 *
 * This object manages the presentation and delegation of the gallery view controller.
 */
object GalleryPresenter {
    private var galleryDelegate: GalleryDelegate? = null

    fun presentGallery(
        viewController: UIViewController,
        onPhotoSelected: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        onDismiss: () -> Unit
    ) {
        try {
            val imagePickerController = createImagePickerController(onPhotoSelected, onError, onDismiss)
            viewController.presentViewController(imagePickerController, animated = true, completion = null)
        } catch (e: Exception) {
            onError(Exception("Failed to present gallery: ${e.message}"))
        }
    }

    private fun createImagePickerController(
        onPhotoSelected: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        onDismiss: () -> Unit
    ): UIImagePickerController {
        return UIImagePickerController().apply {
            sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
            allowsEditing = false

            val cleanup = { galleryDelegate = null }
            val wrappedOnPhotoSelected: (PhotoResult) -> Unit = { result ->
                onPhotoSelected(result)
                cleanup()
            }
            val wrappedOnError: (Exception) -> Unit = { error ->
                onError(error)
                cleanup()
            }
            val wrappedOnDismiss: () -> Unit = {
                onDismiss()
                cleanup()
            }
            galleryDelegate = GalleryDelegate(wrappedOnPhotoSelected, wrappedOnError, wrappedOnDismiss)
            delegate = galleryDelegate
        }
    }
}
