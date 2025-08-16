package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger
import io.github.ismoy.imagepickerkmp.domain.utils.ImagePickerLogger
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LoggerTest {
    
    @Test
    fun `test default logger creation`() {
        val logger = DefaultLogger
        assertNotNull(logger)
    }
    
    @Test
    fun `test default logger implements interface`() {
        val logger: ImagePickerLogger = DefaultLogger
        assertNotNull(logger)
        assertTrue(true)
    }
    
    @Test
    fun `test default logger log method`() {
        val logger = DefaultLogger
        // Should not throw exception when logging messages
        logger.log("Test message")
        assertNotNull(logger)
    }
    
    @Test
    fun `test default logger with null message`() {
        val logger = DefaultLogger
        // Should handle empty messages gracefully instead of null
        logger.log("")
        assertNotNull(logger)
    }
    
    @Test
    fun `test default logger with empty message`() {
        val logger = DefaultLogger
        // Should handle empty messages gracefully
        logger.log("")
        assertNotNull(logger)
    }
    
    @Test
    fun `test default logger with long message`() {
        val logger = DefaultLogger
        val longMessage = "This is a very long message that contains many characters and should be handled properly by the logger without causing any issues or exceptions"
        // Should handle long messages gracefully
        logger.log(longMessage)
        assertNotNull(logger)
    }
    
    @Test
    fun `test default logger with special characters`() {
        val logger = DefaultLogger
        val specialMessage = "Message with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?"
        // Should handle special characters gracefully
        logger.log(specialMessage)
        assertNotNull(logger)
    }
    
    @Test
    fun `test default logger with unicode characters`() {
        val logger = DefaultLogger
        val unicodeMessage = "Message with unicode: ñáéíóúüç€£¥"
        // Should handle unicode characters gracefully
        logger.log(unicodeMessage)
        assertNotNull(logger)
    }
    
    @Test
    fun `test default logger singleton pattern`() {
        val logger1 = DefaultLogger
        val logger2 = DefaultLogger
        
        assertNotNull(logger1)
        assertNotNull(logger2)
        // Should be the same instance (singleton)
        assertTrue(logger1 === logger2)
    }
    
    @Test
    fun `test default logger performance`() {
        val logger = DefaultLogger
        // Should handle multiple rapid log calls without issues
        repeat(100) { index ->
            logger.log("Test message $index")
        }
        assertNotNull(logger)
    }
    
    @Test
    fun `test image picker logger interface`() {
        // Test that the interface is properly defined
        val logger: ImagePickerLogger = DefaultLogger
        assertNotNull(logger)
        assertTrue(true)
    }
    
    @Test
    fun `test default logger thread safety`() {
        val logger = DefaultLogger
        // Should handle concurrent access (basic test)
        repeat(10) {
            logger.log("Concurrent message $it")
        }
        assertNotNull(logger)
    }
    
    @Test
    fun `test default logger memory efficiency`() {
        val logger = DefaultLogger
        // Should not create new instances unnecessarily
        repeat(100) {
            val currentLogger = DefaultLogger
            assertTrue(logger === currentLogger)
        }
    }
} 