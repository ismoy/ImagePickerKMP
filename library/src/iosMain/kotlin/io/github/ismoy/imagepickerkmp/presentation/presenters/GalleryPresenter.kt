package io.github.ismoy.imagepickerkmp.presentation.presenters

import io.github.ismoy.imagepickerkmp.data.delegates.GalleryDelegate
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
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
        onDismiss: () -> Unit,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false
    ) {
        try {
            val imagePickerController = createImagePickerController(
                onPhotoSelected,
                onDismiss,
                compressionLevel,
                includeExif
            )
            viewController.presentViewController(imagePickerController, animated = true, completion = null)
        } catch (e: Exception) {
            onError(Exception("Failed to present gallery: ${e.message}"))
        }
    }

    private fun createImagePickerController(
        onPhotoSelected: (GalleryPhotoResult) -> Unit,
        onDismiss: () -> Unit,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false
    ): UIImagePickerController {
        return UIImagePickerController().apply {
            sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
            allowsEditing = false

            val cleanup = { galleryDelegate = null }
            val wrappedOnPhotoSelected: (GalleryPhotoResult) -> Unit = { result ->
                onPhotoSelected(result)
                cleanup()
            }
            val wrappedOnDismiss: () -> Unit = {
                onDismiss()
                cleanup()
            }

            galleryDelegate = GalleryDelegate(wrappedOnPhotoSelected, wrappedOnDismiss, compressionLevel, includeExif)
            delegate = galleryDelegate
        }
    }
}
