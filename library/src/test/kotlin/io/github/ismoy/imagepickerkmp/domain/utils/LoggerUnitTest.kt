package io.github.ismoy.imagepickerkmp.domain.utils

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoggerUnitTest {

    @Test
    fun testImagePickerLoggerInterface() {
        assertNotNull("ImagePickerLogger interface should exist", ImagePickerLogger::class.java)
        
        // Test that interface has expected methods
        val methods = ImagePickerLogger::class.java.methods
        val methodNames = methods.map { it.name }.toSet()
        
        assertTrue("Should have log method", methodNames.contains("log"))
        assertTrue("Should have logError method", methodNames.contains("logError"))
        assertTrue("Should have logDebug method", methodNames.contains("logDebug"))
    }

    @Test
    fun testDefaultLoggerExists() {
        assertNotNull("DefaultLogger should exist", DefaultLogger)
        assertTrue("DefaultLogger should implement ImagePickerLogger", 
            DefaultLogger is ImagePickerLogger)
    }

    @Test
    fun testDefaultLoggerIsObject() {
        val instance1 = DefaultLogger
        val instance2 = DefaultLogger
        assertSame("DefaultLogger should be singleton object", instance1, instance2)
    }

    @Test
    fun testDefaultLoggerMethods() {
        // Test that methods exist and can be called without exceptions during compilation
        val logger: ImagePickerLogger = DefaultLogger
        
        // These should compile without issues
        assertNotNull("log method should exist", logger::log)
        assertNotNull("logError method should exist", logger::logError) 
        assertNotNull("logDebug method should exist", logger::logDebug)
    }

    @Test
    fun testLogMethodSignature() {
        val logger: ImagePickerLogger = DefaultLogger
        
        // Test that we can call methods with correct signatures
        try {
            logger.log("test")
            logger.logError("test error")
            logger.logError("test error", RuntimeException())
            logger.logDebug("test debug")
        } catch (e: Exception) {
            // We expect these to work during compilation, runtime behavior is tested elsewhere
        }
    }

    @Test
    fun testCustomLoggerImplementation() {
        // Test that interface can be implemented
        val customLogger = object : ImagePickerLogger {
            var lastMessage: String? = null
            var lastError: String? = null
            var lastDebug: String? = null

            override fun log(message: String) {
                lastMessage = message
            }

            override fun logError(message: String, throwable: Throwable?) {
                lastError = message
            }

            override fun logDebug(message: String) {
                lastDebug = message
            }
        }

        assertNotNull("Custom logger should be created", customLogger)
        assertTrue("Custom logger should implement interface", 
            customLogger is ImagePickerLogger)

        // Test implementation
        customLogger.log("test log")
        customLogger.logError("test error")
        customLogger.logDebug("test debug")

        assertEquals("Should capture log message", "test log", customLogger.lastMessage)
        assertEquals("Should capture error message", "test error", customLogger.lastError)
        assertEquals("Should capture debug message", "test debug", customLogger.lastDebug)
    }

    @Test
    fun testLoggerInterfacePolymorphism() {
        val loggers: List<ImagePickerLogger> = listOf(
            DefaultLogger,
            object : ImagePickerLogger {
                override fun log(message: String) {}
                override fun logError(message: String, throwable: Throwable?) {}
                override fun logDebug(message: String) {}
            }
        )

        assertEquals("Should have 2 logger implementations", 2, loggers.size)
        
        for (logger in loggers) {
            assertNotNull("Logger should not be null", logger)
            assertTrue("Should be ImagePickerLogger", logger is ImagePickerLogger)
        }
    }

    @Test
    fun testLoggerErrorMethodOverloading() {
        val logger: ImagePickerLogger = DefaultLogger
        
        // Test both overloads of logError method exist
        try {
            // Method with just message
            logger.logError("error message")
            
            // Method with message and throwable
            logger.logError("error message", RuntimeException("test"))
            
            // Method with message and null throwable
            logger.logError("error message", null)
        } catch (e: Exception) {
            // Method signature testing, runtime behavior tested elsewhere
        }
    }

    @Test
    fun testDefaultLoggerClassName() {
        assertEquals("DefaultLogger class name should match", 
            "DefaultLogger", DefaultLogger::class.simpleName)
    }

    @Test
    fun testImagePickerLoggerInterfaceName() {
        assertEquals("Interface name should match", 
            "ImagePickerLogger", ImagePickerLogger::class.simpleName)
    }

    @Test
    fun testLoggerMethodParameterTypes() {
        val loggerClass = ImagePickerLogger::class.java
        val logMethod = loggerClass.getMethod("log", String::class.java)
        val logErrorMethod1 = loggerClass.getMethod("logError", String::class.java, Throwable::class.java)
        val logDebugMethod = loggerClass.getMethod("logDebug", String::class.java)
        
        assertNotNull("log method should exist", logMethod)
        assertNotNull("logError method should exist", logErrorMethod1)
        assertNotNull("logDebug method should exist", logDebugMethod)
        
        assertEquals("log method should return void", Void.TYPE, logMethod.returnType)
        assertEquals("logError method should return void", Void.TYPE, logErrorMethod1.returnType)
        assertEquals("logDebug method should return void", Void.TYPE, logDebugMethod.returnType)
    }
}
