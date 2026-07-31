package io.github.ismoy.imagepickerkmp.domain.utils

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.ismoy.imagepickerkmp.crop.applyCropAspectRatio
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplyCropAspectRatioTest {

    private val canvas = Size(800f, 600f)
    // A centered crop rect for most tests
    private val centerRect = Rect(200f, 150f, 600f, 450f) // 400×300, center=(400,300)

    // ── Free aspect ratio ─────────────────────────────────────────────────────

    @Test
    fun free_aspectRatio_returnsOriginalRect() {
        val result = applyCropAspectRatio(centerRect, "Free", canvas)
        assertEquals(centerRect.left, result.left, 0.01f)
        assertEquals(centerRect.top, result.top, 0.01f)
        assertEquals(centerRect.right, result.right, 0.01f)
        assertEquals(centerRect.bottom, result.bottom, 0.01f)
    }

    // ── 1:1 aspect ratio ─────────────────────────────────────────────────────

    @Test
    fun oneToOne_resultIsSquare() {
        val result = applyCropAspectRatio(centerRect, "1:1", canvas)
        val width = result.right - result.left
        val height = result.bottom - result.top
        assertEquals(width, height, 1f)
    }

    @Test
    fun oneToOne_resultStaysInsideCanvas() {
        val result = applyCropAspectRatio(centerRect, "1:1", canvas)
        assertTrue(result.left >= 0f, "left must be >= 0")
        assertTrue(result.top >= 0f, "top must be >= 0")
        assertTrue(result.right <= canvas.width, "right must be <= canvas.width")
        assertTrue(result.bottom <= canvas.height, "bottom must be <= canvas.height")
    }

    // ── 4:3 aspect ratio ─────────────────────────────────────────────────────

    @Test
    fun fourToThree_widthOverHeightRatioIsApproximately4over3() {
        val result = applyCropAspectRatio(centerRect, "4:3", canvas)
        val width = result.right - result.left
        val height = result.bottom - result.top
        val ratio = width / height
        assertEquals(4f / 3f, ratio, 0.01f)
    }

    @Test
    fun fourToThree_resultStaysInsideCanvas() {
        val result = applyCropAspectRatio(centerRect, "4:3", canvas)
        assertTrue(result.left >= 0f)
        assertTrue(result.top >= 0f)
        assertTrue(result.right <= canvas.width)
        assertTrue(result.bottom <= canvas.height)
    }

    // ── 16:9 aspect ratio ────────────────────────────────────────────────────

    @Test
    fun sixteenToNine_widthOverHeightRatioIsApproximately16over9() {
        val result = applyCropAspectRatio(centerRect, "16:9", canvas)
        val width = result.right - result.left
        val height = result.bottom - result.top
        val ratio = width / height
        assertEquals(16f / 9f, ratio, 0.01f)
    }

    @Test
    fun sixteenToNine_resultStaysInsideCanvas() {
        val result = applyCropAspectRatio(centerRect, "16:9", canvas)
        assertTrue(result.left >= 0f)
        assertTrue(result.top >= 0f)
        assertTrue(result.right <= canvas.width)
        assertTrue(result.bottom <= canvas.height)
    }

    // ── 9:16 aspect ratio (portrait) ─────────────────────────────────────────

    @Test
    fun nineToSixteen_heightOverWidthRatioIsApproximately16over9() {
        val result = applyCropAspectRatio(centerRect, "9:16", canvas)
        val width = result.right - result.left
        val height = result.bottom - result.top
        val ratio = height / width
        assertEquals(16f / 9f, ratio, 0.01f)
    }

    @Test
    fun nineToSixteen_resultStaysInsideCanvas() {
        val result = applyCropAspectRatio(centerRect, "9:16", canvas)
        assertTrue(result.left >= 0f)
        assertTrue(result.top >= 0f)
        assertTrue(result.right <= canvas.width)
        assertTrue(result.bottom <= canvas.height)
    }

    // ── Unknown aspect ratio falls back to 1:1 ────────────────────────────────

    @Test
    fun unknownRatioString_fallsBackToSquare() {
        val result = applyCropAspectRatio(centerRect, "3:2", canvas)
        val width = result.right - result.left
        val height = result.bottom - result.top
        // Unknown ratio maps to 1f/1f → square
        assertEquals(width, height, 1f)
    }

    // ── Result dimensions are positive ───────────────────────────────────────

    @Test
    fun allRatios_resultHasPositiveDimensions() {
        val ratios = listOf("Free", "1:1", "4:3", "16:9", "9:16")
        ratios.forEach { ratio ->
            val result = applyCropAspectRatio(centerRect, ratio, canvas)
            val width = result.right - result.left
            val height = result.bottom - result.top
            assertTrue(width > 0f, "Width <= 0 for ratio $ratio")
            assertTrue(height > 0f, "Height <= 0 for ratio $ratio")
        }
    }

    // ── Small canvas ─────────────────────────────────────────────────────────

    @Test
    fun smallCanvas_allRatios_stayInsideBounds() {
        val smallCanvas = Size(200f, 200f)
        val smallRect = Rect(50f, 50f, 150f, 150f)
        val ratios = listOf("1:1", "4:3", "16:9", "9:16")
        ratios.forEach { ratio ->
            val result = applyCropAspectRatio(smallRect, ratio, smallCanvas)
            assertTrue(result.left >= 0f, "left < 0 for $ratio")
            assertTrue(result.top >= 0f, "top < 0 for $ratio")
            assertTrue(result.right <= smallCanvas.width, "right > canvas for $ratio")
            assertTrue(result.bottom <= smallCanvas.height, "bottom > canvas for $ratio")
        }
    }

    // ── Nearly-square initial rect ────────────────────────────────────────────

    @Test
    fun squareInitialRect_oneToOne_preservesDimensions() {
        val squareRect = Rect(200f, 200f, 400f, 400f) // 200×200
        val result = applyCropAspectRatio(squareRect, "1:1", canvas)
        val width = result.right - result.left
        val height = result.bottom - result.top
        assertEquals(width, height, 1f)
    }

    // ── Rect near top edge ────────────────────────────────────────────────────

    @Test
    fun rectNearTopEdge_allRatios_stayInsideBounds() {
        val topRect = Rect(100f, 20f, 700f, 200f)
        listOf("1:1", "4:3", "16:9", "9:16").forEach { ratio ->
            val result = applyCropAspectRatio(topRect, ratio, canvas)
            assertTrue(result.top >= 0f, "top < 0 for $ratio")
            assertTrue(result.bottom <= canvas.height, "bottom > canvas for $ratio")
        }
    }
}
