package io.github.ismoy.imagepickerkmp.domain.utils.exif

/**
 * Centralized logging for EXIF extraction operations.
 */
internal object ExifLogger {
    
    private const val TAG = "iOS ExifDataExtractor"
    
    fun debug(message: String) {
        println(" $TAG: $message")
    }
    
    fun error(message: String, exception: Throwable? = null) {
        println(" $TAG: $message")
        exception?.printStackTrace()
    }
}
