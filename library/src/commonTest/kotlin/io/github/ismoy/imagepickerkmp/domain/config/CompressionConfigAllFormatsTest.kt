package io.github.ismoy.imagepickerkmp.domain.config

import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Test to verify that CompressionConfig supports all available image formats by default
 */
class CompressionConfigAllFormatsTest {

    @Test
    fun `default compression config should support all image MIME types`() {
        val defaultConfig = CompressionConfig()
        
        // Verify all individual image formats are supported
        assertTrue(defaultConfig.supportsFormat(MimeType.IMAGE_JPEG.value))
        assertTrue(defaultConfig.supportsFormat(MimeType.IMAGE_PNG.value))
        assertTrue(defaultConfig.supportsFormat(MimeType.IMAGE_WEBP.value))
        assertTrue(defaultConfig.supportsFormat(MimeType.IMAGE_HEIC.value))
        assertTrue(defaultConfig.supportsFormat(MimeType.IMAGE_HEIF.value))
        assertTrue(defaultConfig.supportsFormat(MimeType.IMAGE_GIF.value))
        assertTrue(defaultConfig.supportsFormat(MimeType.IMAGE_BMP.value))
    }

    @Test
    fun `default config should have all supported MIME types count`() {
        val defaultConfig = CompressionConfig()
        
        // Should have image types supported by CompressionConfig (excluding IMAGE_ALL which is a wildcard and APPLICATION_PDF which is not compressible)
        val expectedImageFormats = listOf(
            MimeType.IMAGE_JPEG,
            MimeType.IMAGE_PNG,
            MimeType.IMAGE_WEBP,
            MimeType.IMAGE_HEIC,
            MimeType.IMAGE_HEIF,
            MimeType.IMAGE_GIF,
            MimeType.IMAGE_BMP
        )
        val expectedCount = expectedImageFormats.size
        assertTrue(
            defaultConfig.supportedFormats.size == expectedCount,
            "Expected $expectedCount formats, but got ${defaultConfig.supportedFormats.size}"
        )
    }

    @Test
    fun `custom config can still limit to specific formats`() {
        val customConfig = CompressionConfig(
            supportedFormats = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG)
        )
        
        assertTrue(customConfig.supportsFormat(MimeType.IMAGE_JPEG.value))
        assertTrue(customConfig.supportsFormat(MimeType.IMAGE_PNG.value))
        assertTrue(!customConfig.supportsFormat(MimeType.IMAGE_WEBP.value))
        assertTrue(!customConfig.supportsFormat(MimeType.IMAGE_HEIC.value))
    }
}
