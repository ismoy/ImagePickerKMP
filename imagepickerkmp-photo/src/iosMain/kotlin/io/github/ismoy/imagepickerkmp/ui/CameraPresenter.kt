package io.github.ismoy.imagepickerkmp.ui

import io.github.ismoy.imagepickerkmp.camera.CameraDelegate
import io.github.ismoy.imagepickerkmp.picker.PhotoCaptureException
import io.github.ismoy.imagepickerkmp.picker.PhotoResult
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.logger.PhotoLogger
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UIModalPresentationFullScreen
import platform.UIKit.UIViewController

internal object CameraPresenter {

    private val activeDelegates = mutableMapOf<Int, CameraDelegate>()

    fun presentCamera(
        viewController: UIViewController,
        onPhotoCaptured: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        onDismiss: () -> Unit,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false
    ) {
        try {
            if (!UIImagePickerController.isSourceTypeAvailable(
                UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
            )) {
                val errorMessage = " Camera is not available on this device. " +
                    "The iOS Simulator does not support camera functionality. " +
                    "Please test camera features on a physical iOS device."
                
                PhotoLogger.debug("Camera not available on this device")

                onError(PhotoCaptureException(errorMessage))
                onDismiss()
                return
            }
            
            val imagePickerController = createImagePickerController(
                onPhotoCaptured,
                onError,
                onDismiss,
                compressionLevel,
                includeExif
            )
            viewController.presentViewController(imagePickerController, animated = true, completion = null)
        } catch (e: Exception) {
            PhotoLogger.debug("Camera presentation error: ${e::class.simpleName}")
            onError(PhotoCaptureException("Failed to present camera: ${e.message}"))
            onDismiss()
        }
    }

    private fun createImagePickerController(
        onPhotoCaptured: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        onDismiss: () -> Unit,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false
    ): UIImagePickerController {
        val picker = UIImagePickerController().apply {
            sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
            allowsEditing = false
            modalPresentationStyle = UIModalPresentationFullScreen
        }
        val pickerKey = picker.hashCode()

        val cleanup = { activeDelegates.remove(pickerKey) }
        val delegate = CameraDelegate(
            onPhotoCaptured = { result -> onPhotoCaptured(result); cleanup() },
            onError        = { error  -> onError(error);  cleanup() },
            onDismiss      = { onDismiss(); cleanup() },
            compressionLevel = compressionLevel,
            includeExif = includeExif
        )
        activeDelegates[pickerKey] = delegate
        picker.delegate = delegate
        return picker
    }
}
