package io.github.ismoy.imagepickerkmp.presentation.viewModel

import io.github.ismoy.imagepickerkmp.domain.utils.ImagePickerLogger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ImagePickerViewModelTest {

    @Mock
    private lateinit var logger: ImagePickerLogger

    private lateinit var viewModel: ImagePickerViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = ImagePickerViewModel(logger)
    }

    @Test
    fun testViewModelCreation() {
        assertNotNull("ViewModel should be created successfully", viewModel)
    }

    @Test
    fun testErrorStateInitialValue() = runTest {
        // Act
        val initialError = viewModel.error.first()

        // Assert
        assertNull("Initial error should be null", initialError)
    }

    @Test
    fun testOnErrorSetsErrorMessage() = runTest {
        // Arrange
        val testException = RuntimeException("Test error message")

        // Act
        viewModel.onError(testException)
        val errorMessage = viewModel.error.first()

        // Assert
        assertEquals("Error message should match exception message", 
            "Test error message", errorMessage)
        verify(logger).logError("Error capturing photo", testException)
    }

    @Test
    fun testOnErrorWithNullMessage() = runTest {
        // Arrange
        val testException = RuntimeException(null as String?)

        // Act
        viewModel.onError(testException)
        val errorMessage = viewModel.error.first()

        // Assert
        assertNull("Error message should be null when exception message is null", errorMessage)
        verify(logger).logError("Error capturing photo", testException)
    }

    @Test
    fun testOnErrorWithEmptyMessage() = runTest {
        // Arrange
        val testException = RuntimeException("")

        // Act
        viewModel.onError(testException)
        val errorMessage = viewModel.error.first()

        // Assert
        assertEquals("Error message should be empty string", "", errorMessage)
        verify(logger).logError("Error capturing photo", testException)
    }

    @Test
    fun testOnErrorLogsToLogger() {
        // Arrange
        val testException = IllegalStateException("Test illegal state")

        // Act
        viewModel.onError(testException)

        // Assert
        verify(logger).logError("Error capturing photo", testException)
    }

    @Test
    fun testMultipleErrorCalls() = runTest {
        // Arrange
        val firstException = RuntimeException("First error")
        val secondException = IllegalStateException("Second error")

        // Act
        viewModel.onError(firstException)
        val firstError = viewModel.error.first()

        viewModel.onError(secondException)
        val secondError = viewModel.error.first()

        // Assert
        assertEquals("First error should be set", "First error", firstError)
        assertEquals("Second error should replace first", "Second error", secondError)
        
        verify(logger).logError("Error capturing photo", firstException)
        verify(logger).logError("Error capturing photo", secondException)
    }

    @Test
    fun testOnErrorWithDifferentExceptionTypes() = runTest {
        val exceptions = arrayOf(
            RuntimeException("Runtime error"),
            IllegalArgumentException("Illegal argument"),
            IllegalStateException("Illegal state"),
            UnsupportedOperationException("Unsupported operation"),
            NullPointerException("Null pointer")
        )

        for (exception in exceptions) {
            // Act
            viewModel.onError(exception)
            val errorMessage = viewModel.error.first()

            // Assert
            assertEquals("Error message should match for ${exception::class.simpleName}", 
                exception.message, errorMessage)
            verify(logger).logError("Error capturing photo", exception)
        }
    }

    @Test
    fun testErrorStateFlow() = runTest {
        // Arrange
        val testException = RuntimeException("Flow test error")

        // Act
        viewModel.onError(testException)

        // Assert
        assertNotNull("Error StateFlow should not be null", viewModel.error)
        assertTrue("Error should be StateFlow", viewModel.error is kotlinx.coroutines.flow.StateFlow)
        
        val errorValue = viewModel.error.first()
        assertEquals("Error value should match", "Flow test error", errorValue)
    }

    @Test
    fun testOnErrorWithLongMessage() = runTest {
        // Arrange
        val longMessage = "A".repeat(1000) // Very long error message
        val testException = RuntimeException(longMessage)

        // Act
        viewModel.onError(testException)
        val errorMessage = viewModel.error.first()

        // Assert
        assertEquals("Long error message should be preserved", longMessage, errorMessage)
        verify(logger).logError("Error capturing photo", testException)
    }

    @Test
    fun testOnErrorWithSpecialCharacters() = runTest {
        // Arrange
        val specialMessage = "Error with special chars: @#$%^&*()[]{}|\\:;\"'<>,.?/~`"
        val testException = RuntimeException(specialMessage)

        // Act
        viewModel.onError(testException)
        val errorMessage = viewModel.error.first()

        // Assert
        assertEquals("Special characters should be preserved", specialMessage, errorMessage)
        verify(logger).logError("Error capturing photo", testException)
    }

    @Test
    fun testOnErrorWithUnicodeMessage() = runTest {
        // Arrange
        val unicodeMessage = "Error with unicode: 🚫❌⚠️💥 エラー 错误"
        val testException = RuntimeException(unicodeMessage)

        // Act
        viewModel.onError(testException)
        val errorMessage = viewModel.error.first()

        // Assert
        assertEquals("Unicode characters should be preserved", unicodeMessage, errorMessage)
        verify(logger).logError("Error capturing photo", testException)
    }

    @Test
    fun testLoggerInteraction() {
        // Arrange
        val testException = RuntimeException("Logger interaction test")

        // Act
        viewModel.onError(testException)

        // Assert
        verify(logger, times(1)).logError("Error capturing photo", testException)
        verifyNoMoreInteractions(logger)
    }

    @Test
    fun testErrorStateFlowReference() = runTest {
        // Test that the error StateFlow maintains reference
        val errorFlow1 = viewModel.error
        val errorFlow2 = viewModel.error

        assertSame("Error StateFlow should be same reference", errorFlow1, errorFlow2)
    }

    @Test
    fun testViewModelWithCustomLogger() = runTest {
        // Arrange
        val customLogger = object : ImagePickerLogger {
            var lastMessage: String? = null
            var lastException: Throwable? = null

            override fun log(message: String) {}
            override fun logDebug(message: String) {}
            override fun logError(message: String, throwable: Throwable?) {
                lastMessage = message
                lastException = throwable
            }
        }
        
        val customViewModel = ImagePickerViewModel(customLogger)
        val testException = RuntimeException("Custom logger test")

        // Act
        customViewModel.onError(testException)
        val errorMessage = customViewModel.error.first()

        // Assert
        assertEquals("Error message should be set", "Custom logger test", errorMessage)
        assertEquals("Logger should receive correct message", "Error capturing photo", customLogger.lastMessage)
        assertEquals("Logger should receive correct exception", testException, customLogger.lastException)
    }
}
