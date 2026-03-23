package io.github.ismoy.imagepickerkmp.data.gallery

import android.content.Context
import android.net.Uri
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.APPLICATION_PDF_TEXT
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.GALLERY_PROCESSOR_TAG
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.IMAGE_PREFIX_TEXT
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object GalleryFileProcessor {

    suspend fun processSelectedFile(
        context: Context,
        uri: Uri,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false
    ): GalleryPhotoResult? = withContext(Dispatchers.IO) {
        try {
            val mimeType = GalleryFileUtils.getFileMimeType(context, uri)
            val fileName = GalleryFileUtils.getFileName(context, uri)
            when {
                mimeType?.startsWith(APPLICATION_PDF_TEXT) == true ->
                    processPdfFile(context, uri, fileName, mimeType)
                mimeType?.startsWith(IMAGE_PREFIX_TEXT) == true ->
                    GalleryImageProcessor.processSelectedImageSuspend(context, uri, compressionLevel, includeExif)
                else ->
                    processGenericDocument(context, uri, fileName, mimeType)
            }
        } catch (e: Exception) {
            DefaultLogger.logDebug("$GALLERY_PROCESSOR_TAG${e.message}")
            null
        }
    }

    private fun processPdfFile(
        context: Context,
        uri: Uri,
        fileName: String?,
        mimeType: String
    ): GalleryPhotoResult? {
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
        return GalleryPhotoResult(
            uri = uri.toString(),
            width = null,
            height = null,
            fileName = fileName,
            fileSize = bytes.size.toLong(),
            mimeType = mimeType,
            exif = null
        )
    }

    private fun processGenericDocument(
        context: Context,
        uri: Uri,
        fileName: String?,
        mimeType: String?
    ): GalleryPhotoResult? {
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
        return GalleryPhotoResult(
            uri = uri.toString(),
            width = null,
            height = null,
            fileName = fileName,
            fileSize = bytes.size.toLong(),
            mimeType = mimeType,
            exif = null
        )
    }
}
