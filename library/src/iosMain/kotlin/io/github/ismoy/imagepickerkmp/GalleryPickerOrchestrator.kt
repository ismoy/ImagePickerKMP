package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.GalleryPhotoHandler.PhotoResult

object GalleryPickerOrchestrator {
    fun launchGallery(
        onPhotoSelected: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val rootViewController = ViewControllerProvider.getRootViewController()
            if (rootViewController == null) {
                onError(Exception("Could not find root view controller"))
                return
            }
            GalleryPresenter.presentGallery(rootViewController, onPhotoSelected, onError)
        } catch (e: Exception) {
            onError(Exception("Failed to launch gallery: ${e.message}"))
        }
    }
} 