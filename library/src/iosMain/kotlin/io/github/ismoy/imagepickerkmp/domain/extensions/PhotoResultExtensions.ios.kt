package io.github.ismoy.imagepickerkmp.domain.extensions

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create
import platform.Foundation.dataWithContentsOfURL
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.Foundation.NSLock


private class SynchronizedCache<V> {
    private val map = mutableMapOf<String, V>()
    private val lock = NSLock()

    fun get(key: String): V? {
        lock.lock()
        return try { map[key] } finally { lock.unlock() }
    }

    fun put(key: String, value: V) {
        lock.lock()
        try { map[key] = value } finally { lock.unlock() }
    }

    fun clear() {
        lock.lock()
        try { map.clear() } finally { lock.unlock() }
    }
}

private val painterCacheIOS      = SynchronizedCache<Painter>()
private val imageBitmapCacheIOS  = SynchronizedCache<ImageBitmap>()
private val bytesCacheIOS        = SynchronizedCache<ByteArray>()
private val base64CacheIOS       = SynchronizedCache<String>()

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun PhotoResult.loadBytes(): ByteArray {
    val cacheKey = "${this.uri}_bytes"
    return loadBytesFromURI(uri, CompressionLevel.HIGH, cacheKey)
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun PhotoResult.loadBase64(): String {
    val cacheKey = "${this.uri}_base64"
    return loadBase64FromURI(uri, cacheKey)
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun PhotoResult.loadImageBitmap(): ImageBitmap? {
    val cacheKey = "${this.uri}_bitmap"
    return loadImageBitmapFromURI(uri, cacheKey)
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun PhotoResult.loadPainter(): Painter? {
    val cacheKey = "${this.uri}_painter"
    return loadPainterFromURI(uri, cacheKey)
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun optimizeImageBytesIOS(originalBytes: ByteArray, compressionLevel: CompressionLevel = CompressionLevel.HIGH): ByteArray {
    return try {
        val nsData = originalBytes.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = originalBytes.size.toULong())
        }
        
        val uiImage = UIImage.imageWithData(nsData) ?: return originalBytes
        val quality = compressionLevel.toQualityValue()
        val maxDimension = when (compressionLevel) {
            CompressionLevel.LOW -> 2560.0
            CompressionLevel.MEDIUM -> 1920.0
            CompressionLevel.HIGH -> 1280.0
        }
        val processedImage = resizeImageIfNeededIOS(uiImage, maxDimension)
        val compressedData = UIImageJPEGRepresentation(processedImage, quality)
            ?: return originalBytes
        ByteArray(compressedData.length.toInt()).apply {
            usePinned { pinned ->
                compressedData.bytes?.let { bytes ->
                    platform.posix.memcpy(pinned.addressOf(0), bytes, compressedData.length)
                }
            }
        }
    } catch (_: Exception) {
        originalBytes
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun loadBytesFromURI(uri: String, compressionLevel: CompressionLevel, cacheKey: String?):
        ByteArray {
    cacheKey?.let { key ->
        bytesCacheIOS.get(key)?.let { cached ->
            return cached
        }
    }
    
    return try {
        val nsUrl = NSURL.URLWithString(uri) ?: return ByteArray(0)
        val nsData = NSData.dataWithContentsOfURL(nsUrl) ?: return ByteArray(0)
        val originalBytes = ByteArray(nsData.length.toInt()).apply {
            usePinned { pinned ->
                nsData.bytes?.let { bytes ->
                    platform.posix.memcpy(pinned.addressOf(0), bytes, nsData.length)
                }
            }
        }
        val result = optimizeImageBytesIOS(originalBytes, compressionLevel)
        cacheKey?.let { key ->
            bytesCacheIOS.put(key, result)
        }
        result
    } catch (_: Exception) {
        ByteArray(0)
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun loadBase64FromURI(uri: String, cacheKey: String?): String {
    cacheKey?.let { key ->
        base64CacheIOS.get(key)?.let { cached ->
            return cached
        }
    }
    
    return runCatching {
        val optimizedBytes = loadBytesFromURI(uri, CompressionLevel.HIGH, null)
        if (optimizedBytes.isNotEmpty()) {
            val nsData = optimizedBytes.usePinned { pinned ->
                NSData.create(bytes = pinned.addressOf(0), length = optimizedBytes.size.toULong())
            }
            val result = nsData.base64EncodedStringWithOptions(0u)
            cacheKey?.let { key ->
                if (result.isNotEmpty()) {
                    base64CacheIOS.put(key, result)
                }
            }
            result
        } else {
            ""
        }
    }.getOrElse {
        ""
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun loadImageBitmapFromURI(uri: String, cacheKey: String?): ImageBitmap? {
    cacheKey?.let { key ->
        imageBitmapCacheIOS.get(key)?.let { cached ->
            return cached
        }
    }
    
    return runCatching {
        val optimizedBytes = loadBytesFromURI(uri, CompressionLevel.HIGH, null)
        if (optimizedBytes.isNotEmpty()) {
            val skiaImage = Image.makeFromEncoded(optimizedBytes)
            val imageBitmap = skiaImage.toComposeImageBitmap()
            cacheKey?.let { key ->
                imageBitmapCacheIOS.put(key, imageBitmap)
            }
            imageBitmap
        } else {
            null
        }
    }.getOrNull()
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun loadPainterFromURI(uri: String, cacheKey: String): Painter? {
    painterCacheIOS.get(cacheKey)?.let { cached ->
        return cached
    }
    
    return runCatching {
        val imageBitmap = loadImageBitmapFromURI(uri, null)
        if (imageBitmap != null) {
            val painter = BitmapPainter(imageBitmap)
            painterCacheIOS.put(cacheKey, painter)
            painter
        } else {
            null
        }
    }.getOrNull()
}

@OptIn(ExperimentalForeignApi::class)
private fun resizeImageIfNeededIOS(image: UIImage, maxSize: Double): UIImage {
    return image.size.useContents { 
        if (width > maxSize || height > maxSize) {
            val aspectRatio = width / height
            val newSizeValue = if (width > height) {
                CGSizeMake(maxSize, maxSize / aspectRatio)
            } else {
                CGSizeMake(maxSize * aspectRatio, maxSize)
            }
            
            return@useContents resizeImageIOS(image, newSizeValue)
        } else {
            return@useContents image
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun resizeImageIOS(image: UIImage, newSize: CValue<platform.CoreGraphics.CGSize>): UIImage {
    UIGraphicsBeginImageContextWithOptions(newSize, false, 0.0)
    newSize.useContents {
        image.drawInRect(CGRectMake(0.0, 0.0, width, height))
    }
    val resizedImage = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return resizedImage ?: image
}


