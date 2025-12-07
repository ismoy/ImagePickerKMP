package io.github.ismoy.imagepickerkmp.features.ocr.data.providers

import io.github.ismoy.imagepickerkmp.features.ocr.model.OCRRequestConfig
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

 fun createGeminiRequestBody(base64Image: String, config: OCRRequestConfig): String {
    val prompt = config.prompt ?: buildGeminiOCRPrompt(config.language)

    val json = buildJsonObject {
        putJsonArray("contents") {
            addJsonObject {
                putJsonArray("parts") {
                    addJsonObject {
                        putJsonObject("inlineData") {
                            val detectedMimeType = config.mimeType ?: "image/jpeg"
                            put("mimeType", detectedMimeType)
                            put("data", base64Image)
                        }
                    }
                    addJsonObject {
                        put("text", prompt)
                    }
                }
            }
        }
        putJsonObject("generationConfig") {
            put("temperature", config.temperature)
            val maxTokens = if (config.mimeType?.equals("application/pdf", ignoreCase = true) == true) {
                maxOf(config.maxTokens, 8000)
            } else {
                config.maxTokens
            }
            put("maxOutputTokens", maxTokens)
            put("topP", 1.0)
            put("topK", 1)
        }
    }

    return json.toString()
}

private fun buildGeminiOCRPrompt(language: String?): String {
    val languageHint = if (language != null) {
        "The text is likely in $language. "
    } else ""

    return """
            Analyze this document (image or PDF) and extract ALL visible text and information. 

            CRITICAL: Adapt the JSON structure to match what you actually see. Don't force product format for non-products.
            $languageHint
            EXAMPLES BY IMAGE TYPE:

            For PRODUCTS/RECEIPTS:
            {
              "product_info": {
                "product_name": "exact name",
                "barcode": "exact_digits_as_string"
              },
              "pricing_details": { "price": "amount", "weight": "weight" },
              "document_type": "product_label",
              "language": "detected_language"
            }

            For DOCUMENTS/TEXT:
            {
              "text_content": [
                {"text": "Title or header", "type": "title"},
                {"text": "Body paragraph", "type": "paragraph"}
              ],
              "document_type": "document",
              "language": "detected_language"
            }

            For SIGNS/POSTERS:
            {
              "main_text": "Primary message",
              "secondary_text": "Additional info",
              "contact_info": {"phone": "number", "email": "address"},
              "document_type": "poster",
              "language": "detected_language"  
            }

            For GENERAL TEXT:
            {
              "extracted_text": "All visible text exactly as shown",
              "document_type": "general_text", 
              "language": "detected_language"
            }

            RULES:
            1. NEVER force product structure for non-products
            2. Extract ONLY what you can see
            3. Barcodes ALWAYS as strings: "123456789"
            4. Use null for missing data, never "null" string
            5. Adapt structure to content type
        """.trimIndent()
}
