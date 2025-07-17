package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.GalleryPhotoHandler.PhotoResult
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
        onError: (Exception) -> Unit
    ) {
        try {
            val pickerViewController = createPHPickerController(onPhotoSelected, onError)
            viewController.presentViewController(pickerViewController,
                animated = true, completion = null)
        } catch (e: Exception) {
            onError(Exception("Failed to present PHPicker: ${e.message}"))
        }
    }

    private fun createPHPickerController(
        onPhotoSelected: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit
    ): PHPickerViewController {
        val configuration = PHPickerConfiguration()
        configuration.selectionLimit = 0
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
        pickerDelegate = PHPickerDelegate(wrappedOnPhotoSelected, wrappedOnError)
        return PHPickerViewController(configuration).apply {
            delegate = pickerDelegate
        }
    }
}



