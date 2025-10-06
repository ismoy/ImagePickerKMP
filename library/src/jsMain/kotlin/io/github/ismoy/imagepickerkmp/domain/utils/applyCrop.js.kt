package io.github.ismoy.imagepickerkmp.domain.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult

@Composable
actual fun applyCrop(
    photoResult: PhotoResult,
    cropRect: Rect,
    canvasSize: Size,
    isCircularCrop: Boolean,
    onComplete: (PhotoResult) -> Unit
) {
    // En JS/Web, el recorte se puede implementar usando Canvas API
    // Por ahora devolvemos el resultado original
    // TODO: Implementar recorte usando HTML5 Canvas
    onComplete(photoResult)
}
