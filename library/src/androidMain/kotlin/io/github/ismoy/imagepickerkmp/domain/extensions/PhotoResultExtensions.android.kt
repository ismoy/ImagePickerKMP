package io.github.ismoy.imagepickerkmp.domain.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import java.io.ByteArrayOutputStream
import kotlin.math.max
import java.util.concurrent.ConcurrentHashMap
import androidx.core.net.toUri
import androidx.core.graphics.scale
import java.lang.Class.forName
import java.io.File

private val painterCache = ConcurrentHashMap<String, Painter>()
private val imageBitmapCache = ConcurrentHashMap<String, ImageBitmap>()
private val bytesCache = ConcurrentHashMap<String, ByteArray>()
private val base64Cache = ConcurrentHashMap<String, String>()

/**
 * Android implementation to load photo data as ByteArray from URI with automatic compression optimization.
 * Uses PNG format for lossless compression to maintain maximum quality, especially important for cropped images.
 * Handles both content URIs and local file paths (including cropped images).
 * 
 * @return ByteArray containing the optimized image data in PNG format, or empty ByteArray if loading fails.
 */
actual fun PhotoResult.loadBytes(): ByteArray {
    return loadBytesFromURI(uri, CompressionLevel.MEDIUM, null)
}

/**
 * Android implementation to load gallery photo data as ByteArray from URI with PNG compression optimization.
 * Uses optimized BitmapFactory.Options to reduce memory usage during loading and PNG for lossless compression.
 * Handles both content URIs and local file paths (including cropped images).
 * 
 * @return ByteArray containing the highly optimized image data in PNG format, or empty ByteArray if loading fails.
 */
actual fun GalleryPhotoResult.loadBytes(): ByteArray {
    val cacheKey = "${this.uri}_bytes"
    return loadOptimizedBytesFromURI(uri, cacheKey)
}

/**
 * Android implementation for loading photo data as Base64 string with memory optimization.
 * Uses PNG format for lossless compression before encoding to Base64.
 * Handles both content URIs and local file paths (including cropped images).
 */
actual fun PhotoResult.loadBase64(): String {
    return loadBase64FromURI(uri, CompressionLevel.HIGH, null)
}

/**
 * Android implementation for loading gallery photo data as Base64 string with memory optimization.
 * Uses cached bytes from loadBytes() to avoid reprocessing the image multiple times.
 */
actual fun GalleryPhotoResult.loadBase64(): String {
    val cacheKey = "${this.uri}_base64"
    base64Cache[cacheKey]?.let { cached ->
        return cached
    }
    
    return try {
        // Directly use the optimized bytes to avoid double processing
        val optimizedBytes = loadOptimizedBytesFromURI(uri, "${this.uri}_bytes")
        if (optimizedBytes.isNotEmpty()) {
            val result = Base64.encodeToString(optimizedBytes, Base64.NO_WRAP)
            base64Cache[cacheKey] = result
            result
        } else {
            ""
        }
    } catch (_: Exception) {
        ""
    }
}

/**
 * Android implementation to load photo as ImageBitmap for Compose usage with memory optimization.
 * Uses optimized direct bitmap loading to prevent multiple decompression cycles. *
 * @return ImageBitmap that can be used directly in Compose, or null if loading fails.
 */
actual fun PhotoResult.loadImageBitmap(): ImageBitmap? {
    return loadImageBitmapFromURI(uri, null)
}

/**
 * Android implementation to load gallery photo as ImageBitmap for Compose usage with memory optimization.
 * Uses optimized direct bitmap loading to prevent multiple decompression cycles.
 * Implements internal caching to prevent unnecessary reloading during scroll.
 * Handles both content URIs and local file paths (including cropped images).
 * 
 * @return ImageBitmap that can be used directly in Compose, or null if loading fails.
 */
actual fun GalleryPhotoResult.loadImageBitmap(): ImageBitmap? {
    val cacheKey = "${this.uri}_bitmap"
    return loadOptimizedImageBitmapFromURI(uri, cacheKey)
}

/**
 * Android implementation to load photo as Painter for Compose usage with memory optimization.
 * Automatically applies HIGH compression to prevent memory issues.
 * Implements internal caching to prevent unnecessary reloading.
 * 
 * @return Painter that can be used directly in Image composable, or null if loading fails.
 */
actual fun PhotoResult.loadPainter(): Painter? {
    val cacheKey = "${this.uri}_painter"
    return loadPainterFromURI(uri, cacheKey)
}

/**
 * Android implementation to load gallery photo as Painter for Compose usage with memory optimization.
 * Uses direct bitmap loading with fallback to ensure reliability.
 * Implements internal caching to prevent unnecessary reloading during scroll operations.
 * 
 * @return Painter that can be used directly in Image composables, or null if loading fails.
 */
actual fun GalleryPhotoResult.loadPainter(): Painter? {
    val cacheKey = "${this.uri}_painter"
    return loadPainterFromURI(uri, cacheKey)
}
/**
 * Common function to load bytes from URI with optional caching and compression.
 * @param uri The URI to load from
 * @param compressionLevel The compression level to apply
 * @param cacheKey Optional cache key for caching the result
 */
private fun loadBytesFromURI(uri: String, compressionLevel: CompressionLevel, cacheKey: String?): ByteArray {
    cacheKey?.let { key ->
        bytesCache[key]?.let { cached ->
            return cached
        }
    }
    
    return try {
        val context = getApplicationContext()
        
        if (uri.startsWith("/") || uri.startsWith("file://")) {
            val filePath = if (uri.startsWith("file://")) uri.removePrefix("file://") else uri
            val file = File(filePath)
            if (file.exists()) {
                val originalBytes = file.readBytes()
                val result = optimizeImageBytesWithQuality(originalBytes, compressionLevel)
                cacheKey?.let { key -> bytesCache[key] = result }
                return result
            }
        }
        
        val uriObj = uri.toUri()
        context.contentResolver.openInputStream(uriObj)?.use { inputStream ->
            val originalBytes = inputStream.readBytes()
            val result = optimizeImageBytesWithQuality(originalBytes, compressionLevel)
            cacheKey?.let { key -> bytesCache[key] = result }
            result
        } ?: ByteArray(0)
    } catch (_: Exception) {
        ByteArray(0)
    }
}

/**
 * Optimized function to load bytes from URI with advanced bitmap processing and caching.
 * Uses BitmapFactory.Options for memory-efficient loading.
 */
private fun loadOptimizedBytesFromURI(uri: String, cacheKey: String): ByteArray {
    bytesCache[cacheKey]?.let { cached ->
        return cached
    }
    return try {
        val context = getApplicationContext()
        if (uri.startsWith("/") || uri.startsWith("file://")) {
            val filePath = if (uri.startsWith("file://")) uri.removePrefix("file://") else uri
            val file = File(filePath)
            if (file.exists()) {
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                    inPreferredConfig = Bitmap.Config.RGB_565
                }
                BitmapFactory.decodeFile(filePath, options)
                options.inSampleSize = calculateOptimalSampleSize(options, 1280)
                options.inJustDecodeBounds = false
                val bitmap = BitmapFactory.decodeFile(filePath, options) ?: return ByteArray(0)
                val result = compressBitmapToByteArrayDirect(bitmap)
                bitmap.recycle()
                bytesCache[cacheKey] = result
                return result
            }
        }
        val uriObj = uri.toUri()
        context.contentResolver.openInputStream(uriObj)?.use { inputStream ->
            val imageBytes = inputStream.readBytes()
            if (imageBytes.isEmpty()) return ByteArray(0)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)
            options.inSampleSize = calculateOptimalSampleSize(options, 1280)
            options.inJustDecodeBounds = false
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options) ?: return ByteArray(0)
            val result = compressBitmapToByteArrayDirect(bitmap)
            bitmap.recycle()
            bytesCache[cacheKey] = result
            result
        } ?: ByteArray(0)
    } catch (_: Exception) {
        ByteArray(0)
    }
}

/**
 * Common function to load Base64 from URI with compression.
 * @param uri The URI to load from
 * @param compressionLevel The compression level to apply
 * @param cacheKey Optional cache key for caching the result
 */
private fun loadBase64FromURI(uri: String, compressionLevel: CompressionLevel, cacheKey: String?): String {
    cacheKey?.let { key ->
        base64Cache[key]?.let { cached ->
            return cached
        }
    }
    return try {
        val optimizedBytes = loadBytesFromURI(uri, compressionLevel, null)
        if (optimizedBytes.isEmpty()) {
            ""
        } else {
            try {
                val result = Base64.encodeToString(optimizedBytes, Base64.DEFAULT)
                cacheKey?.let { key -> base64Cache[key] = result }
                result
            } catch (_: Exception) {
                ""
            }
        }
    } catch (_: Exception) {
        ""
    }
}

/**
 * Common function to load ImageBitmap from URI with optional caching.
 * @param uri The URI to load from
 * @param cacheKey Optional cache key for caching the result
 */
private fun loadImageBitmapFromURI(uri: String, cacheKey: String?): ImageBitmap? {
    cacheKey?.let { key ->
        imageBitmapCache[key]?.let { cached ->
            return cached
        }
    }
    
    return runCatching {
        val context = getApplicationContext()
        
        if (uri.startsWith("/") || uri.startsWith("file://")) {
            val filePath = if (uri.startsWith("file://")) uri.removePrefix("file://") else uri
            val file = File(filePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(filePath) ?: return null
                val resultBitmap = bitmap.asImageBitmap()
                cacheKey?.let { key -> imageBitmapCache[key] = resultBitmap }
                resultBitmap
            } else {
                null
            }
        } else {
            val uriObj = uri.toUri()
            context.contentResolver.openInputStream(uriObj)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream) ?: return null
                val resultBitmap = bitmap.asImageBitmap()
                cacheKey?.let { key -> imageBitmapCache[key] = resultBitmap }
                resultBitmap
            }
        }
    }.getOrNull()
}

/**
 * Optimized function to load ImageBitmap from URI with advanced bitmap processing and caching.
 */
private fun loadOptimizedImageBitmapFromURI(uri: String, cacheKey: String): ImageBitmap? {
    imageBitmapCache[cacheKey]?.let { cached ->
        return cached
    }
    
    return runCatching {
        val context = getApplicationContext()
        
        if (uri.startsWith("/") || uri.startsWith("file://")) {
            val filePath = if (uri.startsWith("file://")) uri.removePrefix("file://") else uri
            val file = File(filePath)
            if (file.exists()) {
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                    inPreferredConfig = Bitmap.Config.RGB_565
                }
                BitmapFactory.decodeFile(filePath, options)
                if (options.outWidth > 1280 || options.outHeight > 1280) {
                    options.inSampleSize = calculateOptimalSampleSize(options, 1280)
                }
                options.inJustDecodeBounds = false
                val bitmap = BitmapFactory.decodeFile(filePath, options) ?: return null
                val resultBitmap = bitmap.asImageBitmap()
                imageBitmapCache[cacheKey] = resultBitmap
                return resultBitmap
            }
        }
        
        val uriObj = uri.toUri()
        context.contentResolver.openInputStream(uriObj)?.use { inputStream ->
            val imageBytes = inputStream.readBytes()
            if (imageBytes.isEmpty()) return null
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)
            if (options.outWidth > 1280 || options.outHeight > 1280) {
                options.inSampleSize = calculateOptimalSampleSize(options, 1280)
            }
            options.inJustDecodeBounds = false
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options) ?: return null
            val resultBitmap = bitmap.asImageBitmap()
            imageBitmapCache[cacheKey] = resultBitmap
            resultBitmap
        }
    }.getOrNull()
}

/**
 * Common function to load Painter from URI with caching.
 * @param uri The URI to load from
 * @param cacheKey Cache key for caching the result
 */
private fun loadPainterFromURI(uri: String, cacheKey: String): Painter? {
    painterCache[cacheKey]?.let { cached ->
        return cached
    }
    
    return try {
        val context = getApplicationContext()
        
        if (uri.startsWith("/") || uri.startsWith("file://")) {
            val filePath = if (uri.startsWith("file://")) uri.removePrefix("file://") else uri
            val file = File(filePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(filePath) ?: return null
                val imageBitmap = bitmap.asImageBitmap()
                val painter = BitmapPainter(imageBitmap)
                painterCache[cacheKey] = painter
                return painter
            }
        }
        
        val uriObj = uri.toUri()
        context.contentResolver.openInputStream(uriObj)?.use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream) ?: return null
            val imageBitmap = bitmap.asImageBitmap()
            val painter = BitmapPainter(imageBitmap)
            painterCache[cacheKey] = painter
            painter
        }
    } catch (_: Exception) {
        null
    }
}

/**
 * Optimizes image bytes with configurable compression level and PNG format for lossless quality.
 * Maintains the best possible quality while optimizing dimensions based on compression level.
 */
private fun optimizeImageBytesWithQuality(originalBytes: ByteArray, compressionLevel: CompressionLevel): ByteArray {
    return try {
        val originalBitmap = BitmapFactory.decodeByteArray(originalBytes, 0, originalBytes.size)
            ?: return originalBytes
        val optimizedBitmap = processImageWithCustomCompression(originalBitmap, compressionLevel)
        val outputStream = ByteArrayOutputStream()
        optimizedBitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
        if (optimizedBitmap != originalBitmap) {
            originalBitmap.recycle()
        }
        optimizedBitmap.recycle()
        
        outputStream.toByteArray()
    } catch (_: Exception) {
        originalBytes
    }
}

/**
 * Processes bitmap with configurable compression for better quality control.
 */
private fun processImageWithCustomCompression(bitmap: Bitmap, compressionLevel: CompressionLevel): Bitmap {
    val maxDimension = when (compressionLevel) {
        CompressionLevel.LOW -> 2560
        CompressionLevel.MEDIUM -> 1920
        CompressionLevel.HIGH -> 1280
    }
    
    val currentMaxDimension = max(bitmap.width, bitmap.height)
    
    return if (currentMaxDimension > maxDimension) {
        val scale = maxDimension.toFloat() / currentMaxDimension.toFloat()
        val targetWidth = (bitmap.width * scale).toInt()
        val targetHeight = (bitmap.height * scale).toInt()
        
        val resizedBitmap = bitmap.scale(targetWidth, targetHeight)
        resizedBitmap
    } else {
        bitmap
    }
}

/**
 * Helper function to get application context.
 * Uses reflection to get the current application context without requiring setup.
 */
@SuppressLint("PrivateApi")
private fun getApplicationContext(): Context {
    return try {
        val activityThreadClass = forName("android.app.ActivityThread")
        val currentApplicationMethod = activityThreadClass.getMethod("currentApplication")
        val application = currentApplicationMethod.invoke(null) as Context
        application
    } catch (_: Exception) {
        try {
            val systemServiceClass = forName("android.app.AppGlobals")
            val getInitialApplicationMethod = systemServiceClass.getMethod("getInitialApplication")
            val application = getInitialApplicationMethod.invoke(null) as Context
            application
        } catch (e2: Exception) {
            throw RuntimeException("Cannot obtain application context. Please ensure the app is properly initialized.", e2)
        }
    }
}

/**
 * Calculates optimal sample size for memory-efficient bitmap loading.
 * Reduces memory usage by pre-sampling images during loading.
 */
private fun calculateOptimalSampleSize(options: BitmapFactory.Options, targetMaxDimension: Int): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > targetMaxDimension || width > targetMaxDimension) {
        val halfHeight = height / 2
        val halfWidth = width / 2
        while ((halfHeight / inSampleSize) >= targetMaxDimension && 
               (halfWidth / inSampleSize) >= targetMaxDimension) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}

/**
 * Directly compresses bitmap to ByteArray using PNG format without intermediate steps.
 * More memory efficient than the standard approach and maintains lossless quality.
 */
private fun compressBitmapToByteArrayDirect(bitmap: Bitmap): ByteArray {
    return try {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
        outputStream.toByteArray()
    } catch (_: Exception) {
        ByteArray(0)
    }
}
