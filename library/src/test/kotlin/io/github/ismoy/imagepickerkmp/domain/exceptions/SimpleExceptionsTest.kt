package io.github.ismoy.imagepickerkmp.domain.exceptions

import org.junit.Test
import org.junit.Assert.*

class SimpleExceptionsTest {

    @Test
    fun photoCaptureException_shouldHaveMessage() {
        val message = "Camera capture failed"
        val exception = PhotoCaptureException(message)
        
        assertEquals(message, exception.message)
    }

    @Test
    fun photoCaptureException_shouldHaveMessageAndCause() {
        val message = "Camera capture failed"
        val cause = RuntimeException("Internal error")
        val exception = PhotoCaptureException(message, cause)
        
        assertEquals(message, exception.message)
        assertEquals(cause, exception.cause)
    }

    @Test
    fun photoCaptureException_shouldBeException() {
        val exception = PhotoCaptureException("test")
        
        assertTrue(exception is Exception)
    }
}
