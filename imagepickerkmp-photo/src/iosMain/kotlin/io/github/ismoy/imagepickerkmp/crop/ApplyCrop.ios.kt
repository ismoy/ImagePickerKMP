package io.github.ismoy.imagepickerkmp.crop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.ismoy.imagepickerkmp.picker.PhotoResult
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGAffineTransformConcat
import platform.CoreGraphics.CGAffineTransformMakeRotation
import platform.CoreGraphics.CGAffineTransformMakeTranslation
import platform.CoreGraphics.CGContextAddArc
import platform.CoreGraphics.CGContextClip
import platform.CoreGraphics.CGContextConcatCTM
import platform.CoreGraphics.CGImageCreateWithImageInRect
import platform.CoreGraphics.CGImageRelease
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import io.github.ismoy.imagepickerkmp.camera.ImageOptimizer
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetCurrentContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.native.runtime.GC
import kotlin.native.runtime.NativeRuntimeApi

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
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
    LaunchedEffect(photoResult, cropRect, canvasSize, isCircularCrop, zoomLevel, rotationAngle) {
        val result = withContext(Dispatchers.Default) {
            applyCropUtilsIOS(photoResult, cropRect, canvasSize, isCircularCrop, zoomLevel, rotationAngle)
        }
        onComplete(result)
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class, NativeRuntimeApi::class)
private fun applyCropUtilsIOS(
    photoResult: PhotoResult,
    cropRect: Rect,
    canvasSize: Size,
    isCircularCrop: Boolean,
    zoomLevel: Float,
    rotationAngle: Float
): PhotoResult {
    var finalResult: PhotoResult = photoResult
    try {
        autoreleasepool {
            val url = NSURL.URLWithString(photoResult.uri)
            // The user requested drastic memory reduction for cropping.
            // Downsample the source image to max 1080 pixels before applying the crop math.
            // This reduces the raw Bitmap from ~25MB down to ~3-4MB.
            val image = ImageOptimizer.loadDownsampledUIImage(url ?: return@autoreleasepool, 1080)
            
            if (image != null) {
                val rotatedImage: UIImage = if (rotationAngle != 0f) {
                    rotateUIImage(image, rotationAngle)
                } else {
                    image
                }

                val rotatedSize = rotatedImage.size
                val imageAspectRatio = rotatedSize.useContents { width / height }
                val canvasAspectRatio = canvasSize.width / canvasSize.height

                val baseWidth: Double
                val baseHeight: Double
                if (imageAspectRatio > canvasAspectRatio) {
                    baseWidth = canvasSize.width.toDouble()
                    baseHeight = baseWidth / imageAspectRatio
                } else {
                    baseHeight = canvasSize.height.toDouble()
                    baseWidth = baseHeight * imageAspectRatio
                }

                // Size of the rotated image as displayed on the canvas (with zoom)
                val scaledWidth = baseWidth * zoomLevel
                val scaledHeight = baseHeight * zoomLevel
                val imageOffsetX = (canvasSize.width.toDouble() - scaledWidth) / 2.0
                val imageOffsetY = (canvasSize.height.toDouble() - scaledHeight) / 2.0

                val scaleX = rotatedSize.useContents { width } / scaledWidth
                val scaleY = rotatedSize.useContents { height } / scaledHeight

                // Crop rect in rotated image coordinates (real pixels)
                val cropInImgX = (cropRect.left - imageOffsetX) * scaleX
                val cropInImgY = (cropRect.top - imageOffsetY) * scaleY
                val cropInImgW = cropRect.width * scaleX
                val cropInImgH = cropRect.height * scaleY

                val imgW = rotatedSize.useContents { width }
                val imgH = rotatedSize.useContents { height }

                val finalX = maxOf(0.0, cropInImgX)
                val finalY = maxOf(0.0, cropInImgY)
                val finalW = minOf(cropInImgW, imgW - finalX)
                val finalH = minOf(cropInImgH, imgH - finalY)

                val cropSize = CGSizeMake(finalW, finalH)

                val croppedImage: UIImage? = if (isCircularCrop) {
                    UIGraphicsBeginImageContextWithOptions(cropSize, true, 1.0)
                    val context = UIGraphicsGetCurrentContext()
                    val radius = min(finalW, finalH) / 2.0
                    val centerX = finalW / 2.0
                    val centerY = finalH / 2.0
                    context?.let {
                        CGContextAddArc(it, centerX, centerY, radius, 0.0, 2.0 * PI, 0)
                        CGContextClip(it)
                    }

                    val drawRect = CGRectMake(-finalX, -finalY, imgW, imgH)
                    rotatedImage.drawInRect(drawRect)

                    val img = UIGraphicsGetImageFromCurrentImageContext()
                    UIGraphicsEndImageContext()
                    img
                } else {
                    // Zero-copy memory-efficient rectangular crop
                    val cgImage = rotatedImage.CGImage
                    if (cgImage != null) {
                        val cropRectCG = CGRectMake(finalX, finalY, finalW, finalH)
                        val croppedCG = CGImageCreateWithImageInRect(cgImage, cropRectCG)
                        if (croppedCG != null) {
                            val img = UIImage.imageWithCGImage(croppedCG)
                            CGImageRelease(croppedCG)
                            img
                        } else null
                    } else null
                }

                if (croppedImage != null) {
                    finalResult = saveCroppedImageIOS(croppedImage, photoResult)
                }
            }
        }
    } catch (_: Exception) {
    } finally {
        GC.collect()
    }
    return finalResult
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun rotateUIImage(image: UIImage, angleDegrees: Float): UIImage {
    var resultImage: UIImage = image
    autoreleasepool {
        val angleRad = angleDegrees * PI / 180.0
        val originalSize = image.size
        val origW = originalSize.useContents { width }
        val origH = originalSize.useContents { height }
        val cosA = abs(cos(angleRad))
        val sinA = abs(sin(angleRad))
        val newW = origW * cosA + origH * sinA
        val newH = origW * sinA + origH * cosA

        val newSize = CGSizeMake(newW, newH)
        // Use true (opaque) to avoid massive RAM spikes during JPEG compression
        UIGraphicsBeginImageContextWithOptions(newSize, true, 1.0)
        val context = UIGraphicsGetCurrentContext() ?: run {
            UIGraphicsEndImageContext()
            return@autoreleasepool
        }

        val toCenter = CGAffineTransformMakeTranslation(newW / 2.0, newH / 2.0)
        val rotation = CGAffineTransformMakeRotation(angleRad)
        val toOrigin = CGAffineTransformMakeTranslation(-origW / 2.0, -origH / 2.0)
        val transform = CGAffineTransformConcat(CGAffineTransformConcat(toOrigin, rotation), toCenter)
        CGContextConcatCTM(context, transform)

        image.drawInRect(CGRectMake(0.0, 0.0, origW, origH))

        val rotated = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        resultImage = rotated ?: image
    }
    return resultImage
}