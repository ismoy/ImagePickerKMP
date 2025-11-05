package io.github.ismoy.imagepickerkmp.domain.utils.exif

import androidx.exifinterface.media.ExifInterface
import io.github.ismoy.imagepickerkmp.domain.models.ExifData
import java.text.SimpleDateFormat
import java.util.*

/**
 * Parser for extracting EXIF data from ExifInterface.
 */
internal object ExifDataParser {
    
    /**
     * Extracts complete EXIF data from ExifInterface.
     */
    fun parseExifData(exif: ExifInterface): ExifData {
        return ExifData(
            latitude = extractGPSLatitude(exif),
            longitude = extractGPSLongitude(exif),
            altitude = extractAltitude(exif),
            dateTaken = extractDateTaken(exif),
            dateTime = extractDateTaken(exif),
            digitizedTime = extractDigitizedTime(exif),
            originalTime = extractOriginalTime(exif),
            cameraModel = exif.getAttribute(ExifInterface.TAG_MODEL),
            cameraManufacturer = exif.getAttribute(ExifInterface.TAG_MAKE),
            cameraMake = exif.getAttribute(ExifInterface.TAG_MAKE),
            software = exif.getAttribute(ExifInterface.TAG_SOFTWARE),
            owner = extractOwnerInfo(exif),
            orientation = extractOrientation(exif),
            colorSpace = extractColorSpace(exif),
            whiteBalance = extractWhiteBalance(exif),
            flash = extractFlash(exif),
            focalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH),
            aperture = extractAperture(exif),
            shutterSpeed = extractShutterSpeed(exif),
            iso = extractISO(exif),
            exposureBias = exif.getAttribute(ExifInterface.TAG_EXPOSURE_BIAS_VALUE),
            meteringMode = extractMeteringMode(exif),
            sceneCaptureType = extractSceneCaptureType(exif),
            imageWidth = extractImageWidth(exif),
            imageHeight = extractImageHeight(exif),
            xResolution = exif.getAttribute(ExifInterface.TAG_X_RESOLUTION),
            yResolution = exif.getAttribute(ExifInterface.TAG_Y_RESOLUTION),
            resolutionUnit = extractResolutionUnit(exif),
            compression = extractCompression(exif),
            cloudCache = "N/A",
            thumbnail = extractThumbnail(exif)
        )
    }
    
    private fun extractGPSLatitude(exif: ExifInterface): Double? {
        val latLong = FloatArray(2)
        return if (exif.getLatLong(latLong)) latLong[0].toDouble() else null
    }
    
    private fun extractGPSLongitude(exif: ExifInterface): Double? {
        val latLong = FloatArray(2)
        return if (exif.getLatLong(latLong)) latLong[1].toDouble() else null
    }
    
    private fun extractAltitude(exif: ExifInterface): Double? {
        val altitude = exif.getAttributeDouble(ExifInterface.TAG_GPS_ALTITUDE, -1.0)
        return if (altitude != -1.0) altitude else null
    }
    
    private fun extractDateTaken(exif: ExifInterface): String? {
        val dateTaken = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
            ?: exif.getAttribute(ExifInterface.TAG_DATETIME)
            
        return dateTaken?.let { date ->
            try {
                val inputFormat = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val parsedDate = inputFormat.parse(date)
                parsedDate?.let { outputFormat.format(it) }
            } catch (_: Exception) {
                date
            }
        }
    }
    
    private fun extractDigitizedTime(exif: ExifInterface): String? {
        return exif.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED)?.let { 
            ExifFormatters.formatExifDate(it) 
        }
    }
    
    private fun extractOriginalTime(exif: ExifInterface): String? {
        return exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)?.let { 
            ExifFormatters.formatExifDate(it) 
        }
    }
    
    private fun extractOwnerInfo(exif: ExifInterface): String? {
        return exif.getAttribute(ExifInterface.TAG_ARTIST)
            ?: exif.getAttribute(ExifInterface.TAG_COPYRIGHT)
            ?: exif.getAttribute(ExifInterface.TAG_CAMERA_OWNER_NAME)
    }
    
    private fun extractOrientation(exif: ExifInterface): String? {
        val orientationInt = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION, 
            ExifInterface.ORIENTATION_UNDEFINED
        )
        return if (orientationInt != ExifInterface.ORIENTATION_UNDEFINED) {
            ExifFormatters.getOrientationDescription(orientationInt)
        } else null
    }
    
    private fun extractColorSpace(exif: ExifInterface): String? {
        val value = exif.getAttributeInt(ExifInterface.TAG_COLOR_SPACE, -1)
        return if (value != -1) {
            ExifFormatters.formatColorSpace(value) 
                ?: exif.getAttribute(ExifInterface.TAG_COLOR_SPACE)
        } else null
    }
    
    private fun extractWhiteBalance(exif: ExifInterface): String? {
        val value = exif.getAttributeInt(ExifInterface.TAG_WHITE_BALANCE, -1)
        return if (value != -1) ExifFormatters.formatWhiteBalance(value) else null
    }
    
    private fun extractFlash(exif: ExifInterface): String? {
        return ExifFormatters.formatFlashMode(exif.getAttribute(ExifInterface.TAG_FLASH))
    }
    
    private fun extractAperture(exif: ExifInterface): String? {
        return exif.getAttribute(ExifInterface.TAG_F_NUMBER) 
            ?: exif.getAttribute(ExifInterface.TAG_APERTURE_VALUE)
    }
    
    private fun extractShutterSpeed(exif: ExifInterface): String? {
        return exif.getAttribute(ExifInterface.TAG_SHUTTER_SPEED_VALUE)
            ?: exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)
    }
    
    private fun extractISO(exif: ExifInterface): String? {
        return exif.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS)
            ?: exif.getAttribute(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY)
    }
    
    private fun extractMeteringMode(exif: ExifInterface): String? {
        val value = exif.getAttributeInt(ExifInterface.TAG_METERING_MODE, -1)
        return if (value != -1) ExifFormatters.formatMeteringMode(value) else null
    }
    
    private fun extractSceneCaptureType(exif: ExifInterface): String? {
        val value = exif.getAttributeInt(ExifInterface.TAG_SCENE_CAPTURE_TYPE, -1)
        return if (value != -1) ExifFormatters.formatSceneCaptureType(value) else null
    }
    
    private fun extractImageWidth(exif: ExifInterface): Int? {
        val width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, -1)
        return if (width != -1) width else null
    }
    
    private fun extractImageHeight(exif: ExifInterface): Int? {
        val height = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, -1)
        return if (height != -1) height else null
    }
    
    private fun extractResolutionUnit(exif: ExifInterface): String? {
        val value = exif.getAttributeInt(ExifInterface.TAG_RESOLUTION_UNIT, -1)
        return if (value != -1) ExifFormatters.formatResolutionUnit(value) else null
    }
    
    private fun extractCompression(exif: ExifInterface): String? {
        val value = exif.getAttributeInt(ExifInterface.TAG_COMPRESSION, -1)
        return if (value != -1) {
            ExifFormatters.formatCompression(value) 
                ?: exif.getAttribute(ExifInterface.TAG_COMPRESSION)
        } else null
    }
    
    private fun extractThumbnail(exif: ExifInterface): String? {
        return try {
            exif.thumbnailBytes?.let { thumbnailBytes ->
                android.util.Base64.encodeToString(thumbnailBytes, android.util.Base64.DEFAULT)
            }
        } catch (_: Exception) {
            null
        }
    }
}
