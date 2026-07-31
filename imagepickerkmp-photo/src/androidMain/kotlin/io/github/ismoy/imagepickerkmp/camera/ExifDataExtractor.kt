package io.github.ismoy.imagepickerkmp.camera

import android.content.Context
import android.net.Uri
import io.github.ismoy.imagepickerkmp.picker.ExifData
import io.github.ismoy.imagepickerkmp.camera.exif.ExifDataParser
import io.github.ismoy.imagepickerkmp.camera.exif.ExifFallbackExtractor
import io.github.ismoy.imagepickerkmp.camera.exif.ExifInterfaceHelper

internal object ExifDataExtractor {
    
    fun extractExifDataWithFallbacks(context: Context, uri: Uri): ExifData? {
        return ExifFallbackExtractor.extractWithFallbacks(context, uri)
    }
    
    fun extractExifData(context: Context, uri: Uri): ExifData? {
        val exif = ExifInterfaceHelper.createFromUri(context, uri) ?: return null
        return ExifDataParser.parseExifData(exif)
    }
}