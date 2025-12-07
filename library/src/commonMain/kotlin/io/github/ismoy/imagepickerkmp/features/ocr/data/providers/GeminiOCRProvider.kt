package io.github.ismoy.imagepickerkmp.features.ocr.data.providers

import io.github.ismoy.imagepickerkmp.features.ocr.utils.CloudOCRException
import io.github.ismoy.imagepickerkmp.features.ocr.utils.InvalidAPIKeyException
import io.github.ismoy.imagepickerkmp.features.ocr.model.OCRResult
import io.github.ismoy.imagepickerkmp.features.ocr.model.OCRRequestConfig
import io.github.ismoy.imagepickerkmp.features.ocr.data.network.KtorInstance
import io.github.ismoy.imagepickerkmp.features.ocr.model.ProviderInfo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.timeout
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.encodeBase64
import kotlinx.serialization.json.*

/**
 * Gemini OCR provider that uses Google's Gemini API for text extraction from images and PDFs.
 * Uses the centralized KtorInstance for HTTP client management.
 * 
 * @param apiKey The Google AI Studio API key
 * @param model The Gemini model to use (default: gemini-2.5-flash)
 * @param httpClient Optional HTTP client. If not provided, uses KtorInstance.geminiClient
 */
class GeminiOCRProvider(
    private val apiKey: String,
    private val model: String = "gemini-2.5-flash",
    private val httpClient: HttpClient = KtorInstance.geminiClient
) : CloudOCRProviderInterface {
    
    override val providerName = "Google Gemini ($model)"
    
    private companion object {
        const val GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models"
    }
    
    override suspend fun extractText(imageData: ByteArray, config: OCRRequestConfig): OCRResult {
        try {
            validateFile(imageData, config.mimeType)
            
            val base64Image = imageData.encodeBase64()
            val requestBody = createGeminiRequestBody(base64Image, config)
            
            val response: HttpResponse = httpClient.post("$GEMINI_API_URL/$model:generateContent") {
                parameter("key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(requestBody)
                
                timeout {
                    requestTimeoutMillis = config.timeout
                    connectTimeoutMillis = 30_000L
                    socketTimeoutMillis = config.timeout
                }
            }
            
            when (response.status.value) {
                401, 403 -> throw InvalidAPIKeyException("Invalid Gemini API key or insufficient permissions")
                400 -> {
                    val errorBody = try { response.body<String>() } catch (e: Exception) { "Unable to read error response" }
                    val mimeType = config.mimeType ?: "image/jpeg"
                    throw CloudOCRException("Bad request (400). This might be due to unsupported file format, file too large, or invalid request format. MimeType: $mimeType, Error: $errorBody")
                }
                429 -> throw CloudOCRException("Rate limit exceeded for Gemini API")
                !in 200..299 -> {
                    val errorBody = try { response.body<String>() } catch (e: Exception) { "Unable to read error response" }
                    throw CloudOCRException("Gemini API request failed with status code: ${response.status.value}. Error: $errorBody")
                }
            }
            
            val responseText = response.body<String>()
            return parseGeminiResponse(responseText,model)
            
        } catch (e: InvalidAPIKeyException) {
            throw e
        } catch (e: CloudOCRException) {
            throw e
        } catch (e: Exception) {
            throw CloudOCRException("Gemini API request failed: ${e.message}", e)
        }
    }

    private fun JsonElement.toMap(): Any = when (this) {
        is JsonObject -> mapValues { it.value.toMap() }
        is JsonArray -> map { it.toMap() }
        is JsonPrimitive -> when {
            this.booleanOrNull != null -> this.boolean
            this.intOrNull != null -> this.int
            this.doubleOrNull != null -> this.double
            this.isString -> this.content
            else -> this.content
        }
    }

    override suspend fun isAvailable(): Boolean {
        return try {
            val response = httpClient.get("$GEMINI_API_URL/$model") {
                parameter("key", apiKey)
                timeout {
                    requestTimeoutMillis = 5000L
                }
            }
            response.status.value in 200..299
        } catch (e: Exception) {
            false
        }
    }
    
    override fun getProviderInfo(): ProviderInfo {
        return ProviderInfo(
            name = "Google Gemini ($model)",
            description = "Advanced multimodal AI from Google with excellent OCR capabilities",
            supportsMultipleLanguages = true,
            supportsBarcodes = true,
            supportsHandwriting = true,
            maxImageSize = 20 * 1024 * 1024,
            rateLimit = "60 requests per minute (free tier)",
            pricing = "Free tier: 15 requests/minute, Paid: $0.125 per 1M tokens",
            documentationUrl = "https://ai.google.dev/docs"
        )
    }
    
    override fun close() {}
}
