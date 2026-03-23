package io.github.ismoy.imagepickerkmp.data.gallery

import android.content.Context
import android.graphics.Bitmap
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.GALLERY_PROCESSOR_TAG
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.MINVALUE_COMPRESSOR
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_THREE
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.PREFIX_COMPRESSED_GALLERY
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.SUFFIX_COMPRESSED_GALLERY
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

internal object GalleryImageCompressor {

    fun compressBitmapToByteArray(bitmap: Bitmap, compressionLevel: CompressionLevel): ByteArray {
        val quality = compressionLevel.toJpegQuality()
        val estimatedSize = (bitmap.width * bitmap.height) / NUMBER_THREE
        val out = ByteArrayOutputStream(estimatedSize.coerceAtLeast(MINVALUE_COMPRESSOR))
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        return out.toByteArray()
    }

    fun createTempImageFile(context: Context, imageBytes: ByteArray): File? {
        val tempFile = try {
            File.createTempFile(
                "$PREFIX_COMPRESSED_GALLERY${System.currentTimeMillis()}",
                SUFFIX_COMPRESSED_GALLERY,
                context.cacheDir
            )
        } catch (e: Exception) {
            DefaultLogger.logDebug("$GALLERY_PROCESSOR_TAG: Error creating temp file: ${e.javaClass.simpleName}")
            return null
        }
        return try {
            FileOutputStream(tempFile).use { it.write(imageBytes) }
            tempFile
        } catch (e: Exception) {
            tempFile.delete()
            DefaultLogger.logDebug("$GALLERY_PROCESSOR_TAG: Error writing temp file: ${e.javaClass.simpleName}")
            null
        }
    }
}
