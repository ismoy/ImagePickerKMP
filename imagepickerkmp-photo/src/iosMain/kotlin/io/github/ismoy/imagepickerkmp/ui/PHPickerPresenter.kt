package io.github.ismoy.imagepickerkmp.ui

import io.github.ismoy.imagepickerkmp.core.CoreServices
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionStatus
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionType
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.SELECTION_LIMIT
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.picker.MimeType
import io.github.ismoy.imagepickerkmp.ui.showPermissionDeniedDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            CoroutineScope(Dispatchers.Main).launch {
                val status = CoreServices.permissionManager().requestPermission(PermissionType.Gallery)
                if (status is PermissionStatus.Granted) {
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
                } else {
                    showPermissionDeniedDialog(viewController, onDismiss)
                }
            }
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



