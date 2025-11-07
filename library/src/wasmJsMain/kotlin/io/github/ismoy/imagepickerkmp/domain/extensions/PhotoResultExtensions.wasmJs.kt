package io.github.ismoy.imagepickerkmp.domain.extensions

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult

/**
 * WASM platform implementations for PhotoResult extensions.
 * Note: Image processing functionality is limited in WASM platform.
 * These are stub implementations for compatibility.
 */

actual fun PhotoResult.loadBytes(): ByteArray {
    return ByteArray(0)
}

actual fun GalleryPhotoResult.loadBytes(): ByteArray {
    return ByteArray(0)
}

actual fun PhotoResult.loadBase64(): String {
    return ""
}

actual fun GalleryPhotoResult.loadBase64(): String {
    return ""
}

actual fun PhotoResult.loadImageBitmap(): ImageBitmap? {
    return null
}

actual fun GalleryPhotoResult.loadImageBitmap(): ImageBitmap? {
    return null
}

actual fun PhotoResult.loadPainter(): Painter? {
    return null
}

actual fun GalleryPhotoResult.loadPainter(): Painter? {
    return null
}
