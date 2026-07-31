package io.github.ismoy.imagepickerkmp.camera

import android.net.Uri
import io.github.ismoy.imagepickerkmp.core.CoreServices
import io.github.ismoy.imagepickerkmp.core.uri.platformUriFrom
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.DATE_FORMATE
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.IMAGE_TEMP
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.PREFIX_JPEG
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.SUFFIX_COMPRESSED_GALLERY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class FileManager() {

    companion object {
        private val TIMESTAMP_FORMAT = ThreadLocal.withInitial<SimpleDateFormat> {
            SimpleDateFormat(DATE_FORMATE, Locale.getDefault())
        }
    }

    private val fileSystem = CoreServices.fileSystemManager(subDirectory = IMAGE_TEMP)

    fun createImageFile(): File {
        val timeStamp = TIMESTAMP_FORMAT.get()!!.format(Date())
        val platformFile = runBlocking(Dispatchers.IO) {
            fileSystem.createTempFile(
                prefix = "$PREFIX_JPEG${timeStamp}_",
                suffix = SUFFIX_COMPRESSED_GALLERY
            )
        }
        return File(platformFile.path)
    }

    fun fileToUriString(file: File): String =
        platformUriFrom(Uri.fromFile(file).toString()).rawValue
}
