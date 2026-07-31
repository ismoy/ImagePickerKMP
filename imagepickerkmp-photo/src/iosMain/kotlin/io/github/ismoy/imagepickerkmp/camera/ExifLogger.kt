package io.github.ismoy.imagepickerkmp.camera

import io.github.ismoy.imagepickerkmp.logger.PhotoLogger

internal object ExifLogger {

    fun debug(message: String) {
        PhotoLogger.debug(message)
    }

    fun error(message: String, exception: Throwable? = null) {
        PhotoLogger.error(message, exception)
    }
}
