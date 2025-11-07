package io.github.ismoy.imagepickerkmp.presentation.presenters

import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import platform.UIKit.UIViewController

fun presentPickerViewController(
    viewController: UIViewController,
    onPhotoSelected: (GalleryPhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    selectionLimit: Long,
    compressionLevel: CompressionLevel? = null,
    includeExif: Boolean = false,
    onPhotosSelected: ((List<GalleryPhotoResult>) -> Unit)? = null
) {
    try {
        val pickerViewController = createPHPickerController(
            onPhotoSelected,
            onError,
            onDismiss,
            selectionLimit,
            compressionLevel,
            includeExif,
            onPhotosSelected
        )
        viewController.presentViewController(pickerViewController,
            animated = true, completion = null)
    } catch (e: Exception) {
        onError(Exception("Failed to present PHPicker: ${e.message}"))
    }
}