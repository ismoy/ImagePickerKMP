package io.github.ismoy.imagepickerkmp.domain.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PhotoResultTest {

    // ───────────── Construction ─────────────

    @Test
    fun photoResult_minimalConstruction_uriIsSet() {
        val result = PhotoResult(uri = "content://media/images/1")
        assertEquals("content://media/images/1", result.uri)
    }

    @Test
    fun photoResult_allOptionalFieldsDefaultToNull() {
        val result = PhotoResult(uri = "file:///image.jpg")
        assertNull(result.width)
        assertNull(result.height)
        assertNull(result.fileName)
        assertNull(result.fileSize)
        assertNull(result.mimeType)
        assertNull(result.exif)
    }

    @Test
    fun photoResult_fullConstruction_allFieldsAccessible() {
        val exif = ExifData(cameraModel = "Pixel 8")
        val result = PhotoResult(
            uri = "content://media/1",
            width = 4032,
            height = 3024,
            fileName = "IMG_20240101.jpg",
            fileSize = 3_500_000L,
            mimeType = "image/jpeg",
            exif = exif
        )
        assertEquals("content://media/1", result.uri)
        assertEquals(4032, result.width)
        assertEquals(3024, result.height)
        assertEquals("IMG_20240101.jpg", result.fileName)
        assertEquals(3_500_000L, result.fileSize)
        assertEquals("image/jpeg", result.mimeType)
        assertNotNull(result.exif)
        assertEquals("Pixel 8", result.exif.cameraModel)
    }

    // ───────────── Equality ─────────────

    @Test
    fun twoIdenticalPhotoResults_areEqual() {
        val a = PhotoResult(uri = "file:///a.jpg", width = 100, height = 200)
        val b = PhotoResult(uri = "file:///a.jpg", width = 100, height = 200)
        assertEquals(a, b)
    }

    @Test
    fun photoResultsDifferentUri_areNotEqual() {
        val a = PhotoResult(uri = "file:///a.jpg")
        val b = PhotoResult(uri = "file:///b.jpg")
        assertTrue(a != b)
    }

    @Test
    fun photoResultsDifferentFileSize_areNotEqual() {
        val a = PhotoResult(uri = "file:///a.jpg", fileSize = 1000L)
        val b = PhotoResult(uri = "file:///a.jpg", fileSize = 2000L)
        assertTrue(a != b)
    }

    // ───────────── Copy ─────────────

    @Test
    fun copy_changesOnlySpecifiedField() {
        val original = PhotoResult(uri = "file:///a.jpg", width = 1920, height = 1080)
        val copy = original.copy(uri = "file:///b.jpg")
        assertEquals("file:///b.jpg", copy.uri)
        assertEquals(1920, copy.width)
        assertEquals(1080, copy.height)
    }

    @Test
    fun copy_doesNotMutateOriginal() {
        val original = PhotoResult(uri = "file:///a.jpg")
        original.copy(uri = "file:///b.jpg")
        assertEquals("file:///a.jpg", original.uri)
    }

    // ───────────── GalleryPhotoResult typealias ─────────────

    @Test
    fun galleryPhotoResult_isSameTypeAsPhotoResult() {
        val gallery: GalleryPhotoResult = PhotoResult(uri = "content://media/2")
        val photo: PhotoResult = gallery
        assertEquals(gallery, photo)
    }

    // ───────────── MIME type handling ─────────────

    @Test
    fun photoResult_pdfMimeType_isStoredCorrectly() {
        val result = PhotoResult(uri = "content://docs/1", mimeType = "application/pdf")
        assertEquals("application/pdf", result.mimeType)
        assertNull(result.width, "PDFs have no width")
        assertNull(result.height, "PDFs have no height")
    }

    // ───────────── hashCode consistency ─────────────

    @Test
    fun equalPhotoResults_haveSameHashCode() {
        val a = PhotoResult(uri = "file:///a.jpg", width = 100)
        val b = PhotoResult(uri = "file:///a.jpg", width = 100)
        assertEquals(a.hashCode(), b.hashCode())
    }
}
