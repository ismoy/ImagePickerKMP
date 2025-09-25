package io.github.ismoy.imagepickerkmp.domain.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PermissionConfigTest {

    @Test
    fun `PermissionConfig should be created with default values`() {
        val config = PermissionConfig()
        
        assertNotNull(config.titleDialogConfig)
        assertNotNull(config.descriptionDialogConfig)
        assertNotNull(config.btnDialogConfig)
        assertNotNull(config.titleDialogDenied)
        assertNotNull(config.descriptionDialogDenied)
        assertNotNull(config.btnDialogDenied)
    }

    @Test
    fun `PermissionConfig should have correct default values`() {
        val config = PermissionConfig()
        
        assertEquals("Camera permission required", config.titleDialogConfig)
        assertEquals("Camera permission is required to capture photos. Please grant it in settings", config.descriptionDialogConfig)
        assertEquals("Open settings", config.btnDialogConfig)
        assertEquals("Camera permission denied", config.titleDialogDenied)
        assertEquals("Camera permission is required to capture photos. Please grant the permissions", config.descriptionDialogDenied)
        assertEquals("Grant permission", config.btnDialogDenied)
    }

    @Test
    fun `PermissionConfig should be created with custom values`() {
        val config = PermissionConfig(
            titleDialogConfig = "Custom Title",
            descriptionDialogConfig = "Custom Description",
            btnDialogConfig = "Custom Button",
            titleDialogDenied = "Custom Denied Title",
            descriptionDialogDenied = "Custom Denied Description",
            btnDialogDenied = "Custom Denied Button"
        )
        
        assertEquals("Custom Title", config.titleDialogConfig)
        assertEquals("Custom Description", config.descriptionDialogConfig)
        assertEquals("Custom Button", config.btnDialogConfig)
        assertEquals("Custom Denied Title", config.titleDialogDenied)
        assertEquals("Custom Denied Description", config.descriptionDialogDenied)
        assertEquals("Custom Denied Button", config.btnDialogDenied)
    }

    @Test
    fun `PermissionConfig should support partial customization`() {
        val config = PermissionConfig(
            titleDialogConfig = "Custom Title Only"
        )
        
        assertEquals("Custom Title Only", config.titleDialogConfig)
        // Other values should remain default
        assertEquals("Camera permission is required to capture photos. Please grant it in settings", config.descriptionDialogConfig)
        assertEquals("Open settings", config.btnDialogConfig)
    }

    @Test
    fun `PermissionConfig should be data class with equality`() {
        val config1 = PermissionConfig(
            titleDialogConfig = "Test Title",
            descriptionDialogConfig = "Test Description"
        )
        
        val config2 = PermissionConfig(
            titleDialogConfig = "Test Title",
            descriptionDialogConfig = "Test Description"
        )
        
        val config3 = PermissionConfig(
            titleDialogConfig = "Different Title",
            descriptionDialogConfig = "Test Description"
        )
        
        assertEquals(config1, config2)
        assertTrue(config1 != config3)
    }

    @Test
    fun `PermissionConfig should support copy functionality`() {
        val originalConfig = PermissionConfig(
            titleDialogConfig = "Original Title"
        )
        
        val copiedConfig = originalConfig.copy(
            titleDialogConfig = "Modified Title"
        )
        
        assertEquals("Modified Title", copiedConfig.titleDialogConfig)
        assertEquals(originalConfig.descriptionDialogConfig, copiedConfig.descriptionDialogConfig)
        assertEquals(originalConfig.btnDialogConfig, copiedConfig.btnDialogConfig)
    }

    @Test
    fun `PermissionConfig should have meaningful toString`() {
        val config = PermissionConfig(
            titleDialogConfig = "Test Title"
        )
        
        val stringRepresentation = config.toString()
        assertNotNull(stringRepresentation)
        assertTrue(stringRepresentation.contains("PermissionConfig"))
        assertTrue(stringRepresentation.contains("Test Title"))
    }

    @Test
    fun `PermissionConfig should handle empty strings`() {
        val config = PermissionConfig(
            titleDialogConfig = "",
            descriptionDialogConfig = "",
            btnDialogConfig = "",
            titleDialogDenied = "",
            descriptionDialogDenied = "",
            btnDialogDenied = ""
        )
        
        assertEquals("", config.titleDialogConfig)
        assertEquals("", config.descriptionDialogConfig)
        assertEquals("", config.btnDialogConfig)
        assertEquals("", config.titleDialogDenied)
        assertEquals("", config.descriptionDialogDenied)
        assertEquals("", config.btnDialogDenied)
    }

    @Test
    fun `PermissionConfig should handle long strings`() {
        val longString = "A".repeat(1000)
        
        val config = PermissionConfig(
            titleDialogConfig = longString,
            descriptionDialogConfig = longString
        )
        
        assertEquals(longString, config.titleDialogConfig)
        assertEquals(longString, config.descriptionDialogConfig)
    }

    @Test
    fun `PermissionConfig should handle special characters`() {
        val specialString = "Special characters: abcdefghijk n u chinese symbols and symbols"
        
        val config = PermissionConfig(
            titleDialogConfig = specialString
        )
        
        assertEquals(specialString, config.titleDialogConfig)
    }

    @Test
    fun `PermissionConfig hashCode should be consistent`() {
        val config1 = PermissionConfig(titleDialogConfig = "Test")
        val config2 = PermissionConfig(titleDialogConfig = "Test")
        
        assertEquals(config1.hashCode(), config2.hashCode())
    }

    @Test
    fun `PermissionConfig component functions should work`() {
        val config = PermissionConfig(
            titleDialogConfig = "Title",
            descriptionDialogConfig = "Description",
            btnDialogConfig = "Button"
        )
        
        val (title, description, button, titleDenied, descriptionDenied, buttonDenied) = config
        
        assertEquals("Title", title)
        assertEquals("Description", description)
        assertEquals("Button", button)
        assertNotNull(titleDenied) // Default value
        assertNotNull(descriptionDenied) // Default value
        assertNotNull(buttonDenied) // Default value
    }
}
