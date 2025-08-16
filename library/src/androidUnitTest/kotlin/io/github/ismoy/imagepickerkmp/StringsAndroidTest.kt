package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.presentation.resources.StringResource
import io.github.ismoy.imagepickerkmp.presentation.resources.getStringResource
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StringsAndroidTest {
    
    @Test
    fun testGetStringResourceReturnsValidStringForAllResources() {
        // Test all string resources return valid strings
        StringResource.entries.forEach { stringResource ->
            val result = getStringResource(stringResource)
            assertNotNull(result)
            assertTrue(result.isNotEmpty())
        }
    }
    
    @Test
    fun testGetStringResourceForSpecificResources() {
        // Test specific string resources
        val cameraPermission =
            getStringResource(StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED)
        val openSettings = getStringResource(StringResource.OPEN_SETTINGS)
        
        assertNotNull(cameraPermission)
        assertNotNull(openSettings)
        assertTrue(cameraPermission.isNotEmpty())
        assertTrue(openSettings.isNotEmpty())
    }
    
    @Test
    fun testStringResourceEnumValues() {
        // Test that all enum values exist
        val expectedValues = listOf(
            "CAMERA_PERMISSION_REQUIRED",
            "CAMERA_PERMISSION_DESCRIPTION",
            "OPEN_SETTINGS",
            "CAMERA_PERMISSION_DENIED",
            "CAMERA_PERMISSION_DENIED_DESCRIPTION",
            "GRANT_PERMISSION",
            "CAMERA_PERMISSION_PERMANENTLY_DENIED",
            "IMAGE_CONFIRMATION_TITLE",
            "ACCEPT_BUTTON",
            "RETRY_BUTTON",
            "SELECT_OPTION_DIALOG_TITLE",
            "TAKE_PHOTO_OPTION",
            "SELECT_FROM_GALLERY_OPTION",
            "CANCEL_OPTION",
            "PREVIEW_IMAGE_DESCRIPTION",
            "HD_QUALITY_DESCRIPTION",
            "SD_QUALITY_DESCRIPTION",
            "INVALID_CONTEXT_ERROR",
            "PHOTO_CAPTURE_ERROR",
            "GALLERY_SELECTION_ERROR",
            "PERMISSION_ERROR"
        )
        
        val actualValues = StringResource.values().map { it.name }
        expectedValues.forEach { expected ->
            assertTrue(actualValues.contains(expected), "Missing StringResource: $expected")
        }
    }
} 