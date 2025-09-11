package io.github.ismoy.imagepickerkmp.domain.utils

import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.Foundation.NSDate
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.stringByAppendingPathComponent
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.writeToURL
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

@OptIn(ExperimentalForeignApi::class)
 fun saveCroppedImageIOS(
    image: UIImage,
    originalPhotoResult: PhotoResult,
    onComplete: (PhotoResult) -> Unit
) {
    try {
        val documentsPath = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        )[0] as NSString

        val fileName = "cropped_image_${NSDate().timeIntervalSince1970}.jpg"
        val filePath = documentsPath.stringByAppendingPathComponent(fileName)
        val fileURL = NSURL.fileURLWithPath(filePath)

        val imageData = UIImageJPEGRepresentation(image, 0.9)

        if (imageData != null) {
            imageData.writeToURL(fileURL, true)

            val croppedPhotoResult = PhotoResult(
                uri = fileURL.absoluteString ?: originalPhotoResult.uri,
                width = image.size.useContents { width.toInt() },
                height = image.size.useContents { height.toInt() }
            )

            onComplete(croppedPhotoResult)
        } else {
            onComplete(originalPhotoResult)
        }
    } catch (_: Exception) {
        onComplete(originalPhotoResult)
    }
}