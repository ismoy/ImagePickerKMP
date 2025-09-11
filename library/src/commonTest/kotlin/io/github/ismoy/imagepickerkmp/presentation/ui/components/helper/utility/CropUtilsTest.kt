package io.github.ismoy.imagepickerkmp.presentation.ui.components.helper.utility

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import io.github.ismoy.imagepickerkmp.domain.models.CropHandle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CropUtilsTest {

    @Test
    fun testDetectHandle_topLeftCorner() {
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        val offset = Offset(100f, 100f)
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.TOP_LEFT, result)
    }

    @Test
    fun testDetectHandle_topRightCorner() {
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        val offset = Offset(200f, 100f)
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.TOP_RIGHT, result)
    }

    @Test
    fun testDetectHandle_bottomLeftCorner() {
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        val offset = Offset(100f, 200f)
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.BOTTOM_LEFT, result)
    }

    @Test
    fun testDetectHandle_bottomRightCorner() {
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        val offset = Offset(200f, 200f)
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.BOTTOM_RIGHT, result)
    }

    @Test
    fun testDetectHandle_topCenter() {
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        val offset = Offset(150f, 100f) // Center X, top Y
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.TOP_CENTER, result)
    }

    @Test
    fun testDetectHandle_bottomCenter() {
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        val offset = Offset(150f, 200f) // Center X, bottom Y
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.BOTTOM_CENTER, result)
    }

    @Test
    fun testDetectHandle_leftCenter() {
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        val offset = Offset(100f, 150f) // Left X, center Y
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.LEFT_CENTER, result)
    }

    @Test
    fun testDetectHandle_rightCenter() {
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        val offset = Offset(200f, 150f) // Right X, center Y
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.RIGHT_CENTER, result)
    }

    @Test
    fun testDetectHandle_outsideHandleArea() {
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        val offset = Offset(150f, 150f) // Center of crop area, not on handle
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertNull(result)
    }

    @Test
    fun testDetectHandle_farFromCropRect() {
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        val offset = Offset(300f, 300f) // Far outside
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertNull(result)
    }

    @Test
    fun testDetectHandle_nearButOutsideHandleSize() {
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        // Just outside the 40f handle area for TOP_LEFT
        val offset = Offset(100f - 41f, 100f - 41f)
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertNull(result)
    }

    @Test
    fun testDetectHandle_withinHandleSize() {
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        // Within 40f handle area for TOP_LEFT
        val offset = Offset(100f + 30f, 100f + 30f)
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.TOP_LEFT, result)
    }

    @Test
    fun testDetectHandle_exactHandleBoundary() {
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        // Exactly at 40f distance from TOP_LEFT
        val offset = Offset(100f + 40f, 100f + 40f)
        
        val result = CropUtils.detectHandle(offset, cropRect)
        assertEquals(CropHandle.TOP_LEFT, result)
    }

    @Test
    fun testDetectHandle_priorityOrder() {
        // Test that corner handles have priority over center handles
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        // This position could potentially match both TOP_LEFT and TOP_CENTER
        // but TOP_LEFT appears first in the when statement
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
        
        val result = CropUtils.detectHandle(offset, cropRect)
        // All handles would be at the same point, TOP_LEFT has priority
        assertEquals(CropHandle.TOP_LEFT, result)
    }

    @Test
    fun testDetectHandle_edgeCaseBoundaries() {
        val cropRect = Rect(left = 100f, top = 100f, right = 200f, bottom = 200f)
        
        // Test exact boundary conditions for each handle
        val testCases = listOf(
            Offset(100f - 40f, 100f - 40f) to CropHandle.TOP_LEFT,
            Offset(100f + 40f, 100f + 40f) to CropHandle.TOP_LEFT,
            Offset(200f - 40f, 100f - 40f) to CropHandle.TOP_RIGHT,
            Offset(200f + 40f, 100f + 40f) to CropHandle.TOP_RIGHT
        )
        
        testCases.forEach { (offset, expectedHandle) ->
            val result = CropUtils.detectHandle(offset, cropRect)
            assertEquals(expectedHandle, result)
        }
    }
}
