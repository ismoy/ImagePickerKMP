package io.github.ismoy.imagepickerkmp.domain.extensions

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import kotlinx.io.RawSink
import kotlinx.io.RawSource
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

/**
 * Extension function to load the photo data as ByteArray from the URI.
 * Works for both camera results and gallery picks (via GalleryPhotoResult typealias).
 *
 * @return ByteArray containing the image data, or empty ByteArray if loading fails.
 */
expect fun PhotoResult.loadBytes(): ByteArray

/**
 * Extension function to load the photo data as Base64 encoded string from the URI.
 * @return Base64 encoded string containing the image data, or empty string if loading fails.
 */
expect fun PhotoResult.loadBase64(): String

/**
 * Extension function to load the photo as ImageBitmap for Compose usage.
 * Perfect for use with Painter or Canvas in Compose.
 *
 * @return ImageBitmap that can be used directly in Compose, or null if loading fails.
 */
expect fun PhotoResult.loadImageBitmap(): ImageBitmap?

/**
 * Extension function to load the photo as Painter for Compose usage.
 * Perfect for direct use with Image composable and painterResource.
 *
 * @return Painter that can be used directly in Image composable, or null if loading fails.
 */
expect fun PhotoResult.loadPainter(): Painter?

/**
 * Gets the absolute file system path of the photo.
 *
 * @return String representing the absolute path to the photo file
 */
expect val PhotoResult.absolutePath: String

/**
 * Converts the photo result to a [Path] object for file system operations.
 *
 * @return Path instance representing the photo's location
 */
fun PhotoResult.asPath(): Path {
    return Path(absolutePath)
}

/**
 * Creates a [RawSource] for reading the photo file directly.
 * Use this for low-level file operations without buffering.
 *
 * @return RawSource for the photo file
 */
fun PhotoResult.asRawSource(): RawSource {
    return SystemFileSystem
        .source(asPath())
}

/**
 * Creates a buffered [Source] for efficient reading of the photo file.
 * This is recommended for most read operations as it provides better performance.
 *
 * @return Buffered Source for the photo file
 */
fun PhotoResult.asSource(): Source {
    return asRawSource().use {
        it.buffered()
    }
}

/**
 * Transfers the entire photo file content to the specified [RawSink].
 * Useful for copying or uploading the photo data.
 *
 * @param sink The RawSink to write the photo data to
 *
 * Example:
 * ```
 * val outputFile = SystemFileSystem.sink(Path("copy.jpg"))
 * photoResult.transferToSink(outputFile)
 * ```
 */
fun PhotoResult.transferToSink(sink: RawSink) {
    asSource().transferTo(sink)
}