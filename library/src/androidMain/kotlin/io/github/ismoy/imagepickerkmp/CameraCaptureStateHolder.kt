package io.github.ismoy.imagepickerkmp

import androidx.camera.view.PreviewView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.ismoy.imagepickerkmp.ImagePickerUiConstants.DELAY_TO_TAKE_PHOTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Holds and manages the state for camera capture operations, including flash mode, loading state,
 * and camera switching.
 *
 * This class coordinates camera actions and state updates for the UI.
 */
class CameraCaptureStateHolder(
    private val cameraManager: CameraXManager,
    private val previewView: PreviewView,
    private val preference: CapturePhotoPreference,
    private val coroutineScope: CoroutineScope
) {
    var flashMode by mutableStateOf(CameraController.FlashMode.AUTO)
        private set
    var isLoading by mutableStateOf(true)
        private set
    var showFlashOverlay by mutableStateOf(false)
        private set
    val flashModes: List<CameraController.FlashMode> = cameraManager.flashModes
    private var cameraJob: Job? = null

    fun startCamera(
        onError: (Exception) -> Unit,
        onCameraReady: (() -> Unit)? = null,
        onPermissionError: ((Exception) -> Unit)? = null
    ) {
        cameraJob?.cancel()
        cameraJob = coroutineScope.launch {
            try {
                isLoading = true
                cameraManager.setFlashMode(flashMode)
                cameraManager.startCamera(previewView, preference)
                isLoading = false
                onCameraReady?.invoke()
            } catch (e: Exception) {
                isLoading = false
                if (e.message?.contains("permission", ignoreCase = true) == true ||
                    e.message?.contains("camera", ignoreCase = true) == true) {
                    onPermissionError?.invoke(e)
                } else {
                    onError(e)
                }
            }
        }
    }

    fun switchCamera(
        onError: (Exception) -> Unit,
        onCameraSwitch: (() -> Unit)? = null
    ) {
        cameraJob?.cancel()
        cameraJob = coroutineScope.launch {
            try {
                cameraManager.switchCamera()
                cameraManager.startCamera(previewView, preference)
                onCameraSwitch?.invoke()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun toggleFlash() {
        val idx = flashModes.indexOf(flashMode)
        val newMode = flashModes[(idx + 1) % flashModes.size]
        flashMode = newMode
        cameraManager.setFlashMode(newMode)
    }

    fun capturePhoto(
        onPhotoResult: (CameraPhotoHandler.PhotoResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        showFlashOverlay = true
        cameraManager.takePicture(onPhotoResult, onError)
        coroutineScope.launch {
            delay(DELAY_TO_TAKE_PHOTO)
            showFlashOverlay = false
        }
    }
}
