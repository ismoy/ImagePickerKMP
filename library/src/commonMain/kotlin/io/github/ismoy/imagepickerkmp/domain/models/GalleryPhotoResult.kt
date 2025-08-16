package io.github.ismoy.imagepickerkmp.domain.models

/**
 * Data class representing the result of a photo selected from the gallery.
 *
 * @property uri The URI of the selected photo as a string.
 * @property width The width of the photo in pixels.
 * @property height The height of the photo in pixels.
 * @property fileName The name of the file, if available.
 * @property fileSize The size of the file in bytes, if available.
 */
data class GalleryPhotoResult(
    val uri: String,
    val width: Int,
    val height: Int,
    val fileName: String? = null,
    val fileSize: Long? = null
)
