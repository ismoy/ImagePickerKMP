package io.github.ismoy.imagepickerkmp.features.ocr.utils

import io.github.ismoy.imagepickerkmp.features.ocr.data.providers.CloudOCRProvider
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for [APIKeyValidator] — pure logic, no network required.
 *
 * Covers:
 * - Valid API keys pass validation for all providers
 * - Blank / empty keys throw [MissingAPIKeyException]
 * - Keys too short (< 10 chars) throw [MissingAPIKeyException]
 * - Placeholder keys ("your_api_key", "replace_me", "example") throw [MissingAPIKeyException]
 * - Providers without required keys (Ollama without apiKey) pass
 * - getApiKeyInstructions returns non-empty guidance per provider
 */
class APIKeyValidatorTest {

    // ───────────── Gemini ─────────────

    @Test
    fun gemini_validKey_doesNotThrow() {
        val provider = CloudOCRProvider.Gemini(apiKey = "AIzaSyABCDEFGHIJKLMNOP")
        APIKeyValidator.validateProvider(provider) // must not throw
    }

    @Test
    fun gemini_emptyKey_throwsMissingAPIKeyException() {
        val provider = CloudOCRProvider.Gemini(apiKey = "")
        val ex = assertFailsWith<MissingAPIKeyException> {
            APIKeyValidator.validateProvider(provider)
        }
        assertTrue(ex.message!!.contains("Google Gemini", ignoreCase = true))
    }

    @Test
    fun gemini_blankKey_throwsMissingAPIKeyException() {
        val provider = CloudOCRProvider.Gemini(apiKey = "   ")
        assertFailsWith<MissingAPIKeyException> {
            APIKeyValidator.validateProvider(provider)
        }
    }

    @Test
    fun gemini_keyTooShort_throwsMissingAPIKeyException() {
        val provider = CloudOCRProvider.Gemini(apiKey = "short")
        val ex = assertFailsWith<MissingAPIKeyException> {
            APIKeyValidator.validateProvider(provider)
        }
        assertTrue(ex.message!!.contains("too short", ignoreCase = true))
    }

    @Test
    fun gemini_placeholderKey_throwsMissingAPIKeyException() {
        listOf("your_api_key_here", "REPLACE_ME_with_key", "example_key_123456").forEach { key ->
            val provider = CloudOCRProvider.Gemini(apiKey = key)
            assertFailsWith<MissingAPIKeyException>(
                message = "Expected exception for placeholder key: $key"
            ) {
                APIKeyValidator.validateProvider(provider)
            }
        }
    }

    // ───────────── OpenAI ─────────────

    @Test
    fun openAI_validKey_doesNotThrow() {
        val provider = CloudOCRProvider.OpenAI(apiKey = "sk-ABCDEFghijklmnopQRSTUVwxyz1234567890")
        APIKeyValidator.validateProvider(provider) // must not throw
    }

    @Test
    fun openAI_emptyKey_throwsMissingAPIKeyException() {
        val provider = CloudOCRProvider.OpenAI(apiKey = "")
        assertFailsWith<MissingAPIKeyException> {
            APIKeyValidator.validateProvider(provider)
        }
    }

    // ───────────── Claude ─────────────

    @Test
    fun claude_validKey_doesNotThrow() {
        val provider = CloudOCRProvider.Claude(apiKey = "sk-ant-api03-ABCDEFGHIJKLMNOP")
        APIKeyValidator.validateProvider(provider)
    }

    @Test
    fun claude_shortKey_throwsMissingAPIKeyException() {
        val provider = CloudOCRProvider.Claude(apiKey = "tiny")
        assertFailsWith<MissingAPIKeyException> {
            APIKeyValidator.validateProvider(provider)
        }
    }

    // ───────────── Azure Vision ─────────────

    @Test
    fun azure_validSubscriptionKey_doesNotThrow() {
        val provider = CloudOCRProvider.AzureVision(
            subscriptionKey = "abcdef1234567890abcdef12",
            endpoint = "https://myresource.cognitiveservices.azure.com"
        )
        APIKeyValidator.validateProvider(provider)
    }

    @Test
    fun azure_emptySubscriptionKey_throwsMissingAPIKeyException() {
        val provider = CloudOCRProvider.AzureVision(
            subscriptionKey = "",
            endpoint = "https://myresource.cognitiveservices.azure.com"
        )
        assertFailsWith<MissingAPIKeyException> {
            APIKeyValidator.validateProvider(provider)
        }
    }

    // ───────────── Ollama (no API key required) ─────────────

    @Test
    fun ollama_withoutApiKey_doesNotThrow() {
        val provider = CloudOCRProvider.Ollama(baseUrl = "http://localhost:11434", model = "llava")
        APIKeyValidator.validateProvider(provider) // apiKey is null → no validation
    }

    @Test
    fun ollama_withValidApiKey_doesNotThrow() {
        val provider = CloudOCRProvider.Ollama(apiKey = "some-valid-ollama-key-1234")
        APIKeyValidator.validateProvider(provider)
    }

    @Test
    fun ollama_withShortApiKey_throwsMissingAPIKeyException() {
        val provider = CloudOCRProvider.Ollama(apiKey = "short")
        assertFailsWith<MissingAPIKeyException> {
            APIKeyValidator.validateProvider(provider)
        }
    }

    // ───────────── Custom ─────────────

    @Test
    fun custom_withoutApiKey_doesNotThrow() {
        val provider = CloudOCRProvider.Custom(
            name = "MyOCR",
            baseUrl = "https://myocr.example.com/api"
        )
        APIKeyValidator.validateProvider(provider)
    }

    @Test
    fun custom_withValidApiKey_doesNotThrow() {
        val provider = CloudOCRProvider.Custom(
            name = "MyOCR",
            baseUrl = "https://myocr.example.com/api",
            apiKey = "valid-api-key-longer-than-ten"
        )
        APIKeyValidator.validateProvider(provider)
    }

    @Test
    fun custom_withEmptyApiKey_throwsMissingAPIKeyException() {
        val provider = CloudOCRProvider.Custom(
            name = "MyOCR",
            baseUrl = "https://myocr.example.com/api",
            apiKey = ""
        )
        assertFailsWith<MissingAPIKeyException> {
            APIKeyValidator.validateProvider(provider)
        }
    }

    // ───────────── MissingAPIKeyException companion ─────────────

    @Test
    fun emptyApiKey_factoryMethod_containsProviderName() {
        val ex = MissingAPIKeyException.emptyApiKey("TestProvider")
        assertNotNull(ex.message)
        assertTrue(ex.message!!.contains("TestProvider"))
        assertTrue(ex.message!!.contains("empty", ignoreCase = true))
    }

    // ───────────── getApiKeyInstructions ─────────────

    @Test
    fun instructions_allProviders_returnNonEmptyString() {
        val providers = listOf(
            CloudOCRProvider.Gemini(apiKey = "key"),
            CloudOCRProvider.OpenAI(apiKey = "key"),
            CloudOCRProvider.Claude(apiKey = "key"),
            CloudOCRProvider.AzureVision(subscriptionKey = "key", endpoint = "https://x.azure.com"),
            CloudOCRProvider.Custom(name = "X", baseUrl = "https://x.com"),
            CloudOCRProvider.Ollama()
        )
        providers.forEach { provider ->
            val instructions = APIKeyValidator.getApiKeyInstructions(provider)
            assertTrue(instructions.isNotBlank(), "Instructions should not be blank for $provider")
        }
    }

    @Test
    fun instructions_gemini_containsGoogleUrl() {
        val instructions = APIKeyValidator.getApiKeyInstructions(
            CloudOCRProvider.Gemini(apiKey = "key")
        )
        assertTrue(instructions.contains("aistudio.google.com") || instructions.contains("google", ignoreCase = true))
    }

    @Test
    fun instructions_openAI_containsPlatformUrl() {
        val instructions = APIKeyValidator.getApiKeyInstructions(
            CloudOCRProvider.OpenAI(apiKey = "key")
        )
        assertTrue(instructions.contains("platform.openai.com") || instructions.contains("openai", ignoreCase = true))
    }
}
