package io.github.ismoy.imagepickerkmp.data.gallery

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.GALLERY_FILE_UTILS_TAG
import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger

internal object GalleryFileUtils {

    fun getFileMimeType(context: Context, uri: Uri): String? =
        context.contentResolver.getType(uri)

    fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        val cursor = try {
            context.contentResolver.query(uri, null, null, null, null)
        } catch (e: Exception) {
            DefaultLogger.logDebug("$GALLERY_FILE_UTILS_TAG ${e.javaClass.simpleName}")
            null
        }
        cursor?.use {
            if (it.moveToFirst()) {
                val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx != -1) result = it.getString(idx)
            }
        }
        return result
    }
}
