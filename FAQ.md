# Frequently Asked Questions (FAQ)

Common questions and answers about ImagePickerKMP.

## Table of Contents

- [General Questions](#general-questions)
- [Installation & Setup](#installation--setup)
- [Usage & Implementation](#usage--implementation)
- [Platform-Specific](#platform-specific)
- [Troubleshooting](#troubleshooting)
- [Performance & Optimization](#performance--optimization)
- [Customization](#customization)
- [Analytics](#analytics)

## General Questions

### What is ImagePickerKMP?

ImagePickerKMP is a modern, cross-platform image picker library for Kotlin Multiplatform (KMP) that provides seamless camera integration for both Android and iOS platforms.

**Key Features:**
- Cross-platform camera integration
- Smart permission handling
- Customizable UI components
- Optional analytics integration
- High-quality photo capture
- Comprehensive error handling

### Which platforms are supported?

- **Android**: API 21+ (Android 5.0+)
- **iOS**: iOS 12.0+
- **Kotlin Multiplatform**: Full support

### What are the minimum requirements?

**Android:**
- Minimum SDK: API 21
- Kotlin: 1.8+
- Compose Multiplatform: 1.4+

**iOS:**
- iOS: 12.0+
- Xcode: 14+
- Kotlin Multiplatform: 1.8+

### Is this library free to use?

Yes, ImagePickerKMP is open-source and free to use under the MIT License. You can use it in both personal and commercial projects.

### How does it compare to other image picker libraries?

**Advantages:**
- Cross-platform with single codebase
- Modern Compose Multiplatform UI
- Smart permission handling
- Customizable components
- Privacy-focused analytics
- Active development and support

**Compared to alternatives:**
- More modern than CameraX (Android-only)
- More integrated than UIImagePickerController (iOS-only)
- Better permission handling than most alternatives
- Cross-platform advantage over platform-specific solutions

## Installation & Setup

### How do I add ImagePickerKMP to my project?

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.0")
}
```

### What permissions do I need to add?

**Android** (`AndroidManifest.xml`):
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="true" />
```

**iOS** (`Info.plist`):
```xml
<key>NSCameraUsageDescription</key>
<string>This app needs camera access to capture photos</string>
```

### Do I need to configure anything else?

For basic usage, no additional configuration is required. The library handles most setup automatically.

For advanced features, you may need to:
- Configure custom themes
- Set up analytics (optional)
- Add custom permission dialogs
- Configure photo capture preferences

### How do I set up for iOS development?

1. **Add to your iOS project**:
   ```ruby
   # Podfile
   target 'YourApp' do
     use_frameworks!
     pod 'ImagePickerKMP', :path => '../path/to/your/library'
   end
   ```

2. **Run pod install**:
   ```bash
   pod install
   ```

3. **Import in your iOS code**:
   ```swift
   import ImagePickerKMP
   ```

## Usage & Implementation

### What's the basic implementation?

```kotlin
@Composable
fun MyImagePicker() {
    var showImagePicker by remember { mutableStateOf(false) }
    
    if (showImagePicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                println("Photo captured: ${result.uri}")
                showImagePicker = false
            },
            onError = { exception ->
                println("Error: ${exception.message}")
                showImagePicker = false
            }
        )
    }
    
    Button(onClick = { showImagePicker = true }) {
        Text("Take Photo")
    }
}
```

### How do I handle permissions?

The library handles permissions automatically, but you can customize the behavior:

```kotlin
@Composable
fun CustomPermissionHandler() {
    RequestCameraPermission(
        titleDialogConfig = "Camera Permission Required",
        descriptionDialogConfig = "Please enable camera access",
        btnDialogConfig = "Open Settings",
        onPermissionPermanentlyDenied = {
            // Handle permanent denial
        },
        onResult = { granted ->
            // Handle permission result
        }
    )
}
```

### How do I customize the UI?

```kotlin
@Composable
fun CustomImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Handle photo capture
        },
        onError = { exception ->
            // Handle errors
        },
        customPermissionHandler = { config ->
            // Custom permission dialog
        },
        customConfirmationView = { result, onConfirm, onRetry ->
            // Custom confirmation view
        }
    )
}
```

### How do I handle different photo qualities?

```kotlin
@Composable
fun HighQualityImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Handle high quality photo
        },
        onError = { exception ->
            // Handle errors
        },
        preference = CapturePhotoPreference.HIGH_QUALITY
    )
}
```

## Platform-Specific

### Are there differences between Android and iOS?

**Similarities:**
- Same API interface
- Same permission handling
- Same error handling
- Same customization options

**Differences:**
- Android uses CameraX, iOS uses AVFoundation
- Permission flow slightly different (iOS shows settings immediately after first denial)
- Context parameter (Android needs context, iOS uses null)
- Some platform-specific optimizations

### How do I handle platform-specific code?

```kotlin
@Composable
fun PlatformSpecificImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current, // null for iOS
        onPhotoCaptured = { result ->
            // Platform-agnostic handling
        },
        onError = { exception ->
            when (exception) {
                is CameraPermissionException -> {
                    // Handle permission errors
                }
                is PhotoCaptureException -> {
                    // Handle capture errors
                }
                else -> {
                    // Handle other errors
                }
            }
        }
    )
}
```

### What about iOS-specific features?

iOS-specific features are handled internally by the library. You don't need to write platform-specific code for most use cases.

For advanced iOS features:
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

## Troubleshooting

### The camera doesn't start. What should I check?

1. **Permissions**: Ensure camera permission is granted
2. **Hardware**: Check if device has camera
3. **Context**: Ensure proper context is passed (Android)
4. **Lifecycle**: Check if component is in correct lifecycle state
5. **Dependencies**: Verify all dependencies are properly added

### Permission dialog doesn't show. What's wrong?

1. **Check manifest**: Ensure camera permission is declared
2. **Check Info.plist**: Ensure NSCameraUsageDescription is set (iOS)
3. **Check implementation**: Ensure RequestCameraPermission is used
4. **Check platform**: Verify platform-specific setup

### I get a "Camera not available" error. Why?

1. **Hardware**: Device may not have camera
2. **Permissions**: Camera permission may be denied
3. **Camera in use**: Another app may be using camera
4. **Simulator**: Camera not available in simulator (use device)

### The app crashes when taking photos. How do I fix it?

1. **Memory issues**: Use image compression for large photos
2. **Lifecycle issues**: Ensure proper lifecycle management
3. **Context issues**: Check context validity
4. **Exception handling**: Add proper error handling

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
            // Handle capture errors
            showError("Camera error: ${exception.message}")
        }
    )
}
```

### How do I debug permission issues?

```kotlin
// Debug permission status
fun debugPermissions(context: Context) {
    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
    
    println("Camera permission granted: $hasPermission")
}
```

## Performance & Optimization

### How do I optimize memory usage?

1. **Use URIs instead of Bitmaps**:
```kotlin
@Composable
fun MemoryEfficientImagePicker() {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Store URI instead of Bitmap
            imageUri = result.uri
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

2. **Use image compression**:
```kotlin
@Composable
fun CompressedImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Compress image before processing
            val compressedImage = compressImage(result.uri, 80)
        }
    )
}
```

### How do I improve camera startup time?

1. **Use FAST preference**:
```kotlin
ImagePickerLauncher(
    context = LocalContext.current,
    preference = CapturePhotoPreference.FAST
)
```

2. **Pre-initialize camera**:
```kotlin
// Pre-initialize camera in background
LaunchedEffect(Unit) {
    initializeCamera()
}
```

### How do I handle large photos?

```kotlin
@Composable
fun LargePhotoHandler() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            lifecycleScope.launch(Dispatchers.IO) {
                // Process large photo in background
                val processedImage = processLargeImage(result.uri)
                withContext(Dispatchers.Main) {
                    // Update UI with processed image
                }
            }
        }
    )
}
```

## Customization

### How do I create custom permission dialogs?

```kotlin
@Composable
fun CustomPermissionDialog(
    title: String,
    description: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(description) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Grant Permission")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
```

### How do I create custom confirmation views?

```kotlin
@Composable
fun CustomConfirmationView(
    result: PhotoResult,
    onConfirm: () -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Photo preview
        AsyncImage(
            model = result.uri,
            contentDescription = "Captured photo",
            modifier = Modifier.weight(1f)
        )
        
        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onRetry) {
                Text("Retry")
            }
            Button(onClick = onConfirm) {
                Text("Use Photo")
            }
        }
    }
}
```

### How do I apply custom themes?

```kotlin
@Composable
fun ThemedImagePicker() {
    val customTheme = remember {
        MaterialTheme.colors.copy(
            primary = Color(0xFF1976D2),
            secondary = Color(0xFF42A5F5)
        )
    }
    
    MaterialTheme(colors = customTheme) {
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
}
```

## Analytics

### How do I enable analytics?

```kotlin
@Composable
fun ImagePickerWithAnalytics() {
    val context = LocalContext.current
    
    ImagePickerLauncher(
        context = context,
        onPhotoCaptured = { result ->
            // Track successful photo capture
            FirebaseAnalytics.getInstance(context).logEvent("photo_captured", Bundle().apply {
                putString("photo_uri", result.uri.toString())
                putLong("photo_size", result.size)
            })
        },
        onError = { exception ->
            // Track errors
            FirebaseAnalytics.getInstance(context).logEvent("photo_capture_error", Bundle().apply {
                putString("error_type", exception.javaClass.simpleName)
                putString("error_message", exception.message)
            })
        }
    )
}
```

### What data is collected by analytics?

**Performance metrics:**
- Camera startup time
- Photo capture time
- Error rates and types

**Usage statistics:**
- Number of photos captured
- Permission request outcomes
- Feature usage frequency

**System information:**
- Platform (Android/iOS)
- OS version
- Device type

**No personal data is collected.**

### How do I disable analytics?

Analytics are disabled by default. If you want to ensure no analytics are collected:

```kotlin
@Composable
fun ImagePickerWithoutAnalytics() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Handle photo capture without analytics
        },
        onError = { exception ->
            // Handle errors without analytics
        }
        // Analytics are disabled by default
    )
}
```

### How do I implement custom analytics?

```kotlin
class CustomAnalytics {
    fun trackPhotoCapture(uri: Uri, size: Long) {
        // Your custom analytics implementation
        println("Photo captured: $uri, size: $size")
    }
    
    fun trackError(error: Exception) {
        // Track errors
        println("Error occurred: ${error.message}")
    }
}

@Composable
fun ImagePickerWithCustomAnalytics() {
    val analytics = remember { CustomAnalytics() }
    
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            analytics.trackPhotoCapture(result.uri, result.size)
        },
        onError = { exception ->
            analytics.trackError(exception)
        }
    )
}
```

## Additional Questions

### Where can I get help?

- **Documentation**: [README.md](README.md)
- **API Reference**: [API_REFERENCE.md](API_REFERENCE.md)
- **Examples**: [EXAMPLES.md](EXAMPLES.md)
- **GitHub Issues**: [GitHub Issues](https://github.com/ismoy/ImagePickerKMP/issues)
- **Discussions**: [GitHub Discussions](https://github.com/ismoy/ImagePickerKMP/discussions)
- **Email**: support@imagepickerkmp.com

### How do I report a bug?

1. **Search existing issues**: Check if the bug is already reported
2. **Create new issue**: Use the bug report template
3. **Provide details**: Include steps to reproduce, environment info, logs
4. **Follow up**: Respond to maintainer questions

### How do I request a feature?

1. **Search existing issues**: Check if the feature is already requested
2. **Create feature request**: Use the feature request template
3. **Provide details**: Include use case, proposed implementation
4. **Discuss**: Engage in community discussions

### How do I contribute?

1. **Fork the repository**
2. **Create a feature branch**
3. **Make your changes**
4. **Add tests**
5. **Submit a pull request**

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.

---

**Still have questions?** Feel free to ask in our [GitHub Discussions](https://github.com/ismoy/ImagePickerKMP/discussions) or contact us directly. 