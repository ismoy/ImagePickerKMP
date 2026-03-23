package io.github.ismoy.imagepickerkmp.domain.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CompressionLevelFullTest {

    // ───────────── toQualityValue ─────────────

    @Test
    fun low_toQualityValue_returns0_95() {
        assertEquals(0.95, CompressionLevel.LOW.toQualityValue(), 0.0001)
    }

    @Test
    fun medium_toQualityValue_returns0_80() {
        assertEquals(0.80, CompressionLevel.MEDIUM.toQualityValue(), 0.0001)
    }

    @Test
    fun high_toQualityValue_returns0_60() {
        assertEquals(0.60, CompressionLevel.HIGH.toQualityValue(), 0.0001)
    }

    @Test
    fun qualityValues_areInDescendingOrder() {
        assertTrue(
            CompressionLevel.LOW.toQualityValue() > CompressionLevel.MEDIUM.toQualityValue(),
            "LOW quality value should be greater than MEDIUM"
        )
        assertTrue(
            CompressionLevel.MEDIUM.toQualityValue() > CompressionLevel.HIGH.toQualityValue(),
            "MEDIUM quality value should be greater than HIGH"
        )
    }

    @Test
    fun allQualityValues_areInValidRange() {
        CompressionLevel.entries.forEach { level ->
            val q = level.toQualityValue()
            assertTrue(q in 0.0..1.0, "$level quality $q must be in [0.0, 1.0]")
        }
    }

    // ───────────── toJpegQuality ─────────────

    @Test
    fun low_toJpegQuality_returns85() {
        assertEquals(CompressionLevel.LOW_JPEG_QUALITY, CompressionLevel.LOW.toJpegQuality())
    }

    @Test
    fun medium_toJpegQuality_returns70() {
        assertEquals(CompressionLevel.MEDIUM_JPEG_QUALITY, CompressionLevel.MEDIUM.toJpegQuality())
    }

    @Test
    fun high_toJpegQuality_returns50() {
        assertEquals(CompressionLevel.HIGH_JPEG_QUALITY, CompressionLevel.HIGH.toJpegQuality())
    }

    @Test
    fun jpegQuality_isInDescendingOrder() {
        assertTrue(
            CompressionLevel.LOW.toJpegQuality() > CompressionLevel.MEDIUM.toJpegQuality(),
            "LOW JPEG quality should be greater than MEDIUM"
        )
        assertTrue(
            CompressionLevel.MEDIUM.toJpegQuality() > CompressionLevel.HIGH.toJpegQuality(),
            "MEDIUM JPEG quality should be greater than HIGH"
        )
    }

    @Test
    fun allJpegQualityValues_areInValidRange() {
        CompressionLevel.entries.forEach { level ->
            val q = level.toJpegQuality()
            assertTrue(q in 0..100, "$level JPEG quality $q must be in [0, 100]")
        }
    }

    // ───────────── toMaxDimension ─────────────

    @Test
    fun low_toMaxDimension_returns3840() {
        assertEquals(CompressionLevel.LOW_MAX_DIMENSION, CompressionLevel.LOW.toMaxDimension())
    }

    @Test
    fun medium_toMaxDimension_returns1920() {
        assertEquals(CompressionLevel.MEDIUM_MAX_DIMENSION, CompressionLevel.MEDIUM.toMaxDimension())
    }

    @Test
    fun high_toMaxDimension_returns1280() {
        assertEquals(CompressionLevel.HIGH_MAX_DIMENSION, CompressionLevel.HIGH.toMaxDimension())
    }

    @Test
    fun maxDimensions_areInDescendingOrder() {
        assertTrue(
            CompressionLevel.LOW.toMaxDimension() > CompressionLevel.MEDIUM.toMaxDimension(),
            "LOW max dimension should be greater than MEDIUM"
        )
        assertTrue(
            CompressionLevel.MEDIUM.toMaxDimension() > CompressionLevel.HIGH.toMaxDimension(),
            "MEDIUM max dimension should be greater than HIGH"
        )
    }

    @Test
    fun allMaxDimensions_arePositive() {
        CompressionLevel.entries.forEach { level ->
            assertTrue(level.toMaxDimension() > 0, "$level max dimension must be positive")
        }
    }

    // ───────────── Enum completeness ─────────────

    @Test
    fun compressionLevel_hasExactlyThreeValues() {
        assertEquals(3, CompressionLevel.entries.size)
    }

    @Test
    fun compressionLevel_containsExpectedValues() {
        val values = CompressionLevel.entries.map { it.name }
        assertTrue("LOW" in values)
        assertTrue("MEDIUM" in values)
        assertTrue("HIGH" in values)
    }

    // ───────────── Constants ─────────────

    @Test
    fun constants_matchExpectedValues() {
        assertEquals(85, CompressionLevel.LOW_JPEG_QUALITY)
        assertEquals(70, CompressionLevel.MEDIUM_JPEG_QUALITY)
        assertEquals(50, CompressionLevel.HIGH_JPEG_QUALITY)
        assertEquals(3840, CompressionLevel.LOW_MAX_DIMENSION)
        assertEquals(1920, CompressionLevel.MEDIUM_MAX_DIMENSION)
        assertEquals(1280, CompressionLevel.HIGH_MAX_DIMENSION)
    }
}

