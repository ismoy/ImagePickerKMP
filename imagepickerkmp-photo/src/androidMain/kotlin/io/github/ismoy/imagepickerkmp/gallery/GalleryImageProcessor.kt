package io.github.ismoy.imagepickerkmp.gallery

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.graphics.scale
import androidx.exifinterface.media.ExifInterface
import io.github.ismoy.imagepickerkmp.camera.ExifDataExtractor
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.IMAGEPROCESSOR_TAG
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.NUMBER_ZERO
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_X
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_Y
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_X
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_Y
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.ORIENTATION_ROTATE_180
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.ORIENTATION_ROTATE_270
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.ORIENTATION_ROTATE_90
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.PREFIX_COMPRESSED
import io.github.ismoy.imagepickerkmp.logger.PhotoLogger
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.ExifData
import io.github.ismoy.imagepickerkmp.picker.GalleryPhotoResult
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
            
            val fileSize = getFileSize(context, uri)

            val exifData: ExifData? = if (includeExif) {
                runCatching { ExifDataExtractor.extractExifDataWithFallbacks(context, uri) }
                    .getOrNull()
            } else null

            return@withContext if (compressionLevel != null) {
                val bitmap = decodeCorrectedBitmap(context, uri, compressionLevel)
                if (bitmap != null) {
                    createResultFromBitmap(context, bitmap, fileName, mimeType, exifData, compressionLevel)
                } else {
                    createFallbackResult(context, uri, fileSize, fileName, mimeType, exifData)
                }
            } else {
                createFallbackResult(context, uri, fileSize, fileName, mimeType, exifData)
            }        } catch (e: Exception) {
            PhotoLogger.debug("$IMAGEPROCESSOR_TAG: ${e.message}")
            null
        }
    }

    private fun getFileSize(context: Context, uri: Uri): Long {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
            if (sizeIndex != -1 && cursor.moveToFirst()) {
                return cursor.getLong(sizeIndex)
            }
        }
        runCatching {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                return pfd.statSize
            }
        }
        return 0L
    }

    fun decodeCorrectedBitmap(
        context: Context,
        uri: Uri,
        compressionLevel: CompressionLevel? = null
    ): Bitmap? = runCatching {
        val rotation = context.contentResolver.openInputStream(uri)?.use { stream ->
            runCatching {
                ExifInterface(stream).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            }.getOrDefault(ExifInterface.ORIENTATION_NORMAL)
        } ?: ExifInterface.ORIENTATION_NORMAL

        val options = BitmapFactory.Options()
        
        if (compressionLevel != null) {
            options.inJustDecodeBounds = true
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }
            
            val maxDim = compressionLevel.toMaxDimension()
            options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, maxDim)
            options.inJustDecodeBounds = false
        }
        
        val raw = context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        } ?: return null
        
        val oriented = applyOrientationMatrix(raw, rotation)

        if (compressionLevel != null) applyDimensionCap(oriented, compressionLevel) else oriented
    }.getOrNull()
    
    private fun calculateInSampleSize(width: Int, height: Int, maxDim: Int): Int {
        var inSampleSize = 1
        if (width > maxDim || height > maxDim) {
            val halfWidth = width / 2
            val halfHeight = height / 2
            while (halfWidth / inSampleSize >= maxDim || halfHeight / inSampleSize >= maxDim) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

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
        compressionLevel: CompressionLevel
    ): GalleryPhotoResult? {
        val bytes = GalleryImageCompressor.compressBitmapToByteArray(bitmap, compressionLevel)
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
        context: Context,
        uri: Uri,
        fileSize: Long,
        fileName: String?,
        mimeType: String?,
        exifData: ExifData?
    ): GalleryPhotoResult {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        }
        return GalleryPhotoResult(
            uri = uri.toString(),
            width = options.outWidth,
            height = options.outHeight,
            fileName = fileName,
            fileSize = fileSize,
            mimeType = mimeType,
            exif = exifData
        )
    }
}
