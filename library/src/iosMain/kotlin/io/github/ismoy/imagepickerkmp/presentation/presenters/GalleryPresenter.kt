package io.github.ismoy.imagepickerkmp.presentation.presenters

import io.github.ismoy.imagepickerkmp.data.delegates.GalleryDelegate
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UIViewController

/**
 * Presents the gallery picker interface on iOS and handles photo selection results.
 *
 * This object manages the presentation and delegation of the gallery view controller.
 */
object GalleryPresenter {
    private var galleryDelegate: GalleryDelegate? = null

    fun presentGallery(
        viewController: UIViewController,
        onPhotoSelected: (GalleryPhotoResult) -> Unit,
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
        onPhotoSelected: (GalleryPhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        onDismiss: () -> Unit
    ): UIImagePickerController {
        return UIImagePickerController().apply {
            sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
            allowsEditing = false

            val cleanup = { galleryDelegate = null }
            val wrappedOnPhotoSelected: (GalleryPhotoResult) -> Unit = { result ->
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
            galleryDelegate =
                GalleryDelegate(wrappedOnPhotoSelected, wrappedOnError, wrappedOnDismiss)
            delegate = galleryDelegate
        }
    }
}
