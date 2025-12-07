package io.github.ismoy.imagepickerkmp.features.ocr.utils

/**
 * Exception thrown when attempting to use OCR features that require an API key
 * but no valid API key has been provided.
 * 
 * This exception is typically thrown when:
 * - User calls experimental OCR functions without configuring an API key
 * - The provided API key is empty, null, or invalid
 * - The API key doesn't have sufficient permissions for the requested operation
 */
class MissingAPIKeyException(
    message: String = "API key is required for OCR functionality. " +
            "Please provide a valid API key from your chosen cloud provider " +
            "(Google Gemini, OpenAI, Claude, etc.) to use OCR features.",
    cause: Throwable? = null
) : CloudOCRException(message, cause) {
    
    companion object {
        /**
         * Creates an exception for when an empty API key is provided
         */
        fun emptyApiKey(providerName: String = "cloud OCR"): MissingAPIKeyException {
            return MissingAPIKeyException(
                "Empty or blank API key provided for $providerName. " +
                "Please provide a valid, non-empty API key."
            )
        }
    }
}
