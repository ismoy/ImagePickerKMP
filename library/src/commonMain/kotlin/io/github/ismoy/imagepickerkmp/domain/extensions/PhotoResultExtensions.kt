package io.github.ismoy.imagepickerkmp.domain.extensions

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult

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
