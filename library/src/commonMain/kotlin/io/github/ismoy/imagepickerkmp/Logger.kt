package io.github.ismoy.imagepickerkmp

/**
 * Interface for logging messages within the ImagePicker library.
 *
 * Implementations can provide custom logging mechanisms for debugging or analytics.
 */
interface ImagePickerLogger {
    fun log(message: String)
}

/**
 * Default implementation of [ImagePickerLogger] that logs messages to the standard output.
 */
object DefaultLogger : ImagePickerLogger {
    override fun log(message: String) {
        println(message)
    }
}
