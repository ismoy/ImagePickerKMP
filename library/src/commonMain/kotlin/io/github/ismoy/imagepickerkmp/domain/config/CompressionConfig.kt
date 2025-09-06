package io.github.ismoy.imagepickerkmp.domain.config

import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.MimeType

/**
 * Configuration for automatic image compression.
 *
 * @property enabled Whether automatic compression is enabled
 * @property compressionLevel The level of compression to apply
 * @property maxWidth Maximum width in pixels (null for no limit)
 * @property maxHeight Maximum height in pixels (null for no limit)
 * @property customQuality Custom quality value (0.0 to 1.0), overrides compressionLevel if set
 * @property maintainAspectRatio Whether to maintain aspect ratio when resizing
 * @property supportedFormats List of MIME types that support compression (defaults to all supported image formats)
 */
data class CompressionConfig(
    val enabled: Boolean = false,
    val compressionLevel: CompressionLevel = CompressionLevel.MEDIUM,
    val maxWidth: Int? = null,
    val maxHeight: Int? = null,
    val customQuality: Double? = null,
    val maintainAspectRatio: Boolean = true,
    val supportedFormats: List<MimeType> = MimeType.ALL_SUPPORTED_TYPES.filter { it != MimeType.IMAGE_ALL }
) {
    /**
     * Gets the effective quality value, prioritizing customQuality over compressionLevel
     */
    fun getQuality(): Double {
        return customQuality ?: compressionLevel.toQualityValue()
    }

    /**
     * Checks if the given format supports compression
     */
    fun supportsFormat(mimeType: String): Boolean {
        return supportedFormats.any { it.value.equals(mimeType, ignoreCase = true) }
    }
}
