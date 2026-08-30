package io.github.ismoy.imagepickerkmp.scanner.capture

interface ScannerLifecycle {
    fun startScanning()
    fun stopScanning()
    fun pauseScanning()
    fun resumeScanning()
}

interface ScannerCameraControls {
    fun toggleFlash()

    /** Applies a relative zoom multiplier, for example `1.2f` to increase by 20%. */
    fun setZoom(scale: Float)

    /** Sets the platform zoom control using a normalized progress value in `0f..1f`. */
    fun setZoomProgress(progress: Float)

    fun setFocus(x: Float, y: Float)
}

interface ScannerCaptureManager : ScannerLifecycle, ScannerCameraControls
