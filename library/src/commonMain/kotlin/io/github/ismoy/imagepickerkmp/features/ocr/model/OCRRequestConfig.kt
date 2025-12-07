package io.github.ismoy.imagepickerkmp.features.ocr.model

data class OCRRequestConfig(
    val language: String? = null,
    val prompt: String? = null,
    val temperature: Double = 0.1,
    val maxTokens: Int = 4000,
    val timeout: Long = 30000L,
    val customHeaders: Map<String, String> = emptyMap(),
    val providerSpecific: Map<String, Any> = emptyMap(),
    val mimeType: String? = null
)