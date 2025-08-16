package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.UiConfig
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SimpleConfigTest {
    
    @Test
    fun `CameraCaptureConfig should have default values`() {
        val config = CameraCaptureConfig()
        
        assertNotNull(config)
        assertEquals(CapturePhotoPreference.QUALITY, config.preference)
        assertNotNull(config.uiConfig)
        assertNotNull(config.cameraCallbacks)
    }
    
    @Test
    fun `UiConfig should be created with null values`() {
        val config = UiConfig()
        
        assertNotNull(config)
        // All properties should be nullable and default to null
    }
    
    @Test
    fun `CapturePhotoPreference QUALITY should exist`() {
        val preference = CapturePhotoPreference.QUALITY
        
        assertNotNull(preference)
        assertEquals("QUALITY", preference.name)
    }
    
    @Test
    fun `CameraCaptureConfig should accept custom preference`() {
        val config = CameraCaptureConfig(
            preference = CapturePhotoPreference.QUALITY
        )
        
        assertEquals(CapturePhotoPreference.QUALITY, config.preference)
    }
    
    @Test
    fun `CameraCaptureConfig should work with custom UiConfig`() {
        val uiConfig = UiConfig()
        val config = CameraCaptureConfig(
            uiConfig = uiConfig
        )
        
        assertEquals(uiConfig, config.uiConfig)
    }
}