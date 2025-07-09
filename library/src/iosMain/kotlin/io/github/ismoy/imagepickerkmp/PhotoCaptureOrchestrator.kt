package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.CameraPhotoHandler.PhotoResult

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