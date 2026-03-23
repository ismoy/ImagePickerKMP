package io.github.ismoy.imagepickerkmp.domain.utils.exif

import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger

internal object ExifLogger {

    fun debug(message: String) {
        DefaultLogger.logDebug(message)
    }

    fun error(message: String, exception: Throwable? = null) {
        DefaultLogger.logError(message, exception)
    }
}
