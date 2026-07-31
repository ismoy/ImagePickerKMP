package io.github.ismoy.imagepickerkmp.camera

import io.github.ismoy.imagepickerkmp.ui.CameraPresenter
import io.github.ismoy.imagepickerkmp.ui.ViewControllerProvider
import io.github.ismoy.imagepickerkmp.picker.PhotoCaptureException
import io.github.ismoy.imagepickerkmp.picker.PhotoResult
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
internal object PhotoCaptureOrchestrator {
    fun launchCamera(
        onPhotoCaptured: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        onDismiss: () -> Unit,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false
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
                compressionLevel,
                includeExif
            )
        } catch (e: Exception) {
            onError(PhotoCaptureException("Failed to launch camera: ${e.message}"))
        }
    }
}
