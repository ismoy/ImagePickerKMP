package io.github.ismoy.imagepickerkmp.domain.models

import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CompressionLevelTest {

    // ── toQualityValue ────────────────────────────────────────────────────────

    @Test
    fun low_toQualityValue_returns0_95() {
        assertEquals(0.95, CompressionLevel.LOW.toQualityValue())
    }

    @Test
    fun medium_toQualityValue_returns0_80() {
        assertEquals(0.80, CompressionLevel.MEDIUM.toQualityValue())
    }

    @Test
    fun high_toQualityValue_returns0_60() {
        assertEquals(0.60, CompressionLevel.HIGH.toQualityValue())
    }

    // ── toJpegQuality ─────────────────────────────────────────────────────────

    @Test
    fun low_toJpegQuality_returns85() {
        assertEquals(85, CompressionLevel.LOW.toJpegQuality())
    }

    @Test
    fun medium_toJpegQuality_returns70() {
        assertEquals(70, CompressionLevel.MEDIUM.toJpegQuality())
    }

    @Test
    fun high_toJpegQuality_returns50() {
        assertEquals(50, CompressionLevel.HIGH.toJpegQuality())
    }

    // ── toMaxDimension ────────────────────────────────────────────────────────

    @Test
    fun low_toMaxDimension_returns3840() {
        assertEquals(3840, CompressionLevel.LOW.toMaxDimension())
    }

    @Test
    fun medium_toMaxDimension_returns1920() {
        assertEquals(1920, CompressionLevel.MEDIUM.toMaxDimension())
    }

    @Test
    fun high_toMaxDimension_returns1280() {
        assertEquals(1280, CompressionLevel.HIGH.toMaxDimension())
    }

    // ── Companion constants ───────────────────────────────────────────────────

    @Test
    fun constants_lowJpegQuality_is85() {
        assertEquals(85, CompressionLevel.LOW_JPEG_QUALITY)
    }

    @Test
    fun constants_mediumJpegQuality_is70() {
        assertEquals(70, CompressionLevel.MEDIUM_JPEG_QUALITY)
    }

    @Test
    fun constants_highJpegQuality_is50() {
        assertEquals(50, CompressionLevel.HIGH_JPEG_QUALITY)
    }

    @Test
    fun constants_lowMaxDimension_is3840() {
        assertEquals(3840, CompressionLevel.LOW_MAX_DIMENSION)
    }

    @Test
    fun constants_mediumMaxDimension_is1920() {
        assertEquals(1920, CompressionLevel.MEDIUM_MAX_DIMENSION)
    }

    @Test
    fun constants_highMaxDimension_is1280() {
        assertEquals(1280, CompressionLevel.HIGH_MAX_DIMENSION)
    }

    // ── Ordering / enum entries ───────────────────────────────────────────────

    @Test
    fun allEntries_exactlyThree() {
        assertEquals(3, CompressionLevel.entries.size)
    }

    @Test
    fun entries_containsAllValues() {
        val entries = CompressionLevel.entries
        assertTrue(entries.contains(CompressionLevel.LOW))
        assertTrue(entries.contains(CompressionLevel.MEDIUM))
        assertTrue(entries.contains(CompressionLevel.HIGH))
    }

    @Test
    fun qualityValues_areDescending_lowerCompressionHigherQuality() {
        assertTrue(CompressionLevel.LOW.toQualityValue() > CompressionLevel.MEDIUM.toQualityValue())
        assertTrue(CompressionLevel.MEDIUM.toQualityValue() > CompressionLevel.HIGH.toQualityValue())
    }

    @Test
    fun jpegQuality_areDescending_lowerCompressionHigherJpeg() {
        assertTrue(CompressionLevel.LOW.toJpegQuality() > CompressionLevel.MEDIUM.toJpegQuality())
        assertTrue(CompressionLevel.MEDIUM.toJpegQuality() > CompressionLevel.HIGH.toJpegQuality())
    }

    @Test
    fun maxDimension_areDescending_lowerCompressionLargerDimension() {
        assertTrue(CompressionLevel.LOW.toMaxDimension() > CompressionLevel.MEDIUM.toMaxDimension())
        assertTrue(CompressionLevel.MEDIUM.toMaxDimension() > CompressionLevel.HIGH.toMaxDimension())
    }

    // ── valueOf / name ────────────────────────────────────────────────────────

    @Test
    fun valueOf_low_returnsCorrectEnum() {
        assertEquals(CompressionLevel.LOW, CompressionLevel.valueOf("LOW"))
    }

    @Test
    fun valueOf_medium_returnsCorrectEnum() {
        assertEquals(CompressionLevel.MEDIUM, CompressionLevel.valueOf("MEDIUM"))
    }

    @Test
    fun valueOf_high_returnsCorrectEnum() {
        assertEquals(CompressionLevel.HIGH, CompressionLevel.valueOf("HIGH"))
    }

    @Test
    fun name_low_isLOW() {
        assertEquals("LOW", CompressionLevel.LOW.name)
    }

    @Test
    fun name_medium_isMEDIUM() {
        assertEquals("MEDIUM", CompressionLevel.MEDIUM.name)
    }

    @Test
    fun name_high_isHIGH() {
        assertEquals("HIGH", CompressionLevel.HIGH.name)
    }

    // ── Method consistency cross-check ────────────────────────────────────────

    @Test
    fun low_methodsMatchCompanionConstants() {
        assertEquals(CompressionLevel.LOW_JPEG_QUALITY, CompressionLevel.LOW.toJpegQuality())
        assertEquals(CompressionLevel.LOW_MAX_DIMENSION, CompressionLevel.LOW.toMaxDimension())
    }

    @Test
    fun medium_methodsMatchCompanionConstants() {
        assertEquals(CompressionLevel.MEDIUM_JPEG_QUALITY, CompressionLevel.MEDIUM.toJpegQuality())
        assertEquals(CompressionLevel.MEDIUM_MAX_DIMENSION, CompressionLevel.MEDIUM.toMaxDimension())
    }

    @Test
    fun high_methodsMatchCompanionConstants() {
        assertEquals(CompressionLevel.HIGH_JPEG_QUALITY, CompressionLevel.HIGH.toJpegQuality())
        assertEquals(CompressionLevel.HIGH_MAX_DIMENSION, CompressionLevel.HIGH.toMaxDimension())
    }
}
