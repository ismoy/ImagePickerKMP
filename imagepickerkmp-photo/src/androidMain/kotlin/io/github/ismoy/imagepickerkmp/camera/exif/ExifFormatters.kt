package io.github.ismoy.imagepickerkmp.camera.exif

import androidx.exifinterface.media.ExifInterface
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.INPUT_DATE_FORMAT
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.OUTPUT_DATE_FORMAT
import java.text.SimpleDateFormat
import java.util.*

private object FlashValues {
    const val NO_FLASH = 0
    const val FLASH_FIRED = 1
    const val STROBE_RETURN_LIGHT_NOT_DETECTED = 5
    const val STROBE_RETURN_LIGHT_DETECTED = 7
    const val FLASH_FIRED_COMPULSORY_MODE = 9
    const val FLASH_FIRED_COMPULSORY_MODE_RETURN_NOT_DETECTED = 13
    const val FLASH_FIRED_COMPULSORY_MODE_RETURN_DETECTED = 15
    const val FLASH_DID_NOT_FIRE_COMPULSORY_MODE = 16
    const val FLASH_DID_NOT_FIRE_AUTO_MODE = 24
    const val FLASH_FIRED_AUTO_MODE = 25
    const val FLASH_FIRED_AUTO_MODE_RETURN_NOT_DETECTED = 29
    const val FLASH_FIRED_AUTO_MODE_RETURN_DETECTED = 31
    const val NO_FLASH_FUNCTION = 32
    const val FLASH_FIRED_RED_EYE_REDUCTION = 65
    const val FLASH_FIRED_RED_EYE_REDUCTION_RETURN_NOT_DETECTED = 69
    const val FLASH_FIRED_RED_EYE_REDUCTION_RETURN_DETECTED = 71
    const val FLASH_FIRED_COMPULSORY_MODE_RED_EYE_REDUCTION = 73
    const val FLASH_FIRED_COMPULSORY_MODE_RED_EYE_REDUCTION_RETURN_NOT_DETECTED = 77
    const val FLASH_FIRED_COMPULSORY_MODE_RED_EYE_REDUCTION_RETURN_DETECTED = 79
    const val FLASH_FIRED_AUTO_MODE_RED_EYE_REDUCTION = 89
    const val FLASH_FIRED_AUTO_MODE_RETURN_NOT_DETECTED_RED_EYE_REDUCTION = 93
    const val FLASH_FIRED_AUTO_MODE_RETURN_DETECTED_RED_EYE_REDUCTION = 95

    fun getDescription(value: Int): String = when (value) {
        NO_FLASH -> "No Flash"
        FLASH_FIRED -> "Flash Fired"
        STROBE_RETURN_LIGHT_NOT_DETECTED -> "Strobe Return Light Not Detected"
        STROBE_RETURN_LIGHT_DETECTED -> "Strobe Return Light Detected"
        FLASH_FIRED_COMPULSORY_MODE -> "Flash Fired, Compulsory Flash Mode"
        FLASH_FIRED_COMPULSORY_MODE_RETURN_NOT_DETECTED -> "Flash Fired, Compulsory Flash Mode, Return Light Not Detected"
        FLASH_FIRED_COMPULSORY_MODE_RETURN_DETECTED -> "Flash Fired, Compulsory Flash Mode, Return Light Detected"
        FLASH_DID_NOT_FIRE_COMPULSORY_MODE -> "Flash Did Not Fire, Compulsory Flash Mode"
        FLASH_DID_NOT_FIRE_AUTO_MODE -> "Flash Did Not Fire, Auto Mode"
        FLASH_FIRED_AUTO_MODE -> "Flash Fired, Auto Mode"
        FLASH_FIRED_AUTO_MODE_RETURN_NOT_DETECTED -> "Flash Fired, Auto Mode, Return Light Not Detected"
        FLASH_FIRED_AUTO_MODE_RETURN_DETECTED -> "Flash Fired, Auto Mode, Return Light Detected"
        NO_FLASH_FUNCTION -> "No Flash Function"
        FLASH_FIRED_RED_EYE_REDUCTION -> "Flash Fired, Red-Eye Reduction Mode"
        FLASH_FIRED_RED_EYE_REDUCTION_RETURN_NOT_DETECTED -> "Flash Fired, Red-Eye Reduction Mode, Return Light Not Detected"
        FLASH_FIRED_RED_EYE_REDUCTION_RETURN_DETECTED -> "Flash Fired, Red-Eye Reduction Mode, Return Light Detected"
        FLASH_FIRED_COMPULSORY_MODE_RED_EYE_REDUCTION -> "Flash Fired, Compulsory Flash Mode, Red-Eye Reduction Mode"
        FLASH_FIRED_COMPULSORY_MODE_RED_EYE_REDUCTION_RETURN_NOT_DETECTED -> "Flash Fired, Compulsory Flash Mode, Red-Eye Reduction Mode, Return Light Not Detected"
        FLASH_FIRED_COMPULSORY_MODE_RED_EYE_REDUCTION_RETURN_DETECTED -> "Flash Fired, Compulsory Flash Mode, Red-Eye Reduction Mode, Return Light Detected"
        FLASH_FIRED_AUTO_MODE_RED_EYE_REDUCTION -> "Flash Fired, Auto Mode, Red-Eye Reduction Mode"
        FLASH_FIRED_AUTO_MODE_RETURN_NOT_DETECTED_RED_EYE_REDUCTION -> "Flash Fired, Auto Mode, Return Light Not Detected, Red-Eye Reduction Mode"
        FLASH_FIRED_AUTO_MODE_RETURN_DETECTED_RED_EYE_REDUCTION -> "Flash Fired, Auto Mode, Return Light Detected, Red-Eye Reduction Mode"
        else -> "Flash: $value"
    }
}

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
