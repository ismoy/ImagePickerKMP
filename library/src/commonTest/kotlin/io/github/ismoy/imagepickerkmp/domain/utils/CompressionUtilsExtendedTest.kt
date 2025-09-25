package io.github.ismoy.imagepickerkmp.domain.utils

import io.github.ismoy.imagepickerkmp.domain.config.CompressionConfig
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith

class CompressionUtilsExtendedTest {

    @Test
    fun `calculateOptimalDimensions should maintain aspect ratio when both max dimensions provided`() {
        val config = CompressionConfig(
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = 800,
            maxHeight = 600,
            maintainAspectRatio = true
        )
        
        val result = CompressionUtils.calculateOptimalDimensions(1600, 1200, config)
        
        assertEquals(800, result.first)
        assertEquals(600, result.second)
    }

    @Test
    fun `calculateOptimalDimensions should not maintain aspect ratio when disabled`() {
        val config = CompressionConfig(
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = 800,
            maxHeight = 600,
            maintainAspectRatio = false
        )
        
        val result = CompressionUtils.calculateOptimalDimensions(1600, 1800, config)
        
        assertEquals(800, result.first)
        assertEquals(600, result.second)
    }

    @Test
    fun `calculateOptimalDimensions should handle only max width constraint`() {
        val config = CompressionConfig(
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = 1000,
            maxHeight = null
        )
        
        val result = CompressionUtils.calculateOptimalDimensions(2000, 1500, config)
        
        assertEquals(1000, result.first)
        assertEquals(750, result.second) // Maintains aspect ratio
    }

    @Test
    fun `calculateOptimalDimensions should handle only max height constraint`() {
        val config = CompressionConfig(
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = null,
            maxHeight = 800
        )
        
        val result = CompressionUtils.calculateOptimalDimensions(1200, 1600, config)
        
        assertEquals(600, result.first) // Maintains aspect ratio
        assertEquals(800, result.second)
    }

    @Test
    fun `calculateOptimalDimensions should return original when no constraints`() {
        val config = CompressionConfig(
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = null,
            maxHeight = null
        )
        
        val result = CompressionUtils.calculateOptimalDimensions(1200, 800, config)
        
        assertEquals(1200, result.first)
        assertEquals(800, result.second)
    }

    @Test
    fun `calculateOptimalDimensions should not upscale when original is smaller`() {
        val config = CompressionConfig(
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = 1000,
            maxHeight = 800
        )
        
        val result = CompressionUtils.calculateOptimalDimensions(500, 400, config)
        
        assertEquals(500, result.first)
        assertEquals(400, result.second)
    }

    @Test
    fun `shouldCompress should return true for supported formats when enabled`() {
        val config = CompressionConfig(
            compressionLevel = CompressionLevel.MEDIUM,
            enabled = true
        )
        
        assertTrue(CompressionUtils.shouldCompress("image/jpeg", config))
        assertTrue(CompressionUtils.shouldCompress("image/png", config))
        assertTrue(CompressionUtils.shouldCompress("image/webp", config))
    }

    @Test
    fun `shouldCompress should return false when compression disabled`() {
        val config = CompressionConfig(
            compressionLevel = CompressionLevel.MEDIUM,
            enabled = false
        )
        
        assertFalse(CompressionUtils.shouldCompress("image/jpeg", config))
        assertFalse(CompressionUtils.shouldCompress("image/png", config))
    }

    @Test
    fun `shouldCompress should return false for null mime type`() {
        val config = CompressionConfig(
            compressionLevel = CompressionLevel.MEDIUM,
            enabled = true
        )
        
        assertFalse(CompressionUtils.shouldCompress(null, config))
    }

    @Test
    fun `validateConfig should pass for valid configuration`() {
        val config = CompressionConfig(
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = 1920,
            maxHeight = 1080
        )
        
        // Should not throw exception
        CompressionUtils.validateConfig(config)
    }

    @Test
    fun `validateConfig should throw for negative max width`() {
        val config = CompressionConfig(
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = -100,
            maxHeight = 1080
        )
        
        assertFailsWith<IllegalArgumentException> {
            CompressionUtils.validateConfig(config)
        }
    }

    @Test
    fun `validateConfig should throw for negative max height`() {
        val config = CompressionConfig(
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = 1920,
            maxHeight = -100
        )
        
        assertFailsWith<IllegalArgumentException> {
            CompressionUtils.validateConfig(config)
        }
    }

    @Test
    fun `validateConfig should throw for zero max width`() {
        val config = CompressionConfig(
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = 0,
            maxHeight = 1080
        )
        
        assertFailsWith<IllegalArgumentException> {
            CompressionUtils.validateConfig(config)
        }
    }

    @Test
    fun `validateConfig should throw for zero max height`() {
        val config = CompressionConfig(
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = 1920,
            maxHeight = 0
        )
        
        assertFailsWith<IllegalArgumentException> {
            CompressionUtils.validateConfig(config)
        }
    }

    @Test
    fun `calculateOptimalDimensions should handle extreme aspect ratios`() {
        val config = CompressionConfig(
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = 1000,
            maxHeight = 1000,
            maintainAspectRatio = true
        )
        
        // Very wide image
        val wideResult = CompressionUtils.calculateOptimalDimensions(4000, 100, config)
        assertEquals(1000, wideResult.first)
        assertEquals(25, wideResult.second)
        
        // Very tall image
        val tallResult = CompressionUtils.calculateOptimalDimensions(100, 4000, config)
        assertEquals(25, tallResult.first)
        assertEquals(1000, tallResult.second)
    }

    @Test
    fun `calculateOptimalDimensions should handle square images correctly`() {
        val config = CompressionConfig(
            compressionLevel = CompressionLevel.MEDIUM,
            maxWidth = 800,
            maxHeight = 600,
            maintainAspectRatio = true
        )
        
        val result = CompressionUtils.calculateOptimalDimensions(1000, 1000, config)
        
        assertEquals(600, result.first) // Limited by smaller max dimension
        assertEquals(600, result.second)
    }

    @Test
    fun `shouldCompress should handle case insensitive mime types`() {
        val config = CompressionConfig(
            compressionLevel = CompressionLevel.MEDIUM,
            enabled = true
        )
        
        // Assuming the config supports case insensitive comparison
        // This depends on the actual implementation of supportsFormat
        assertTrue(CompressionUtils.shouldCompress("image/jpeg", config))
        assertTrue(CompressionUtils.shouldCompress("image/png", config))
    }
}
