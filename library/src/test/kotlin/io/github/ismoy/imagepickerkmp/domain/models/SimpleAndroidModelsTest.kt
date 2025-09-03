package io.github.ismoy.imagepickerkmp.domain.models

import org.junit.Test
import org.junit.Assert.*

class SimpleAndroidModelsTest {

    @Test
    fun photoResult_shouldHaveCorrectData() {
        val result = PhotoResult(
            uri = "test_uri",
            width = 100,
            height = 200
        )
        
        assertEquals("test_uri", result.uri)
        assertEquals(100, result.width)
        assertEquals(200, result.height)
    }

    @Test
    fun galleryPhotoResult_shouldHaveCorrectData() {
        val result = GalleryPhotoResult(
            uri = "gallery_uri",
            width = 300,
            height = 400
        )
        
        assertEquals("gallery_uri", result.uri)
        assertEquals(300, result.width)
        assertEquals(400, result.height)
    }

    @Test
    fun capturePhotoPreference_shouldHaveCorrectValues() {
        assertEquals("FAST", CapturePhotoPreference.FAST.name)
        assertEquals("BALANCED", CapturePhotoPreference.BALANCED.name)
        assertEquals("QUALITY", CapturePhotoPreference.QUALITY.name)
    }

    @Test
    fun mimeType_shouldWorkCorrectly() {
        assertEquals("image/*", MimeType.IMAGE_ALL.value)
        assertEquals("image/jpeg", MimeType.IMAGE_JPEG.value)
        assertEquals("image/png", MimeType.IMAGE_PNG.value)
    }

    @Test
    fun mimeType_toMimeTypeStrings_shouldConvertCorrectly() {
        val result = MimeType.toMimeTypeStrings(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG)
        assertEquals(2, result.size)
        assertEquals("image/jpeg", result[0])
        assertEquals("image/png", result[1])
    }

    @Test
    fun mimeType_fromString_shouldWorkCorrectly() {
        val result = MimeType.fromString("image/jpeg")
        assertEquals(MimeType.IMAGE_JPEG, result)
        
        val nullResult = MimeType.fromString("invalid/type")
        assertNull(nullResult)
    }

    @Test
    fun mimeType_fromString_shouldBeCaseInsensitive() {
        val result1 = MimeType.fromString("IMAGE/JPEG")
        assertEquals(MimeType.IMAGE_JPEG, result1)
        
        val result2 = MimeType.fromString("Image/Png")
        assertEquals(MimeType.IMAGE_PNG, result2)
    }

    @Test
    fun mimeType_commonImageTypes_shouldContainCorrectTypes() {
        val commonTypes = MimeType.COMMON_IMAGE_TYPES
        
        assertEquals(4, commonTypes.size)
        assertTrue(commonTypes.contains(MimeType.IMAGE_JPEG))
        assertTrue(commonTypes.contains(MimeType.IMAGE_PNG))
        assertTrue(commonTypes.contains(MimeType.IMAGE_GIF))
        assertTrue(commonTypes.contains(MimeType.IMAGE_WEBP))
    }

    @Test
    fun mimeType_allSupportedTypes_shouldContainAllEntries() {
        val allTypes = MimeType.ALL_SUPPORTED_TYPES
        
        assertEquals(8, allTypes.size)
        assertTrue(allTypes.contains(MimeType.IMAGE_ALL))
        assertTrue(allTypes.contains(MimeType.IMAGE_HEIC))
        assertTrue(allTypes.contains(MimeType.IMAGE_HEIF))
        assertTrue(allTypes.contains(MimeType.IMAGE_BMP))
    }

    @Test
    fun mimeType_toMimeTypeStrings_shouldHandleVarargs() {
        val result1 = MimeType.toMimeTypeStrings(MimeType.IMAGE_JPEG)
        assertEquals(1, result1.size)
        assertEquals("image/jpeg", result1[0])
        
        val result2 = MimeType.toMimeTypeStrings()
        assertEquals(0, result2.size)
        
        val result3 = MimeType.toMimeTypeStrings(
            MimeType.IMAGE_JPEG, 
            MimeType.IMAGE_PNG, 
            MimeType.IMAGE_GIF
        )
        assertEquals(3, result3.size)
    }

    @Test
    fun mimeType_allValues_shouldHaveCorrectStrings() {
        assertEquals("image/*", MimeType.IMAGE_ALL.value)
        assertEquals("image/jpeg", MimeType.IMAGE_JPEG.value)
        assertEquals("image/png", MimeType.IMAGE_PNG.value)
        assertEquals("image/gif", MimeType.IMAGE_GIF.value)
        assertEquals("image/webp", MimeType.IMAGE_WEBP.value)
        assertEquals("image/bmp", MimeType.IMAGE_BMP.value)
        assertEquals("image/heic", MimeType.IMAGE_HEIC.value)
        assertEquals("image/heif", MimeType.IMAGE_HEIF.value)
    }
}
