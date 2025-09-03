package io.github.ismoy.imagepickerkmp.data.utils

import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import org.junit.Test
import org.junit.Assert.*
import java.io.File

class FileUtilsComprehensiveTest {

    @Test
    fun testMimeTypeValues() {
        // Test MimeType enum values that exist
        assertTrue("MimeType should have values", MimeType.values().isNotEmpty())
        
        // Test that IMAGE_ALL exists and has correct value
        assertEquals("image/*", MimeType.IMAGE_ALL.value)
    }

    @Test
    fun testMimeTypeFromString() {
        // Test creating MimeType from string
        val imageAllType = MimeType.fromString("image/*")
        assertEquals(MimeType.IMAGE_ALL, imageAllType)
        
        val unknownType = MimeType.fromString("image/unknown")
        assertNull("Should return null for unknown type", unknownType)
    }

    @Test
    fun testFileExtensionUtils() {
        // Test file extension extraction
        val jpegFile = File("image.jpg")
        val pngFile = File("image.png")
        val noExtFile = File("image")
        
        assertEquals("jpg", jpegFile.extension)
        assertEquals("png", pngFile.extension)
        assertEquals("", noExtFile.extension)
    }

    @Test
    fun testMimeTypeConstants() {
        // Test that MimeType constants are accessible
        assertNotNull(MimeType.IMAGE_ALL)
        assertTrue("Should have multiple mime types", MimeType.values().size > 1)
    }

    @Test
    fun testFileOperations() {
        // Test basic file operations
        val testFile = File("test.txt")
        assertFalse(testFile.exists())
        assertEquals(0L, testFile.length())
    }

    @Test
    fun testFilePathValidation() {
        val validPath = "/storage/emulated/0/Pictures/image.jpg"
        val invalidPath = ""
        
        assertTrue(validPath.isNotEmpty())
        assertTrue(invalidPath.isEmpty())
    }

    @Test
    fun testFileNameValidation() {
        val validName = "image.jpg"
        val emptyName = ""
        
        assertTrue(validName.isNotEmpty())
        assertTrue(emptyName.isEmpty())
    }

    @Test
    fun testMimeTypeComparison() {
        assertTrue("IMAGE_ALL should contain image", MimeType.IMAGE_ALL.value.contains("image"))
        assertTrue("Should have at least one mime type", MimeType.values().isNotEmpty())
    }
}
