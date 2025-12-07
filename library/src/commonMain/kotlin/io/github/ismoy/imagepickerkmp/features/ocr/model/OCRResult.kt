package io.github.ismoy.imagepickerkmp.features.ocr.model

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Represents the result of an OCR analysis operation
 *
 * @param text Complete extracted text from the image
 * @param lines List of individual text lines found in the image
 * @param language Detected language of the text (if available)
 * @param confidence Overall confidence level of the OCR analysis (0.0 to 1.0)
 * @param metadata Additional analysis data (bounding boxes, formatting info, etc.)
 * @property getMetadataAsJson Returns metadata as a JSON string
 */
data class OCRResult(
    val text: String,
    val lines: List<String>,
    val language: String? = null,
    val confidence: Float? = null,
    val metadata: Map<String, Any>? = null
) {
    fun getMetadataAsJson(): String {
        return if (metadata != null) {
            convertToJsonElement(metadata).toString()
        } else {
            "{}"
        }
    }

    private fun convertToJsonElement(value: Any?): JsonElement {
        return when (value) {
            null -> JsonNull
            is String -> JsonPrimitive(value)
            is Number -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is Map<*, *> -> JsonObject(
                value.entries.associate { (k, v) ->
                    k.toString() to convertToJsonElement(v)
                }
            )
            is List<*> -> JsonArray(
                value.map { convertToJsonElement(it) }
            )
            else -> JsonPrimitive(value.toString())
        }
    }
}