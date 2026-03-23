
package io.github.ismoy.imagepickerkmp.domain.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import io.github.ismoy.imagepickerkmp.data.processors.applyCropUtils
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult

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
    val context = LocalContext.current
    LaunchedEffect(photoResult, cropRect, canvasSize, isCircularCrop, zoomLevel, rotationAngle) {
        val result = applyCropUtils(
            context = context,
            photoResult = photoResult,
            cropRect = cropRect,
            canvasSize = canvasSize,
            isCircularCrop = isCircularCrop,
            zoomLevel = zoomLevel,
            rotationAngle = rotationAngle
        )
        onComplete(result)
    }
}
