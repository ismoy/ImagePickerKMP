package io.github.ismoy.imagepickerkmp.scanner

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerCaptureManager
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEventManager
import io.github.ismoy.imagepickerkmp.scanner.camera.DefaultScannerCameraStateManager

internal class AndroidScannerCaptureManager(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    config: ScannerCameraConfig,
    eventManager: ScannerEventManager,
    stateManager: DefaultScannerCameraStateManager,
) : ScannerCaptureManager {

    private val soundManager = AndroidScannerSoundManager(context, config)
    private val flashManager = AndroidScannerFlashManager(stateManager, eventManager)

    private val cameraBinder = AndroidCameraXBinder(
        context = context,
        lifecycleOwner = lifecycleOwner,
        previewView = previewView,
        config = config,
        eventManager = eventManager,
        stateManager = stateManager,
        soundManager = soundManager,
        flashManager = flashManager
    )

    override fun startScanning() = cameraBinder.startScanning()
    override fun pauseScanning() = cameraBinder.pauseScanning()
    override fun resumeScanning() = cameraBinder.resumeScanning()
    override fun stopScanning() = cameraBinder.stopScanning()

    override fun toggleFlash() = flashManager.toggleFlash(cameraBinder.camera)
    override fun setZoom(scale: Float) = cameraBinder.setZoom(scale)
    override fun setZoomProgress(progress: Float) = cameraBinder.setZoomProgress(progress)
    override fun setFocus(x: Float, y: Float) = cameraBinder.setFocus(x, y)
}
