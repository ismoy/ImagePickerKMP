# imagepickerkmp — Photo Module

Camera capture and gallery image picking for Kotlin Multiplatform.

[![Maven Central](https://img.shields.io/maven-central/v/io.github.ismoy/imagepickerkmp.svg?label=Maven%20Central)](https://search.maven.org/artifact/io.github.ismoy/imagepickerkmp)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

Part of the [ImagePickerKMP ecosystem](../README.md).

---

## Installation

```kotlin
// commonMain
implementation("io.github.ismoy:imagepickerkmp-photo:1.1.0")
```

### iOS — `Info.plist`

```xml
<key>NSCameraUsageDescription</key>
<string>Required to capture photos.</string>
<key>NSPhotoLibraryUsageDescription</key>
<string>Required to select photos from your library.</string>
```

---

## Basic Usage

`rememberImagePickerKMP` is the primary API. It returns a single state holder you call `launchCamera()` or `launchGallery()` on.

```kotlin
@Composable
fun MyScreen() {
    val picker = rememberImagePickerKMP()

    Row {
        Button(onClick = { picker.launchCamera() })  { Text("Camera") }
        Button(onClick = { picker.launchGallery() }) { Text("Gallery") }
    }

    when (val result = picker.result) {
        is ImagePickerResult.Idle      -> Text("Press a button to start.")
        is ImagePickerResult.Loading   -> CircularProgressIndicator()
        is ImagePickerResult.Success   -> {
            result.photos.forEach { photo ->
                Image(painter = photo.loadPainter(), contentDescription = null,
                      modifier = Modifier.fillMaxWidth())
            }
        }
        is ImagePickerResult.Dismissed -> Text("Cancelled.")
        is ImagePickerResult.Error     -> Text("Error: ${result.exception.message}")
    }
}
```

---

## Configuration

Pass `ImagePickerKMPConfig` to customise behaviour globally. Every field has a sensible default.

```kotlin
val picker = rememberImagePickerKMP(
    config = ImagePickerKMPConfig(
        galleryConfig = GalleryConfig(
            allowMultiple    = true,
            selectionLimit   = 10,
            mimeTypes        = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG),
            includeExif      = true,
            redactGpsData    = true  // strip GPS before returning
        ),
        cameraCaptureConfig = CameraCaptureConfig(
            compressionLevel = CompressionLevel.HIGH,
            includeExif      = true,
            redactGpsData    = false
        ),
        cropConfig = CropConfig(
            enabled          = true,
            squareCrop       = true,
            circularCrop     = false
        )
    )
)
```

### Per-launch overrides

Any parameter can be overridden for a single launch without changing the global config:

```kotlin
// Override gallery options once
picker.launchGallery(
    allowMultiple  = true,
    selectionLimit = 5,
    mimeTypes      = listOf(MimeType.IMAGE_JPEG),
    includeExif    = true
)

// Override camera options once
picker.launchCamera(
    cameraCaptureConfig = CameraCaptureConfig(
        compressionLevel = CompressionLevel.LOW
    ),
    onDismiss = { /* user cancelled */ },
    onError   = { exception -> /* handle error */ }
)
```

---

## `ImagePickerResult`

| State | When |
|-------|------|
| `Idle` | Initial state, or after `reset()` |
| `Loading` | Picker is open and waiting for user input |
| `Success(photos)` | User selected one or more photos |
| `Dismissed` | User cancelled without selecting |
| `Error(exception)` | Something went wrong |

```kotlin
when (val result = picker.result) {
    is ImagePickerResult.Success -> {
        val first: PhotoResult? = result.first   // shortcut for single picks
        val all: List<PhotoResult> = result.photos
    }
    else -> Unit
}
```

---

## `PhotoResult`

Every selected or captured image is returned as a `PhotoResult`.

```kotlin
data class PhotoResult(
    val uri      : String,
    val width    : Int?,
    val height   : Int?,
    val fileName : String?,
    val fileSize : Long?,    // bytes
    val mimeType : String?,
    val exif     : ExifData?
)
```

### Extension functions

```kotlin
val photo: PhotoResult = result.photos.first()

// Compose
val painter = photo.loadPainter()       // Painter  — use in Image()
val bitmap  = photo.loadImageBitmap()  // ImageBitmap — use in Canvas

// Raw data
val bytes  = photo.loadBytes()         // ByteArray
val base64 = photo.loadBase64()        // Base64 string (for APIs, uploads)

// File system (kotlinx-io)
val path   = photo.asPath()            // kotlinx.io.files.Path
val exists = photo.exists()            // Boolean
val source = photo.asSource()          // Buffered Source for reading

// Transfer
val sink = SystemFileSystem.sink(Path("copy.jpg"))
photo.transferToSink(sink)
```

---

## EXIF Metadata

Enable `includeExif = true` in `GalleryConfig` or `CameraCaptureConfig`. GPS data is **redacted by default** (`redactGpsData = true`); opt in explicitly.

```kotlin
val exif: ExifData? = photo.exif

exif?.let {
    println("Camera : ${it.cameraModel} by ${it.cameraManufacturer}")
    println("Taken  : ${it.dateTaken}")
    println("ISO    : ${it.iso}  f/${it.aperture}  ${it.shutterSpeed}")
    println("GPS    : ${it.latitude}, ${it.longitude}")  // null if redacted
    println("Size   : ${it.imageWidth}×${it.imageHeight}")
}
```

Available on Android and iOS only.

---

## Compression

Default is **no compression** (`compressionLevel = null`). Pass a level to enable it.

```kotlin
// Camera
CameraCaptureConfig(
    compressionLevel = CompressionLevel.HIGH  // LOW | MEDIUM | HIGH | null (default, no compression)
)

// Gallery — use GalleryConfig, not CameraCaptureConfig
GalleryConfig(
    compressionLevel = CompressionLevel.MEDIUM
)
```

| Level | JPEG Quality | Max Dimension | Use case |
|-------|-------------|---------------|----------|
| `null` (default) | — | original | Preserve original quality |
| `LOW` | 85% | 3840 px | Near-lossless, large files |
| `MEDIUM` | 70% | 1920 px | Balanced — recommended |
| `HIGH` | 50% | 1280 px | Small files, upload-friendly |

`compressionLevel = null` skips all resizing and re-encoding on both Android and iOS — `PhotoResult.fileSize` will reflect the original file size.

---

## Crop

```kotlin
CropConfig(
    enabled          = true,
    squareCrop       = true,    // show square preset
    circularCrop     = true,    // show circle preset
    freeformCrop     = false,   // allow free-form resize
    aspectRatioLocked = false
)
```

The crop UI is built with Compose and works on all platforms that support Compose rendering.

---

## PDF Support

Include `MimeType.APPLICATION_PDF` in `mimeTypes` to allow PDF selection alongside images:

```kotlin
picker.launchGallery(
    mimeTypes = listOf(
        MimeType.IMAGE_JPEG,
        MimeType.IMAGE_PNG
    )
)
```

On Android, the library automatically opens the appropriate system picker (gallery for images).

---

## `ImagePickerKMPState` API reference

| Method | Description |
|--------|-------------|
| `launchCamera(...)` | Open the camera picker |
| `launchGallery(...)` | Open the gallery picker |
| `reset()` | Reset result to `Idle` and close any active picker |
| `result` | Current `ImagePickerResult` — observe this in your UI |
| `isCropActive` | `true` while the crop screen is visible |

---

## Platform support

| Feature | Android | iOS | Desktop | Web |
|---------|:-------:|:---:|:-------:|:---:|
| Camera capture | ✅ | ✅ |    ❌    | ❌ |
| Gallery picking | ✅ | ✅ |    ✅    | ✅ |
| Multiple selection | ✅ | ✅ |    ✅    | ✅ |
| Crop | ✅ | ✅ |❌| ❌ |
| EXIF | ✅ | ✅ |    ❌    | ❌ |
| Compression | ✅ | ✅ |    ❌    | ❌ |
