package io.github.ismoy.imagepickerkmp.domain.models


enum class CompressionLevel {
    LOW,
    MEDIUM,
    HIGH;

    companion object {
        private const val LOW_QUALITY    = 0.98
        private const val MEDIUM_QUALITY = 0.85
        private const val HIGH_QUALITY   = 0.65
    }

    fun toQualityValue(): Double {
        return when (this) {
            LOW    -> LOW_QUALITY
            MEDIUM -> MEDIUM_QUALITY
            HIGH   -> HIGH_QUALITY
        }
    }
}
