package io.github.ismoy.imagepickerkmp.features.ocr.model

data class ExtractionIndicators(
    val ocrIndicatorText: String = "Extraction Text",
    val ocrIndicatorEmoji: String = "📝",
    val structuredIndicatorText: String = "Structured Data",
    val structuredIndicatorEmoji: String = "🏗️",
    val iaIndicatorText: String = "IA",
    val iaIndicatorEmoji: String = "🤖"
)