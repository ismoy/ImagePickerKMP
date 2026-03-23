package io.github.ismoy.imagepickerkmp.features.ocr.utils

import io.github.ismoy.imagepickerkmp.features.ocr.data.providers.CloudOCRProvider


internal object APIKeyValidator {
    
    fun validateProvider(provider: CloudOCRProvider) {
        when (provider) {
            is CloudOCRProvider.Gemini -> {
                validateApiKey(provider.apiKey, "Google Gemini")
            }
            is CloudOCRProvider.OpenAI -> {
                validateApiKey(provider.apiKey, "OpenAI")
            }
            is CloudOCRProvider.Claude -> {
                validateApiKey(provider.apiKey, "Claude (Anthropic)")
            }
            is CloudOCRProvider.AzureVision -> {
                validateApiKey(provider.subscriptionKey, "Azure Computer Vision")
            }
            is CloudOCRProvider.Custom -> {
                provider.apiKey?.let { 
                    validateApiKey(it, provider.name)
                }
            }
            is CloudOCRProvider.Ollama -> {
                provider.apiKey?.let {
                    validateApiKey(it, "Ollama")
                }
            }
        }
    }
    
    private fun validateApiKey(apiKey: String, providerName: String) {
        when {
            apiKey.isBlank() -> throw MissingAPIKeyException.emptyApiKey(providerName)
            apiKey.length < 10 -> throw MissingAPIKeyException(
                "API key for $providerName appears to be too short (${apiKey.length} characters). " +
                "Please verify you have provided the complete API key."
            )
            apiKey.contains("your_api_key", ignoreCase = true) ||
            apiKey.contains("replace_me", ignoreCase = true) ||
            apiKey.contains("example", ignoreCase = true) -> {
                throw MissingAPIKeyException(
                    "Please replace the placeholder API key with your actual $providerName API key."
                )
            }
        }
    }
    
    fun getApiKeyInstructions(provider: CloudOCRProvider): String {
        return when (provider) {
            is CloudOCRProvider.Gemini -> 
                "Get your Google Gemini API key from: https://aistudio.google.com/app/apikey"
            is CloudOCRProvider.OpenAI -> 
                "Get your OpenAI API key from: https://platform.openai.com/api-keys"
            is CloudOCRProvider.Claude -> 
                "Get your Claude API key from: https://console.anthropic.com/settings/keys"
            is CloudOCRProvider.AzureVision -> 
                "Get your Azure Computer Vision subscription key from: https://portal.azure.com"
            is CloudOCRProvider.Custom -> 
                "Contact your ${provider.name} service provider for API key instructions"
            is CloudOCRProvider.Ollama -> 
                "Ollama typically runs locally. If authentication is required, check your Ollama server configuration"
        }
    }
}
