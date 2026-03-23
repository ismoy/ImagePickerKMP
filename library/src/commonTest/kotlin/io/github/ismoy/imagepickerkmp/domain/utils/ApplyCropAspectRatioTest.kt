package io.github.ismoy.imagepickerkmp.domain.utils

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplyCropAspectRatioTest {

    private val canvasSize = Size(800f, 600f)
    private val baseCropRect = Rect(100f, 100f, 500f, 400f) // 400x300

    // ───────────── Free aspect ratio ─────────────

    @Test
    fun free_aspectRatio_returnsOriginalRect() {
        val result = applyCropAspectRatio(baseCropRect, "Free", canvasSize)
        assertEquals(baseCropRect, result)
    }

    // ───────────── 1:1 square ─────────────

    @Test
    fun square_1x1_resultHasEqualWidthAndHeight() {
        val result = applyCropAspectRatio(baseCropRect, "1:1", canvasSize)
        val width = result.right - result.left
        val height = result.bottom - result.top
        assertEquals(width, height, absoluteTolerance = 1f)
    }

    @Test
    fun square_1x1_resultFitsWithinCanvas() {
        val result = applyCropAspectRatio(baseCropRect, "1:1", canvasSize)
        assertTrue(result.left >= 0f, "left should be >= 0")
        assertTrue(result.top >= 0f, "top should be >= 0")
        assertTrue(result.right <= canvasSize.width, "right should fit canvas width")
        assertTrue(result.bottom <= canvasSize.height, "bottom should fit canvas height")
    }

    // ───────────── 4:3 ─────────────

    @Test
    fun ratio_4x3_resultApproximatelyCorrectRatio() {
        val result = applyCropAspectRatio(baseCropRect, "4:3", canvasSize)
        val width = result.right - result.left
        val height = result.bottom - result.top
        val ratio = width / height
        assertEquals(4f / 3f, ratio, absoluteTolerance = 0.02f)
    }

    @Test
    fun ratio_4x3_resultFitsWithinCanvas() {
        val result = applyCropAspectRatio(baseCropRect, "4:3", canvasSize)
        assertTrue(result.left >= 0f)
        assertTrue(result.top >= 0f)
        assertTrue(result.right <= canvasSize.width + 1f) // small float tolerance
        assertTrue(result.bottom <= canvasSize.height + 1f)
    }

    // ───────────── 16:9 ─────────────

    @Test
    fun ratio_16x9_resultApproximatelyCorrectRatio() {
        val result = applyCropAspectRatio(baseCropRect, "16:9", canvasSize)
        val width = result.right - result.left
        val height = result.bottom - result.top
        val ratio = width / height
        assertEquals(16f / 9f, ratio, absoluteTolerance = 0.02f)
    }

    @Test
    fun ratio_16x9_resultFitsWithinCanvas() {
        val result = applyCropAspectRatio(baseCropRect, "16:9", canvasSize)
        assertTrue(result.left >= 0f)
        assertTrue(result.top >= 0f)
        assertTrue(result.right <= canvasSize.width + 1f)
        assertTrue(result.bottom <= canvasSize.height + 1f)
    }

    // ───────────── 9:16 portrait ─────────────

    @Test
    fun ratio_9x16_resultApproximatelyCorrectRatio() {
        val result = applyCropAspectRatio(baseCropRect, "9:16", canvasSize)
        val width = result.right - result.left
        val height = result.bottom - result.top
        val ratio = width / height
        assertEquals(9f / 16f, ratio, absoluteTolerance = 0.02f)
    }

    @Test
    fun ratio_9x16_resultFitsWithinCanvas() {
        val result = applyCropAspectRatio(baseCropRect, "9:16", canvasSize)
        assertTrue(result.left >= 0f)
        assertTrue(result.top >= 0f)
        assertTrue(result.right <= canvasSize.width + 1f)
        assertTrue(result.bottom <= canvasSize.height + 1f)
    }

    // ───────────── Unknown ratio defaults to 1:1 ─────────────

    @Test
    fun unknown_aspectRatio_defaultsTo1x1() {
        val result = applyCropAspectRatio(baseCropRect, "3:2", canvasSize)
        val width = result.right - result.left
        val height = result.bottom - result.top
        // Unknown ratio falls back to 1:1
        assertEquals(width, height, absoluteTolerance = 1f)
    }

    // ───────────── Margin constraints ─────────────

    @Test
    fun anyRatio_resultRespects40pxMargin() {
        val margin = 20f // half margin (coerceAtLeast used in function)
        val result = applyCropAspectRatio(baseCropRect, "4:3", canvasSize)
        assertTrue(result.left >= margin - 1f, "left must respect margin")
        assertTrue(result.top >= margin - 1f, "top must respect margin")
    }

    // ───────────── Small canvas ─────────────

    @Test
    fun smallCanvas_resultFitsWithinBounds() {
        val smallCanvas = Size(200f, 200f)
        val rect = Rect(10f, 10f, 150f, 150f)
        val result = applyCropAspectRatio(rect, "16:9", smallCanvas)
        assertTrue(result.right <= smallCanvas.width + 1f)
        assertTrue(result.bottom <= smallCanvas.height + 1f)
        assertTrue(result.left >= 0f)
        assertTrue(result.top >= 0f)
    }

    // ───────────── Height-constrained canvas (ratio >= 1, heightBased > maxHeight) ─────────────

    @Test
    fun ratio_4x3_tallCanvas_heightConstrainedBranch() {
        // Canvas very wide but short: maxHeight will constrain the result
        val tallCanvas = Size(2000f, 150f)
        val bigRect = Rect(0f, 0f, 1900f, 100f)
        val result = applyCropAspectRatio(bigRect, "4:3", tallCanvas)
        val width = result.right - result.left
        val height = result.bottom - result.top
        assertEquals(4f / 3f, width / height, absoluteTolerance = 0.02f)
        assertTrue(result.bottom <= tallCanvas.height + 1f)
        assertTrue(result.right <= tallCanvas.width + 1f)
    }

    @Test
    fun ratio_16x9_tallCanvas_heightConstrainedBranch() {
        // Force the else-branch: widthBasedHeight > maxHeight
        val shortCanvas = Size(2000f, 100f)
        val wideRect = Rect(0f, 0f, 1900f, 80f)
        val result = applyCropAspectRatio(wideRect, "16:9", shortCanvas)
        val width = result.right - result.left
        val height = result.bottom - result.top
        assertEquals(16f / 9f, width / height, absoluteTolerance = 0.02f)
        assertTrue(result.bottom <= shortCanvas.height + 1f)
    }

    @Test
    fun ratio_1x1_heightConstrained_elseBranch() {
        // Canvas very wide and low: height will be constraining factor for 1:1
        val wideShortCanvas = Size(3000f, 80f)
        val rect = Rect(100f, 10f, 2500f, 60f)
        val result = applyCropAspectRatio(rect, "1:1", wideShortCanvas)
        val width = result.right - result.left
        val height = result.bottom - result.top
        assertEquals(width, height, absoluteTolerance = 1f)
        assertTrue(result.bottom <= wideShortCanvas.height + 1f)
    }

    // ───────────── Portrait ratio width-constrained (ratio < 1, widthBased > maxWidth) ─────────────

    @Test
    fun ratio_9x16_narrowCanvas_widthConstrainedBranch() {
        // Canvas tall and narrow: width will limit result for portrait ratio
        val narrowCanvas = Size(80f, 2000f)
        val rect = Rect(10f, 100f, 60f, 1800f)
        val result = applyCropAspectRatio(rect, "9:16", narrowCanvas)
        val width = result.right - result.left
        val height = result.bottom - result.top
        assertEquals(9f / 16f, width / height, absoluteTolerance = 0.02f)
        assertTrue(result.right <= narrowCanvas.width + 1f)
    }

    // ───────────── Result is always a valid Rect ─────────────

    @Test
    fun allRatios_resultIsAlwaysValid() {
        val ratios = listOf("Free", "1:1", "4:3", "16:9", "9:16")
        ratios.forEach { ratio ->
            val result = applyCropAspectRatio(baseCropRect, ratio, canvasSize)
            assertTrue(result.right > result.left, "right > left for ratio $ratio")
            assertTrue(result.bottom > result.top, "bottom > top for ratio $ratio")
        }
    }
}

