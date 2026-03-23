package io.github.ismoy.imagepickerkmp.data.processors

import android.content.Context
import android.net.Uri
import io.github.ismoy.imagepickerkmp.domain.models.ExifData
import io.github.ismoy.imagepickerkmp.data.processors.exif.ExifDataParser
import io.github.ismoy.imagepickerkmp.data.processors.exif.ExifFallbackExtractor
import io.github.ismoy.imagepickerkmp.data.processors.exif.ExifInterfaceHelper

internal object ExifDataExtractor {
    
    fun extractExifDataWithFallbacks(context: Context, uri: Uri): ExifData? {
        return ExifFallbackExtractor.extractWithFallbacks(context, uri)
    }
    
    fun extractExifData(context: Context, uri: Uri): ExifData? {
        val exif = ExifInterfaceHelper.createFromUri(context, uri) ?: return null
        return ExifDataParser.parseExifData(exif)
    }
}