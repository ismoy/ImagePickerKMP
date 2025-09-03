package io.github.ismoy.imagepickerkmp.domain.utils

import kotlin.test.Test
import kotlin.test.assertNotNull

class LoggerTest {

    @Test
    fun `Logger debug should not throw exception`() {
        // Test that Logger.debug doesn't crash
        Logger.debug("Test debug message")
        assertNotNull(Logger)
    }

    @Test
    fun `Logger info should not throw exception`() {
        // Test that Logger.info doesn't crash
        Logger.info("Test info message")
        assertNotNull(Logger)
    }

    @Test
    fun `Logger warn should not throw exception`() {
        // Test that Logger.warn doesn't crash
        Logger.warn("Test warn message")
        assertNotNull(Logger)
    }

    @Test
    fun `Logger error should not throw exception`() {
        // Test that Logger.error doesn't crash
        Logger.error("Test error message")
        assertNotNull(Logger)
    }

    @Test
    fun `Logger error with exception should not throw exception`() {
        // Test that Logger.error with exception doesn't crash
        val exception = Exception("Test exception")
        Logger.error("Test error message", exception)
        assertNotNull(Logger)
    }
}
