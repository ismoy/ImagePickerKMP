package io.github.ismoy.imagepickerkmp.scanner

import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerCaptureManager
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEventManager
import io.github.ismoy.imagepickerkmp.scanner.camera.DefaultScannerCameraStateManager
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig

expect fun createScannerCaptureManager(
    dependencies: PlatformScannerDependencies,
    config: ScannerCameraConfig,
    eventManager: ScannerEventManager,
    stateManager: DefaultScannerCameraStateManager
): ScannerCaptureManager
