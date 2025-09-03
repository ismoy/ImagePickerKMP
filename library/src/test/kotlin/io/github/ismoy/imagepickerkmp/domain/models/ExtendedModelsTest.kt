package io.github.ismoy.imagepickerkmp.domain.models

import org.junit.Test
import org.junit.Assert.*

class ExtendedModelsTest {

    @Test
    fun photoResult_copy_shouldWork() {
        val original = PhotoResult("uri1", 100, 200)
        val copy = original.copy(uri = "uri2")
        
        assertEquals("uri2", copy.uri)
        assertEquals(100, copy.width)
        assertEquals(200, copy.height)
    }

    @Test
    fun photoResult_equals_shouldWork() {
        val result1 = PhotoResult("uri", 100, 200)
        val result2 = PhotoResult("uri", 100, 200)
        val result3 = PhotoResult("different", 100, 200)
        
        assertEquals(result1, result2)
        assertNotEquals(result1, result3)
    }

    @Test
    fun photoResult_hashCode_shouldWork() {
        val result1 = PhotoResult("uri", 100, 200)
        val result2 = PhotoResult("uri", 100, 200)
        
        assertEquals(result1.hashCode(), result2.hashCode())
    }

    @Test
    fun photoResult_toString_shouldWork() {
        val result = PhotoResult("test_uri", 800, 600)
        val string = result.toString()
        
        assertTrue(string.contains("test_uri"))
        assertTrue(string.contains("800"))
        assertTrue(string.contains("600"))
    }

    @Test
    fun galleryPhotoResult_copy_shouldWork() {
        val original = GalleryPhotoResult("uri1", 100, 200, "file1.jpg", 1024L)
        val copy = original.copy(fileName = "file2.jpg")
        
        assertEquals("uri1", copy.uri)
        assertEquals("file2.jpg", copy.fileName)
        assertEquals(1024L, copy.fileSize)
    }

    @Test
    fun galleryPhotoResult_equals_shouldWork() {
        val result1 = GalleryPhotoResult("uri", 100, 200, "file.jpg", 1024L)
        val result2 = GalleryPhotoResult("uri", 100, 200, "file.jpg", 1024L)
        val result3 = GalleryPhotoResult("uri", 100, 200, "different.jpg", 1024L)
        
        assertEquals(result1, result2)
        assertNotEquals(result1, result3)
    }

    @Test
    fun galleryPhotoResult_toString_shouldWork() {
        val result = GalleryPhotoResult("test_uri", 800, 600, "photo.jpg", 2048L)
        val string = result.toString()
        
        assertTrue(string.contains("test_uri"))
        assertTrue(string.contains("photo.jpg"))
        assertTrue(string.contains("2048"))
    }

    @Test
    fun capturePhotoPreference_valueOf_shouldWork() {
        val fast = CapturePhotoPreference.valueOf("FAST")
        val balanced = CapturePhotoPreference.valueOf("BALANCED")
        val quality = CapturePhotoPreference.valueOf("QUALITY")
        
        assertEquals(CapturePhotoPreference.FAST, fast)
        assertEquals(CapturePhotoPreference.BALANCED, balanced)
        assertEquals(CapturePhotoPreference.QUALITY, quality)
    }

    @Test
    fun capturePhotoPreference_values_shouldWork() {
        val values = CapturePhotoPreference.values()
        
        assertEquals(3, values.size)
        assertTrue(values.contains(CapturePhotoPreference.FAST))
        assertTrue(values.contains(CapturePhotoPreference.BALANCED))
        assertTrue(values.contains(CapturePhotoPreference.QUALITY))
    }

    @Test
    fun capturePhotoPreference_ordinal_shouldWork() {
        assertEquals(0, CapturePhotoPreference.FAST.ordinal)
        assertEquals(1, CapturePhotoPreference.BALANCED.ordinal)
        assertEquals(2, CapturePhotoPreference.QUALITY.ordinal)
    }
}
