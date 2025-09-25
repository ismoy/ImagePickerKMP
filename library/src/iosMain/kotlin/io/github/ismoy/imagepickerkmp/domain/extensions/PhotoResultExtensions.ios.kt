package io.github.ismoy.imagepickerkmp.domain.extensions

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
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

private val painterCacheIOS = mutableMapOf<String, Painter>()
private val imageBitmapCacheIOS = mutableMapOf<String, ImageBitmap>()
private val bytesCacheIOS = mutableMapOf<String, ByteArray>()
private val base64CacheIOS = mutableMapOf<String, String>()

/**
 * iOS implementation to load photo data as ByteArray from URI with automatic compression optimization.
 * Automatically applies HIGH compression to optimize memory usage and reduce file size.
 * 
 * @return ByteArray containing the optimized image data, or empty ByteArray if loading fails.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun PhotoResult.loadBytes(): ByteArray {
    return loadBytesFromURI(uri, CompressionLevel.HIGH, null)
}

/**
 * iOS implementation to load gallery photo data as ByteArray from URI with automatic compression optimization.
 * Automatically applies HIGH compression to optimize memory usage and reduce file size.
 * 
 * @return ByteArray containing the optimized image data, or empty ByteArray if loading fails.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun GalleryPhotoResult.loadBytes(): ByteArray {
    val cacheKey = "${this.uri}_bytes"
    return loadBytesFromURI(uri, CompressionLevel.HIGH, cacheKey)
}

/**
 * iOS implementation for loading photo data as Base64 string with memory optimization.
 * Uses automatic HIGH compression before encoding to Base64.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun PhotoResult.loadBase64(): String {
    return loadBase64FromURI(uri, null)
}

/**
 * iOS implementation for loading gallery photo data as Base64 string with memory optimization.
 * Uses automatic HIGH compression before encoding to Base64.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun GalleryPhotoResult.loadBase64(): String {
    val cacheKey = "${this.uri}_base64"
    return loadBase64FromURI(uri, cacheKey)
}

/**
 * iOS implementation to load photo as ImageBitmap for Compose usage with memory optimization.
 * Automatically applies HIGH compression to prevent memory issues.
 * 
 * @return ImageBitmap that can be used directly in Compose, or null if loading fails.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun PhotoResult.loadImageBitmap(): ImageBitmap? {
    return loadImageBitmapFromURI(uri, null)
}

/**
 * iOS implementation to load gallery photo as ImageBitmap for Compose usage with memory optimization.
 * Automatically applies HIGH compression to prevent memory issues.
 * 
 * @return ImageBitmap that can be used directly in Compose, or null if loading fails.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun GalleryPhotoResult.loadImageBitmap(): ImageBitmap? {
    val cacheKey = "${this.uri}_bitmap"
    return loadImageBitmapFromURI(uri, cacheKey)
}

/**
 * iOS implementation to load photo as Painter for Compose usage with memory optimization.
 * Automatically applies HIGH compression to prevent memory issues.
 * Implements internal caching to prevent unnecessary reloading.
 * 
 * @return Painter that can be used directly in Image composable, or null if loading fails.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun PhotoResult.loadPainter(): Painter? {
    val cacheKey = "${this.uri}_painter"
    return loadPainterFromURI(uri, cacheKey)
}

/**
 * iOS implementation to load gallery photo as Painter for Compose usage with memory optimization.
 * Automatically applies HIGH compression to prevent memory issues.
 * Implements internal caching to prevent unnecessary reloading during scroll operations.
 * 
 * @return Painter that can be used directly in Image composable, or null if loading fails.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun GalleryPhotoResult.loadPainter(): Painter? {
    val cacheKey = "${this.uri}_painter"
    return loadPainterFromURI(uri, cacheKey)
}

/**
 * Optimizes image bytes using configurable compression for memory efficiency on iOS.
 * @param originalBytes The original image bytes to optimize
 * @param compressionLevel The compression level to apply
 */
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

/**
 * Common function to load bytes from URI with optional caching.
 * @param uri The URI to load from
 * @param compressionLevel The compression level to apply
 * @param cacheKey Optional cache key for caching the result
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun loadBytesFromURI(uri: String, compressionLevel: CompressionLevel, cacheKey: String?): ByteArray {
    cacheKey?.let { key ->
        bytesCacheIOS[key]?.let { cached ->
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
            bytesCacheIOS[key] = result
        }
        result
    } catch (_: Exception) {
        ByteArray(0)
    }
}

/**
 * Common function to load Base64 from URI with optional caching.
 * @param uri The URI to load from
 * @param cacheKey Optional cache key for caching the result
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun loadBase64FromURI(uri: String, cacheKey: String?): String {
    cacheKey?.let { key ->
        base64CacheIOS[key]?.let { cached ->
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
                    base64CacheIOS[key] = result
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

/**
 * Common function to load ImageBitmap from URI with optional caching.
 * @param uri The URI to load from
 * @param cacheKey Optional cache key for caching the result
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun loadImageBitmapFromURI(uri: String, cacheKey: String?): ImageBitmap? {
    cacheKey?.let { key ->
        imageBitmapCacheIOS[key]?.let { cached ->
            return cached
        }
    }
    
    return runCatching {
        val optimizedBytes = loadBytesFromURI(uri, CompressionLevel.HIGH, null)
        if (optimizedBytes.isNotEmpty()) {
            val skiaImage = Image.makeFromEncoded(optimizedBytes)
            val imageBitmap = skiaImage.toComposeImageBitmap()
            cacheKey?.let { key ->
                imageBitmapCacheIOS[key] = imageBitmap
            }
            imageBitmap
        } else {
            null
        }
    }.getOrNull()
}

/**
 * Common function to load Painter from URI with caching.
 * @param uri The URI to load from
 * @param cacheKey Cache key for caching the result
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun loadPainterFromURI(uri: String, cacheKey: String): Painter? {
    painterCacheIOS[cacheKey]?.let { cached ->
        return cached
    }
    
    return runCatching {
        val imageBitmap = loadImageBitmapFromURI(uri, null)
        if (imageBitmap != null) {
            val painter = BitmapPainter(imageBitmap)
            painterCacheIOS[cacheKey] = painter
            painter
        } else {
            null
        }
    }.getOrNull()
}

/**
 * Resize image if it exceeds the maximum size while maintaining aspect ratio for iOS.
 */
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

/**
 * Resize image to specific size on iOS.
 */
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


