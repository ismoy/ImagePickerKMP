package io.github.ismoy.imagepickerkmp.data.delegates

import io.github.ismoy.imagepickerkmp.data.processors.ImageProcessor
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfURL
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/**
 * Delegate for handling PHPickerViewController events and photo selection results on iOS.
 */
@OptIn(ExperimentalForeignApi::class)
class PHPickerDelegate(
    private val onPhotoSelected: (GalleryPhotoResult) -> Unit,
    private val onError: (Exception) -> Unit,
    private val onDismiss: () -> Unit,
    private val compressionLevel: CompressionLevel? = null
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
                        val processedData = if (compressionLevel != null) {
                            ImageProcessor.processImageForGallery(uiImage, compressionLevel)
                        } else {
                            UIImageJPEGRepresentation(uiImage, 1.0)
                        }
                        
                        if (processedData != null) {
                            val tempURL = ImageProcessor.saveImageToTempDirectory(processedData)
                            if (tempURL != null) {
                                val fileSizeInBytes = processedData.length.toLong()
                                val fileSizeInKB = fileSizeInBytes / 1024
                                val galleryResult = GalleryPhotoResult(
                                    uri = tempURL.absoluteString ?: "",
                                    width = uiImage.size.useContents { width.toInt() },
                                    height = uiImage.size.useContents { height.toInt() },
                                    fileName = tempURL.lastPathComponent,
                                    fileSize = fileSizeInKB
                                )
                                results.add(galleryResult)
                            } else {
                                onError(Exception("Failed to save processed image"))
                            }
                        } else {
                            onError(Exception("Failed to process image"))
                        }
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
