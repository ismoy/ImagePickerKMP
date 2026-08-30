package io.github.ismoy.imagepickerkmp.scanner

import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerCaptureManager
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEventManager
import io.github.ismoy.imagepickerkmp.scanner.camera.DefaultScannerCameraStateManager

actual fun createScannerCaptureManager(
    dependencies: PlatformScannerDependencies,
    config: ScannerCameraConfig,
    eventManager: ScannerEventManager,
    stateManager: DefaultScannerCameraStateManager
): ScannerCaptureManager {
    return IOSScannerCaptureManager(
        previewView = dependencies.previewView,
        config = config,
        eventManager = eventManager,
        stateManager = stateManager
    )
}
