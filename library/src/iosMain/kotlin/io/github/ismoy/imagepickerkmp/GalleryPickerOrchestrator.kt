package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.GalleryPhotoHandler.PhotoResult

/**
 * Orchestrates the presentation and handling of the gallery picker on iOS.
 *
 * Provides a method to launch the gallery and handle photo selection or errors.
 */
object GalleryPickerOrchestrator {
    fun launchGallery(
        onPhotoSelected: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        allowMultiple: Boolean = false
    ) {
        try {
            val rootViewController = ViewControllerProvider.getRootViewController()
            if (rootViewController == null) {
                onError(Exception("Could not find root view controller"))
                return
            }
            if (allowMultiple) {
                PHPickerPresenter.presentGallery(rootViewController, onPhotoSelected, onError)
            } else {
                GalleryPresenter.presentGallery(rootViewController, onPhotoSelected, onError)
            }
        } catch (e: Exception) {
            onError(Exception("Failed to launch gallery: ${e.message}"))
        }
    }
}
