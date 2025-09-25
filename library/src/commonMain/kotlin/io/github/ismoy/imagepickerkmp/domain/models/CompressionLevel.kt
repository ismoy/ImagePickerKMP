package io.github.ismoy.imagepickerkmp.domain.models

/**
 * Enum representing different compression levels for image processing.
 *
 * @property LOW Low compression - maintains high quality but larger file size
 * @property MEDIUM Medium compression - balanced quality and file size
 * @property HIGH High compression - smaller file size but lower quality
 */
enum class CompressionLevel {
    LOW,
    MEDIUM,
    HIGH;

    /**
     * Companion object containing quality constants for compression levels.
     */
    companion object {
        /** Quality constants for different compression levels. */
        private const val LOW_QUALITY = 0.95
        private const val MEDIUM_QUALITY = 0.75
        private const val HIGH_QUALITY = 0.50
    }

    /**
     * Converts compression level to quality value (0.0 to 1.0).
     */
    fun toQualityValue(): Double {
        return when (this) {
            LOW -> LOW_QUALITY
            MEDIUM -> MEDIUM_QUALITY
            HIGH -> HIGH_QUALITY
        }
    }
}
