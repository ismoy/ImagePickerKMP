package io.github.ismoy.imagepickerkmp.domain.utils.exif

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreFoundation.*
import platform.Foundation.CFBridgingRelease
import platform.Foundation.NSDictionary
import platform.ImageIO.*

/**
 * Helper functions for working with ImageIO framework.
 */
@OptIn(ExperimentalForeignApi::class)
internal object ImageIOHelper {
    
    /**
     * Creates a CGImageSource from a file path.
     */
    fun createImageSource(path: String): CGImageSourceRef? {
        val pathString = CFStringCreateWithCString(null, path, kCFStringEncodingUTF8)
        val cfUrl = CFURLCreateWithFileSystemPath(null, pathString, kCFURLPOSIXPathStyle, false)
        val imageSource = CGImageSourceCreateWithURL(cfUrl, null)
        
        if (imageSource == null) {
            ExifLogger.error("Failed to create image source")
        }
        
        return imageSource
    }
    
    /**
     * Retrieves image properties from a CGImageSource.
     */
    fun getImageProperties(imageSource: CGImageSourceRef): NSDictionary? {
        val properties = CGImageSourceCopyPropertiesAtIndex(imageSource, 0u, null)
        if (properties == null) {
            ExifLogger.error("Failed to get image properties")
            return null
        }
        
        val propertiesDict = CFBridgingRelease(properties) as? NSDictionary
        if (propertiesDict == null) {
            ExifLogger.error("Failed to convert properties to NSDictionary")
        }
        
        return propertiesDict
    }
}
