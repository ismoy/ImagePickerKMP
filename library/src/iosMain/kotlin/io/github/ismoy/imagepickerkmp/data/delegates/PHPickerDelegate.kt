package io.github.ismoy.imagepickerkmp.data.delegates

import io.github.ismoy.imagepickerkmp.data.processors.ImageProcessor
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfURL
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIImage
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/**
 * Delegate for handling PHPickerViewController events and photo selection results on iOS.
 */
class PHPickerDelegate(
    private val onPhotoSelected: (GalleryPhotoResult) -> Unit,
    private val onError: (Exception) -> Unit,
    private val onDismiss: () -> Unit
) : NSObject(), PHPickerViewControllerDelegateProtocol {

    override fun picker(
        picker: PHPickerViewController,
        didFinishPicking: List<*>
    ) {
        if (didFinishPicking.isEmpty()) {
            onDismiss()
            dismissPicker(picker)
            return
        }

        val totalCount = didFinishPicking.size
        var processedCount = 0
        val results = mutableListOf<GalleryPhotoResult>()

        fun checkAndFinish() {
            if (processedCount >= totalCount) {
                results.forEach(onPhotoSelected)
                dismissPicker(picker)
            }
        }

        for (result in didFinishPicking) {
            val pickerResult = result as? PHPickerResult
            if (pickerResult == null) {
                processedCount++
                onError(Exception("Invalid picker result"))
                checkAndFinish()
                continue
            }

            pickerResult.itemProvider.loadFileRepresentationForTypeIdentifier(
                "public.image"
            ) { url, error ->
                processedCount++
                if (error != null || url == null) {
                    onError(Exception("Failed to load image"))
                    checkAndFinish()
                    return@loadFileRepresentationForTypeIdentifier
                }

                try {
                    val imageData = NSData.dataWithContentsOfURL(url)
                    val uiImage = imageData?.let { UIImage.imageWithData(it) }

                    if (uiImage != null) {
                        val photoResult = ImageProcessor.processImageForGallery(uiImage)
                        results.add(photoResult)
                    } else {
                        onError(Exception("Failed to create UIImage"))
                    }
                } catch (e: Exception) {
                    onError(Exception("Error processing image: ${e.message}"))
                }

                checkAndFinish()
            }
        }
    }

    private fun dismissPicker(picker: PHPickerViewController) {
        dispatch_async(dispatch_get_main_queue()) {
            picker.dismissViewControllerAnimated(true, completion = null)
        }
    }
}
