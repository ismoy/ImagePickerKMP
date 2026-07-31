package io.github.ismoy.imagepickerkmp.camera

import cnames.structs.CGImageSource
import cnames.structs.__CFDictionary
import cnames.structs.__CFURL
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.useContents
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryCreate
import platform.CoreFoundation.CFNumberCreate
import platform.CoreFoundation.CFStringCreateWithCString
import platform.CoreFoundation.CFURLCreateWithFileSystemPath
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFNumberIntType
import platform.CoreFoundation.kCFStringEncodingUTF8
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.CoreFoundation.kCFURLPOSIXPathStyle
import platform.CoreGraphics.CGImageRef
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.ImageIO.CGImageSourceCreateThumbnailAtIndex
import platform.ImageIO.CGImageSourceCreateWithURL
import platform.ImageIO.CGImageSourceRef
import platform.ImageIO.kCGImageSourceCreateThumbnailFromImageAlways
import platform.ImageIO.kCGImageSourceCreateThumbnailWithTransform
import platform.ImageIO.kCGImageSourceThumbnailMaxPixelSize
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal object ImageOptimizer {
    
  
    private const val MAX_DIMENSION = 1280.0

    /**
     * Returns the max dimension used for resizing based on [compressionLevel].
     * When null, defaults to [MAX_DIMENSION].
     */
    fun getMaxDimension(compressionLevel: CompressionLevel?): Double {
        return compressionLevel?.toMaxDimension()?.toDouble() ?: MAX_DIMENSION
    }
    
   
    fun resizeImageIfNeeded(image: UIImage): UIImage {
        val originalWidth = image.size.useContents { width.toInt() }
        val originalHeight = image.size.useContents { height.toInt() }
        
        if (originalWidth <= MAX_DIMENSION && originalHeight <= MAX_DIMENSION) {
            return image
        }
        
        return image.size.useContents {
            val aspectRatio = width / height
            val newSize = if (width > height) {
                CGSizeMake(MAX_DIMENSION, MAX_DIMENSION / aspectRatio)
            } else {
                CGSizeMake(MAX_DIMENSION * aspectRatio, MAX_DIMENSION)
            }
            
            UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
            val rect = newSize.useContents {
                CGRectMake(0.0, 0.0, this.width, this.height)
            }
            image.drawInRect(rect)
            val resizedImage = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()
            
            resizedImage ?: image
        }
    }
    
   
    fun calculateCompressionQuality(
        width: Int,
        height: Int,
        compressionLevel: CompressionLevel?
    ): Double {
        if (compressionLevel != null) {
            return compressionLevel.toQualityValue()
        }
        
        val pixels = width * height
        return when {
            pixels > 1_500_000 -> 0.50  
            pixels > 800_000 -> 0.60   
            else -> 0.70               
        }
    }
    
  
    fun compressImage(image: UIImage, quality: Double): NSData? {
        return UIImageJPEGRepresentation(image, quality)
    }
    
 
    fun processImage(image: UIImage, compressionLevel: CompressionLevel?): NSData? {
        val resizedImage = resizeImageIfNeeded(image)
        val width = resizedImage.size.useContents { width.toInt() }
        val height = resizedImage.size.useContents { height.toInt() }
        val quality = calculateCompressionQuality(width, height, compressionLevel)
        
        return compressImage(resizedImage, quality)
    }

    /**
     * Processes an image directly from a file [url] using **ImageIO hardware-accelerated
     * downsampling**, reading only the bytes needed for the target size. This avoids loading
     * the full original file into memory and never allocates a full-resolution bitmap, making
     * it the fastest and most memory-efficient path.
     *
     * @param url File URL to the source image (e.g. the temp file from PHPicker).
     * @param compressionLevel Optional compression level. When null, adaptive quality is used.
     * @return A [ProcessedImageResult], or null if processing fails (caller should fall back).
     */
    fun processImageFromURL(url: NSURL, compressionLevel: CompressionLevel?): ProcessedImageResult? {
        val path = url.path ?: return null
        val maxDim = getMaxDimension(compressionLevel).toInt()
        var result: ProcessedImageResult? = null

        autoreleasepool {
            // Build a CFURL from the file-system path (avoids an unsafe NSURL→CFURLRef cast).
            val cfPath = CFStringCreateWithCString(null, path, kCFStringEncodingUTF8)
            val cfUrl = CFURLCreateWithFileSystemPath(null, cfPath, kCFURLPOSIXPathStyle, false)
            val source = CGImageSourceCreateWithURL(cfUrl, null) ?: return@autoreleasepool
            val thumbnail = createThumbnail(source, maxDim) ?: return@autoreleasepool
            val image = UIImage.imageWithCGImage(thumbnail)

            val finalWidth = image.size.useContents { width.toInt() }
            val finalHeight = image.size.useContents { height.toInt() }
            val quality = calculateCompressionQuality(finalWidth, finalHeight, compressionLevel)

            val data = UIImageJPEGRepresentation(image, quality)
            if (data != null) {
                result = ProcessedImageResult(data = data, width = finalWidth, height = finalHeight)
            }
        }

        return result
    }

    /**
     * Builds the ImageIO thumbnail options dictionary and generates a downsampled [CGImageRef].
     */
    private fun createThumbnail(source: CGImageSourceRef, maxPixelSize: Int): CGImageRef? {
        return memScoped {
            val intVar = alloc<IntVar>()
            intVar.value = maxPixelSize
            val maxSizeNumber = CFNumberCreate(null, kCFNumberIntType, intVar.ptr)

            val keysArray: CArrayPointer<*> = allocArrayOf(
                kCGImageSourceCreateThumbnailFromImageAlways?.reinterpret<ByteVar>(),
                kCGImageSourceThumbnailMaxPixelSize?.reinterpret<ByteVar>(),
                kCGImageSourceCreateThumbnailWithTransform?.reinterpret<ByteVar>()
            )
            val valuesArray: CArrayPointer<*> = allocArrayOf(
                kCFBooleanTrue?.reinterpret<ByteVar>(),
                maxSizeNumber?.reinterpret<ByteVar>(),
                kCFBooleanTrue?.reinterpret<ByteVar>()
            )

            val options = CFDictionaryCreate(
                null,
                keysArray.reinterpret(),
                valuesArray.reinterpret(),
                3,
                kCFTypeDictionaryKeyCallBacks.ptr,
                kCFTypeDictionaryValueCallBacks.ptr
            )

            CGImageSourceCreateThumbnailAtIndex(source, 0u, options)
        }
    }
}

/**
 * Holds the output of a single image processing pass — the compressed data plus final dimensions.
 * This avoids needing to decode the result a second time just to read width/height.
 */
internal class ProcessedImageResult(
    val data: NSData,
    val width: Int,
    val height: Int
)
