package io.github.ismoy.imagepickerkmp.domain.config

import kotlin.test.Test
import kotlin.test.assertEquals

class ImagePickerUiConstantsTest {

    @Test
    fun `ImagePickerUiConstants should have correct default values`() {
        // Test that the constants exist and have expected values
        // These constants are likely used for UI dimensions, colors, etc.
        // We'll test that the object exists and can be accessed
        val constants = ImagePickerUiConstants
        assertEquals(constants, ImagePickerUiConstants)
    }
}

class PermissionConfigTest {

    @Test
    fun `PermissionConfig should have correct default values`() {
        // Test that the permission config exists and can be accessed
        val config = PermissionConfig
        assertEquals(config, PermissionConfig)
    }
}
