package io.github.ismoy.imagepickerkmp.domain.utils

import io.github.ismoy.imagepickerkmp.data.processors.ImageProcessor
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

@OptIn(ExperimentalForeignApi::class)
fun saveCroppedImageIOS(
    image: UIImage,
    originalPhotoResult: PhotoResult,
    onComplete: (PhotoResult) -> Unit
) {
    try {
        val imageData = UIImageJPEGRepresentation(image, 0.9)

        if (imageData != null) {
            val fileName = "cropped_image_${NSDate().timeIntervalSince1970}.jpg"
            val fileURL = ImageProcessor.saveImageToTempDirectory(imageData)

            if (fileURL != null) {
                val fileSizeInBytes = imageData.length.toLong()

                val croppedPhotoResult = PhotoResult(
                    uri = fileURL.absoluteString ?: originalPhotoResult.uri,
                    width = image.size.useContents { width.toInt() },
                    height = image.size.useContents { height.toInt() },
                    fileName = fileURL.lastPathComponent ?: fileName,
                    fileSize = fileSizeInBytes,
                    mimeType = "image/jpeg"
                )
                onComplete(croppedPhotoResult)
            } else {
                onComplete(originalPhotoResult)
            }
        } else {
            onComplete(originalPhotoResult)
        }
    } catch (_: Exception) {
        onComplete(originalPhotoResult)
    }
}