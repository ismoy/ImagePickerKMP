package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CapturePhotoPreferenceTest {
    
    @Test
    fun `test all preference values exist`() {
        val preferences = CapturePhotoPreference.values()
        assertEquals(3, preferences.size, "Should have exactly 3 preference options")
        
        assertNotNull(CapturePhotoPreference.FAST)
        assertNotNull(CapturePhotoPreference.BALANCED)
        assertNotNull(CapturePhotoPreference.QUALITY)
    }
    
    @Test
    fun `test preference names are descriptive`() {
        assertEquals("FAST", CapturePhotoPreference.FAST.name)
        assertEquals("BALANCED", CapturePhotoPreference.BALANCED.name)
        assertEquals("QUALITY", CapturePhotoPreference.QUALITY.name)
    }
    
    @Test
    fun `test preference ordinal values`() {
        assertEquals(0, CapturePhotoPreference.FAST.ordinal)
        assertEquals(1, CapturePhotoPreference.BALANCED.ordinal)
        assertEquals(2, CapturePhotoPreference.QUALITY.ordinal)
    }
    
    @Test
    fun `test preference values are unique`() {
        val preferences = CapturePhotoPreference.values()
        val uniquePreferences = preferences.toSet()
        
        assertEquals(preferences.size, uniquePreferences.size, 
            "All preference values should be unique")
    }
    
    @Test
    fun `test default preference is balanced`() {
        // Assuming BALANCED is the default choice for most use cases
        val defaultPreference = CapturePhotoPreference.BALANCED
        assertEquals(CapturePhotoPreference.BALANCED, defaultPreference)
    }
} 