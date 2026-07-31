package io.github.ismoy.imagepickerkmp.ui

import io.github.ismoy.imagepickerkmp.core.CoreServices
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionStatus
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionType
import io.github.ismoy.imagepickerkmp.gallery.GalleryDelegate
import io.github.ismoy.imagepickerkmp.picker.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.MimeType
import io.github.ismoy.imagepickerkmp.ui.showPermissionDeniedDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UIViewController
import platform.UIKit.presentationController

internal object GalleryPresenter {
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
        PHPickerPresenter.presentGallery(
            viewController = viewController,
            onPhotoSelected = onPhotoSelected,
            onError = onError,
            onDismiss = onDismiss,
            selectionLimit = 1,
            compressionLevel = compressionLevel,
            includeExif = includeExif,
            mimeTypes = mimeTypes,
            mimeTypeMismatchMessage = mimeTypeMismatchMessage,
            onPhotosSelected = null
        )
    }
}
