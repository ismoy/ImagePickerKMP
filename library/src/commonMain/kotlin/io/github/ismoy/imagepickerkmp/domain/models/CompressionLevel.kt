package io.github.ismoy.imagepickerkmp.domain.models

enum class CompressionLevel {
    LOW,
    MEDIUM,
    HIGH;

    companion object {
        private const val LOW_QUALITY    = 0.95
        private const val MEDIUM_QUALITY = 0.80
        private const val HIGH_QUALITY   = 0.60

        const val LOW_JPEG_QUALITY    = 85
        const val MEDIUM_JPEG_QUALITY = 70
        const val HIGH_JPEG_QUALITY   = 50

        const val LOW_MAX_DIMENSION    = 3840
        const val MEDIUM_MAX_DIMENSION = 1920
        const val HIGH_MAX_DIMENSION   = 1280
    }

    fun toQualityValue(): Double = when (this) {
        LOW    -> LOW_QUALITY
        MEDIUM -> MEDIUM_QUALITY
        HIGH   -> HIGH_QUALITY
    }

    fun toJpegQuality(): Int = when (this) {
        LOW    -> LOW_JPEG_QUALITY
        MEDIUM -> MEDIUM_JPEG_QUALITY
        HIGH   -> HIGH_JPEG_QUALITY
    }

    fun toMaxDimension(): Int = when (this) {
        LOW    -> LOW_MAX_DIMENSION
        MEDIUM -> MEDIUM_MAX_DIMENSION
        HIGH   -> HIGH_MAX_DIMENSION
    }
}
