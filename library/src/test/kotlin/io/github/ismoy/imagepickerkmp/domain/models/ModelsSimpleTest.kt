package io.github.ismoy.imagepickerkmp.domain.models

import junit.framework.TestCase

class ModelsSimpleTest : TestCase() {

    fun testCapturePhotoPreferenceValues() {
        val preferences = CapturePhotoPreference.values()
        
        assertNotNull("Preferences array should not be null", preferences)
        assertTrue("Should have at least one preference", preferences.isNotEmpty())
        
        // Test that all expected values exist
        val preferenceNames = preferences.map { it.name }.toSet()
        assertTrue("Should contain FAST", preferenceNames.contains("FAST"))
        assertTrue("Should contain BALANCED", preferenceNames.contains("BALANCED"))
        assertTrue("Should contain QUALITY", preferenceNames.contains("QUALITY"))
    }

    fun testCapturePhotoPreferenceValueOf() {
        // Test valueOf with valid names
        assertNotNull("FAST should be retrievable", CapturePhotoPreference.valueOf("FAST"))
        assertNotNull("BALANCED should be retrievable", CapturePhotoPreference.valueOf("BALANCED"))
        assertNotNull("QUALITY should be retrievable", CapturePhotoPreference.valueOf("QUALITY"))
        
        // Test that valueOf returns correct instances
        assertEquals("FAST valueOf should match", CapturePhotoPreference.FAST, 
                    CapturePhotoPreference.valueOf("FAST"))
        assertEquals("BALANCED valueOf should match", CapturePhotoPreference.BALANCED, 
                    CapturePhotoPreference.valueOf("BALANCED"))
        assertEquals("QUALITY valueOf should match", CapturePhotoPreference.QUALITY, 
                    CapturePhotoPreference.valueOf("QUALITY"))
    }

    fun testCapturePhotoPreferenceEquality() {
        // Test enum equality
        assertTrue("FAST should equal itself", CapturePhotoPreference.FAST == CapturePhotoPreference.FAST)
        assertTrue("BALANCED should equal itself", CapturePhotoPreference.BALANCED == CapturePhotoPreference.BALANCED)
        assertTrue("QUALITY should equal itself", CapturePhotoPreference.QUALITY == CapturePhotoPreference.QUALITY)
        
        // Test enum inequality
        assertFalse("FAST should not equal BALANCED", CapturePhotoPreference.FAST == CapturePhotoPreference.BALANCED)
        assertFalse("FAST should not equal QUALITY", CapturePhotoPreference.FAST == CapturePhotoPreference.QUALITY)
        assertFalse("BALANCED should not equal QUALITY", CapturePhotoPreference.BALANCED == CapturePhotoPreference.QUALITY)
    }

    fun testMimeTypeValues() {
        val mimeTypes = MimeType.values()
        
        assertNotNull("MimeType array should not be null", mimeTypes)
        assertTrue("Should have at least one mime type", mimeTypes.isNotEmpty())
        
        // Test that common mime types exist
        val typeNames = mimeTypes.map { it.name }.toSet()
        assertTrue("Should contain IMAGE_JPEG", typeNames.contains("IMAGE_JPEG"))
        assertTrue("Should contain IMAGE_PNG", typeNames.contains("IMAGE_PNG"))
    }

    fun testMimeTypeValues_StringValues() {
        // Test that mime types have proper string values
        assertTrue("IMAGE_JPEG should have image/jpeg value", 
                  MimeType.IMAGE_JPEG.value == "image/jpeg")
        assertTrue("IMAGE_PNG should have image/png value", 
                  MimeType.IMAGE_PNG.value == "image/png")
        assertTrue("IMAGE_WEBP should have image/webp value", 
                  MimeType.IMAGE_WEBP.value == "image/webp")
    }

    fun testPhotoResultBasicProperties() {
        val testFile = "test_file.jpg"
        val testMimeType = MimeType.IMAGE_JPEG
        val photoResult = PhotoResult(testFile, 800, 600)
        
        assertNotNull("PhotoResult should not be null", photoResult)
        assertEquals("URI should match", testFile, photoResult.uri)
        assertEquals("Width should match", 800, photoResult.width)
        assertEquals("Height should match", 600, photoResult.height)
    }

    fun testGalleryPhotoResultBasicProperties() {
        val testFile = "gallery_file.png"
        val testMimeType = MimeType.IMAGE_PNG
        val galleryResult = GalleryPhotoResult(testFile, 1024, 768)
        
        assertNotNull("GalleryPhotoResult should not be null", galleryResult)
        assertEquals("URI should match", testFile, galleryResult.uri)
        assertEquals("Width should match", 1024, galleryResult.width)
        assertEquals("Height should match", 768, galleryResult.height)
    }

    fun testDataClassEquality() {
        val file1 = "test1.jpg"
        val file2 = "test2.jpg"
        val mimeType = MimeType.IMAGE_JPEG
        
        val result1a = PhotoResult(file1, 800, 600)
        val result1b = PhotoResult(file1, 800, 600)
        val result2 = PhotoResult(file2, 800, 600)
        
        // Test equality for same data
        assertEquals("Same data should be equal", result1a, result1b)
        // Test inequality for different data
        assertFalse("Different data should not be equal", result1a == result2)
    }

    fun testDataClassHashCode() {
        val file = "test.jpg"
        val mimeType = MimeType.IMAGE_JPEG
        
        val result1 = PhotoResult(file, 800, 600)
        val result2 = PhotoResult(file, 800, 600)
        
        assertEquals("Same data should have same hash code", 
                    result1.hashCode(), result2.hashCode())
    }

    fun testMimeTypeEnumBehavior() {
        val allMimeTypes = MimeType.values()
        
        allMimeTypes.forEach { mimeType ->
            assertNotNull("MimeType should not be null", mimeType)
            assertNotNull("MimeType value should not be null", mimeType.value)
            assertTrue("MimeType value should not be empty", mimeType.value.isNotEmpty())
            assertTrue("MimeType value should contain /", mimeType.value.contains("/"))
            assertTrue("MimeType value should start with image/", mimeType.value.startsWith("image/"))
        }
    }

    fun testCapturePhotoPreferenceOrdinals() {
        val preferences = CapturePhotoPreference.values()
        
        // Test that ordinals are sequential starting from 0
        for (i in preferences.indices) {
            assertEquals("Ordinal should match index", i, preferences[i].ordinal)
        }
    }

    fun testEnumToString() {
        assertEquals("FAST toString should be FAST", "FAST", CapturePhotoPreference.FAST.toString())
        assertEquals("BALANCED toString should be BALANCED", "BALANCED", CapturePhotoPreference.BALANCED.toString())
        assertEquals("QUALITY toString should be QUALITY", "QUALITY", CapturePhotoPreference.QUALITY.toString())
        
        assertEquals("IMAGE_JPEG toString should be IMAGE_JPEG", "IMAGE_JPEG", MimeType.IMAGE_JPEG.toString())
        assertEquals("IMAGE_PNG toString should be IMAGE_PNG", "IMAGE_PNG", MimeType.IMAGE_PNG.toString())
    }
}
