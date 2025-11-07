package io.github.ismoy.imagepickerkmp.data.delegates

import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

@OptIn(ExperimentalForeignApi::class)
internal object ImageOptimizer {
    
  
    private const val MAX_DIMENSION = 1280.0
    
   
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
}
