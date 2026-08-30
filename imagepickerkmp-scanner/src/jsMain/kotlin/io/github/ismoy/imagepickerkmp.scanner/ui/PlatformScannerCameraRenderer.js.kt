package io.github.ismoy.imagepickerkmp.scanner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerCaptureManager

@Composable
actual fun PlatformScannerCameraRenderer(
    onPreviewViewReady: (io.github.ismoy.imagepickerkmp.scanner.PlatformScannerDependencies) -> Unit,
    scanner: ScannerCaptureManager?,
    modifier: Modifier,
    onUserInteraction: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize().background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Text("Scanner not supported on JS", color = Color.White)
    }
}
