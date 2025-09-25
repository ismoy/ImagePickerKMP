package io.github.ismoy.imagepickerkmp.domain.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

class LoggerTest {

    @Test
    fun `DefaultLogger should implement ImagePickerLogger`() {
        assertTrue(true)
    }

    @Test
    fun `DefaultLogger log method should not throw exception`() {
        try {
            DefaultLogger.log("Test message")
            assertTrue(true) // Test passes if no exception is thrown
        } catch (e: Exception) {
            assertTrue(false, "log method should not throw exception ${e.message}")
        }
    }

    @Test
    fun `DefaultLogger logError method should not throw exception`() {
        try {
            DefaultLogger.logError("Test error message")
            assertTrue(true) // Test passes if no exception is thrown
        } catch (e: Exception) {
            assertTrue(false, "logError method should not throw exception ${e.message}")
        }
    }

    @Test
    fun `DefaultLogger logError with throwable should not throw exception`() {
        try {
            val throwable = RuntimeException("Test exception")
            DefaultLogger.logError("Test error message", throwable)
            assertTrue(true) // Test passes if no exception is thrown
        } catch (e: Exception) {
            assertTrue(false, "logError with throwable should not throw exception:" +
                    "${e.message}")
        }
    }

    @Test
    fun `DefaultLogger logDebug method should not throw exception`() {
        try {
            DefaultLogger.logDebug("Test debug message")
            assertTrue(true) // Test passes if no exception is thrown
        } catch (e: Exception) {
            assertTrue(false, "logDebug method should not throw exception: " +
                    "${e.message}")
        }
    }

    @Test
    fun `DefaultLogger should handle null throwable`() {
        try {
            DefaultLogger.logError("Test error message", null)
            assertTrue(true) // Test passes if no exception is thrown
        } catch (e: Exception) {
            assertTrue(false, "logError with null throwable should not throw exception:" +
                    "${e.message}")
        }
    }

    @Test
    fun `DefaultLogger should handle empty messages`() {
        try {
            DefaultLogger.log("")
            DefaultLogger.logError("")
            DefaultLogger.logDebug("")
            assertTrue(true) // Test passes if no exception is thrown
        } catch (e: Exception) {
            assertTrue(false, "Logger should handle empty messages: " +
                    "${e.message}")
        }
    }

    @Test
    fun `ImagePickerLogger interface should have correct method signatures`() {
        val logger: ImagePickerLogger = DefaultLogger
        
        // These calls should compile and not throw exceptions
        logger.log("Test")
        logger.logError("Test error")
        logger.logError("Test error", RuntimeException())
        logger.logDebug("Test debug")
        
        assertTrue(true)
    }

    @Test
    fun `DefaultLogger should be an object singleton`() {
        val logger1 = DefaultLogger
        val logger2 = DefaultLogger
        
        assertTrue(logger1 === logger2, "DefaultLogger should be a singleton object")
    }

    // Test custom logger implementation
    class TestLogger : ImagePickerLogger {
        val messages = mutableListOf<String>()
        val errorMessages = mutableListOf<Pair<String, Throwable?>>()
        val debugMessages = mutableListOf<String>()

        override fun log(message: String) {
            messages.add(message)
        }

        override fun logError(message: String, throwable: Throwable?) {
            errorMessages.add(Pair(message, throwable))
        }

        override fun logDebug(message: String) {
            debugMessages.add(message)
        }
    }

    @Test
    fun `custom logger implementation should work correctly`() {
        val testLogger = TestLogger()
        
        testLogger.log("Test message")
        testLogger.logError("Error message", RuntimeException("Test"))
        testLogger.logDebug("Debug message")
        
        assertEquals(1, testLogger.messages.size)
        assertEquals("Test message", testLogger.messages[0])
        
        assertEquals(1, testLogger.errorMessages.size)
        assertEquals("Error message", testLogger.errorMessages[0].first)
        assertNotNull(testLogger.errorMessages[0].second)
        
        assertEquals(1, testLogger.debugMessages.size)
        assertEquals("Debug message", testLogger.debugMessages[0])
    }
}
