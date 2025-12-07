package io.github.ismoy.imagepickerkmp.features.ocr.model

/**
 * Request format for custom OCR providers
 */
enum class RequestFormat {
    /**
     * Multipart form data (standard for file uploads)
     */
    MULTIPART_FORM,

    /**
     * Base64 encoded image in JSON body
     */
    JSON_BASE64,

    /**
     * JSON with image URL reference
     */
    JSON_URL,

    /**
     * Binary image data with specific headers
     */
    BINARY
}