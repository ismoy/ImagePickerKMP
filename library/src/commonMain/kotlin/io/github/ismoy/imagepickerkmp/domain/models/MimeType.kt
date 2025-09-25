package io.github.ismoy.imagepickerkmp.domain.models

/**
 * Defines the supported MIME types for image operations.
 * Contains common image formats and utility functions for MIME type handling.
 */
enum class MimeType(val value: String) {
    IMAGE_ALL("image/*"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    IMAGE_GIF("image/gif"),
    IMAGE_WEBP("image/webp"),
    IMAGE_BMP("image/bmp"),
    IMAGE_HEIC("image/heic"),
    IMAGE_HEIF("image/heif");

    /**
     * Companion object containing utility functions and constants for MIME type operations.
     */
    companion object {
        fun toMimeTypeStrings(vararg mimeTypes: MimeType): List<String> =
            mimeTypes.map { it.value }
        val COMMON_IMAGE_TYPES = listOf(IMAGE_JPEG, IMAGE_PNG, IMAGE_GIF, IMAGE_WEBP)
        val ALL_SUPPORTED_TYPES = entries
        fun fromString(mimeTypeString: String): MimeType? =
            entries.find { it.value.equals(mimeTypeString, ignoreCase = true) }
    }
}
