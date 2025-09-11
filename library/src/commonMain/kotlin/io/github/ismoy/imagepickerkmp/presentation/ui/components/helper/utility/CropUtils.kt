package io.github.ismoy.imagepickerkmp.presentation.ui.components.helper.utility

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import io.github.ismoy.imagepickerkmp.domain.models.CropHandle
import io.github.ismoy.imagepickerkmp.domain.utils.centerX
import io.github.ismoy.imagepickerkmp.domain.utils.centerY

object CropUtils {
     fun detectHandle(offset: Offset, cropRect: Rect): CropHandle? {
        val handleSize = 40f

        return when {
            isInArea(offset, Offset(cropRect.left, cropRect.top), handleSize) -> CropHandle.TOP_LEFT
            isInArea(offset, Offset(cropRect.right, cropRect.top), handleSize) -> CropHandle.TOP_RIGHT
            isInArea(offset, Offset(cropRect.left, cropRect.bottom), handleSize) -> CropHandle.BOTTOM_LEFT
            isInArea(offset, Offset(cropRect.right, cropRect.bottom), handleSize) -> CropHandle.BOTTOM_RIGHT

            isInArea(offset, Offset(cropRect.centerX, cropRect.top), handleSize) -> CropHandle.TOP_CENTER
            isInArea(offset, Offset(cropRect.centerX, cropRect.bottom), handleSize) -> CropHandle.BOTTOM_CENTER
            isInArea(offset, Offset(cropRect.left, cropRect.centerY), handleSize) -> CropHandle.LEFT_CENTER
            isInArea(offset, Offset(cropRect.right, cropRect.centerY), handleSize) -> CropHandle.RIGHT_CENTER

            else -> null
        }
    }
    private fun isInArea(touch: Offset, target: Offset, size: Float): Boolean {
        return (touch.x >= target.x - size && touch.x <= target.x + size &&
                touch.y >= target.y - size && touch.y <= target.y + size)
    }
}