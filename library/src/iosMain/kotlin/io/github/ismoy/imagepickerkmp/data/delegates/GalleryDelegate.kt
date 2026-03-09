package io.github.ismoy.imagepickerkmp.data.delegates

import io.github.ismoy.imagepickerkmp.data.processors.ImageProcessor
import io.github.ismoy.imagepickerkmp.domain.utils.ExifDataExtractor
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
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

/**
 * Delegate for handling UIImagePickerController events and photo selection results on iOS.
 *
 * This class processes selected images from the gallery and communicates results or errors to the caller.
 */
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
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        if (image != null) {
            // Filtro post-selección: verificar el MIME type real del archivo seleccionado
            val imageUrl = didFinishPickingMediaWithInfo[UIImagePickerControllerImageURL] as? platform.Foundation.NSURL
            if (imageUrl != null && !urlMatchesMimeTypes(imageUrl, allowedMimeTypes)) {
                logDebug("MIME type mismatch. Allowed: ${allowedMimeTypes.joinToString { it.value }}")
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

    // Llamado por iOS cuando el usuario hace swipe-to-dismiss (drag down) en el picker
    override fun presentationControllerDidDismiss(presentationController: UIPresentationController) {
        onDismiss()
    }

    private fun processSelectedImage(image: UIImage, picker: UIImagePickerController) {
        try {
            logDebug("Processing selected image...")
            logDebug("CompressionLevel received: $compressionLevel")
            
            val processedData = if (compressionLevel != null) {
                logDebug("Using compression with level: $compressionLevel")
                ImageProcessor.processImageForGallery(image, compressionLevel)
            } else {
                logDebug("No compression - using original quality")
                UIImageJPEGRepresentation(image, 1.0)
            }
            
            if (processedData != null) {
                val tempURL = ImageProcessor.saveImageToTempDirectory(processedData)
                if (tempURL != null) {
                    val fileSizeInBytes = processedData.length.toLong()
                    logDebug("EXIF extraction - includeExif: $includeExif, tempURL.path: ${tempURL.path}")
                    val exifData = if (includeExif) {
                        logDebug("Calling ExifDataExtractor.extractExifData for path: ${tempURL.path ?: "NULL"}")
                        val result = ExifDataExtractor.extractExifData(tempURL.path ?: "")
                        logDebug("ExifDataExtractor result: ${result != null}")
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
                    logDebug("Final result - File size: ${fileSizeInBytes} bytes (${fileSizeInBytes / 1024}KB)")
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

    /**
     * Verifica si una URL de archivo cumple con alguno de los MimeTypes permitidos,
     * igual que Android: deja seleccionar al usuario pero en el callback valida la extensión real.
     */
    private fun urlMatchesMimeTypes(url: platform.Foundation.NSURL, allowedMimeTypes: List<MimeType>): Boolean {
        // Si contiene IMAGE_ALL o wildcard, se acepta todo
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
