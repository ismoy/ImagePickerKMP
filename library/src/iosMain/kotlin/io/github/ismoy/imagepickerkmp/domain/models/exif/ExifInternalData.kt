package io.github.ismoy.imagepickerkmp.domain.models.exif

/**
 * Internal data models for EXIF extraction.
 * These classes are used internally to organize extracted metadata.
 */

/**
 * GPS location data extracted from EXIF.
 */
internal data class GPSData(
    val latitude: Double?,
    val longitude: Double?,
    val altitude: Double?
)

/**
 * TIFF metadata (camera and device information).
 */
internal data class TIFFData(
    val cameraManufacturer: String?,
    val cameraModel: String?,
    val software: String?,
    val owner: String?,
    val dateTime: String?
)

/**
 * EXIF technical metadata (camera settings).
 */
internal data class EXIFData(
    val dateTaken: String?,
    val digitizedTime: String?,
    val colorSpace: String?,
    val focalLength: String?,
    val aperture: String?,
    val shutterSpeed: String?,
    val iso: String?,
    val flash: String?,
    val whiteBalance: String?
)

/**
 * Basic image properties.
 */
internal data class BasicData(
    val orientation: String?,
    val width: Int?,
    val height: Int?
)
