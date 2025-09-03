package io.github.ismoy.imagepickerkmp.presentation.viewModel

import io.github.ismoy.imagepickerkmp.domain.utils.ImagePickerLogger
import org.junit.Test
import org.junit.Assert.*

class SimpleViewModelTest {

    @Test
    fun imagePickerViewModel_shouldInitialize() {
        val mockLogger = object : ImagePickerLogger {
            override fun log(message: String) { }
            override fun logError(message: String, throwable: Throwable?) { }
            override fun logDebug(message: String) { }
        }
        val viewModel = ImagePickerViewModel(mockLogger)
        
        assertNotNull(viewModel)
        assertNotNull(viewModel.error)
    }

    @Test
    fun imagePickerViewModel_onError_shouldSetError() {
        val mockLogger = object : ImagePickerLogger {
            override fun log(message: String) { }
            override fun logError(message: String, throwable: Throwable?) { }
            override fun logDebug(message: String) { }
        }
        val viewModel = ImagePickerViewModel(mockLogger)
        val exception = Exception("Test error")
        
        viewModel.onError(exception)
        
        // Error should be set
        assertNotNull(viewModel.error.value)
        assertEquals("Test error", viewModel.error.value)
    }

    @Test
    fun imagePickerViewModel_initialState_shouldBeCorrect() {
        val mockLogger = object : ImagePickerLogger {
            override fun log(message: String) { }
            override fun logError(message: String, throwable: Throwable?) { }
            override fun logDebug(message: String) { }
        }
        val viewModel = ImagePickerViewModel(mockLogger)
        
        // Initial error should be null
        assertNull(viewModel.error.value)
    }
}
