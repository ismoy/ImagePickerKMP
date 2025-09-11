package io.github.ismoy.imagepickerkmp.domain.utils

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size

 fun applyCropAspectRatio(
    currentCropRect: Rect,
    aspectRatio: String,
    canvasSize: Size
): Rect {
    if (aspectRatio == "Free") {
        return currentCropRect
    }

    val ratio = when (aspectRatio) {
        "1:1" -> 1f / 1f
        "4:3" -> 4f / 3f
        "16:9" -> 16f / 9f
        "9:16" -> 9f / 16f
        else -> 1f / 1f
    }

    val centerX = currentCropRect.center.x
    val centerY = currentCropRect.center.y
    val currentWidth = currentCropRect.width
    val currentHeight = currentCropRect.height

    val newWidth: Float
    val newHeight: Float

    if (ratio >= 1f) {
        newHeight = currentHeight
        newWidth = newHeight * ratio
    } else {
        newWidth = currentWidth
        newHeight = newWidth / ratio
    }

    val margin = 40f
    val maxWidth = canvasSize.width - margin
    val maxHeight = canvasSize.height - margin
    
    val adjustedWidth: Float
    val adjustedHeight: Float
    
    if (ratio < 1f) {
        val heightBasedHeight = maxHeight.coerceAtMost(newHeight)
        val heightBasedWidth = heightBasedHeight * ratio
        
        if (heightBasedWidth <= maxWidth) {
            adjustedHeight = heightBasedHeight
            adjustedWidth = heightBasedWidth
        } else {
            adjustedWidth = maxWidth
            adjustedHeight = adjustedWidth / ratio
        }
    } else {
        val widthBasedWidth = maxWidth.coerceAtMost(newWidth)
        val widthBasedHeight = widthBasedWidth / ratio
        
        if (widthBasedHeight <= maxHeight) {
            adjustedWidth = widthBasedWidth
            adjustedHeight = widthBasedHeight
        } else {
            adjustedHeight = maxHeight
            adjustedWidth = adjustedHeight * ratio
        }
    }

    val minMargin = margin / 2
    val left = (centerX - adjustedWidth / 2).coerceAtLeast(minMargin).coerceAtMost(canvasSize.width - adjustedWidth - minMargin)
    val top = (centerY - adjustedHeight / 2).coerceAtLeast(minMargin).coerceAtMost(canvasSize.height - adjustedHeight - minMargin)

    return Rect(
        left = left,
        top = top,
        right = left + adjustedWidth,
        bottom = top + adjustedHeight
    )
}