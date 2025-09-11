package io.github.ismoy.imagepickerkmp.domain.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGContextAddArc
import platform.CoreGraphics.CGContextClip
import platform.CoreGraphics.CGPoint
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSize
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetCurrentContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import kotlin.math.PI
import kotlin.math.min

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun applyCrop(
    photoResult: PhotoResult,
    cropRect: Rect,
    canvasSize: Size,
    isCircularCrop: Boolean,
    onComplete: (PhotoResult) -> Unit
) {
    try {
        val url = NSURL.URLWithString(photoResult.uri)
        val data = NSData.dataWithContentsOfURL(url ?: return onComplete(photoResult))
        val image = UIImage.imageWithData(data ?: return onComplete(photoResult))
        
        if (image != null) {
            val originalSize = image.size
            val imageAspectRatio = originalSize.useContents { width / height }
            val canvasAspectRatio = canvasSize.width / canvasSize.height
            val displayedImageSize: CValue<CGSize>
            val imageOffset: CValue<CGPoint>
            
            if (imageAspectRatio > canvasAspectRatio) {
                val displayedWidth = canvasSize.width.toDouble()
                val displayedHeight = displayedWidth / imageAspectRatio
                displayedImageSize = CGSizeMake(displayedWidth, displayedHeight)
                imageOffset = CGPointMake(
                    0.0,
                    (canvasSize.height.toDouble() - displayedHeight) / 2.0
                )
            } else {
                val displayedHeight = canvasSize.height.toDouble()
                val displayedWidth = displayedHeight * imageAspectRatio
                displayedImageSize = CGSizeMake(displayedWidth, displayedHeight)
                imageOffset = CGPointMake(
                    (canvasSize.width.toDouble() - displayedWidth) / 2.0,
                    0.0
                )
            }
            
            val adjustedCropRect = CGRectMake(
                cropRect.left.toDouble() - imageOffset.useContents { x },
                cropRect.top.toDouble() - imageOffset.useContents { y },
                cropRect.width.toDouble(),
                cropRect.height.toDouble()
            )
            
            val scaleX = originalSize.useContents { width } / displayedImageSize.useContents { width }
            val scaleY = originalSize.useContents { height } / displayedImageSize.useContents { height }
            
            val finalCropRect = CGRectMake(
                maxOf(0.0, adjustedCropRect.useContents { origin.x } * scaleX),
                maxOf(0.0, adjustedCropRect.useContents { origin.y } * scaleY),
                minOf(
                    adjustedCropRect.useContents { size.width } * scaleX,
                    originalSize.useContents { width } - maxOf(0.0, adjustedCropRect.useContents { origin.x } * scaleX)
                ),
                minOf(
                    adjustedCropRect.useContents { size.height } * scaleY,
                    originalSize.useContents { height } - maxOf(0.0, adjustedCropRect.useContents { origin.y } * scaleY)
                )
            )
            
            val cropSize = finalCropRect.useContents {
                CGSizeMake(size.width, size.height) 
            }
            
            UIGraphicsBeginImageContextWithOptions(cropSize, false, image.scale)
            val context = UIGraphicsGetCurrentContext()
            
            if (isCircularCrop) {
                val radius = min(cropSize.useContents { width }, cropSize.useContents { height }) / 2.0
                val centerX = cropSize.useContents { width } / 2.0
                val centerY = cropSize.useContents { height } / 2.0
                
                context?.let {
                    CGContextAddArc(it, centerX, centerY, radius, 0.0, 2.0 * PI, 0)
                    CGContextClip(it)
                }
            }
            
            val drawRect = CGRectMake(
                -finalCropRect.useContents { origin.x },
                -finalCropRect.useContents { origin.y },
                originalSize.useContents { width },
                originalSize.useContents { height }
            )
            
            image.drawInRect(drawRect)
            
            val croppedImage = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()
            
            if (croppedImage != null) {
                saveCroppedImageIOS(croppedImage, photoResult, onComplete)
            } else {
                onComplete(photoResult)
            }
        } else {
            onComplete(photoResult)
        }
    } catch (_: Exception) {
        onComplete(photoResult)
    }
}