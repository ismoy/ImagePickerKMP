package io.github.ismoy.imagepickerkmp.domain.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import io.github.ismoy.imagepickerkmp.domain.models.ExifData
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility for extracting EXIF data from images on Android.
 */
object ExifDataExtractor {
    
    /**
     * Formats EXIF date string to ISO format.
     */
    private fun formatExifDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val parsedDate = inputFormat.parse(dateString)
            parsedDate?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString // Return original if parsing fails
        }
    }
    
    /**
     * Converts EXIF orientation integer to human readable description.
     */
    private fun getOrientationDescription(orientation: Int): String {
        return when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> "Normal [1]"
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> "Flip Horizontal [2]" 
            ExifInterface.ORIENTATION_ROTATE_180 -> "Rotate 180° [3]"
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> "Flip Vertical [4]"
            ExifInterface.ORIENTATION_TRANSPOSE -> "Transpose [5]"
            ExifInterface.ORIENTATION_ROTATE_90 -> "Rotate 90° CW [6]"
            ExifInterface.ORIENTATION_TRANSVERSE -> "Transverse [7]"
            ExifInterface.ORIENTATION_ROTATE_270 -> "Rotate 90° CCW [8]"
            ExifInterface.ORIENTATION_UNDEFINED -> "Undefined [0]"
            else -> "Unknown [$orientation]"
        }
    }
    
    /**
     * Extracts EXIF data using multiple fallback methods.
     * This is especially useful for gallery photos from external sources (WhatsApp, Bluetooth, etc.)
     */
    fun extractExifDataWithFallbacks(context: Context, uri: Uri): ExifData? {
        println("🔧 ExifDataExtractor: Using advanced extraction with fallbacks for URI: $uri")
        
        // Method 1: Direct URI extraction (works for most content:// URIs)
        extractExifData(context, uri)?.let { exifData ->
            println("✅ Method 1 SUCCESS: Direct URI extraction worked")
            return exifData
        }
        
        // Method 2: Copy to temp file and extract (fallback for restricted URIs)
        try {
            println("🔄 Method 1 failed, trying Method 2: Temp file extraction...")
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val tempFile = java.io.File.createTempFile(
                    "exif_fallback_${System.currentTimeMillis()}",
                    ".jpg",
                    context.cacheDir
                )
                
                java.io.FileOutputStream(tempFile).use { outputStream ->
                    stream.copyTo(outputStream)
                }
                
                // Extract from temp file
                val tempUri = Uri.fromFile(tempFile)
                val exifData = extractExifData(context, tempUri)
                tempFile.delete()
                
                if (exifData != null) {
                    println("✅ Method 2 SUCCESS: Temp file extraction worked")
                    return exifData
                } else {
                    println("❌ Method 2 failed: No EXIF data found in temp file")
                }
            }
        } catch (e: Exception) {
            println(" Method 2 failed with exception: ${e.message}")
        }
        try {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    val filePath = cursor.getString(columnIndex)
                    if (!filePath.isNullOrEmpty()) {
                        val file = java.io.File(filePath)
                        if (file.exists()) {
                            val exifData = extractExifData(context, Uri.fromFile(file))
                            if (exifData != null) {
                                return exifData
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println(" Method 3 failed with exception: ${e.message}")
        }
        return null
    }
    
    /**
     * Extracts EXIF data from an image URI.
     *
     * @param context Android context
     * @param uri Image URI
     * @return ExifData object with extracted metadata, or null if extraction fails
     */
    fun extractExifData(context: Context, uri: Uri): ExifData? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                println(" ExifDataExtractor: Failed to open InputStream for URI: $uri")
                return null
            }
            
            inputStream.use { stream ->
                println("✅ ExifDataExtractor: InputStream opened successfully, creating ExifInterface...")
                val exif = ExifInterface(stream)
                println("✅ ExifDataExtractor: ExifInterface created, extracting data...")
                
                val latLong = FloatArray(2)
                val hasGPS = exif.getLatLong(latLong)
                val altitude = exif.getAttributeDouble(ExifInterface.TAG_GPS_ALTITUDE, -1.0)
                val dateTaken = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
                    ?: exif.getAttribute(ExifInterface.TAG_DATETIME)
                
                val formattedDate = dateTaken?.let { date ->
                    try {
                        val inputFormat = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        val parsedDate = inputFormat.parse(date)
                        parsedDate?.let { outputFormat.format(it) }
                    } catch (_: Exception) {
                        date
                    }
                }
                
                val orientationInt = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 
                    ExifInterface.ORIENTATION_UNDEFINED
                )
                val orientation = if (orientationInt != ExifInterface.ORIENTATION_UNDEFINED) {
                    getOrientationDescription(orientationInt)
                } else null
                
                val cameraModel = exif.getAttribute(ExifInterface.TAG_MODEL)
                val cameraManufacturer = exif.getAttribute(ExifInterface.TAG_MAKE)
                
                val dateTimeDigitized = exif.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED)?.let { dateString: String ->
                    formatExifDate(dateString) 
                }
                
                val originalTime = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)?.let { dateString: String ->
                    formatExifDate(dateString) 
                }
                
                val software = exif.getAttribute(ExifInterface.TAG_SOFTWARE)
                
                val owner = exif.getAttribute(ExifInterface.TAG_ARTIST)
                    ?: exif.getAttribute(ExifInterface.TAG_COPYRIGHT)
                    ?: exif.getAttribute(ExifInterface.TAG_CAMERA_OWNER_NAME)
                
                val colorSpace = when (exif.getAttributeInt(ExifInterface.TAG_COLOR_SPACE, -1)) {
                    1 -> "sRGB"
                    65535 -> "Uncalibrated"
                    else -> exif.getAttribute(ExifInterface.TAG_COLOR_SPACE)
                }
                
                val whiteBalance = when (exif.getAttributeInt(ExifInterface.TAG_WHITE_BALANCE, -1)) {
                    0 -> "Auto"
                    1 -> "Manual"
                    else -> null
                }
                
                val flash = exif.getAttribute(ExifInterface.TAG_FLASH)?.let { flashValue ->
                    when (flashValue.toIntOrNull()) {
                        0 -> "No Flash"
                        1 -> "Flash Fired"
                        5 -> "Strobe Return Light Not Detected"
                        7 -> "Strobe Return Light Detected" 
                        9 -> "Flash Fired, Compulsory Flash Mode"
                        13 -> "Flash Fired, Compulsory Flash Mode, Return Light Not Detected"
                        15 -> "Flash Fired, Compulsory Flash Mode, Return Light Detected"
                        16 -> "Flash Did Not Fire, Compulsory Flash Mode"
                        24 -> "Flash Did Not Fire, Auto Mode"
                        25 -> "Flash Fired, Auto Mode"
                        29 -> "Flash Fired, Auto Mode, Return Light Not Detected"
                        31 -> "Flash Fired, Auto Mode, Return Light Detected"
                        32 -> "No Flash Function"
                        65 -> "Flash Fired, Red-Eye Reduction Mode"
                        69 -> "Flash Fired, Red-Eye Reduction Mode, Return Light Not Detected"
                        71 -> "Flash Fired, Red-Eye Reduction Mode, Return Light Detected"
                        73 -> "Flash Fired, Compulsory Flash Mode, Red-Eye Reduction Mode"
                        77 -> "Flash Fired, Compulsory Flash Mode, Red-Eye Reduction Mode, Return Light Not Detected"
                        79 -> "Flash Fired, Compulsory Flash Mode, Red-Eye Reduction Mode, Return Light Detected"
                        89 -> "Flash Fired, Auto Mode, Red-Eye Reduction Mode"
                        93 -> "Flash Fired, Auto Mode, Return Light Not Detected, Red-Eye Reduction Mode"
                        95 -> "Flash Fired, Auto Mode, Return Light Detected, Red-Eye Reduction Mode"
                        else -> "Flash: $flashValue"
                    }
                }
                
                val focalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)
                val aperture = exif.getAttribute(ExifInterface.TAG_F_NUMBER) 
                    ?: exif.getAttribute(ExifInterface.TAG_APERTURE_VALUE)
                val shutterSpeed = exif.getAttribute(ExifInterface.TAG_SHUTTER_SPEED_VALUE)
                    ?: exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)
                val iso = exif.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS)
                    ?: exif.getAttribute(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY)
                val exposureBias = exif.getAttribute(ExifInterface.TAG_EXPOSURE_BIAS_VALUE)
                
                val meteringMode = when (exif.getAttributeInt(ExifInterface.TAG_METERING_MODE, -1)) {
                    0 -> "Unknown"
                    1 -> "Average"
                    2 -> "Center Weighted Average"
                    3 -> "Spot"
                    4 -> "Multi Spot"
                    5 -> "Pattern"
                    6 -> "Partial"
                    255 -> "Other"
                    else -> null
                }
                
                val sceneCaptureType = when (exif.getAttributeInt(ExifInterface.TAG_SCENE_CAPTURE_TYPE, -1)) {
                    0 -> "Standard"
                    1 -> "Landscape"
                    2 -> "Portrait"
                    3 -> "Night Scene"
                    else -> null
                }
                
                val imageWidth = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, -1).let {
                    if (it != -1) it else null 
                }
                val imageHeight = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, -1).let { 
                    if (it != -1) it else null 
                }
                val xResolution = exif.getAttribute(ExifInterface.TAG_X_RESOLUTION)
                val yResolution = exif.getAttribute(ExifInterface.TAG_Y_RESOLUTION)
                
                val resolutionUnit = when (exif.getAttributeInt(ExifInterface.TAG_RESOLUTION_UNIT, -1)) {
                    1 -> "None"
                    2 -> "Inch" 
                    3 -> "Centimeter"
                    else -> null
                }
                
                val compression = when (exif.getAttributeInt(ExifInterface.TAG_COMPRESSION, -1)) {
                    1 -> "Uncompressed"
                    6 -> "JPEG"
                    else -> exif.getAttribute(ExifInterface.TAG_COMPRESSION)
                }
                
                val thumbnail = try {
                    exif.thumbnailBytes?.let { thumbnailBytes ->
                        android.util.Base64.encodeToString(thumbnailBytes, android.util.Base64.DEFAULT)
                    }
                } catch (e: Exception) {
                    null
                }
                
                ExifData(
                    latitude = if (hasGPS) latLong[0].toDouble() else null,
                    longitude = if (hasGPS) latLong[1].toDouble() else null,
                    altitude = if (altitude != -1.0) altitude else null,
                    dateTaken = formattedDate,
                    dateTime = formattedDate,
                    digitizedTime = dateTimeDigitized,
                    originalTime = originalTime,
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
                    exposureBias = exposureBias,
                    meteringMode = meteringMode,
                    sceneCaptureType = sceneCaptureType,
                    imageWidth = imageWidth,
                    imageHeight = imageHeight,
                    xResolution = xResolution,
                    yResolution = yResolution,
                    resolutionUnit = resolutionUnit,
                    compression = compression,
                    cloudCache = "N/A", // Android doesn't typically have this
                    thumbnail = thumbnail
                )
            }
        } catch (e: Exception) {
            println(" ExifDataExtractor: Exception occurred - ${e.javaClass.simpleName}: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}
