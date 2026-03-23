package io.github.ismoy.imagepickerkmp.domain.utils

internal interface ImagePickerLogger {
    fun log(message: String)
    fun logError(message: String, throwable: Throwable? = null)
    fun logDebug(message: String)
}


internal object DefaultLogger : ImagePickerLogger {
    var debugMode: Boolean = false

    override fun log(message: String) {
        if (debugMode) println("[ImagePicker] $message")
    }

    override fun logError(message: String, throwable: Throwable?) {
        if (debugMode) {
            println("[ImagePicker ERROR] $message")
            throwable?.printStackTrace()
        }
    }

    override fun logDebug(message: String) {
        if (debugMode) println("[ImagePicker DEBUG] $message")
    }
}
