package io.github.ismoy.imagepickerkmp.data.delegates

import io.github.ismoy.imagepickerkmp.data.processors.ImageProcessor
import io.github.ismoy.imagepickerkmp.domain.utils.ExifDataExtractor
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.Foundation.NSData
import platform.Foundation.NSUUID
import platform.Foundation.dataWithContentsOfURL
import platform.UIKit.UIImage
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerImageURL
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIAdaptivePresentationControllerDelegateProtocol
import platform.UIKit.UIPresentationController
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
internal class GalleryDelegate(
    private val onImagePicked: (GalleryPhotoResult) -> Unit,
    private val onDismiss: () -> Unit,
    private val compressionLevel: CompressionLevel? = null,
    private val includeExif: Boolean = false,
    private val allowedMimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL),
    private val onError: ((Exception) -> Unit)? = null,
    private val mimeTypeMismatchMessage: String? = null
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol, UIAdaptivePresentationControllerDelegateProtocol {

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        val imageUrl = didFinishPickingMediaWithInfo[UIImagePickerControllerImageURL] as? platform.Foundation.NSURL
        // GIF bypass: UIImage only retains the first frame, destroying animation.
        // Read raw bytes from the original file URL instead of going through UIImage.
        if (imageUrl != null) {
            val ext = imageUrl.pathExtension?.lowercase()
            logDebug("imagePickerController — url=$imageUrl | extension=$ext")
            if (ext == "gif") {
                logDebug("GIF detected — bypassing UIImage pipeline")
                if (!urlMatchesMimeTypes(imageUrl, allowedMimeTypes)) {
                    val msg = mimeTypeMismatchMessage
                        ?: "The selected file does not match the allowed types: ${allowedMimeTypes.joinToString { it.value }}"
                    onError?.invoke(Exception(msg))
                    dismissPicker(picker)
                    return
                }
                processSelectedGif(imageUrl, picker)
                return
            }
        }

        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        if (image != null) {
            if (imageUrl != null && !urlMatchesMimeTypes(imageUrl, allowedMimeTypes)) {
                val msg = mimeTypeMismatchMessage
                    ?: "The selected file does not match the allowed types: ${allowedMimeTypes.joinToString { it.value }}"
                onError?.invoke(Exception(msg))
                dismissPicker(picker)
                return
            }
            processSelectedImage(image, picker)
        } else {
            dismissPicker(picker)
        }
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        onDismiss()
        dismissPicker(picker)
    }

    override fun presentationControllerDidDismiss(presentationController: UIPresentationController) {
        onDismiss()
    }

    private fun processSelectedGif(gifUrl: platform.Foundation.NSURL, picker: UIImagePickerController) {
        try {
            val gifData = NSData.dataWithContentsOfURL(gifUrl)
            if (gifData == null) {
                dismissPicker(picker)
                return
            }
            val fileName = "${NSUUID().UUIDString}.gif"
            val tempURL = ImageProcessor.saveDataToTempDirectory(gifData, fileName)
            if (tempURL == null) {
                dismissPicker(picker)
                return
            }
            // Read dimensions from first frame only — file on disk is untouched
            val previewImage = UIImage.imageWithData(gifData)
            val width = previewImage?.size?.useContents { width.toInt() } ?: 0
            val height = previewImage?.size?.useContents { height.toInt() } ?: 0
            val galleryResult = GalleryPhotoResult(
                uri = tempURL.absoluteString ?: "",
                width = width,
                height = height,
                fileName = fileName,
                fileSize = gifData.length.toLong() / 1024,
                mimeType = MimeType.IMAGE_GIF.value,
                exif = null
            )
            onImagePicked(galleryResult)
        } catch (e: Exception) {
            onError?.invoke(e)
        } finally {
            dismissPicker(picker)
        }
    }

    private fun processSelectedImage(image: UIImage, picker: UIImagePickerController) {
        try {
            val processedData = if (compressionLevel != null) {
                ImageProcessor.processImageForGallery(image, compressionLevel)
            } else {
                logDebug("No compression - using original quality")
                UIImageJPEGRepresentation(image, 1.0)
            }
            
            if (processedData != null) {
                val tempURL = ImageProcessor.saveImageToTempDirectory(processedData)
                if (tempURL != null) {
                    val fileSizeInBytes = processedData.length.toLong()
                    val exifData = if (includeExif) {
                        val result = ExifDataExtractor.extractExifData(tempURL.path ?: "")
                        result
                    } else {
                        logDebug("EXIF extraction skipped (includeExif = false)")
                        null
                    }
                    
                    val galleryResult = GalleryPhotoResult(
                        uri = tempURL.absoluteString ?: "",
                        width = image.size.useContents { width.toInt() },
                        height = image.size.useContents { height.toInt() },
                        fileName = tempURL.lastPathComponent,
                        fileSize = fileSizeInBytes,
                        mimeType = "image/jpeg",
                        exif = exifData
                    )
                    onImagePicked(galleryResult)
                } else {
                    logDebug("Error: Failed to save image to temp directory")
                }
            } else {
                logDebug("Error: Failed to process image data")
            }
        } catch (e: Exception) {
            logDebug("Error processing image: ${e.message}")
        } finally {
            dismissPicker(picker)
        }
    }

    private fun logDebug(message: String) {
        println(" iOS GalleryDelegate: $message")
    }

    private fun dismissPicker(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, null)
    }

    private fun urlMatchesMimeTypes(url: platform.Foundation.NSURL, allowedMimeTypes: List<MimeType>): Boolean {
        if (allowedMimeTypes.any { it == MimeType.IMAGE_ALL }) return true

        val pathExtension = url.pathExtension?.lowercase() ?: return true

        val actualMimeType = extensionToMimeType(pathExtension)

        return allowedMimeTypes.any { allowed ->
            when {
                allowed.value.endsWith("/*") -> actualMimeType.startsWith(allowed.value.removeSuffix("*"))
                else -> actualMimeType.equals(allowed.value, ignoreCase = true)
            }
        }
    }

    private fun extensionToMimeType(extension: String): String {
        return when (extension) {
            "jpg", "jpeg" -> "image/jpeg"
            "png"         -> "image/png"
            "gif"         -> "image/gif"
            "webp"        -> "image/webp"
            "bmp"         -> "image/bmp"
            "heic"        -> "image/heic"
            "heif"        -> "image/heif"
            "pdf"         -> "application/pdf"
            else          -> "image/$extension"
        }
    }
}
