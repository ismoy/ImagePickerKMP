package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.GalleryPhotoHandler.PhotoResult
import io.github.ismoy.imagepickerkmp.ImagePickerUiConstants.SELECTION_LIMIT
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerViewController
import platform.UIKit.UIViewController

/**
 * Presents the PHPickerViewController for multiple photo selection on iOS 14+.
 *
 * This object manages the presentation and delegation of the PHPickerViewController.
 */
object PHPickerPresenter {
    private var pickerDelegate: PHPickerDelegate? = null

    fun presentGallery(
        viewController: UIViewController,
        onPhotoSelected: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        onDismiss: () -> Unit,
        selectionLimit: Long
    ) {
        require(selectionLimit <= SELECTION_LIMIT) {"Selection limit cannot exceed $SELECTION_LIMIT"}
        try {
            val pickerViewController = createPHPickerController(onPhotoSelected, onError, onDismiss, selectionLimit)
            viewController.presentViewController(pickerViewController,
                animated = true, completion = null)
        } catch (e: Exception) {
            onError(Exception("Failed to present PHPicker: ${e.message}"))
        }
    }

    private fun createPHPickerController(
        onPhotoSelected: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        onDismiss: () -> Unit,
        selectionLimit: Long
    ): PHPickerViewController {
        val configuration = PHPickerConfiguration()
        configuration.selectionLimit = selectionLimit
        configuration.filter = PHPickerFilter.imagesFilter

        val cleanup = { pickerDelegate = null }
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
        pickerDelegate = PHPickerDelegate(wrappedOnPhotoSelected, wrappedOnError, wrappedOnDismiss)
        return PHPickerViewController(configuration).apply {
            delegate = pickerDelegate
        }
    }
}



