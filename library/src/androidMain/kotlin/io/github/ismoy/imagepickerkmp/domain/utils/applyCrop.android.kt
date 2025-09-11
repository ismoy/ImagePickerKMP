package io.github.ismoy.imagepickerkmp.domain.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult

@Composable
actual fun applyCrop(
    photoResult: PhotoResult,
    cropRect: Rect,
    canvasSize: Size,
    isCircularCrop: Boolean,
    onComplete: (PhotoResult) -> Unit
) {
    val context = LocalContext.current
    
    applyCropUtils(
        context = context,
        photoResult = photoResult,
        cropRect = cropRect,
        canvasSize = canvasSize,
        isCircularCrop = isCircularCrop,
        onComplete = onComplete
    )
}