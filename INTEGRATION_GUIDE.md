# Integration Guide

This guide will help you integrate ImagePickerKMP into your Kotlin Multiplatform project for both Android and iOS platforms.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Android Setup](#android-setup)
- [iOS Setup](#ios-setup)
- [Basic Integration](#basic-integration)
- [Advanced Configuration](#advanced-configuration)
- [Troubleshooting](#troubleshooting)
- [Migration from Other Libraries](#migration-from-other-libraries)
- [Gallery Selection & iOS Dialog Customization](#gallery-selection-ios-dialog-customization)

## Prerequisites

Before integrating ImagePickerKMP, ensure you have:

- **Kotlin Multiplatform project** set up
- **Android Studio** or **IntelliJ IDEA** with Kotlin plugin
- **Xcode** (for iOS development)
- **Minimum SDK versions**:
  - Android: API 21+
  - iOS: iOS 12.0+

## Android Setup

### 1. Add Dependencies

In your `build.gradle.kts` (app level):

```kotlin
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.0")
}
```

### 2. Add Permissions

In your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="true" />
```

### 3. Configure ProGuard (if using)

Add to your `proguard-rules.pro`:

```proguard
-keep class io.github.ismoy.imagepickerkmp.** { *; }
```

## iOS Setup

### 1. Add Dependencies

In your `build.gradle.kts` (shared module):

```kotlin
kotlin {
    ios {
        binaries {
            framework {
                baseName = "ImagePickerKMP"
            }
        }
    }
}
```

### 2. Add Camera Permission

In your `Info.plist`:

```xml
<key>NSCameraUsageDescription</key>
<string>This app needs camera access to capture photos</string>
```

### 3. Configure Podfile (if using CocoaPods)

```ruby
target 'YourApp' do
  use_frameworks!
  pod 'ImagePickerKMP', :path => '../path/to/your/library'
end
```

## Basic Integration

### Simple Implementation

```kotlin
@Composable
fun SimpleImagePicker() {
    var showPicker by remember { mutableStateOf(false) }
    
    if (showPicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                // Handle successful photo capture
                println("Photo captured: ${result.uri}")
                showPicker = false
            },
            onError = { exception ->
                // Handle errors
                println("Error: ${exception.message}")
                showPicker = false
            }
        )
    }
    
    Button(onClick = { showPicker = true }) {
        Text("Take Photo")
    }
}
```

### With Custom Configuration

```kotlin
@Composable
fun CustomImagePicker() {
    var showPicker by remember { mutableStateOf(false) }
    
    if (showPicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                // Handle photo capture
                showPicker = false
            },
            onError = { exception ->
                // Handle errors
                showPicker = false
            },
            customPermissionHandler = { config ->
                // Custom permission handling
                println("Custom permission config: ${config.titleDialogConfig}")
            },
            customConfirmationView = { result, onConfirm, onRetry ->
                // Custom confirmation view
                CustomConfirmationDialog(
                    result = result,
                    onConfirm = onConfirm,
                    onRetry = onRetry
                )
            },
            preference = CapturePhotoPreference.HIGH_QUALITY
        )
    }
    
    Button(onClick = { showPicker = true }) {
        Text("Take High Quality Photo")
    }
}
```

## Advanced Configuration

### Custom Permission Dialogs

```kotlin
@Composable
fun CustomPermissionDialog(
    title: String,
    description: String,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(title) },
        text = { Text(description) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Grant Permission")
            }
        }
    )
}

// Usage
ImagePickerLauncher(
    context = LocalContext.current,
    onPhotoCaptured = { /* ... */ },
    onError = { /* ... */ },
    customPermissionHandler = { config ->
        // Show custom permission dialog
        CustomPermissionDialog(
            title = config.titleDialogConfig,
            description = config.descriptionDialogConfig,
            onConfirm = { /* Grant permission logic */ }
        )
    }
)
```

### Analytics Integration

```kotlin
@Composable
fun ImagePickerWithAnalytics() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Track photo capture event
            FirebaseAnalytics.getInstance(context).logEvent("photo_captured", Bundle().apply {
                putString("photo_uri", result.uri.toString())
            })
        },
        onError = { exception ->
            // Track error event
            FirebaseAnalytics.getInstance(context).logEvent("photo_capture_error", Bundle().apply {
                putString("error_message", exception.message)
            })
        }
    )
}
```

### Custom Photo Processing

```kotlin
@Composable
fun ImagePickerWithProcessing() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Process the captured photo
            lifecycleScope.launch {
                val processedImage = processImage(result.uri)
                // Use processed image
            }
        },
        onError = { exception ->
            // Handle processing errors
        }
    )
}

suspend fun processImage(uri: Uri): Bitmap {
    return withContext(Dispatchers.IO) {
        // Image processing logic
        // Resize, compress, apply filters, etc.
    }
}
```

## Platform-Specific Configuration

### Android Specific

```kotlin
// Android-specific configuration
@Composable
fun AndroidImagePicker() {
    val context = LocalContext.current
    
    ImagePickerLauncher(
        context = context,
        onPhotoCaptured = { result ->
            // Android-specific handling
            if (context is ComponentActivity) {
                // Use Android-specific APIs
            }
        },
        onError = { exception ->
            // Android-specific error handling
        }
    )
}
```

### iOS Specific

```kotlin
// iOS-specific configuration
@Composable
fun IOSImagePicker() {
    ImagePickerLauncher(
        context = null, // iOS doesn't need context
        onPhotoCaptured = { result ->
            // iOS-specific handling
        },
        onError = { exception ->
            // iOS-specific error handling
        }
    )
}
```

## Gallery Selection & iOS Dialog Customization

### Multiplatform Gallery Support

You can allow users to select images from the gallery on both Android and iOS. On Android, a gallery icon appears in the camera UI. On iOS, you can present a dialog to choose between camera and gallery.

### iOS Dialog Text Customization

You can customize the dialog texts (title, take photo, select from gallery, cancel) on iOS:

```kotlin
ImagePickerLauncher(
    context = ..., // platform context
    onPhotoCaptured = { result -> /* ... */ },
    onError = { exception -> /* ... */ },
    dialogTitle = "Choose action", // iOS only
    takePhotoText = "Camera",      // iOS only
    selectFromGalleryText = "Gallery", // iOS only
    cancelText = "Dismiss"         // iOS only
)
```

- On Android, these parameters are ignored.
- On iOS, if not provided, defaults are in English.

## Troubleshooting

### Common Issues

#### 1. Permission Denied

**Problem**: Camera permission is denied and not showing retry dialog.

**Solution**: Ensure you're using the `RequestCameraPermission` component:

```kotlin
@Composable
fun RequestCameraPermission(
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit
) {
    // Implementation handles platform differences
}
```

#### 2. Camera Not Starting

**Problem**: Camera doesn't start after permission is granted.

**Solution**: Check your camera permissions and hardware:

```kotlin
// Check if camera is available
val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
val cameraIds = cameraManager.cameraIdList
if (cameraIds.isNotEmpty()) {
    // Camera is available
}
```

#### 3. iOS Build Errors

**Problem**: iOS build fails with linking errors.

**Solution**: Ensure proper framework configuration:

```kotlin
kotlin {
    ios {
        binaries {
            framework {
                baseName = "ImagePickerKMP"
                isStatic = false
            }
        }
    }
}
```

#### 4. Memory Issues

**Problem**: App crashes due to memory issues with large images.

**Solution**: Implement image compression:

```kotlin
@Composable
fun ImagePickerWithCompression() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Compress image before processing
            val compressedImage = compressImage(result.uri)
        }
    )
}
```

### Debug Tips

1. **Enable Logging**:
```kotlin
// Add logging to track issues
ImagePickerLauncher(
    context = LocalContext.current,
    onPhotoCaptured = { result ->
        Log.d("ImagePicker", "Photo captured: ${result.uri}")
    },
    onError = { exception ->
        Log.e("ImagePicker", "Error: ${exception.message}", exception)
    }
)
```

2. **Check Permissions**:
```kotlin
// Debug permission status
fun checkCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, 
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}
```

3. **Test on Different Devices**:
- Test on physical devices, not just emulators
- Test on different Android versions
- Test on different iOS versions

## Migration from Other Libraries

### From CameraX

```kotlin
// Old CameraX implementation
class CameraXImplementation {
    fun startCamera() {
        // CameraX code
    }
}

// New ImagePickerKMP implementation
@Composable
fun ImagePickerKMPImplementation() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Handle photo capture
        },
        onError = { exception ->
            // Handle errors
        }
    )
}
```

### From UIImagePickerController (iOS)

```swift
// Old UIImagePickerController implementation
let imagePicker = UIImagePickerController()
imagePicker.sourceType = .camera
present(imagePicker, animated: true)

// New ImagePickerKMP implementation
@Composable
fun ImagePickerKMPImplementation() {
    ImagePickerLauncher(
        context = null,
        onPhotoCaptured = { result ->
            // Handle photo capture
        },
        onError = { exception ->
            // Handle errors
        }
    )
}
```

## Best Practices

### 1. Error Handling

```kotlin
@Composable
fun RobustImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            try {
                // Process photo safely
                processPhoto(result)
            } catch (e: Exception) {
                // Handle processing errors
                showError("Failed to process photo: ${e.message}")
            }
        },
        onError = { exception ->
            when (exception) {
                is CameraPermissionException -> {
                    showPermissionError()
                }
                is PhotoCaptureException -> {
                    showCaptureError()
                }
                else -> {
                    showGenericError()
                }
            }
        }
    )
}
```

### 2. Memory Management

```kotlin
@Composable
fun MemoryEfficientImagePicker() {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Store URI instead of Bitmap to save memory
            imageUri = result.uri
        },
        onError = { exception ->
            // Handle errors
        }
    )
    
    // Load image only when needed
    imageUri?.let { uri ->
        AsyncImage(
            model = uri,
            contentDescription = "Captured photo"
        )
    }
}
```

### 3. User Experience

```kotlin
@Composable
fun UserFriendlyImagePicker() {
    var isLoading by remember { mutableStateOf(false) }
    
    if (isLoading) {
        CircularProgressIndicator()
    } else {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                isLoading = true
                // Process photo
                processPhoto(result) {
                    isLoading = false
                }
            },
            onError = { exception ->
                // Show user-friendly error message
                showSnackbar("Unable to capture photo. Please try again.")
            }
        )
    }
}
```

## Support

If you encounter issues during integration:

1. **Check the documentation**: Review this guide and other documentation
2. **Search issues**: Look for similar issues in the GitHub repository
3. **Create an issue**: Provide detailed information about your problem
4. **Community support**: Ask questions in the community forums

For more detailed information, refer to:
- [API Reference](API_REFERENCE.md)
- [Examples](EXAMPLES.md)
- [Permission Guide](Permission_readme_English.md) 