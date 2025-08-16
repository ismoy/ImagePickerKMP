package io.github.ismoy.imagepickerkmp

import androidx.camera.core.ImageCapture
import io.github.ismoy.imagepickerkmp.data.dataSource.getCaptureMode
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import org.junit.Test
import kotlin.test.assertEquals

class CapturePhotoPreferenceAndroidTest {
    
    @Test
    fun testGetCaptureModeReturnsCorrectImageCaptureQualityForFast() {
        // When
        val result = getCaptureMode(CapturePhotoPreference.FAST)
        
        // Then
        assertEquals(ImageCapture.CAPTURE_MODE_ZERO_SHUTTER_LAG, result)
    }
    
    @Test
    fun testGetCaptureModeReturnsCorrectImageCaptureQualityForBalanced() {
        // When
        val result = getCaptureMode(CapturePhotoPreference.BALANCED)
        
        // Then
        assertEquals(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY, result)
    }
    
    @Test
    fun testGetCaptureModeReturnsCorrectImageCaptureQualityForQuality() {
        // When
        val result = getCaptureMode(CapturePhotoPreference.QUALITY)
        
        // Then
        assertEquals(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY, result)
    }
    
    @Test
    fun testAllCapturePhotoPreferenceValuesAreCovered() {
        // Test that all enum values are handled
        CapturePhotoPreference.entries.forEach { preference ->
            val result = getCaptureMode(preference)
            assert(result in listOf(
                ImageCapture.CAPTURE_MODE_ZERO_SHUTTER_LAG,
                ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY,
                ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
            ))
        }
    }
} 