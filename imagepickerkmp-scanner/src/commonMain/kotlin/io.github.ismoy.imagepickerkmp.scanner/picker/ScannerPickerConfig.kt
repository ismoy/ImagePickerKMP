package io.github.ismoy.imagepickerkmp.scanner.picker

import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig
import io.github.ismoy.imagepickerkmp.scanner.permission.ScannerPermissionConfig
import io.github.ismoy.imagepickerkmp.scanner.ui.ScannerUIExtensions

data class ScannerPickerConfig(
    val camera: ScannerCameraConfig = ScannerCameraConfig(),
    val permissions: ScannerPermissionConfig = ScannerPermissionConfig(),
    val uiExtensions: ScannerUIExtensions = ScannerUIExtensions()
) {
    companion object {
        fun default() = ScannerPickerConfig()
    }
}
