package io.github.ismoy.imagepickerkmp.domain.exceptions

/**
 * Base exception for ImagePicker operations.
 * 
 * SOLID: Single Responsibility - Only handles exception information
 * SOLID: Open/Closed - Can be extended for specific error types
 */
sealed class ImagePickerException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Exception thrown when an error occurs during photo capture.
 */
class PhotoCaptureException(message: String, cause: Throwable? = null) : ImagePickerException(message, cause)

/**
 * Exception thrown when permission is denied.
 */
class PermissionDeniedException(message: String, cause: Throwable? = null) : ImagePickerException(message, cause)

/**
 * Exception thrown when image processing fails.
 */
class ImageProcessingException(message: String, cause: Throwable? = null) : ImagePickerException(message, cause)
