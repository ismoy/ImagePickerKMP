package io.github.ismoy.imagepickerkmp.domain.utils

import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.io.ByteArrayOutputStream
import java.io.PrintStream

@RunWith(MockitoJUnitRunner::class)
class LoggerTest {

    private lateinit var outputStream: ByteArrayOutputStream
    private lateinit var originalOut: PrintStream

    @Before
    fun setUp() {
        // Capture console output for testing
        outputStream = ByteArrayOutputStream()
        originalOut = System.out
        System.setOut(PrintStream(outputStream))
    }

    @After
    fun tearDown() {
        // Restore original output stream
        System.setOut(originalOut)
    }

    @Test
    fun testDefaultLoggerLogMessage() {
        // Arrange
        val message = "Test log message"

        // Act
        DefaultLogger.log(message)

        // Assert
        val output = outputStream.toString().trim()
        assertEquals("Log output should match expected format", "[ImagePicker] $message", output)
    }

    @Test
    fun testDefaultLoggerLogError() {
        // Arrange
        val message = "Test error message"

        // Act
        DefaultLogger.logError(message)

        // Assert
        val output = outputStream.toString().trim()
        assertEquals("Error log output should match expected format", "[ImagePicker ERROR] $message", output)
    }

    @Test
    fun testDefaultLoggerLogErrorWithThrowable() {
        // Arrange
        val message = "Test error with exception"
        val exception = RuntimeException("Test exception")

        // Act
        DefaultLogger.logError(message, exception)

        // Assert
        val output = outputStream.toString()
        assertTrue("Error log should contain error message", output.contains("[ImagePicker ERROR] $message"))
        assertTrue("Error log should contain exception info", output.contains("RuntimeException"))
        assertTrue("Error log should contain 'Test exception'", output.contains("Test exception"))
    }

    @Test
    fun testDefaultLoggerLogErrorWithNullThrowable() {
        // Arrange
        val message = "Test error without exception"

        // Act
        DefaultLogger.logError(message, null)

        // Assert
        val output = outputStream.toString().trim()
        assertEquals("Error log output should match expected format", "[ImagePicker ERROR] $message", output)
    }

    @Test
    fun testDefaultLoggerLogDebug() {
        // Arrange
        val message = "Test debug message"

        // Act
        DefaultLogger.logDebug(message)

        // Assert
        val output = outputStream.toString().trim()
        assertEquals("Debug log output should match expected format", "[ImagePicker DEBUG] $message", output)
    }

    @Test
    fun testDefaultLoggerIsInstanceOfImagePickerLogger() {
        // Assert
        assertTrue("DefaultLogger should implement ImagePickerLogger", 
            DefaultLogger is ImagePickerLogger)
    }

    @Test
    fun testDefaultLoggerObjectIsSingleton() {
        // Act
        val instance1 = DefaultLogger
        val instance2 = DefaultLogger

        // Assert
        assertSame("DefaultLogger should be same instance (singleton)", instance1, instance2)
    }

    @Test
    fun testImagePickerLoggerInterfaceMethods() {
        // Verify that the interface has all expected methods
        val logger: ImagePickerLogger = DefaultLogger

        // Test that all methods exist and can be called
        assertNotNull("log method should exist", logger::log)
        assertNotNull("logError method should exist", logger::logError)
        assertNotNull("logDebug method should exist", logger::logDebug)
    }

    @Test
    fun testLogWithEmptyMessage() {
        // Arrange
        val message = ""

        // Act
        DefaultLogger.log(message)

        // Assert
        val output = outputStream.toString().trim()
        assertEquals("Should handle empty message", "[ImagePicker] ", output)
    }

    @Test
    fun testLogErrorWithEmptyMessage() {
        // Arrange
        val message = ""

        // Act
        DefaultLogger.logError(message)

        // Assert
        val output = outputStream.toString().trim()
        assertEquals("Should handle empty error message", "[ImagePicker ERROR] ", output)
    }

    @Test
    fun testLogDebugWithEmptyMessage() {
        // Arrange
        val message = ""

        // Act
        DefaultLogger.logDebug(message)

        // Assert
        val output = outputStream.toString().trim()
        assertEquals("Should handle empty debug message", "[ImagePicker DEBUG] ", output)
    }

    @Test
    fun testLogWithSpecialCharacters() {
        // Arrange
        val message = "Message with special chars: @#$%^&*()[]{}|\\:;\"'<>,.?/~`"

        // Act
        DefaultLogger.log(message)

        // Assert
        val output = outputStream.toString().trim()
        assertEquals("Should handle special characters", "[ImagePicker] $message", output)
    }

    @Test
    fun testLogWithNewlines() {
        // Arrange
        val message = "Line 1\nLine 2\nLine 3"

        // Act
        DefaultLogger.log(message)

        // Assert
        val output = outputStream.toString()
        assertTrue("Should contain first line", output.contains("[ImagePicker] Line 1"))
        assertTrue("Should contain newlines", output.contains("\n"))
    }

    @Test
    fun testLogErrorWithComplexException() {
        // Arrange
        val message = "Complex error scenario"
        val rootCause = IllegalArgumentException("Root cause")
        val exception = RuntimeException("Wrapper exception", rootCause)

        // Act
        DefaultLogger.logError(message, exception)

        // Assert
        val output = outputStream.toString()
        assertTrue("Should contain error message", output.contains("[ImagePicker ERROR] $message"))
        assertTrue("Should contain RuntimeException", output.contains("RuntimeException"))
        assertTrue("Should contain wrapper message", output.contains("Wrapper exception"))
        // Stack trace should also include root cause information
    }

    @Test
    fun testMultipleLogCalls() {
        // Arrange
        val messages = arrayOf("Message 1", "Message 2", "Message 3")

        // Act
        for (message in messages) {
            DefaultLogger.log(message)
        }

        // Assert
        val output = outputStream.toString()
        for (message in messages) {
            assertTrue("Should contain $message", output.contains("[ImagePicker] $message"))
        }
    }

    @Test
    fun testMixedLogLevels() {
        // Arrange
        val logMessage = "Regular log"
        val debugMessage = "Debug info"
        val errorMessage = "Error occurred"

        // Act
        DefaultLogger.log(logMessage)
        DefaultLogger.logDebug(debugMessage)
        DefaultLogger.logError(errorMessage)

        // Assert
        val output = outputStream.toString()
        assertTrue("Should contain regular log", output.contains("[ImagePicker] $logMessage"))
        assertTrue("Should contain debug log", output.contains("[ImagePicker DEBUG] $debugMessage"))
        assertTrue("Should contain error log", output.contains("[ImagePicker ERROR] $errorMessage"))
    }

    @Test
    fun testLogOrderPreservation() {
        // Arrange
        val firstMessage = "First message"
        val secondMessage = "Second message"

        // Act
        DefaultLogger.log(firstMessage)
        DefaultLogger.log(secondMessage)

        // Assert
        val output = outputStream.toString()
        val firstIndex = output.indexOf("[ImagePicker] $firstMessage")
        val secondIndex = output.indexOf("[ImagePicker] $secondMessage")
        
        assertTrue("First message should be found", firstIndex >= 0)
        assertTrue("Second message should be found", secondIndex >= 0)
        assertTrue("First message should come before second", firstIndex < secondIndex)
    }

    @Test
    fun testLoggerInterfaceCanBeImplemented() {
        // Create a custom implementation to test interface usability
        val customLogger = object : ImagePickerLogger {
            var lastMessage: String? = null
            var lastError: String? = null
            var lastDebug: String? = null
            var lastThrowable: Throwable? = null

            override fun log(message: String) {
                lastMessage = message
            }

            override fun logError(message: String, throwable: Throwable?) {
                lastError = message
                lastThrowable = throwable
            }

            override fun logDebug(message: String) {
                lastDebug = message
            }
        }

        // Act
        customLogger.log("test message")
        customLogger.logError("test error", RuntimeException())
        customLogger.logDebug("test debug")

        // Assert
        assertEquals("Custom logger should capture log message", "test message", customLogger.lastMessage)
        assertEquals("Custom logger should capture error message", "test error", customLogger.lastError)
        assertEquals("Custom logger should capture debug message", "test debug", customLogger.lastDebug)
        assertNotNull("Custom logger should capture throwable", customLogger.lastThrowable)
    }

    @Test
    fun testDefaultLoggerCanBeUsedAsInterface() {
        // Act
        val logger: ImagePickerLogger = DefaultLogger

        // This should compile and work without issues
        logger.log("Interface test")
        logger.logError("Interface error test")
        logger.logDebug("Interface debug test")

        // Assert
        val output = outputStream.toString()
        assertTrue("Should work through interface", output.contains("[ImagePicker] Interface test"))
        assertTrue("Should work through interface", output.contains("[ImagePicker ERROR] Interface error test"))
        assertTrue("Should work through interface", output.contains("[ImagePicker DEBUG] Interface debug test"))
    }
}
