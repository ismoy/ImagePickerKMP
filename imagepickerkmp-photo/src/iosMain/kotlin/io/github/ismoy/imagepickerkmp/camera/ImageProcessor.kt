package io.github.ismoy.imagepickerkmp.camera

import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.Foundation.temporaryDirectory
import platform.Foundation.writeToURL
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal object ImageProcessor {

    fun processImage(image: UIImage, compressionLevel: CompressionLevel): NSData? {
        return try {
            var result: NSData? = null
            autoreleasepool {
                val quality = compressionLevel.toQualityValue()
                val maxDimension = compressionLevel.toMaxDimension().toDouble()
                val processedImage = resizeImageIfNeeded(image, maxDimension)
                result = UIImageJPEGRepresentation(processedImage, quality)
            }
            result
        } catch (e: Exception) {
            println("❌ iOS Camera ImageProcessor error: ${e.message}")
            null
        }
    }

    fun processImageForGallery(image: UIImage, compressionLevel: CompressionLevel): NSData? {
        return try {
            var result: NSData? = null
            autoreleasepool {
                val quality = compressionLevel.toQualityValue()
                val maxDimension = compressionLevel.toMaxDimension().toDouble()
                val processedImage = resizeImageIfNeeded(image, maxDimension)
                result = UIImageJPEGRepresentation(processedImage, quality)
            }
            result
        } catch (e: Exception) {
            println("❌ iOS ImageProcessor error: ${e.message}")
            null
        }
    }

    private fun resizeImageIfNeeded(image: UIImage, maxSize: Double): UIImage {
        return image.size.useContents { 
            if (width > maxSize || height > maxSize) {
                val aspectRatio = width / height
                val newSizeValue = if (width > height) {
                    CGSizeMake(maxSize, maxSize / aspectRatio)
                } else {
                    CGSizeMake(maxSize * aspectRatio, maxSize)
                }
                return@useContents resizeImage(image, newSizeValue)
            } else {
                return@useContents image
            }
        }
    }

    private fun resizeImage(image: UIImage, newSize: kotlinx.cinterop.CValue<platform.CoreGraphics.CGSize>): UIImage {
        var resizedImage: UIImage? = null
        autoreleasepool {
            UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
            newSize.useContents {
                image.drawInRect(CGRectMake(0.0, 0.0, width, height))
            }
            resizedImage = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()
        }
        return resizedImage ?: image
    }

    fun saveImageToTempDirectory(imageData: NSData): NSURL? {
        return try {
            val tempDir = NSFileManager.defaultManager.temporaryDirectory
            val fileName = "${NSUUID().UUIDString}.jpg"
            val fileURL = tempDir.URLByAppendingPathComponent(fileName)
            
            fileURL?.let { url ->
                if (imageData.writeToURL(url, true)) {
                    url
                } else {
                    null
                }
            }
        } catch (_: Exception) {
            null
        }
    }

    fun saveDataToTempDirectory(data: NSData, fileName: String): NSURL? {
        return try {
            val tempDir = NSFileManager.defaultManager.temporaryDirectory
            val fileURL = tempDir.URLByAppendingPathComponent(fileName)
            
            fileURL?.let { url ->
                if (data.writeToURL(url, true)) {
                    url
                } else {
                    null
                }
            }
        } catch (_: Exception) {
            null
        }
    }
}
