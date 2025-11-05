package io.github.ismoy.imagepickerkmp.domain.utils.exif

import io.github.ismoy.imagepickerkmp.domain.models.exif.*
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*

/**
 * Parsers for extracting specific EXIF data sections from NSDictionary.
 */
@OptIn(ExperimentalForeignApi::class)
internal object ExifDataParsers {
    
    /**
     * Extracts GPS location data from properties dictionary.
     */
    fun extractGPSData(properties: NSDictionary): GPSData {
        val gpsDict = properties.objectForKey("{GPS}") as? NSDictionary 
            ?: return GPSData(null, null, null)
        
        val latValue = gpsDict.objectForKey("Latitude") as? NSNumber
        val lonValue = gpsDict.objectForKey("Longitude") as? NSNumber
        val latRef = gpsDict.objectForKey("LatitudeRef") as? NSString
        val lonRef = gpsDict.objectForKey("LongitudeRef") as? NSString
        val altValue = gpsDict.objectForKey("Altitude") as? NSNumber
        
        val latitude = if (latValue != null && latRef != null) {
            if (latRef.toString() == "S") -latValue.doubleValue else latValue.doubleValue
        } else null
        
        val longitude = if (lonValue != null && lonRef != null) {
            if (lonRef.toString() == "W") -lonValue.doubleValue else lonValue.doubleValue
        } else null
        
        if (latitude != null && longitude != null) {
            ExifLogger.debug("GPS coordinates: $latitude, $longitude")
        }
        
        return GPSData(latitude, longitude, altValue?.doubleValue)
    }
    
    /**
     * Extracts TIFF metadata (camera information) from properties dictionary.
     */
    fun extractTIFFData(properties: NSDictionary): TIFFData {
        val tiffDict = properties.objectForKey("{TIFF}") as? NSDictionary 
            ?: return TIFFData(null, null, null, null, null)
        
        val manufacturer = (tiffDict.objectForKey("Make") as? NSString)?.toString()
        val model = (tiffDict.objectForKey("Model") as? NSString)?.toString()
        val software = (tiffDict.objectForKey("Software") as? NSString)?.toString()
        val owner = (tiffDict.objectForKey("Artist") as? NSString)?.toString()
            ?: (tiffDict.objectForKey("Copyright") as? NSString)?.toString()
        val dateTime = (tiffDict.objectForKey("DateTime") as? NSString)?.toString()
        
        if (manufacturer != null || model != null) {
            ExifLogger.debug("Camera: $manufacturer $model")
        }
        
        return TIFFData(manufacturer, model, software, owner, dateTime)
    }
    
    /**
     * Extracts EXIF technical metadata from properties dictionary.
     */
    fun extractEXIFData(properties: NSDictionary): EXIFData {
        val exifDict = properties.objectForKey("{Exif}") as? NSDictionary 
            ?: return EXIFData(null, null, null, null, null, null, null, null, null)
        
        val dateTaken = (exifDict.objectForKey("DateTimeOriginal") as? NSString)?.toString()
        val digitizedTime = (exifDict.objectForKey("DateTimeDigitized") as? NSString)?.toString()
        
        val colorSpaceValue = exifDict.objectForKey("ColorSpace") as? NSNumber
        val colorSpace = when (colorSpaceValue?.intValue) {
            1 -> "sRGB"
            2 -> "Adobe RGB"
            65535 -> "Uncalibrated"
            else -> colorSpaceValue?.intValue?.let { "ColorSpace: $it" }
        }
        
        val focalLength = (exifDict.objectForKey("FocalLength") as? NSNumber)?.stringValue
        val aperture = (exifDict.objectForKey("FNumber") as? NSNumber)?.stringValue
        val shutterSpeed = (exifDict.objectForKey("ExposureTime") as? NSNumber)?.stringValue
        
        val isoArray = exifDict.objectForKey("ISOSpeedRatings") as? NSArray
        val iso = (isoArray?.firstObject as? NSNumber)?.stringValue
        
        val flashValue = exifDict.objectForKey("Flash") as? NSNumber
        val flash = flashValue?.let { ExifFormatters.getFlashDescription(it.intValue) }
        
        val whiteBalanceValue = exifDict.objectForKey("WhiteBalance") as? NSNumber
        val whiteBalance = when (whiteBalanceValue?.intValue) {
            0 -> "Auto"
            1 -> "Manual"
            else -> null
        }
        
        return EXIFData(dateTaken, digitizedTime, colorSpace, focalLength, aperture, shutterSpeed, iso, flash, whiteBalance)
    }
    
    /**
     * Extracts basic image properties from properties dictionary.
     */
    fun extractBasicProperties(properties: NSDictionary): BasicData {
        val orientation = (properties.objectForKey("Orientation") as? NSNumber)?.let {
            ExifFormatters.getOrientationDescription(it.intValue)
        }
        val width = (properties.objectForKey("PixelWidth") as? NSNumber)?.intValue
        val height = (properties.objectForKey("PixelHeight") as? NSNumber)?.intValue
        
        return BasicData(orientation, width, height)
    }
}
