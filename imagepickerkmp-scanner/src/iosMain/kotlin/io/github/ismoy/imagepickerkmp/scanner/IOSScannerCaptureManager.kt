package io.github.ismoy.imagepickerkmp.scanner

import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerCaptureManager
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEventManager
import io.github.ismoy.imagepickerkmp.scanner.camera.DefaultScannerCameraStateManager
import io.github.ismoy.imagepickerkmp.scanner.utils.extensions.getBackCameraDevice
import platform.UIKit.UIView

class IOSScannerCaptureManager(
    previewView: UIView,
    config: ScannerCameraConfig,
    eventManager: ScannerEventManager,
    stateManager: DefaultScannerCameraStateManager
) : ScannerCaptureManager {

    private val soundManager = IOSScannerSoundManager(config)
    private val flashManager = IOSScannerFlashManager(stateManager, eventManager) { getBackCameraDevice() }

    private val binder = IOSCameraBinder(
        previewView = previewView,
        config = config,
        eventManager = eventManager,
        stateManager = stateManager,
        soundManager = soundManager
    )

    override fun startScanning() = binder.startScanning()
    override fun stopScanning() = binder.stopScanning()
    override fun pauseScanning() = binder.pauseScanning()
    override fun resumeScanning() = binder.resumeScanning()

    override fun toggleFlash() = flashManager.toggleFlash()
    override fun setZoom(scale: Float) = binder.setZoom(scale)
    override fun setZoomProgress(progress: Float) = binder.setZoomProgress(progress)
    override fun setFocus(x: Float, y: Float) = binder.setFocus(x, y)
}
