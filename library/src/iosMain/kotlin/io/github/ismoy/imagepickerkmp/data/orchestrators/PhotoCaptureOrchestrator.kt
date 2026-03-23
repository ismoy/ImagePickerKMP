package io.github.ismoy.imagepickerkmp.data.orchestrators

import io.github.ismoy.imagepickerkmp.presentation.presenters.CameraPresenter
import io.github.ismoy.imagepickerkmp.domain.utils.ViewControllerProvider
import io.github.ismoy.imagepickerkmp.domain.exceptions.PhotoCaptureException
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
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
