package io.github.ismoy.imagepickerkmp.domain.utils.exif

/**
 * Formatters and converters for EXIF data values.
 */
internal object ExifFormatters {
    
    /**
     * Formats EXIF date string to ISO format.
     * Converts "yyyy:MM:dd HH:mm:ss" to "yyyy-MM-ddTHH:mm:ss"
     */
    fun formatExifDate(dateString: String): String = runCatching {
        val parts = dateString.split(":", " ")
        if (parts.size >= 4) {
            "${parts[0]}-${parts[1]}-${parts[2]}T${parts[3]}"
        } else {
            dateString
        }
    }.getOrElse { dateString }
    
    /**
     * Converts orientation integer to human readable description.
     */
    fun getOrientationDescription(orientation: Int): String = when (orientation) {
        1 -> "Normal [1]"
        2 -> "Flip Horizontal [2]"
        3 -> "Rotate 180° [3]"
        4 -> "Flip Vertical [4]"
        5 -> "Transpose [5]"
        6 -> "Rotate 90° CW [6]"
        7 -> "Transverse [7]"
        8 -> "Rotate 90° CCW [8]"
        else -> "Unknown [$orientation]"
    }
    
    /**
     * Converts flash value to human readable description.
     */
    fun getFlashDescription(flash: Int): String = when (flash) {
        0 -> "No Flash"
        1 -> "Flash Fired"
        5 -> "Strobe Return Light Not Detected"
        7 -> "Strobe Return Light Detected"
        9 -> "Flash Fired, Compulsory Flash Mode"
        16 -> "Flash Did Not Fire, Compulsory Flash Mode"
        24 -> "Flash Did Not Fire, Auto Mode"
        25 -> "Flash Fired, Auto Mode"
        32 -> "No Flash Function"
        65 -> "Flash Fired, Red-Eye Reduction Mode"
        else -> "Flash: $flash"
    }
}
