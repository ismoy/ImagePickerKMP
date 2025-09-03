package io.github.ismoy.imagepickerkmp.domain.models

import kotlin.test.Test
import kotlin.test.assertEquals

class PhotoResultTest {

    @Test
    fun `PhotoResult should contain correct data`() {
        val result = PhotoResult(
            uri = "test_uri",
            width = 100,
            height = 200
        )
        
        assertEquals("test_uri", result.uri)
        assertEquals(100, result.width)
        assertEquals(200, result.height)
    }
}


