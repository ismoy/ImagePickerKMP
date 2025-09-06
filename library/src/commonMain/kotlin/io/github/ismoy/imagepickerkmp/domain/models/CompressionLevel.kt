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
     * Converts compression level to quality value (0.0 to 1.0)
     */
    fun toQualityValue(): Double {
        return when (this) {
            LOW -> 0.95
            MEDIUM -> 0.75
            HIGH -> 0.50
        }
    }
}
