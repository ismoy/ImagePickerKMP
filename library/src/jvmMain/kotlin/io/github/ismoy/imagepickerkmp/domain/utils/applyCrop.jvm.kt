package io.github.ismoy.imagepickerkmp.domain.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult

/**
 * Desktop implementation of applyCrop.
 * Crop functionality is not implemented for Desktop.
 * This is a stub implementation that returns the original result.
 */
@Composable
actual fun applyCrop(
    photoResult: PhotoResult,
    cropRect: Rect,
    canvasSize: Size,
    isCircularCrop: Boolean,
    onComplete: (PhotoResult) -> Unit
) {
    // Crop functionality is not available on Desktop
    // For now, just return the original result
    onComplete(photoResult)
}
