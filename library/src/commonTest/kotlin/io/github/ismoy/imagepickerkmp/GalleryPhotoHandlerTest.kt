package io.github.ismoy.imagepickerkmp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class GalleryPhotoHandlerTest {
    @Test
    fun testPhotoResultProperties() {
        val result = GalleryPhotoHandler.PhotoResult(
            uri = "file:///tmp/gallery.jpg",
            width = 640,
            height = 480,
            fileName = "gallery.jpg",
            fileSize = 98765L
        )
        assertEquals("file:///tmp/gallery.jpg", result.uri)
        assertEquals(640, result.width)
        assertEquals(480, result.height)
        assertEquals("gallery.jpg", result.fileName)
        assertEquals(98765L, result.fileSize)
    }

    @Test
    fun testPhotoResultEquality() {
        val result1 = GalleryPhotoHandler.PhotoResult(
            uri = "file:///tmp/gallery.jpg",
            width = 640,
            height = 480,
            fileName = "gallery.jpg",
            fileSize = 98765L
        )
        val result2 = GalleryPhotoHandler.PhotoResult(
            uri = "file:///tmp/gallery.jpg",
            width = 640,
            height = 480,
            fileName = "gallery.jpg",
            fileSize = 98765L
        )
        val result3 = GalleryPhotoHandler.PhotoResult(
            uri = "file:///tmp/other.jpg",
            width = 320,
            height = 240,
            fileName = "other.jpg",
            fileSize = 12345L
        )
        assertEquals(result1, result2)
        assertNotEquals(result1, result3)
    }
}
