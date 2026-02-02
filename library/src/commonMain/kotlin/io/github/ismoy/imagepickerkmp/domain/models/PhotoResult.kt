package io.github.ismoy.imagepickerkmp.domain.models

/**
 * Data class representing the result of a file captured by the camera or selected from gallery.
 * Supports both images and documents (like PDFs).
 *
 * @property uri The URI of the captured file as a string.
 * @property width The width of the file in pixels (only for images, null for documents like PDFs).
 * @property height The height of the file in pixels (only for images, null for documents like PDFs).
 * @property fileName The name of the file, if available.
 * @property fileSize The size of the file in bytes, if available.
 * @property mimeType The MIME type of the file (e.g., "image/jpeg", "application/pdf").
 * @property exif EXIF metadata including GPS location data (only available when includeExif is enabled).
 */
data class PhotoResult(
    val uri: String,
    val width: Int? = null,
    val height: Int? = null,
    val fileName: String? = null,
    val fileSize: Long? = null,
    val mimeType: String? = null,
    val exif: ExifData? = null
)
