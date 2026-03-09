package io.github.ismoy.imagepickerkmp.data.delegates

import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import kotlinx.cinterop.ExperimentalForeignApi
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIPresentationController
import platform.UIKit.UIAdaptivePresentationControllerDelegateProtocol
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue


@OptIn(ExperimentalForeignApi::class)
internal class PHPickerDelegate(
    private val onPhotoSelected: (GalleryPhotoResult) -> Unit,
    private val onPhotosSelected: ((List<GalleryPhotoResult>) -> Unit)? = null,
    private val onError: (Exception) -> Unit,
    private val onDismiss: () -> Unit,
    private val compressionLevel: CompressionLevel? = null,
    private val includeExif: Boolean = false,
    private val allowedMimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL),
    private val mimeTypeMismatchMessage: String? = null
) : NSObject(), PHPickerViewControllerDelegateProtocol, UIAdaptivePresentationControllerDelegateProtocol {
    
    var dismissHandled = false
        internal set

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun picker(
        picker: PHPickerViewController,
        didFinishPicking: List<*>
    ) {
        dismissHandled = true
        
        if (didFinishPicking.isEmpty()) {
            onDismiss()
            dismissPicker(picker)
            return
        }

        val pickerResults = didFinishPicking.mapNotNull { it as? PHPickerResult }
        if (pickerResults.isEmpty()) {
            onError(Exception("Invalid picker results"))
            dismissPicker(picker)
            return
        }

        val processingQueue = ImageProcessingQueue(
            pickerResults = pickerResults,
            compressionLevel = compressionLevel,
            includeExif = includeExif,
            allowedMimeTypes = allowedMimeTypes,
            onComplete = { results, mismatchedCount ->
                if (results.isEmpty() && mismatchedCount > 0) {
                    // Todas las imágenes seleccionadas tenían MIME type incorrecto
                    val msg = mimeTypeMismatchMessage
                        ?: "The selected file(s) do not match the allowed types: ${allowedMimeTypes.joinToString { it.value }}"
                    onError(Exception(msg))
                } else {
                    handleProcessingComplete(results)
                }
                dismissPicker(picker)
            },
            onError = { error ->
                onError(error)
            }
        )
        
        processingQueue.start()
    }

    private fun handleProcessingComplete(results: List<GalleryPhotoResult>) {
        if (onPhotosSelected != null) {
            onPhotosSelected.invoke(results)
        } else {
            results.forEach(onPhotoSelected)
        }
    }

    private fun dismissPicker(picker: PHPickerViewController) {
        dispatch_async(dispatch_get_main_queue()) {
            picker.dismissViewControllerAnimated(true, completion = null)
        }
    }
    
    fun onPickerDismissed() {
        if (!dismissHandled) {
            dismissHandled = true
            onDismiss()
        }
    }

    // Llamado por iOS cuando el usuario hace swipe-to-dismiss (drag down) en el picker
    override fun presentationControllerDidDismiss(presentationController: UIPresentationController) {
        if (!dismissHandled) {
            dismissHandled = true
            onDismiss()
        }
    }
}