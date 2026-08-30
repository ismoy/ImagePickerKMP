package io.github.ismoy.imagepickerkmp.scanner

import androidx.camera.core.Camera
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEvent
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEventManager
import io.github.ismoy.imagepickerkmp.scanner.camera.DefaultScannerCameraStateManager
import io.github.ismoy.imagepickerkmp.scanner.domain.model.FlashMode
import io.github.ismoy.imagepickerkmp.scanner.utils.LoggerFactory
import io.github.ismoy.imagepickerkmp.scanner.utils.camera

internal class AndroidScannerFlashManager(
    private val stateManager: DefaultScannerCameraStateManager,
    private val eventManager: ScannerEventManager
) {
    private val logger = LoggerFactory.getLogger()

    fun toggleFlash(camera: Camera?) {
        try {
            val currentUiState = stateManager.flashMode.value
            val newFlashMode = if (currentUiState == FlashMode.ON) "OFF" else "ON"
            camera?.cameraControl?.enableTorch(newFlashMode == "ON")
            stateManager.setFlashMode(newFlashMode)
            eventManager.emitEvent(ScannerEvent.FlashStateChanged(isEnabled = newFlashMode == "ON", mode = newFlashMode))
            logger.camera("Torch toggled to: $newFlashMode")
        } catch (e: Exception) {
            logger.error("CameraX", "Error toggling torch", e)
        }
    }

    fun checkTorchAvailability(camera: Camera?) {
        try {
            val hasFlashUnit = camera?.cameraInfo?.hasFlashUnit() ?: false
            logger.camera("Torch availability: $hasFlashUnit")
        } catch (e: Exception) {
            logger.error("CameraX", "Error checking torch availability", e)
        }
    }
}
