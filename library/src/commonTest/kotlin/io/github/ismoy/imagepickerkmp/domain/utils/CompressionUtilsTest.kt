package io.github.ismoy.imagepickerkmp.domain.utils

import io.github.ismoy.imagepickerkmp.domain.config.CompressionConfig
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CompressionUtilsTest {

    @Test
    fun testCalculateOptimalDimensions_noLimits_returnsOriginal() {
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = null,
            maxHeight = null
        )
        
        val result = CompressionUtils.calculateOptimalDimensions(1920, 1080, config)
        
        assertEquals(Pair(1920, 1080), result)
    }

    @Test
    fun testCalculateOptimalDimensions_withMaxWidth_maintainAspectRatio() {
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = 1000,
            maxHeight = null,
            maintainAspectRatio = true
        )
        
        val result = CompressionUtils.calculateOptimalDimensions(1920, 1080, config)
        
        assertEquals(Pair(1000, 562), result) // 1000 / (1920/1080) = 562.5 -> 562
    }

    @Test
    fun testCalculateOptimalDimensions_withMaxHeight_maintainAspectRatio() {
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = null,
            maxHeight = 600,
            maintainAspectRatio = true
        )
        
        val result = CompressionUtils.calculateOptimalDimensions(1920, 1080, config)
        
        assertEquals(Pair(1066, 600), result) // 600 * (1920/1080) = 1066.6 -> 1066
    }

    @Test
    fun testCalculateOptimalDimensions_withBothLimits_maintainAspectRatio() {
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = 800,
            maxHeight = 600,
            maintainAspectRatio = true
        )
        
        val result = CompressionUtils.calculateOptimalDimensions(1920, 1080, config)
        
        // Scale factors: width = 800/1920 = 0.416, height = 600/1080 = 0.556
        // Min scale = 0.416, so result should be (800, 450)
        assertEquals(Pair(800, 450), result)
    }

    @Test
    fun testCalculateOptimalDimensions_withBothLimits_noAspectRatioMaintenance() {
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = 800,
            maxHeight = 600,
            maintainAspectRatio = false
        )
        
        val result = CompressionUtils.calculateOptimalDimensions(1920, 1080, config)
        
        assertEquals(Pair(800, 600), result)
    }

    @Test
    fun testCalculateOptimalDimensions_imageSmallerThanLimits_returnsOriginal() {
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = 2000,
            maxHeight = 2000,
            maintainAspectRatio = true
        )
        
        val result = CompressionUtils.calculateOptimalDimensions(800, 600, config)
        
        assertEquals(Pair(800, 600), result)
    }

    @Test
    fun testCalculateOptimalDimensions_widthOnly_smallerImage() {
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = 2000,
            maxHeight = null
        )
        
        val result = CompressionUtils.calculateOptimalDimensions(800, 600, config)
        
        assertEquals(Pair(800, 600), result)
    }

    @Test
    fun testCalculateOptimalDimensions_heightOnly_smallerImage() {
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = null,
            maxHeight = 2000
        )
        
        val result = CompressionUtils.calculateOptimalDimensions(800, 600, config)
        
        assertEquals(Pair(800, 600), result)
    }

    @Test
    fun testShouldCompress_enabledWithSupportedFormat_returnsTrue() {
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.MEDIUM,
            supportedFormats = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG)
        )
        
        assertTrue(CompressionUtils.shouldCompress("image/jpeg", config))
        assertTrue(CompressionUtils.shouldCompress("image/png", config))
    }

    @Test
    fun testShouldCompress_enabledWithUnsupportedFormat_returnsFalse() {
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.MEDIUM,
            supportedFormats = listOf(MimeType.IMAGE_JPEG)
        )
        
        assertFalse(CompressionUtils.shouldCompress("image/gif", config))
    }

    @Test
    fun testShouldCompress_disabled_returnsFalse() {
        val config = CompressionConfig(
            enabled = false,
            compressionLevel = CompressionLevel.MEDIUM,
            supportedFormats = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG)
        )
        
        assertFalse(CompressionUtils.shouldCompress("image/jpeg", config))
    }

    @Test
    fun testShouldCompress_nullMimeType_returnsFalse() {
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.MEDIUM,
            supportedFormats = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG)
        )
        
        assertFalse(CompressionUtils.shouldCompress(null, config))
    }

    @Test
    fun testValidateConfig_validQuality_doesNotThrow() {
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.MEDIUM,
            customQuality = 0.5
        )
        
        // Should not throw
        CompressionUtils.validateConfig(config)
    }

    @Test
    fun testValidateConfig_invalidQualityTooHigh_throws() {
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.MEDIUM,
            customQuality = 1.5
        )
        
        assertFailsWith<IllegalArgumentException> {
            CompressionUtils.validateConfig(config)
        }
    }

    @Test
    fun testValidateConfig_invalidQualityTooLow_throws() {
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.MEDIUM,
            customQuality = -0.1
        )
        
        assertFailsWith<IllegalArgumentException> {
            CompressionUtils.validateConfig(config)
        }
    }

    @Test
    fun testValidateConfig_validDimensions_doesNotThrow() {
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = 1920,
            maxHeight = 1080
        )
        
        // Should not throw
        CompressionUtils.validateConfig(config)
    }

    @Test
    fun testValidateConfig_invalidMaxWidth_throws() {
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = -100,
            maxHeight = 1080
        )
        
        assertFailsWith<IllegalArgumentException> {
            CompressionUtils.validateConfig(config)
        }
    }

    @Test
    fun testValidateConfig_invalidMaxHeight_throws() {
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = 1920,
            maxHeight = -50
        )
        
        assertFailsWith<IllegalArgumentException> {
            CompressionUtils.validateConfig(config)
        }
    }

    @Test
    fun testValidateConfig_zeroWidth_throws() {
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = 0,
            maxHeight = 1080
        )
        
        assertFailsWith<IllegalArgumentException> {
            CompressionUtils.validateConfig(config)
        }
    }

    @Test
    fun testValidateConfig_zeroHeight_throws() {
        val config = CompressionConfig(
            enabled = true,
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = 1920,
            maxHeight = 0
        )
        
        assertFailsWith<IllegalArgumentException> {
            CompressionUtils.validateConfig(config)
        }
    }
}
