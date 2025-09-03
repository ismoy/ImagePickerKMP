package io.github.ismoy.imagepickerkmp.domain.exceptions

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PhotoCaptureExceptionTest {

    @Test
    fun `PhotoCaptureException should have message`() {
        val message = "Camera capture failed"
        val exception = PhotoCaptureException(message)
        
        assertEquals(message, exception.message)
    }

    @Test
    fun `PhotoCaptureException should have message and cause`() {
        val message = "Camera capture failed"
        val cause = RuntimeException("Internal error")
        val exception = PhotoCaptureException(message, cause)
        
        assertEquals(message, exception.message)
        assertEquals(cause, exception.cause)
    }

    @Test
    fun `PhotoCaptureException should extend Exception`() {
        val exception = PhotoCaptureException("test")
        
        assertNotNull(exception as Exception)
    }
}
