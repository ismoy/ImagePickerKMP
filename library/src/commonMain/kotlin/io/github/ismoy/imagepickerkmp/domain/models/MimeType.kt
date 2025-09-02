package io.github.ismoy.imagepickerkmp.domain.models


enum class MimeType(val value: String) {
    IMAGE_ALL("image/*"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    IMAGE_GIF("image/gif"),
    IMAGE_WEBP("image/webp"),
    IMAGE_BMP("image/bmp"),
    IMAGE_HEIC("image/heic"),
    IMAGE_HEIF("image/heif");
    companion object {
        fun toMimeTypeStrings(vararg mimeTypes: MimeType): List<String> {
            return mimeTypes.map { it.value }
        }
        val COMMON_IMAGE_TYPES = listOf(IMAGE_JPEG, IMAGE_PNG, IMAGE_GIF, IMAGE_WEBP)
        val ALL_SUPPORTED_TYPES = entries
        fun fromString(mimeTypeString: String): MimeType? {
            return entries.find { it.value.equals(mimeTypeString, ignoreCase = true) }
        }
    }
}
