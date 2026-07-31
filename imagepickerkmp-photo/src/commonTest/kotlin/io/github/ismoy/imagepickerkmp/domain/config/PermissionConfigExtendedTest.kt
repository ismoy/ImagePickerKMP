package io.github.ismoy.imagepickerkmp.domain.config

import io.github.ismoy.imagepickerkmp.config.PermissionConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PermissionConfigExtendedTest {

    private fun buildConfig(
        title: String = "Camera permission required",
        description: String = "Camera permission is required to capture photos",
        btn: String = "Open settings",
        titleDenied: String = "Camera permission denied",
        descriptionDenied: String = "Please grant the camera permission",
        btnDenied: String = "Grant permission",
        btnCancel: String = "Cancel"
    ) = PermissionConfig(
        titleDialogConfig = title,
        descriptionDialogConfig = description,
        btnDialogConfig = btn,
        titleDialogDenied = titleDenied,
        descriptionDialogDenied = descriptionDenied,
        btnDialogDenied = btnDenied,
        btnCancel = btnCancel
    )

    @Test
    fun permissionConfig_multilingualStrings_storedCorrectly() {
        val config = buildConfig(
            title = "Camera permission required",
            description = "Camera permission is required to capture photos",
            btn = "Open settings"
        )
        assertEquals("Camera permission required", config.titleDialogConfig)
        assertEquals("Camera permission is required to capture photos", config.descriptionDialogConfig)
        assertEquals("Open settings", config.btnDialogConfig)
    }

    @Test
    fun permissionConfig_veryLongStrings_storedCorrectly() {
        val longTitle = "A".repeat(200)
        val longDescription = "B".repeat(500)
        val config = buildConfig(title = longTitle, description = longDescription)
        assertEquals(longTitle, config.titleDialogConfig)
        assertEquals(longDescription, config.descriptionDialogConfig)
        assertTrue(config.titleDialogConfig.length == 200)
        assertTrue(config.descriptionDialogConfig.length == 500)
    }

    @Test
    fun permissionConfig_specialCharacters_storedCorrectly() {
        val config = buildConfig(
            title = "Camera Permission with special chars: &*^%",
            description = "Allow access to camera for photos with special characters: <>&"
        )
        assertEquals("Camera Permission with special chars: &*^%", config.titleDialogConfig)
        assertTrue(config.descriptionDialogConfig.contains("special"))
        assertTrue(config.descriptionDialogConfig.contains("characters"))
    }

    @Test
    fun permissionConfig_copy_createsIndependentInstance() {
        val original = buildConfig(title = "Original")
        val copied = original.copy(titleDialogConfig = "Modified")
        assertEquals("Original", original.titleDialogConfig)
        assertEquals("Modified", copied.titleDialogConfig)
        assertTrue(original !== copied)
    }

    @Test
    fun permissionConfig_copy_preservesAllOtherFields() {
        val original = buildConfig(
            title = "Title", description = "Description", btn = "Button",
            titleDenied = "DeniedTitle", descriptionDenied = "DeniedDescription", btnDenied = "DeniedButton"
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
    fun permissionConfig_newlineCharacters_storedCorrectly() {
        val config = buildConfig(
            title = "Camera\nPermission",
            description = "Line 1\nLine 2\nLine 3"
        )
        assertTrue(config.titleDialogConfig.contains("\n"))
        assertTrue(config.descriptionDialogConfig.contains("\n"))
        assertEquals(3, config.descriptionDialogConfig.split("\n").size)
    }

    @Test
    fun permissionConfig_equality_allFieldsEqual() {
        val a = buildConfig(title = "A", description = "B", btn = "C",
            titleDenied = "D", descriptionDenied = "E", btnDenied = "F", btnCancel = "G")
        val b = buildConfig(title = "A", description = "B", btn = "C",
            titleDenied = "D", descriptionDenied = "E", btnDenied = "F", btnCancel = "G")
        val c = buildConfig(title = "X", description = "B", btn = "C",
            titleDenied = "D", descriptionDenied = "E", btnDenied = "F", btnCancel = "G")
        assertEquals(a, b)
        assertTrue(a != c)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun permissionConfig_tabCharacters_storedCorrectly() {
        val config = buildConfig(
            title = "Title\t\tWith\tTabs",
            description = "   Spaces   And   Tabs\t\t"
        )
        assertTrue(config.titleDialogConfig.contains("\t"))
        assertTrue(config.descriptionDialogConfig.contains("   "))
    }

    @Test
    fun permissionConfig_destructuring_allSevenFields() {
        val config = buildConfig(
            title = "Title", description = "Description", btn = "Button",
            titleDenied = "DeniedTitle", descriptionDenied = "DeniedDescription",
            btnDenied = "DeniedButton", btnCancel = "CancelBtn"
        )
        val (title, desc, btn, deniedTitle, deniedDesc, deniedBtn, cancelBtn) = config
        assertEquals("Title", title)
        assertEquals("Description", desc)
        assertEquals("Button", btn)
        assertEquals("DeniedTitle", deniedTitle)
        assertEquals("DeniedDescription", deniedDesc)
        assertEquals("DeniedButton", deniedBtn)
        assertEquals("CancelBtn", cancelBtn)
    }

    @Test
    fun permissionConfig_builderLikeChainedCopy() {
        val config = buildConfig()
            .copy(titleDialogConfig = "Step 1")
            .copy(descriptionDialogConfig = "Step 2")
            .copy(btnDialogConfig = "Step 3")
        assertEquals("Step 1", config.titleDialogConfig)
        assertEquals("Step 2", config.descriptionDialogConfig)
        assertEquals("Step 3", config.btnDialogConfig)
    }
}
