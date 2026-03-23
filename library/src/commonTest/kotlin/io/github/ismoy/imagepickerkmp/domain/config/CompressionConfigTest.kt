package io.github.ismoy.imagepickerkmp.domain.config

import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CompressionConfigTest {

    // ───────────── Default values ─────────────

    @Test
    fun defaultConfig_isDisabled() {
        val config = CompressionConfig()
        assertFalse(config.enabled)
    }

    @Test
    fun defaultConfig_compressionLevelIsMedium() {
        val config = CompressionConfig()
        assertEquals(CompressionLevel.MEDIUM, config.compressionLevel)
    }

    @Test
    fun defaultConfig_maxWidthAndHeightAreNull() {
        val config = CompressionConfig()
        assertEquals(null, config.maxWidth)
        assertEquals(null, config.maxHeight)
    }

    @Test
    fun defaultConfig_maintainAspectRatioIsTrue() {
        val config = CompressionConfig()
        assertTrue(config.maintainAspectRatio)
    }

    @Test
    fun defaultConfig_customQualityIsNull() {
        val config = CompressionConfig()
        assertEquals(null, config.customQuality)
    }

    // ───────────── getQuality ─────────────

    @Test
    fun getQuality_noCustomQuality_returnsCompressionLevelValue() {
        val config = CompressionConfig(compressionLevel = CompressionLevel.LOW)
        assertEquals(CompressionLevel.LOW.toQualityValue(), config.getQuality(), 0.0001)
    }

    @Test
    fun getQuality_withCustomQuality_returnsCustomValue() {
        val config = CompressionConfig(
            compressionLevel = CompressionLevel.LOW,
            customQuality = 0.42
        )
        assertEquals(0.42, config.getQuality(), 0.0001)
    }

    @Test
    fun getQuality_customQualityOverridesLevel_forAllLevels() {
        val custom = 0.75
        CompressionLevel.entries.forEach { level ->
            val config = CompressionConfig(compressionLevel = level, customQuality = custom)
            assertEquals(custom, config.getQuality(), 0.0001,
                "Custom quality should override level=$level")
        }
    }

    // ───────────── supportsFormat ─────────────

    @Test
    fun supportsFormat_jpeg_returnsTrue_byDefault() {
        val config = CompressionConfig()
        assertTrue(config.supportsFormat("image/jpeg"))
    }

    @Test
    fun supportsFormat_png_returnsTrue_byDefault() {
        val config = CompressionConfig()
        assertTrue(config.supportsFormat("image/png"))
    }

    @Test
    fun supportsFormat_pdf_returnsFalse_byDefault() {
        val config = CompressionConfig()
        assertFalse(config.supportsFormat("application/pdf"))
    }

    @Test
    fun supportsFormat_caseInsensitive() {
        val config = CompressionConfig()
        assertTrue(config.supportsFormat("IMAGE/JPEG"))
        assertTrue(config.supportsFormat("Image/Png"))
    }

    @Test
    fun supportsFormat_customFormats_onlyMatchesSpecified() {
        val config = CompressionConfig(
            supportedFormats = listOf(MimeType.IMAGE_WEBP)
        )
        assertTrue(config.supportsFormat("image/webp"))
        assertFalse(config.supportsFormat("image/jpeg"))
        assertFalse(config.supportsFormat("image/png"))
    }

    @Test
    fun supportsFormat_emptyFormats_returnsFalseForAll() {
        val config = CompressionConfig(supportedFormats = emptyList())
        assertFalse(config.supportsFormat("image/jpeg"))
        assertFalse(config.supportsFormat("image/png"))
    }

    // ───────────── supportedFormats defaults ─────────────

    @Test
    fun defaultSupportedFormats_doesNotIncludeImageAll() {
        val config = CompressionConfig()
        assertFalse(config.supportsFormat(MimeType.IMAGE_ALL.value))
    }

    @Test
    fun defaultSupportedFormats_doesNotIncludePdf() {
        val config = CompressionConfig()
        assertFalse(config.supportsFormat(MimeType.APPLICATION_PDF.value))
    }

    @Test
    fun defaultSupportedFormats_includesAllCommonImageTypes() {
        val config = CompressionConfig()
        val commonTypes = listOf("image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp")
        commonTypes.forEach { mime ->
            assertTrue(config.supportsFormat(mime), "$mime should be supported by default")
        }
    }

    // ───────────── Copy / immutability ─────────────

    @Test
    fun copy_doesNotMutateOriginal() {
        val original = CompressionConfig(enabled = true, maxWidth = 1920)
        original.copy(enabled = false)
        assertTrue(original.enabled)
        assertEquals(1920, original.maxWidth)
    }
}

