package io.github.ismoy.imagepickerkmp.domain.config

import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CompressionConfigTest {

    @Test
    fun testCompressionConfig_defaultValues() {
        val config = CompressionConfig()
        
        assertFalse(config.enabled)
        assertEquals(CompressionLevel.MEDIUM, config.compressionLevel)
        assertEquals(null, config.maxWidth)
        assertEquals(null, config.maxHeight)
        assertEquals(null, config.customQuality)
        assertTrue(config.maintainAspectRatio)
        assertEquals(MimeType.ALL_SUPPORTED_TYPES.filter { it != MimeType.IMAGE_ALL }, config.supportedFormats)
    }

    @Test
    fun testCompressionConfig_allParameters() {
        val supportedFormats = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG)
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.HIGH,
            maxWidth = 1920,
            maxHeight = 1080,
            customQuality = 0.8,
            maintainAspectRatio = false,
            supportedFormats = supportedFormats
        )
        
        assertTrue(config.enabled)
        assertEquals(CompressionLevel.HIGH, config.compressionLevel)
        assertEquals(1920, config.maxWidth)
        assertEquals(1080, config.maxHeight)
        assertEquals(0.8, config.customQuality)
        assertFalse(config.maintainAspectRatio)
        assertEquals(supportedFormats, config.supportedFormats)
    }

    @Test
    fun testGetQuality_withCustomQuality() {
        val config = CompressionConfig(
            compressionLevel = CompressionLevel.LOW,
            customQuality = 0.9
        )
        
        assertEquals(0.9, config.getQuality())
    }

    @Test
    fun testGetQuality_withoutCustomQuality() {
        val config = CompressionConfig(
            compressionLevel = CompressionLevel.HIGH
        )
        
        assertEquals(CompressionLevel.HIGH.toQualityValue(), config.getQuality())
    }

    @Test
    fun testSupportsFormat_supportedFormat() {
        val config = CompressionConfig(
            supportedFormats = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG)
        )
        
        assertTrue(config.supportsFormat("image/jpeg"))
        assertTrue(config.supportsFormat("image/png"))
        assertTrue(config.supportsFormat("IMAGE/JPEG")) // case insensitive
    }

    @Test
    fun testSupportsFormat_unsupportedFormat() {
        val config = CompressionConfig(
            supportedFormats = listOf(MimeType.IMAGE_JPEG)
        )
        
        assertFalse(config.supportsFormat("image/png"))
        assertFalse(config.supportsFormat("image/gif"))
    }

    @Test
    fun testSupportsFormat_emptyFormats() {
        val config = CompressionConfig(
            supportedFormats = emptyList()
        )
        
        assertFalse(config.supportsFormat("image/jpeg"))
        assertFalse(config.supportsFormat("image/png"))
    }

    @Test
    fun testCompressionConfig_dataClassEquality() {
        val config1 = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.HIGH,
            maxWidth = 1920,
            maxHeight = 1080,
            customQuality = 0.8,
            maintainAspectRatio = false,
            supportedFormats = listOf(MimeType.IMAGE_JPEG)
        )

        val config2 = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.HIGH,
            maxWidth = 1920,
            maxHeight = 1080,
            customQuality = 0.8,
            maintainAspectRatio = false,
            supportedFormats = listOf(MimeType.IMAGE_JPEG)
        )

        assertEquals(config1, config2)
        assertEquals(config1.hashCode(), config2.hashCode())
    }

    @Test
    fun testCompressionConfig_toString() {
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.HIGH,
            maxWidth = 1920,
            maxHeight = 1080
        )

        val string = config.toString()
        assert(string.contains("enabled=true"))
        assert(string.contains("HIGH"))
        assert(string.contains("1920"))
        assert(string.contains("1080"))
    }
}
