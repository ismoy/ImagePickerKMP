package io.github.ismoy.imagepickerkmp

/**
 * Exception thrown when an error occurs during photo capture or processing.
 *
 * Used to signal camera or image processing failures in the ImagePicker library.
 */
class PhotoCaptureException(message: String) : Exception(message)
