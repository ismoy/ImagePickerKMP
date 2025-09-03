package io.github.ismoy.imagepickerkmp.presentation.resources

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class StringResourceUnitTest {

    @Test
    fun testStringResourceEnumValues() {
        val values = StringResource.values()
        assertTrue("Should have at least 25 values", values.size >= 25)
        
        // Test specific values exist
        assertTrue("Should contain CAMERA_PERMISSION_REQUIRED", 
            values.contains(StringResource.CAMERA_PERMISSION_REQUIRED))
        assertTrue("Should contain ACCEPT_BUTTON", 
            values.contains(StringResource.ACCEPT_BUTTON))
        assertTrue("Should contain GALLERY_PERMISSION_REQUIRED", 
            values.contains(StringResource.GALLERY_PERMISSION_REQUIRED))
    }

    @Test
    fun testStringResourceValueOf() {
        val cameraPermission = StringResource.valueOf("CAMERA_PERMISSION_REQUIRED")
        assertEquals("valueOf should work correctly", StringResource.CAMERA_PERMISSION_REQUIRED, cameraPermission)
        
        val acceptButton = StringResource.valueOf("ACCEPT_BUTTON")
        assertEquals("valueOf should work correctly", StringResource.ACCEPT_BUTTON, acceptButton)
    }

    @Test
    fun testStringResourceOrdinals() {
        val values = StringResource.values()
        for (i in values.indices) {
            assertEquals("Ordinal should match index", i, values[i].ordinal)
        }
    }

    @Test
    fun testStringResourceNames() {
        assertEquals("Name should match", "CAMERA_PERMISSION_REQUIRED", 
            StringResource.CAMERA_PERMISSION_REQUIRED.name)
        assertEquals("Name should match", "ACCEPT_BUTTON", 
            StringResource.ACCEPT_BUTTON.name)
        assertEquals("Name should match", "RETRY_BUTTON", 
            StringResource.RETRY_BUTTON.name)
    }

    @Test
    fun testStringResourceEquality() {
        val resource1 = StringResource.CAMERA_PERMISSION_REQUIRED
        val resource2 = StringResource.CAMERA_PERMISSION_REQUIRED
        val resource3 = StringResource.ACCEPT_BUTTON
        
        assertEquals("Same resources should be equal", resource1, resource2)
        assertNotEquals("Different resources should not be equal", resource1, resource3)
        assertTrue("Same resources should have same identity", resource1 === resource2)
    }

    @Test
    fun testStringResourceHashCode() {
        val resource1 = StringResource.CAMERA_PERMISSION_REQUIRED
        val resource2 = StringResource.CAMERA_PERMISSION_REQUIRED
        val resource3 = StringResource.ACCEPT_BUTTON
        
        assertEquals("Same resources should have same hash code", 
            resource1.hashCode(), resource2.hashCode())
        assertNotEquals("Different resources should have different hash codes", 
            resource1.hashCode(), resource3.hashCode())
    }

    @Test
    fun testStringResourceToString() {
        assertEquals("toString should return name", "CAMERA_PERMISSION_REQUIRED", 
            StringResource.CAMERA_PERMISSION_REQUIRED.toString())
        assertEquals("toString should return name", "ACCEPT_BUTTON", 
            StringResource.ACCEPT_BUTTON.toString())
    }

    @Test
    fun testAllCameraRelatedResources() {
        val cameraResources = StringResource.values().filter { it.name.contains("CAMERA") }
        assertTrue("Should have camera related resources", cameraResources.isNotEmpty())
        
        for (resource in cameraResources) {
            assertNotNull("Camera resource should not be null", resource)
            assertTrue("Camera resource name should contain CAMERA", 
                resource.name.contains("CAMERA"))
        }
    }

    @Test
    fun testAllGalleryRelatedResources() {
        val galleryResources = StringResource.values().filter { it.name.contains("GALLERY") }
        assertTrue("Should have gallery related resources", galleryResources.isNotEmpty())
        
        for (resource in galleryResources) {
            assertNotNull("Gallery resource should not be null", resource)
            assertTrue("Gallery resource name should contain GALLERY", 
                resource.name.contains("GALLERY"))
        }
    }

    @Test
    fun testAllButtonRelatedResources() {
        val buttonResources = StringResource.values().filter { it.name.contains("BUTTON") }
        assertTrue("Should have button related resources", buttonResources.isNotEmpty())
        
        for (resource in buttonResources) {
            assertNotNull("Button resource should not be null", resource)
            assertTrue("Button resource name should contain BUTTON", 
                resource.name.contains("BUTTON"))
        }
    }

    @Test
    fun testAllErrorRelatedResources() {
        val errorResources = StringResource.values().filter { it.name.contains("ERROR") }
        assertTrue("Should have error related resources", errorResources.isNotEmpty())
        
        for (resource in errorResources) {
            assertNotNull("Error resource should not be null", resource)
            assertTrue("Error resource name should contain ERROR", 
                resource.name.contains("ERROR"))
        }
    }

    @Test
    fun testResourceNamingConvention() {
        val values = StringResource.values()
        
        for (value in values) {
            assertTrue("Resource name should be uppercase", 
                value.name == value.name.uppercase())
            assertTrue("Resource name should follow convention", 
                value.name.matches(Regex("^[A-Z0-9_]+$")))
        }
    }

    @Test
    fun testResourceUniqueness() {
        val values = StringResource.values()
        val uniqueValues = values.toSet()
        assertEquals("All values should be unique", values.size, uniqueValues.size)
        
        val names = values.map { it.name }
        val uniqueNames = names.toSet()
        assertEquals("All names should be unique", names.size, uniqueNames.size)
    }

    @Test
    fun testEnumCompareTo() {
        val first = StringResource.CAMERA_PERMISSION_REQUIRED
        val second = StringResource.CAMERA_PERMISSION_DESCRIPTION
        
        assertTrue("First ordinal should be less than second", first.ordinal < second.ordinal)
        assertTrue("CompareTo should reflect ordinal difference", first < second)
        assertEquals("Resource should be equal to itself", 0, first.compareTo(first))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidValueOf() {
        StringResource.valueOf("INVALID_RESOURCE_NAME")
    }
}
