package io.github.ismoy.imagepickerkmp.domain.utils

import org.junit.Test
import org.junit.Assert.*

class SimpleUtilsTest {

    @Test
    fun imagePickerLogger_shouldNotCrash() {
        val logger = object : ImagePickerLogger {
            override fun log(message: String) { }
            override fun logError(message: String, throwable: Throwable?) { }
            override fun logDebug(message: String) { }
        }
        
        // Test basic logger functionality doesn't crash
        assertNotNull(logger)
        
        // Test logging methods don't throw exceptions
        logger.logError("test message", Exception("test"))
        logger.log("test info")
        logger.logDebug("test debug")
    }

    @Test
    fun defaultLogger_staticMethods_shouldWork() {
        // Test static methods if they exist
        assertNotNull(DefaultLogger)
        
        // These should not throw exceptions
        DefaultLogger.logDebug("test debug")
        DefaultLogger.log("test info") 
        DefaultLogger.logError("test error")
    }

    @Test
    fun defaultLogger_withException_shouldWork() {
        val exception = RuntimeException("test exception")
        
        // Should not throw
        DefaultLogger.logError("test message", exception)
        
        assertNotNull(exception)
    }
}
