package io.github.ismoy.imagepickerkmp.scanner.utils.extensions

import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEvent
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEventManager
import io.github.ismoy.imagepickerkmp.scanner.utils.LoggerFactory
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.videoZoomFactor

@OptIn(ExperimentalForeignApi::class)
internal fun AVCaptureDevice.updateZoomByProgress(progress: Float, eventManager: ScannerEventManager) {
    try {
        lockForConfiguration(null)
        val maxZoom = minOf(activeFormat.videoMaxZoomFactor, 5.0)
        val minZoom = 1.0
        val newZoom = minZoom + (maxZoom - minZoom) * progress
        videoZoomFactor = newZoom
        unlockForConfiguration()

        eventManager.emitEvent(ScannerEvent.ZoomStateChanged(
            minZoom = minZoom.toFloat(),
            maxZoom = maxZoom.toFloat(),
            currentZoom = newZoom.toFloat()
        ))
    } catch (e: Exception) {
        LoggerFactory.getLogger().error("Camera", "Error setting zoom progress: ${e.message}", e)
    }
}