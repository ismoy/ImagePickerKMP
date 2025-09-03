package io.github.ismoy.imagepickerkmp.domain.models

import junit.framework.TestCase

class MimeTypeComprehensiveTest : TestCase() {

    fun testMimeTypeValues() {
        // Test that MimeType enum exists and has values
        val values = MimeType.values()
        assertTrue("MimeType should have values", values.isNotEmpty())
        
        // Test IMAGE_ALL specifically since we know it exists
        assertEquals("image/*", MimeType.IMAGE_ALL.value)
    }

    fun testMimeTypeSpecificValues() {
        // Test accessing specific MimeType values
        val allMimeTypes = MimeType.values()
        assertTrue("Should have at least one mime type", allMimeTypes.isNotEmpty())
        
        // Test that we can access specific mime types
        val jpegType = MimeType.IMAGE_JPEG
        assertEquals("image/jpeg", jpegType.value)
        
        val pngType = MimeType.IMAGE_PNG
        assertEquals("image/png", pngType.value)
    }

    fun testMimeTypeConstants() {
        // Test that MimeType constants are accessible
        assertNotNull(MimeType.IMAGE_ALL)
        assertTrue("Should have multiple mime types", MimeType.values().size >= 1)
    }

    fun testMimeTypeComparison() {
        assertTrue("IMAGE_ALL should contain image", MimeType.IMAGE_ALL.value.contains("image"))
        assertNotNull("MimeType should have toString", MimeType.IMAGE_ALL.toString())
    }

    fun testMimeTypeEnumBehavior() {
        // Test basic enum behavior
        val allValues = MimeType.values()
        assertTrue("Should have at least IMAGE_ALL", allValues.contains(MimeType.IMAGE_ALL))
        
        val byName = MimeType.valueOf("IMAGE_ALL")
        assertEquals(MimeType.IMAGE_ALL, byName)
    }
}
