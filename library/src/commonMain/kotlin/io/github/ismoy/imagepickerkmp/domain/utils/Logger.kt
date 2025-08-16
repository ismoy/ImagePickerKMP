package io.github.ismoy.imagepickerkmp.domain.utils

/**
 * Interface for logging messages within the ImagePicker library.
 * 
 * SOLID: Interface Segregation - Focused interface for logging only
 * SOLID: Open/Closed - Open for extension (new implementations), closed for modification
 */
interface ImagePickerLogger {
    fun log(message: String)
    fun logError(message: String, throwable: Throwable? = null)
    fun logDebug(message: String)
}

/**
 * Default implementation of [ImagePickerLogger] that logs messages to the standard output.
 * 
 * SOLID: Single Responsibility - Only handles console logging
 */
object DefaultLogger : ImagePickerLogger {
    override fun log(message: String) {
        println("[ImagePicker] $message")
    }
    
    override fun logError(message: String, throwable: Throwable?) {
        println("[ImagePicker ERROR] $message")
        throwable?.printStackTrace()
    }
    
    override fun logDebug(message: String) {
        println("[ImagePicker DEBUG] $message")
    }
}
