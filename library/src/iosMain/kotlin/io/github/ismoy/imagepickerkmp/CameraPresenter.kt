package io.github.ismoy.imagepickerkmp

import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UIViewController
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler.PhotoResult

/**
 * Presents the camera interface on iOS and handles photo capture results.
 *
 * This object manages the presentation and delegation of the camera view controller.
 */
object CameraPresenter {

    private var cameraDelegate: CameraDelegate? = null

    fun presentCamera(
        viewController: UIViewController,
        onPhotoCaptured: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val imagePickerController = createImagePickerController(onPhotoCaptured, onError)
            viewController.presentViewController(imagePickerController, animated = true, completion = null)
        } catch (e: Exception) {
            onError(PhotoCaptureException("Failed to present camera: ${e.message}"))
        }
    }

    private fun createImagePickerController(
        onPhotoCaptured: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit
    ): UIImagePickerController {
        return UIImagePickerController().apply {
            sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
            allowsEditing = false

            val cleanup = { cameraDelegate = null }
            val wrappedOnPhotoCaptured: (PhotoResult) -> Unit = { result ->
                onPhotoCaptured(result)
                cleanup()
            }
            val wrappedOnError: (Exception) -> Unit = { error ->
                onError(error)
                cleanup()
            }
            cameraDelegate = CameraDelegate(wrappedOnPhotoCaptured, wrappedOnError)
            delegate = cameraDelegate
        }
    }
}
