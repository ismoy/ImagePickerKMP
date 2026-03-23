package io.github.ismoy.imagepickerkmp.domain.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.ismoy.imagepickerkmp.domain.models.CropHandle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResizeCropRectTest {

    private val canvas = Size(800f, 600f)
    private val base = Rect(100f, 100f, 500f, 400f) // 400×300, minSize=100

    // ───────────── TOP_LEFT ─────────────

    @Test
    fun topLeft_dragRight_narrowsLeftEdge() {
        val result = resizeCropRect(base, CropHandle.TOP_LEFT, Offset(50f, 0f), canvas)
        assertEquals(150f, result.left, 0.01f)
        assertEquals(base.right, result.right, 0.01f)
    }

    @Test
    fun topLeft_dragDown_narrowsTopEdge() {
        val result = resizeCropRect(base, CropHandle.TOP_LEFT, Offset(0f, 50f), canvas)
        assertEquals(150f, result.top, 0.01f)
        assertEquals(base.bottom, result.bottom, 0.01f)
    }

    @Test
    fun topLeft_dragBeyondMinSize_clampsToMinSize() {
        // Drag so far right that width would be < 100
        val result = resizeCropRect(base, CropHandle.TOP_LEFT, Offset(500f, 0f), canvas)
        val width = result.right - result.left
        assertTrue(width >= 100f, "Width must be >= minSize (100f), was $width")
    }

    // ───────────── TOP_RIGHT ─────────────

    @Test
    fun topRight_dragRight_extendsRightEdge() {
        val result = resizeCropRect(base, CropHandle.TOP_RIGHT, Offset(50f, 0f), canvas)
        assertEquals(550f, result.right, 0.01f)
        assertEquals(base.left, result.left, 0.01f)
    }

    @Test
    fun topRight_dragBeyondMinSize_clampsToMinSize() {
        val result = resizeCropRect(base, CropHandle.TOP_RIGHT, Offset(-500f, 0f), canvas)
        val width = result.right - result.left
        assertTrue(width >= 100f, "Width must be >= minSize (100f), was $width")
    }

    // ───────────── BOTTOM_LEFT ─────────────

    @Test
    fun bottomLeft_dragDown_extendsBottomEdge() {
        val result = resizeCropRect(base, CropHandle.BOTTOM_LEFT, Offset(0f, 50f), canvas)
        assertEquals(450f, result.bottom, 0.01f)
        assertEquals(base.top, result.top, 0.01f)
    }

    @Test
    fun bottomLeft_dragBeyondMinSize_clampsHeight() {
        val result = resizeCropRect(base, CropHandle.BOTTOM_LEFT, Offset(0f, -500f), canvas)
        val height = result.bottom - result.top
        assertTrue(height >= 100f, "Height must be >= minSize (100f), was $height")
    }

    // ───────────── BOTTOM_RIGHT ─────────────

    @Test
    fun bottomRight_dragDown_extendsBottomEdge() {
        val result = resizeCropRect(base, CropHandle.BOTTOM_RIGHT, Offset(0f, 50f), canvas)
        assertEquals(450f, result.bottom, 0.01f)
    }

    @Test
    fun bottomRight_dragRight_extendsRightEdge() {
        val result = resizeCropRect(base, CropHandle.BOTTOM_RIGHT, Offset(50f, 0f), canvas)
        assertEquals(550f, result.right, 0.01f)
    }

    @Test
    fun bottomRight_dragBeyondCanvasBound_clampsToCanvas() {
        val result = resizeCropRect(base, CropHandle.BOTTOM_RIGHT, Offset(1000f, 1000f), canvas)
        assertEquals(canvas.width, result.right, 0.01f)
        assertEquals(canvas.height, result.bottom, 0.01f)
    }

    // ───────────── TOP_CENTER ─────────────

    @Test
    fun topCenter_dragDown_narrowsTopEdge() {
        val result = resizeCropRect(base, CropHandle.TOP_CENTER, Offset(0f, 50f), canvas)
        assertEquals(150f, result.top, 0.01f)
        assertEquals(base.left, result.left, 0.01f)
        assertEquals(base.right, result.right, 0.01f)
    }

    @Test
    fun topCenter_leftAndRightUnchanged() {
        val result = resizeCropRect(base, CropHandle.TOP_CENTER, Offset(0f, 10f), canvas)
        assertEquals(base.left, result.left, 0.01f)
        assertEquals(base.right, result.right, 0.01f)
    }

    // ───────────── BOTTOM_CENTER ─────────────

    @Test
    fun bottomCenter_dragDown_extendsBottomEdge() {
        val result = resizeCropRect(base, CropHandle.BOTTOM_CENTER, Offset(0f, 50f), canvas)
        assertEquals(450f, result.bottom, 0.01f)
        assertEquals(base.left, result.left, 0.01f)
        assertEquals(base.right, result.right, 0.01f)
    }

    @Test
    fun bottomCenter_dragBeyondMinHeight_clampsHeight() {
        val result = resizeCropRect(base, CropHandle.BOTTOM_CENTER, Offset(0f, -500f), canvas)
        val height = result.bottom - result.top
        assertTrue(height >= 100f, "Height must be >= minSize (100f), was $height")
    }

    // ───────────── LEFT_CENTER ─────────────

    @Test
    fun leftCenter_dragRight_narrowsLeftEdge() {
        val result = resizeCropRect(base, CropHandle.LEFT_CENTER, Offset(50f, 0f), canvas)
        assertEquals(150f, result.left, 0.01f)
        assertEquals(base.top, result.top, 0.01f)
        assertEquals(base.bottom, result.bottom, 0.01f)
    }

    @Test
    fun leftCenter_dragBeyondMinWidth_clampsWidth() {
        val result = resizeCropRect(base, CropHandle.LEFT_CENTER, Offset(500f, 0f), canvas)
        val width = result.right - result.left
        assertTrue(width >= 100f, "Width must be >= minSize (100f), was $width")
    }

    // ───────────── RIGHT_CENTER ─────────────

    @Test
    fun rightCenter_dragRight_extendsRightEdge() {
        val result = resizeCropRect(base, CropHandle.RIGHT_CENTER, Offset(50f, 0f), canvas)
        assertEquals(550f, result.right, 0.01f)
        assertEquals(base.top, result.top, 0.01f)
        assertEquals(base.bottom, result.bottom, 0.01f)
    }

    @Test
    fun rightCenter_dragBeyondMinWidth_clampsWidth() {
        val result = resizeCropRect(base, CropHandle.RIGHT_CENTER, Offset(-500f, 0f), canvas)
        val width = result.right - result.left
        assertTrue(width >= 100f, "Width must be >= minSize (100f), was $width")
    }

    // ───────────── Canvas boundary clamping ─────────────

    @Test
    fun allHandles_resultNeverExceedsCanvas() {
        val dragFar = Offset(2000f, 2000f)
        val handles = CropHandle.entries
        handles.forEach { handle ->
            val result = resizeCropRect(base, handle, dragFar, canvas)
            assertTrue(result.left >= 0f, "left < 0 for $handle")
            assertTrue(result.top >= 0f, "top < 0 for $handle")
            assertTrue(result.right <= canvas.width, "right > canvas for $handle")
            assertTrue(result.bottom <= canvas.height, "bottom > canvas for $handle")
        }
    }

    @Test
    fun zeroDrag_returnsEquivalentRect() {
        val result = resizeCropRect(base, CropHandle.BOTTOM_RIGHT, Offset(0f, 0f), canvas)
        assertEquals(base.left, result.left, 0.01f)
        assertEquals(base.top, result.top, 0.01f)
        assertEquals(base.right, result.right, 0.01f)
        assertEquals(base.bottom, result.bottom, 0.01f)
    }
}

private fun assertEquals(expected: Float, actual: Float, tolerance: Float) {
    assertTrue(
        kotlin.math.abs(expected - actual) <= tolerance,
        "Expected $expected but was $actual (tolerance $tolerance)"
    )
}
