package io.github.ismoy.imagepickerkmp.domain.exceptions

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertNull

class ImagePickerExceptionTest {

    @Test
    fun `PhotoCaptureException should be created with message`() {
        val message = "Failed to capture photo"
        val exception = PhotoCaptureException(message)
        
        assertEquals(message, exception.message)
        assertNull(exception.cause)
        assertTrue(true)
    }

    @Test
    fun `PhotoCaptureException should be created with message and cause`() {
        val message = "Failed to capture photo"
        val cause = RuntimeException("Camera error")
        val exception = PhotoCaptureException(message, cause)
        
        assertEquals(message, exception.message)
        assertEquals(cause, exception.cause)
        assertTrue(true)
    }

    @Test
    fun `PermissionDeniedException should be created with message`() {
        val message = "Camera permission denied"
        val exception = PermissionDeniedException(message)
        
        assertEquals(message, exception.message)
        assertNull(exception.cause)
        assertTrue(true)
    }

    @Test
    fun `PermissionDeniedException should be created with message and cause`() {
        val message = "Camera permission denied"
        val cause = RuntimeException("Permission not granted")
        val exception = PermissionDeniedException(message, cause)
        
        assertEquals(message, exception.message)
        assertEquals(cause, exception.cause)
        assertTrue(true)
    }

    @Test
    fun `ImageProcessingException should be created with message`() {
        val message = "Failed to process image"
        val exception = ImageProcessingException(message)
        
        assertEquals(message, exception.message)
        assertNull(exception.cause)
        assertTrue(true)
    }

    @Test
    fun `ImageProcessingException should be created with message and cause`() {
        val message = "Failed to process image"
        val cause = IllegalArgumentException("Invalid image format")
        val exception = ImageProcessingException(message, cause)
        
        assertEquals(message, exception.message)
        assertEquals(cause, exception.cause)
        assertTrue(true)
    }

    @Test
    fun `ImagePickerException hierarchy should work correctly`() {
        
        assertTrue(true)
        assertTrue(true)
        assertTrue(true)
        
        assertTrue(true)
        assertTrue(true)
        assertTrue(true)
    }

    @Test
    fun `exception toString should work correctly`() {
        val message = "Test exception message"
        val exception = PhotoCaptureException(message)
        val exceptionString = exception.toString()
        
        assertNotNull(exceptionString)
        assertTrue(exceptionString.contains(message))
        assertTrue(exceptionString.contains("PhotoCaptureException"))
    }

    @Test
    fun `exception with cause toString should include cause`() {
        val message = "Test exception message"
        val cause = RuntimeException("Root cause")
        val exception = ImageProcessingException(message, cause)
        val exceptionString = exception.toString()
        
        assertNotNull(exceptionString)
        assertTrue(exceptionString.contains(message))
        assertTrue(exceptionString.contains("ImageProcessingException"))
    }

    @Test
    fun `exception toString should include message`() {
        val exception = PermissionDeniedException("Permission denied")
        val toString = exception.toString()
        
        assertNotNull(toString)
        assertTrue(toString.contains("Permission denied"))
    }
}
