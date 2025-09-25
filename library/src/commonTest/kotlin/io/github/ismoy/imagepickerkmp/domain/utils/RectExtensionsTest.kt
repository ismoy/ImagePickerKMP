package io.github.ismoy.imagepickerkmp.domain.utils

import androidx.compose.ui.geometry.Rect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RectExtensionsTest {

    @Test
    fun `centerX should calculate correct center X coordinate`() {
        val rect = Rect(left = 10f, top = 20f, right = 30f, bottom = 40f)
        
        val expectedCenterX = 10f + (30f - 10f) / 2 // left + width / 2
        assertEquals(20f, expectedCenterX)
        assertEquals(expectedCenterX, rect.centerX)
    }

    @Test
    fun `centerY should calculate correct center Y coordinate`() {
        val rect = Rect(left = 10f, top = 20f, right = 30f, bottom = 40f)
        
        val expectedCenterY = 20f + (40f - 20f) / 2 // top + height / 2
        assertEquals(30f, expectedCenterY)
        assertEquals(expectedCenterY, rect.centerY)
    }

    @Test
    fun `centerX should work with zero left coordinate`() {
        val rect = Rect(left = 0f, top = 0f, right = 100f, bottom = 50f)
        
        assertEquals(50f, rect.centerX)
    }

    @Test
    fun `centerY should work with zero top coordinate`() {
        val rect = Rect(left = 0f, top = 0f, right = 100f, bottom = 50f)
        
        assertEquals(25f, rect.centerY)
    }

    @Test
    fun `centerX should work with negative coordinates`() {
        val rect = Rect(left = -20f, top = -10f, right = 0f, bottom = 10f)
        
        val expectedCenterX = -20f + (0f - (-20f)) / 2 // -20 + 20/2 = -10
        assertEquals(-10f, expectedCenterX)
        assertEquals(expectedCenterX, rect.centerX)
    }

    @Test
    fun `centerY should work with negative coordinates`() {
        val rect = Rect(left = -20f, top = -10f, right = 0f, bottom = 10f)
        
        val expectedCenterY = -10f + (10f - (-10f)) / 2 // -10 + 20/2 = 0
        assertEquals(0f, expectedCenterY)
        assertEquals(expectedCenterY, rect.centerY)
    }

    @Test
    fun `centerX should work with square rectangle`() {
        val rect = Rect(left = 0f, top = 0f, right = 100f, bottom = 100f)
        
        assertEquals(50f, rect.centerX)
    }

    @Test
    fun `centerY should work with square rectangle`() {
        val rect = Rect(left = 0f, top = 0f, right = 100f, bottom = 100f)
        
        assertEquals(50f, rect.centerY)
    }

    @Test
    fun `centerX should work with very small rectangle`() {
        val rect = Rect(left = 0f, top = 0f, right = 1f, bottom = 1f)
        
        assertEquals(0.5f, rect.centerX)
    }

    @Test
    fun `centerY should work with very small rectangle`() {
        val rect = Rect(left = 0f, top = 0f, right = 1f, bottom = 1f)
        
        assertEquals(0.5f, rect.centerY)
    }

    @Test
    fun `centerX should work with large coordinates`() {
        val rect = Rect(left = 1000f, top = 2000f, right = 3000f, bottom = 4000f)
        
        val expectedCenterX = 1000f + (3000f - 1000f) / 2 // 1000 + 1000 = 2000
        assertEquals(2000f, expectedCenterX)
        assertEquals(expectedCenterX, rect.centerX)
    }

    @Test
    fun `centerY should work with large coordinates`() {
        val rect = Rect(left = 1000f, top = 2000f, right = 3000f, bottom = 4000f)
        
        val expectedCenterY = 2000f + (4000f - 2000f) / 2 // 2000 + 1000 = 3000
        assertEquals(3000f, expectedCenterY)
        assertEquals(expectedCenterY, rect.centerY)
    }

    @Test
    fun `centerX and centerY should work together`() {
        val rect = Rect(left = 10f, top = 15f, right = 50f, bottom = 35f)
        
        val centerX = rect.centerX // 10 + (50-10)/2 = 30
        val centerY = rect.centerY // 15 + (35-15)/2 = 25
        
        assertEquals(30f, centerX)
        assertEquals(25f, centerY)
    }

    @Test
    fun `extensions should be val properties`() {
        val rect = Rect(left = 0f, top = 0f, right = 10f, bottom = 10f)
        
        // Test that these are properties (not functions)
        val x = rect.centerX
        val y = rect.centerY
        
        // Calling multiple times should give same result
        assertEquals(x, rect.centerX)
        assertEquals(y, rect.centerY)
    }

    @Test
    fun `center coordinates should be consistent with Rect properties`() {
        val rect = Rect(left = 20f, top = 30f, right = 80f, bottom = 70f)
        
        // Verify using Rect's built-in width and height
        val expectedCenterX = rect.left + rect.width / 2
        val expectedCenterY = rect.top + rect.height / 2
        
        assertEquals(expectedCenterX, rect.centerX)
        assertEquals(expectedCenterY, rect.centerY)
    }

    @Test
    fun `center coordinates should handle float precision`() {
        val rect = Rect(left = 0.1f, top = 0.2f, right = 0.7f, bottom = 0.8f)
        
        val centerX = rect.centerX
        val centerY = rect.centerY
        
        assertTrue(centerX > 0.1f && centerX < 0.7f)
        assertTrue(centerY > 0.2f && centerY < 0.8f)
        
        // Should be approximately in the middle
        val tolerance = 0.001f
        assertTrue(kotlin.math.abs(centerX - 0.4f) < tolerance)
        assertTrue(kotlin.math.abs(centerY - 0.5f) < tolerance)
    }
}
