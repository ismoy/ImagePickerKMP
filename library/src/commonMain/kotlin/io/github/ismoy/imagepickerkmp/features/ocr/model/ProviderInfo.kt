package io.github.ismoy.imagepickerkmp.features.ocr.model

data class ProviderInfo(
    val name: String,
    val description: String,
    val supportsMultipleLanguages: Boolean,
    val supportsBarcodes: Boolean,
    val supportsHandwriting: Boolean,
    val maxImageSize: Long,
    val rateLimit: String?,
    val pricing: String?,
    val documentationUrl: String?
)