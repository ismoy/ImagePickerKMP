package io.github.ismoy.imagepickerkmp.domain.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PhotoResultTest {

    @Test
    fun testPhotoResult_allParameters() {
        val photoResult = PhotoResult(
            uri = "file://test/path/image.jpg",
            width = 1920,
            height = 1080,
            fileName = "image.jpg",
            fileSize = 2048L
        )

        assertEquals("file://test/path/image.jpg", photoResult.uri)
        assertEquals(1920, photoResult.width)
        assertEquals(1080, photoResult.height)
        assertEquals("image.jpg", photoResult.fileName)
        assertEquals(2048L, photoResult.fileSize)
    }

    @Test
    fun testPhotoResult_defaultParameters() {
        val photoResult = PhotoResult(
            uri = "file://test/path/image.jpg",
            width = 1920,
            height = 1080
        )

        assertEquals("file://test/path/image.jpg", photoResult.uri)
        assertEquals(1920, photoResult.width)
        assertEquals(1080, photoResult.height)
        assertNull(photoResult.fileName)
        assertNull(photoResult.fileSize)
    }

    @Test
    fun testPhotoResult_withFileName() {
        val photoResult = PhotoResult(
            uri = "file://test/path/image.jpg",
            width = 1920,
            height = 1080,
            fileName = "test_image.jpg"
        )

        assertEquals("file://test/path/image.jpg", photoResult.uri)
        assertEquals(1920, photoResult.width)
        assertEquals(1080, photoResult.height)
        assertEquals("test_image.jpg", photoResult.fileName)
        assertNull(photoResult.fileSize)
    }

    @Test
    fun testPhotoResult_withFileSize() {
        val photoResult = PhotoResult(
            uri = "file://test/path/image.jpg",
            width = 1920,
            height = 1080,
            fileSize = 4096L
        )

        assertEquals("file://test/path/image.jpg", photoResult.uri)
        assertEquals(1920, photoResult.width)
        assertEquals(1080, photoResult.height)
        assertNull(photoResult.fileName)
        assertEquals(4096L, photoResult.fileSize)
    }

    @Test
    fun testPhotoResult_squareImage() {
        val photoResult = PhotoResult(
            uri = "file://test/path/square.jpg",
            width = 1080,
            height = 1080,
            fileName = "square.jpg",
            fileSize = 1536L
        )

        assertEquals("file://test/path/square.jpg", photoResult.uri)
        assertEquals(1080, photoResult.width)
        assertEquals(1080, photoResult.height)
        assertEquals("square.jpg", photoResult.fileName)
        assertEquals(1536L, photoResult.fileSize)
    }

    @Test
    fun testPhotoResult_portraitImage() {
        val photoResult = PhotoResult(
            uri = "file://test/path/portrait.jpg",
            width = 1080,
            height = 1920,
            fileName = "portrait.jpg",
            fileSize = 2560L
        )

        assertEquals("file://test/path/portrait.jpg", photoResult.uri)
        assertEquals(1080, photoResult.width)
        assertEquals(1920, photoResult.height)
        assertEquals("portrait.jpg", photoResult.fileName)
        assertEquals(2560L, photoResult.fileSize)
    }

    @Test
    fun testPhotoResult_dataClassEquality() {
        val photoResult1 = PhotoResult(
            uri = "file://test/path/image.jpg",
            width = 1920,
            height = 1080,
            fileName = "image.jpg",
            fileSize = 2048L
        )

        val photoResult2 = PhotoResult(
            uri = "file://test/path/image.jpg",
            width = 1920,
            height = 1080,
            fileName = "image.jpg",
            fileSize = 2048L
        )

        assertEquals(photoResult1, photoResult2)
        assertEquals(photoResult1.hashCode(), photoResult2.hashCode())
    }

    @Test
    fun testPhotoResult_toString() {
        val photoResult = PhotoResult(
            uri = "file://test/path/image.jpg",
            width = 1920,
            height = 1080,
            fileName = "image.jpg",
            fileSize = 2048L
        )

        val string = photoResult.toString()
        assert(string.contains("file://test/path/image.jpg"))
        assert(string.contains("1920"))
        assert(string.contains("1080"))
        assert(string.contains("image.jpg"))
        assert(string.contains("2048"))
    }
}
