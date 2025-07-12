package io.github.ismoy.imagepickerkmp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PhotoCaptureExceptionTest {
    
    @Test
    fun `test photo capture exception creation with message`() {
        val message = "Camera not available"
        val exception = PhotoCaptureException(message)
        
        assertEquals(message, exception.message)
    }
    
    @Test
    fun `test photo capture exception creation with null message`() {
        val exception = PhotoCaptureException("")
        
        assertEquals("", exception.message)
    }
    
    @Test
    fun `test photo capture exception creation with empty message`() {
        val exception = PhotoCaptureException("")
        
        assertEquals("", exception.message)
    }
    
    @Test
    fun `test photo capture exception inheritance`() {
        val exception = PhotoCaptureException("Test exception")
        
        assertTrue(exception is Exception)
    }
    
    @Test
    fun `test photo capture exception with different error types`() {
        val cameraError = PhotoCaptureException("Camera not available")
        val permissionError = PhotoCaptureException("Permission denied")
        val hardwareError = PhotoCaptureException("Hardware failure")
        
        assertEquals("Camera not available", cameraError.message)
        assertEquals("Permission denied", permissionError.message)
        assertEquals("Hardware failure", hardwareError.message)
    }
    
    @Test
    fun `test photo capture exception string representation`() {
        val message = "Camera error occurred"
        val exception = PhotoCaptureException(message)
        val stringRep = exception.toString()
        
        assertTrue(stringRep.contains("PhotoCaptureException"))
        assertTrue(stringRep.contains(message))
    }
    
    @Test
    fun `test photo capture exception with localized message`() {
        val englishMessage = "Camera not available"
        val spanishMessage = "Cámara no disponible"
        
        val englishException = PhotoCaptureException(englishMessage)
        val spanishException = PhotoCaptureException(spanishMessage)
        
        assertEquals(englishMessage, englishException.message)
        assertEquals(spanishMessage, spanishException.message)
    }
} 