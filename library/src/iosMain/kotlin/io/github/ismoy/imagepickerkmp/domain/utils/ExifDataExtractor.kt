package io.github.ismoy.imagepickerkmp.domain.utils

import io.github.ismoy.imagepickerkmp.domain.models.ExifData
import io.github.ismoy.imagepickerkmp.domain.utils.exif.*
import io.github.ismoy.imagepickerkmp.domain.utils.exif.PHAssetExtensions.extractLatitude
import io.github.ismoy.imagepickerkmp.domain.utils.exif.PHAssetExtensions.extractLongitude
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDictionary
import platform.Photos.PHAsset

@OptIn(ExperimentalForeignApi::class)
internal object ExifDataExtractor {
    
    fun extractExifData(path: String): ExifData? = runCatching {
        ExifLogger.debug("Extracting EXIF from path: $path")
        
        val imageSource = ImageIOHelper.createImageSource(path) ?: return null
        val properties = ImageIOHelper.getImageProperties(imageSource) ?: return null
        
        extractDataFromImageIOProperties(properties)
    }.getOrElse { 
        ExifLogger.error("Exception during extraction", it)
        null
    }
    
    fun extractExifDataFromAsset(asset: PHAsset): ExifData? = runCatching {
        ExifLogger.debug("Extracting EXIF from PHAsset")
        
        ExifData(
            latitude = asset.extractLatitude(),
            longitude = asset.extractLongitude(),
            altitude = asset.location?.altitude,
            dateTaken = asset.creationDate?.toString(),
            dateTime = asset.modificationDate?.toString(),
            digitizedTime = asset.creationDate?.toString(),
            originalTime = asset.creationDate?.toString(),
            imageWidth = asset.pixelWidth.toInt(),
            imageHeight = asset.pixelHeight.toInt(),
            cameraModel = null,
            cameraManufacturer = null,
            cameraMake = null,
            software = null,
            owner = null,
            orientation = null,
            colorSpace = null,
            whiteBalance = null,
            flash = null,
            focalLength = null,
            aperture = null,
            shutterSpeed = null,
            iso = null,
            exposureBias = null,
            meteringMode = null,
            sceneCaptureType = null,
            xResolution = null,
            yResolution = null,
            resolutionUnit = null,
            compression = null,
            cloudCache = null,
            thumbnail = null
        )
    }.getOrElse {
        ExifLogger.error("Exception extracting from PHAsset", it)
        null
    }
    
    private fun extractDataFromImageIOProperties(properties: NSDictionary): ExifData {
        val gpsData = ExifDataParsers.extractGPSData(properties)
        val tiffData = ExifDataParsers.extractTIFFData(properties)
        val exifData = ExifDataParsers.extractEXIFData(properties)
        val basicData = ExifDataParsers.extractBasicProperties(properties)
        
        ExifLogger.debug("Extraction completed - GPS: ${gpsData.latitude != null}, Camera: ${tiffData.cameraModel}")
        
        return ExifData(
            latitude = gpsData.latitude,
            longitude = gpsData.longitude,
            altitude = gpsData.altitude,
            dateTaken = exifData.dateTaken?.let(ExifFormatters::formatExifDate),
            dateTime = tiffData.dateTime?.let(ExifFormatters::formatExifDate),
            digitizedTime = exifData.digitizedTime?.let(ExifFormatters::formatExifDate),
            originalTime = exifData.dateTaken?.let(ExifFormatters::formatExifDate),
            cameraModel = tiffData.cameraModel,
            cameraManufacturer = tiffData.cameraManufacturer,
            cameraMake = tiffData.cameraManufacturer,
            software = tiffData.software,
            owner = tiffData.owner,
            orientation = basicData.orientation,
            colorSpace = exifData.colorSpace,
            whiteBalance = exifData.whiteBalance,
            flash = exifData.flash,
            focalLength = exifData.focalLength,
            aperture = exifData.aperture,
            shutterSpeed = exifData.shutterSpeed,
            iso = exifData.iso,
            imageWidth = basicData.width,
            imageHeight = basicData.height,
            exposureBias = null,
            meteringMode = null,
            sceneCaptureType = null,
            xResolution = null,
            yResolution = null,
            resolutionUnit = null,
            compression = null,
            cloudCache = null,
            thumbnail = null
        )
    }
}
