package io.github.ismoy.imagepickerkmp.data.gallery

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.graphics.scale
import androidx.exifinterface.media.ExifInterface
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.ExifData
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger
import io.github.ismoy.imagepickerkmp.data.processors.ExifDataExtractor
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.IMAGEPROCESSOR_TAG
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.IMAGE_PREFIX_TEXT
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_ZERO
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_X
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_Y
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_X
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_Y
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_ROTATE_180
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_ROTATE_270
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.ORIENTATION_ROTATE_90
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.PREFIX_COMPRESSED
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object GalleryImageProcessor {

    suspend fun processSelectedImageSuspend(
        context: Context,
        uri: Uri,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false
    ): GalleryPhotoResult? = withContext(Dispatchers.IO) {
        try {
            val fileName = GalleryFileUtils.getFileName(context, uri)
            val mimeType = GalleryFileUtils.getFileMimeType(context, uri)

            val originalBytes: ByteArray = context.contentResolver
                .openInputStream(uri)?.use { it.readBytes() }
                ?: return@withContext null

            if (compressionLevel == null && !includeExif) {
                return@withContext createFallbackResult(uri, originalBytes, fileName, mimeType, null)
            }

            val exifData: ExifData? = if (includeExif && mimeType?.startsWith(IMAGE_PREFIX_TEXT) == true) {
                runCatching { ExifDataExtractor.extractExifDataWithFallbacks(context, uri) }
                    .getOrNull()
            } else null

            return@withContext if (compressionLevel != null) {
                val bitmap = decodeCorrectedBitmapFromBytes(originalBytes, compressionLevel)
                if (bitmap != null) {
                    createResultFromBitmap(context, bitmap, fileName, mimeType, exifData, compressionLevel)
                } else {
                    createFallbackResult(uri, originalBytes, fileName, mimeType, exifData)
                }
            } else {
                createFallbackResult(uri, originalBytes, fileName, mimeType, exifData)
            }
        } catch (e: Exception) {
            DefaultLogger.logDebug("$IMAGEPROCESSOR_TAG: ${e.message}")
            null
        }
    }

    fun decodeCorrectedBitmapFromBytes(
        bytes: ByteArray,
        compressionLevel: CompressionLevel? = null
    ): Bitmap? = runCatching {
        val rotation = bytes.inputStream().use { stream ->
            runCatching {
                ExifInterface(stream).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            }.getOrDefault(ExifInterface.ORIENTATION_NORMAL)
        }

        val raw = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
        val oriented = applyOrientationMatrix(raw, rotation)

        if (compressionLevel != null) applyDimensionCap(oriented, compressionLevel) else oriented
    }.getOrNull()

    private fun applyOrientationMatrix(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90  -> matrix.postRotate(ORIENTATION_ROTATE_90)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(ORIENTATION_ROTATE_180)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(ORIENTATION_ROTATE_270)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(ORIENTATION_FLIP_HORIZONTAL_X, ORIENTATION_FLIP_HORIZONTAL_Y)
            ExifInterface.ORIENTATION_FLIP_VERTICAL   -> matrix.postScale(ORIENTATION_FLIP_VERTICAL_X, ORIENTATION_FLIP_VERTICAL_Y)
            ExifInterface.ORIENTATION_TRANSPOSE   -> { matrix.postRotate(ORIENTATION_ROTATE_90);  matrix.postScale(ORIENTATION_FLIP_HORIZONTAL_X, ORIENTATION_FLIP_HORIZONTAL_Y) }
            ExifInterface.ORIENTATION_TRANSVERSE  -> { matrix.postRotate(ORIENTATION_ROTATE_270); matrix.postScale(ORIENTATION_FLIP_VERTICAL_Y, ORIENTATION_FLIP_VERTICAL_X) }
            else -> return bitmap
        }
        return Bitmap.createBitmap(bitmap, NUMBER_ZERO, NUMBER_ZERO, bitmap.width, bitmap.height, matrix, true)
            .also { if (it != bitmap) bitmap.recycle() }
    }

    private fun applyDimensionCap(bitmap: Bitmap, compressionLevel: CompressionLevel): Bitmap {
        val maxDim = compressionLevel.toMaxDimension()
        val current = maxOf(bitmap.width, bitmap.height)
        if (current <= maxDim) return bitmap
        val scale = maxDim.toFloat() / current
        val tw = (bitmap.width  * scale).toInt()
        val th = (bitmap.height * scale).toInt()
        return bitmap.scale(tw, th).also { if (it != bitmap) bitmap.recycle() }
    }

    private fun createResultFromBitmap(
        context: Context,
        bitmap: Bitmap,
        fileName: String?,
        mimeType: String?,
        exifData: ExifData?,
        compressionLevel: CompressionLevel?
    ): GalleryPhotoResult? {
        val level = compressionLevel ?: CompressionLevel.LOW
        val bytes = GalleryImageCompressor.compressBitmapToByteArray(bitmap, level)
        val tempFile = GalleryImageCompressor.createTempImageFile(context, bytes) ?: return null
        return GalleryPhotoResult(
            uri = Uri.fromFile(tempFile).toString(),
            width = bitmap.width,
            height = bitmap.height,
            fileName = if (compressionLevel != null) "$PREFIX_COMPRESSED$fileName" else fileName,
            fileSize = bytes.size.toLong(),
            mimeType = mimeType,
            exif = exifData
        )
    }

    private fun createFallbackResult(
        uri: Uri,
        originalBytes: ByteArray,
        fileName: String?,
        mimeType: String?,
        exifData: ExifData?
    ): GalleryPhotoResult {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(originalBytes, NUMBER_ZERO, originalBytes.size, options)
        return GalleryPhotoResult(
            uri = uri.toString(),
            width = options.outWidth,
            height = options.outHeight,
            fileName = fileName,
            fileSize = originalBytes.size.toLong(),
            mimeType = mimeType,
            exif = exifData
        )
    }
}
