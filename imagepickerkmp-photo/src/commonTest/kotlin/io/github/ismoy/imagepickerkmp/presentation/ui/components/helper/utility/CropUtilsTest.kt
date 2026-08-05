package io.github.ismoy.imagepickerkmp.ui.helper.utility

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import io.github.ismoy.imagepickerkmp.crop.CropHandle
import io.github.ismoy.imagepickerkmp.crop.CropUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CropUtilsTest {

    // handleSize in CropUtils.detectHandle is 120f

    @Test
    fun testDetectHandle_topLeftCorner() {
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        val offset = Offset(100f, 100f)
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.TOP_LEFT, result)
    }

    @Test
    fun testDetectHandle_topRightCorner() {
        // Rect must be wide enough so right is >240f from left (2×handleSize)
        // to avoid TOP_LEFT overlapping TOP_RIGHT.
        val cropRect = Rect(left = 0f, top = 100f, right = 500f, bottom = 400f)
        val offset = Offset(500f, 100f)
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.TOP_RIGHT, result)
    }

    @Test
    fun testDetectHandle_bottomLeftCorner() {
        // Rect must be tall enough so bottom is >240f from top (2×handleSize)
        val cropRect = Rect(left = 100f, top = 0f, right = 400f, bottom = 500f)
        val offset = Offset(100f, 500f)
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.BOTTOM_LEFT, result)
    }

    @Test
    fun testDetectHandle_bottomRightCorner() {
        // Rect must be large enough so no corner overlaps
        val cropRect = Rect(left = 0f, top = 0f, right = 500f, bottom = 500f)
        val offset = Offset(500f, 500f)
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.BOTTOM_RIGHT, result)
    }

    @Test
    fun testDetectHandle_topCenter() {
        // Use a large rect so center X is far enough from corners to not overlap
        val cropRect = Rect(left = 0f, top = 100f, right = 1000f, bottom = 500f)
        val offset = Offset(500f, 100f) // Center X, top Y
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.TOP_CENTER, result)
    }

    @Test
    fun testDetectHandle_bottomCenter() {
        // Use a large rect so center X is far enough from corners to not overlap
        val cropRect = Rect(left = 0f, top = 100f, right = 1000f, bottom = 500f)
        val offset = Offset(500f, 500f) // Center X, bottom Y
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.BOTTOM_CENTER, result)
    }

    @Test
    fun testDetectHandle_leftCenter() {
        // Use a large rect so center Y is far enough from corners to not overlap
        val cropRect = Rect(left = 100f, top = 0f, right = 500f, bottom = 1000f)
        val offset = Offset(100f, 500f) // Left X, center Y
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.LEFT_CENTER, result)
    }

    @Test
    fun testDetectHandle_rightCenter() {
        // Use a large rect so center Y is far enough from corners to not overlap
        val cropRect = Rect(left = 100f, top = 0f, right = 500f, bottom = 1000f)
        val offset = Offset(500f, 500f) // Right X, center Y
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.RIGHT_CENTER, result)
    }

    @Test
    fun testDetectHandle_outsideHandleArea() {
        // With handleSize=120f, must be far enough from ALL handles
        // Use a large rect and pick a point that's >120f away from any edge/corner
        val cropRect = Rect(left = 0f, top = 0f, right = 1000f, bottom = 1000f)
        val offset = Offset(500f, 500f) // Dead center, >120f from every edge
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertNull(result)
    }

    @Test
    fun testDetectHandle_farFromCropRect() {
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        val offset = Offset(600f, 600f) // >120f from any handle
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertNull(result)
    }

    @Test
    fun testDetectHandle_nearButOutsideHandleSize() {
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        // Just outside the 120f handle area for TOP_LEFT
        val offset = Offset(100f - 121f, 100f - 121f)
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertNull(result)
    }

    @Test
    fun testDetectHandle_withinHandleSize() {
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        // Within 120f handle area for TOP_LEFT
        val offset = Offset(100f + 30f, 100f + 30f)
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.TOP_LEFT, result)
    }

    @Test
    fun testDetectHandle_exactHandleBoundary() {
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        // Exactly at 120f distance from TOP_LEFT
        val offset = Offset(100f + 120f, 100f + 120f)
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.TOP_LEFT, result)
    }

    @Test
    fun testDetectHandle_priorityOrder() {
        // Corner handles have priority over center handles
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        val offset = Offset(100f, 100f)
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.TOP_LEFT, result)
    }

    @Test
    fun testDetectHandle_smallCropRect() {
        val cropRect = Rect(left = 50f, top = 50f, right = 60f, bottom = 60f)
        val offset = Offset(50f, 50f)
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.TOP_LEFT, result)
    }

    @Test
    fun testDetectHandle_largeCropRect() {
        val cropRect = Rect(left = 0f, top = 0f, right = 1000f, bottom = 1000f)
        val offset = Offset(500f, 0f) // Top center
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.TOP_CENTER, result)
    }

    @Test
    fun testDetectHandle_negativeCoordinates() {
        val cropRect = Rect(left = -100f, top = -100f, right = 0f, bottom = 0f)
        val offset = Offset(-100f, -100f) // Top left
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.TOP_LEFT, result)
    }

    @Test
    fun testDetectHandle_zeroSizeCropRect() {
        val cropRect = Rect(left = 100f, top = 100f, right = 100f, bottom = 100f)
        val offset = Offset(100f, 100f)
        
        // All handles would be at the same point, TOP_LEFT has priority
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.TOP_LEFT, result)
    }

    @Test
    fun testDetectHandle_edgeCaseBoundaries() {
        // Use a large rect (>240f in both dimensions) to avoid corner overlap with handleSize=120f
        val cropRect = Rect(left = 200f, top = 200f, right = 600f, bottom = 600f)
        
        // Test exact boundary conditions for each handle (inclusive at ±120f)
        val testCases = listOf(
            Offset(200f - 120f, 200f - 120f) to CropHandle.TOP_LEFT,   // exact outer boundary
            Offset(200f + 120f, 200f + 120f) to CropHandle.TOP_LEFT,   // exact inner boundary
            Offset(600f - 120f, 200f - 120f) to CropHandle.TOP_RIGHT,  // inner boundary for TOP_RIGHT
            Offset(600f + 120f, 200f + 120f) to CropHandle.TOP_RIGHT   // outer boundary for TOP_RIGHT
        )
        
        testCases.forEach { (offset, expectedHandle) ->
            val result = CropUtils.detectHandle(offset, cropRect)
            assertEquals(expectedHandle, result, "Expected $expectedHandle for offset $offset")
        }
    }
}
