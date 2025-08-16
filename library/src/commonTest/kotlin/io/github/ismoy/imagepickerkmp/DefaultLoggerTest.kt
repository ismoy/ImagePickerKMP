package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger
import io.github.ismoy.imagepickerkmp.domain.utils.ImagePickerLogger
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DefaultLoggerTest {
    
    @Test
    fun `DefaultLogger should implement ImagePickerLogger`() {
        val logger: ImagePickerLogger = DefaultLogger
        assertNotNull(logger)
        assertTrue(logger is ImagePickerLogger)
    }
    
    @Test
    fun `DefaultLogger log should not throw exception`() {
        // Test that logging doesn't crash
        DefaultLogger.log("Test message")
        DefaultLogger.log("")
        DefaultLogger.log("Message with special chars: !@#$%^&*()")
    }
    
    @Test
    fun `DefaultLogger logError should not throw exception`() {
        // Test that error logging doesn't crash
        DefaultLogger.logError("Test error")
        DefaultLogger.logError("Test error", Exception("Test exception"))
        DefaultLogger.logError("", null)
    }
    
    @Test
    fun `DefaultLogger logDebug should not throw exception`() {
        // Test that debug logging doesn't crash
        DefaultLogger.logDebug("Test debug message")
        DefaultLogger.logDebug("")
        DefaultLogger.logDebug("Debug with numbers: 123456")
    }
    
    @Test
    fun `DefaultLogger should handle null throwable`() {
        // Test that null throwable is handled gracefully
        DefaultLogger.logError("Error without throwable", null)
    }
    
    @Test
    fun `DefaultLogger should handle long messages`() {
        val longMessage = "A".repeat(1000)
        DefaultLogger.log(longMessage)
        DefaultLogger.logError(longMessage)
        DefaultLogger.logDebug(longMessage)
    }
    
    @Test
    fun `DefaultLogger should handle multiline messages`() {
        val multilineMessage = "Line 1\nLine 2\nLine 3"
        DefaultLogger.log(multilineMessage)
        DefaultLogger.logError(multilineMessage)
        DefaultLogger.logDebug(multilineMessage)
    }
    
    @Test
    fun `DefaultLogger should be singleton`() {
        val logger1 = DefaultLogger
        val logger2 = DefaultLogger
        
        assertTrue(logger1 === logger2)
    }
    
    @Test
    fun `DefaultLogger should handle various exception types`() {
        DefaultLogger.logError("Runtime exception", RuntimeException("Test"))
        DefaultLogger.logError("Illegal argument", IllegalArgumentException("Test"))
        DefaultLogger.logError("Null pointer", NullPointerException("Test"))
        DefaultLogger.logError("IO exception", Exception("IO error"))
    }
    
    @Test
    fun `DefaultLogger should handle concurrent access`() {
        // Test that concurrent logging doesn't cause issues
        repeat(10) { i ->
            DefaultLogger.log("Concurrent message $i")
            DefaultLogger.logError("Concurrent error $i")
            DefaultLogger.logDebug("Concurrent debug $i")
        }
    }
}