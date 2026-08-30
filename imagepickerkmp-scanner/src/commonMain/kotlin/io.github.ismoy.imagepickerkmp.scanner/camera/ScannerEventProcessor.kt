package io.github.ismoy.imagepickerkmp.scanner.camera

import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEvent
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEventListener
import io.github.ismoy.imagepickerkmp.scanner.domain.model.BarcodeData
import io.github.ismoy.imagepickerkmp.scanner.domain.model.CameraPositionDistance
import io.github.ismoy.imagepickerkmp.scanner.domain.model.FlashMode

class ScannerEventProcessor(
    private val onCodeScanned: (code: String, format: String?) -> Unit,
    private val onCameraError: (message: String) -> Unit,
    private val onPermissionResult: (granted: Boolean) -> Unit,
    private val onPermissionPermanentlyDenied: () -> Unit,
    private val onDistanceChanged: (distance: CameraPositionDistance) -> Unit,
    private val onFlashStateChanged: (flashMode: FlashMode) -> Unit,
    private val onCameraStateChanged: (state: ScannerCameraState) -> Unit,
    private val onZoomStateChanged: (min: Float, max: Float, current: Float) -> Unit,
    private val onBarcodesDetected: (barcodes: List<BarcodeData>) -> Unit
) : ScannerEventListener {

    override fun onEvent(event: ScannerEvent) {
        when (event) {
            is ScannerEvent.CodeScanned ->
                onCodeScanned(event.code, event.format)

            is ScannerEvent.CameraError ->
                onCameraError(event.error)

            is ScannerEvent.PermissionResult ->
                onPermissionResult(event.granted)

            is ScannerEvent.PermissionPermanentlyDenied ->
                onPermissionPermanentlyDenied()

            is ScannerEvent.DistanceChanged ->
                onDistanceChanged(event.distance)

            is ScannerEvent.FlashStateChanged -> {
                val mode = if (event.mode == "ON") FlashMode.ON else FlashMode.OFF
                onFlashStateChanged(mode)
            }

            is ScannerEvent.TorchAvailabilityChanged -> Unit

            is ScannerEvent.ScanningStarted ->
                onCameraStateChanged(ScannerCameraState.Scanning)

            is ScannerEvent.ScanningStopped ->
                onCameraStateChanged(ScannerCameraState.CameraReady)

            is ScannerEvent.ScanningPaused ->
                onCameraStateChanged(ScannerCameraState.Paused)

            is ScannerEvent.ScanningResumed ->
                onCameraStateChanged(ScannerCameraState.Scanning)

            is ScannerEvent.ZoomStateChanged ->
                onZoomStateChanged(event.minZoom, event.maxZoom, event.currentZoom)

            is ScannerEvent.BarcodesDetected ->
                onBarcodesDetected(event.barcodes)
        }
    }
}
