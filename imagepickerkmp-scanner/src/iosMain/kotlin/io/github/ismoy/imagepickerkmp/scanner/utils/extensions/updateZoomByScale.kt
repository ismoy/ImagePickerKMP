package io.github.ismoy.imagepickerkmp.scanner.utils.extensions

import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEvent
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEventManager
import io.github.ismoy.imagepickerkmp.scanner.utils.LoggerFactory
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.videoZoomFactor

@OptIn(ExperimentalForeignApi::class)
internal fun AVCaptureDevice.updateZoomByScale(scale: Float, eventManager: ScannerEventManager) {
    try {
        lockForConfiguration(null)
        val currentZoom = videoZoomFactor
        val maxZoom = activeFormat.videoMaxZoomFactor
        var newZoom = currentZoom * scale
        newZoom = maxOf(1.0, minOf(newZoom, maxZoom))
        videoZoomFactor = newZoom
        unlockForConfiguration()

        val maxZoomVal = minOf(activeFormat.videoMaxZoomFactor, 5.0)
        eventManager.emitEvent(ScannerEvent.ZoomStateChanged(
            minZoom = 1.0f,
            maxZoom = maxZoomVal.toFloat(),
            currentZoom = newZoom.toFloat()
        ))
    } catch (e: Exception) {
        LoggerFactory.getLogger().error("Camera", "Error setting zoom: ${e.message}", e)
    }
}