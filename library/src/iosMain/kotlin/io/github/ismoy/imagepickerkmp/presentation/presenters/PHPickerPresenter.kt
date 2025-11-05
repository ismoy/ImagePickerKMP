package io.github.ismoy.imagepickerkmp.presentation.presenters

import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.SELECTION_LIMIT
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.presentation.ui.components.showPermissionDeniedDialog
import platform.UIKit.UIViewController

/**
 * Presents the PHPickerViewController for multiple photo selection on iOS 14+.
 *
 * This object manages the presentation and delegation of the PHPickerViewController.
 */
object PHPickerPresenter {
    fun presentGallery(
        viewController: UIViewController,
        onPhotoSelected: (GalleryPhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        onDismiss: () -> Unit,
        selectionLimit: Long,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false,
        onPhotosSelected: ((List<GalleryPhotoResult>) -> Unit)? = null
    ) {
        require(selectionLimit <= SELECTION_LIMIT) {"Selection limit cannot exceed $SELECTION_LIMIT"}
        
        if (includeExif) {
            checkPhotoLibraryPermission(
                onAuthorized = {
                    presentPickerViewController(
                        viewController,
                        onPhotoSelected,
                        onError,
                        onDismiss,
                        selectionLimit,
                        compressionLevel,
                        includeExif,
                        onPhotosSelected
                    )
                },
                onDenied = {
                    showPermissionDeniedDialog(viewController, onDismiss)
                }
            )
        } else {
            presentPickerViewController(
                viewController,
                onPhotoSelected,
                onError,
                onDismiss,
                selectionLimit,
                compressionLevel,
                includeExif,
                onPhotosSelected
            )
        }
    }
}



