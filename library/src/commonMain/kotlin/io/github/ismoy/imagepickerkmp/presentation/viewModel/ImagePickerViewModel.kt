
package io.github.ismoy.imagepickerkmp.presentation.viewModel

import io.github.ismoy.imagepickerkmp.domain.utils.ImagePickerLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for ImagePicker operations.
 * 
 * SOLID: Single Responsibility - Only handles image picker state management
 * SOLID: Dependency Inversion - Dependencies injected via constructor
 */
class ImagePickerViewModel(
    private val logger: ImagePickerLogger
) {
    private val _isLoading = MutableStateFlow(false)

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun onError(exception: Exception) {
        logger.logError("Error capturing photo", exception)
        _error.value = exception.message
        _isLoading.value = false
    }
    
}
