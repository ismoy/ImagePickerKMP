
package io.github.ismoy.imagepickerkmp.data.processors

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.exifinterface.media.ExifInterface
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger
import androidx.core.net.toUri
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_HUNDRED
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_TWO_FLOAT
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_ZERO
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_ZERO_FLOAT
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.PNG_TEXT
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.PREFIX_CROPPED_IMAGE
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.SUFFIX_PNG
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal suspend fun applyCropUtils(
    context: Context,
    photoResult: PhotoResult,
    cropRect: Rect,
    canvasSize: Size,
    isCircularCrop: Boolean,
    zoomLevel: Float = 1f,
    rotationAngle: Float = 0f
): PhotoResult = withContext(Dispatchers.IO) {
    try {
        val rawBitmap = context.contentResolver
            .openInputStream(photoResult.uri.toUri())
            ?.use { BitmapFactory.decodeStream(it) }

        val originalBitmap = rawBitmap?.let { bmp ->
            val exifOrientation = try {
                context.contentResolver.openInputStream(photoResult.uri.toUri())?.use { stream ->
                    ExifInterface(stream).getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                } ?: ExifInterface.ORIENTATION_NORMAL
            } catch (_: Exception) {
                ExifInterface.ORIENTATION_NORMAL
            }

            val matrix = Matrix()
            when (exifOrientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    matrix.postRotate(90f); matrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    matrix.postRotate(270f); matrix.postScale(-1f, 1f)
                }
                else -> null
            }

            if (!matrix.isIdentity) {
                val rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, false)
                bmp.recycle()
                rotated
            } else {
                bmp
            }
        }

        if (originalBitmap != null) {
            // Apply UI rotation to the bitmap so it matches what the user sees
            val orientedBitmap = if (rotationAngle != 0f) {
                val rotMatrix = Matrix().apply { postRotate(rotationAngle) }
                val rotated = Bitmap.createBitmap(
                    originalBitmap, 0, 0,
                    originalBitmap.width, originalBitmap.height, rotMatrix, true
                )
                originalBitmap.recycle()
                rotated
            } else {
                originalBitmap
            }

            val imageAspectRatio =
                orientedBitmap.width.toFloat() / orientedBitmap.height.toFloat()
            val canvasAspectRatio = canvasSize.width / canvasSize.height

            val displayedImageSize: Size
            val imageOffset: Offset

            // Compute base fitted size then apply zoomLevel (same as graphicsLayer in UI)
            val baseWidth: Float
            val baseHeight: Float
            if (imageAspectRatio > canvasAspectRatio) {
                baseWidth = canvasSize.width
                baseHeight = baseWidth / imageAspectRatio
            } else {
                baseHeight = canvasSize.height
                baseWidth = baseHeight * imageAspectRatio
            }
            val scaledWidth = baseWidth * zoomLevel
            val scaledHeight = baseHeight * zoomLevel
            displayedImageSize = Size(scaledWidth, scaledHeight)
            imageOffset = Offset(
                x = (canvasSize.width - scaledWidth) / NUMBER_TWO_FLOAT,
                y = (canvasSize.height - scaledHeight) / NUMBER_TWO_FLOAT
            )

            val adjustedCropRect = Rect(
                left = cropRect.left - imageOffset.x,
                top = cropRect.top - imageOffset.y,
                right = cropRect.right - imageOffset.x,
                bottom = cropRect.bottom - imageOffset.y
            )
            val scaleX = orientedBitmap.width.toFloat() / displayedImageSize.width
            val scaleY = orientedBitmap.height.toFloat() / displayedImageSize.height

            val cropX = (adjustedCropRect.left * scaleX).toInt().coerceAtLeast(NUMBER_ZERO)
            val cropY = (adjustedCropRect.top * scaleY).toInt().coerceAtLeast(NUMBER_ZERO)
            val cropWidth = (adjustedCropRect.width * scaleX).toInt()
                .coerceAtMost(orientedBitmap.width - cropX)
            val cropHeight = (adjustedCropRect.height * scaleY).toInt()
                .coerceAtMost(orientedBitmap.height - cropY)

            val croppedBitmap = Bitmap.createBitmap(
                orientedBitmap,
                cropX,
                cropY,
                cropWidth,
                cropHeight
            )
            orientedBitmap.recycle()

            val finalBitmap = if (isCircularCrop) {
                createCircularBitmap(croppedBitmap)
            } else {
                createTransparentBitmap(croppedBitmap)
            }
            val croppedWidth = croppedBitmap.width
            val croppedHeight = croppedBitmap.height
            if (finalBitmap != croppedBitmap) croppedBitmap.recycle()

            context.cacheDir.listFiles { f -> f.name.startsWith(PREFIX_CROPPED_IMAGE) }
                ?.forEach { it.delete() }

            val outputFile =
                File(context.cacheDir, "$PREFIX_CROPPED_IMAGE${System.currentTimeMillis()}$SUFFIX_PNG")
            try {
                FileOutputStream(outputFile).use { outputStream ->
                    finalBitmap.compress(Bitmap.CompressFormat.PNG, NUMBER_HUNDRED, outputStream)
                }
            } finally {
                finalBitmap.recycle()
            }

            val fileSizeInBytes = outputFile.length()

            PhotoResult(
                uri = outputFile.absolutePath,
                width = croppedWidth,
                height = croppedHeight,
                fileName = outputFile.name,
                fileSize = fileSizeInBytes,
                mimeType = PNG_TEXT
            )
        } else {
            photoResult
        }
    } catch (e: Exception) {
        DefaultLogger.logDebug("applyCropUtils error: ${e.javaClass.simpleName}")
        photoResult
    }
}
