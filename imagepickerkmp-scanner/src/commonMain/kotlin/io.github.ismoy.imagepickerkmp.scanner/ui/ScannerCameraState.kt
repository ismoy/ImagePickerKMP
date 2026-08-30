package io.github.ismoy.imagepickerkmp.scanner.ui

import io.github.ismoy.imagepickerkmp.scanner.domain.model.FlashMode

interface ScannerCameraUIState {
    val isScanning: Boolean
    val flashMode: FlashMode
    val lastScannedCode: String?

    fun toggleFlash()
    fun pauseScanning()
    fun resumeScanning()
    fun stopScanning()
}
