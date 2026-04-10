package io.github.ismoy.imagepickerkmp.data.camera

import android.content.Context
import android.os.Build
import android.util.Size
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import io.github.ismoy.imagepickerkmp.data.dataSource.getCaptureMode as getCaptureModeFn
import io.github.ismoy.imagepickerkmp.data.managers.FileManager
import io.github.ismoy.imagepickerkmp.data.models.CameraType
import io.github.ismoy.imagepickerkmp.data.models.FlashMode
import io.github.ismoy.imagepickerkmp.domain.exceptions.PhotoCaptureException
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.domain.config.HighPerformanceConfig
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.BOUND_SIZE_HEIGHT
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.BOUND_SIZE_WIDTH
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.DELAY_TO_TAKE_PHOTO
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ERROR_BIND_CAMERA_MESSAGE
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ERROR_CAMERA_NOT_INITIALIZED
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ERROR_TO_SWITCH_CAMERA_MESSAGE

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File


internal class CameraController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val fileManager: FileManager
) {
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var currentFlashMode: FlashMode = FlashMode.AUTO
    private var currentCameraType: CameraType = CameraType.BACK

    private val sharedResolutionSelector: ResolutionSelector by lazy {
        ResolutionSelector.Builder()
            .setAspectRatioStrategy(
                AspectRatioStrategy(
                    AspectRatio.RATIO_4_3,
                    AspectRatioStrategy.FALLBACK_RULE_AUTO
                )
            )
            .setResolutionStrategy(
                ResolutionStrategy(
                    Size(BOUND_SIZE_WIDTH, BOUND_SIZE_HEIGHT),
                    ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                )
            )
            .build()
    }

    suspend fun startCamera(
        previewView: PreviewView,
        preference: CapturePhotoPreference
    ) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            delay(DELAY_TO_TAKE_PHOTO)
        }
   
        cameraProvider = withContext(Dispatchers.Main) {
            ProcessCameraProvider.awaitInstance(context)
        }

        withContext(Dispatchers.Main) {
            if (HighPerformanceConfig.requiresCompatibilityMode()) {
                previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }

            val preview = Preview.Builder()
                .setResolutionSelector(sharedResolutionSelector)
                .build().also { it.surfaceProvider = previewView.surfaceProvider }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(getCaptureModeFn(preference))
                .setFlashMode(getImageCaptureFlashMode(currentFlashMode))
                .setJpegQuality(HighPerformanceConfig.getOptimalJpegQuality(context))
                .setResolutionSelector(sharedResolutionSelector)
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
                throw PhotoCaptureException("$ERROR_BIND_CAMERA_MESSAGE ${exc.message}")
            }
        }
    }

    fun switchCamera() {
        currentCameraType = when (currentCameraType) {
            CameraType.BACK -> CameraType.FRONT
            CameraType.FRONT -> CameraType.BACK
        }
    }

    suspend fun switchCameraWarm(
        previewView: PreviewView,
        preference: CapturePhotoPreference
    ) {
        switchCamera()
        val provider = cameraProvider
        if (provider == null) {
            startCamera(previewView, preference)
            return
        }
        withContext(Dispatchers.Main) {
            try {
                val preview = Preview.Builder()
                    .setResolutionSelector(sharedResolutionSelector)
                    .build()
                    .also { it.surfaceProvider = previewView.surfaceProvider }

                val newImageCapture = ImageCapture.Builder()
                    .setCaptureMode(getCaptureModeFn(preference))
                    .setFlashMode(getImageCaptureFlashMode(currentFlashMode))
                    .setJpegQuality(HighPerformanceConfig.getOptimalJpegQuality(context))
                    .setResolutionSelector(sharedResolutionSelector)
                    .build()

                val cameraSelector = when (currentCameraType) {
                    CameraType.BACK -> CameraSelector.DEFAULT_BACK_CAMERA
                    CameraType.FRONT -> CameraSelector.DEFAULT_FRONT_CAMERA
                }

                provider.unbindAll()
                provider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, newImageCapture)
                imageCapture = newImageCapture
            } catch (exc: Exception) {
                throw PhotoCaptureException("$ERROR_TO_SWITCH_CAMERA_MESSAGE${exc.message}")
            }
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
            ?: return onError(PhotoCaptureException(ERROR_CAMERA_NOT_INITIALIZED))

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
        cameraProvider = null
        imageCapture = null
    }
}
