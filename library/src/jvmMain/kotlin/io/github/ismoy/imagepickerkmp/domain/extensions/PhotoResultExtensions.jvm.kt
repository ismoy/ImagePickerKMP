package io.github.ismoy.imagepickerkmp.domain.extensions

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import org.jetbrains.skia.Image
import java.io.File
import java.util.Base64

/**
 * Desktop implementations for PhotoResult extension functions.
 * These functions work with file URIs and use Java's ImageIO for image operations.
 * GalleryPhotoResult is a typealias for PhotoResult — all functions apply to both.
 */

actual fun PhotoResult.loadBytes(): ByteArray {
    return try {
        val file = File(java.net.URI(uri))
        file.readBytes()
    } catch (_: Exception) {
        byteArrayOf()
    }
}

actual fun PhotoResult.loadBase64(): String {
    return try {
        val bytes = loadBytes()
        if (bytes.isNotEmpty()) {
            Base64.getEncoder().encodeToString(bytes)
        } else {
            ""
        }
    } catch (_: Exception) {
        ""
    }
}

actual fun PhotoResult.loadImageBitmap(): ImageBitmap? {
    return try {
        val bytes = loadBytes()
        if (bytes.isNotEmpty()) {
            Image.makeFromEncoded(bytes).toComposeImageBitmap()
        } else {
            null
        }
    } catch (_: Exception) {
        null
    }
}

actual fun PhotoResult.loadPainter(): Painter? {
    return try {
        val imageBitmap = loadImageBitmap()
        imageBitmap?.let { androidx.compose.ui.graphics.painter.BitmapPainter(it) }
    } catch (_: Exception) {
        null
    }
}
