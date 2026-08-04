package io.github.ismoy.imagepickerkmp.crop

import io.github.ismoy.imagepickerkmp.camera.ImageProcessor
import io.github.ismoy.imagepickerkmp.picker.PhotoResult
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.useContents
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import kotlin.native.runtime.GC
import kotlin.native.runtime.NativeRuntimeApi

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class, NativeRuntimeApi::class)
fun saveCroppedImageIOS(
    image: UIImage,
    originalPhotoResult: PhotoResult
): PhotoResult {
    var result: PhotoResult = originalPhotoResult
    try {
        autoreleasepool {
            UIGraphicsBeginImageContextWithOptions(image.size, true, 1.0)
            val rect = image.size.useContents { platform.CoreGraphics.CGRectMake(0.0, 0.0, this.width, this.height) }
            image.drawInRect(rect)
            val opaqueImage = UIGraphicsGetImageFromCurrentImageContext() ?: image
            UIGraphicsEndImageContext()

            val imageData = UIImageJPEGRepresentation(opaqueImage, 0.9)

            if (imageData != null) {
                val fileName = "cropped_image_${NSDate().timeIntervalSince1970}.jpg"
                val fileURL = ImageProcessor.saveImageToTempDirectory(imageData)

                if (fileURL != null) {
                    val fileSizeInBytes = imageData.length.toLong()

                    result = PhotoResult(
                        uri = fileURL.absoluteString ?: originalPhotoResult.uri,
                        width = image.size.useContents { width.toInt() },
                        height = image.size.useContents { height.toInt() },
                        fileName = fileURL.lastPathComponent ?: fileName,
                        fileSize = fileSizeInBytes,
                        mimeType = "image/jpeg"
                    )
                }
            }
        }
    } catch (_: Exception) {
    } finally {
        GC.collect()
    }
    return result
}