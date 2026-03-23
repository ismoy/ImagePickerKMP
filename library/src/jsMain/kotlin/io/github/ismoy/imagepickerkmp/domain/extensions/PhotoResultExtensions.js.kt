package io.github.ismoy.imagepickerkmp.domain.extensions

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import org.jetbrains.skia.Image
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
actual fun PhotoResult.loadBytes(): ByteArray {
    return try {
        val dataUrlPrefix = "data:"
        val base64Prefix = ";base64,"
        if (uri.startsWith(dataUrlPrefix) && uri.contains(base64Prefix)) {
            Base64.decode(uri.substringAfter(base64Prefix))
        } else {
            byteArrayOf()
        }
    } catch (e: Exception) {
        byteArrayOf()
    }
}

actual fun PhotoResult.loadBase64(): String {
    return try {
        val base64Prefix = ";base64,"
        if (uri.contains(base64Prefix)) uri.substringAfter(base64Prefix) else ""
    } catch (e: Exception) {
        ""
    }
}

actual fun PhotoResult.loadImageBitmap(): ImageBitmap? {
    return try {
        val bytes = loadBytes()
        if (bytes.isNotEmpty()) Image.makeFromEncoded(bytes).toComposeImageBitmap() else null
    } catch (e: Exception) {
        null
    }
}

actual fun PhotoResult.loadPainter(): Painter? {
    return try {
        loadImageBitmap()?.let { androidx.compose.ui.graphics.painter.BitmapPainter(it) }
    } catch (e: Exception) {
        null
    }
}
