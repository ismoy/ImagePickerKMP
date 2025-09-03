package io.github.ismoy.imagepickerkmp.domain.exceptions

import junit.framework.TestCase

class ExceptionsComprehensiveTest : TestCase() {

    fun testImagePickerExceptionCreation() {
        val message = "Test image picker error"
        val cause = RuntimeException("Root cause")
        
        val exception = ImagePickerException(message, cause)
        
        assertNotNull("Exception should not be null", exception)
        assertEquals("Message should match", message, exception.message)
        assertEquals("Cause should match", cause, exception.cause)
        assertTrue("Should be instance of Exception", exception is Exception)
    }

    fun testImagePickerExceptionWithDefaultCause() {
        val message = "Test error without cause"
        
        val exception = ImagePickerException(message)
        
        assertNotNull("Exception should not be null", exception)
        assertEquals("Message should match", message, exception.message)
        assertNull("Cause should be null", exception.cause)
    }

    fun testPhotoCaptureExceptionCreation() {
        val message = "Photo capture failed"
        val cause = IllegalStateException("Camera not ready")
        
        val exception = PhotoCaptureException(message, cause)
        
        assertNotNull("Exception should not be null", exception)
        assertEquals("Message should match", message, exception.message)
        assertEquals("Cause should match", cause, exception.cause)
        assertTrue("Should be instance of ImagePickerException", exception is ImagePickerException)
        assertTrue("Should be instance of Exception", exception is Exception)
    }

    fun testPhotoCaptureExceptionWithDefaultCause() {
        val message = "Photo capture error"
        
        val exception = PhotoCaptureException(message)
        
        assertNotNull("Exception should not be null", exception)
        assertEquals("Message should match", message, exception.message)
        assertNull("Cause should be null", exception.cause)
    }

    fun testPermissionDeniedExceptionCreation() {
        val message = "Camera permission denied"
        val cause = SecurityException("No camera permission")
        
        val exception = PermissionDeniedException(message, cause)
        
        assertNotNull("Exception should not be null", exception)
        assertEquals("Message should match", message, exception.message)
        assertEquals("Cause should match", cause, exception.cause)
        assertTrue("Should be instance of ImagePickerException", exception is ImagePickerException)
    }

    fun testPermissionDeniedExceptionWithDefaultCause() {
        val message = "Permission denied"
        
        val exception = PermissionDeniedException(message)
        
        assertNotNull("Exception should not be null", exception)
        assertEquals("Message should match", message, exception.message)
        assertNull("Cause should be null", exception.cause)
    }

    fun testImageProcessingExceptionCreation() {
        val message = "Image processing failed"
        val cause = OutOfMemoryError("Insufficient memory")
        
        val exception = ImageProcessingException(message, cause)
        
        assertNotNull("Exception should not be null", exception)
        assertEquals("Message should match", message, exception.message)
        assertEquals("Cause should match", cause, exception.cause)
        assertTrue("Should be instance of ImagePickerException", exception is ImagePickerException)
    }

    fun testImageProcessingExceptionWithDefaultCause() {
        val message = "Processing error"
        
        val exception = ImageProcessingException(message)
        
        assertNotNull("Exception should not be null", exception)
        assertEquals("Message should match", message, exception.message)
        assertNull("Cause should be null", exception.cause)
    }

    fun testExceptionInheritance() {
        val photoException = PhotoCaptureException("Photo error")
        val permissionException = PermissionDeniedException("Permission error")
        val processingException = ImageProcessingException("Processing error")
        
        // Test inheritance chain
        assertTrue("PhotoCaptureException should extend ImagePickerException", 
                  photoException is ImagePickerException)
        assertTrue("PermissionDeniedException should extend ImagePickerException", 
                  permissionException is ImagePickerException)
        assertTrue("ImageProcessingException should extend ImagePickerException", 
                  processingException is ImagePickerException)
        
        // Test they're all Throwable
        assertTrue("PhotoCaptureException should be Throwable", photoException is Throwable)
        assertTrue("PermissionDeniedException should be Throwable", permissionException is Throwable)
        assertTrue("ImageProcessingException should be Throwable", processingException is Throwable)
    }

    fun testExceptionMessageFormatting() {
        val baseMessage = "Base error"
        val detailMessage = "Detailed information"
        val fullMessage = "$baseMessage: $detailMessage"
        
        val exception = ImagePickerException(fullMessage)
        
        assertNotNull("Exception message should not be null", exception.message)
        assertTrue("Message should contain base message", 
                  exception.message!!.contains(baseMessage))
        assertTrue("Message should contain detail message", 
                  exception.message!!.contains(detailMessage))
    }

    fun testExceptionStackTrace() {
        try {
            throw PhotoCaptureException("Test exception")
        } catch (e: PhotoCaptureException) {
            assertNotNull("Stack trace should not be null", e.stackTrace)
            assertTrue("Stack trace should not be empty", e.stackTrace.isNotEmpty())
            
            val topFrame = e.stackTrace[0]
            assertNotNull("Top frame should not be null", topFrame)
            assertTrue("Should contain test method", 
                      topFrame.methodName.contains("testExceptionStackTrace"))
        }
    }
}
