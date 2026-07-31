package io.github.ismoy.imagepickerkmp.domain.utils

import androidx.compose.ui.geometry.Rect
import io.github.ismoy.imagepickerkmp.crop.centerX
import io.github.ismoy.imagepickerkmp.crop.centerY
import kotlin.test.Test
import kotlin.test.assertEquals

class RectExtensionsTest {

    @Test
    fun centerX_symmetricRect_isMidpoint() {
        val rect = Rect(100f, 0f, 300f, 200f)
        assertEquals(200f, rect.centerX, 0.01f)
    }

    @Test
    fun centerY_symmetricRect_isMidpoint() {
        val rect = Rect(0f, 100f, 200f, 300f)
        assertEquals(200f, rect.centerY, 0.01f)
    }

    @Test
    fun centerX_zeroOrigin_isHalfWidth() {
        val rect = Rect(0f, 0f, 400f, 200f)
        assertEquals(200f, rect.centerX, 0.01f)
    }

    @Test
    fun centerY_zeroOrigin_isHalfHeight() {
        val rect = Rect(0f, 0f, 200f, 600f)
        assertEquals(300f, rect.centerY, 0.01f)
    }

    @Test
    fun centerX_thinRect_matchesLeft() {
        val rect = Rect(50f, 0f, 50f, 100f)
        assertEquals(50f, rect.centerX, 0.01f)
    }

    @Test
    fun centerY_thinRect_matchesTop() {
        val rect = Rect(0f, 75f, 100f, 75f)
        assertEquals(75f, rect.centerY, 0.01f)
    }

    @Test
    fun centerX_negativeLeft_calculatesCorrectly() {
        val rect = Rect(-100f, 0f, 100f, 50f)
        assertEquals(0f, rect.centerX, 0.01f)
    }

    @Test
    fun centerY_negativeTop_calculatesCorrectly() {
        val rect = Rect(0f, -50f, 100f, 50f)
        assertEquals(0f, rect.centerY, 0.01f)
    }

    @Test
    fun centerX_largeRect_calculatesCorrectly() {
        val rect = Rect(0f, 0f, 3840f, 2160f)
        assertEquals(1920f, rect.centerX, 0.01f)
    }

    @Test
    fun centerY_largeRect_calculatesCorrectly() {
        val rect = Rect(0f, 0f, 3840f, 2160f)
        assertEquals(1080f, rect.centerY, 0.01f)
    }

    @Test
    fun centerX_offsetRect_isAverageofleftAndRight() {
        val rect = Rect(150f, 0f, 350f, 100f)
        val expected = (150f + 350f) / 2f
        assertEquals(expected, rect.centerX, 0.01f)
    }

    @Test
    fun centerY_offsetRect_isAverageOfTopAndBottom() {
        val rect = Rect(0f, 200f, 100f, 800f)
        val expected = (200f + 800f) / 2f
        assertEquals(expected, rect.centerY, 0.01f)
    }
}
