package io.github.ismoy.imagepickerkmp.data.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import io.github.ismoy.imagepickerkmp.data.dataSource.getCaptureMode as getCaptureModeFn
import io.github.ismoy.imagepickerkmp.data.managers.FileManager
import io.github.ismoy.imagepickerkmp.domain.exceptions.PhotoCaptureException
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.domain.config.HighPerformanceConfig

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Controls camera operations such as starting, stopping, capturing photos, and switching cameras.
 *
 * This class manages the camera lifecycle and configuration for photo capture.
 * 
 * SOLID: Dependency Inversion - Dependencies injected via constructor
 */
class CameraController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val fileManager: FileManager
) {
    /**
     * Enum representing the available flash modes for the camera.
     */
    enum class FlashMode {
        AUTO, ON, OFF
    }
    /**
     * Enum representing the available camera types (front or back).
     */
    enum class CameraType {
        BACK, FRONT
    }
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var currentFlashMode: FlashMode = FlashMode.AUTO
    private var currentCameraType: CameraType = CameraType.BACK

    suspend fun startCamera(
        previewView: PreviewView,
        preference: CapturePhotoPreference
    ) {
        cameraProvider = withContext(Dispatchers.IO) {
            ProcessCameraProvider.getInstance(context).get()
        }

        withContext(Dispatchers.Main) {
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(getCaptureModeFn(preference))
                .setFlashMode(getImageCaptureFlashMode(currentFlashMode))
                .setJpegQuality(HighPerformanceConfig.getOptimalJpegQuality()) // Dynamic quality based on device
                .build()

            val cameraSelector = when (currentCameraType) {
                CameraType.BACK -> CameraSelector.DEFAULT_BACK_CAMERA
                CameraType.FRONT -> CameraSelector.DEFAULT_FRONT_CAMERA
            }

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                throw PhotoCaptureException("Failed to bind camera use cases: ${exc.message}")
            }
        }
    }

    fun switchCamera() {
        currentCameraType = when (currentCameraType) {
            CameraType.BACK -> CameraType.FRONT
            CameraType.FRONT -> CameraType.BACK
        }
    }

    fun setFlashMode(mode: FlashMode) {
        currentFlashMode = mode
        imageCapture?.flashMode = getImageCaptureFlashMode(mode)
    }

    private fun getImageCaptureFlashMode(mode: FlashMode): Int {
        return when (mode) {
            FlashMode.AUTO -> ImageCapture.FLASH_MODE_AUTO
            FlashMode.ON -> ImageCapture.FLASH_MODE_ON
            FlashMode.OFF -> ImageCapture.FLASH_MODE_OFF
        }
    }

    fun takePicture(
        onImageCaptured: (File, CameraType) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val imageCapture = this.imageCapture
            ?: return onError(PhotoCaptureException("Camera not initialized."))

        val photoFile = fileManager.createImageFile()
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    onImageCaptured(photoFile, currentCameraType)
                }

                override fun onError(exc: ImageCaptureException) {
                    onError(exc)
                }
            }
        )
    }

    fun stopCamera() {
        cameraProvider?.unbindAll()
    }
}
