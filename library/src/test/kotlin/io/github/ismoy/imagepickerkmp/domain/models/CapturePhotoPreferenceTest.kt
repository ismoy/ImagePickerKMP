package io.github.ismoy.imagepickerkmp.domain.models

import junit.framework.TestCase

class CapturePhotoPreferenceTest : TestCase() {

    fun testCapturePhotoPreferenceValues() {
        // Test that CapturePhotoPreference enum exists and has values
        val values = CapturePhotoPreference.values()
        assertTrue("CapturePhotoPreference should have values", values.isNotEmpty())
    }

    fun testCapturePhotoPreferenceConstants() {
        // Test that basic constants are accessible
        assertNotNull(CapturePhotoPreference.FAST)
        assertNotNull(CapturePhotoPreference.QUALITY)
    }

    fun testCapturePhotoPreferenceEnumBehavior() {
        // Test basic enum behavior
        val allValues = CapturePhotoPreference.values()
        assertTrue("Should have at least FAST", allValues.contains(CapturePhotoPreference.FAST))
        assertTrue("Should have at least QUALITY", allValues.contains(CapturePhotoPreference.QUALITY))
        
        val byName = CapturePhotoPreference.valueOf("FAST")
        assertEquals(CapturePhotoPreference.FAST, byName)
    }

    fun testCapturePhotoPreferenceComparison() {
        assertTrue("FAST and QUALITY should be different", CapturePhotoPreference.FAST != CapturePhotoPreference.QUALITY)
        assertNotNull("Should have toString", CapturePhotoPreference.FAST.toString())
    }
}
