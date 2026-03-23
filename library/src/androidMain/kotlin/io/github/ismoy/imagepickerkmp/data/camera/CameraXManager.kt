package io.github.ismoy.imagepickerkmp.data.camera

import androidx.camera.view.PreviewView
import io.github.ismoy.imagepickerkmp.data.models.FlashMode
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


internal class CameraXManager(
    private val cameraController: CameraController,
    private val imageProcessor: io.github.ismoy.imagepickerkmp.data.processors.ImageProcessor
) {

    suspend fun startCamera(
        previewView: PreviewView,
        preference: CapturePhotoPreference
    ) {
        cameraController.startCamera(previewView, preference)
    }

    fun takePicture(
        callerScope: CoroutineScope,
        onPhotoResult: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false,
        redactGpsData: Boolean = true
    ) {
        cameraController.takePicture(
            onImageCaptured = { imageFile, cameraType ->
                callerScope.launch {
                    try {
                        val result = imageProcessor.processImage(
                            imageFile = imageFile,
                            cameraType = cameraType,
                            compressionLevel = compressionLevel,
                            includeExif = includeExif,
                            redactGpsData = redactGpsData
                        )
                        withContext(Dispatchers.Main) { onPhotoResult(result) }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) { onError(e) }
                    }
                }
            },
            onError = onError
        )
    }

    fun stopCamera() {
        cameraController.stopCamera()
    }

    suspend fun switchCameraWarm(
        previewView: PreviewView,
        preference: CapturePhotoPreference
    ) {
        cameraController.switchCameraWarm(previewView, preference)
    }
    fun setFlashMode(mode: FlashMode) {
        cameraController.setFlashMode(mode)
    }
    val flashModes: List<FlashMode>
        get() = FlashMode.entries
}

