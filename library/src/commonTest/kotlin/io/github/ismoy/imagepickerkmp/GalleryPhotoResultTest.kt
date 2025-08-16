package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import kotlin.experimental.ExperimentalNativeApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

@OptIn(ExperimentalNativeApi::class)

class GalleryPhotoResultTest {
    
    @Test
    fun `GalleryPhotoResult should store all properties correctly`() {
        val result = GalleryPhotoResult(
            uri = "content://media/external/images/media/123",
            width = 1920,
            height = 1080,
            fileName = "IMG_001.jpg",
            fileSize = 2048576L
        )
        
        assertEquals("content://media/external/images/media/123", result.uri)
        assertEquals(1920, result.width)
        assertEquals(1080, result.height)
        assertEquals("IMG_001.jpg", result.fileName)
        assertEquals(2048576L, result.fileSize)
    }
    
    @Test
    fun `GalleryPhotoResult with null optional fields should work`() {
        val result = GalleryPhotoResult(
            uri = "content://media/external/images/media/456",
            width = 800,
            height = 600,
            fileName = null,
            fileSize = null
        )
        
        assertEquals("content://media/external/images/media/456", result.uri)
        assertEquals(800, result.width)
        assertEquals(600, result.height)
        assertNull(result.fileName)
        assertNull(result.fileSize)
    }
    
    @Test
    fun `GalleryPhotoResult equality should work correctly`() {
        val result1 = GalleryPhotoResult(
            uri = "content://test/123",
            width = 1920,
            height = 1080,
            fileName = "test.jpg",
            fileSize = 1024L
        )
        
        val result2 = GalleryPhotoResult(
            uri = "content://test/123",
            width = 1920,
            height = 1080,
            fileName = "test.jpg",
            fileSize = 1024L
        )
        
        val result3 = GalleryPhotoResult(
            uri = "content://test/456",
            width = 1920,
            height = 1080,
            fileName = "test.jpg",
            fileSize = 1024L
        )
        
        assertEquals(result1, result2)
        assertNotEquals(result1, result3)
        assertEquals(result1.hashCode(), result2.hashCode())
    }
    
    @Test
    fun `GalleryPhotoResult toString should contain all fields`() {
        val result = GalleryPhotoResult(
            uri = "content://test/123",
            width = 1920,
            height = 1080,
            fileName = "test.jpg",
            fileSize = 2048L
        )
        
        val toString = result.toString()
        
        // Should contain all field values
        assert(toString.contains("content://test/123"))
        assert(toString.contains("1920"))
        assert(toString.contains("1080"))
        assert(toString.contains("test.jpg"))
        assert(toString.contains("2048"))
    }
    
    @Test
    fun `GalleryPhotoResult copy should work correctly`() {
        val original = GalleryPhotoResult(
            uri = "content://original/123",
            width = 1920,
            height = 1080,
            fileName = "original.jpg",
            fileSize = 1024L
        )
        
        val copied = original.copy(
            uri = "content://copied/456",
            fileName = "copied.jpg"
        )
        
        assertEquals("content://copied/456", copied.uri)
        assertEquals(1920, copied.width) // unchanged
        assertEquals(1080, copied.height) // unchanged
        assertEquals("copied.jpg", copied.fileName)
        assertEquals(1024L, copied.fileSize) // unchanged
    }
    
    @Test
    fun `GalleryPhotoResult with different file sizes should work`() {
        val smallFile = GalleryPhotoResult(
            uri = "content://small",
            width = 100,
            height = 100,
            fileName = "small.jpg",
            fileSize = 1024L // 1KB
        )
        
        val largeFile = GalleryPhotoResult(
            uri = "content://large",
            width = 4000,
            height = 3000,
            fileName = "large.jpg",
            fileSize = 10485760L // 10MB
        )
        
        assertEquals(1024L, smallFile.fileSize)
        assertEquals(10485760L, largeFile.fileSize)
        assertNotEquals(smallFile.fileSize, largeFile.fileSize)
    }
    
    @Test
    fun `GalleryPhotoResult with various image dimensions should work`() {
        val portrait = GalleryPhotoResult(
            uri = "content://portrait",
            width = 1080,
            height = 1920,
            fileName = "portrait.jpg",
            fileSize = null
        )
        
        val landscape = GalleryPhotoResult(
            uri = "content://landscape",
            width = 1920,
            height = 1080,
            fileName = "landscape.jpg",
            fileSize = null
        )
        
        val square = GalleryPhotoResult(
            uri = "content://square",
            width = 1080,
            height = 1080,
            fileName = "square.jpg",
            fileSize = null
        )
        
        // Portrait
        assert(portrait.height > portrait.width)
        
        // Landscape
        assert(landscape.width > landscape.height)
        
        // Square
        assertEquals(square.width, square.height)
    }
}