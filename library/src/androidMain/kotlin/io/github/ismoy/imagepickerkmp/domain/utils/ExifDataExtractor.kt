package io.github.ismoy.imagepickerkmp.domain.utils

import android.content.Context
import android.net.Uri
import io.github.ismoy.imagepickerkmp.domain.models.ExifData
import io.github.ismoy.imagepickerkmp.domain.utils.exif.ExifDataParser
import io.github.ismoy.imagepickerkmp.domain.utils.exif.ExifFallbackExtractor
import io.github.ismoy.imagepickerkmp.domain.utils.exif.ExifInterfaceHelper

/**
 * Utility for extracting EXIF data from images on Android.
 * 
 * This is the main entry point for EXIF extraction.
 * Uses modular extractors and parsers for clean architecture.
 */
object ExifDataExtractor {
    
    /**
     * Extracts EXIF data using multiple fallback methods.
     * This is especially useful for gallery photos from external sources (WhatsApp, Bluetooth, etc.)
     * 
     * @param context Android context
     * @param uri Image URI
     * @return ExifData object with extracted metadata, or null if extraction fails
     */
    fun extractExifDataWithFallbacks(context: Context, uri: Uri): ExifData? {
        return ExifFallbackExtractor.extractWithFallbacks(context, uri)
    }
    
    /**
     * Extracts EXIF data from an image URI.
     *
     * @param context Android context
     * @param uri Image URI
     * @return ExifData object with extracted metadata, or null if extraction fails
     */
    fun extractExifData(context: Context, uri: Uri): ExifData? {
        val exif = ExifInterfaceHelper.createFromUri(context, uri) ?: return null
        return ExifDataParser.parseExifData(exif)
    }
}