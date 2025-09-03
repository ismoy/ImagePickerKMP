package io.github.ismoy.imagepickerkmp.domain.config

import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import org.junit.Test
import org.junit.Assert.*

class SimpleAndroidConfigTest {

    @Test
    fun galleryConfig_shouldHaveCorrectDefaults() {
        val config = GalleryConfig()
        
        assertEquals(30, config.selectionLimit)
        assertEquals(listOf(MimeType.IMAGE_ALL), config.mimeTypes)
        assertFalse(config.allowMultiple)
    }

    @Test
    fun galleryConfig_shouldAllowCustomValues() {
        val customMimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG)
        val config = GalleryConfig(
            selectionLimit = 5,
            mimeTypes = customMimeTypes,
            allowMultiple = true
        )
        
        assertEquals(5, config.selectionLimit)
        assertEquals(customMimeTypes, config.mimeTypes)
        assertTrue(config.allowMultiple)
    }

    @Test
    fun cameraCaptureConfig_shouldHaveCorrectDefaults() {
        val config = CameraCaptureConfig()
        
        assertEquals(CapturePhotoPreference.QUALITY, config.preference)
        assertNotNull(config.uiConfig)
        assertNotNull(config.galleryConfig)
    }

    @Test
    fun uiConfig_shouldHaveNullDefaults() {
        val config = UiConfig()
        
        assertNull(config.buttonColor)
        assertNull(config.iconColor)
        assertNull(config.buttonSize)
    }
}
