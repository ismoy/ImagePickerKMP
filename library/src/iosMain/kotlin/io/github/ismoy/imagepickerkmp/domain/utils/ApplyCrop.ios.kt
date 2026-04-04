package io.github.ismoy.imagepickerkmp.domain.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGAffineTransformMakeRotation
import platform.CoreGraphics.CGAffineTransformMakeTranslation
import platform.CoreGraphics.CGAffineTransformConcat
import platform.CoreGraphics.CGContextAddArc
import platform.CoreGraphics.CGContextClip
import platform.CoreGraphics.CGContextConcatCTM
import platform.CoreGraphics.CGRectMake
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
import kotlin.math.abs
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
            // ── Paso 1: rotar la imagen completa al ángulo indicado ──────────────
            // Esto replica exactamente el graphicsLayer(rotationZ = rotationAngle)
            // que se ve en CropImageCanvas. Una vez rotada, el cropRect del canvas
            // apunta directamente a la región correcta en la imagen rotada.
            val rotatedImage: UIImage = if (rotationAngle != 0f) {
                rotateUIImage(image, rotationAngle)
            } else {
                image
            }

            // ── Paso 2: mapear cropRect (coords canvas) → coords imagen rotada ──
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

            // Tamaño de la imagen rotada tal como se muestra en el canvas (con zoom)
            val scaledWidth = baseWidth * zoomLevel
            val scaledHeight = baseHeight * zoomLevel
            val imageOffsetX = (canvasSize.width.toDouble() - scaledWidth) / 2.0
            val imageOffsetY = (canvasSize.height.toDouble() - scaledHeight) / 2.0

            // Factor de escala: de coords canvas → coords imagen rotada
            val scaleX = rotatedSize.useContents { width } / scaledWidth
            val scaleY = rotatedSize.useContents { height } / scaledHeight

            // Crop rect en coordenadas de la imagen rotada (píxeles reales)
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

            // ── Paso 3: extraer la región del crop de la imagen rotada ───────────
            // scale = 0.0 → UIKit usa automáticamente el screen scale (@2x, @3x).
            UIGraphicsBeginImageContextWithOptions(cropSize, false, 0.0)
            val context = UIGraphicsGetCurrentContext()

            if (isCircularCrop) {
                val radius = min(finalW, finalH) / 2.0
                val centerX = finalW / 2.0
                val centerY = finalH / 2.0
                context?.let {
                    CGContextAddArc(it, centerX, centerY, radius, 0.0, 2.0 * PI, 0)
                    CGContextClip(it)
                }
            }

            // Dibujar imagen rotada desplazada para que el crop quede en (0,0)
            val drawRect = CGRectMake(-finalX, -finalY, imgW, imgH)
            rotatedImage.drawInRect(drawRect)

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

/**
 * Rota una UIImage al ángulo dado (en grados, sentido horario positivo,
 * igual que Compose rotationZ).
 * El tamaño del canvas resultante se expande para contener la imagen completa rotada.
 */
@OptIn(ExperimentalForeignApi::class)
private fun rotateUIImage(image: UIImage, angleDegrees: Float): UIImage {
    val angleRad = angleDegrees * PI / 180.0
    val originalSize = image.size
    val origW = originalSize.useContents { width }
    val origH = originalSize.useContents { height }

    // Nuevo tamaño del canvas para no recortar esquinas
    val cosA = abs(cos(angleRad))
    val sinA = abs(sin(angleRad))
    val newW = origW * cosA + origH * sinA
    val newH = origW * sinA + origH * cosA

    val newSize = CGSizeMake(newW, newH)

    // scale = 0.0 → misma resolución que la pantalla (o la imagen original)
    UIGraphicsBeginImageContextWithOptions(newSize, false, 0.0)
    val context = UIGraphicsGetCurrentContext() ?: run {
        UIGraphicsEndImageContext()
        return image
    }

    // Mover origen al centro del nuevo canvas, rotar, centrar la imagen
    val toCenter = CGAffineTransformMakeTranslation(newW / 2.0, newH / 2.0)
    val rotation = CGAffineTransformMakeRotation(angleRad)
    val toOrigin = CGAffineTransformMakeTranslation(-origW / 2.0, -origH / 2.0)
    val transform = CGAffineTransformConcat(CGAffineTransformConcat(toOrigin, rotation), toCenter)
    CGContextConcatCTM(context, transform)

    image.drawInRect(CGRectMake(0.0, 0.0, origW, origH))

    val rotated = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()

    return rotated ?: image
}