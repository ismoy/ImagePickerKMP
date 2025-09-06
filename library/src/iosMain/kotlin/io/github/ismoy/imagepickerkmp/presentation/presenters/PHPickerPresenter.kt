package io.github.ismoy.imagepickerkmp.presentation.presenters

import io.github.ismoy.imagepickerkmp.data.delegates.PHPickerDelegate
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.SELECTION_LIMIT
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
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
        onPhotoSelected: (GalleryPhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        onDismiss: () -> Unit,
        selectionLimit: Long,
        compressionLevel: CompressionLevel? = null
    ) {
        require(selectionLimit <= SELECTION_LIMIT) {"Selection limit cannot exceed $SELECTION_LIMIT"}
        try {
            val pickerViewController = createPHPickerController(
                onPhotoSelected,
                onError,
                onDismiss,
                selectionLimit,
                compressionLevel
            )
            viewController.presentViewController(pickerViewController,
                animated = true, completion = null)
        } catch (e: Exception) {
            onError(Exception("Failed to present PHPicker: ${e.message}"))
        }
    }

    private fun createPHPickerController(
        onPhotoSelected: (GalleryPhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        onDismiss: () -> Unit,
        selectionLimit: Long,
        compressionLevel: CompressionLevel? = null
    ): PHPickerViewController {
        val configuration = PHPickerConfiguration()
        configuration.selectionLimit = selectionLimit
        configuration.filter = PHPickerFilter.imagesFilter

        val cleanup = { pickerDelegate = null }
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
        pickerDelegate = PHPickerDelegate(
            wrappedOnPhotoSelected,
            wrappedOnError,
            wrappedOnDismiss,
            compressionLevel
        )
        return PHPickerViewController(configuration).apply {
            delegate = pickerDelegate
        }
    }
}



