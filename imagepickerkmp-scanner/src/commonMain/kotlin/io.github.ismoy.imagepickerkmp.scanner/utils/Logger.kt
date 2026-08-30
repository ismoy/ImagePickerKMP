package io.github.ismoy.imagepickerkmp.scanner.utils

import io.github.ismoy.imagepickerkmp.core.logger.ImagePickerConsoleLogger
import io.github.ismoy.imagepickerkmp.core.logger.MediaLogger

object LoggerFactory {
    private var logger: MediaLogger = ImagePickerConsoleLogger()

    fun getLogger(): MediaLogger = logger

    fun setLogger(newLogger: MediaLogger) {
        logger = newLogger
    }

}

fun MediaLogger.scanner(message: String, throwable: Throwable? = null) {
    if (throwable != null) {
        error("Scanner", message, throwable)
    } else {
        info("Scanner", message)
    }
}

fun MediaLogger.camera(message: String, throwable: Throwable? = null) {
    if (throwable != null) {
        error("Camera", message, throwable)
    } else {
        info("Camera", message)
    }
}

fun MediaLogger.security(message: String, throwable: Throwable? = null) {
    warning("Security", message, throwable)
}

fun MediaLogger.permission(message: String, throwable: Throwable? = null) {
    if (throwable != null) {
        error("Permission", message, throwable)
    } else {
        info("Permission", message)
    }
}
