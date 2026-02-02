package io.github.ismoy.imagepickerkmp.presentation.ui.components.gallery

import android.content.Context
import android.net.Uri
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Processor for selected files from gallery.
 */
internal object GalleryFileProcessor {
    
    /**
     * Process selected file (image or PDF) appropriately.
     */
    suspend fun processSelectedFile(
        context: Context,
        uri: Uri,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false
    ): GalleryPhotoResult? {
        return withContext(Dispatchers.IO) {
            try {
                val mimeType = GalleryFileUtils.getFileMimeType(context, uri)
                val fileName = GalleryFileUtils.getFileName(context, uri)
                
                when {
                    mimeType?.startsWith("application/pdf") == true -> {
                        processPdfFile(context, uri, fileName, mimeType)
                    }
                    mimeType?.startsWith("image/") == true -> {
                        GalleryImageProcessor.processSelectedImageSuspend(
                            context, 
                            uri, 
                            compressionLevel, 
                            includeExif
                        )
                    }
                    else -> {
                        processGenericDocument(context, uri, fileName, mimeType)
                    }
                }
            } catch (e: Exception) {
                logDebug(" Error processing selected file: ${e.message}")
                null
            }
        }
    }
    
    /**
     * Process PDF file.
     */
    private fun processPdfFile(
        context: Context,
        uri: Uri,
        fileName: String?,
        mimeType: String
    ): GalleryPhotoResult? {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileBytes = inputStream?.readBytes()
        inputStream?.close()
        
        if (fileBytes != null) {
            val fileSizeInBytes = fileBytes.size.toLong()
            logDebug(" PDF file selected - File size: ${fileSizeInBytes} bytes (${fileSizeInBytes / 1024}KB), MIME: $mimeType")
            return GalleryPhotoResult(
                uri = uri.toString(),
                width = null,
                height = null,
                fileName = fileName,
                fileSize = fileSizeInBytes,
                mimeType = mimeType,
                exif = null
            )
        }
        return null
    }
    
    /**
     * Process generic document file.
     */
    private fun processGenericDocument(
        context: Context,
        uri: Uri,
        fileName: String?,
        mimeType: String?
    ): GalleryPhotoResult? {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileBytes = inputStream?.readBytes()
        inputStream?.close()
        
        if (fileBytes != null) {
            val fileSizeInBytes = fileBytes.size.toLong()
            return GalleryPhotoResult(
                uri = uri.toString(),
                width = null,
                height = null,
                fileName = fileName,
                fileSize = fileSizeInBytes,
                mimeType = mimeType,
                exif = null 
            )
        }
        return null
    }
    
    private fun logDebug(message: String) {
        DefaultLogger.logDebug(" GalleryFileProcessor: $message")
    }
}
