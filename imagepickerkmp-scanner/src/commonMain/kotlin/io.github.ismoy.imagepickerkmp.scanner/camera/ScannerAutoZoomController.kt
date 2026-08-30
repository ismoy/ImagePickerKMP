package io.github.ismoy.imagepickerkmp.scanner.camera

import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerCaptureManager
import io.github.ismoy.imagepickerkmp.scanner.domain.model.BarcodeData
import io.github.ismoy.imagepickerkmp.scanner.utils.getCurrentTimeMillis

/**
 * Requests small relative zoom changes only after the previous request has been
 * acknowledged by the platform zoom state, preventing a frame-by-frame zoom storm.
 */
internal class ScannerAutoZoomController(
    private val config: ScannerCameraConfig
) {
    private var pendingZoomTarget: Float? = null
    private var lastZoomRequestAt = 0L

    fun handleAutoZoom(
        barcodes: List<BarcodeData>,
        currentZoom: Float,
        maxZoom: Float,
        scanner: ScannerCaptureManager?
    ) {
        if (!config.advanced.enableAutoZoom || barcodes.isEmpty() || currentZoom >= maxZoom) {
            return
        }

        val captureManager = scanner ?: return
        val now = getCurrentTimeMillis()
        pendingZoomTarget?.let { target ->
            if (currentZoom >= target - ZOOM_ACKNOWLEDGEMENT_EPSILON) {
                pendingZoomTarget = null
            } else if (now - lastZoomRequestAt < MIN_REQUEST_INTERVAL_MILLIS) {
                return
            }
        }

        val largest = barcodes.maxByOrNull {
            it.boundingBox?.let { bounds -> bounds.width * bounds.height } ?: 0f
        }
        val bounds = largest?.boundingBox ?: return
        val sourceArea = bounds.sourceWidth * bounds.sourceHeight
        if (sourceArea <= 0f) return

        val areaRatio = (bounds.width * bounds.height) / sourceArea
        if (areaRatio >= config.behavior.areaRatioThreshold) return

        val targetZoom = (currentZoom + ZOOM_INCREMENT).coerceAtMost(maxZoom)
        if (targetZoom <= currentZoom) return

        // ScannerCaptureManager#setZoom accepts a relative multiplier, not an
        // absolute zoom ratio. Passing targetZoom directly would compound zoom.
        captureManager.setZoom(targetZoom / currentZoom.coerceAtLeast(MIN_ZOOM_RATIO))
        pendingZoomTarget = targetZoom
        lastZoomRequestAt = now
    }

    private companion object {
        const val ZOOM_INCREMENT = 0.5f
        const val MIN_ZOOM_RATIO = 0.01f
        const val ZOOM_ACKNOWLEDGEMENT_EPSILON = 0.05f
        const val MIN_REQUEST_INTERVAL_MILLIS = 500L
    }
}
