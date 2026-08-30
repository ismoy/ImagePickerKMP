package io.github.ismoy.imagepickerkmp.scanner

import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEvent
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEventManager
import io.github.ismoy.imagepickerkmp.scanner.camera.DefaultScannerCameraStateManager
import io.github.ismoy.imagepickerkmp.scanner.utils.LoggerFactory
import io.github.ismoy.imagepickerkmp.scanner.utils.camera
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureTorchModeOff
import platform.AVFoundation.AVCaptureTorchModeOn
import platform.AVFoundation.hasTorch
import platform.AVFoundation.torchMode

internal class IOSScannerFlashManager(
    private val stateManager: DefaultScannerCameraStateManager,
    private val eventManager: ScannerEventManager,
    private val getCameraDevice: () -> AVCaptureDevice?
) {
    private val logger = LoggerFactory.getLogger()

    @OptIn(ExperimentalForeignApi::class)
    fun toggleFlash() {
        try {
            val device = getCameraDevice() ?: return
            if (device.hasTorch) {
                device.lockForConfiguration(null)
                val isCurrentlyOn = device.torchMode == AVCaptureTorchModeOn
                val newMode = if (isCurrentlyOn) {
                    AVCaptureTorchModeOff
                } else {
                    AVCaptureTorchModeOn
                }
                device.torchMode = newMode
                device.unlockForConfiguration()

                val flashStateStr = if (isCurrentlyOn) "OFF" else "ON"
                stateManager.setFlashMode(flashStateStr)
                eventManager.emitEvent(ScannerEvent.FlashStateChanged(isEnabled = !isCurrentlyOn, mode = flashStateStr))
                logger.camera("Torch toggled to: $flashStateStr")
            }
        } catch (e: Exception) {
            logger.error("Camera", "Error toggling torch: ${e.message}", e)
        }
    }
}
