package io.github.ismoy.imagepickerkmp.presentation.presenters

import io.github.ismoy.imagepickerkmp.data.delegates.GalleryDelegate
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UIViewController
import platform.UIKit.presentationController

internal object GalleryPresenter {
    private var galleryDelegate: GalleryDelegate? = null

    fun presentGallery(
        viewController: UIViewController,
        onPhotoSelected: (GalleryPhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        onDismiss: () -> Unit,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false,
        mimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL),
        mimeTypeMismatchMessage: String? = null
    ) {
        try {
            val imagePickerController = createImagePickerController(
                onPhotoSelected,
                onError,
                onDismiss,
                compressionLevel,
                includeExif,
                mimeTypes,
                mimeTypeMismatchMessage
            )
            val currentDelegate = galleryDelegate
            viewController.presentViewController(imagePickerController, animated = true) {
                currentDelegate?.let { d ->
                    imagePickerController.presentationController?.setDelegate(d)
                }
            }
        } catch (e: Exception) {
            onError(Exception("Failed to present gallery: ${e.message}"))
        }
    }

    private fun createImagePickerController(
        onPhotoSelected: (GalleryPhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        onDismiss: () -> Unit,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false,
        mimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL),
        mimeTypeMismatchMessage: String? = null
    ): UIImagePickerController {
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

        galleryDelegate = GalleryDelegate(
            onImagePicked = wrappedOnPhotoSelected,
            onDismiss = wrappedOnDismiss,
            compressionLevel = compressionLevel,
            includeExif = includeExif,
            allowedMimeTypes = mimeTypes,
            onError = wrappedOnError,
            mimeTypeMismatchMessage = mimeTypeMismatchMessage
        )

        return UIImagePickerController().apply {
            sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
            allowsEditing = false
            delegate = galleryDelegate
        }
    }
}
