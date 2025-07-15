# CameraCaptureView Documentation

## Overview

`CameraCaptureView` is an Android-specific Compose component that provides a complete camera capture interface with gallery selection capabilities. It handles camera permissions, photo capture, confirmation views, and gallery integration in a single composable.

## Function Signature

```kotlin
@Composable
fun CameraCaptureView(
    activity: ComponentActivity,
    preference: CapturePhotoPreference = CapturePhotoPreference.FAST,
    onPhotoResult: (PhotoResult) -> Unit,
    onPhotosSelected: ((List<GalleryPhotoHandler.PhotoResult>) -> Unit)? = null,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
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
| `activity` | `ComponentActivity` | The Android activity that hosts the camera view. |
| `onPhotoResult` | `(PhotoResult) -> Unit` | Callback invoked when a photo is captured and confirmed. |
| `onError` | `(Exception) -> Unit` | Callback invoked when an error occurs during the process. |

### Optional Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `preference` | `CapturePhotoPreference` | `CapturePhotoPreference.FAST` | Photo capture quality preference. |
| `onPhotosSelected` | `((List<GalleryPhotoHandler.PhotoResult>) -> Unit)?` | `null` | Callback for multiple photo selection from gallery. |
| `customPermissionHandler` | `((PermissionConfig) -> Unit)?` | `null` | Custom permission handling logic. |
| `customConfirmationView` | `(@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` | `null` | Custom confirmation view for captured photos. |
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

## Component States

The `CameraCaptureView` manages three main states:

1. **Permission State**: Checks and requests camera permissions
2. **Camera State**: Shows camera preview with capture controls
3. **Confirmation State**: Shows captured photo with confirm/retry options

## Internal Components

### CameraXManager
- Handles camera lifecycle and operations
- Manages camera preview and photo capture
- Provides camera switching and flash control

### CameraCapturePreview
- Renders camera preview
- Provides capture button and camera controls
- Handles camera interactions

### ImageConfirmationViewWithCustomButtons
- Shows captured photo for confirmation
- Provides confirm and retry options
- Supports custom confirmation views

### GalleryPickerLauncher
- Handles gallery photo selection
- Supports multiple photo selection
- Integrates with system gallery

## Usage Examples

### Basic Camera Capture
```kotlin
@Composable
fun MyCameraView() {
    CameraCaptureView(
        activity = LocalContext.current as ComponentActivity,
        onPhotoResult = { result ->
            // Handle captured photo
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            // Handle error
            println("Error: ${exception.message}")
        }
    )
}
```

### Advanced Usage with Customization
```kotlin
@Composable
fun CustomCameraView() {
    CameraCaptureView(
        activity = LocalContext.current as ComponentActivity,
        preference = CapturePhotoPreference.QUALITY,
        onPhotoResult = { result ->
            // Handle captured photo
        },
        onPhotosSelected = { results ->
            // Handle multiple selected photos
        },
        onError = { exception ->
            // Handle error
        },
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
fun CameraWithCustomPermissions() {
    CameraCaptureView(
        activity = LocalContext.current as ComponentActivity,
        onPhotoResult = { result ->
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

### Custom Confirmation View
```kotlin
@Composable
fun CameraWithCustomConfirmation() {
    CameraCaptureView(
        activity = LocalContext.current as ComponentActivity,
        onPhotoResult = { result ->
            // Handle captured photo
        },
        onError = { exception ->
            // Handle error
        },
        customConfirmationView = { result, onConfirm, onRetry ->
            // Custom confirmation view
            Column {
                AsyncImage(
                    model = result.uri,
                    contentDescription = "Captured photo"
                )
                Row {
                    Button(onClick = { onConfirm(result) }) {
                        Text("Confirm")
                    }
                    Button(onClick = onRetry) {
                        Text("Retry")
                    }
                }
            }
        }
    )
}
```

## Error Handling

The component handles various error scenarios:

1. **Permission Denied**: Automatically requests camera permissions
2. **Camera Unavailable**: Handled through `onError` callback
3. **Gallery Access Issues**: Handled through `onError` callback
4. **Photo Capture Failures**: Handled through `onError` callback

## Permissions

### Required Permissions
- `CAMERA`: Required for camera functionality
- `READ_EXTERNAL_STORAGE`: Required for gallery access
- `WRITE_EXTERNAL_STORAGE`: Required for saving photos

### Permission Flow
1. Component checks for camera permission
2. If not granted, shows permission request dialog
3. If denied, shows settings dialog
4. If permanently denied, calls `onError` with `PhotoCaptureException`

## Camera Features

### Supported Features
- **Camera Preview**: Real-time camera preview
- **Photo Capture**: High-quality photo capture
- **Flash Control**: Flash on/off/auto modes
- **Camera Switch**: Front/back camera switching
- **Gallery Integration**: Direct gallery access
- **Multiple Selection**: Multiple photo selection from gallery

### Camera Controls
- **Capture Button**: Large, accessible capture button
- **Flash Toggle**: Flash mode control
- **Camera Switch**: Switch between front/back cameras
- **Gallery Button**: Quick access to gallery

## UI Customization

### Color Customization
```kotlin
buttonColor = Color.Blue,      // Custom button color
iconColor = Color.White,       // Custom icon color
```

### Icon Customization
```kotlin
flashIcon = Icons.Default.FlashOn,           // Custom flash icon
switchCameraIcon = Icons.Default.Cameraswitch, // Custom camera switch icon
captureIcon = Icons.Default.Camera,          // Custom capture icon
galleryIcon = Icons.Default.PhotoLibrary,    // Custom gallery icon
```

### Size Customization
```kotlin
buttonSize = 24.dp,  // Custom button size
```

## Performance Considerations

### Memory Management
- Camera resources are properly disposed when component is destroyed
- Photo results are managed efficiently
- Preview surfaces are cleaned up automatically

### Battery Optimization
- Camera is stopped when component is not visible
- Flash usage is optimized
- Preview quality is balanced for performance

## Best Practices

1. **Always handle errors**: Provide proper error handling in `onError` callback
2. **Check permissions**: Use `customPermissionHandler` for custom permission logic
3. **Validate activity**: Ensure proper `ComponentActivity` is provided
4. **Handle lifecycle**: Component automatically handles camera lifecycle
5. **Customize UI**: Use color and icon parameters for consistent theming
6. **Test on devices**: Test on various Android devices and API levels

## Dependencies

### Required Dependencies
```kotlin
implementation("androidx.camera:camera-core:1.3.0")
implementation("androidx.camera:camera-camera2:1.3.0")
implementation("androidx.camera:camera-lifecycle:1.3.0")
implementation("androidx.camera:camera-view:1.3.0")
implementation("androidx.activity:activity-compose:1.7.0")
```

### Optional Dependencies
```kotlin
implementation("io.coil-kt:coil-compose:2.4.0")  // For AsyncImage
```

## Version Compatibility

- **Android**: API 21+
- **Compose**: 1.4.0+
- **CameraX**: 1.3.0+
- **Kotlin**: 1.8.0+

## Troubleshooting

### Common Issues

1. **Camera Not Starting**
   - Check camera permissions
   - Verify activity is ComponentActivity
   - Check device camera availability

2. **Permission Issues**
   - Verify manifest permissions
   - Check runtime permission handling
   - Test on different Android versions

3. **Photo Capture Fails**
   - Check storage permissions
   - Verify file provider configuration
   - Check available storage space

4. **Gallery Not Opening**
   - Check storage permissions
   - Verify MIME type configuration
   - Test on different Android versions

5. **UI Not Rendering**
   - Check Compose version compatibility
   - Verify activity lifecycle
   - Check for conflicting UI components

### Debug Tips

1. **Enable Logging**: Add logging to callbacks for debugging
2. **Test Permissions**: Test permission flows thoroughly
3. **Check Lifecycle**: Verify activity lifecycle handling
4. **Monitor Memory**: Check for memory leaks in camera usage
5. **Test Devices**: Test on various Android devices and versions

## Migration Guide

### From Previous Versions
1. Update to use `ComponentActivity` instead of generic context
2. Ensure proper error handling implementation
3. Update permission handling if using custom logic
4. Review callback signatures for any changes
5. Update dependencies to latest versions

## Support

For issues and questions:
- GitHub Issues: [Repository URL]
- Documentation: [Documentation URL]
- Examples: [Examples URL] 