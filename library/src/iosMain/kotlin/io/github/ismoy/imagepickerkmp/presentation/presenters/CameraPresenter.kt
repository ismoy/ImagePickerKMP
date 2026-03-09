package io.github.ismoy.imagepickerkmp.presentation.presenters

import io.github.ismoy.imagepickerkmp.data.delegates.CameraDelegate
import io.github.ismoy.imagepickerkmp.domain.exceptions.PhotoCaptureException
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UIModalPresentationFullScreen
import platform.UIKit.UIViewController

internal object CameraPresenter {

    private var cameraDelegate: CameraDelegate? = null

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
                
                println(" $errorMessage")
                
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
            val errorMessage = "Failed to present camera: ${e.message}"
            println("❌ Camera presentation error: $errorMessage")
            onError(PhotoCaptureException(errorMessage))
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
        return UIImagePickerController().apply {
            sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
            allowsEditing = false
            modalPresentationStyle = UIModalPresentationFullScreen

            val cleanup = { cameraDelegate = null }
            val wrappedOnPhotoCaptured: (PhotoResult) -> Unit = { result ->
                onPhotoCaptured(result)
                cleanup()
            }
            val wrappedOnError: (Exception) -> Unit = { error ->
                onError(error)
                cleanup()
            }
            val wrappedOnDismiss: () -> Unit = {
                onDismiss()
                cleanup()
            }
            cameraDelegate = CameraDelegate(
                wrappedOnPhotoCaptured,
                wrappedOnError,
                wrappedOnDismiss,
                compressionLevel,
                includeExif
            )
            delegate = cameraDelegate
        }
    }
}
