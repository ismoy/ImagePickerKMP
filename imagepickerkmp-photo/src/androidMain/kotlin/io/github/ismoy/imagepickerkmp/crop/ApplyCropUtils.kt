package io.github.ismoy.imagepickerkmp.crop

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.NUMBER_HUNDRED
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.NUMBER_TWO_FLOAT
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.PNG_TEXT
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.PREFIX_CROPPED_IMAGE
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.SUFFIX_PNG
import io.github.ismoy.imagepickerkmp.logger.PhotoLogger
import io.github.ismoy.imagepickerkmp.picker.PhotoResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

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

        val originalBitmap = rawBitmap?.let { bitmap ->
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

            val exifMatrix = Matrix()
            when (exifOrientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> exifMatrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> exifMatrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> exifMatrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> exifMatrix.postScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> exifMatrix.postScale(1f, -1f)
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    exifMatrix.postRotate(90f)
                    exifMatrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    exifMatrix.postRotate(270f)
                    exifMatrix.postScale(1f, -1f)
                }
            }

            if (!exifMatrix.isIdentity) {
                Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.width,
                    bitmap.height,
                    exifMatrix,
                    false
                ).also { bitmap.recycle() }
            } else {
                bitmap
            }
        } ?: return@withContext photoResult

        val croppedBitmap = try {
            if (rotationAngle == 0f) {
                cropUnrotatedBitmap(originalBitmap, cropRect, canvasSize, zoomLevel)
            } else {
                cropRotatedBitmap(originalBitmap, cropRect, canvasSize, zoomLevel, rotationAngle)
            }
        } finally {
            originalBitmap.recycle()
        } ?: return@withContext photoResult

        val finalBitmap = if (isCircularCrop) {
            createCircularBitmap(croppedBitmap).also { croppedBitmap.recycle() }
        } else {
            croppedBitmap
        }
        val croppedWidth = finalBitmap.width
        val croppedHeight = finalBitmap.height

        context.cacheDir.listFiles { file -> file.name.startsWith(PREFIX_CROPPED_IMAGE) }
            ?.forEach { it.delete() }

        val usePngOutput = isCircularCrop || finalBitmap.hasAlpha()
        val outputFile = File(
            context.cacheDir,
            "$PREFIX_CROPPED_IMAGE${System.currentTimeMillis()}${if (usePngOutput) SUFFIX_PNG else ".jpg"}"
        )
        try {
            FileOutputStream(outputFile).use { outputStream ->
                val outputFormat = if (usePngOutput) {
                    Bitmap.CompressFormat.PNG
                } else {
                    Bitmap.CompressFormat.JPEG
                }
                val outputQuality = if (usePngOutput) NUMBER_HUNDRED else 95
                finalBitmap.compress(outputFormat, outputQuality, outputStream)
            }
        } finally {
            finalBitmap.recycle()
        }

        PhotoResult(
            uri = outputFile.absolutePath,
            width = croppedWidth,
            height = croppedHeight,
            fileName = outputFile.name,
            fileSize = outputFile.length(),
            mimeType = if (usePngOutput) PNG_TEXT else "image/jpeg"
        )
    } catch (exception: Exception) {
        PhotoLogger.debug("applyCropUtils error: ${exception.javaClass.simpleName}")
        photoResult
    }
}

private fun cropUnrotatedBitmap(
    bitmap: Bitmap,
    cropRect: Rect,
    canvasSize: Size,
    zoomLevel: Float
): Bitmap? {
    val displayScale = displayScale(bitmap, canvasSize, zoomLevel) ?: return null
    val imageOffset = Offset(
        x = (canvasSize.width - bitmap.width * displayScale) / NUMBER_TWO_FLOAT,
        y = (canvasSize.height - bitmap.height * displayScale) / NUMBER_TWO_FLOAT
    )
    val sourceLeft = ((cropRect.left - imageOffset.x) / displayScale).toInt()
    val sourceTop = ((cropRect.top - imageOffset.y) / displayScale).toInt()
    val sourceRight = ((cropRect.right - imageOffset.x) / displayScale).toInt()
    val sourceBottom = ((cropRect.bottom - imageOffset.y) / displayScale).toInt()

    val cropLeft = sourceLeft.coerceIn(0, bitmap.width - 1)
    val cropTop = sourceTop.coerceIn(0, bitmap.height - 1)
    val cropRight = sourceRight.coerceIn(cropLeft + 1, bitmap.width)
    val cropBottom = sourceBottom.coerceIn(cropTop + 1, bitmap.height)

    return Bitmap.createBitmap(
        bitmap,
        cropLeft,
        cropTop,
        cropRight - cropLeft,
        cropBottom - cropTop
    )
}

private fun cropRotatedBitmap(
    bitmap: Bitmap,
    cropRect: Rect,
    canvasSize: Size,
    zoomLevel: Float,
    rotationAngle: Float
): Bitmap? {
    val displayScale = displayScale(bitmap, canvasSize, zoomLevel) ?: return null
    val outputWidth = (cropRect.width / displayScale).toInt()
    val outputHeight = (cropRect.height / displayScale).toInt()
    if (outputWidth <= 0 || outputHeight <= 0) return null

    val outputConfig = if (bitmap.config == Bitmap.Config.RGB_565) {
        Bitmap.Config.RGB_565
    } else {
        Bitmap.Config.ARGB_8888
    }
    val output = Bitmap.createBitmap(outputWidth, outputHeight, outputConfig)
    output.setHasAlpha(bitmap.hasAlpha())

    val sourceToCrop = Matrix().apply {
        postTranslate(-bitmap.width / NUMBER_TWO_FLOAT, -bitmap.height / NUMBER_TWO_FLOAT)
        postRotate(rotationAngle)
        postTranslate(
            canvasSize.width / (NUMBER_TWO_FLOAT * displayScale) - cropRect.left / displayScale,
            canvasSize.height / (NUMBER_TWO_FLOAT * displayScale) - cropRect.top / displayScale
        )
    }
    val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    Canvas(output).drawBitmap(bitmap, sourceToCrop, paint)
    return output
}

private fun displayScale(
    bitmap: Bitmap,
    canvasSize: Size,
    zoomLevel: Float
): Float? {
    if (bitmap.width <= 0 || bitmap.height <= 0 ||
        canvasSize.width <= 0f || canvasSize.height <= 0f || zoomLevel <= 0f
    ) {
        return null
    }

    val fitScale = min(
        canvasSize.width / bitmap.width.toFloat(),
        canvasSize.height / bitmap.height.toFloat()
    )
    val result = fitScale * zoomLevel
    return result.takeIf { it.isFinite() && it > 0f }
}
