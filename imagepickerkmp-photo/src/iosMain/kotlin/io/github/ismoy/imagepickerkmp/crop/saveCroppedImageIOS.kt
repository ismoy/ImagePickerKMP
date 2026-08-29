package io.github.ismoy.imagepickerkmp.crop

import io.github.ismoy.imagepickerkmp.camera.ImageProcessor
import io.github.ismoy.imagepickerkmp.picker.PhotoResult
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.useContents
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePNGRepresentation
import kotlin.native.runtime.GC
import kotlin.native.runtime.NativeRuntimeApi

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class, NativeRuntimeApi::class)
fun saveCroppedImageIOS(
    image: UIImage,
    originalPhotoResult: PhotoResult,
    preserveAlpha: Boolean
): PhotoResult {
    var result: PhotoResult = originalPhotoResult
    try {
        autoreleasepool {
            val imageData = if (preserveAlpha) {
                UIImagePNGRepresentation(image)
            } else {
                UIImageJPEGRepresentation(image, 0.9)
            }

            if (imageData != null) {
                val extension = if (preserveAlpha) "png" else "jpg"
                val mimeType = if (preserveAlpha) "image/png" else "image/jpeg"
                val fileName = "cropped_image_${NSDate().timeIntervalSince1970}.$extension"
                val fileURL = ImageProcessor.saveImageToTempDirectory(imageData)

                if (fileURL != null) {
                    val fileSizeInBytes = imageData.length.toLong()

                    result = PhotoResult(
                        uri = fileURL.absoluteString ?: originalPhotoResult.uri,
                        width = image.size.useContents { width.toInt() },
                        height = image.size.useContents { height.toInt() },
                        fileName = fileURL.lastPathComponent ?: fileName,
                        fileSize = fileSizeInBytes,
                        mimeType = mimeType
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
