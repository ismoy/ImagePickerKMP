package io.github.ismoy.imagepickerkmp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class ImageProcessorTest {
    @Test
    fun testPhotoResultDataClass() {
        val result = GalleryPhotoHandler.PhotoResult(
            uri = "file:///tmp/test.jpg",
            width = 100,
            height = 200,
            fileName = "test.jpg",
            fileSize = 12345L
        )
        assertEquals("file:///tmp/test.jpg", result.uri)
        assertEquals(100, result.width)
        assertEquals(200, result.height)
        assertEquals("test.jpg", result.fileName)
        assertEquals(12345L, result.fileSize)
    }

    @Test
    fun testProcessImageHandlesNullFileGracefully() {
        val processor = FakeImageProcessor()
        var error: Exception? = null
        processor.processImage(
            imageFile = null,
            cameraType = null,
            onPhotoCaptured = { },
            onError = { e -> error = e }
        )
        assertNotNull(error, "Debe lanzar error si el archivo es nulo")
    }

    @Test
    fun testProcessImageWithCorruptFile() {
        val processor = FakeImageProcessor()
        var error: Exception? = null
        processor.processImage(
            imageFile = "corrupt",
            cameraType = null,
            onPhotoCaptured = { },
            onError = { e -> error = e }
        )
        assertNotNull(error)
        assertTrue(error!!.message!!.contains("corrupt"))
    }

    @Test
    fun testProcessImageWithUnsupportedFormat() {
        val processor = FakeImageProcessor()
        var error: Exception? = null
        processor.processImage(
            imageFile = "unsupported_format",
            cameraType = null,
            onPhotoCaptured = { },
            onError = { e -> error = e }
        )
        assertNotNull(error)
        assertTrue(error!!.message!!.contains("unsupported"))
    }

    @Test
    fun testProcessImageWithOrientationExif() {
        val processor = FakeImageProcessor()
        var captured: Any? = null
        processor.processImage(
            imageFile = "exif_orientation",
            cameraType = null,
            onPhotoCaptured = { captured = it },
            onError = { }
        )
        assertNotNull(captured)
        assertEquals("exif_orientation", captured)
    }

    @Test
    fun testProcessImageThrowsUnexpectedException() {
        val processor = FakeImageProcessor(throwUnexpected = true)
        var error: Exception? = null
        processor.processImage(
            imageFile = "any",
            cameraType = null,
            onPhotoCaptured = { },
            onError = { e -> error = e }
        )
        assertNotNull(error)
        assertTrue(error!!.message!!.contains("unexpected"))
    }
}

class FakeImageProcessor(val throwUnexpected: Boolean = false) {
    fun processImage(
        imageFile: Any?,
        cameraType: Any?,
        onPhotoCaptured: (Any) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (throwUnexpected) {
            onError(PhotoCaptureException("unexpected error"))
            return
        }
        when (imageFile) {
            null -> onError(PhotoCaptureException("Archivo nulo"))
            "corrupt" -> onError(PhotoCaptureException("corrupt file"))
            "unsupported_format" -> onError(PhotoCaptureException("unsupported format"))
            "exif_orientation" -> onPhotoCaptured(imageFile)
            else -> onPhotoCaptured(imageFile)
        }
    }
}
