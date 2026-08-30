package io.github.ismoy.imagepickerkmp.scanner.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerCaptureManager

@SuppressLint("ClickableViewAccessibility")
@Composable
actual fun PlatformScannerCameraRenderer(
    onPreviewViewReady: (io.github.ismoy.imagepickerkmp.scanner.PlatformScannerDependencies) -> Unit,
    scanner: ScannerCaptureManager?,
    modifier: Modifier,
    onUserInteraction: () -> Unit
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context: Context ->
            PreviewView(context).also { previewView ->
                onPreviewViewReady(
                io.github.ismoy.imagepickerkmp.scanner.PlatformScannerDependencies(
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    previewView = previewView
                )
            )
            }
        },
        update = { previewView ->
            previewView.setOnTouchListener { _, _ ->
                onUserInteraction()
                false
            }
        }
    )
}
