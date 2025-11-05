import Foundation
import ImageIO
import CoreLocation

@objc public class ExifExtractorNative: NSObject {
    
    @objc public static func extractExifData(from imagePath: String) -> [String: Any]? {
        let url = URL(fileURLWithPath: imagePath)
        print("📂 ExifExtractorNative: Creating URL from path: \(url.absoluteString)")
        
        guard let imageSource = CGImageSourceCreateWithURL(url as CFURL, nil) else {
            print("❌ ExifExtractorNative: Failed to create image source for URL: \(url.absoluteString)")
            return nil
        }
        
        guard let properties = CGImageSourceCopyPropertiesAtIndex(imageSource, 0, nil) as? [String: Any] else {
            print("❌ ExifExtractorNative: Failed to get properties")
            return nil
        }
        
        print("✅ ExifExtractorNative: Properties loaded successfully")
        print("📊 Available properties: \(properties.keys)")
        
        var result: [String: Any] = [:]
        
        // GPS Data
        if let gpsData = properties[kCGImagePropertyGPSDictionary as String] as? [String: Any] {
            print("🗺️ GPS data found: \(gpsData)")
            
            if let latitude = gpsData[kCGImagePropertyGPSLatitude as String] as? Double,
               let longitude = gpsData[kCGImagePropertyGPSLongitude as String] as? Double,
               let latitudeRef = gpsData[kCGImagePropertyGPSLatitudeRef as String] as? String,
               let longitudeRef = gpsData[kCGImagePropertyGPSLongitudeRef as String] as? String {
                
                let finalLatitude = latitudeRef == "S" ? -latitude : latitude
                let finalLongitude = longitudeRef == "W" ? -longitude : longitude
                
                result["latitude"] = finalLatitude
                result["longitude"] = finalLongitude
                
                if let altitude = gpsData[kCGImagePropertyGPSAltitude as String] as? Double {
                    result["altitude"] = altitude
                }
            }
        } else {
            print("📍 No GPS data found")
        }
        
        // TIFF Data (Camera info, dates, etc.)
        if let tiffData = properties[kCGImagePropertyTIFFDictionary as String] as? [String: Any] {
            print("📷 TIFF data found: \(tiffData)")
            
            if let make = tiffData[kCGImagePropertyTIFFMake as String] as? String {
                result["cameraManufacturer"] = make
                result["cameraMake"] = make
                print("🏭 Camera Make: \(make)")
            }
            
            if let model = tiffData[kCGImagePropertyTIFFModel as String] as? String {
                result["cameraModel"] = model
                print("📱 Camera Model: \(model)")
            }
            
            if let dateTime = tiffData[kCGImagePropertyTIFFDateTime as String] as? String {
                result["dateTime"] = formatExifDate(dateTime)
                print("📅 DateTime: \(dateTime)")
            }
            
            if let software = tiffData[kCGImagePropertyTIFFSoftware as String] as? String {
                result["software"] = software
                print("💾 Software: \(software)")
            }
            
            if let artist = tiffData[kCGImagePropertyTIFFArtist as String] as? String {
                result["owner"] = artist
            } else if let copyright = tiffData[kCGImagePropertyTIFFCopyright as String] as? String {
                result["owner"] = copyright
            }
            
            if let orientation = tiffData[kCGImagePropertyTIFFOrientation as String] as? Int {
                result["orientation"] = getOrientationDescription(orientation)
            }
            
        } else {
            print("📄 No TIFF data found")
        }
        
        // EXIF Data (Technical details)
        if let exifData = properties[kCGImagePropertyExifDictionary as String] as? [String: Any] {
            print("🔧 EXIF data found: \(exifData)")
            
            if let dateTimeOriginal = exifData[kCGImagePropertyExifDateTimeOriginal as String] as? String {
                result["dateTaken"] = formatExifDate(dateTimeOriginal)
                result["originalTime"] = formatExifDate(dateTimeOriginal)
                print("📸 Date Taken: \(dateTimeOriginal)")
            }
            
            if let dateTimeDigitized = exifData[kCGImagePropertyExifDateTimeDigitized as String] as? String {
                result["digitizedTime"] = formatExifDate(dateTimeDigitized)
            }
            
            if let colorSpace = exifData[kCGImagePropertyExifColorSpace as String] as? Int {
                switch colorSpace {
                case 1:
                    result["colorSpace"] = "sRGB"
                case 2:
                    result["colorSpace"] = "Adobe RGB"
                case 65535:
                    result["colorSpace"] = "Uncalibrated"
                default:
                    result["colorSpace"] = "ColorSpace: \(colorSpace)"
                }
            }
            
            if let focalLength = exifData[kCGImagePropertyExifFocalLength as String] as? Double {
                result["focalLength"] = String(focalLength)
            }
            
            if let fNumber = exifData[kCGImagePropertyExifFNumber as String] as? Double {
                result["aperture"] = String(fNumber)
            }
            
            if let exposureTime = exifData[kCGImagePropertyExifExposureTime as String] as? Double {
                result["shutterSpeed"] = String(exposureTime)
            }
            
            if let isoSpeedRatings = exifData[kCGImagePropertyExifISOSpeedRatings as String] as? [Int],
               let iso = isoSpeedRatings.first {
                result["iso"] = String(iso)
            }
            
            if let flash = exifData[kCGImagePropertyExifFlash as String] as? Int {
                result["flash"] = getFlashDescription(flash)
            }
            
            if let whiteBalance = exifData[kCGImagePropertyExifWhiteBalance as String] as? Int {
                result["whiteBalance"] = whiteBalance == 0 ? "Auto" : "Manual"
            }
            
        } else {
            print("🔧 No EXIF data found")
        }
        
        // Basic properties
        if let orientation = properties[kCGImagePropertyOrientation as String] as? Int {
            result["orientation"] = getOrientationDescription(orientation)
        }
        
        if let pixelWidth = properties[kCGImagePropertyPixelWidth as String] as? Int {
            result["imageWidth"] = pixelWidth
        }
        
        if let pixelHeight = properties[kCGImagePropertyPixelHeight as String] as? Int {
            result["imageHeight"] = pixelHeight
        }
        
        print("🎯 ExifExtractorNative: Extraction completed with \(result.count) properties")
        return result
    }
    
    private static func formatExifDate(_ dateString: String) -> String {
        let inputFormatter = DateFormatter()
        inputFormatter.dateFormat = "yyyy:MM:dd HH:mm:ss"
        
        let outputFormatter = DateFormatter()
        outputFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        
        if let date = inputFormatter.date(from: dateString) {
            return outputFormatter.string(from: date)
        }
        
        return dateString
    }
    
    private static func getOrientationDescription(_ orientation: Int) -> String {
        switch orientation {
        case 1: return "Normal [1]"
        case 2: return "Flip Horizontal [2]"
        case 3: return "Rotate 180° [3]"
        case 4: return "Flip Vertical [4]"
        case 5: return "Transpose [5]"
        case 6: return "Rotate 90° CW [6]"
        case 7: return "Transverse [7]"
        case 8: return "Rotate 90° CCW [8]"
        default: return "Unknown [\(orientation)]"
        }
    }
    
    private static func getFlashDescription(_ flash: Int) -> String {
        switch flash {
        case 0: return "No Flash"
        case 1: return "Flash Fired"
        case 5: return "Strobe Return Light Not Detected"
        case 7: return "Strobe Return Light Detected"
        case 9: return "Flash Fired, Compulsory Flash Mode"
        case 16: return "Flash Did Not Fire, Compulsory Flash Mode"
        case 24: return "Flash Did Not Fire, Auto Mode"
        case 25: return "Flash Fired, Auto Mode"
        case 32: return "No Flash Function"
        case 65: return "Flash Fired, Red-Eye Reduction Mode"
        default: return "Flash: \(flash)"
        }
    }
}

// C function for Kotlin/Native interop
@_cdecl("extractExifDataFromPath")
public func extractExifDataFromPath(_ path: UnsafePointer<CChar>) -> NSObject? {
    let pathString = String(cString: path)
    print("🔄 ExifExtractorNative Bridge: Processing path: \(pathString)")
    
    return autoreleasepool { () -> NSObject? in
        if let exifData = ExifExtractorNative.extractExifData(from: pathString) {
            print("✅ ExifExtractorNative Bridge: Data extracted successfully")
            let dict = NSDictionary(dictionary: exifData)
            print("📦 ExifExtractorNative Bridge: Converted to NSDictionary")
            return dict
        }
        print("❌ ExifExtractorNative Bridge: Failed to extract data")
        return nil
    }
}
