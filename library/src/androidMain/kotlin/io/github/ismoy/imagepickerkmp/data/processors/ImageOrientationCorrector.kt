
package io.github.ismoy.imagepickerkmp.data.processors

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import io.github.ismoy.imagepickerkmp.data.models.CameraType
import io.github.ismoy.imagepickerkmp.domain.config.HighPerformanceConfig
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_ONE
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_ONE_THOUSAND_TWENTY_FOR
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_TWO
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_TWO_THOUSAND_FORTY_EIGHT
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_ZERO
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_X
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_Y
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_X
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_Y
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_ROTATE_180
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_ROTATE_270
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_ROTATE_90
import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger
import java.io.File
import java.io.FileOutputStream


internal class ImageOrientationCorrector(private val context: Context) {
    
    @SuppressLint("ExifInterface")
    fun correctImageOrientation(imageFile: File, cameraType: CameraType): File {
        return try {
            val exif = ExifInterface(imageFile.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            val needsCorrection = orientation != ExifInterface.ORIENTATION_NORMAL || cameraType == CameraType.FRONT
            if (!needsCorrection) {
                return imageFile
            }

            val isHighEnd = HighPerformanceConfig.isHighEndDevice(context)
            val probeOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(imageFile.absolutePath, probeOptions)
            val options = BitmapFactory.Options().apply {
                inPreferredConfig = if (isHighEnd) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
                inSampleSize = calculateInSampleSize(probeOptions.outWidth, probeOptions.outHeight, isHighEnd)
            }
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath, options) ?: return imageFile

            val matrix = getRotationMatrix(orientation)

            if (cameraType == CameraType.FRONT) {
                matrix.postScale(ORIENTATION_FLIP_HORIZONTAL_X, ORIENTATION_FLIP_HORIZONTAL_Y)
            }

            val finalBitmap = if (matrix.isIdentity) {
                bitmap
            } else {
                Bitmap.createBitmap(bitmap, NUMBER_ZERO, NUMBER_ZERO, bitmap.width, bitmap.height, matrix, false).also {
                    bitmap.recycle()
                }
            }

            val outputFile = if (matrix.isIdentity) {
                imageFile
            } else {
                val parentDir = imageFile.parentFile ?: imageFile.canonicalFile.parentFile
                    ?: throw IllegalStateException("Cannot resolve parent directory for: ${imageFile.absolutePath}")
                val correctedFile = File(parentDir, "corrected_${imageFile.name}")
                val recompressQuality = HighPerformanceConfig.getRecompressQuality()
                FileOutputStream(correctedFile).use { out ->
                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, recompressQuality, out)
                }
                finalBitmap.recycle()
                correctedFile
            }

            outputFile
        } catch (e: Exception) {
            DefaultLogger.logDebug("Error correcting image orientation: ${e.javaClass.simpleName}")
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

    private fun calculateInSampleSize(srcWidth: Int, srcHeight: Int, isHighEnd: Boolean): Int {
        val maxDimension = if (isHighEnd) NUMBER_TWO_THOUSAND_FORTY_EIGHT else NUMBER_ONE_THOUSAND_TWENTY_FOR
        var inSampleSize = NUMBER_ONE
        if (srcWidth <= NUMBER_ZERO || srcHeight <= NUMBER_ZERO) return inSampleSize
        val maxSrc = maxOf(srcWidth, srcHeight)
        while (maxSrc / (inSampleSize * NUMBER_TWO) >= maxDimension) {
            inSampleSize *= NUMBER_TWO
        }
        return inSampleSize
    }
}
