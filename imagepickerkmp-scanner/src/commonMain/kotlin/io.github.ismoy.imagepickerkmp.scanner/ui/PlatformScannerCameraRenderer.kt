package io.github.ismoy.imagepickerkmp.scanner.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.ismoy.imagepickerkmp.scanner.PlatformScannerDependencies
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerCaptureManager

@Composable
expect fun PlatformScannerCameraRenderer(
    onPreviewViewReady: (PlatformScannerDependencies) -> Unit,
    scanner: ScannerCaptureManager?,
    modifier: Modifier,
    onUserInteraction: () -> Unit
)