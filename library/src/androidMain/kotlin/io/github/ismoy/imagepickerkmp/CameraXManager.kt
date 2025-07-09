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
            onImageCaptured = {imageFile->
                imageProcessor.processImage(imageFile,onPhotoResult,onError)
            },
            onError = onError
        )
    }
    fun stopCamera(){
        cameraController.stopCamera()
    }
}