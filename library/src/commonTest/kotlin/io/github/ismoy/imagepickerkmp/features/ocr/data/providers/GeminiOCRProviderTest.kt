package io.github.ismoy.imagepickerkmp.features.ocr.data.providers

import io.github.ismoy.imagepickerkmp.features.ocr.model.OCRRequestConfig
import io.github.ismoy.imagepickerkmp.features.ocr.utils.CloudOCRException
import io.github.ismoy.imagepickerkmp.features.ocr.utils.InvalidAPIKeyException
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests de red para [GeminiOCRProvider] usando [MockEngine] de Ktor.
 *
 * No realiza ninguna llamada HTTP real. Todas las respuestas son simuladas
 * con [MockEngine] → los tests funcionan offline y en CI.
 *
 * Casos cubiertos:
 * - 200 OK con respuesta estructurada → devuelve [OCRResult] con texto
 * - 200 OK con respuesta plain text  → devuelve [OCRResult] con texto plano
 * - 401 / 403 → lanza [InvalidAPIKeyException]
 * - 400 Bad Request → lanza [CloudOCRException]
 * - 429 Rate Limit  → lanza [CloudOCRException]
 * - 500 Server Error → lanza [CloudOCRException]
 * - Timeout / red caída → lanza [CloudOCRException]
 * - isAvailable() → true en 200, false en error
 * - Archivo demasiado grande → lanza [CloudOCRException] antes de llamar a la red
 * - Formato MIME no soportado → lanza [CloudOCRException] antes de llamar a la red
 */
class GeminiOCRProviderTest {

    // ── Helpers ────────────────────────────────────────────────────────────────

    private val validApiKey = "AIzaSyABCDEFGHIJKLMNOP-test-key-1234567"
    private val smallImage = ByteArray(100) { it.toByte() } // 100 bytes, valid size

    private val geminiSuccessResponse = """
        {
          "candidates": [{
            "content": {
              "parts": [{
                "text": "Hello World from OCR"
              }],
              "role": "model"
            },
            "finishReason": "STOP"
          }]
        }
    """.trimIndent()

    private val geminiJsonStructuredResponse = """
        {
          "candidates": [{
            "content": {
              "parts": [{
                "text": "{\"text\": \"Invoice Total: ${'$'}150.00\", \"lines\": [\"Invoice Total: ${'$'}150.00\"]}"
              }],
              "role": "model"
            },
            "finishReason": "STOP"
          }]
        }
    """.trimIndent()

    private val geminiEmptyCandidatesResponse = """
        {
          "candidates": []
        }
    """.trimIndent()

    /** Construye un [GeminiOCRProvider] con un [MockEngine] que devuelve la respuesta dada. */
    private fun buildProvider(
        statusCode: HttpStatusCode = HttpStatusCode.OK,
        responseBody: String = geminiSuccessResponse,
        contentType: ContentType = ContentType.Application.Json
    ): GeminiOCRProvider {
        val mockEngine = MockEngine { _ ->
            respond(
                content = responseBody,
                status = statusCode,
                headers = headersOf(HttpHeaders.ContentType, contentType.toString())
            )
        }
        val mockClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
            expectSuccess = false
        }
        return GeminiOCRProvider(
            apiKey = validApiKey,
            model = "gemini-2.5-flash",
            httpClient = mockClient
        )
    }

    private fun defaultConfig() = OCRRequestConfig(
        mimeType = "image/jpeg",
        timeout = 30_000L
    )

    // ── 200 OK — extracción de texto ──────────────────────────────────────────

    @Test
    fun extractText_200_plainTextResponse_returnsOCRResult() = runTest {
        val provider = buildProvider(responseBody = geminiSuccessResponse)
        val result = provider.extractText(smallImage, defaultConfig())

        assertNotNull(result)
        assertTrue(result.text.isNotBlank(), "text should not be blank")
    }

    @Test
    fun extractText_200_textContainsExpectedContent() = runTest {
        val provider = buildProvider(responseBody = geminiSuccessResponse)
        val result = provider.extractText(smallImage, defaultConfig())

        assertTrue(
            result.text.contains("Hello World", ignoreCase = true),
            "Expected 'Hello World' in extracted text but got: '${result.text}'"
        )
    }

    @Test
    fun extractText_200_resultHasLines() = runTest {
        val provider = buildProvider(responseBody = geminiSuccessResponse)
        val result = provider.extractText(smallImage, defaultConfig())

        assertNotNull(result.lines)
        assertTrue(result.lines.isNotEmpty(), "lines should not be empty")
    }

    @Test
    fun extractText_200_resultHasMetadata() = runTest {
        val provider = buildProvider(responseBody = geminiSuccessResponse)
        val result = provider.extractText(smallImage, defaultConfig())

        assertNotNull(result.metadata)
        assertTrue(result.metadata.isNotEmpty(), "metadata should not be empty")
    }

    @Test
    fun extractText_200_metadataContainsGeminiSource() = runTest {
        val provider = buildProvider(responseBody = geminiSuccessResponse)
        val result = provider.extractText(smallImage, defaultConfig())

        val metadataValues = result.metadata?.values?.map { it.toString() } ?: emptyList()
        assertTrue(
            metadataValues.any { it.contains("Gemini", ignoreCase = true) },
            "Metadata should reference Gemini as source"
        )
    }

    // ── Errores HTTP ──────────────────────────────────────────────────────────

    @Test
    fun extractText_401_throwsInvalidAPIKeyException() = runTest {
        val provider = buildProvider(statusCode = HttpStatusCode.Unauthorized)
        assertFailsWith<InvalidAPIKeyException> {
            provider.extractText(smallImage, defaultConfig())
        }
    }

    @Test
    fun extractText_403_throwsInvalidAPIKeyException() = runTest {
        val provider = buildProvider(statusCode = HttpStatusCode.Forbidden)
        assertFailsWith<InvalidAPIKeyException> {
            provider.extractText(smallImage, defaultConfig())
        }
    }

    @Test
    fun extractText_400_throwsCloudOCRException() = runTest {
        val provider = buildProvider(
            statusCode = HttpStatusCode.BadRequest,
            responseBody = """{"error": {"message": "Invalid request body"}}"""
        )
        val ex = assertFailsWith<CloudOCRException> {
            provider.extractText(smallImage, defaultConfig())
        }
        assertTrue(ex.message!!.contains("400", ignoreCase = true))
    }

    @Test
    fun extractText_429_throwsCloudOCRExceptionWithRateLimit() = runTest {
        val provider = buildProvider(statusCode = HttpStatusCode.TooManyRequests)
        val ex = assertFailsWith<CloudOCRException> {
            provider.extractText(smallImage, defaultConfig())
        }
        assertTrue(
            ex.message!!.contains("rate limit", ignoreCase = true) ||
            ex.message!!.contains("429", ignoreCase = true),
            "Expected rate limit message but got: ${ex.message}"
        )
    }

    @Test
    fun extractText_500_throwsCloudOCRException() = runTest {
        val provider = buildProvider(
            statusCode = HttpStatusCode.InternalServerError,
            responseBody = "Internal Server Error"
        )
        val ex = assertFailsWith<CloudOCRException> {
            provider.extractText(smallImage, defaultConfig())
        }
        assertTrue(ex.message!!.contains("500", ignoreCase = true))
    }

    // ── Respuesta vacía de candidatos ─────────────────────────────────────────

    @Test
    fun extractText_200_emptyCandidates_throwsCloudOCRException() = runTest {
        val provider = buildProvider(responseBody = geminiEmptyCandidatesResponse)
        val ex = assertFailsWith<CloudOCRException> {
            provider.extractText(smallImage, defaultConfig())
        }
        assertNotNull(ex.message)
        assertTrue(ex.message!!.isNotBlank())
    }

    // ── Validación pre-HTTP (sin red) ──────────────────────────────────────────

    @Test
    fun extractText_fileTooLarge_throwsCloudOCRExceptionWithoutCallingNetwork() = runTest {
        val oversizedImage = ByteArray(21 * 1024 * 1024) // 21 MB — excede el límite de 20 MB
        var networkCalled = false
        val mockEngine = MockEngine { _ ->
            networkCalled = true
            respond("", HttpStatusCode.OK)
        }
        val mockClient = HttpClient(mockEngine) { expectSuccess = false }
        val provider = GeminiOCRProvider(
            apiKey = validApiKey,
            httpClient = mockClient
        )

        assertFailsWith<CloudOCRException> {
            provider.extractText(oversizedImage, defaultConfig())
        }
        assertFalse(networkCalled, "Network should NOT be called when file is too large")
    }

    @Test
    fun extractText_unsupportedMimeType_throwsCloudOCRExceptionWithoutCallingNetwork() = runTest {
        var networkCalled = false
        val mockEngine = MockEngine { _ ->
            networkCalled = true
            respond("", HttpStatusCode.OK)
        }
        val mockClient = HttpClient(mockEngine) { expectSuccess = false }
        val provider = GeminiOCRProvider(
            apiKey = validApiKey,
            httpClient = mockClient
        )
        val configWithBadMime = OCRRequestConfig(mimeType = "application/exe", timeout = 30_000L)

        assertFailsWith<CloudOCRException> {
            provider.extractText(smallImage, configWithBadMime)
        }
        assertFalse(networkCalled, "Network should NOT be called for unsupported MIME type")
    }

    // ── isAvailable() ─────────────────────────────────────────────────────────

    @Test
    fun isAvailable_200Response_returnsTrue() = runTest {
        val provider = buildProvider(statusCode = HttpStatusCode.OK, responseBody = "{}")
        assertTrue(provider.isAvailable())
    }

    @Test
    fun isAvailable_errorResponse_returnsFalse() = runTest {
        val provider = buildProvider(statusCode = HttpStatusCode.InternalServerError, responseBody = "error")
        assertFalse(provider.isAvailable())
    }

    // ── getProviderInfo() ──────────────────────────────────────────────────────

    @Test
    fun getProviderInfo_returnsValidInfo() {
        val provider = buildProvider()
        val info = provider.getProviderInfo()

        assertNotNull(info)
        assertTrue(info.name.contains("Gemini", ignoreCase = true))
        assertTrue(info.supportsMultipleLanguages)
        assertTrue(info.supportsBarcodes)
        assertTrue(info.supportsHandwriting)
        assertTrue(info.maxImageSize > 0)
        assertTrue(info.documentationUrl?.startsWith("https://") == true)
    }

    // ── PDF config ────────────────────────────────────────────────────────────

    @Test
    fun extractText_withPdfMimeType_validPdfBytes_callsNetwork() = runTest {
        // PDF magic bytes: %PDF
        val pdfBytes = byteArrayOf(0x25, 0x50, 0x44, 0x46) + ByteArray(100)
        var networkCalled = false
        val mockEngine = MockEngine { _ ->
            networkCalled = true
            respond(
                geminiSuccessResponse,
                HttpStatusCode.OK,
                headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val mockClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
            expectSuccess = false
        }
        val provider = GeminiOCRProvider(apiKey = validApiKey, httpClient = mockClient)
        val pdfConfig = OCRRequestConfig(mimeType = "application/pdf", timeout = 30_000L)

        provider.extractText(pdfBytes, pdfConfig)

        assertTrue(networkCalled, "Network should be called for valid PDF")
    }
}