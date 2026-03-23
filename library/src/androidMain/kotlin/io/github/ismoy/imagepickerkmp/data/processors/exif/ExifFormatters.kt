package io.github.ismoy.imagepickerkmp.data.processors.exif

import androidx.exifinterface.media.ExifInterface
import io.github.ismoy.imagepickerkmp.domain.config.FlashValues
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.INPUT_DATE_FORMAT
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.OUTPUT_DATE_FORMAT
import java.text.SimpleDateFormat
import java.util.*

internal object ExifFormatters {

    fun formatExifDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat(INPUT_DATE_FORMAT, Locale.getDefault())
            val outputFormat = SimpleDateFormat(OUTPUT_DATE_FORMAT, Locale.getDefault())
            val parsedDate = inputFormat.parse(dateString)
            parsedDate?.let { outputFormat.format(it) } ?: dateString
        } catch (_: Exception) {
            dateString 
        }
    }

    fun getOrientationDescription(orientation: Int): String {
        return when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> "Normal [1]"
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> "Flip Horizontal [2]" 
            ExifInterface.ORIENTATION_ROTATE_180 -> "Rotate 180° [3]"
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> "Flip Vertical [4]"
            ExifInterface.ORIENTATION_TRANSPOSE -> "Transpose [5]"
            ExifInterface.ORIENTATION_ROTATE_90 -> "Rotate 90° CW [6]"
            ExifInterface.ORIENTATION_TRANSVERSE -> "Transverse [7]"
            ExifInterface.ORIENTATION_ROTATE_270 -> "Rotate 90° CCW [8]"
            ExifInterface.ORIENTATION_UNDEFINED -> "Undefined [0]"
            else -> "Unknown [$orientation]"
        }
    }

    fun formatFlashMode(flashValue: String?): String? {
        return flashValue?.toIntOrNull()?.let(FlashValues::getDescription)
    }
}
