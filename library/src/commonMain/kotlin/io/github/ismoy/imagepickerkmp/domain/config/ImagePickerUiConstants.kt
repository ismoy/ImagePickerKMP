package io.github.ismoy.imagepickerkmp.domain.config

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal object ImagePickerUiConstants {
    val FlashToggleBackgroundColor = Color(0xAA444444)
    const val FlashToggleCornerRadius = 50
    val FlashToggleHorizontalPadding = 30.dp
    val FlashToggleVerticalPadding = 8.dp

    val ConfirmationCardMaxWidth = 400.dp
    val ConfirmationCardCornerRadius = 32.dp
    const val ConfirmationCardImageAspectRatio = 1f
    val ConfirmationCardPadding = 24.dp
    val ConfirmationCardSpacerHeight = 24.dp
    val ConfirmationCardButtonSpacing = 16.dp
    val ConfirmationCardTitleFontSize = 22.sp
    val ConfirmationCardTitleColor = Color(0xFFCBD5E1)
    val ConfirmationCardBackgroundColor = Color(0xFF2D3748)
    val BackgroundColor = Color(0xFFF5F5F5)
    val ConfirmationCardIconColor = Color.White
    val ConfirmationCardButtonSize = 48.dp
    val ConfirmationCardRetryButtonColor = Color(0xFFE53935)
    val ConfirmationCardAcceptButtonColor = Color(0xFF43A047)
    val ConfirmationCardButtonIconPadding = 4.dp
    val ConfirmationCardDialogBackground = Color(0xFF1A202C)
    val ConfirmationCardDialogHorizontalPadding = 16.dp
    val ConfirmationCardButtonTextFontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    val DefaultCardElevation = 16.dp
    const val ORIENTATION_ROTATE_90 = 90f
    const val ORIENTATION_ROTATE_180 = 180f
    const val ORIENTATION_ROTATE_270 = 270f
    const val ORIENTATION_FLIP_HORIZONTAL_X = -1f
    const val ORIENTATION_FLIP_HORIZONTAL_Y = 1f
    const val ORIENTATION_FLIP_VERTICAL_X = 1f
    const val ORIENTATION_FLIP_VERTICAL_Y = -1f
    const val SYSTEM_VERSION_10 = 10.0
    const val DELAY_TO_TAKE_PHOTO = 10L
    const val SELECTION_LIMIT = 30L
    const val ERROR_BIND_CAMERA_MESSAGE = "Failed to bind camera use cases: "
    const val ERROR_TO_SWITCH_CAMERA_MESSAGE = "Failed to switch camera: "
    const val ERROR_CAMERA_NOT_INITIALIZED = "Camera not initialized. "
    const val BOUND_SIZE_WIDTH = 4000
    const val BOUND_SIZE_HEIGHT = 3000
    const val PNG_TEXT = "image/png"
    const val APPLICATION_PDF_TEXT = "application/pdf"
    const val IMAGE_PREFIX_TEXT = "image/"
    const val IMAGE_PREFIX_TEXT_ALL = "image/*"
    const val GALLERY_PROCESSOR_TAG = "GalleryProcessor"
    const val IMAGEPROCESSOR_TAG = "GalleryImageProcessor"
    const val GALLERY_FILE_UTILS_TAG = "GalleryFileUtils: Error querying file name:"
    const val NUMBER_THREE = 3
    const val MINVALUE_COMPRESSOR = 8192
    const val PREFIX_COMPRESSED_GALLERY = "compressed_gallery_"
    const val SUFFIX_COMPRESSED_GALLERY = ".jpg"
    const val NUMBER_ZERO = 0
    const val PREFIX_COMPRESSED = "compressed_"
    const val DATE_FORMATE = "yyyyMMdd_HHmmss"
    const val IMAGE_TEMP = "imagepicker_temp"
    const val PREFIX_JPEG = "JPEG_"
    const val NUMBER_TEN = 10
    const val N_A = "N/A"
    const val NUMBER_TWO = 2
    const val NUMBER_ONE = 1
    const val NUMBER_MINUS_ONE_DOT_ZERO = -1.0
    const val INPUT_DATE_FORMAT = "yyyy:MM:dd HH:mm:ss"
    const val OUTPUT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    const val NUMBER_MINUS_ONE = -1
    const val EXTRACTION_EXIF_FAILED_TAG = "Thumbnail extraction failed"
    const val PREFIX_EXIF_CALLBACK = "exif_fallback_"
    const val NUMBER_ZERO_FLOAT = 0f
    const val NUMBER_TWO_FLOAT = 2f
    const val NUMBER_HUNDRED = 100
    const val PREFIX_CROPPED_IMAGE = "cropped_image_"
    const val SUFFIX_PNG = ".png"
    const val NUMBER_TWO_THOUSAND_FORTY_EIGHT = 2048
    const val NUMBER_ONE_THOUSAND_TWENTY_FOR = 1024
    const val SIXTY_FIVE_THOUSAND_FIVE_HUNDRED_THIRTY_SIX = 65536
    const val SUFFIX_PDF = "pdf"

}

internal object FlashValues {
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

internal object ExifValues {

    const val METERING_UNKNOWN = 0
    const val METERING_AVERAGE = 1
    const val METERING_CENTER_WEIGHTED_AVERAGE = 2
    const val METERING_SPOT = 3
    const val METERING_MULTI_SPOT = 4
    const val METERING_PATTERN = 5
    const val METERING_PARTIAL = 6
    const val METERING_OTHER = 255

    fun formatMeteringMode(value: Int): String? = when (value) {
        METERING_UNKNOWN -> "Unknown"
        METERING_AVERAGE -> "Average"
        METERING_CENTER_WEIGHTED_AVERAGE -> "Center Weighted Average"
        METERING_SPOT -> "Spot"
        METERING_MULTI_SPOT -> "Multi Spot"
        METERING_PATTERN -> "Pattern"
        METERING_PARTIAL -> "Partial"
        METERING_OTHER -> "Other"
        else -> null
    }

    const val SCENE_STANDARD = 0
    const val SCENE_LANDSCAPE = 1
    const val SCENE_PORTRAIT = 2
    const val SCENE_NIGHT = 3

    fun formatSceneCaptureType(value: Int): String? = when (value) {
        SCENE_STANDARD -> "Standard"
        SCENE_LANDSCAPE -> "Landscape"
        SCENE_PORTRAIT -> "Portrait"
        SCENE_NIGHT -> "Night Scene"
        else -> null
    }

    const val COLOR_SPACE_SRGB = 1
    const val COLOR_SPACE_UNCALIBRATED = 65535

    fun formatColorSpace(value: Int): String? = when (value) {
        COLOR_SPACE_SRGB -> "sRGB"
        COLOR_SPACE_UNCALIBRATED -> "Uncalibrated"
        else -> null
    }

    const val WHITE_BALANCE_AUTO = 0
    const val WHITE_BALANCE_MANUAL = 1

    fun formatWhiteBalance(value: Int): String? = when (value) {
        WHITE_BALANCE_AUTO -> "Auto"
        WHITE_BALANCE_MANUAL -> "Manual"
        else -> null
    }

    const val RESOLUTION_UNIT_NONE = 1
    const val RESOLUTION_UNIT_INCH = 2
    const val RESOLUTION_UNIT_CENTIMETER = 3

    fun formatResolutionUnit(value: Int): String? = when (value) {
        RESOLUTION_UNIT_NONE -> "None"
        RESOLUTION_UNIT_INCH -> "Inch"
        RESOLUTION_UNIT_CENTIMETER -> "Centimeter"
        else -> null
    }

    const val COMPRESSION_UNCOMPRESSED = 1
    const val COMPRESSION_JPEG = 6

    fun formatCompression(value: Int): String? = when (value) {
        COMPRESSION_UNCOMPRESSED -> "Uncompressed"
        COMPRESSION_JPEG -> "JPEG"
        else -> null
    }

}