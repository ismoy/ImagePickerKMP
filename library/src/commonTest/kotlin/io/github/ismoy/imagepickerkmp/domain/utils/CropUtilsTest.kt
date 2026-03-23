package io.github.ismoy.imagepickerkmp.domain.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import io.github.ismoy.imagepickerkmp.domain.models.CropHandle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CropUtilsTest {

    private val cropRect = Rect(100f, 100f, 500f, 400f)
    private val handleSize = 40f

    // ───────────── Corner handles ─────────────

    @Test
    fun detectHandle_topLeft_exactPosition_returnsTopLeft() {
        val touch = Offset(cropRect.left, cropRect.top)
        assertEquals(CropHandle.TOP_LEFT, CropUtils.detectHandle(touch, cropRect))
    }

    @Test
    fun detectHandle_topRight_exactPosition_returnsTopRight() {
        val touch = Offset(cropRect.right, cropRect.top)
        assertEquals(CropHandle.TOP_RIGHT, CropUtils.detectHandle(touch, cropRect))
    }

    @Test
    fun detectHandle_bottomLeft_exactPosition_returnsBottomLeft() {
        val touch = Offset(cropRect.left, cropRect.bottom)
        assertEquals(CropHandle.BOTTOM_LEFT, CropUtils.detectHandle(touch, cropRect))
    }

    @Test
    fun detectHandle_bottomRight_exactPosition_returnsBottomRight() {
        val touch = Offset(cropRect.right, cropRect.bottom)
        assertEquals(CropHandle.BOTTOM_RIGHT, CropUtils.detectHandle(touch, cropRect))
    }

    // ───────────── Edge mid-point handles ─────────────

    @Test
    fun detectHandle_topCenter_exactPosition_returnsTopCenter() {
        val touch = Offset(cropRect.centerX, cropRect.top)
        assertEquals(CropHandle.TOP_CENTER, CropUtils.detectHandle(touch, cropRect))
    }

    @Test
    fun detectHandle_bottomCenter_exactPosition_returnsBottomCenter() {
        val touch = Offset(cropRect.centerX, cropRect.bottom)
        assertEquals(CropHandle.BOTTOM_CENTER, CropUtils.detectHandle(touch, cropRect))
    }

    @Test
    fun detectHandle_leftCenter_exactPosition_returnsLeftCenter() {
        val touch = Offset(cropRect.left, cropRect.centerY)
        assertEquals(CropHandle.LEFT_CENTER, CropUtils.detectHandle(touch, cropRect))
    }

    @Test
    fun detectHandle_rightCenter_exactPosition_returnsRightCenter() {
        val touch = Offset(cropRect.right, cropRect.centerY)
        assertEquals(CropHandle.RIGHT_CENTER, CropUtils.detectHandle(touch, cropRect))
    }

    // ───────────── Within tolerance ─────────────

    @Test
    fun detectHandle_topLeft_withinTolerance_returnsTopLeft() {
        // Offset within ±39px of the corner
        val touch = Offset(cropRect.left + 30f, cropRect.top + 30f)
        assertEquals(CropHandle.TOP_LEFT, CropUtils.detectHandle(touch, cropRect))
    }

    @Test
    fun detectHandle_bottomRight_withinTolerance_returnsBottomRight() {
        val touch = Offset(cropRect.right - 30f, cropRect.bottom - 30f)
        assertEquals(CropHandle.BOTTOM_RIGHT, CropUtils.detectHandle(touch, cropRect))
    }

    // ───────────── Outside all handles ─────────────

    @Test
    fun detectHandle_outsideAllHandles_returnsNull() {
        // Center of the rect, far from any handle
        val touch = Offset(cropRect.centerX, cropRect.centerY)
        assertNull(CropUtils.detectHandle(touch, cropRect))
    }

    @Test
    fun detectHandle_completelyOutside_returnsNull() {
        val touch = Offset(0f, 0f)
        assertNull(CropUtils.detectHandle(touch, cropRect))
    }

    @Test
    fun detectHandle_justOutsideTolerance_returnsNull() {
        // Just beyond handleSize from TOP_LEFT corner
        val touch = Offset(cropRect.left - handleSize - 1f, cropRect.top - handleSize - 1f)
        assertNull(CropUtils.detectHandle(touch, cropRect))
    }

    // ───────────── All handles are detectable ─────────────

    @Test
    fun detectHandle_allHandlePositions_noneReturnNull() {
        val positions = mapOf(
            CropHandle.TOP_LEFT     to Offset(cropRect.left,    cropRect.top),
            CropHandle.TOP_RIGHT    to Offset(cropRect.right,   cropRect.top),
            CropHandle.BOTTOM_LEFT  to Offset(cropRect.left,    cropRect.bottom),
            CropHandle.BOTTOM_RIGHT to Offset(cropRect.right,   cropRect.bottom),
            CropHandle.TOP_CENTER   to Offset(cropRect.centerX, cropRect.top),
            CropHandle.BOTTOM_CENTER to Offset(cropRect.centerX, cropRect.bottom),
            CropHandle.LEFT_CENTER  to Offset(cropRect.left,    cropRect.centerY),
            CropHandle.RIGHT_CENTER to Offset(cropRect.right,   cropRect.centerY)
        )

        positions.forEach { (expectedHandle, touch) ->
            val detected = CropUtils.detectHandle(touch, cropRect)
            assertNotNull(detected, "Expected $expectedHandle but got null for touch $touch")
            assertEquals(expectedHandle, detected)
        }
    }

    // ───────────── Small rect edge case ─────────────

    @Test
    fun detectHandle_smallRect_cornerHandlesStillWork() {
        val smallRect = Rect(200f, 200f, 250f, 250f)
        val touch = Offset(200f, 200f)
        assertEquals(CropHandle.TOP_LEFT, CropUtils.detectHandle(touch, smallRect))
    }
}
