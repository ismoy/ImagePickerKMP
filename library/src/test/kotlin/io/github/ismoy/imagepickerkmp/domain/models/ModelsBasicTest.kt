package io.github.ismoy.imagepickerkmp.domain.models

import junit.framework.TestCase

class ModelsBasicTest : TestCase() {

    fun testCapturePhotoPreferenceEnumValues() {
        val preferences = CapturePhotoPreference.values()
        
        assertNotNull("Preferences array should not be null", preferences)
        assertTrue("Should have at least one preference", preferences.isNotEmpty())
        
        val preferenceNames = preferences.map { it.name }.toSet()
        assertTrue("Should contain FAST", preferenceNames.contains("FAST"))
        assertTrue("Should contain BALANCED", preferenceNames.contains("BALANCED"))
        assertTrue("Should contain QUALITY", preferenceNames.contains("QUALITY"))
    }

    fun testCapturePhotoPreferenceValueOf() {
        assertEquals("FAST valueOf should work", CapturePhotoPreference.FAST, 
                    CapturePhotoPreference.valueOf("FAST"))
        assertEquals("BALANCED valueOf should work", CapturePhotoPreference.BALANCED, 
                    CapturePhotoPreference.valueOf("BALANCED"))
        assertEquals("QUALITY valueOf should work", CapturePhotoPreference.QUALITY, 
                    CapturePhotoPreference.valueOf("QUALITY"))
    }

    fun testMimeTypeEnumValues() {
        val mimeTypes = MimeType.values()
        
        assertNotNull("MimeType array should not be null", mimeTypes)
        assertTrue("Should have at least one mime type", mimeTypes.isNotEmpty())
        
        val typeNames = mimeTypes.map { it.name }.toSet()
        assertTrue("Should contain IMAGE_JPEG", typeNames.contains("IMAGE_JPEG"))
        assertTrue("Should contain IMAGE_PNG", typeNames.contains("IMAGE_PNG"))
    }

    fun testMimeTypeStringValues() {
        assertEquals("IMAGE_JPEG should have correct value", "image/jpeg", MimeType.IMAGE_JPEG.value)
        assertEquals("IMAGE_PNG should have correct value", "image/png", MimeType.IMAGE_PNG.value)
        assertEquals("IMAGE_WEBP should have correct value", "image/webp", MimeType.IMAGE_WEBP.value)
    }

    fun testPhotoResultCreation() {
        val uri = "content://media/external/images/123"
        val width = 1920
        val height = 1080
        
        val photoResult = PhotoResult(uri, width, height)
        
        assertNotNull("PhotoResult should not be null", photoResult)
        assertEquals("URI should match", uri, photoResult.uri)
        assertEquals("Width should match", width, photoResult.width)
        assertEquals("Height should match", height, photoResult.height)
    }

    fun testGalleryPhotoResultCreation() {
        val uri = "content://media/external/images/456"
        val width = 1280
        val height = 720
        val fileName = "image.jpg"
        val fileSize = 1024L
        
        val galleryResult = GalleryPhotoResult(uri, width, height, fileName, fileSize)
        
        assertNotNull("GalleryPhotoResult should not be null", galleryResult)
        assertEquals("URI should match", uri, galleryResult.uri)
        assertEquals("Width should match", width, galleryResult.width)
        assertEquals("Height should match", height, galleryResult.height)
        assertEquals("FileName should match", fileName, galleryResult.fileName)
        assertEquals("FileSize should match", fileSize, galleryResult.fileSize)
    }

    fun testGalleryPhotoResultWithDefaults() {
        val uri = "content://media/external/images/789"
        val width = 800
        val height = 600
        
        val galleryResult = GalleryPhotoResult(uri, width, height)
        
        assertNotNull("GalleryPhotoResult should not be null", galleryResult)
        assertEquals("URI should match", uri, galleryResult.uri)
        assertEquals("Width should match", width, galleryResult.width)
        assertEquals("Height should match", height, galleryResult.height)
        assertNull("FileName should be null", galleryResult.fileName)
        assertNull("FileSize should be null", galleryResult.fileSize)
    }

    fun testDataClassEquality() {
        val uri = "test_uri"
        val width = 100
        val height = 100
        
        val photo1 = PhotoResult(uri, width, height)
        val photo2 = PhotoResult(uri, width, height)
        val photo3 = PhotoResult("different_uri", width, height)
        
        assertEquals("Same data should be equal", photo1, photo2)
        assertFalse("Different data should not be equal", photo1 == photo3)
    }

    fun testDataClassHashCode() {
        val uri = "test_uri"
        val width = 100
        val height = 100
        
        val photo1 = PhotoResult(uri, width, height)
        val photo2 = PhotoResult(uri, width, height)
        
        assertEquals("Same data should have same hash code", 
                    photo1.hashCode(), photo2.hashCode())
    }

    fun testEnumToString() {
        assertEquals("FAST toString", "FAST", CapturePhotoPreference.FAST.toString())
        assertEquals("BALANCED toString", "BALANCED", CapturePhotoPreference.BALANCED.toString())
        assertEquals("QUALITY toString", "QUALITY", CapturePhotoPreference.QUALITY.toString())
        
        assertEquals("IMAGE_JPEG toString", "IMAGE_JPEG", MimeType.IMAGE_JPEG.toString())
        assertEquals("IMAGE_PNG toString", "IMAGE_PNG", MimeType.IMAGE_PNG.toString())
    }

    fun testEnumOrdinals() {
        val preferences = CapturePhotoPreference.values()
        val mimeTypes = MimeType.values()
        
        for (i in preferences.indices) {
            assertEquals("Preference ordinal should match index", i, preferences[i].ordinal)
        }
        
        for (i in mimeTypes.indices) {
            assertEquals("MimeType ordinal should match index", i, mimeTypes[i].ordinal)
        }
    }

    fun testMimeTypeAllValue() {
        val mimeTypes = MimeType.values()
        val hasImageAll = mimeTypes.any { it.name == "IMAGE_ALL" }
        
        if (hasImageAll) {
            val imageAll = MimeType.valueOf("IMAGE_ALL")
            assertNotNull("IMAGE_ALL should not be null", imageAll)
            assertEquals("IMAGE_ALL should have correct value", "image/*", imageAll.value)
        }
    }

    fun testResultValidation() {
        val uris = listOf(
            "content://media/external/images/123",
            "file:///storage/emulated/0/DCIM/Camera/IMG_001.jpg",
            "/storage/emulated/0/Pictures/photo.png"
        )
        
        uris.forEach { uri ->
            val result = PhotoResult(uri, 100, 100)
            assertNotNull("Result should not be null for URI: $uri", result)
            assertEquals("URI should match", uri, result.uri)
            assertTrue("Width should be positive", result.width > 0)
            assertTrue("Height should be positive", result.height > 0)
        }
    }
}
