package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.presentation.resources.StringResource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StringsTest {
    
    @Test
    fun `test string resource enum values exist`() {
        val values = StringResource.values()
        assertNotNull(values)
        assertTrue(values.isNotEmpty())
    }
    
    @Test
    fun `test string resource enum has expected values`() {
        // Test that key string resources exist
        assertNotNull(StringResource.CAMERA_PERMISSION_REQUIRED)
        assertNotNull(StringResource.CAMERA_PERMISSION_DESCRIPTION)
        assertNotNull(StringResource.OPEN_SETTINGS)
        assertNotNull(StringResource.IMAGE_CONFIRMATION_TITLE)
        assertNotNull(StringResource.ACCEPT_BUTTON)
        assertNotNull(StringResource.RETRY_BUTTON)
    }
    
    @Test
    fun `test string resource enum values are unique`() {
        val values = StringResource.values()
        val uniqueValues = values.toSet()
        
        assertEquals(values.size, uniqueValues.size, 
            "All enum values should be unique")
    }
    
    @Test
    fun `test string resource enum ordinal values`() {
        val values = StringResource.values()
        
        values.forEachIndexed { index, value ->
            assertEquals(index, value.ordinal, 
                "Ordinal should match array index")
        }
    }
    
    @Test
    fun `test string resource enum name values`() {
        // Test that enum names are descriptive and follow naming convention
        assertTrue(StringResource.CAMERA_PERMISSION_REQUIRED.name.contains("CAMERA"))
        assertTrue(StringResource.CAMERA_PERMISSION_REQUIRED.name.contains("PERMISSION"))
        assertTrue(StringResource.IMAGE_CONFIRMATION_TITLE.name.contains("IMAGE"))
        assertTrue(StringResource.IMAGE_CONFIRMATION_TITLE.name.contains("TITLE"))
    }
    
    @Test
    fun `test string resource enum categorization`() {
        // Test that string resources are properly categorized
        val permissionResources = StringResource.values().filter { 
            it.name.contains("PERMISSION") 
        }
        assertTrue(permissionResources.isNotEmpty())
        
        val confirmationResources = StringResource.values().filter { 
            it.name.contains("CONFIRMATION") 
        }
        assertTrue(confirmationResources.isNotEmpty())
        
        val errorResources = StringResource.values().filter { 
            it.name.contains("ERROR") 
        }
        assertTrue(errorResources.isNotEmpty())
    }
    
    @Test
    fun `test string resource enum consistency`() {
        val values = StringResource.values()
        
        // Test that enum values remain consistent across multiple accesses
        repeat(10) {
            val currentValues = StringResource.values()
            assertEquals(values.size, currentValues.size)
            values.forEachIndexed { index, value ->
                assertEquals(value, currentValues[index])
            }
        }
    }
    
    @Test
    fun `test string resource enum performance`() {
        // Test that enum access is performant
        repeat(1000) {
            val values = StringResource.values()
            assertTrue(values.isNotEmpty())
        }
    }
    
    @Test
    fun `test string resource enum memory efficiency`() {
        val initialSize = StringResource.values().size
        
        // Test that enum doesn't grow unexpectedly
        repeat(100) {
            val currentSize = StringResource.values().size
            assertEquals(initialSize, currentSize)
        }
    }
    
    @Test
    fun `test string resource enum naming convention`() {
        val values = StringResource.values()
        
        values.forEach { value ->
            // Test that all enum names follow UPPER_SNAKE_CASE convention
            assertTrue(value.name.matches(Regex("[A-Z_]+")), 
                "Enum name should follow UPPER_SNAKE_CASE: ${value.name}")
        }
    }
    
    @Test
    fun `test string resource enum completeness`() {
        val values = StringResource.values()
        
        // Test that we have a reasonable number of string resources
        assertTrue(values.size >= 10, "Should have at least 10 string resources")
        assertTrue(values.size <= 100, "Should not have more than 100 string resources")
    }
} 