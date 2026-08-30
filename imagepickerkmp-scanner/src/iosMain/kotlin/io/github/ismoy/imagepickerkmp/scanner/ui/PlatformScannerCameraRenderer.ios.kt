package io.github.ismoy.imagepickerkmp.scanner.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import io.github.ismoy.imagepickerkmp.scanner.PlatformScannerDependencies
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerCaptureManager
import platform.UIKit.UIView

@Composable
actual fun PlatformScannerCameraRenderer(
    onPreviewViewReady: (PlatformScannerDependencies) -> Unit,
    scanner: ScannerCaptureManager?,
    modifier: Modifier,
    onUserInteraction: () -> Unit
) {
    val containerView = remember {
        UIView()
    }

    UIKitView(factory = {
        onPreviewViewReady(PlatformScannerDependencies(previewView = containerView))
        containerView
    },
        modifier = modifier.fillMaxSize(),
        onRelease = {}, properties = UIKitInteropProperties(isInteractive = true,
            isNativeAccessibilityEnabled = true))
}
