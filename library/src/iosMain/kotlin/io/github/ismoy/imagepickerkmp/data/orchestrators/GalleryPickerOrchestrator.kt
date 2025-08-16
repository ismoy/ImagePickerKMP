package io.github.ismoy.imagepickerkmp.data.orchestrators

import io.github.ismoy.imagepickerkmp.presentation.presenters.GalleryPresenter
import io.github.ismoy.imagepickerkmp.domain.utils.ViewControllerProvider
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.SELECTION_LIMIT
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.presentation.presenters.PHPickerPresenter

/**
 * Orchestrates the presentation and handling of the gallery picker on iOS.
 *
 * Provides a method to launch the gallery and handle photo selection or errors.
 */
object GalleryPickerOrchestrator {
    fun launchGallery(
        onPhotoSelected: (GalleryPhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        onDismiss: () -> Unit,
        allowMultiple: Boolean = false,
        selectionLimit: Long = SELECTION_LIMIT
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
                    selectionLimit
                )
            } else {
                GalleryPresenter.presentGallery(rootViewController, onPhotoSelected, onError, onDismiss)
            }
        } catch (e: Exception) {
            onError(Exception("Failed to launch gallery: ${e.message}"))
        }
    }
}
