package io.github.ismoy.imagepickerkmp

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.camera.view.PreviewView

class CameraXManager (
    context: Context,
    activity: ComponentActivity
){
    private val cameraController = CameraController(context,activity)
    private val imageProcessor = ImageProcessor(context)

    suspend fun startCamera(
        previewView: PreviewView,
        preference: CapturePhotoPreference
    ){
        cameraController.startCamera(previewView,preference)
    }
    fun takePicture(
        onPhotoResult: (CameraPhotoHandler.PhotoResult) -> Unit,
        onError: (Exception) -> Unit
    ){
        cameraController.takePicture(
            onImageCaptured = { imageFile, cameraType ->
                imageProcessor.processImage(imageFile, cameraType, onPhotoResult, onError)
            },
            onError = onError
        )
    }
    fun stopCamera(){
        cameraController.stopCamera()
    }
    fun setFlashMode(mode: CameraController.FlashMode) {
        cameraController.setFlashMode(mode)
    }
    fun switchCamera() {
        cameraController.switchCamera()
    }

    val flashModes: List<CameraController.FlashMode>
        get() = CameraController.FlashMode.values().toList()
}