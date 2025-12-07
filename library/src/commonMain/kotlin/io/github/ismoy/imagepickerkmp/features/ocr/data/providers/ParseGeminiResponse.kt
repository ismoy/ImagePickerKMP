package io.github.ismoy.imagepickerkmp.features.ocr.data.providers

import io.github.ismoy.imagepickerkmp.features.ocr.model.OCRResult
import io.github.ismoy.imagepickerkmp.features.ocr.utils.CloudOCRException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

 fun parseGeminiResponse(response: String,model: String): OCRResult {
    try {
        val jsonResponse = Json.parseToJsonElement(response).jsonObject
        val candidates = jsonResponse["candidates"]?.jsonArray

        if (candidates.isNullOrEmpty()) {
            throw CloudOCRException("No text found in image")
        }

        val firstCandidate = candidates[0].jsonObject
        val content = firstCandidate["content"]?.jsonObject
        val parts = content?.get("parts")?.jsonArray

        if (parts.isNullOrEmpty()) {
            throw CloudOCRException("No content in API response")
        }

        val textContent = parts[0].jsonObject["text"]?.jsonPrimitive?.content
            ?: throw CloudOCRException("No text content in response")

        val cleanedContent = textContent
            .removePrefix("```json")
            .removeSuffix("```")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        return try {
            val parsedJson = parseJsonWithRecovery(cleanedContent)

            val extractedText = extractAllTextFromGeminiJson(parsedJson)
            val textLines = extractedText.split("\n").filter { it.isNotBlank() }
            val language = parsedJson["language"]?.jsonPrimitive?.content

            val metadata = buildMap<String, Any> {
                put("source", "Google Gemini")
                put("model", model)
                put("format", "structured_json")
                put("text_length", extractedText.length)
                put("lines_count", textLines.size)

                language?.let { put("detected_language", it) }
                parsedJson["document_type"]?.jsonPrimitive?.content?.let {
                    put("document_type", it)
                }

                put("gemini_structured_data", parsedJson.toMap())

                jsonResponse["usageMetadata"]?.jsonObject?.let { usage ->
                    usage["promptTokenCount"]?.jsonPrimitive?.intOrNull?.let {
                        put("prompt_tokens", it)
                    }
                    usage["candidatesTokenCount"]?.jsonPrimitive?.intOrNull?.let {
                        put("completion_tokens", it)
                    }
                    usage["totalTokenCount"]?.jsonPrimitive?.intOrNull?.let {
                        put("total_tokens", it)
                    }
                }
            }

            OCRResult(
                text = extractedText,
                lines = textLines,
                language = language,
                confidence = null,
                metadata = metadata
            )

        } catch (jsonException: Exception) {
            val lines = cleanedContent.split("\n").filter { it.isNotBlank() }

            val partialText = extractPartialTextFromTruncatedJson(cleanedContent)

            val isTruncated = jsonException.message?.let { msg ->
                msg.contains("EOF") || msg.contains("quotation mark") ||
                        msg.contains("Unexpected end") || cleanedContent.length > 3000
            } ?: false

            val errorMetadata = mutableMapOf<String, Any>(
                "source" to "Google Gemini",
                "model" to model,
                "format" to "plain_text",
                "parse_error" to (jsonException.message ?: "Unknown parsing error"),
                "is_truncated" to isTruncated,
                "content_length" to cleanedContent.length
            )

            if (isTruncated) {
                errorMetadata["error_type"] = "response_truncated"
                errorMetadata["suggestion"] = "Consider reducing file size, using a simpler prompt, or increasing maxTokens"
                errorMetadata["partial_text_extracted"] = partialText.isNotEmpty()
            }

            errorMetadata["raw_response"] = if (cleanedContent.length > 1000) {
                cleanedContent.take(1000) + "...[truncated]"
            } else {
                cleanedContent
            }

            OCRResult(
                text = partialText.ifEmpty { cleanedContent },
                lines = if (partialText.isNotEmpty()) partialText.split("\n").filter { it.isNotBlank() } else lines,
                language = null,
                confidence = null,
                metadata = errorMetadata
            )
        }

    } catch (e: Exception) {
        throw CloudOCRException("Failed to parse Gemini response: ${e.message}", e)
    }
}

private fun extractAllTextFromGeminiJson(jsonObject: JsonObject): String {
    val textValues = mutableListOf<String>()

    fun extractFromElement(element: JsonElement) {
        when (element) {
            is JsonPrimitive -> {
                if (element.isString) {
                    val text = element.content.trim()
                    if (text.isNotEmpty()) {
                        textValues.add(text)
                    }
                }
            }
            is JsonObject -> {
                element.values.forEach { extractFromElement(it) }
            }
            is JsonArray -> {
                element.forEach { extractFromElement(it) }
            }
        }
    }

    extractFromElement(jsonObject)
    return textValues.joinToString("\n")
}

private fun extractPartialTextFromTruncatedJson(jsonText: String): String {
    val textBuilder = StringBuilder()

    val textPatterns = listOf(
        "\"text\":\\s*\"([^\"]+)\"",
        "\"description\":\\s*\"([^\"]+)\"",
        "\"product_name\":\\s*\"([^\"]+)\"",
        "\"name\":\\s*\"([^\"]+)\"",
        "\"title\":\\s*\"([^\"]+)\"",
        "\"content\":\\s*\"([^\"]+)\""
    )

    textPatterns.forEach { pattern ->
        val regex = Regex(pattern)
        regex.findAll(jsonText).forEach { match ->
            if (match.groupValues.size > 1) {
                textBuilder.appendLine(match.groupValues[1])
            }
        }
    }

    return textBuilder.toString().trim()
}

private fun parseJsonWithRecovery(jsonText: String): JsonObject {
    return try {
        Json.parseToJsonElement(jsonText).jsonObject
    } catch (e: Exception) {
        if (e.message?.contains("EOF") == true ||
            e.message?.contains("quotation mark") == true ||
            e.message?.contains("Unexpected end") == true) {

            val recoveredJson = tryRecoverTruncatedJson(jsonText)
            if (recoveredJson != null) {
                return recoveredJson
            }
        }
        throw CloudOCRException("Failed to parse JSON response. The response may be incomplete or truncated. Original error: ${e.message}", e)
    }
}