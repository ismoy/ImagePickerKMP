package io.github.ismoy.imagepickerkmp.domain.extensions

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult

/**
 * WASM platform stub implementations for PhotoResult extensions.
 * GalleryPhotoResult is a typealias for PhotoResult — all functions apply to both.
 */

actual fun PhotoResult.loadBytes(): ByteArray = ByteArray(0)

actual fun PhotoResult.loadBase64(): String = ""

actual fun PhotoResult.loadImageBitmap(): ImageBitmap? = null

actual fun PhotoResult.loadPainter(): Painter? = null
