package io.github.ismoy.imagepickerkmp.domain.utils.exif

import androidx.exifinterface.media.ExifInterface
import java.text.SimpleDateFormat
import java.util.*

internal object ExifFormatters {

    fun formatExifDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
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
        return flashValue?.toIntOrNull()?.let { value ->
            when (value) {
                0 -> "No Flash"
                1 -> "Flash Fired"
                5 -> "Strobe Return Light Not Detected"
                7 -> "Strobe Return Light Detected" 
                9 -> "Flash Fired, Compulsory Flash Mode"
                13 -> "Flash Fired, Compulsory Flash Mode, Return Light Not Detected"
                15 -> "Flash Fired, Compulsory Flash Mode, Return Light Detected"
                16 -> "Flash Did Not Fire, Compulsory Flash Mode"
                24 -> "Flash Did Not Fire, Auto Mode"
                25 -> "Flash Fired, Auto Mode"
                29 -> "Flash Fired, Auto Mode, Return Light Not Detected"
                31 -> "Flash Fired, Auto Mode, Return Light Detected"
                32 -> "No Flash Function"
                65 -> "Flash Fired, Red-Eye Reduction Mode"
                69 -> "Flash Fired, Red-Eye Reduction Mode, Return Light Not Detected"
                71 -> "Flash Fired, Red-Eye Reduction Mode, Return Light Detected"
                73 -> "Flash Fired, Compulsory Flash Mode, Red-Eye Reduction Mode"
                77 -> "Flash Fired, Compulsory Flash Mode, Red-Eye Reduction Mode, Return Light Not Detected"
                79 -> "Flash Fired, Compulsory Flash Mode, Red-Eye Reduction Mode, Return Light Detected"
                89 -> "Flash Fired, Auto Mode, Red-Eye Reduction Mode"
                93 -> "Flash Fired, Auto Mode, Return Light Not Detected, Red-Eye Reduction Mode"
                95 -> "Flash Fired, Auto Mode, Return Light Detected, Red-Eye Reduction Mode"
                else -> "Flash: $value"
            }
        }
    }

    fun formatMeteringMode(value: Int): String? {
        return when (value) {
            0 -> "Unknown"
            1 -> "Average"
            2 -> "Center Weighted Average"
            3 -> "Spot"
            4 -> "Multi Spot"
            5 -> "Pattern"
            6 -> "Partial"
            255 -> "Other"
            else -> null
        }
    }

    fun formatSceneCaptureType(value: Int): String? {
        return when (value) {
            0 -> "Standard"
            1 -> "Landscape"
            2 -> "Portrait"
            3 -> "Night Scene"
            else -> null
        }
    }

    fun formatColorSpace(value: Int): String? {
        return when (value) {
            1 -> "sRGB"
            65535 -> "Uncalibrated"
            else -> null
        }
    }

    fun formatWhiteBalance(value: Int): String? {
        return when (value) {
            0 -> "Auto"
            1 -> "Manual"
            else -> null
        }
    }

    fun formatResolutionUnit(value: Int): String? {
        return when (value) {
            1 -> "None"
            2 -> "Inch" 
            3 -> "Centimeter"
            else -> null
        }
    }

    fun formatCompression(value: Int): String? {
        return when (value) {
            1 -> "Uncompressed"
            6 -> "JPEG"
            else -> null
        }
    }
}
