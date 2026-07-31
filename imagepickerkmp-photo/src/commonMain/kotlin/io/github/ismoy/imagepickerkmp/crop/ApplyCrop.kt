package io.github.ismoy.imagepickerkmp.crop

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.ismoy.imagepickerkmp.picker.PhotoResult

@Composable
expect fun ApplyCrop(
    photoResult: PhotoResult,
    cropRect: Rect,
    canvasSize: Size,
    isCircularCrop: Boolean,
    zoomLevel: Float = 1f,
    rotationAngle: Float = 0f,
    onComplete: (PhotoResult) -> Unit
)