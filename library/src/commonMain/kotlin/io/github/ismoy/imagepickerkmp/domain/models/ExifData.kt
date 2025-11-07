package io.github.ismoy.imagepickerkmp.domain.models

/**
 * Contains comprehensive EXIF metadata from an image file.
 * 
 * GPS Data:
 * @property latitude GPS latitude coordinate (if available)
 * @property longitude GPS longitude coordinate (if available) 
 * @property altitude GPS altitude in meters (if available)
 * 
 * Date & Time:
 * @property dateTaken Original date and time when the photo was taken
 * @property dateTime General date/time (same as dateTaken, for compatibility)
 * @property digitizedTime Date and time when image was digitized
 * @property modifiedTime Date and time when image was last modified
 * @property utcTime UTC date and time
 * @property originalTime Original creation time
 * 
 * Camera Information:
 * @property cameraModel Camera model used to take the photo
 * @property cameraManufacturer Camera manufacturer (also cameraMake for compatibility)
 * @property cameraMake Camera make/manufacturer
 * @property software Software used to process the image
 * @property owner Owner/copyright information
 * 
 * Image Properties:
 * @property orientation Image orientation (rotation) information
 * @property colorSpace Color space information (sRGB, Adobe RGB, etc.)
 * @property whiteBalance White balance setting
 * @property flash Flash information
 * @property focalLength Focal length in mm
 * @property aperture Aperture f-stop value
 * @property shutterSpeed Shutter speed
 * @property iso ISO sensitivity
 * @property exposureBias Exposure compensation
 * @property meteringMode Metering mode used
 * @property sceneCaptureType Scene capture type
 * 
 * Technical Data:
 * @property imageWidth Image width in pixels
 * @property imageHeight Image height in pixels
 * @property xResolution Horizontal resolution (DPI)
 * @property yResolution Vertical resolution (DPI)
 * @property resolutionUnit Resolution unit (inches/cm)
 * @property compression Compression method
 * @property cloudCache Cloud cache information
 * @property thumbnail Base64 encoded thumbnail data (if available)
 */
data class ExifData(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val altitude: Double? = null,
    val dateTaken: String? = null,
    val dateTime: String? = null,
    val digitizedTime: String? = null,
    val originalTime: String? = null,
    val cameraModel: String? = null,
    val cameraManufacturer: String? = null,
    val cameraMake: String? = null,
    val software: String? = null,
    val owner: String? = null,
    val orientation: String? = null,
    val colorSpace: String? = null,
    val whiteBalance: String? = null,
    val flash: String? = null,
    val focalLength: String? = null,
    val aperture: String? = null,
    val shutterSpeed: String? = null,
    val iso: String? = null,
    val exposureBias: String? = null,
    val meteringMode: String? = null,
    val sceneCaptureType: String? = null,
    val imageWidth: Int? = null,
    val imageHeight: Int? = null,
    val xResolution: String? = null,
    val yResolution: String? = null,
    val resolutionUnit: String? = null,
    val compression: String? = null,
    val cloudCache: String? = null,
    val thumbnail: String? = null
)
