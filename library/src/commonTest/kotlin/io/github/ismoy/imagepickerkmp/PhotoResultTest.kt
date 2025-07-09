package io.github.ismoy.imagepickerkmp

import kotlin.test.Test
import kotlin.test.assertEquals

class PhotoResultTest {
    @Test
    fun testPhotoResultData() {
        val result = CameraPhotoHandler.PhotoResult("uri", 100, 200)
        assertEquals("uri", result.uri)
        assertEquals(100, result.width)
        assertEquals(200, result.height)
    }
} 