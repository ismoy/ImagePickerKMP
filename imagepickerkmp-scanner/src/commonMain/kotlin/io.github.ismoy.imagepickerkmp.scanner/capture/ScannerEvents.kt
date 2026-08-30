package io.github.ismoy.imagepickerkmp.scanner.capture

import io.github.ismoy.imagepickerkmp.scanner.domain.model.BarcodeData
import io.github.ismoy.imagepickerkmp.scanner.domain.model.CameraPositionDistance

sealed class ScannerEvent {
    data class CodeScanned(val code: String, val format: String? = null) : ScannerEvent()
    data class DistanceChanged(val distance: CameraPositionDistance) : ScannerEvent()
    data class CameraError(val error: String) : ScannerEvent()
    data class PermissionResult(val granted: Boolean) : ScannerEvent()
    data class PermissionPermanentlyDenied(val reason: String) : ScannerEvent()
    data class FlashStateChanged(val isEnabled: Boolean, val mode: String) : ScannerEvent()
    data class TorchAvailabilityChanged(val isAvailable: Boolean) : ScannerEvent()
    object ScanningStarted : ScannerEvent()
    object ScanningStopped : ScannerEvent()
    object ScanningPaused : ScannerEvent()
    object ScanningResumed : ScannerEvent()
    data class ZoomStateChanged(val minZoom: Float, val maxZoom: Float, val currentZoom: Float) : ScannerEvent()
    data class BarcodesDetected(val barcodes: List<BarcodeData>) : ScannerEvent()
}

interface ScannerEventListener {
    fun onEvent(event: ScannerEvent)
}

class ScannerEventManager {
    private val listeners = mutableSetOf<ScannerEventListener>()

    fun addListener(listener: ScannerEventListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ScannerEventListener) {
        listeners.remove(listener)
    }

    /**
     * Delivers through a snapshot so a listener can safely subscribe or unsubscribe
     * while an event is being dispatched.
     */
    fun emitEvent(event: ScannerEvent) {
        listeners.toList().forEach { it.onEvent(event) }
    }

    fun clear() {
        listeners.clear()
    }
}
