# ImagePickerLauncher Documentation

## Overview

`ImagePickerLauncher` is a Kotlin Multiplatform Compose function that provides a unified interface for launching camera capture and gallery selection functionality across Android and iOS platforms.

## Function Signature

```kotlin
@Composable
expect fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onPhotosSelected: ((List<GalleryPhotoHandler.PhotoResult>) -> Unit)? = null,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null,
    dialogTitle: String = getStringResource(StringResource.SELECT_OPTION_DIALOG_TITLE),
    takePhotoText: String = getStringResource(StringResource.TAKE_PHOTO_OPTION),
    selectFromGalleryText: String = getStringResource(StringResource.SELECT_FROM_GALLERY_OPTION),
    cancelText: String = getStringResource(StringResource.CANCEL_OPTION),
    buttonColor: Color? = null,
    iconColor: Color? = null,
    buttonSize: Dp? = null,
    layoutPosition: String? = null,
    flashIcon: ImageVector? = null,
    switchCameraIcon: ImageVector? = null,
    captureIcon: ImageVector? = null,
    galleryIcon: ImageVector? = null,
    onCameraReady: (() -> Unit)? = null,
    onCameraSwitch: (() -> Unit)? = null,
    onPermissionError: ((Exception) -> Unit)? = null,
    onGalleryOpened: (() -> Unit)? = null,
    allowMultiple: Boolean = false,
    mimeTypes: List<String> = listOf("image/*")
)
```

## Parameters

### Required Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `context` | `Any?` | The context/activity for the launcher. Must be `ComponentActivity` on Android. |
| `onPhotoCaptured` | `(PhotoResult) -> Unit` | Callback invoked when a photo is captured from camera. |
| `onError` | `(Exception) -> Unit` | Callback invoked when an error occurs during the process. |

### Optional Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `onPhotosSelected` | `((List<GalleryPhotoHandler.PhotoResult>) -> Unit)?` | `null` | Callback for multiple photo selection from gallery. |
| `customPermissionHandler` | `((PermissionConfig) -> Unit)?` | `null` | Custom permission handling logic. |
| `customConfirmationView` | `(@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` | `null` | Custom confirmation view for captured photos. |
| `preference` | `CapturePhotoPreference?` | `null` | Photo capture preferences (FAST, QUALITY, etc.). |
| `dialogTitle` | `String` | Localized "Select option" | Title for the selection dialog. |
| `takePhotoText` | `String` | Localized "Take photo" | Text for camera option. |
| `selectFromGalleryText` | `String` | Localized "Select from gallery" | Text for gallery option. |
| `cancelText` | `String` | Localized "Cancel" | Text for cancel option. |
| `buttonColor` | `Color?` | `null` | Custom color for UI buttons. |
| `iconColor` | `Color?` | `null` | Custom color for UI icons. |
| `buttonSize` | `Dp?` | `null` | Custom size for UI buttons. |
| `layoutPosition` | `String?` | `null` | Custom layout position. |
| `flashIcon` | `ImageVector?` | `null` | Custom flash icon. |
| `switchCameraIcon` | `ImageVector?` | `null` | Custom camera switch icon. |
| `captureIcon` | `ImageVector?` | `null` | Custom capture button icon. |
| `galleryIcon` | `ImageVector?` | `null` | Custom gallery icon. |
| `onCameraReady` | `(() -> Unit)?` | `null` | Callback when camera is ready. |
| `onCameraSwitch` | `(() -> Unit)?` | `null` | Callback when camera is switched. |
| `onPermissionError` | `((Exception) -> Unit)?` | `null` | Callback for permission errors. |
| `onGalleryOpened` | `(() -> Unit)?` | `null` | Callback when gallery is opened. |
| `allowMultiple` | `Boolean` | `false` | Allow multiple photo selection. |
| `mimeTypes` | `List<String>` | `["image/*"]` | Allowed MIME types for file selection. |

## Return Types

### PhotoResult
```kotlin
data class PhotoResult(
    val uri: String,
    val width: Int,
    val height: Int
)
```

### GalleryPhotoHandler.PhotoResult
```kotlin
data class PhotoResult(
    val uri: String,
    val width: Int,
    val height: Int
)
```

## Platform-Specific Behavior

### Android Implementation
- Uses `CameraCaptureView` for camera functionality
- Requires `ComponentActivity` as context
- Supports CameraX for camera operations
- Native Android gallery integration

### iOS Implementation
- Uses native iOS camera and photo picker
- Shows action sheet for option selection
- Handles permissions through iOS system dialogs
- Supports both camera and photo library access

## Usage Examples

### Basic Usage
```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        config = ImagePickerConfig(
            onPhotoCaptured = { result ->
                // Handle captured photo
                println("Photo captured: ${result.uri}")
            },
            onError = { exception ->
                // Handle error
                println("Error: ${exception.message}")
            }
        )
    )
}
```

### Advanced Usage with Customization
```kotlin
@Composable
fun CustomImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Handle captured photo
        },
        onPhotosSelected = { results ->
            // Handle multiple selected photos
        },
        onError = { exception ->
            // Handle error
        },
        preference = CapturePhotoPreference.QUALITY,
        buttonColor = Color.Blue,
        iconColor = Color.White,
        allowMultiple = true,
        mimeTypes = listOf("image/jpeg", "image/png")
    )
}
```

### Custom Permission Handling
```kotlin
@Composable
fun ImagePickerWithCustomPermissions() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Handle captured photo
        },
        onError = { exception ->
            // Handle error
        },
        customPermissionHandler = { config ->
            // Custom permission handling logic
            when (config) {
                is PermissionConfig.Camera -> {
                    // Handle camera permission
                }
                is PermissionConfig.Gallery -> {
                    // Handle gallery permission
                }
            }
        }
    )
}
```

## Error Handling

The function handles various error scenarios:

1. **Invalid Context**: Throws exception if context is not `ComponentActivity` on Android
2. **Permission Denied**: Handled through `onPermissionError` callback
3. **Camera Unavailable**: Handled through `onError` callback
4. **Gallery Access Issues**: Handled through `onError` callback

## Permissions

### Android Permissions
- `CAMERA`: Required for camera functionality
- `READ_EXTERNAL_STORAGE`: Required for gallery access
- `WRITE_EXTERNAL_STORAGE`: Required for saving photos

### iOS Permissions
- `NSCameraUsageDescription`: Required for camera access
- `NSPhotoLibraryUsageDescription`: Required for photo library access

## Localization

The function supports localization through the `StringResource` system:

- `SELECT_OPTION_DIALOG_TITLE`: Dialog title
- `TAKE_PHOTO_OPTION`: Camera option text
- `SELECT_FROM_GALLERY_OPTION`: Gallery option text
- `CANCEL_OPTION`: Cancel option text

## Best Practices

1. **Always handle errors**: Provide proper error handling in `onError` callback
2. **Check permissions**: Use `customPermissionHandler` for custom permission logic
3. **Validate context**: Ensure proper context is provided (ComponentActivity on Android)
4. **Handle multiple selection**: Use `onPhotosSelected` for multiple photo handling
5. **Customize UI**: Use color and icon parameters for consistent theming

## Dependencies

### Android Dependencies
```kotlin
implementation("androidx.camera:camera-core:1.3.0")
implementation("androidx.camera:camera-camera2:1.3.0")
implementation("androidx.camera:camera-lifecycle:1.3.0")
implementation("androidx.camera:camera-view:1.3.0")
```

### iOS Dependencies
- No additional dependencies required (uses native iOS APIs)

## Version Compatibility

- **Kotlin**: 1.8.0+
- **Compose**: 1.4.0+
- **Android**: API 21+
- **iOS**: 13.0+

## Migration Guide

### From Previous Versions
1. Update context parameter to use `Any?` type
2. Ensure proper error handling implementation
3. Update permission handling if using custom logic
4. Review callback signatures for any changes

## Troubleshooting

### Common Issues

1. **Context Error on Android**
   - Ensure context is `ComponentActivity`
   - Check activity lifecycle

2. **Permission Issues**
   - Verify manifest permissions
   - Check iOS Info.plist entries
   - Implement custom permission handling

3. **Camera Not Working**
   - Check device camera availability
   - Verify camera permissions
   - Ensure proper activity context

4. **Gallery Access Issues**
   - Check storage permissions
   - Verify MIME type configuration
   - Ensure proper file provider setup

## Support

For issues and questions:
- GitHub Issues: [Repository URL]
- Documentation: [Documentation URL]
- Examples: [Examples URL] 