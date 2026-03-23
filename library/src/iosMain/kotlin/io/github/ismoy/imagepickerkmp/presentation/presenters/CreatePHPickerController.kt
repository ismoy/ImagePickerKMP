package io.github.ismoy.imagepickerkmp.presentation.presenters

import io.github.ismoy.imagepickerkmp.data.delegates.PHPickerDelegate
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import platform.Photos.PHPhotoLibrary
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerViewController

private var strongPickerDelegate: PHPickerDelegate? = null

internal fun createPHPickerController(
    onPhotoSelected: (GalleryPhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    selectionLimit: Long,
    compressionLevel: CompressionLevel? = null,
    includeExif: Boolean = false,
    mimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL),
    mimeTypeMismatchMessage: String? = null,
    onPhotosSelected: ((List<GalleryPhotoResult>) -> Unit)? = null
): PHPickerViewController {
    val configuration = PHPickerConfiguration(
        photoLibrary = PHPhotoLibrary.sharedPhotoLibrary()
    )
    configuration.selectionLimit = selectionLimit
    configuration.filter = PHPickerFilter.imagesFilter

    val cleanup = {
        strongPickerDelegate = null
    }
    val wrappedOnPhotoSelected: (GalleryPhotoResult) -> Unit = { result ->
        onPhotoSelected(result)
        cleanup()
    }
    val wrappedOnPhotosSelected: ((List<GalleryPhotoResult>) -> Unit)? = if (onPhotosSelected != null) {
        { results ->
            onPhotosSelected(results)
            cleanup()
        }
    } else null

    val wrappedOnError: (Exception) -> Unit = { error ->
        onError(error)
        cleanup()
    }
    val wrappedOnDismiss: () -> Unit = {
        onDismiss()
        cleanup()
    }

    strongPickerDelegate = PHPickerDelegate(
        wrappedOnPhotoSelected,
        wrappedOnPhotosSelected,
        wrappedOnError,
        wrappedOnDismiss,
        compressionLevel,
        includeExif,
        mimeTypes,
        mimeTypeMismatchMessage
    )

    val pickerDelegate = strongPickerDelegate ?: return PHPickerViewController(configuration).apply {
        delegate = null
    }

    return DismissalAwarePHPickerViewController.createPickerViewController(configuration, pickerDelegate).apply {
        delegate = pickerDelegate
    }
}
