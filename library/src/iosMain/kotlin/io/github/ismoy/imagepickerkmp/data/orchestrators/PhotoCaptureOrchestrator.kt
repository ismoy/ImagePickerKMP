package io.github.ismoy.imagepickerkmp.data.orchestrators

import io.github.ismoy.imagepickerkmp.presentation.presenters.CameraPresenter
import io.github.ismoy.imagepickerkmp.domain.utils.ViewControllerProvider
import io.github.ismoy.imagepickerkmp.domain.exceptions.PhotoCaptureException
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel

/**
 * Orchestrates the presentation and handling of the camera interface on iOS.
 *
 * Provides a method to launch the camera and handle photo capture or errors.
 */
object PhotoCaptureOrchestrator {
    fun launchCamera(
        onPhotoCaptured: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        onDismiss: () -> Unit,
        compressionLevel: CompressionLevel? = null
    ) {
        try {
            val rootViewController = ViewControllerProvider.getRootViewController()
            if (rootViewController == null) {
                onError(PhotoCaptureException("Could not find root view controller"))
                return
            }
            CameraPresenter.presentCamera(
                rootViewController,
                onPhotoCaptured,
                onError,
                onDismiss,
                compressionLevel
            )
        } catch (e: Exception) {
            onError(PhotoCaptureException("Failed to launch camera: ${e.message}"))
        }
    }
}
