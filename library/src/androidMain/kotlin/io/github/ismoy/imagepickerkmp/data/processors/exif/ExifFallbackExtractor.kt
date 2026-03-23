package io.github.ismoy.imagepickerkmp.data.processors.exif

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.exifinterface.media.ExifInterface
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.PREFIX_EXIF_CALLBACK
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.SUFFIX_COMPRESSED_GALLERY
import io.github.ismoy.imagepickerkmp.domain.models.ExifData


internal object ExifFallbackExtractor {

    fun extractWithFallbacks(context: Context, uri: Uri): ExifData? {
        ExifInterfaceHelper.createFromUri(context, uri)?.let { exif ->
            return ExifDataParser.parseExifData(exif)
        }

        extractFromTempFile(context, uri)?.let { return it }

        extractViaFileDescriptor(context, uri)?.let { return it }

        return null
    }

    private fun extractFromTempFile(context: Context, uri: Uri): ExifData? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = java.io.File.createTempFile(
                "$PREFIX_EXIF_CALLBACK${System.currentTimeMillis()}",
                SUFFIX_COMPRESSED_GALLERY,
                context.cacheDir
            )
            try {
                inputStream.use { stream ->
                    java.io.FileOutputStream(tempFile).use { out -> stream.copyTo(out) }
                }
                val tempUri = Uri.fromFile(tempFile)
                ExifInterfaceHelper.createFromUri(context, tempUri)?.let {
                    ExifDataParser.parseExifData(it)
                }
            } finally {
                tempFile.delete()
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun extractViaFileDescriptor(context: Context, uri: Uri): ExifData? {
        return try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd: ParcelFileDescriptor ->
                val exif = ExifInterface(pfd.fileDescriptor)
                ExifDataParser.parseExifData(exif)
            }
        } catch (_: Exception) {
            null
        }
    }
}
