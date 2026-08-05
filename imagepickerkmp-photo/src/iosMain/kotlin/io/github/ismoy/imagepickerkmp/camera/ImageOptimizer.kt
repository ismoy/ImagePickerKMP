package io.github.ismoy.imagepickerkmp.camera

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
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringCreateWithCString
import platform.CoreFoundation.CFURLCreateWithFileSystemPath
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFNumberIntType
import platform.CoreFoundation.kCFStringEncodingUTF8
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.CoreFoundation.kCFURLPOSIXPathStyle
import platform.CoreGraphics.CGImageRef
import platform.CoreGraphics.CGImageRelease
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
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
import platform.UIKit.UIImagePNGRepresentation

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal object ImageOptimizer {
    
  
    private const val MAX_DIMENSION = 1280.0

    fun getMaxDimension(compressionLevel: CompressionLevel?): Double? {
        return compressionLevel?.toMaxDimension()?.toDouble()
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

            UIGraphicsBeginImageContextWithOptions(newSize, true, 1.0)
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
        if (compressionLevel == null) {
            return UIImagePNGRepresentation(image)
        }
        val maxDim = compressionLevel.toMaxDimension().toDouble()
        val resizedImage = resizeImageToMaxDimension(image, maxDim)
        val width = resizedImage.size.useContents { width.toInt() }
        val height = resizedImage.size.useContents { height.toInt() }
        val quality = compressionLevel.toQualityValue()
        return UIImageJPEGRepresentation(resizedImage, quality)
    }

    private fun resizeImageToMaxDimension(image: UIImage, maxDim: Double): UIImage {
        val originalWidth = image.size.useContents { width }
        val originalHeight = image.size.useContents { height }

        if (originalWidth <= maxDim && originalHeight <= maxDim) return image

        return image.size.useContents {
            val aspectRatio = width / height
            val newSize = if (width > height) {
                CGSizeMake(maxDim, maxDim / aspectRatio)
            } else {
                CGSizeMake(maxDim * aspectRatio, maxDim)
            }
            UIGraphicsBeginImageContextWithOptions(newSize, true, 1.0)
            val rect = newSize.useContents { CGRectMake(0.0, 0.0, this.width, this.height) }
            image.drawInRect(rect)
            val resized = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()
            resized ?: image
        }
    }
    fun processImageFromURL(url: NSURL, compressionLevel: CompressionLevel?): ProcessedImageResult? {
        if (compressionLevel == null) {
            var result: ProcessedImageResult? = null
            autoreleasepool {
                val data = NSData.dataWithContentsOfURL(url) ?: return@autoreleasepool
                var image: UIImage? = null
                autoreleasepool {
                    image = UIImage.imageWithData(data)
                }
                val width = image?.size?.useContents { width.toInt() } ?: 0
                val height = image?.size?.useContents { height.toInt() } ?: 0
                result = ProcessedImageResult(data = data, width = width, height = height)
            }
            return result
        }

        val maxDim = getMaxDimension(compressionLevel)!!.toInt()
        var result: ProcessedImageResult? = null

        autoreleasepool {
            var source: CGImageSourceRef? = null
            var thumbnail: CGImageRef? = null
            var cfData: platform.CoreFoundation.CFDataRef? = null

            try {
                val data = NSData.dataWithContentsOfURL(url)
                if (data == null) return@autoreleasepool

                val bytes = data.bytes
                val length = data.length.toLong()
                if (bytes == null || length <= 0) return@autoreleasepool

                cfData = platform.CoreFoundation.CFDataCreateWithBytesNoCopy(
                    null,
                    bytes.reinterpret(),
                    length,
                    platform.CoreFoundation.kCFAllocatorNull
                )
                if (cfData == null) return@autoreleasepool
                
                source = platform.ImageIO.CGImageSourceCreateWithData(cfData, null)
                if (source == null) return@autoreleasepool
                
                thumbnail = createThumbnail(source, maxDim)
                if (thumbnail == null) return@autoreleasepool
                
                var image = UIImage.imageWithCGImage(thumbnail)

                UIGraphicsBeginImageContextWithOptions(image.size, true, 1.0)
                val rect = image.size.useContents { CGRectMake(0.0, 0.0, this.width, this.height) }
                image.drawInRect(rect)
                val opaqueImage = UIGraphicsGetImageFromCurrentImageContext()
                UIGraphicsEndImageContext()
                
                if (opaqueImage != null) {
                    image = opaqueImage
                }

                val finalWidth = image.size.useContents { width.toInt() }
                val finalHeight = image.size.useContents { height.toInt() }
                val quality = calculateCompressionQuality(finalWidth, finalHeight, compressionLevel)

                val outData = UIImageJPEGRepresentation(image, quality)
                if (outData != null) {
                    result = ProcessedImageResult(data = outData, width = finalWidth, height = finalHeight)
                }
            } finally {
                if (cfData != null) CFRelease(cfData)
                if (source != null) CFRelease(source)
                if (thumbnail != null) CGImageRelease(thumbnail)
            }
        }

        return result
    }

    fun loadDownsampledUIImage(url: NSURL, maxDimension: Int): UIImage? {
        val path = url.path ?: return null
        var resultImage: UIImage? = null

        autoreleasepool {
            var cfPath: platform.CoreFoundation.CFStringRef? = null
            var cfUrl: platform.CoreFoundation.CFURLRef? = null
            var source: CGImageSourceRef? = null
            var thumbnail: CGImageRef? = null

            try {
                cfPath = CFStringCreateWithCString(null, path, kCFStringEncodingUTF8)
                if (cfPath == null) return@autoreleasepool
                
                cfUrl = CFURLCreateWithFileSystemPath(null, cfPath,
                    kCFURLPOSIXPathStyle, false)
                if (cfUrl == null) return@autoreleasepool
                
                source = CGImageSourceCreateWithURL(cfUrl, null)
                if (source == null) return@autoreleasepool
                
                thumbnail = createThumbnail(source, maxDimension)
                if (thumbnail == null) return@autoreleasepool
                
                resultImage = UIImage.imageWithCGImage(thumbnail)
            } finally {
                if (cfPath != null) CFRelease(cfPath)
                if (cfUrl != null) CFRelease(cfUrl)
                if (source != null) CFRelease(source)
                if (thumbnail != null) CGImageRelease(thumbnail)
            }
        }

        return resultImage
    }

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

            val thumbnail = CGImageSourceCreateThumbnailAtIndex(source, 0u, options)
            
            if (maxSizeNumber != null) CFRelease(maxSizeNumber)
            if (options != null) CFRelease(options)
            
            thumbnail
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
