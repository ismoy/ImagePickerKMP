package io.github.ismoy.imagepickerkmp.domain.utils

import kotlin.test.Test
import kotlin.test.assertNotNull

class LoggerTest {

    @Test
    fun `DefaultLogger debug should not throw exception`() {
        // Test that DefaultLogger.logDebug doesn't crash
        DefaultLogger.logDebug("Test debug message")
        assertNotNull(DefaultLogger)
    }

    @Test
    fun `DefaultLogger info should not throw exception`() {
        // Test that DefaultLogger.log doesn't crash
        DefaultLogger.log("Test info message")
        assertNotNull(DefaultLogger)
    }

    @Test
    fun `DefaultLogger warn should not throw exception`() {
        // Test that DefaultLogger.log doesn't crash
        DefaultLogger.log("Test warn message")
        assertNotNull(DefaultLogger)
    }

    @Test
    fun `DefaultLogger error should not throw exception`() {
        // Test that DefaultLogger.logError doesn't crash
        DefaultLogger.logError("Test error message")
        assertNotNull(DefaultLogger)
    }

    @Test
    fun `DefaultLogger error with exception should not throw exception`() {
        // Test that DefaultLogger.logError with exception doesn't crash
        val exception = Exception("Test exception")
        DefaultLogger.logError("Test error message", exception)
        assertNotNull(DefaultLogger)
    }
}
