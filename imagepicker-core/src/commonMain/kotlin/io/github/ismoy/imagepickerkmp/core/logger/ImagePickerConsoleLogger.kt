package io.github.ismoy.imagepickerkmp.core.logger

class ImagePickerConsoleLogger : MediaLogger {

    var minimumLevel: LogLevel = LogLevel.DEBUG

    private fun enabled(level: LogLevel): Boolean =
        minimumLevel.priority <= level.priority

    override fun debug(tag: String, message: String) {
        if (enabled(LogLevel.DEBUG)) println("[ImagePicker DEBUG] $message")
    }

    override fun info(tag: String, message: String) {
        if (enabled(LogLevel.INFO)) println("[ImagePicker] $message")
    }

    override fun warning(tag: String, message: String, throwable: Throwable?) {
        if (enabled(LogLevel.WARNING)) {
            println("[ImagePicker WARN] $message")
            throwable?.printStackTrace()
        }
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        if (enabled(LogLevel.ERROR)) {
            println("[ImagePicker ERROR] $message")
            throwable?.printStackTrace()
        }
    }
}
