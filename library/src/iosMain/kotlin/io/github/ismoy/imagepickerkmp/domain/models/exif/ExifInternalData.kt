package io.github.ismoy.imagepickerkmp.domain.models.exif

internal data class GPSData(
    val latitude: Double?,
    val longitude: Double?,
    val altitude: Double?
)

internal data class TIFFData(
    val cameraManufacturer: String?,
    val cameraModel: String?,
    val software: String?,
    val owner: String?,
    val dateTime: String?
)

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

internal data class BasicData(
    val orientation: String?,
    val width: Int?,
    val height: Int?
)
