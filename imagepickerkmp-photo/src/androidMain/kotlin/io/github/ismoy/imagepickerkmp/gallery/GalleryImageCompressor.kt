package io.github.ismoy.imagepickerkmp.gallery

import android.content.Context
import android.graphics.Bitmap
import io.github.ismoy.imagepickerkmp.gallery.GalleryFileUtils.getNameAndExtension
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.GALLERY_PROCESSOR_TAG
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.IMAGE_TEMP
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.MINVALUE_COMPRESSOR
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.NUMBER_THREE
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.PREFIX_COMPRESSED_GALLERY
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.PREFIX_GALLERY
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.SUFFIX_COMPRESSED_GALLERY
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.logger.PhotoLogger
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

    fun createTempImageFile(context: Context, imageBytes: ByteArray, fileName: String? = null): File? {
        val (name, extension) = fileName.getNameAndExtension()
        val storageDir = context.cacheDir.resolve(IMAGE_TEMP).also { it.mkdirs() }

        val tempFile = try {
            File.createTempFile(
                name ?: "$PREFIX_COMPRESSED_GALLERY${System.currentTimeMillis()}",
                extension ?: SUFFIX_COMPRESSED_GALLERY,
                storageDir
            )
        } catch (e: Exception) {
            PhotoLogger.debug("$GALLERY_PROCESSOR_TAG: Error creating temp file: ${e.javaClass.simpleName}")
            return null
        }
        return try {
            FileOutputStream(tempFile).use { it.write(imageBytes) }
            tempFile
        } catch (e: Exception) {
            tempFile.delete()
            PhotoLogger.debug("$GALLERY_PROCESSOR_TAG: Error writing temp file: ${e.javaClass.simpleName}")
            null
        }
    }
}
