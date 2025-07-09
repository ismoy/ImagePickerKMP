package io.github.ismoy.imagepickerkmp

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CameraCaptureStateHolder(
    private val context: Context,
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

    fun startCamera(onError: (Exception) -> Unit) {
        cameraJob?.cancel()
        cameraJob = coroutineScope.launch {
            try {
                isLoading = true
                cameraManager.setFlashMode(flashMode)
                cameraManager.startCamera(previewView, preference)
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
                onError(e)
            }
        }
    }

    fun switchCamera(onError: (Exception) -> Unit) {
        cameraJob?.cancel()
        cameraJob = coroutineScope.launch {
            try {
                cameraManager.switchCamera()
                cameraManager.startCamera(previewView, preference)
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
            kotlinx.coroutines.delay(120)
            showFlashOverlay = false
        }
    }

    fun stopCamera() {
        cameraJob?.cancel()
        cameraManager.stopCamera()
    }
} 