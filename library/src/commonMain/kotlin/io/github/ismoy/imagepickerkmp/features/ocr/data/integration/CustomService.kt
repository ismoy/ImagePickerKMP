package io.github.ismoy.imagepickerkmp.features.ocr.data.integration

import io.github.ismoy.imagepickerkmp.features.ocr.data.network.KtorInstance
import io.github.ismoy.imagepickerkmp.features.ocr.data.providers.CloudOCRProvider
import io.github.ismoy.imagepickerkmp.features.ocr.model.CustomOCRResponse
import io.github.ismoy.imagepickerkmp.features.ocr.model.OCRResult
import io.github.ismoy.imagepickerkmp.features.ocr.model.OCRSpaceResponse
import io.github.ismoy.imagepickerkmp.features.ocr.model.RequestFormat
import io.github.ismoy.imagepickerkmp.features.ocr.utils.CloudOCRException
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.encodeBase64
import kotlinx.serialization.json.Json

/**
 * Common implementation of Custom OCR Service using KtorInstance
 * Supports different request formats for custom OCR providers across all platforms
 */
class CustomService(private val provider: CloudOCRProvider.Custom) {
    
    private val client = KtorInstance.client
    
    private val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
    }
    
    suspend fun extractText(imageData: ByteArray, mimeType: String? = null, fileName: String? = null): OCRResult {
        return try {
            val response = when (provider.requestFormat) {
                RequestFormat.MULTIPART_FORM -> performMultipartRequest(imageData, mimeType, fileName)
                RequestFormat.JSON_BASE64 -> performJsonBase64Request(imageData, mimeType, fileName)
                RequestFormat.JSON_URL -> throw CloudOCRException("JSON_URL format not yet implemented")
                RequestFormat.BINARY -> performBinaryRequest(imageData, mimeType, fileName)
            }
            
            parseResponse(response)
        } catch (e: Exception) {
            throw CloudOCRException("Custom OCR request failed: ${e.message}", e)
        }
    }
    
    private suspend fun performMultipartRequest(imageData: ByteArray, mimeType: String?, fileName: String?): HttpResponse {
        val contentType = mimeType ?: "application/octet-stream"
        val finalFileName = fileName?.let { name ->
            when {
                name.contains(".") -> name
                contentType == "application/pdf" -> "$name.pdf"
                contentType.startsWith("image/") -> "$name.${contentType.substringAfter("/")}"
                else -> name
            }
        } ?: run {
            when (contentType) {
                "image/jpeg" -> "image.jpeg"
                "image/jpg" -> "image.jpg"
                "image/png" -> "image.png"
                "image/gif" -> "image.gif"
                "image/bmp" -> "image.bmp"
                "image/tiff", "image/tif" -> "image.tiff"
                "image/webp" -> "image.webp"
                "application/pdf" -> "document.pdf"
                else -> when {
                    contentType.startsWith("image/") -> "image.${contentType.substringAfter("/")}"
                    else -> "file.bin"
                }
            }
        }
        
        val isOCRSpace = provider.baseUrl.contains("ocr.space") || provider.baseUrl.contains("api.ocr.space")
        
        return if (isOCRSpace) {
            client.submitFormWithBinaryData(
                url = provider.baseUrl,
                formData = formData {
                    append("file", imageData, Headers.build {
                        append(HttpHeaders.ContentType, contentType)
                        append(HttpHeaders.ContentDisposition, "filename=\"$finalFileName\"")
                    })
                    append("language", "eng")
                    append("isOverlayRequired", "false")
                    append("filetype", when (contentType.lowercase()) {
                        "application/pdf" -> "PDF"
                        "image/jpeg", "image/jpg" -> "JPG" 
                        "image/png" -> "PNG"
                        "image/gif" -> "GIF"
                        "image/bmp" -> "BMP"
                        "image/tiff", "image/tif" -> "TIF"
                        else -> "JPG"
                    })
                    provider.model?.let { model ->
                        append("OCREngine", model)
                    }
                }
            ) {
                provider.apiKey?.let { apiKey ->
                    header("apikey", apiKey)
                }
                provider.headers.forEach { (key, value) ->
                    header(key, value)
                }
            }
        } else {
            client.submitFormWithBinaryData(
                url = provider.baseUrl,
                formData = formData {
                    append("file", imageData, Headers.build {
                        append(HttpHeaders.ContentType, contentType)
                        append(HttpHeaders.ContentDisposition, "filename=$finalFileName")
                    })
                    provider.model?.let {
                        append("model", it) 
                    }
                }
            ) {
                provider.headers.forEach { (key, value) ->
                    header(key, value)
                }
                provider.apiKey?.let { apiKey ->
                    header("Authorization", "Bearer $apiKey")
                }
            }
        }
    }
    
    private suspend fun performJsonBase64Request(imageData: ByteArray, mimeType: String?, fileName: String?): HttpResponse {
        val base64Image = imageData.encodeBase64()
        
        val requestBody = buildMap<String, Any> {
            put("image", base64Image)
            provider.model?.let { put("model", it) }
            mimeType?.let { put("mimeType", it) }
            fileName?.let { put("fileName", it) }
        }
        
        return client.post(provider.baseUrl) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
            provider.headers.forEach { (key, value) ->
                header(key, value)
            }
            provider.apiKey?.let { apiKey ->
                header("Authorization", "Bearer $apiKey")
            }
        }
    }
    
    private suspend fun performBinaryRequest(imageData: ByteArray, mimeType: String?, fileName: String?): HttpResponse {
        val contentType = when (mimeType) {
            "image/jpeg" -> ContentType.Image.JPEG
            "image/png" -> ContentType.Image.PNG
            "image/gif" -> ContentType.Image.GIF
            "image/webp" -> ContentType.Image.Any
            "application/pdf" -> ContentType.Application.Pdf
            else -> ContentType.Application.OctetStream
        }
        
        return client.post(provider.baseUrl) {
            contentType(contentType)
            setBody(imageData)
            provider.headers.forEach { (key, value) ->
                header(key, value)
            }
            provider.apiKey?.let { apiKey ->
                header("Authorization", "Bearer $apiKey")
            }
            
            provider.model?.let { model ->
                header("X-Model", model)
            }
            mimeType?.let { mime ->
                header("X-Content-Type", mime)
            }
            fileName?.let { name ->
                header("X-Filename", name)
            }
        }
    }
    
    private suspend fun parseResponse(response: HttpResponse): OCRResult {
        val responseText = response.bodyAsText()
        
        return try {
            val isOCRSpace = provider.baseUrl.contains("ocr.space") || provider.baseUrl.contains("api.ocr.space")
            
            if (isOCRSpace) {
                val ocrSpaceResponse = json.decodeFromString<OCRSpaceResponse>(responseText)
                if (ocrSpaceResponse.isErroredOnProcessing == true || ocrSpaceResponse.oCRExitCode != "1") {
                    val errorMsg = ocrSpaceResponse.errorMessage?.joinToString("; ") ?: "OCR processing failed"
                    throw CloudOCRException("OCR.Space error: $errorMsg (Exit Code: ${ocrSpaceResponse.oCRExitCode})")
                }
                val rawText = ocrSpaceResponse.parsedResults?.firstOrNull()?.parsedText ?: ""
                val cleanedText = rawText
                    .replace("\\r\\n", "\n")
                    .replace("\\r", "\n")
                    .replace("\\n", "\n")
                    .lines()
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .joinToString("\n")
                
                val cleanLines = cleanedText.split("\n").filter { it.isNotEmpty() }
                val result = OCRResult(
                    text = cleanedText,
                    lines = cleanLines,
                    confidence = if (ocrSpaceResponse.oCRExitCode == "1") 1.0f else 0.5f,
                    metadata = mapOf(
                        "provider" to "OCR.Space",
                        "status" to "success",
                        "exitCode" to (ocrSpaceResponse.oCRExitCode.ifEmpty { "unknown" }),
                        "processingTime" to "${ocrSpaceResponse.processingTimeInMilliseconds.orEmpty().ifEmpty { "unknown" }}ms",
                        "url" to provider.baseUrl,
                        "totalLines" to cleanLines.size,
                        "rawTextLength" to rawText.length,
                        "cleanedTextLength" to cleanedText.length
                    )
                )
                result
            } else {
                val jsonResponse = json.decodeFromString<CustomOCRResponse>(responseText)
                val rawText = jsonResponse.text ?: jsonResponse.result ?: jsonResponse.content ?: ""
                val cleanedText = rawText
                    .replace("\\r\\n", "\n")
                    .replace("\\r", "\n") 
                    .replace("\\n", "\n")
                    .lines()
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .joinToString("\n")
                
                val cleanLines = cleanedText.split("\n").filter { it.isNotEmpty() }
                
                OCRResult(
                    text = cleanedText,
                    lines = cleanLines,
                    confidence = jsonResponse.confidence ?: 1.0f,
                    metadata = mapOf(
                        "provider" to provider.name,
                        "status" to "success",
                        "url" to provider.baseUrl,
                        "totalLines" to cleanLines.size,
                        "hasError" to (jsonResponse.error != null),
                        "message" to (jsonResponse.message ?: "success")
                    )
                )
            }
        } catch (_: Exception) {
            val cleanedText = responseText
                .replace("\\r\\n", "\n")
                .replace("\\r", "\n")
                .replace("\\n", "\n")
                .lines()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .joinToString("\n")
            
            val cleanLines = cleanedText.split("\n").filter { it.isNotEmpty() }
            
            OCRResult(
                text = cleanedText,
                lines = cleanLines,
                confidence = 1.0f,
                metadata = mapOf(
                    "provider" to provider.name,
                    "status" to "success",
                    "url" to provider.baseUrl,
                    "totalLines" to cleanLines.size,
                    "parseError" to "Failed to parse JSON, treated as plain text",
                    "originalLength" to responseText.length
                )
            )
        }
    }
}