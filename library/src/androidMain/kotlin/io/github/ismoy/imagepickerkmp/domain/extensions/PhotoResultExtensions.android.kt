package io.github.ismoy.imagepickerkmp.domain.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import java.io.ByteArrayOutputStream
import kotlin.math.max
import androidx.core.net.toUri
import androidx.core.graphics.scale
import java.lang.Class.forName
import java.io.File


private val painterCache     = object : LruCache<String, Painter>(50) {}
private val imageBitmapCache = object : LruCache<String, ImageBitmap>(50) {}
private val bytesCache       = object : LruCache<String, ByteArray>(50) {}
private val base64Cache      = object : LruCache<String, String>(50) {}

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
 * Android implementation for loading photo data as Base64 string with memory optimization.
 * Uses PNG format for lossless compression before encoding to Base64.
 * Handles both content URIs and local file paths (including cropped images).
 */
actual fun PhotoResult.loadBase64(): String {
    return loadBase64FromURI(uri, CompressionLevel.HIGH, null)
}

/**
 * Android implementation to load photo as ImageBitmap for Compose usage with memory optimization.
 * Uses optimized direct bitmap loading to prevent multiple decompression cycles. *
 * @return ImageBitmap that can be used directly in Compose, or null if loading fails.
 */
actual fun PhotoResult.loadImageBitmap(): ImageBitmap? {
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

private fun loadBytesFromURI(uri: String, compressionLevel: CompressionLevel, cacheKey: String?): ByteArray {
    cacheKey?.let { key ->
        bytesCache.get(key)?.let { cached -> return cached }
    }
    return try {
        val context = getApplicationContext()
        if (uri.startsWith("/") || uri.startsWith("file://")) {
            val filePath = if (uri.startsWith("file://")) uri.removePrefix("file://") else uri
            val file = File(filePath)
            if (file.exists()) {
                val originalBytes = file.readBytes()
                val result = optimizeImageBytesWithQuality(originalBytes, compressionLevel)
                cacheKey?.let { key -> bytesCache.put(key, result) }
                return result
            }
        }
        val uriObj = uri.toUri()
        context.contentResolver.openInputStream(uriObj)?.use { inputStream ->
            val originalBytes = inputStream.readBytes()
            val result = optimizeImageBytesWithQuality(originalBytes, compressionLevel)
            cacheKey?.let { key -> bytesCache.put(key, result) }
            result
        } ?: ByteArray(0)
    } catch (_: Exception) {
        ByteArray(0)
    }
}

private fun loadOptimizedBytesFromURI(uri: String, cacheKey: String): ByteArray {
    bytesCache.get(cacheKey)?.let { return it }
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
                bytesCache.put(cacheKey, result)
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
            bytesCache.put(cacheKey, result)
            result
        } ?: ByteArray(0)
    } catch (_: Exception) {
        ByteArray(0)
    }
}


private fun loadBase64FromURI(uri: String, compressionLevel: CompressionLevel, cacheKey: String?): String {
    cacheKey?.let { key -> base64Cache.get(key)?.let { return it } }
    return try {
        val optimizedBytes = loadBytesFromURI(uri, compressionLevel, null)
        if (optimizedBytes.isEmpty()) {
            ""
        } else {
            try {
                val result = Base64.encodeToString(optimizedBytes, Base64.DEFAULT)
                cacheKey?.let { key -> base64Cache.put(key, result) }
                result
            } catch (_: Exception) { "" }
        }
    } catch (_: Exception) { "" }
}

private fun loadImageBitmapFromURI(uri: String, cacheKey: String?): ImageBitmap? {
    cacheKey?.let { key -> imageBitmapCache.get(key)?.let { return it } }
    return runCatching {
        val context = getApplicationContext()
        if (uri.startsWith("/") || uri.startsWith("file://")) {
            val filePath = if (uri.startsWith("file://")) uri.removePrefix("file://") else uri
            val file = File(filePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(filePath) ?: return null
                val resultBitmap = bitmap.asImageBitmap()
                cacheKey?.let { key -> imageBitmapCache.put(key, resultBitmap) }
                resultBitmap
            } else null
        } else {
            val uriObj = uri.toUri()
            context.contentResolver.openInputStream(uriObj)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream) ?: return null
                val resultBitmap = bitmap.asImageBitmap()
                cacheKey?.let { key -> imageBitmapCache.put(key, resultBitmap) }
                resultBitmap
            }
        }
    }.getOrNull()
}

private fun loadOptimizedImageBitmapFromURI(uri: String, cacheKey: String): ImageBitmap? {
    imageBitmapCache.get(cacheKey)?.let { return it }
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
                imageBitmapCache.put(cacheKey, resultBitmap)
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
            imageBitmapCache.put(cacheKey, resultBitmap)
            resultBitmap
        }
    }.getOrNull()
}

private fun loadPainterFromURI(uri: String, cacheKey: String): Painter? {
    painterCache.get(cacheKey)?.let { return it }
    return try {
        val context = getApplicationContext()
        if (uri.startsWith("/") || uri.startsWith("file://")) {
            val filePath = if (uri.startsWith("file://")) uri.removePrefix("file://") else uri
            val file = File(filePath)
            if (file.exists()) {
                // Fix A2: use inSampleSize to avoid loading full-resolution bitmap for Painter
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
                val painter = BitmapPainter(bitmap.asImageBitmap())
                painterCache.put(cacheKey, painter)
                return painter
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
            val painter = BitmapPainter(bitmap.asImageBitmap())
            painterCache.put(cacheKey, painter)
            painter
        }
    } catch (_: Exception) { null }
}

private fun optimizeImageBytesWithQuality(originalBytes: ByteArray, compressionLevel: CompressionLevel): ByteArray {
    return try {
        val originalBitmap = BitmapFactory.decodeByteArray(originalBytes, 0, originalBytes.size)
            ?: return originalBytes
        val optimizedBitmap = processImageWithCustomCompression(originalBitmap, compressionLevel)
        val outputStream = ByteArrayOutputStream()
        // Use JPEG instead of PNG: PNG lossless encoding can be 5–10× larger than the original JPEG.
        optimizedBitmap.compress(Bitmap.CompressFormat.JPEG, compressionLevel.toJpegQuality(), outputStream)
        if (optimizedBitmap != originalBitmap) originalBitmap.recycle()
        optimizedBitmap.recycle()
        outputStream.toByteArray()
    } catch (_: Exception) {
        originalBytes
    }
}

private fun processImageWithCustomCompression(bitmap: Bitmap, compressionLevel: CompressionLevel): Bitmap {
    val maxDimension = compressionLevel.toMaxDimension()
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

@SuppressLint("PrivateApi")
private fun getApplicationContext(): Context {
    // Cache the result so reflection is invoked only once per process lifetime.
    cachedAppContext?.get()?.let { return it }
    val ctx = try {
        val activityThreadClass = forName("android.app.ActivityThread")
        val currentApplicationMethod = activityThreadClass.getMethod("currentApplication")
        currentApplicationMethod.invoke(null) as Context
    } catch (_: Exception) {
        try {
            val systemServiceClass = forName("android.app.AppGlobals")
            val getInitialApplicationMethod = systemServiceClass.getMethod("getInitialApplication")
            getInitialApplicationMethod.invoke(null) as Context
        } catch (e2: Exception) {
            throw RuntimeException("Cannot obtain application context.", e2)
        }
    }
    cachedAppContext = java.lang.ref.WeakReference(ctx)
    return ctx
}

private var cachedAppContext: java.lang.ref.WeakReference<Context>? = null

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

private fun compressBitmapToByteArrayDirect(bitmap: Bitmap): ByteArray {
    return try {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
        outputStream.toByteArray()
    } catch (_: Exception) {
        ByteArray(0)
    }
}
