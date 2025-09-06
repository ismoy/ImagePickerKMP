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
        
        // Should have 7 specific image types (excluding IMAGE_ALL which is a wildcard)
        val expectedCount = 7
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
