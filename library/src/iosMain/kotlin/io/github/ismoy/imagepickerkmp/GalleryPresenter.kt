package io.github.ismoy.imagepickerkmp

import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UIViewController
import io.github.ismoy.imagepickerkmp.GalleryPhotoHandler.PhotoResult

object GalleryPresenter {
    private var galleryDelegate: GalleryDelegate? = null

    fun presentGallery(
        viewController: UIViewController,
        onPhotoSelected: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val imagePickerController = createImagePickerController(onPhotoSelected, onError)
            viewController.presentViewController(imagePickerController, animated = true, completion = null)
        } catch (e: Exception) {
            onError(Exception("Failed to present gallery: ${e.message}"))
        }
    }

    private fun createImagePickerController(
        onPhotoSelected: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit
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
            galleryDelegate = GalleryDelegate(wrappedOnPhotoSelected, wrappedOnError)
            delegate = galleryDelegate
        }
    }
} 