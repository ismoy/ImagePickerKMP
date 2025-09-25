package io.github.ismoy.imagepickerkmp.domain.extensions

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult

/**
 * Extension function to load the photo data as ByteArray from the URI.
 * This allows developers to control when the full image data is loaded into memory.
 * 
 * @return ByteArray containing the image data, or empty ByteArray if loading fails.
 */
expect fun PhotoResult.loadBytes(): ByteArray

/**
 * Extension function to load the gallery photo data as ByteArray from the URI.
 * Ideal for gallery collections with multiple photos.
 * 
 * @return ByteArray containing the highly optimized image data, or empty ByteArray if loading fails.
 */
expect fun GalleryPhotoResult.loadBytes(): ByteArray

/**
 * Extension function to load the photo data as Base64 encoded string from the URI.
 * @return Base64 encoded string containing the image data, or empty string if loading fails.
 */
expect fun PhotoResult.loadBase64(): String

/**
 * Extension function to load the gallery photo data as Base64 encoded string from the URI.
 * @return Base64 encoded string containing the highly optimized image data, or empty string if loading fails.
 */
expect fun GalleryPhotoResult.loadBase64(): String

/**
 * Extension function to load the photo as ImageBitmap for Compose usage.
 * Perfect for use with Painter or Canvas in Compose.
 * 
 * @return ImageBitmap that can be used directly in Compose, or null if loading fails.
 */
expect fun PhotoResult.loadImageBitmap(): ImageBitmap?

/**
 * Extension function to load the gallery photo as ImageBitmap for Compose usage.
 * Perfect for use with Painter or Canvas in Compose. *
 * @return ImageBitmap that can be used directly in Compose, or null if loading fails.
 */
expect fun GalleryPhotoResult.loadImageBitmap(): ImageBitmap?

/**
 * Extension function to load the photo as Painter for Compose usage.
 * Perfect for direct use with Image composable and painterResource.
 * 
 * @return Painter that can be used directly in Image composable, or null if loading fails.
 */
expect fun PhotoResult.loadPainter(): Painter?

/**
 * Extension function to load the gallery photo as Painter for Compose usage.
 * @return Painter that can be used directly in Image composable, or null if loading fails.
 */
expect fun GalleryPhotoResult.loadPainter(): Painter?
