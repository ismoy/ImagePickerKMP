package io.github.ismoy.imagepickerkmp

// TODO: Re-enable this test once Compose testing is properly configured
/*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CameraPhotoHandlerTest {
    // Test implementation will be added back once Compose testing is configured
}
*/

import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class CameraPhotoHandlerTest {
    @Test
    fun testPhotoResultProperties() {
        val result = PhotoResult(
            uri = "file:///tmp/camera.jpg",
            width = 800,
            height = 600
        )
        assertEquals("file:///tmp/camera.jpg", result.uri)
        assertEquals(800, result.width)
        assertEquals(600, result.height)
    }

    @Test
    fun testPhotoResultEquality() {
        val result1 = PhotoResult(
            uri = "file:///tmp/camera.jpg",
            width = 800,
            height = 600
        )
        val result2 = PhotoResult(
            uri = "file:///tmp/camera.jpg",
            width = 800,
            height = 600
        )
        val result3 = PhotoResult(
            uri = "file:///tmp/other.jpg",
            width = 320,
            height = 240
        )
        assertEquals(result1, result2)
        assertNotEquals(result1, result3)
    }
}
