package io.github.ismoy.imagepickerkmp.features.ocr.utils

/**
 * Base exception for OCR-related errors
 */
open class OCRException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Exception thrown when cloud OCR analysis fails
 */
open class CloudOCRException(message: String, cause: Throwable? = null) : OCRException(message, cause)

/**
 * Exception thrown when API key is invalid or missing
 */
class InvalidAPIKeyException(message: String = "Invalid or missing API key") : OCRException(message)
