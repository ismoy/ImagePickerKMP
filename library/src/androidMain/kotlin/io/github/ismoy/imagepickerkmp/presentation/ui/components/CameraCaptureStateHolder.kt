package io.github.ismoy.imagepickerkmp.presentation.ui.components

import android.os.Build
import android.util.Log
import androidx.camera.view.PreviewView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.ismoy.imagepickerkmp.data.camera.CameraController
import io.github.ismoy.imagepickerkmp.data.camera.CameraXManager
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.DELAY_TO_TAKE_PHOTO
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        onPhotoResult: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false
    ) {
        showFlashOverlay = true
        cameraManager.takePicture(onPhotoResult, onError, compressionLevel, includeExif)
        coroutineScope.launch {
            delay(DELAY_TO_TAKE_PHOTO)
            showFlashOverlay = false
        }
    }
}
