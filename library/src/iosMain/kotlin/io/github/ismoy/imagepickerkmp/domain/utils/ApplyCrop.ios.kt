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
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun ApplyCrop(
    photoResult: PhotoResult,
    cropRect: Rect,
    canvasSize: Size,
    isCircularCrop: Boolean,
    zoomLevel: Float,
    rotationAngle: Float,
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

            // Compute base fitted size then scale by zoomLevel (mirrors graphicsLayer in UI)
            val baseWidth: Double
            val baseHeight: Double
            if (imageAspectRatio > canvasAspectRatio) {
                baseWidth = canvasSize.width.toDouble()
                baseHeight = baseWidth / imageAspectRatio
            } else {
                baseHeight = canvasSize.height.toDouble()
                baseWidth = baseHeight * imageAspectRatio
            }
            val scaledWidth = baseWidth * zoomLevel
            val scaledHeight = baseHeight * zoomLevel
            displayedImageSize = CGSizeMake(scaledWidth, scaledHeight)
            imageOffset = CGPointMake(
                (canvasSize.width.toDouble() - scaledWidth) / 2.0,
                (canvasSize.height.toDouble() - scaledHeight) / 2.0
            )

            // When rotation is applied, the image is rotated around its center in the canvas.
            // We need to inverse-rotate the cropRect center to get the real crop position.
            val angleRad = rotationAngle * PI / 180.0
            val cosA = cos(-angleRad)
            val sinA = sin(-angleRad)

            // Center of the displayed image in canvas coords
            val imgCx = imageOffset.useContents { x } + scaledWidth / 2.0
            val imgCy = imageOffset.useContents { y } + scaledHeight / 2.0

            // Center of crop rect in canvas coords
            val cropCx = (cropRect.left + cropRect.right) / 2.0
            val cropCy = (cropRect.top + cropRect.bottom) / 2.0

            // Translate crop center relative to image center, rotate inverse, translate back
            val dx = cropCx - imgCx
            val dy = cropCy - imgCy
            val rotCx = cosA * dx - sinA * dy + imgCx
            val rotCy = sinA * dx + cosA * dy + imgCy

            val halfW = cropRect.width / 2.0
            val halfH = cropRect.height / 2.0

            val adjustedCropRect = CGRectMake(
                rotCx - halfW - imageOffset.useContents { x },
                rotCy - halfH - imageOffset.useContents { y },
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