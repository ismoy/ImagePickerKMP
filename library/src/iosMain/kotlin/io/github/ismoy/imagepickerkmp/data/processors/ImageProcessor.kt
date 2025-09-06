package io.github.ismoy.imagepickerkmp.data.processors

import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSNumber
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.Foundation.temporaryDirectory
import platform.Foundation.writeToURL
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.drawInRect

/**
 * Handles image processing operations for iOS, including compression and conversion.
 */
@OptIn(ExperimentalForeignApi::class)
object ImageProcessor {

    /**
     * Process an image with compression for camera capture
     */
    fun processImage(image: UIImage, compressionLevel: CompressionLevel): NSData? {
        return try {
            val quality = compressionLevel.toQualityValue()
            val maxDimension = when (compressionLevel) {
                CompressionLevel.HIGH -> 1280.0   // Más compresión = imagen más pequeña
                CompressionLevel.MEDIUM -> 1920.0 // Compresión media
                CompressionLevel.LOW -> 2560.0    // Menos compresión = imagen más grande
            }
            
            // Debug logs
            println("📸 iOS Camera ImageProcessor DEBUG:")
            println("   CompressionLevel: $compressionLevel")
            println("   Quality: $quality")
            println("   MaxDimension: $maxDimension")
            println("   Original size: ${image.size.useContents { "$width x $height" }}")
            
            val processedImage = resizeImageIfNeeded(image, maxDimension)
            val result = UIImageJPEGRepresentation(processedImage, quality)
            
            println("   Final data size: ${result?.length ?: 0} bytes")
            
            result
        } catch (e: Exception) {
            println("❌ iOS Camera ImageProcessor error: ${e.message}")
            null
        }
    }    /**
     * Process an image with compression for gallery selection
     */
    fun processImageForGallery(image: UIImage, compressionLevel: CompressionLevel): NSData? {
        return try {
            val quality = compressionLevel.toQualityValue()
            val maxDimension = when (compressionLevel) {
                CompressionLevel.HIGH -> 1280.0   // Más compresión = imagen más pequeña
                CompressionLevel.MEDIUM -> 1920.0 // Compresión media
                CompressionLevel.LOW -> 2560.0    // Menos compresión = imagen más grande
            }
            
            // Debug logs
            println("🔧 iOS ImageProcessor DEBUG:")
            println("   CompressionLevel: $compressionLevel")
            println("   Quality: $quality")
            println("   MaxDimension: $maxDimension")
            println("   Original size: ${image.size.useContents { "$width x $height" }}")
            
            val processedImage = resizeImageIfNeeded(image, maxDimension)
            val result = UIImageJPEGRepresentation(processedImage, quality)
            
            println("   Final data size: ${result?.length ?: 0} bytes")
            
            result
        } catch (e: Exception) {
            println("❌ iOS ImageProcessor error: ${e.message}")
            null
        }
    }

    /**
     * Resize image if it exceeds the maximum size while maintaining aspect ratio
     */
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

    /**
     * Resize image to specific size
     */
    private fun resizeImage(image: UIImage, newSize: kotlinx.cinterop.CValue<platform.CoreGraphics.CGSize>): UIImage {
        UIGraphicsBeginImageContextWithOptions(newSize, false, 0.0)
        newSize.useContents {
            image.drawInRect(CGRectMake(0.0, 0.0, width, height))
        }
        val resizedImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return resizedImage ?: image
    }

    /**
     * Save image data to temporary directory and return URL
     */
    fun saveImageToTempDirectory(imageData: NSData): NSURL? {
        return try {
            val tempDir = NSFileManager.defaultManager.temporaryDirectory
            val fileName = "${NSUUID().UUIDString}.jpg"
            val fileURL = tempDir?.URLByAppendingPathComponent(fileName)
            
            fileURL?.let { url ->
                if (imageData.writeToURL(url, true)) {
                    url
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get file size for a given URL
     */
    private fun getFileSize(fileURL: NSURL): Long? {
        return try {
            val fileManager = NSFileManager.defaultManager
            val attributes = fileManager.attributesOfItemAtPath(fileURL.path!!, null)
            (attributes?.get("NSFileSize") as? NSNumber)?.longValue
        } catch (e: Exception) {
            null
        }
    }
}
