package io.github.ismoy.imagepickerkmp.domain.exceptions

import junit.framework.TestCase

class ExceptionsComprehensiveTest : TestCase() {

    fun testPhotoCaptureExceptionCreation() {
        val message = "Test photo capture error"
        val cause = RuntimeException("Root cause")
        
        val exception = PhotoCaptureException(message, cause)
        
        assertNotNull("Exception should not be null", exception)
        assertEquals("Message should match", message, exception.message)
        assertEquals("Cause should match", cause, exception.cause)
        assertTrue("Should be instance of ImagePickerException", exception is ImagePickerException)
    }

    fun testPermissionDeniedExceptionCreation() {
        val message = "Permission denied error"
        val cause = SecurityException("Security cause")
        
        val exception = PermissionDeniedException(message, cause)
        
        assertNotNull("Exception should not be null", exception)
        assertEquals("Message should match", message, exception.message)
        assertEquals("Cause should match", cause, exception.cause)
        assertTrue("Should be instance of ImagePickerException", exception is ImagePickerException)
    }

    fun testImageProcessingExceptionCreation() {
        val message = "Image processing error"
        
        val exception = ImageProcessingException(message)
        
        assertNotNull("Exception should not be null", exception)
        assertEquals("Message should match", message, exception.message)
        assertNull("Cause should be null", exception.cause)
        assertTrue("Should be instance of ImagePickerException", exception is ImagePickerException)
    }

    fun testExceptionHierarchy() {
        val photoCaptureException = PhotoCaptureException("test")
        val permissionException = PermissionDeniedException("test")
        val processingException = ImageProcessingException("test")
        
        assertTrue("PhotoCaptureException should extend ImagePickerException", 
            photoCaptureException is ImagePickerException)
        assertTrue("PermissionDeniedException should extend ImagePickerException", 
            permissionException is ImagePickerException)
        assertTrue("ImageProcessingException should extend ImagePickerException", 
            processingException is ImagePickerException)
    }

    fun testExceptionMessages() {
        val testMessage = "Custom error message"
        
        val photoException = PhotoCaptureException(testMessage)
        val permissionException = PermissionDeniedException(testMessage)
        val processingException = ImageProcessingException(testMessage)
        
        assertEquals("Photo exception message should match", testMessage, photoException.message)
        assertEquals("Permission exception message should match", testMessage, permissionException.message)
        assertEquals("Processing exception message should match", testMessage, processingException.message)
    }

    fun testExceptionWithCause() {
        val rootCause = IllegalArgumentException("Root cause")
        val message = "Wrapper message"
        
        val exception = PhotoCaptureException(message, rootCause)
        
        assertNotNull("Exception should not be null", exception)
        assertEquals("Message should match", message, exception.message)
        assertEquals("Cause should match", rootCause, exception.cause)
    }

    fun testExceptionWithoutCause() {
        val message = "No cause message"
        
        val exception = ImageProcessingException(message)
        
        assertNotNull("Exception should not be null", exception)
        assertEquals("Message should match", message, exception.message)
        assertNull("Cause should be null", exception.cause)
    }
}
