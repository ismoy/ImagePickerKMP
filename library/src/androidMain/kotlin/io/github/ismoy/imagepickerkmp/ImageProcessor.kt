package io.github.ismoy.imagepickerkmp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import io.github.ismoy.imagepickerkmp.CameraController.CameraType
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler.PhotoResult
import io.github.ismoy.imagepickerkmp.ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_X
import io.github.ismoy.imagepickerkmp.ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_Y
import io.github.ismoy.imagepickerkmp.ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_X
import io.github.ismoy.imagepickerkmp.ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_Y
import io.github.ismoy.imagepickerkmp.ImagePickerUiConstants.ORIENTATION_ROTATE_180
import io.github.ismoy.imagepickerkmp.ImagePickerUiConstants.ORIENTATION_ROTATE_270
import io.github.ismoy.imagepickerkmp.ImagePickerUiConstants.ORIENTATION_ROTATE_90
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Handles image processing operations such as orientation correction and photo result creation.
 *
 * Provides methods to process images captured by the camera, including rotation, mirroring,
 * and conversion to result data classes for use in the library.
 */
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
        return try {
            val exif = ExifInterface(imageFile.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath) ?: return imageFile

            val matrix = getRotationMatrix(orientation)

            if (cameraType == CameraType.FRONT) {
                matrix.postScale(-1f, 1f)
            }

            val finalBitmap = if (matrix.isIdentity) {
                bitmap
            } else {
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true).also {
                    bitmap.recycle()
                }
            }

            val outputFile = if (matrix.isIdentity) {
                imageFile
            } else {
                val correctedFile = File(imageFile.parentFile, "corrected_${imageFile.name}")
                FileOutputStream(correctedFile).use { out ->
                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                finalBitmap.recycle()
                correctedFile
            }

            outputFile
        } catch (e: Exception) {
            println("Error correcting image orientation: ${e.message}")
            imageFile
        }
    }

    private fun getRotationMatrix(orientation: Int): Matrix {
        return Matrix().apply {
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> postRotate(ORIENTATION_ROTATE_90)
                ExifInterface.ORIENTATION_ROTATE_180 -> postRotate(ORIENTATION_ROTATE_180)
                ExifInterface.ORIENTATION_ROTATE_270 -> postRotate(ORIENTATION_ROTATE_270)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> postScale(ORIENTATION_FLIP_HORIZONTAL_X,
                    ORIENTATION_FLIP_HORIZONTAL_Y)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> postScale(ORIENTATION_FLIP_VERTICAL_X,
                    ORIENTATION_FLIP_VERTICAL_Y)
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    postRotate(ORIENTATION_ROTATE_90)
                    postScale(ORIENTATION_FLIP_HORIZONTAL_X, ORIENTATION_FLIP_HORIZONTAL_Y)
                }
                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    postRotate(ORIENTATION_ROTATE_270)
                    postScale(ORIENTATION_FLIP_HORIZONTAL_X, ORIENTATION_FLIP_HORIZONTAL_Y)
                }
            }
        }
    }
}
