package io.github.ismoy.imagepickerkmp.domain.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

 fun DrawScope.drawCropHandles(cropRect: Rect) {
    val handleSize = 18f
    val borderWidth = 3f

    val handles = listOf(
        Offset(cropRect.left, cropRect.top),
        Offset(cropRect.right, cropRect.top),
        Offset(cropRect.left, cropRect.bottom),
        Offset(cropRect.right, cropRect.bottom),
        Offset(cropRect.centerX, cropRect.top),
        Offset(cropRect.centerX, cropRect.bottom),
        Offset(cropRect.left, cropRect.centerY),
        Offset(cropRect.right, cropRect.centerY)
    )

    handles.forEach { handle ->
        drawCircle(
            color = Color.White,
            radius = handleSize,
            center = handle
        )

        drawCircle(
            color = Color.Black,
            radius = handleSize,
            center = handle,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = borderWidth)
        )
    }
}