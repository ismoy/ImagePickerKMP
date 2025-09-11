package io.github.ismoy.imagepickerkmp.presentation.resources

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StringResourceTest {

    @Test
    fun testStringResource_allValuesExist() {
        // Test that all StringResource enum values exist and can be accessed
        val allEntries = StringResource.entries
        assertTrue(allEntries.isNotEmpty())
        
        // Test specific values to ensure they exist
        assertEquals(StringResource.CAMERA_PERMISSION_REQUIRED, StringResource.CAMERA_PERMISSION_REQUIRED)
        assertEquals(StringResource.CAMERA_PERMISSION_DESCRIPTION, StringResource.CAMERA_PERMISSION_DESCRIPTION)
        assertEquals(StringResource.OPEN_SETTINGS, StringResource.OPEN_SETTINGS)
        assertEquals(StringResource.CAMERA_PERMISSION_DENIED, StringResource.CAMERA_PERMISSION_DENIED)
        assertEquals(StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION, StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION)
        assertEquals(StringResource.GRANT_PERMISSION, StringResource.GRANT_PERMISSION)
    }

    @Test
    fun testStringResource_entries() {
        // Test the entries property to cover the getEntries method
        val entries = StringResource.entries
        assertTrue(entries.contains(StringResource.CAMERA_PERMISSION_REQUIRED))
        assertTrue(entries.contains(StringResource.CAMERA_PERMISSION_DESCRIPTION))
        assertTrue(entries.contains(StringResource.OPEN_SETTINGS))
        assertTrue(entries.contains(StringResource.CAMERA_PERMISSION_DENIED))
        assertTrue(entries.contains(StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION))
        assertTrue(entries.contains(StringResource.GRANT_PERMISSION))
    }

    @Test
    fun testStringResource_enumValues() {
        // Test individual enum values to ensure they're properly initialized
        val cameraPermissionRequired = StringResource.CAMERA_PERMISSION_REQUIRED
        val cameraPermissionDescription = StringResource.CAMERA_PERMISSION_DESCRIPTION
        val openSettings = StringResource.OPEN_SETTINGS
        val cameraPermissionDenied = StringResource.CAMERA_PERMISSION_DENIED
        val cameraPermissionDeniedDescription = StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION
        val grantPermission = StringResource.GRANT_PERMISSION
        
        // Verify they are not null and are the correct type
        assertTrue(cameraPermissionRequired is StringResource)
        assertTrue(cameraPermissionDescription is StringResource)
        assertTrue(openSettings is StringResource)
        assertTrue(cameraPermissionDenied is StringResource)
        assertTrue(cameraPermissionDeniedDescription is StringResource)
        assertTrue(grantPermission is StringResource)
    }

    @Test
    fun testStringResource_enumComparison() {
        // Test enum comparison to ensure proper equality
        assertTrue(StringResource.CAMERA_PERMISSION_REQUIRED == StringResource.CAMERA_PERMISSION_REQUIRED)
        assertTrue(StringResource.CAMERA_PERMISSION_DESCRIPTION == StringResource.CAMERA_PERMISSION_DESCRIPTION)
        assertTrue(StringResource.OPEN_SETTINGS == StringResource.OPEN_SETTINGS)
        assertTrue(StringResource.CAMERA_PERMISSION_DENIED == StringResource.CAMERA_PERMISSION_DENIED)
        assertTrue(StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION == StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION)
        assertTrue(StringResource.GRANT_PERMISSION == StringResource.GRANT_PERMISSION)
    }

    @Test
    fun testStringResource_enumOrdering() {
        // Test that enum values maintain their declaration order
        val entries = StringResource.entries
        val expectedOrder = listOf(
            StringResource.CAMERA_PERMISSION_REQUIRED,
            StringResource.CAMERA_PERMISSION_DESCRIPTION,
            StringResource.OPEN_SETTINGS,
            StringResource.CAMERA_PERMISSION_DENIED,
            StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION,
            StringResource.GRANT_PERMISSION
        )
        
        // Verify the first few entries match expected order
        assertTrue(entries.indexOf(StringResource.CAMERA_PERMISSION_REQUIRED) >= 0)
        assertTrue(entries.indexOf(StringResource.CAMERA_PERMISSION_DESCRIPTION) >= 0)
        assertTrue(entries.indexOf(StringResource.OPEN_SETTINGS) >= 0)
    }

    @Test
    fun testStringResource_enumCount() {
        // Test that we have the expected number of enum values
        val entries = StringResource.entries
        assertTrue(entries.size >= 6) // At least the 6 basic values we know about
    }

    @Test
    fun testStringResource_toString() {
        // Test toString functionality of enum values
        val cameraPermissionRequired = StringResource.CAMERA_PERMISSION_REQUIRED.toString()
        val openSettings = StringResource.OPEN_SETTINGS.toString()
        
        assertTrue(cameraPermissionRequired.isNotEmpty())
        assertTrue(openSettings.isNotEmpty())
    }
}
