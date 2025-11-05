package io.github.ismoy.imagepickerkmp.domain.utils

import io.github.ismoy.imagepickerkmp.domain.models.ExifData
import kotlinx.cinterop.*
import platform.Foundation.*
import platform.ImageIO.*
import platform.CoreLocation.*
import platform.CoreFoundation.*
import platform.Photos.PHAsset
import platform.Photos.PHImageManager
import platform.Photos.PHImageRequestOptions
import platform.Photos.PHImageRequestOptionsDeliveryModeHighQualityFormat

/**
 * Utility for extracting EXIF data from images on iOS using simplified native approach.
 */
@OptIn(ExperimentalForeignApi::class)
object ExifDataExtractor {
    
    /**
     * Extracts EXIF data from an image URL using simplified native ImageIO approach.
     *
     * @param url Image file URL  
     * @return ExifData object with extracted metadata, or null if extraction fails
     */
    fun extractExifData(url: NSURL): ExifData? {
        return try {
            println("🔍 iOS ExifDataExtractor: Using simplified native extraction for URL: $url")
            
            // Extraer path de la URL
            val path = url.path ?: return null
            return extractExifData(path)
            
        } catch (e: Exception) {
            println("❌ iOS ExifDataExtractor: Exception during extraction: ${e.message}")
            e.printStackTrace()
            return null
        }
    }

    /**
     * Extracts EXIF data from an image file path using native ImageIO implementation 
     * based on Swift logic but written in Kotlin/Native.
     *
     * @param path Image file path
     * @return ExifData object with extracted metadata, or null if extraction fails
     */
    fun extractExifData(path: String): ExifData? {
        return try {
            println("🔍 iOS ExifDataExtractor: Using native ImageIO for path: $path")
            
            // Crear URL string y convertir a CFString 
            val pathString = CFStringCreateWithCString(null, path, kCFStringEncodingUTF8)
            val cfUrl = CFURLCreateWithFileSystemPath(null, pathString, kCFURLPOSIXPathStyle, false)
            val imageSource = CGImageSourceCreateWithURL(cfUrl, null)
            
            if (imageSource == null) {
                println("❌ Failed to create image source")
                return null
            }
            
            val properties = CGImageSourceCopyPropertiesAtIndex(imageSource, 0u, null)
            if (properties == null) {
                println("❌ Failed to get image properties")
                return null
            }
            
            println("✅ Image properties loaded successfully")
            
            // Convertir CFDictionary a NSDictionary de manera segura usando bridging
            val propertiesDict = CFBridgingRelease(properties) as? NSDictionary
            if (propertiesDict == null) {
                println("❌ Failed to convert properties to NSDictionary")
                return null
            }
            
            println("📊 Available properties: ${propertiesDict.allKeys()}")
            
            // Extraer datos usando la misma lógica que el código Swift
            return extractDataFromImageIOProperties(propertiesDict)
            
        } catch (e: Exception) {
            println("❌ iOS ExifDataExtractor: Exception during extraction: ${e.message}")
            e.printStackTrace()
            return null
        }
    }
    
    /**
     * Extracts data from ImageIO properties dictionary using native constants.
     * This implements the same logic as the Swift version but in Kotlin/Native.
     */
    private fun extractDataFromImageIOProperties(properties: NSDictionary): ExifData {
        val result = mutableMapOf<String, Any>()
        
        // GPS Data - usando strings directos para las claves
        val gpsDict = properties.objectForKey("{GPS}") as? NSDictionary
        var latitude: Double? = null
        var longitude: Double? = null
        var altitude: Double? = null
        
        gpsDict?.let { gps ->
            println("🗺️ GPS data found: ${gps.allKeys()}")
            val latValue = gps.objectForKey("Latitude") as? NSNumber
            val lonValue = gps.objectForKey("Longitude") as? NSNumber
            val latRef = gps.objectForKey("LatitudeRef") as? NSString
            val lonRef = gps.objectForKey("LongitudeRef") as? NSString
            val altValue = gps.objectForKey("Altitude") as? NSNumber
            
            println("📍 GPS values - Lat: $latValue, Lon: $lonValue, LatRef: $latRef, LonRef: $lonRef")
            
            if (latValue != null && lonValue != null && latRef != null && lonRef != null) {
                latitude = if (latRef.toString() == "S") -latValue.doubleValue else latValue.doubleValue
                longitude = if (lonRef.toString() == "W") -lonValue.doubleValue else lonValue.doubleValue
                println("✅ GPS coordinates: $latitude, $longitude")
            }
            
            altitude = altValue?.doubleValue
        }
        
        // TIFF Data (Camera info) - usando strings directos
        val tiffDict = properties.objectForKey("{TIFF}") as? NSDictionary
        var cameraManufacturer: String? = null
        var cameraModel: String? = null
        var software: String? = null
        var owner: String? = null
        var dateTime: String? = null
        
        tiffDict?.let { tiff ->
            println("📷 TIFF data found: ${tiff.allKeys()}")
            cameraManufacturer = (tiff.objectForKey("Make") as? NSString)?.toString()
            cameraModel = (tiff.objectForKey("Model") as? NSString)?.toString()
            software = (tiff.objectForKey("Software") as? NSString)?.toString()
            owner = (tiff.objectForKey("Artist") as? NSString)?.toString()
                ?: (tiff.objectForKey("Copyright") as? NSString)?.toString()
            dateTime = (tiff.objectForKey("DateTime") as? NSString)?.toString()
            
            println("📸 Camera: $cameraManufacturer $cameraModel")
        }
        
        // EXIF Data (Technical details) - usando strings directos
        val exifDict = properties.objectForKey("{Exif}") as? NSDictionary
        var dateTaken: String? = null
        var digitizedTime: String? = null
        var colorSpace: String? = null
        var focalLength: String? = null
        var aperture: String? = null
        var shutterSpeed: String? = null
        var iso: String? = null
        var flash: String? = null
        var whiteBalance: String? = null
        
        exifDict?.let { exif ->
            println("🔧 EXIF data found: ${exif.allKeys()}")
            dateTaken = (exif.objectForKey("DateTimeOriginal") as? NSString)?.toString()
            digitizedTime = (exif.objectForKey("DateTimeDigitized") as? NSString)?.toString()
            
            val colorSpaceValue = exif.objectForKey("ColorSpace") as? NSNumber
            colorSpace = when (colorSpaceValue?.intValue) {
                1 -> "sRGB"
                2 -> "Adobe RGB"
                65535 -> "Uncalibrated"
                else -> "ColorSpace: ${colorSpaceValue?.intValue}"
            }
            
            focalLength = (exif.objectForKey("FocalLength") as? NSNumber)?.stringValue
            aperture = (exif.objectForKey("FNumber") as? NSNumber)?.stringValue
            shutterSpeed = (exif.objectForKey("ExposureTime") as? NSNumber)?.stringValue
            
            val isoArray = exif.objectForKey("ISOSpeedRatings") as? NSArray
            iso = (isoArray?.firstObject as? NSNumber)?.stringValue
            
            val flashValue = exif.objectForKey("Flash") as? NSNumber
            flash = flashValue?.let { getFlashDescription(it.intValue) }
            
            val whiteBalanceValue = exif.objectForKey("WhiteBalance") as? NSNumber
            whiteBalance = when (whiteBalanceValue?.intValue) {
                0 -> "Auto"
                1 -> "Manual"
                else -> null
            }
            
            println("📸 EXIF extracted - Date: $dateTaken, ISO: $iso, Focal: $focalLength")
        }
        
        // Basic properties
        val orientation = (properties.objectForKey("Orientation") as? NSNumber)?.let {
            getOrientationDescription(it.intValue)
        }
        
        val imageWidth = (properties.objectForKey("PixelWidth") as? NSNumber)?.intValue
        val imageHeight = (properties.objectForKey("PixelHeight") as? NSNumber)?.intValue
        
        println("📊 Native extraction completed - GPS: ${latitude != null}, Camera: $cameraModel")
        
        return ExifData(
            latitude = latitude,
            longitude = longitude,
            altitude = altitude,
            dateTaken = dateTaken?.let { formatExifDate(it) },
            dateTime = dateTime?.let { formatExifDate(it) },
            digitizedTime = digitizedTime?.let { formatExifDate(it) },
            originalTime = dateTaken?.let { formatExifDate(it) },
            cameraModel = cameraModel,
            cameraManufacturer = cameraManufacturer,
            cameraMake = cameraManufacturer,
            software = software,
            owner = owner,
            orientation = orientation,
            colorSpace = colorSpace,
            whiteBalance = whiteBalance,
            flash = flash,
            focalLength = focalLength,
            aperture = aperture,
            shutterSpeed = shutterSpeed,
            iso = iso,
            exposureBias = null,
            meteringMode = null,
            sceneCaptureType = null,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            xResolution = null,
            yResolution = null,
            resolutionUnit = null,
            compression = null,
            cloudCache = null,
            thumbnail = null
        )
    }
    
    /**
     * Formats EXIF date string to ISO format.
     */
    private fun formatExifDate(dateString: String): String {
        return try {
            // EXIF date format: "yyyy:MM:dd HH:mm:ss"
            // Target format: "yyyy-MM-ddTHH:mm:ss"
            val parts = dateString.split(":", " ")
            if (parts.size >= 4) {
                "${parts[0]}-${parts[1]}-${parts[2]}T${parts[3]}"
            } else {
                dateString
            }
        } catch (e: Exception) {
            dateString
        }
    }
    
    /**
     * Converts orientation integer to human readable description.
     */
    private fun getOrientationDescription(orientation: Int): String {
        return when (orientation) {
            1 -> "Normal [1]"
            2 -> "Flip Horizontal [2]"
            3 -> "Rotate 180° [3]"
            4 -> "Flip Vertical [4]"
            5 -> "Transpose [5]"
            6 -> "Rotate 90° CW [6]"
            7 -> "Transverse [7]"
            8 -> "Rotate 90° CCW [8]"
            else -> "Unknown [$orientation]"
        }
    }
    
    /**
     * Converts flash value to human readable description.
     */
    private fun getFlashDescription(flash: Int): String {
        return when (flash) {
            0 -> "No Flash"
            1 -> "Flash Fired"
            5 -> "Strobe Return Light Not Detected"
            7 -> "Strobe Return Light Detected"
            9 -> "Flash Fired, Compulsory Flash Mode"
            16 -> "Flash Did Not Fire, Compulsory Flash Mode"
            24 -> "Flash Did Not Fire, Auto Mode"
            25 -> "Flash Fired, Auto Mode"
            32 -> "No Flash Function"
            65 -> "Flash Fired, Red-Eye Reduction Mode"
            else -> "Flash: $flash"
        }
    }
    
    /**
     * Extracts EXIF data from a PHAsset (original photo from library).
     * This method retrieves the original metadata including GPS, camera info, and dates.
     *
     * @param asset PHAsset from the photo library
     * @return ExifData object with extracted metadata, or null if extraction fails
     */
    fun extractExifDataFromAsset(asset: PHAsset): ExifData? {
        return try {
            println("🔍 iOS ExifDataExtractor: Extracting from PHAsset")
            
            // Extract location from PHAsset
            val location = asset.location
            val latitude = location?.coordinate?.useContents { latitude }
            val longitude = location?.coordinate?.useContents { longitude }
            val altitude = location?.altitude
            
            println("📍 PHAsset location: lat=$latitude, lon=$longitude, alt=$altitude")
            
            // Extract dates
            val creationDate = asset.creationDate
            val modificationDate = asset.modificationDate
            
            println("📅 PHAsset dates: creation=$creationDate, modification=$modificationDate")
            
            // Extract basic metadata
            val pixelWidth = asset.pixelWidth.toInt()
            val pixelHeight = asset.pixelHeight.toInt()
            
            println("🖼️ PHAsset dimensions: ${pixelWidth}x${pixelHeight}")
            
            // Create ExifData with available information
            ExifData(
                latitude = latitude,
                longitude = longitude,
                altitude = altitude,
                dateTaken = creationDate?.toString(),
                dateTime = modificationDate?.toString(),
                digitizedTime = creationDate?.toString(),
                originalTime = creationDate?.toString(),
                cameraModel = null, // PHAsset doesn't directly expose camera model
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
                imageWidth = pixelWidth,
                imageHeight = pixelHeight,
                xResolution = null,
                yResolution = null,
                resolutionUnit = null,
                compression = null,
                cloudCache = null,
                thumbnail = null
            )
            
        } catch (e: Exception) {
            println("❌ iOS ExifDataExtractor: Exception extracting from PHAsset: ${e.message}")
            e.printStackTrace()
            return null
        }
    }
}
