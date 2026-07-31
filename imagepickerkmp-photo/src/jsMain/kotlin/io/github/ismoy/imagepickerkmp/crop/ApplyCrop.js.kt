package io.github.ismoy.imagepickerkmp.crop

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.ismoy.imagepickerkmp.picker.PhotoResult

@Composable
actual fun ApplyCrop(
    photoResult: PhotoResult,
    cropRect: Rect,
    canvasSize: Size,
    isCircularCrop: Boolean,
    zoomLevel: Float,
    rotationAngle: Float,
    onComplete: (PhotoResult) -> Unit
) {
    // TODO: Implementar recorte usando HTML5 Canvas
    onComplete(photoResult)
}
