package io.github.ismoy.imagepickerkmp.domain.utils

import io.github.ismoy.imagepickerkmp.domain.config.CompressionConfig
import kotlin.math.min

/**
 * Utility object for common image compression operations.
 */
object CompressionUtils {
    
    /**
     * Calculates the optimal dimensions for an image based on compression configuration.
     *
     * @param originalWidth Original image width
     * @param originalHeight Original image height
     * @param config Compression configuration
     * @return Pair of (width, height) for the resized image
     */
    fun calculateOptimalDimensions(
        originalWidth: Int,
        originalHeight: Int,
        config: CompressionConfig
    ): Pair<Int, Int> {
        if (config.maxWidth == null && config.maxHeight == null) {
            return Pair(originalWidth, originalHeight)
        }

        val aspectRatio = originalWidth.toFloat() / originalHeight.toFloat()
        
        return when {
            config.maxWidth != null && config.maxHeight != null -> {
                if (config.maintainAspectRatio) {
                    val scaleWidth = config.maxWidth.toFloat() / originalWidth
                    val scaleHeight = config.maxHeight.toFloat() / originalHeight
                    val scale = min(scaleWidth, scaleHeight)
                    
                    if (scale < 1.0f) {
                        Pair(
                            (originalWidth * scale).toInt(),
                            (originalHeight * scale).toInt()
                        )
                    } else {
                        Pair(originalWidth, originalHeight)
                    }
                } else {
                    Pair(
                        min(originalWidth, config.maxWidth),
                        min(originalHeight, config.maxHeight)
                    )
                }
            }
            config.maxWidth != null -> {
                if (originalWidth > config.maxWidth) {
                    val newHeight = (config.maxWidth / aspectRatio).toInt()
                    Pair(config.maxWidth, newHeight)
                } else {
                    Pair(originalWidth, originalHeight)
                }
            }
            config.maxHeight != null -> {
                if (originalHeight > config.maxHeight) {
                    val newWidth = (config.maxHeight * aspectRatio).toInt()
                    Pair(newWidth, config.maxHeight)
                } else {
                    Pair(originalWidth, originalHeight)
                }
            }
            else -> Pair(originalWidth, originalHeight)
        }
    }

    /**
     * Determines if compression should be applied based on the image format.
     *
     * @param mimeType The MIME type of the image
     * @param config Compression configuration
     * @return true if compression should be applied
     */
    fun shouldCompress(mimeType: String?, config: CompressionConfig): Boolean =
        config.enabled && mimeType != null && config.supportsFormat(mimeType)

    /**
     * Validates compression configuration values.
     *
     * @param config Compression configuration to validate
     * @throws IllegalArgumentException if configuration is invalid
     */
    fun validateConfig(config: CompressionConfig) {
        require(config.getQuality() in 0.0..1.0) {
            "Quality must be between 0.0 and 1.0, got ${config.getQuality()}"
        }
        
        config.maxWidth?.let { width ->
            require(width > 0) { "Max width must be positive, got $width" }
        }
        
        config.maxHeight?.let { height ->
            require(height > 0) { "Max height must be positive, got $height" }
        }
    }
}
