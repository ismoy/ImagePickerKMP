# ImageProcessor Documentation

## Overview

`ImageProcessor` is a multiplatform utility class responsible for handling all image processing operations within the library. It ensures that images captured from the camera or selected from the gallery are correctly oriented, resized, and formatted for further use or display. This component abstracts away platform-specific details and provides a unified API for image manipulation.

## Main Responsibilities

- **Orientation Correction:** Ensures images are displayed with the correct orientation, using EXIF data or platform-specific metadata.
- **Resizing:** Adjusts image dimensions to meet requirements or optimize performance.
- **Format Conversion:** Converts images between formats (e.g., JPEG, PNG) as needed.
- **Cropping (if implemented):** Allows cropping images to a specific aspect ratio or region.
- **Error Handling:** Handles unsupported formats, corrupted images, or processing failures gracefully.

## Typical Methods

| Method | Description |
|--------|-------------|
| `processImage(input: Image): Image` | Processes the input image and returns a processed version (correct orientation, size, etc.). |
| `correctImageOrientation(image: Image, orientation: Int): Image` | Adjusts the image orientation based on EXIF or platform data. |
| `resizeImage(image: Image, width: Int, height: Int): Image` | Resizes the image to the specified dimensions. |
| `convertFormat(image: Image, format: String): Image` | Converts the image to the specified format. |

## Usage Example

```kotlin
val processed = ImageProcessor.processImage(rawImage)
val rotated = ImageProcessor.correctImageOrientation(processed, orientation)
val resized = ImageProcessor.resizeImage(rotated, 1024, 768)
```

## Error Handling

- Throws exceptions or returns error results if the image cannot be processed.
- Handles unsupported formats and corrupted images gracefully.

## Platform Notes

- **Android:** Uses `Bitmap`, EXIF utilities, and platform APIs for image manipulation.
- **iOS:** Uses `UIImage`, CoreGraphics, and native APIs for processing.

## Best Practices

1. Always handle exceptions from image processing methods.
2. Validate input images before processing.
3. Use appropriate image formats for your use case (e.g., JPEG for photos, PNG for transparency).
4. Test on multiple devices and platforms to ensure consistent results. 