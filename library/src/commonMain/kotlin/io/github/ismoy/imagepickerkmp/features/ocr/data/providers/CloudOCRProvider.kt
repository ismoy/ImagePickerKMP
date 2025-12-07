package io.github.ismoy.imagepickerkmp.features.ocr.data.providers

import io.github.ismoy.imagepickerkmp.features.ocr.model.RequestFormat

sealed class CloudOCRProvider {
    data class Gemini(
        val apiKey: String,
        val model: String = "gemini-2.5-flash"
    ) : CloudOCRProvider()

    data class Ollama(
        val baseUrl: String = "http://localhost:11434",
        val model: String = "llava",
        val apiKey: String? = null
    ) : CloudOCRProvider()

    data class OpenAI(
        val apiKey: String,
        val model: String = "gpt-4-vision-preview",
        val baseUrl: String = "https://api.openai.com/v1/chat/completions"
    ) : CloudOCRProvider()

    data class Claude(
        val apiKey: String,
        val model: String = "claude-3-sonnet-20240229",
        val baseUrl: String = "https://api.anthropic.com/v1/messages"
    ) : CloudOCRProvider()

    data class AzureVision(
        val subscriptionKey: String,
        val endpoint: String,
        val version: String = "3.2"
    ) : CloudOCRProvider()

    data class Custom(
        val name: String,
        val baseUrl: String,
        val apiKey: String? = null,
        val headers: Map<String, String> = emptyMap(),
        val requestFormat: RequestFormat = RequestFormat.MULTIPART_FORM,
        val model: String? = null
    ) : CloudOCRProvider()
}
