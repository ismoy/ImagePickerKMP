package io.github.ismoy.imagepickerkmp.features.ocr.annotations

/**
 * Marks declarations related to OCR functionality as experimental API.
 * These features require external API keys and are subject to third-party service limitations.
 * 
 * Features marked with this annotation:
 * - Require valid API keys from cloud providers (Google Gemini, OpenAI, etc.)
 * - May have rate limits and usage costs
 * - Are dependent on external service availability
 * 
 * @since 1.0.0
 */
@Suppress("ExperimentalAnnotationRetention")
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This OCR API is experimental and requires external API keys. " +
            "Please ensure you have configured a valid API key for your chosen provider. " +
            "Usage may be subject to rate limits and costs from the external service provider."
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR
)
annotation class ExperimentalOCRApi
