package io.github.ismoy.imagepickerkmp.domain.extensions

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import org.jetbrains.skia.Image
import java.io.File
import java.util.*
import javax.imageio.ImageIO

/**
 * Desktop implementations for PhotoResult extension functions.
 * These functions work with file URIs and use Java's ImageIO for image operations.
 */

/**
 * Load image data as ByteArray from file URI on Desktop
 */
actual fun PhotoResult.loadBytes(): ByteArray {
    return try {
        val file = File(java.net.URI(uri))
        file.readBytes()
    } catch (e: Exception) {
        byteArrayOf()
    }
}

/**
 * Load image data as ByteArray for GalleryPhotoResult on Desktop
 */
actual fun GalleryPhotoResult.loadBytes(): ByteArray {
    return try {
        val file = File(java.net.URI(uri))
        file.readBytes()
    } catch (e: Exception) {
        byteArrayOf()
    }
}

/**
 * Load image as Base64 string from PhotoResult on Desktop
 */
actual fun PhotoResult.loadBase64(): String {
    return try {
        val bytes = loadBytes()
        if (bytes.isNotEmpty()) {
            Base64.getEncoder().encodeToString(bytes)
        } else {
            ""
        }
    } catch (e: Exception) {
        ""
    }
}

/**
 * Load image as Base64 string from GalleryPhotoResult on Desktop
 */
actual fun GalleryPhotoResult.loadBase64(): String {
    return try {
        val bytes = loadBytes()
        if (bytes.isNotEmpty()) {
            Base64.getEncoder().encodeToString(bytes)
        } else {
            ""
        }
    } catch (e: Exception) {
        ""
    }
}

/**
 * Load image as ImageBitmap from PhotoResult on Desktop
 */
actual fun PhotoResult.loadImageBitmap(): ImageBitmap? {
    return try {
        val bytes = loadBytes()
        if (bytes.isNotEmpty()) {
            Image.makeFromEncoded(bytes).toComposeImageBitmap()
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

/**
 * Load image as ImageBitmap from GalleryPhotoResult on Desktop
 */
actual fun GalleryPhotoResult.loadImageBitmap(): ImageBitmap? {
    return try {
        val bytes = loadBytes()
        if (bytes.isNotEmpty()) {
            Image.makeFromEncoded(bytes).toComposeImageBitmap()
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

/**
 * Load image as Painter from PhotoResult on Desktop
 */
actual fun PhotoResult.loadPainter(): Painter? {
    return try {
        val imageBitmap = loadImageBitmap()
        imageBitmap?.let { androidx.compose.ui.graphics.painter.BitmapPainter(it) }
    } catch (e: Exception) {
        null
    }
}

/**
 * Load image as Painter from GalleryPhotoResult on Desktop
 */
actual fun GalleryPhotoResult.loadPainter(): Painter? {
    return try {
        val imageBitmap = loadImageBitmap()
        imageBitmap?.let { androidx.compose.ui.graphics.painter.BitmapPainter(it) }
    } catch (e: Exception) {
        null
    }
}
