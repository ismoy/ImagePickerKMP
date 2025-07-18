package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.CameraPhotoHandler.PhotoResult

/**
 * Orchestrates the presentation and handling of the camera interface on iOS.
 *
 * Provides a method to launch the camera and handle photo capture or errors.
 */
object PhotoCaptureOrchestrator {
    fun launchCamera(
        onPhotoCaptured: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val rootViewController = ViewControllerProvider.getRootViewController()
            if (rootViewController == null) {
                onError(PhotoCaptureException("Could not find root view controller"))
                return
            }
            CameraPresenter.presentCamera(rootViewController, onPhotoCaptured, onError)
        } catch (e: Exception) {
            onError(PhotoCaptureException("Failed to launch camera: ${e.message}"))
        }
    }
}
