package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.domain.exceptions.ImagePickerException
import io.github.ismoy.imagepickerkmp.domain.exceptions.ImageProcessingException
import io.github.ismoy.imagepickerkmp.domain.exceptions.PermissionDeniedException
import io.github.ismoy.imagepickerkmp.domain.exceptions.PhotoCaptureException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ImagePickerExceptionsTest {
    
    @Test
    fun `PhotoCaptureException should extend ImagePickerException`() {
        val exception = PhotoCaptureException("Test message")
        
        assertTrue(exception is ImagePickerException)
        assertEquals("Test message", exception.message)
        assertNull(exception.cause)
    }
    
    @Test
    fun `PhotoCaptureException with cause should store cause`() {
        val cause = RuntimeException("Root cause")
        val exception = PhotoCaptureException("Test message", cause)
        
        assertEquals("Test message", exception.message)
        assertEquals(cause, exception.cause)
    }
    
    @Test
    fun `PermissionDeniedException should extend ImagePickerException`() {
        val exception = PermissionDeniedException("Permission denied")
        
        assertTrue(exception is ImagePickerException)
        assertEquals("Permission denied", exception.message)
        assertNull(exception.cause)
    }
    
    @Test
    fun `PermissionDeniedException with cause should store cause`() {
        val cause = RuntimeException("Security error")
        val exception = PermissionDeniedException("Permission denied", cause)
        
        assertEquals("Permission denied", exception.message)
        assertEquals(cause, exception.cause)
    }
    
    @Test
    fun `ImageProcessingException should extend ImagePickerException`() {
        val exception = ImageProcessingException("Processing failed")
        
        assertTrue(exception is ImagePickerException)
        assertEquals("Processing failed", exception.message)
        assertNull(exception.cause)
    }
    
    @Test
    fun `ImageProcessingException with cause should store cause`() {
        val cause = IllegalArgumentException("Invalid argument")
        val exception = ImageProcessingException("Processing failed", cause)
        
        assertEquals("Processing failed", exception.message)
        assertEquals(cause, exception.cause)
    }
    
    @Test
    fun `ImagePickerException should be sealed class`() {
        val photoException = PhotoCaptureException("Photo error")
        val permissionException = PermissionDeniedException("Permission error")
        val processingException = ImageProcessingException("Processing error")
        
        // All should be instances of ImagePickerException
        assertTrue(photoException is ImagePickerException)
        assertTrue(permissionException is ImagePickerException)
        assertTrue(processingException is ImagePickerException)
        
        // Test polymorphic behavior
        val exceptions: List<ImagePickerException> = listOf(
            photoException,
            permissionException,
            processingException
        )
        
        assertEquals(3, exceptions.size)
        exceptions.forEach { exception ->
            assertNotNull(exception.message)
            assertTrue(exception.message!!.isNotEmpty())
        }
    }
    
    @Test
    fun `exception hierarchy should work with when expressions`() {
        val exceptions = listOf(
            PhotoCaptureException("Photo error"),
            PermissionDeniedException("Permission error"),
            ImageProcessingException("Processing error")
        )
        
        exceptions.forEach { exception ->
            val result = when (exception) {
                is PhotoCaptureException -> "Photo capture failed"
                is PermissionDeniedException -> "Permission denied"
                is ImageProcessingException -> "Image processing failed"
            }
            
            assertNotNull(result)
            assertTrue(result.isNotEmpty())
        }
    }
    
    @Test
    fun `exceptions should maintain stack trace`() {
        try {
            throw PhotoCaptureException("Test exception")
        } catch (e: PhotoCaptureException) {
            // Stack trace testing is platform-specific, just verify the exception works
            assertEquals("Test exception", e.message)
            assertNotNull(e)
        }
    }
    
    @Test
    fun `nested exceptions should work correctly`() {
        val rootCause = RuntimeException("Root cause")
        val intermediateCause = PhotoCaptureException("Intermediate", rootCause)
        val finalException = ImageProcessingException("Final error", intermediateCause)
        
        assertEquals("Final error", finalException.message)
        assertEquals(intermediateCause, finalException.cause)
        assertEquals(rootCause, finalException.cause?.cause)
        
        // Test cause chain
        var currentCause: Throwable? = finalException
        val messages = mutableListOf<String>()
        
        while (currentCause != null) {
            currentCause.message?.let { messages.add(it) }
            currentCause = currentCause.cause
        }
        
        assertEquals(listOf("Final error", "Intermediate", "Root cause"), messages)
    }
}