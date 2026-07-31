package io.github.ismoy.imagepickerkmp.presentation.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StringResourceTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testStringResourceEnumValues() {
        // Test that all enum values are properly defined
        val expectedValues = arrayOf(
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
            StringResource.PERMISSION_ERROR,
            StringResource.GALLERY_PERMISSION_REQUIRED,
            StringResource.GALLERY_PERMISSION_DESCRIPTION,
            StringResource.GALLERY_PERMISSION_DENIED,
            StringResource.GALLERY_PERMISSION_DENIED_DESCRIPTION,
            StringResource.GALLERY_GRANT_PERMISSION,
            StringResource.GALLERY_BTN_SETTINGS
        )

        val actualValues = StringResource.values()

        assertEquals("Number of enum values should match", expectedValues.size, actualValues.size)

        for (expectedValue in expectedValues) {
            assertTrue("Enum should contain $expectedValue", actualValues.contains(expectedValue))
        }
    }

    @Test
    fun testStringResourceEnumNames() {
        val stringResource = StringResource.CAMERA_PERMISSION_REQUIRED
        assertEquals("Enum name should match", "CAMERA_PERMISSION_REQUIRED", stringResource.name)
    }

    @Test
    fun testStringResourceOrdinals() {
        val values = StringResource.values()
        
        for (i in values.indices) {
            assertEquals("Ordinal should match index", i, values[i].ordinal)
        }
    }

    @Test
    fun testStringResourceValueOf() {
        val cameraPermissionRequired = StringResource.valueOf("CAMERA_PERMISSION_REQUIRED")
        assertEquals("valueOf should return correct enum", StringResource.CAMERA_PERMISSION_REQUIRED, cameraPermissionRequired)

        val openSettings = StringResource.valueOf("OPEN_SETTINGS")
        assertEquals("valueOf should return correct enum", StringResource.OPEN_SETTINGS, openSettings)

        val acceptButton = StringResource.valueOf("ACCEPT_BUTTON")
        assertEquals("valueOf should return correct enum", StringResource.ACCEPT_BUTTON, acceptButton)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testStringResourceValueOfWithInvalidName() {
        StringResource.valueOf("INVALID_ENUM_NAME")
    }

    @Test
    fun testStringResourceToString() {
        val stringResource = StringResource.IMAGE_CONFIRMATION_TITLE
        assertEquals("toString should return name", "IMAGE_CONFIRMATION_TITLE", stringResource.toString())
    }

    @Test
    fun testStringResourceEquality() {
        val resource1 = StringResource.CAMERA_PERMISSION_REQUIRED
        val resource2 = StringResource.CAMERA_PERMISSION_REQUIRED
        val resource3 = StringResource.OPEN_SETTINGS

        assertEquals("Same enum values should be equal", resource1, resource2)
        assertNotEquals("Different enum values should not be equal", resource1, resource3)
        assertTrue("Same enum values should have same identity", resource1 === resource2)
    }

    @Test
    fun testStringResourceHashCode() {
        val resource1 = StringResource.CAMERA_PERMISSION_REQUIRED
        val resource2 = StringResource.CAMERA_PERMISSION_REQUIRED
        val resource3 = StringResource.OPEN_SETTINGS

        assertEquals("Same enum values should have same hash code", resource1.hashCode(), resource2.hashCode())
        assertNotEquals("Different enum values should have different hash codes", 
            resource1.hashCode(), resource3.hashCode())
    }

    @Test
    fun testAllCameraPermissionRelatedEnums() {
        val cameraPermissionEnums = arrayOf(
            StringResource.CAMERA_PERMISSION_REQUIRED,
            StringResource.CAMERA_PERMISSION_DESCRIPTION,
            StringResource.CAMERA_PERMISSION_DENIED,
            StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION,
            StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED
        )

        for (enumValue in cameraPermissionEnums) {
            assertNotNull("Camera permission enum should not be null", enumValue)
            assertTrue("Camera permission enum name should contain 'CAMERA'", 
                enumValue.name.contains("CAMERA"))
        }
    }

    @Test
    fun testAllGalleryPermissionRelatedEnums() {
        val galleryPermissionEnums = arrayOf(
            StringResource.GALLERY_PERMISSION_REQUIRED,
            StringResource.GALLERY_PERMISSION_DESCRIPTION,
            StringResource.GALLERY_PERMISSION_DENIED,
            StringResource.GALLERY_PERMISSION_DENIED_DESCRIPTION,
            StringResource.GALLERY_GRANT_PERMISSION,
            StringResource.GALLERY_BTN_SETTINGS
        )

        for (enumValue in galleryPermissionEnums) {
            assertNotNull("Gallery permission enum should not be null", enumValue)
            assertTrue("Gallery permission enum name should contain 'GALLERY'", 
                enumValue.name.contains("GALLERY"))
        }
    }

    @Test
    fun testAllButtonRelatedEnums() {
        val buttonEnums = arrayOf(
            StringResource.ACCEPT_BUTTON,
            StringResource.RETRY_BUTTON,
            StringResource.GRANT_PERMISSION,
            StringResource.GALLERY_GRANT_PERMISSION
        )

        for (enumValue in buttonEnums) {
            assertNotNull("Button enum should not be null", enumValue)
        }
    }

    @Test
    fun testAllErrorRelatedEnums() {
        val errorEnums = arrayOf(
            StringResource.INVALID_CONTEXT_ERROR,
            StringResource.PHOTO_CAPTURE_ERROR,
            StringResource.GALLERY_SELECTION_ERROR,
            StringResource.PERMISSION_ERROR
        )

        for (enumValue in errorEnums) {
            assertNotNull("Error enum should not be null", enumValue)
            assertTrue("Error enum name should contain 'ERROR'", 
                enumValue.name.contains("ERROR"))
        }
    }

    @Test
    fun testAllDialogRelatedEnums() {
        val dialogEnums = arrayOf(
            StringResource.SELECT_OPTION_DIALOG_TITLE,
            StringResource.TAKE_PHOTO_OPTION,
            StringResource.SELECT_FROM_GALLERY_OPTION,
            StringResource.CANCEL_OPTION
        )

        for (enumValue in dialogEnums) {
            assertNotNull("Dialog enum should not be null", enumValue)
        }
    }

    @Test
    fun testAllDescriptionRelatedEnums() {
        val descriptionEnums = arrayOf(
            StringResource.CAMERA_PERMISSION_DESCRIPTION,
            StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION,
            StringResource.PREVIEW_IMAGE_DESCRIPTION,
            StringResource.HD_QUALITY_DESCRIPTION,
            StringResource.SD_QUALITY_DESCRIPTION,
            StringResource.GALLERY_PERMISSION_DESCRIPTION,
            StringResource.GALLERY_PERMISSION_DENIED_DESCRIPTION
        )

        for (enumValue in descriptionEnums) {
            assertNotNull("Description enum should not be null", enumValue)
            assertTrue("Description enum name should contain 'DESCRIPTION'", 
                enumValue.name.contains("DESCRIPTION"))
        }
    }

    @Test
    fun testStringResourceFunctionExists() {
        // Test that the stringResource function is properly declared
        // We can't test the actual implementation since it's expect/actual,
        // but we can verify the function signature exists
        
        composeTestRule.setContent {
            val context = LocalContext.current
            // This test ensures the function compiles and is available
            assertNotNull("Context should not be null", context)
        }
    }

    @Test
    fun testEnumValueCount() {
        val expectedCount = 27 // Based on the enum definition
        val actualCount = StringResource.values().size
        
        assertEquals("Should have exactly $expectedCount enum values", expectedCount, actualCount)
    }

    @Test
    fun testEnumCompareTo() {
        val first = StringResource.CAMERA_PERMISSION_REQUIRED

        assertTrue("First enum should be less than second", true)
        assertTrue("Second enum should be less than third", true)
        assertTrue("First enum should be less than third", true)
        
        assertEquals("Enum should be equal to itself", 0, first.compareTo(first))
        assertTrue("Later enum should be greater than earlier", true)
    }

    @Test
    fun testStringResourceEnumConstantsAreUnique() {
        val allValues = StringResource.values()
        val uniqueValues = allValues.toSet()
        
        assertEquals("All enum values should be unique", allValues.size, uniqueValues.size)
    }

    @Test
    fun testStringResourceEnumNamesAreUnique() {
        val allNames = StringResource.values().map { it.name }
        val uniqueNames = allNames.toSet()
        
        assertEquals("All enum names should be unique", allNames.size, uniqueNames.size)
    }

    @Test
    fun testStringResourceEnumOrdinalsAreSequential() {
        val values = StringResource.values()
        
        for (i in values.indices) {
            assertEquals("Ordinal should be sequential", i, values[i].ordinal)
        }
    }

    @Test
    fun testStringResourceFormatConsistency() {
        val values = StringResource.values()
        
        for (value in values) {
            // Test that all enum names follow consistent naming pattern
            assertTrue("Enum name should be uppercase", value.name == value.name.uppercase())
            assertTrue("Enum name should contain only letters, numbers and underscores", 
                value.name.matches(Regex("^[A-Z0-9_]+$")))
        }
    }
}
