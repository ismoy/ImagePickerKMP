package io.github.ismoy.imagepickerkmp.domain.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.ismoy.imagepickerkmp.domain.models.CropHandle

 fun resizeCropRect(
    currentRect: Rect,
    handle: CropHandle,
    dragAmount: Offset,
    canvasSize: Size
): Rect {
    val minSize = 100f

    var newRect = when (handle) {
        CropHandle.TOP_LEFT -> {
            val newLeft = (currentRect.left + dragAmount.x).coerceAtMost(currentRect.right - minSize)
            val newTop = (currentRect.top + dragAmount.y).coerceAtMost(currentRect.bottom - minSize)
            Rect(newLeft, newTop, currentRect.right, currentRect.bottom)
        }
        CropHandle.TOP_RIGHT -> {
            val newRight = (currentRect.right + dragAmount.x).coerceAtLeast(currentRect.left + minSize)
            val newTop = (currentRect.top + dragAmount.y).coerceAtMost(currentRect.bottom - minSize)
            Rect(currentRect.left, newTop, newRight, currentRect.bottom)
        }
        CropHandle.BOTTOM_LEFT -> {
            val newLeft = (currentRect.left + dragAmount.x).coerceAtMost(currentRect.right - minSize)
            val newBottom = (currentRect.bottom + dragAmount.y).coerceAtLeast(currentRect.top + minSize)
            Rect(newLeft, currentRect.top, currentRect.right, newBottom)
        }
        CropHandle.BOTTOM_RIGHT -> {
            val newRight = (currentRect.right + dragAmount.x).coerceAtLeast(currentRect.left + minSize)
            val newBottom = (currentRect.bottom + dragAmount.y).coerceAtLeast(currentRect.top + minSize)
            Rect(currentRect.left, currentRect.top, newRight, newBottom)
        }
        CropHandle.TOP_CENTER -> {
            val newTop = (currentRect.top + dragAmount.y).coerceAtMost(currentRect.bottom - minSize)
            Rect(currentRect.left, newTop, currentRect.right, currentRect.bottom)
        }
        CropHandle.BOTTOM_CENTER -> {
            val newBottom = (currentRect.bottom + dragAmount.y).coerceAtLeast(currentRect.top + minSize)
            Rect(currentRect.left, currentRect.top, currentRect.right, newBottom)
        }
        CropHandle.LEFT_CENTER -> {
            val newLeft = (currentRect.left + dragAmount.x).coerceAtMost(currentRect.right - minSize)
            Rect(newLeft, currentRect.top, currentRect.right, currentRect.bottom)
        }
        CropHandle.RIGHT_CENTER -> {
            val newRight = (currentRect.right + dragAmount.x).coerceAtLeast(currentRect.left + minSize)
            Rect(currentRect.left, currentRect.top, newRight, currentRect.bottom)
        }
    }

    newRect = Rect(
        left = newRect.left.coerceAtLeast(0f),
        top = newRect.top.coerceAtLeast(0f),
        right = newRect.right.coerceAtMost(canvasSize.width),
        bottom = newRect.bottom.coerceAtMost(canvasSize.height)
    )

    return newRect
}