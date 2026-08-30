package io.github.ismoy.imagepickerkmp.scanner.camera

import io.github.ismoy.imagepickerkmp.scanner.domain.model.FlashMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class ScannerCameraState {
    data object CameraReady : ScannerCameraState()
    data object StartingCamera : ScannerCameraState()
    data object Scanning : ScannerCameraState()
    data object Paused : ScannerCameraState()
    data class CodeDetected(val code: String, val format: String? = null) : ScannerCameraState()
    data class Error(val message: String) : ScannerCameraState()
}

class DefaultScannerCameraStateManager {
    private val _currentState = MutableStateFlow<ScannerCameraState>(ScannerCameraState.CameraReady)
    val currentState: StateFlow<ScannerCameraState> = _currentState.asStateFlow()

    private val _flashMode = MutableStateFlow(FlashMode.OFF)
    val flashMode: StateFlow<FlashMode> = _flashMode.asStateFlow()

    fun updateState(newState: ScannerCameraState) {
        _currentState.value = newState
    }

    fun setFlashMode(mode: String) {
        _flashMode.value = if (mode == "ON") FlashMode.ON else FlashMode.OFF
    }
}
