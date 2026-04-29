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
 *
 * ## Available Extensions
 *
 * ### Data Access
 * - [.absolutePath] — Absolute file system path of the photo
 * - [.asPath()] — Converts to a [kotlinx.io.files.Path] object
 * - [.exists()] — Checks whether the file exists on disk
 *
 * ### Reading
 * - [.loadBytes()] — Reads the file into a [ByteArray]
 * - [.loadBase64()] — Reads the file and encodes it as a `Base64` string
 * - [.asRawSource()] — Opens an unbuffered [kotlinx.io.RawSource] for reading
 * - [.asSource()] — Opens a buffered [kotlinx.io.Source] for efficient reading
 *
 * ### UI / Compose
 * - [.loadImageBitmap()] — Decodes the file into an [androidx.compose.ui.graphics.ImageBitmap] for Compose
 * - [.loadPainter()] — Decodes the file into a [androidx.compose.ui.graphics.painter.Painter] for Compose
 *
 * ### Writing
 * - [.transferToSink()] — Transfers the file content to a [kotlinx.io.RawSink]
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
