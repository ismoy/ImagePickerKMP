package io.github.ismoy.imagepickerkmp.data.processors.exif

import android.system.ErrnoException
import android.util.Base64.encodeToString
import androidx.exifinterface.media.ExifInterface
import io.github.ismoy.imagepickerkmp.domain.config.ExifValues
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.EXTRACTION_EXIF_FAILED_TAG
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.INPUT_DATE_FORMAT
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_MINUS_ONE
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_MINUS_ONE_DOT_ZERO
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_ONE
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_TWO
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_ZERO
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.N_A
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.OUTPUT_DATE_FORMAT
import io.github.ismoy.imagepickerkmp.domain.models.ExifData
import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger
import java.text.SimpleDateFormat
import java.util.Locale

internal object ExifDataParser {
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
            cloudCache = N_A,
            thumbnail = extractThumbnail(exif)
        )
    }
    
    private fun extractGPSLatitude(exif: ExifInterface): Double? {
        val latLong = FloatArray(NUMBER_TWO)
        return if (exif.getLatLong(latLong)) latLong[NUMBER_ZERO].toDouble() else null
    }
    
    private fun extractGPSLongitude(exif: ExifInterface): Double? {
        val latLong = FloatArray(NUMBER_TWO)
        return if (exif.getLatLong(latLong)) latLong[NUMBER_ONE].toDouble() else null
    }
    
    private fun extractAltitude(exif: ExifInterface): Double? {
        val altitude = exif.getAttributeDouble(ExifInterface.TAG_GPS_ALTITUDE, NUMBER_MINUS_ONE_DOT_ZERO)
        return if (altitude != NUMBER_MINUS_ONE_DOT_ZERO) altitude else null
    }
    
    private fun extractDateTaken(exif: ExifInterface): String? {
        val dateTaken = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
            ?: exif.getAttribute(ExifInterface.TAG_DATETIME)
            
        return dateTaken?.let { date ->
            try {
                val inputFormat = SimpleDateFormat(INPUT_DATE_FORMAT, Locale.getDefault())
                val outputFormat = SimpleDateFormat(OUTPUT_DATE_FORMAT, Locale.getDefault())
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
        val value = exif.getAttributeInt(ExifInterface.TAG_COLOR_SPACE, NUMBER_MINUS_ONE)
        return if (value != NUMBER_MINUS_ONE) {
            ExifValues.formatColorSpace(value)
                ?: exif.getAttribute(ExifInterface.TAG_COLOR_SPACE)
        } else null
    }
    
    private fun extractWhiteBalance(exif: ExifInterface): String? {
        val value = exif.getAttributeInt(ExifInterface.TAG_WHITE_BALANCE, NUMBER_MINUS_ONE)
        return if (value != NUMBER_MINUS_ONE) ExifValues.formatWhiteBalance(value) else null
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
        val value = exif.getAttributeInt(ExifInterface.TAG_METERING_MODE, NUMBER_MINUS_ONE)
        return if (value != NUMBER_MINUS_ONE) ExifValues.formatMeteringMode(value) else null
    }
    
    private fun extractSceneCaptureType(exif: ExifInterface): String? {
        val value = exif.getAttributeInt(ExifInterface.TAG_SCENE_CAPTURE_TYPE, NUMBER_MINUS_ONE)
        return if (value != NUMBER_MINUS_ONE) ExifValues.formatSceneCaptureType(value) else null
    }
    
    private fun extractImageWidth(exif: ExifInterface): Int? {
        val width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, NUMBER_MINUS_ONE)
        return if (width != NUMBER_MINUS_ONE) width else null
    }
    
    private fun extractImageHeight(exif: ExifInterface): Int? {
        val height = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, NUMBER_MINUS_ONE)
        return if (height != NUMBER_MINUS_ONE) height else null
    }
    
    private fun extractResolutionUnit(exif: ExifInterface): String? {
        val value = exif.getAttributeInt(ExifInterface.TAG_RESOLUTION_UNIT, NUMBER_MINUS_ONE)
        return if (value != NUMBER_MINUS_ONE) ExifValues.formatResolutionUnit(value) else null
    }
    
    private fun extractCompression(exif: ExifInterface): String? {
        val value = exif.getAttributeInt(ExifInterface.TAG_COMPRESSION, NUMBER_MINUS_ONE)
        return if (value != NUMBER_MINUS_ONE) {
            ExifValues.formatCompression(value)
                ?: exif.getAttribute(ExifInterface.TAG_COMPRESSION)
        } else null
    }
    
    private fun extractThumbnail(exif: ExifInterface): String? {
        return try {
            val hasThumbnail = exif.hasThumbnail()
            if (!hasThumbnail) {
                return null
            }
            val thumbnailBytes = exif.thumbnailBytes
            thumbnailBytes?.let { bytes ->
                encodeToString(bytes, android.util.Base64.DEFAULT)
            }
        } catch (_: ErrnoException) {
            DefaultLogger.logDebug("$EXTRACTION_EXIF_FAILED_TAG- file descriptor issue")
            null
        } catch (e: Exception) {
            DefaultLogger.logDebug("$EXTRACTION_EXIF_FAILED_TAG ${e.javaClass.simpleName}")
            null
        }
    }
}
