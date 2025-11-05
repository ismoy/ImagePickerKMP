package io.github.ismoy.imagepickerkmp.domain.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult

/**
 * WASM platform implementation of applyCrop.
 * Note: Image cropping functionality is not available in WASM platform.
 * This is a stub implementation for compatibility.
 */
@Composable
actual fun applyCrop(
    photoResult: PhotoResult,
    cropRect: Rect,
    canvasSize: Size,
    isCircularCrop: Boolean,
    onComplete: (PhotoResult) -> Unit
) {
    // Stub implementation - WASM doesn't support image cropping
    // Return the original photoResult unchanged
    onComplete(photoResult)
}
