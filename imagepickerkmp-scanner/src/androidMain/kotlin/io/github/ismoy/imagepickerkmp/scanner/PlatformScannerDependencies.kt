package io.github.ismoy.imagepickerkmp.scanner

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner

actual class PlatformScannerDependencies(
    val context: Context,
    val lifecycleOwner: LifecycleOwner,
    val previewView: PreviewView
)
