package io.github.ismoy.imagepickerkmp

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GalleryPhotoHandlerTest {
    
    @Test
    fun testPhotoResultConstructorAndGetters() {
        // Given
        val uri = "content://media/external/images/123"
        val width = 1920
        val height = 1080
        val fileName = "test_image.jpg"
        val fileSize = 1024L
        
        // When
        val photoResult = GalleryPhotoHandler.PhotoResult(
            uri = uri,
            width = width,
            height = height,
            fileName = fileName,
            fileSize = fileSize
        )
        
        // Then
        assertEquals(uri, photoResult.uri)
        assertEquals(width, photoResult.width)
        assertEquals(height, photoResult.height)
        assertEquals(fileName, photoResult.fileName)
        assertEquals(fileSize, photoResult.fileSize)
    }
    
    @Test
    fun testPhotoResultWithDifferentValues() {
        // Given
        val uri = "content://media/external/images/456"
        val width = 3840
        val height = 2160
        val fileName = "high_res_image.png"
        val fileSize = 2048L
        
        // When
        val photoResult = GalleryPhotoHandler.PhotoResult(
            uri = uri,
            width = width,
            height = height,
            fileName = fileName,
            fileSize = fileSize
        )
        
        // Then
        assertEquals(uri, photoResult.uri)
        assertEquals(width, photoResult.width)
        assertEquals(height, photoResult.height)
        assertEquals(fileName, photoResult.fileName)
        assertEquals(fileSize, photoResult.fileSize)
    }
    
    @Test
    fun testPhotoResultWithZeroValues() {
        // Given
        val uri = "content://media/external/images/789"
        val width = 0
        val height = 0
        val fileName = "empty_image.jpg"
        val fileSize = 0L
        
        // When
        val photoResult = GalleryPhotoHandler.PhotoResult(
            uri = uri,
            width = width,
            height = height,
            fileName = fileName,
            fileSize = fileSize
        )
        
        // Then
        assertEquals(uri, photoResult.uri)
        assertEquals(width, photoResult.width)
        assertEquals(height, photoResult.height)
        assertEquals(fileName, photoResult.fileName)
        assertEquals(fileSize, photoResult.fileSize)
    }
    
    @Test
    fun testPhotoResultWithEmptyString() {
        // Given
        val uri = ""
        val width = 100
        val height = 100
        val fileName = ""
        val fileSize = 100L
        
        // When
        val photoResult = GalleryPhotoHandler.PhotoResult(
            uri = uri,
            width = width,
            height = height,
            fileName = fileName,
            fileSize = fileSize
        )
        
        // Then
        assertEquals(uri, photoResult.uri)
        assertEquals(width, photoResult.width)
        assertEquals(height, photoResult.height)
        assertEquals(fileName, photoResult.fileName)
        assertEquals(fileSize, photoResult.fileSize)
    }
    
    @Test
    fun testPhotoResultWithNullFileSize() {
        // Given
        val uri = "content://media/external/images/123"
        val width = 1920
        val height = 1080
        val fileName = "test_image.jpg"
        
        // When
        val photoResult = GalleryPhotoHandler.PhotoResult(
            uri = uri,
            width = width,
            height = height,
            fileName = fileName,
            fileSize = null
        )
        
        // Then
        assertEquals(uri, photoResult.uri)
        assertEquals(width, photoResult.width)
        assertEquals(height, photoResult.height)
        assertEquals(fileName, photoResult.fileName)
        assertEquals(null, photoResult.fileSize)
    }
    
    @Test
    fun testPhotoResultToStringMethod() {
        // Given
        val photoResult = GalleryPhotoHandler.PhotoResult(
            uri = "test_uri",
            width = 100,
            height = 200,
            fileName = "test.jpg",
            fileSize = 500L
        )
        
        // When
        val result = photoResult.toString()
        
        // Then
        assertNotNull(result)
        assert(result.contains("test_uri"))
        assert(result.contains("100"))
        assert(result.contains("200"))
        assert(result.contains("test.jpg"))
        assert(result.contains("500"))
    }
} 