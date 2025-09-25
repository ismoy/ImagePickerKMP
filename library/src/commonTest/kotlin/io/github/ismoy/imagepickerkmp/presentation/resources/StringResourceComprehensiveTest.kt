package io.github.ismoy.imagepickerkmp.presentation.resources

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StringResourceComprehensiveTest {

    @Test
    fun `StringResource should have all expected values`() {
        val values = StringResource.values()
        val expectedCount = 27 // Update this if more string resources are added
        
        assertTrue(values.size >= expectedCount, "Should have at least $expectedCount string resources")
    }

    @Test
    fun `StringResource should contain all camera permission related resources`() {
        val values = StringResource.values()
        val names = values.map { it.name }.toSet()
        
        assertTrue(names.contains("CAMERA_PERMISSION_REQUIRED"))
        assertTrue(names.contains("CAMERA_PERMISSION_DESCRIPTION"))
        assertTrue(names.contains("CAMERA_PERMISSION_DENIED"))
        assertTrue(names.contains("CAMERA_PERMISSION_DENIED_DESCRIPTION"))
        assertTrue(names.contains("CAMERA_PERMISSION_PERMANENTLY_DENIED"))
    }

    @Test
    fun `StringResource should contain all gallery permission related resources`() {
        val values = StringResource.values()
        val names = values.map { it.name }.toSet()
        
        assertTrue(names.contains("GALLERY_PERMISSION_REQUIRED"))
        assertTrue(names.contains("GALLERY_PERMISSION_DESCRIPTION"))
        assertTrue(names.contains("GALLERY_PERMISSION_DENIED"))
        assertTrue(names.contains("GALLERY_PERMISSION_DENIED_DESCRIPTION"))
        assertTrue(names.contains("GALLERY_GRANT_PERMISSION"))
        assertTrue(names.contains("GALLERY_BTN_SETTINGS"))
    }

    @Test
    fun `StringResource should contain all dialog related resources`() {
        val values = StringResource.values()
        val names = values.map { it.name }.toSet()
        
        assertTrue(names.contains("SELECT_OPTION_DIALOG_TITLE"))
        assertTrue(names.contains("TAKE_PHOTO_OPTION"))
        assertTrue(names.contains("SELECT_FROM_GALLERY_OPTION"))
        assertTrue(names.contains("CANCEL_OPTION"))
        assertTrue(names.contains("IMAGE_CONFIRMATION_TITLE"))
        assertTrue(names.contains("ACCEPT_BUTTON"))
        assertTrue(names.contains("RETRY_BUTTON"))
    }

    @Test
    fun `StringResource should contain all error related resources`() {
        val values = StringResource.values()
        val names = values.map { it.name }.toSet()
        
        assertTrue(names.contains("INVALID_CONTEXT_ERROR"))
        assertTrue(names.contains("PHOTO_CAPTURE_ERROR"))
        assertTrue(names.contains("GALLERY_SELECTION_ERROR"))
        assertTrue(names.contains("PERMISSION_ERROR"))
    }

    @Test
    fun `StringResource should contain all description related resources`() {
        val values = StringResource.values()
        val names = values.map { it.name }.toSet()
        
        assertTrue(names.contains("PREVIEW_IMAGE_DESCRIPTION"))
        assertTrue(names.contains("HD_QUALITY_DESCRIPTION"))
        assertTrue(names.contains("SD_QUALITY_DESCRIPTION"))
    }

    @Test
    fun `StringResource should contain settings related resources`() {
        val values = StringResource.values()
        val names = values.map { it.name }.toSet()
        
        assertTrue(names.contains("OPEN_SETTINGS"))
        assertTrue(names.contains("GRANT_PERMISSION"))
    }

    @Test
    fun `all StringResource values should have unique names`() {
        val values = StringResource.values()
        val names = values.map { it.name }
        val uniqueNames = names.toSet()
        
        assertEquals(names.size, uniqueNames.size, "All string resource names should be unique")
    }

    @Test
    fun `all StringResource values should have unique ordinals`() {
        val values = StringResource.values()
        val ordinals = values.map { it.ordinal }
        val uniqueOrdinals = ordinals.toSet()
        
        assertEquals(ordinals.size, uniqueOrdinals.size, "All string resource ordinals should be unique")
    }

    @Test
    fun `StringResource ordinals should be sequential`() {
        val values = StringResource.values()
        
        for (i in values.indices) {
            assertEquals(i, values[i].ordinal, "Ordinal at index $i should be $i")
        }
    }

    @Test
    fun `StringResource valueOf should work for all values`() {
        val values = StringResource.values()
        
        for (value in values) {
            val retrieved = StringResource.valueOf(value.name)
            assertEquals(value, retrieved, "valueOf should return correct enum for ${value.name}")
        }
    }

    @Test
    fun `StringResource toString should return name`() {
        val values = StringResource.values()
        
        for (value in values) {
            assertEquals(value.name, value.toString(), "toString should return name for ${value.name}")
        }
    }

    @Test
    fun `StringResource names should follow naming convention`() {
        val values = StringResource.values()
        
        for (value in values) {
            val name = value.name
            
            // Should be uppercase
            assertEquals(name.uppercase(), name, "$name should be uppercase")
            
            // Should only contain letters, numbers, and underscores
            assertTrue(name.matches(Regex("^[A-Z0-9_]+$")), "$name should only contain uppercase letters, numbers, and underscores")
            
            // Should not start or end with underscore
            assertTrue(!name.startsWith("_"), "$name should not start with underscore")
            assertTrue(!name.endsWith("_"), "$name should not end with underscore")
        }
    }

    @Test
    fun `StringResource should support equality comparison`() {
        val value1 = StringResource.CAMERA_PERMISSION_REQUIRED
        val value2 = StringResource.CAMERA_PERMISSION_REQUIRED
        val value3 = StringResource.GALLERY_PERMISSION_REQUIRED
        
        assertEquals(value1, value2)
        assertTrue(value1 != value3)
    }

    @Test
    fun `StringResource should support hash code consistency`() {
        val value1 = StringResource.CAMERA_PERMISSION_REQUIRED
        val value2 = StringResource.CAMERA_PERMISSION_REQUIRED
        
        assertEquals(value1.hashCode(), value2.hashCode())
    }

    @Test
    fun `StringResource should be comparable`() {
        val earlier = StringResource.values()[0]
        val later = StringResource.values().last()
        
        assertTrue(earlier < later, "Earlier enum value should be less than later")
        assertEquals(0, earlier.compareTo(earlier), "Enum should be equal to itself")
    }

    @Test
    fun `StringResource groupings should be logical`() {
        val values = StringResource.values()
        
        // Camera permission group
        val cameraPermissionResources = values.filter { it.name.contains("CAMERA_PERMISSION") }
        assertTrue(cameraPermissionResources.size >= 5, "Should have multiple camera permission resources")
        
        // Gallery permission group
        val galleryPermissionResources = values.filter { it.name.contains("GALLERY") }
        assertTrue(galleryPermissionResources.size >= 6, "Should have multiple gallery permission resources")
        
        // Error group
        val errorResources = values.filter { it.name.contains("ERROR") }
        assertTrue(errorResources.size >= 4, "Should have multiple error resources")
        
        // Description group
        val descriptionResources = values.filter { it.name.contains("DESCRIPTION") }
        assertTrue(descriptionResources.size >= 5, "Should have multiple description resources")
    }

    @Test
    fun `StringResource should have meaningful semantic groups`() {
        val values = StringResource.values()
        val names = values.map { it.name }
        
        // Verify semantic groupings exist
        val hasPermissionResources = names.any { it.contains("PERMISSION") }
        val hasButtonResources = names.any { it.contains("BUTTON") || it.contains("BTN") }
        val hasDialogResources = names.any { it.contains("DIALOG") }
        val hasOptionResources = names.any { it.contains("OPTION") }
        
        assertTrue(hasPermissionResources, "Should have permission-related resources")
        assertTrue(hasButtonResources, "Should have button-related resources")
        assertTrue(hasDialogResources, "Should have dialog-related resources")
        assertTrue(hasOptionResources, "Should have option-related resources")
    }

    @Test
    fun `StringResource enum should be properly structured`() {
        // Test that StringResource has expected values
        val values = StringResource.values()
        assertNotNull(values, "values() should return non-null array")
        assertTrue(values.isNotEmpty(), "values() should return non-empty array")
        
        // Test valueOf with a known value
        val knownValue = StringResource.valueOf("CAMERA_PERMISSION_REQUIRED")
        assertEquals(StringResource.CAMERA_PERMISSION_REQUIRED, knownValue)
    }
}
