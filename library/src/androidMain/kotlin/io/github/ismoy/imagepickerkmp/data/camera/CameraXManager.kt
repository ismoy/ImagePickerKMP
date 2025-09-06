package io.github.ismoy.imagepickerkmp.data.camera

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.camera.view.PreviewView
import io.github.ismoy.imagepickerkmp.data.processors.ImageProcessor
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult

/**
 * High-level manager for camera operations, providing an interface to start, stop, and control the camera.
 * 
 * SOLID: Dependency Inversion - Depends on injected dependencies instead of creating them
 * SOLID: Single Responsibility - Only manages camera operations coordination
 */
class CameraXManager(
    private val cameraController: CameraController,
    private val imageProcessor: ImageProcessor
) {
    
    // Secondary constructor for backward compatibility
    constructor(context: Context, activity: ComponentActivity) : this(
        cameraController = CameraController(context, activity),
        imageProcessor = ImageProcessor(
            fileManager = io.github.ismoy.imagepickerkmp.data.managers.FileManager(context),
            orientationCorrector = io.github.ismoy.imagepickerkmp.data.processors.ImageOrientationCorrector()
        )
    )

    suspend fun startCamera(
        previewView: PreviewView,
        preference: CapturePhotoPreference
    ){
        cameraController.startCamera(previewView,preference)
    }
    fun takePicture(
        onPhotoResult: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        compressionLevel: CompressionLevel? = null
    ){
        cameraController.takePicture(
            onImageCaptured = { imageFile, cameraType ->
                imageProcessor.processImage(
                    imageFile = imageFile,
                    cameraType = cameraType,
                    compressionLevel = compressionLevel,
                    onPhotoCaptured = onPhotoResult,
                    onError = onError
                )
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
        get() = CameraController.FlashMode.entries
}
