package io.github.ismoy.imagepickerkmp.data.managers

import android.content.Context
import android.net.Uri
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.DATE_FORMATE
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.IMAGE_TEMP
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.PREFIX_JPEG
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.SUFFIX_COMPRESSED_GALLERY
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


internal class FileManager(private val context: Context) {

    companion object {
        private val TIMESTAMP_FORMAT = ThreadLocal.withInitial<SimpleDateFormat> {
            SimpleDateFormat(DATE_FORMATE, Locale.getDefault())
        }
    }

    fun createImageFile(): File {
        val timeStamp = TIMESTAMP_FORMAT.get()!!.format(Date())
        val storageDir = context.cacheDir.resolve(IMAGE_TEMP).also { it.mkdirs() }
        return File.createTempFile("$PREFIX_JPEG${timeStamp}_", SUFFIX_COMPRESSED_GALLERY, storageDir)
    }

    fun fileToUriString(file: File): String = Uri.fromFile(file).toString()

}
