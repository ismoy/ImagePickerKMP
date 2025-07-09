package io.github.ismoy.imagepickerkmp

interface ImagePickerLogger {
    fun log(message: String)
}

object DefaultLogger : ImagePickerLogger {
    override fun log(message: String) {
        println(message)
    }
} 