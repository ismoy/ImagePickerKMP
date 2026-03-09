package io.github.ismoy.imagepickerkmp.presentation.presenters

import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.SELECTION_LIMIT
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import io.github.ismoy.imagepickerkmp.presentation.ui.components.showPermissionDeniedDialog
import platform.UIKit.UIViewController


internal object PHPickerPresenter {
    fun presentGallery(
        viewController: UIViewController,
        onPhotoSelected: (GalleryPhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        onDismiss: () -> Unit,
        selectionLimit: Long,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean,
        mimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL),
        mimeTypeMismatchMessage: String? = null,
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
                        mimeTypes,
                        mimeTypeMismatchMessage,
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
                mimeTypes,
                mimeTypeMismatchMessage,
                onPhotosSelected
            )
        }
    }
}



