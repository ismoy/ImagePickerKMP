
package io.github.ismoy.imagepickerkmp.data.processors

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import io.github.ismoy.imagepickerkmp.data.camera.CameraController.CameraType
import io.github.ismoy.imagepickerkmp.domain.config.HighPerformanceConfig
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_X
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_Y
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_X
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_Y
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_ROTATE_180
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_ROTATE_270
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_ROTATE_90
import java.io.File
import java.io.FileOutputStream

/**
 * Handles image orientation correction operations.
 * 
 * SOLID: Single Responsibility - Only handles orientation correction
 * SOLID: Open/Closed - Can be extended for different correction algorithms
 */
class ImageOrientationCorrector {
    
    @SuppressLint("ExifInterface")
    fun correctImageOrientation(imageFile: File, cameraType: CameraType): File {
        return try {
            val exif = ExifInterface(imageFile.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            // Quick check if no correction is needed
            val needsCorrection = orientation != ExifInterface.ORIENTATION_NORMAL || cameraType == CameraType.FRONT
            if (!needsCorrection) {
                return imageFile
            }

            // Use optimized bitmap decoding based on device capabilities
            val options = BitmapFactory.Options().apply {
                inPreferredConfig = if (HighPerformanceConfig.isHighEndDevice()) {
                    Bitmap.Config.ARGB_8888 // Best quality for flagship devices
                } else {
                    Bitmap.Config.RGB_565 // Memory efficient for other devices
                }
                inSampleSize = 1
            }
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath, options) ?: return imageFile

            val matrix = getRotationMatrix(orientation)

            if (cameraType == CameraType.FRONT) {
                matrix.postScale(-1f, 1f)
            }

            val finalBitmap = if (matrix.isIdentity) {
                bitmap
            } else {
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false).also {
                    bitmap.recycle()
                }
            }

            val outputFile = if (matrix.isIdentity) {
                imageFile
            } else {
                val correctedFile = File(imageFile.parentFile, "corrected_${imageFile.name}")
                FileOutputStream(correctedFile).use { out ->
                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out) // Slightly higher quality for better performance trade-off
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
