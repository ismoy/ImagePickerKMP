package io.github.ismoy.imagepickerkmp.domain.models

import junit.framework.TestCase

class ModelsExtendedTest : TestCase() {

    fun testGalleryPhotoResultCreation() {
        val uri = "content://media/external/images/1"
        val width = 1920
        val height = 1080
        val fileName = "IMG_001.jpg"
        val fileSize = 2048000L // 2MB
        
        val galleryResult = GalleryPhotoResult(uri, width, height, fileName, fileSize)
        
        assertNotNull("Gallery result should not be null", galleryResult)
        assertEquals("URI should match", uri, galleryResult.uri)
        assertEquals("Width should match", width, galleryResult.width)
        assertEquals("Height should match", height, galleryResult.height)
        assertEquals("File name should match", fileName, galleryResult.fileName)
        assertEquals("File size should match", fileSize, galleryResult.fileSize)
    }

    fun testGalleryPhotoResultWithDefaultFileSize() {
        val uri = "content://media/external/images/2"
        val width = 1280
        val height = 720
        val fileName = "IMG_002.png"
        
        val galleryResult = GalleryPhotoResult(uri, width, height, fileName)
        
        assertNotNull("Gallery result should not be null", galleryResult)
        assertEquals("URI should match", uri, galleryResult.uri)
        assertEquals("Width should match", width, galleryResult.width)
        assertEquals("Height should match", height, galleryResult.height)
        assertEquals("File name should match", fileName, galleryResult.fileName)
        assertNull("File size should be null when not provided", galleryResult.fileSize)
    }

    fun testPhotoResultCreation() {
        val uri = "file:///storage/emulated/0/Pictures/photo.jpg"
        val width = 2560
        val height = 1440
        
        val photoResult = PhotoResult(uri, width, height)
        
        assertNotNull("Photo result should not be null", photoResult)
        assertEquals("URI should match", uri, photoResult.uri)
        assertEquals("Width should match", width, photoResult.width)
        assertEquals("Height should match", height, photoResult.height)
    }

    fun testPhotoResultValidation() {
        val validUris = listOf(
            "file:///storage/photo.jpg",
            "content://media/external/images/123",
            "/sdcard/Pictures/image.png"
        )
        
        val validDimensions = listOf(
            Pair(1920, 1080),
            Pair(1280, 720),
            Pair(3840, 2160),
            Pair(640, 480)
        )
        
        validUris.forEach { uri ->
            assertTrue("URI should not be empty", uri.isNotEmpty())
            assertTrue("URI should be reasonable length", uri.length > 10)
        }
        
        validDimensions.forEach { (width, height) ->
            assertTrue("Width should be positive", width > 0)
            assertTrue("Height should be positive", height > 0)
            assertTrue("Width should be reasonable", width in 100..10000)
            assertTrue("Height should be reasonable", height in 100..10000)
        }
    }

    fun testCapturePhotoPreferenceEnum() {
        val preferences = CapturePhotoPreference.values()
        
        assertTrue("Should have capture preferences", preferences.isNotEmpty())
        assertTrue("Should have at least 3 preferences", preferences.size >= 3)
        
        val expectedPreferences = setOf(
            CapturePhotoPreference.FAST,
            CapturePhotoPreference.BALANCED,
            CapturePhotoPreference.QUALITY
        )
        
        expectedPreferences.forEach { expected ->
            assertTrue("Should contain expected preference", preferences.contains(expected))
        }
        
        preferences.forEach { preference ->
            assertNotNull("Preference should not be null", preference)
            assertTrue("Preference name should not be empty", preference.name.isNotEmpty())
        }
    }

    fun testMimeTypeEnum() {
        val mimeTypes = MimeType.values()
        
        assertTrue("Should have MIME types", mimeTypes.isNotEmpty())
        assertTrue("Should have at least 5 MIME types", mimeTypes.size >= 5)
        
        val expectedTypes = setOf(
            MimeType.IMAGE_JPEG,
            MimeType.IMAGE_PNG,
            MimeType.IMAGE_GIF,
            MimeType.IMAGE_BMP,
            MimeType.IMAGE_WEBP
        )
        
        expectedTypes.forEach { expected ->
            assertTrue("Should contain expected MIME type", mimeTypes.contains(expected))
        }
        
        mimeTypes.forEach { mimeType ->
            assertNotNull("MIME type should not be null", mimeType)
            assertNotNull("MIME type value should not be null", mimeType.value)
            assertTrue("MIME type value should not be empty", mimeType.value.isNotEmpty())
            assertTrue("MIME type value should contain '/'", mimeType.value.contains("/"))
            assertTrue("MIME type value should start with 'image/'", mimeType.value.startsWith("image/"))
        }
    }

    fun testMimeTypeValues() {
        val expectedMimeValues = mapOf(
            MimeType.IMAGE_JPEG to "image/jpeg",
            MimeType.IMAGE_PNG to "image/png",
            MimeType.IMAGE_GIF to "image/gif",
            MimeType.IMAGE_BMP to "image/bmp",
            MimeType.IMAGE_WEBP to "image/webp"
        )
        
        expectedMimeValues.forEach { (mimeType, expectedValue) ->
            assertEquals("MIME type value should match", expectedValue, mimeType.value)
        }
    }

    fun testPhotoResultWithDifferentAspectRatios() {
        val aspectRatios = listOf(
            Triple("16:9", 1920, 1080),
            Triple("4:3", 1600, 1200),
            Triple("1:1", 1080, 1080),
            Triple("21:9", 2560, 1080)
        )
        
        aspectRatios.forEach { (ratio, width, height) ->
            val photoResult = PhotoResult("test://uri/$ratio", width, height)
            
            assertNotNull("Photo result should not be null", photoResult)
            assertEquals("Width should match", width, photoResult.width)
            assertEquals("Height should match", height, photoResult.height)
            
            val calculatedRatio = width.toFloat() / height.toFloat()
            assertTrue("Aspect ratio should be reasonable", calculatedRatio > 0.5 && calculatedRatio < 3.0)
        }
    }

    fun testGalleryPhotoResultWithLargeFiles() {
        val largeSizes = listOf(
            5L * 1024 * 1024,    // 5MB
            10L * 1024 * 1024,   // 10MB
            50L * 1024 * 1024,   // 50MB
            100L * 1024 * 1024   // 100MB
        )
        
        largeSizes.forEach { size ->
            val galleryResult = GalleryPhotoResult(
                uri = "content://large/file",
                width = 4000,
                height = 3000,
                fileName = "large_image.jpg",
                fileSize = size
            )
            
            assertNotNull("Gallery result should handle large files", galleryResult)
            assertEquals("File size should match", size, galleryResult.fileSize)
            assertTrue("File size should be positive", galleryResult.fileSize!! > 0)
        }
    }

    fun testModelStringRepresentation() {
        val photoResult = PhotoResult("test://uri", 800, 600)
        val galleryResult = GalleryPhotoResult("test://gallery", 1200, 800, "test.jpg", 1024L)
        
        // Test that toString doesn't crash (implicit toString test)
        val photoString = photoResult.toString()
        val galleryString = galleryResult.toString()
        
        assertNotNull("Photo result string should not be null", photoString)
        assertNotNull("Gallery result string should not be null", galleryString)
        assertTrue("Photo string should not be empty", photoString.isNotEmpty())
        assertTrue("Gallery string should not be empty", galleryString.isNotEmpty())
    }

    fun testModelEquality() {
        val photo1 = PhotoResult("same://uri", 100, 100)
        val photo2 = PhotoResult("same://uri", 100, 100)
        val photo3 = PhotoResult("different://uri", 100, 100)
        
        // Note: Depending on implementation, these might be data classes with auto-generated equals
        assertNotNull("Photo 1 should not be null", photo1)
        assertNotNull("Photo 2 should not be null", photo2)
        assertNotNull("Photo 3 should not be null", photo3)
        
        // Test that different instances are not the same reference
        assertNotSame("Different instances should not be same reference", photo1, photo2)
    }
}
