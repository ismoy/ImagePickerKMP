package io.github.ismoy.imagepickerkmp.domain.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PermissionConfigExtendedTest {

    @Test
    fun `PermissionConfig should handle multilingual strings`() {
        val multilingualConfig = PermissionConfig(
            titleDialogConfig = "Camera permission required",
            descriptionDialogConfig = "Camera permission is required to capture photos",
            btnDialogConfig = "Open settings"
        )
        
        assertEquals("Camera permission required", multilingualConfig.titleDialogConfig)
        assertEquals("Camera permission is required to capture photos", multilingualConfig.descriptionDialogConfig)
        assertEquals("Open settings", multilingualConfig.btnDialogConfig)
    }

    @Test
    fun `PermissionConfig should support very long text strings`() {
        val longTitle = "A".repeat(200)
        val longDescription = "B".repeat(500)
        
        val config = PermissionConfig(
            titleDialogConfig = longTitle,
            descriptionDialogConfig = longDescription
        )
        
        assertEquals(longTitle, config.titleDialogConfig)
        assertEquals(longDescription, config.descriptionDialogConfig)
        assertTrue(config.titleDialogConfig.length == 200)
        assertTrue(config.descriptionDialogConfig.length == 500)
    }

    @Test
    fun `PermissionConfig should handle special characters and emojis`() {
        val config = PermissionConfig(
            titleDialogConfig = "Camera Permission with special chars",
            descriptionDialogConfig = "Allow access to camera for photos with special characters"
        )
        
        assertEquals("Camera Permission with special chars", config.titleDialogConfig)
        assertTrue(config.descriptionDialogConfig.contains("special"))
        assertTrue(config.descriptionDialogConfig.contains("characters"))
    }

    @Test
    fun `PermissionConfig copy should create independent instance`() {
        val original = PermissionConfig(titleDialogConfig = "Original")
        val copied = original.copy(titleDialogConfig = "Modified")
        
        assertEquals("Original", original.titleDialogConfig)
        assertEquals("Modified", copied.titleDialogConfig)
        assertTrue(original !== copied) // Different instances
    }

    @Test
    fun `PermissionConfig should maintain all fields when copying single field`() {
        val original = PermissionConfig(
            titleDialogConfig = "Title",
            descriptionDialogConfig = "Description",
            btnDialogConfig = "Button"
        )
        
        val copied = original.copy(titleDialogConfig = "New Title")
        
        assertEquals("New Title", copied.titleDialogConfig)
        assertEquals("Description", copied.descriptionDialogConfig)
        assertEquals("Button", copied.btnDialogConfig)
        assertEquals(original.titleDialogDenied, copied.titleDialogDenied)
        assertEquals(original.descriptionDialogDenied, copied.descriptionDialogDenied)
        assertEquals(original.btnDialogDenied, copied.btnDialogDenied)
    }

    @Test
    fun `PermissionConfig should handle newline characters in strings`() {
        val config = PermissionConfig(
            titleDialogConfig = "Camera\nPermission",
            descriptionDialogConfig = "Line 1\nLine 2\nLine 3"
        )
        
        assertTrue(config.titleDialogConfig.contains("\n"))
        assertTrue(config.descriptionDialogConfig.contains("\n"))
        assertEquals(3, config.descriptionDialogConfig.split("\n").size)
    }

    @Test
    fun `PermissionConfig equals should work correctly with all combinations`() {
        val config1 = PermissionConfig("A", "B", "C", "D", "E", "F")
        val config2 = PermissionConfig("A", "B", "C", "D", "E", "F")
        val config3 = PermissionConfig("X", "B", "C", "D", "E", "F")
        
        assertEquals(config1, config2)
        assertTrue(config1 != config3)
        assertEquals(config1.hashCode(), config2.hashCode())
    }

    @Test
    fun `PermissionConfig should handle tab and special whitespace characters`() {
        val config = PermissionConfig(
            titleDialogConfig = "Title\t\tWith\tTabs",
            descriptionDialogConfig = "   Spaces   And   Tabs\t\t"
        )
        
        assertTrue(config.titleDialogConfig.contains("\t"))
        assertTrue(config.descriptionDialogConfig.contains("   "))
    }

    @Test
    fun `PermissionConfig destructuring should work for all properties`() {
        val config = PermissionConfig(
            titleDialogConfig = "Title",
            descriptionDialogConfig = "Description", 
            btnDialogConfig = "Button",
            titleDialogDenied = "DeniedTitle",
            descriptionDialogDenied = "DeniedDescription",
            btnDialogDenied = "DeniedButton"
        )
        
        val (title, desc, btn, deniedTitle, deniedDesc, deniedBtn) = config
        
        assertEquals("Title", title)
        assertEquals("Description", desc)
        assertEquals("Button", btn)
        assertEquals("DeniedTitle", deniedTitle)
        assertEquals("DeniedDescription", deniedDesc)
        assertEquals("DeniedButton", deniedBtn)
    }

    @Test
    fun `PermissionConfig should support builder-like pattern through copy`() {
        val config = PermissionConfig()
            .copy(titleDialogConfig = "Step 1")
            .copy(descriptionDialogConfig = "Step 2")
            .copy(btnDialogConfig = "Step 3")
        
        assertEquals("Step 1", config.titleDialogConfig)
        assertEquals("Step 2", config.descriptionDialogConfig)
        assertEquals("Step 3", config.btnDialogConfig)
    }
}
