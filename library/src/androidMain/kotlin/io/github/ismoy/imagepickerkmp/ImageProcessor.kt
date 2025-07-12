package io.github.ismoy.imagepickerkmp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler.PhotoResult
import io.github.ismoy.imagepickerkmp.CameraController.CameraType

class ImageProcessor(context: Context) {
    private val fileManager = FileManager(context)

    fun processImage(
        imageFile: File,
        cameraType: CameraType,
        onPhotoCaptured: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Correct orientation and mirroring if needed (e.g., for front camera)
                val correctedImageFile = correctImageOrientation(imageFile, cameraType)
                val bitmap = BitmapFactory.decodeFile(correctedImageFile.absolutePath)
                if (bitmap != null) {
                    val result = PhotoResult(
                        uri = fileManager.fileToUriString(correctedImageFile),
                        width = bitmap.width,
                        height = bitmap.height
                    )
                    withContext(Dispatchers.Main) {
                        onPhotoCaptured(result)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onError(PhotoCaptureException("Failed to decode captured image."))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(PhotoCaptureException("Failed to process image: ${e.message}"))
                }
            }
        }
    }

    private fun correctImageOrientation(imageFile: File, cameraType: CameraType): File {
        try {
            val exif = ExifInterface(imageFile.absolutePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            if (bitmap == null) return imageFile

            val matrix = Matrix()
            // Apply EXIF-based rotation if needed
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    matrix.postRotate(90f)
                    matrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    matrix.postRotate(270f)
                    matrix.postScale(-1f, 1f)
                }
            }

            // Mirror horizontally if using the front camera
            if (cameraType == CameraType.FRONT) {
                matrix.postScale(-1f, 1f)
            }

            // Only create a new bitmap if a transformation is needed
            val finalBitmap = if (matrix.isIdentity) {
                bitmap
            } else {
                Bitmap.createBitmap(
                    bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                ).also {
                    // Free original bitmap if a new one is created
                    bitmap.recycle()
                }
            }

            // Save the corrected image only if it was transformed
            val outputFile = if (matrix.isIdentity) {
                imageFile
            } else {
                val correctedFile = File(imageFile.parentFile, "corrected_${imageFile.name}")
                FileOutputStream(correctedFile).use { out ->
                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                correctedFile
            }

            // Free memory if a new bitmap was created
            if (!matrix.isIdentity) {
                finalBitmap.recycle()
            }

            return outputFile
        } catch (e: Exception) {
            // If anything fails, return the original file
            return imageFile
        }
    }
}