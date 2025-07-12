package io.github.ismoy.imagepickerkmp

import kotlin.test.Test
import kotlin.test.assertEquals

class StringResourceTest {
    
    @Test
    fun `test string resource enum has expected count`() {
        // Verify that we have all expected string resources
        val expectedCount = 21 // Ajusta este número si cambias el enum
        assertEquals(expectedCount, StringResource.values().size, 
            "StringResource enum should have exactly $expectedCount values")
    }
    
    @Test
    fun `test string resources are unique`() {
        val resources = StringResource.values()
        val uniqueResources = resources.toSet()
        
        assertEquals(resources.size, uniqueResources.size, 
            "All StringResource values should be unique")
    }
    
    @Test
    fun `test all string resource enum values exist`() {
        // Test that all expected enum values exist
        val expectedValues = listOf(
            StringResource.CAMERA_PERMISSION_REQUIRED,
            StringResource.CAMERA_PERMISSION_DESCRIPTION,
            StringResource.OPEN_SETTINGS,
            StringResource.CAMERA_PERMISSION_DENIED,
            StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION,
            StringResource.GRANT_PERMISSION,
            StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED,
            StringResource.IMAGE_CONFIRMATION_TITLE,
            StringResource.ACCEPT_BUTTON,
            StringResource.RETRY_BUTTON,
            StringResource.SELECT_OPTION_DIALOG_TITLE,
            StringResource.TAKE_PHOTO_OPTION,
            StringResource.SELECT_FROM_GALLERY_OPTION,
            StringResource.CANCEL_OPTION,
            StringResource.PREVIEW_IMAGE_DESCRIPTION,
            StringResource.HD_QUALITY_DESCRIPTION,
            StringResource.SD_QUALITY_DESCRIPTION,
            StringResource.INVALID_CONTEXT_ERROR,
            StringResource.PHOTO_CAPTURE_ERROR,
            StringResource.GALLERY_SELECTION_ERROR,
            StringResource.PERMISSION_ERROR
        )
        
        assertEquals(expectedValues.size, StringResource.values().size,
            "All expected string resources should be present")
    }
} 