package io.github.ismoy.imagepickerkmp.domain.models

/**
 * Data class representing the result of a photo captured by the camera.
 *
 * @property uri The URI of the captured photo as a string.
 * @property width The width of the photo in pixels.
 * @property height The height of the photo in pixels.
 * @property fileName The name of the file, if available.
 * @property fileSize The size of the file in KB, if available.
 */
data class PhotoResult(
    val uri: String,
    val width: Int,
    val height: Int,
    val fileName: String? = null,
    val fileSize: Long? = null
)
