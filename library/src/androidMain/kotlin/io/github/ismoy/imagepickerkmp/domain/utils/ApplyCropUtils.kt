
package io.github.ismoy.imagepickerkmp.domain.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun applyCropUtils(
    context: Context,
    photoResult: PhotoResult,
    cropRect: Rect,
    canvasSize: Size,
    isCircularCrop: Boolean,
    onComplete: (PhotoResult) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val inputStream = context.contentResolver.openInputStream(photoResult.uri.toUri())
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap != null) {
                val imageAspectRatio =
                    originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
                val canvasAspectRatio = canvasSize.width / canvasSize.height

                val displayedImageSize: Size
                val imageOffset: Offset

                if (imageAspectRatio > canvasAspectRatio) {
                    val displayedWidth = canvasSize.width
                    val displayedHeight = displayedWidth / imageAspectRatio
                    displayedImageSize = Size(displayedWidth, displayedHeight)
                    imageOffset = Offset(
                        x = 0f,
                        y = (canvasSize.height - displayedHeight) / 2f
                    )
                } else {
                    val displayedHeight = canvasSize.height
                    val displayedWidth = displayedHeight * imageAspectRatio
                    displayedImageSize = Size(displayedWidth, displayedHeight)
                    imageOffset = Offset(
                        x = (canvasSize.width - displayedWidth) / 2f,
                        y = 0f
                    )
                }
                val adjustedCropRect = Rect(
                    left = cropRect.left - imageOffset.x,
                    top = cropRect.top - imageOffset.y,
                    right = cropRect.right - imageOffset.x,
                    bottom = cropRect.bottom - imageOffset.y
                )
                val scaleX = originalBitmap.width.toFloat() / displayedImageSize.width
                val scaleY = originalBitmap.height.toFloat() / displayedImageSize.height

                val cropX = (adjustedCropRect.left * scaleX).toInt().coerceAtLeast(0)
                val cropY = (adjustedCropRect.top * scaleY).toInt().coerceAtLeast(0)
                val cropWidth = (adjustedCropRect.width * scaleX).toInt()
                    .coerceAtMost(originalBitmap.width - cropX)
                val cropHeight = (adjustedCropRect.height * scaleY).toInt()
                    .coerceAtMost(originalBitmap.height - cropY)
                val croppedBitmap = Bitmap.createBitmap(
                    originalBitmap,
                    cropX,
                    cropY,
                    cropWidth,
                    cropHeight
                )

                val finalBitmap = if (isCircularCrop) {
                    createCircularBitmap(croppedBitmap)
                } else {
                    createTransparentBitmap(croppedBitmap)
                }
                val outputFile =
                    File(context.cacheDir, "cropped_image_${System.currentTimeMillis()}.png")
                val outputStream = FileOutputStream(outputFile)
                finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
                
                val fileSizeInBytes = outputFile.length()
                
                val croppedPhotoResult = PhotoResult(
                    uri = outputFile.absolutePath,
                    width = finalBitmap.width,
                    height = finalBitmap.height,
                    fileName = "cropped_image_${System.currentTimeMillis()}.png",
                    fileSize = fileSizeInBytes,
                    mimeType = "image/png"
                )

                CoroutineScope(Dispatchers.Main).launch {
                    onComplete(croppedPhotoResult)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            CoroutineScope(Dispatchers.Main).launch {
                onComplete(photoResult)
            }
        }
    }
}