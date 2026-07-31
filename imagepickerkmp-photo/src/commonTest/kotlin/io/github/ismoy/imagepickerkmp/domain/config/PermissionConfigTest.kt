package io.github.ismoy.imagepickerkmp.domain.config

import io.github.ismoy.imagepickerkmp.config.PermissionConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit tests for [PermissionConfig].
 * All fields are required — no default constructor exists.
 */
class PermissionConfigTest {

    /** Helper that builds a fully-specified [PermissionConfig] with sensible defaults. */
    private fun buildConfig(
        title: String = "Camera permission required",
        description: String = "Camera permission is required to capture photos. Please grant it in settings",
        btn: String = "Open settings",
        titleDenied: String = "Camera permission denied",
        descriptionDenied: String = "Camera permission is required to capture photos. Please grant the permissions",
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
    fun permissionConfig_allFieldsAccessible() {
        val config = buildConfig()
        assertNotNull(config.titleDialogConfig)
        assertNotNull(config.descriptionDialogConfig)
        assertNotNull(config.btnDialogConfig)
        assertNotNull(config.titleDialogDenied)
        assertNotNull(config.descriptionDialogDenied)
        assertNotNull(config.btnDialogDenied)
        assertNotNull(config.btnCancel)
    }

    @Test
    fun permissionConfig_storedValuesMatchInputs() {
        val config = buildConfig(
            title = "Camera permission required",
            description = "Camera permission is required to capture photos. Please grant it in settings",
            btn = "Open settings",
            titleDenied = "Camera permission denied",
            descriptionDenied = "Camera permission is required to capture photos. Please grant the permissions",
            btnDenied = "Grant permission",
            btnCancel = "Cancel"
        )
        assertEquals("Camera permission required", config.titleDialogConfig)
        assertEquals("Camera permission is required to capture photos. Please grant it in settings", config.descriptionDialogConfig)
        assertEquals("Open settings", config.btnDialogConfig)
        assertEquals("Camera permission denied", config.titleDialogDenied)
        assertEquals("Camera permission is required to capture photos. Please grant the permissions", config.descriptionDialogDenied)
        assertEquals("Grant permission", config.btnDialogDenied)
        assertEquals("Cancel", config.btnCancel)
    }

    @Test
    fun permissionConfig_customValues_storedCorrectly() {
        val config = buildConfig(
            title = "Custom Title",
            description = "Custom Description",
            btn = "Custom Button",
            titleDenied = "Custom Denied Title",
            descriptionDenied = "Custom Denied Description",
            btnDenied = "Custom Denied Button",
            btnCancel = "Custom Cancel"
        )
        assertEquals("Custom Title", config.titleDialogConfig)
        assertEquals("Custom Description", config.descriptionDialogConfig)
        assertEquals("Custom Button", config.btnDialogConfig)
        assertEquals("Custom Denied Title", config.titleDialogDenied)
        assertEquals("Custom Denied Description", config.descriptionDialogDenied)
        assertEquals("Custom Denied Button", config.btnDialogDenied)
        assertEquals("Custom Cancel", config.btnCancel)
    }

    @Test
    fun permissionConfig_equality_sameValues() {
        val a = buildConfig(title = "Test Title", description = "Test Description")
        val b = buildConfig(title = "Test Title", description = "Test Description")
        assertEquals(a, b)
    }

    @Test
    fun permissionConfig_equality_differentTitle_notEqual() {
        val a = buildConfig(title = "Title A")
        val b = buildConfig(title = "Title B")
        assertTrue(a != b)
    }

    @Test
    fun permissionConfig_hashCode_sameForEqualInstances() {
        val a = buildConfig(title = "Test")
        val b = buildConfig(title = "Test")
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun permissionConfig_copy_changesOnlySpecifiedField() {
        val original = buildConfig(title = "Original Title")
        val copied = original.copy(titleDialogConfig = "Modified Title")
        assertEquals("Modified Title", copied.titleDialogConfig)
        assertEquals(original.descriptionDialogConfig, copied.descriptionDialogConfig)
        assertEquals(original.btnDialogConfig, copied.btnDialogConfig)
        assertEquals(original.titleDialogDenied, copied.titleDialogDenied)
        assertEquals(original.descriptionDialogDenied, copied.descriptionDialogDenied)
        assertEquals(original.btnDialogDenied, copied.btnDialogDenied)
        assertEquals(original.btnCancel, copied.btnCancel)
    }

    @Test
    fun permissionConfig_copy_doesNotMutateOriginal() {
        val original = buildConfig(title = "Original")
        original.copy(titleDialogConfig = "Modified")
        assertEquals("Original", original.titleDialogConfig)
    }

    @Test
    fun permissionConfig_toString_containsClassNameAndValues() {
        val config = buildConfig(title = "Test Title")
        val str = config.toString()
        assertNotNull(str)
        assertTrue(str.contains("PermissionConfig"))
        assertTrue(str.contains("Test Title"))
    }

    @Test
    fun permissionConfig_emptyStrings_storedCorrectly() {
        val config = buildConfig(title = "", description = "", btn = "",
            titleDenied = "", descriptionDenied = "", btnDenied = "", btnCancel = "")
        assertEquals("", config.titleDialogConfig)
        assertEquals("", config.descriptionDialogConfig)
        assertEquals("", config.btnDialogConfig)
        assertEquals("", config.titleDialogDenied)
        assertEquals("", config.descriptionDialogDenied)
        assertEquals("", config.btnDialogDenied)
        assertEquals("", config.btnCancel)
    }

    @Test
    fun permissionConfig_longStrings_storedCorrectly() {
        val longTitle = "A".repeat(1000)
        val longDescription = "B".repeat(2000)
        val config = buildConfig(title = longTitle, description = longDescription)
        assertEquals(longTitle, config.titleDialogConfig)
        assertEquals(longDescription, config.descriptionDialogConfig)
        assertEquals(1000, config.titleDialogConfig.length)
        assertEquals(2000, config.descriptionDialogConfig.length)
    }

    @Test
    fun permissionConfig_destructuring_worksForAllSevenFields() {
        val config = buildConfig(
            title = "T", description = "D", btn = "B",
            titleDenied = "TD", descriptionDenied = "DD", btnDenied = "BD",
            btnCancel = "C"
        )
        val (t, d, b, td, dd, bd, c) = config
        assertEquals("T", t)
        assertEquals("D", d)
        assertEquals("B", b)
        assertEquals("TD", td)
        assertEquals("DD", dd)
        assertEquals("BD", bd)
        assertEquals("C", c)
    }

    @Test
    fun permissionConfig_builderLikeChainedCopy() {
        val config = buildConfig()
            .copy(titleDialogConfig = "Step1")
            .copy(descriptionDialogConfig = "Step2")
            .copy(btnDialogConfig = "Step3")
        assertEquals("Step1", config.titleDialogConfig)
        assertEquals("Step2", config.descriptionDialogConfig)
        assertEquals("Step3", config.btnDialogConfig)
    }
}
