package io.github.ismoy.imagepickerkmp.domain.utils

import org.junit.Test
import org.junit.Assert.*

class SimpleUtilsTest {

    @Test
    fun imagePickerLogger_shouldNotCrash() {
        val logger = ImagePickerLogger()
        
        // Test basic logger functionality doesn't crash
        assertNotNull(logger)
        
        // Test logging methods don't throw exceptions
        logger.logError("test message", Exception("test"))
        logger.logInfo("test info")
        logger.logDebug("test debug")
        logger.logWarning("test warning")
    }

    @Test
    fun logger_staticMethods_shouldWork() {
        // Test static methods if they exist
        assertNotNull(Logger)
        
        // These should not throw exceptions
        Logger.debug("test debug")
        Logger.info("test info") 
        Logger.warn("test warning")
        Logger.error("test error")
    }

    @Test
    fun logger_withException_shouldWork() {
        val exception = RuntimeException("test exception")
        
        // Should not throw
        Logger.error("test message", exception)
        Logger.warn("test warning", exception)
        
        assertNotNull(exception)
    }
}
