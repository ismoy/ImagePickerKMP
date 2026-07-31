package io.github.ismoy.imagepickerkmp.logger

import io.github.ismoy.imagepickerkmp.core.logger.ImagePickerConsoleLogger
import io.github.ismoy.imagepickerkmp.core.logger.LogLevel
import io.github.ismoy.imagepickerkmp.core.logger.MediaLogger

internal object PhotoLogger {
    private const val TAG = "ImagePicker"
    private val logger: MediaLogger = ImagePickerConsoleLogger().also {
        it.minimumLevel = LogLevel.DEBUG
    }
    var debugMode: Boolean = true

    fun debug(message: String) {
        if (debugMode) logger.debug(TAG, message)
    }
    fun info(message: String) {
        if (debugMode) logger.info(TAG, message)
    }
    fun error(message: String, throwable: Throwable? = null) {
        logger.error(TAG, message, throwable)
    }
    fun warning(message: String, throwable: Throwable? = null) {
        if (debugMode) logger.warning(TAG, message, throwable)
    }
}
