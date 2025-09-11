package io.github.ismoy.imagepickerkmp.domain.config

import kotlin.test.Test
import kotlin.test.assertEquals

class PermissionConfigTest {

    @Test
    fun testPermissionConfig_defaultValues() {
        val config = PermissionConfig()
        
        assertEquals("Camera permission required", config.titleDialogConfig)
        assertEquals("Camera permission is required to capture photos. Please grant it in settings", config.descriptionDialogConfig)
        assertEquals("Open settings", config.btnDialogConfig)
        assertEquals("Camera permission denied", config.titleDialogDenied)
        assertEquals("Camera permission is required to capture photos. Please grant the permissions", config.descriptionDialogDenied)
        assertEquals("Grant permission", config.btnDialogDenied)
    }

    @Test
    fun testPermissionConfig_customValues() {
        val config = PermissionConfig(
            titleDialogConfig = "Custom Camera Permission",
            descriptionDialogConfig = "Custom description for camera permission",
            btnDialogConfig = "Custom Settings",
            titleDialogDenied = "Custom Permission Denied",
            descriptionDialogDenied = "Custom denial description",
            btnDialogDenied = "Custom Grant"
        )
        
        assertEquals("Custom Camera Permission", config.titleDialogConfig)
        assertEquals("Custom description for camera permission", config.descriptionDialogConfig)
        assertEquals("Custom Settings", config.btnDialogConfig)
        assertEquals("Custom Permission Denied", config.titleDialogDenied)
        assertEquals("Custom denial description", config.descriptionDialogDenied)
        assertEquals("Custom Grant", config.btnDialogDenied)
    }

    @Test
    fun testPermissionConfig_partialCustomValues() {
        val config = PermissionConfig(
            titleDialogConfig = "Custom Title",
            btnDialogConfig = "Custom Button"
        )
        
        assertEquals("Custom Title", config.titleDialogConfig)
        assertEquals("Camera permission is required to capture photos. Please grant it in settings", config.descriptionDialogConfig)
        assertEquals("Custom Button", config.btnDialogConfig)
        assertEquals("Camera permission denied", config.titleDialogDenied)
        assertEquals("Camera permission is required to capture photos. Please grant the permissions", config.descriptionDialogDenied)
        assertEquals("Grant permission", config.btnDialogDenied)
    }

    @Test
    fun testPermissionConfig_dataClassEquality() {
        val config1 = PermissionConfig(
            titleDialogConfig = "Title",
            descriptionDialogConfig = "Description",
            btnDialogConfig = "Button",
            titleDialogDenied = "Denied Title",
            descriptionDialogDenied = "Denied Description",
            btnDialogDenied = "Denied Button"
        )

        val config2 = PermissionConfig(
            titleDialogConfig = "Title",
            descriptionDialogConfig = "Description",
            btnDialogConfig = "Button",
            titleDialogDenied = "Denied Title",
            descriptionDialogDenied = "Denied Description",
            btnDialogDenied = "Denied Button"
        )

        assertEquals(config1, config2)
        assertEquals(config1.hashCode(), config2.hashCode())
    }

    @Test
    fun testPermissionConfig_toString() {
        val config = PermissionConfig(
            titleDialogConfig = "Test Title",
            descriptionDialogConfig = "Test Description",
            btnDialogConfig = "Test Button"
        )

        val string = config.toString()
        assert(string.contains("Test Title"))
        assert(string.contains("Test Description"))
        assert(string.contains("Test Button"))
    }

    @Test
    fun testPermissionConfig_copyWithModification() {
        val original = PermissionConfig()
        val modified = original.copy(
            titleDialogConfig = "Modified Title",
            btnDialogConfig = "Modified Button"
        )
        
        assertEquals("Modified Title", modified.titleDialogConfig)
        assertEquals("Camera permission is required to capture photos. Please grant it in settings", modified.descriptionDialogConfig)
        assertEquals("Modified Button", modified.btnDialogConfig)
        assertEquals("Camera permission denied", modified.titleDialogDenied)
        assertEquals("Camera permission is required to capture photos. Please grant the permissions", modified.descriptionDialogDenied)
        assertEquals("Grant permission", modified.btnDialogDenied)
    }
}
