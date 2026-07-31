package io.github.ismoy.imagepickerkmp.gallery

import io.github.ismoy.imagepickerkmp.ui.GalleryPresenter
import io.github.ismoy.imagepickerkmp.ui.ViewControllerProvider
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.SELECTION_LIMIT
import io.github.ismoy.imagepickerkmp.picker.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.MimeType
import io.github.ismoy.imagepickerkmp.ui.PHPickerPresenter

/**
 * Orchestrates the presentation and handling of the gallery picker on iOS.
 *
 * Provides a method to launch the gallery and handle photo selection or errors.
 */
internal object GalleryPickerOrchestrator {
    fun launchGallery(
        onPhotoSelected: (GalleryPhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        onDismiss: () -> Unit,
        allowMultiple: Boolean = false,
        selectionLimit: Long = SELECTION_LIMIT,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false,
        mimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL),
        mimeTypeMismatchMessage: String? = null,
        onPhotosSelected: ((List<GalleryPhotoResult>) -> Unit)? = null
    ) {
        try {
            val rootViewController = ViewControllerProvider.getRootViewController()
            if (rootViewController == null) {
                onError(Exception("Could not find root view controller"))
                return
            }
            if (allowMultiple) {
                PHPickerPresenter.presentGallery(
                    rootViewController,
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
                GalleryPresenter.presentGallery(
                    rootViewController,
                    onPhotoSelected,
                    onError,
                    onDismiss,
                    compressionLevel,
                    includeExif,
                    mimeTypes,
                    mimeTypeMismatchMessage
                )
            }
        } catch (e: Exception) {
            onError(Exception("Failed to launch gallery: ${e.message}"))
        }
    }
}
