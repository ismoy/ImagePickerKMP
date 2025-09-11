package io.github.ismoy.imagepickerkmp.domain.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CropConfigTest {

    @Test
    fun testCropConfig_defaultValues() {
        val config = CropConfig()
        
        assertFalse(config.enabled)
        assertFalse(config.aspectRatioLocked)
        assertFalse(config.circularCrop)
        assertTrue(config.squareCrop)  // Default is true
        assertFalse(config.freeformCrop)  // Default is false
    }

    @Test
    fun testCropConfig_allEnabled() {
        val config = CropConfig(
            enabled = true,
            aspectRatioLocked = true,
            circularCrop = true,
            squareCrop = true,
            freeformCrop = true
        )
        
        assertTrue(config.enabled)
        assertTrue(config.aspectRatioLocked)
        assertTrue(config.circularCrop)
        assertTrue(config.squareCrop)
        assertTrue(config.freeformCrop)
    }

    @Test
    fun testCropConfig_partialConfiguration() {
        val config = CropConfig(
            enabled = true,
            aspectRatioLocked = false,
            circularCrop = true,
            squareCrop = false,
            freeformCrop = false
        )
        
        assertTrue(config.enabled)
        assertFalse(config.aspectRatioLocked)
        assertTrue(config.circularCrop)
        assertFalse(config.squareCrop)
        assertFalse(config.freeformCrop)
    }

    @Test
    fun testCropConfig_enabledOnly() {
        val config = CropConfig(enabled = true)
        
        assertTrue(config.enabled)
        assertFalse(config.aspectRatioLocked)
        assertFalse(config.circularCrop)
        assertTrue(config.squareCrop) // Default is true
        assertFalse(config.freeformCrop) // Default is false
    }

    @Test
    fun testCropConfig_circularCropOnly() {
        val config = CropConfig(
            enabled = false,
            circularCrop = true
        )
        
        assertFalse(config.enabled)
        assertFalse(config.aspectRatioLocked)
        assertTrue(config.circularCrop)
        assertTrue(config.squareCrop) // Default is true
        assertFalse(config.freeformCrop) // Default is false
    }

    @Test
    fun testCropConfig_squareCropOnly() {
        val config = CropConfig(
            enabled = false,
            squareCrop = true
        )
        
        assertFalse(config.enabled)
        assertFalse(config.aspectRatioLocked)
        assertFalse(config.circularCrop)
        assertTrue(config.squareCrop)
        assertFalse(config.freeformCrop) // Default is false
    }

    @Test
    fun testCropConfig_aspectRatioLockedOnly() {
        val config = CropConfig(
            enabled = false,
            aspectRatioLocked = true
        )
        
        assertFalse(config.enabled)
        assertTrue(config.aspectRatioLocked)
        assertFalse(config.circularCrop)
        assertTrue(config.squareCrop) // Default is true
        assertFalse(config.freeformCrop) // Default is false
    }

    @Test
    fun testCropConfig_freeformCropDisabled() {
        val config = CropConfig(
            enabled = true,
            freeformCrop = false
        )
        
        assertTrue(config.enabled)
        assertFalse(config.aspectRatioLocked) // Default is false
        assertFalse(config.circularCrop) // Default is false
        assertTrue(config.squareCrop) // Default is true
        assertFalse(config.freeformCrop)
    }

    @Test
    fun testCropConfig_dataClassEquality() {
        val config1 = CropConfig(
            enabled = true,
            aspectRatioLocked = true,
            circularCrop = false,
            squareCrop = true,
            freeformCrop = false
        )

        val config2 = CropConfig(
            enabled = true,
            aspectRatioLocked = true,
            circularCrop = false,
            squareCrop = true,
            freeformCrop = false
        )

        assertEquals(config1, config2)
        assertEquals(config1.hashCode(), config2.hashCode())
    }

    @Test
    fun testCropConfig_toString() {
        val config = CropConfig(
            enabled = true,
            aspectRatioLocked = true,
            circularCrop = true,
            squareCrop = false,
            freeformCrop = false
        )

        val string = config.toString()
        assert(string.contains("enabled=true"))
        assert(string.contains("aspectRatioLocked=true"))
        assert(string.contains("circularCrop=true"))
        assert(string.contains("squareCrop=false"))
        assert(string.contains("freeformCrop=false"))
    }

    @Test
    fun testCropConfig_copyWithModification() {
        val original = CropConfig()
        val modified = original.copy(
            enabled = true,
            circularCrop = true
        )
        
        assertTrue(modified.enabled)
        assertFalse(modified.aspectRatioLocked) // Default is false
        assertTrue(modified.circularCrop)
        assertTrue(modified.squareCrop) // Default is true
        assertFalse(modified.freeformCrop) // Default is false
    }

    @Test
    fun testCropConfig_allCombinations() {
        // Test various combinations to ensure all getters work correctly
        val config1 = CropConfig(enabled = true, aspectRatioLocked = true)
        assertTrue(config1.enabled)
        assertTrue(config1.aspectRatioLocked)
        
        val config2 = CropConfig(circularCrop = true, squareCrop = true)
        assertTrue(config2.circularCrop)
        assertTrue(config2.squareCrop)
        
        val config3 = CropConfig(freeformCrop = false)
        assertFalse(config3.freeformCrop)
    }
}
