

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

- [Problemas Comunes](#problemas-comunes)

## General Questions

### What is ImagePickerKMP?

ImagePickerKMP is a modern, cross-platform image picker library for Kotlin Multiplatform (KMP) that provides seamless camera integration for both Android and iOS platforms.

**Key Features:**
- Cross-platform camera integration
- Smart permission handling
- Customizable UI components

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
    implementation("io.github.ismoy:imagepickerkmp-photo:1.1.0")
    // ... add other modules as needed (video, audio, scanner)
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
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            println("Photo captured: ${result.photos.first()}")
        }
        is ImagePickerResult.Error -> {
            println("Error: ${result.exception.message}")
        }
        is ImagePickerResult.Dismissed -> {
            println("User cancelled or dismissed the picker")
        }
        is ImagePickerResult.Loading -> { /* loading */ }
        is ImagePickerResult.Idle -> { /* initial state */ }
    }

    Button(onClick = { picker.launchCamera() }) {
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
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                preference = CapturePhotoPreference.HIGH_QUALITY,
                permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                    customPermissionHandler = { config ->
                        // Custom permission handling
                    },
                    customConfirmationView = { result, onConfirm, onRetry ->
                        // Custom confirmation view
                    }
                )
            )
        )
    )

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            // Handle captured photo
        }
        is ImagePickerResult.Error -> {
            // Handle error
        }
        is ImagePickerResult.Dismissed -> { /* cancelled */ }
        is ImagePickerResult.Loading -> { /* loading */ }
        is ImagePickerResult.Idle -> { /* initial state */ }
    }

    Button(onClick = { picker.launchCamera() }) {
        Text("Take Photo")
    }
}
```

### How do I handle different photo qualities?

```kotlin
@Composable
fun HighQualityImagePicker() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                preference = CapturePhotoPreference.HIGH_QUALITY
            )
        )
    )

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            // Handle high quality photo
        }
        is ImagePickerResult.Error -> {
            // Handle errors
        }
        is ImagePickerResult.Dismissed -> { /* cancelled */ }
        is ImagePickerResult.Loading -> { /* loading */ }
        is ImagePickerResult.Idle -> { /* initial state */ }
    }

    Button(onClick = { picker.launchCamera() }) {
        Text("Capture High Quality")
    }
}
```

## How do I handle user cancellation?

The library provides an `ImagePickerResult.Dismissed` state that is emitted when the user cancels or dismisses the picker without selecting anything. This is essential for resetting your UI state.

### Camera Example

```kotlin
@Composable
fun MyImagePicker() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            println("Photo captured: ${result.photos.first()}")
        }
        is ImagePickerResult.Error -> {
            println("Error: ${result.exception.message}")
        }
        is ImagePickerResult.Dismissed -> {
            println("User cancelled or dismissed the picker")
        }
        is ImagePickerResult.Loading -> { /* loading */ }
        is ImagePickerResult.Idle -> { /* initial state */ }
    }

    Button(onClick = { picker.launchCamera() }) {
        Text("Take Photo")
    }
}
```

### Gallery Example

```kotlin
@Composable
fun MyGalleryPicker() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(allowMultiple = true)
        )
    )

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            println("Selected ${result.photos.size} images")
        }
        is ImagePickerResult.Error -> {
            println("Error: ${result.exception.message}")
        }
        is ImagePickerResult.Dismissed -> {
            println("User cancelled gallery selection")
        }
        is ImagePickerResult.Loading -> { /* loading */ }
        is ImagePickerResult.Idle -> { /* initial state */ }
    }

    Button(onClick = { picker.launchGallery(allowMultiple = true) }) {
        Text("Pick from Gallery")
    }
}
```

**When `ImagePickerResult.Dismissed` is emitted:**
- **Android:** User cancels the selection dialog or camera interface
- **iOS:** User taps "Cancel" in the dialog or camera interface
- **iOS:** User cancels camera permission request
- **iOS:** User cancels the camera interface (taps "Cancel" or "X")

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
- Some platform-specific optimizations

### How do I handle platform-specific code?

```kotlin
@Composable
fun PlatformSpecificImagePicker() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            // Platform-agnostic handling
        }
        is ImagePickerResult.Error -> {
            when (result.exception) {
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
        is ImagePickerResult.Dismissed -> { /* cancelled */ }
        is ImagePickerResult.Loading -> { /* loading */ }
        is ImagePickerResult.Idle -> { /* initial state */ }
    }

    Button(onClick = { picker.launchCamera() }) {
        Text("Take Photo")
    }
}
```

### What about iOS-specific features?

iOS-specific features are handled internally by the library. You don't need to write platform-specific code for most use cases.

For advanced iOS features:
```kotlin
// iOS-specific configuration
@Composable
fun IOSImagePicker() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            // iOS-specific handling
        }
        is ImagePickerResult.Error -> {
            // iOS-specific error handling
        }
        is ImagePickerResult.Dismissed -> { /* cancelled */ }
        is ImagePickerResult.Loading -> { /* loading */ }
        is ImagePickerResult.Idle -> { /* initial state */ }
    }

    Button(onClick = { picker.launchCamera() }) {
        Text("Take Photo")
    }
}
```

## Troubleshooting

### The camera doesn't start. What should I check?

1. **Permissions**: Ensure camera permission is granted
2. **Hardware**: Check if device has camera
3. **Lifecycle**: Check if component is in correct lifecycle state
4. **Dependencies**: Verify all dependencies are properly added

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
5. **No camera app**: On Android the capture goes through the system camera app, which can be
   absent or disabled (enterprise/kiosk devices, or a user disabling the stock camera)

### The error stays on screen after the picker closes. How do I clear it?

`result` is sticky: `ImagePickerResult.Error` persists until you either start another launch
(which sets `Loading`) or clear it explicitly. Call `reset()` once you've shown the error:

```kotlin
is ImagePickerResult.Error -> {
    ErrorBanner(
        message = result.exception.message,
        onDismiss = { picker.reset() }   // back to Idle
    )
}
```

Call `reset()` from an event handler as above, not directly inside the `when` block — writing
state during composition is what you want to avoid.

This applies to every error, not just an unavailable camera — a MIME-type mismatch or a failed
gallery read behaves the same way. A dismissal that follows an error does *not* clear it, so
"no camera app" stays distinguishable from "user cancelled".

### The app crashes when taking photos. How do I fix it?

1. **Memory issues**: Use image compression for large photos
2. **Lifecycle issues**: Ensure proper lifecycle management
3. **Exception handling**: Add proper error handling

```kotlin
@Composable
fun RobustImagePicker() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            try {
                // Process photo safely
                processPhoto(result.photos.first())
            } catch (e: Exception) {
                // Handle processing errors
                showError("Failed to process photo: ${e.message}")
            }
        }
        is ImagePickerResult.Error -> {
            // Handle capture errors
            showError("Camera error: ${result.exception.message}")
        }
        is ImagePickerResult.Dismissed -> { /* cancelled */ }
        is ImagePickerResult.Loading -> { /* loading */ }
        is ImagePickerResult.Idle -> { /* initial state */ }
    }

    Button(onClick = { picker.launchCamera() }) {
        Text("Take Photo")
    }
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

### iOS build fails with linker error: `_OBJC_CLASS_$_CLLocation` or `CoreLocation.framework` not found

**Symptoms:**

```
Could not find or use auto-linked framework '_LocationEssentials': framework '_LocationEssentials' not found
ld: Undefined symbols:
  _OBJC_CLASS_$_CLLocation, referenced from:
       in ComposeApp[...](libImagePickerKMP:library-cache.a.o)
linker command failed with exit code 1
```

Android and JVM Desktop work fine, but the iOS build fails during the linking phase.

**Cause:**

ImagePickerKMP uses `CoreLocation` internally (e.g. for location metadata when capturing images). On some Xcode / KMP configurations the framework is **not auto-linked**, so the linker cannot resolve `CLLocation` symbols.

**Solution:**

Add `CoreLocation.framework` manually to your Xcode project's **Build Phases**:

1. Open your iOS project (`.xcworkspace` or `.xcodeproj`) in **Xcode**.
2. Select your app target in the Project navigator.
3. Go to **Build Phases → Link Binary With Libraries**.
4. Click **+** and search for **CoreLocation**.
5. Select `CoreLocation.framework` and click **Add**.
6. Clean the build folder (**Product → Clean Build Folder**, or `⇧⌘K`) and rebuild.

> ✅ No code changes are required — this is a project-level configuration step only.

**Reported environment:** Xcode 15.2 · iOS 15 · Kotlin 2.2.x · ImagePickerKMP 1.0.35

## Performance & Optimization

### How do I optimize memory usage?

1. **Use URIs instead of Bitmaps**:
```kotlin
@Composable
fun MemoryEfficientImagePicker() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            // Store URI instead of Bitmap
            imageUri = result.photos.first().uri
        }
        is ImagePickerResult.Error -> { /* handle error */ }
        is ImagePickerResult.Dismissed -> { /* cancelled */ }
        is ImagePickerResult.Loading -> { /* loading */ }
        is ImagePickerResult.Idle -> { /* initial state */ }
    }

    // Load image only when needed
    imageUri?.let { uri ->
        AsyncImage(
            model = uri,
            contentDescription = "Captured photo"
        )
    }

    Button(onClick = { picker.launchCamera() }) {
        Text("Take Photo")
    }
}
```

2. **Use image compression**:
```kotlin
@Composable
fun CompressedImagePicker() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                compressionLevel = CompressionLevel.MEDIUM  // LOW | MEDIUM | HIGH
            ),
            galleryConfig = GalleryConfig(
                compressionLevel = CompressionLevel.MEDIUM  // set separately for gallery
            )
        )
    )

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            // Image is compressed
            val photo = result.photos.first()
        }
        is ImagePickerResult.Error -> { /* handle error */ }
        is ImagePickerResult.Dismissed -> { /* cancelled */ }
        is ImagePickerResult.Loading -> { /* loading */ }
        is ImagePickerResult.Idle -> { /* initial state */ }
    }

    Button(onClick = { picker.launchCamera() }) {
        Text("Take Compressed Photo")
    }
}
```

### How do I improve camera startup time?

1. **Use FAST preference**:
```kotlin
val picker = rememberImagePickerKMP(
    config = ImagePickerKMPConfig(
        cameraCaptureConfig = CameraCaptureConfig(
            preference = CapturePhotoPreference.FAST
        )
    )
)

Button(onClick = { picker.launchCamera() }) {
    Text("Quick Capture")
}
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
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            lifecycleScope.launch(Dispatchers.IO) {
                // Process large photo in background
                val processedImage = processLargeImage(result.photos.first())
                withContext(Dispatchers.Main) {
                    // Update UI with processed image
                }
            }
        }
        is ImagePickerResult.Error -> { /* handle error */ }
        is ImagePickerResult.Dismissed -> { /* cancelled */ }
        is ImagePickerResult.Loading -> { /* loading */ }
        is ImagePickerResult.Idle -> { /* initial state */ }
    }

    Button(onClick = { picker.launchCamera() }) {
        Text("Take Photo")
    }
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

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    MaterialTheme(colors = customTheme) {
        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                // Handle photo capture
            }
            is ImagePickerResult.Error -> {
                // Handle errors
            }
            is ImagePickerResult.Dismissed -> { /* cancelled */ }
            is ImagePickerResult.Loading -> { /* loading */ }
            is ImagePickerResult.Idle -> { /* initial state */ }
        }

        Button(onClick = { picker.launchCamera() }) {
            Text("Take Photo")
        }
    }
}
```



## Common Issues & Recent Changes

### Why was PDF support removed from the Photo Picker?

**Question**: The photo picker used to allow selecting PDFs. Why was this removed?

**Answer**: To keep the library as lightweight and performant as possible, we restructured it into multiple modules (`imagepickerkmp-photo`, `imagepickerkmp-video`, etc.). The photo module is now strictly a *Photo Picker*. Handling PDFs introduced unnecessary overhead and generic MIME types (`*/*`), which complicated permissions and memory handling. If you need document selection, it's recommended to use a dedicated file picker library.

### I used to get Out Of Memory (OOM) errors on Android when selecting large images. Is this fixed?

**Question**: My app crashed with an OOM error when a user selected multiple 15MB photos from the gallery.

**Answer**: **Yes, this is completely resolved in version 1.1.0+!** 
Previously, the library loaded the entire raw image byte array into memory just to calculate dimensions or apply compression. Now, it uses `ContentResolver.openFileDescriptor` and `inSampleSize` (downsampling) to read dimensions instantly and load a scaled-down version of the image directly from the stream. This reduces memory footprint to almost 0MB during the selection phase, making the library incredibly fast and safe for large photo selections on both Android and iOS.

### Why do front-camera photos look "mirrored" or rotated?

**Problem**: Photos taken with the front camera appear with incorrect orientation (mirrored or rotated).

**Cause**: Android front cameras have a different orientation than rear cameras. The image is captured with an orientation that isn't natural for the user.

**Solution**: The library now includes automatic orientation correction for front camera photos. The system:

1. **Automatically detects** if the photo was taken with the front camera
2. **Applies mirror correction** only when necessary
3. **Maintains quality** of the original image
4. **Is efficient** - only processes when it really needs correction

This is completely automatic and requires no configuration.

## Additional Questions

### Where can I get help?

- **Documentation**: [README.md](../README.md)
- **API Reference**: [API_REFERENCE.md](docs/API_REFERENCE.md)
- **Examples**: [EXAMPLES.md](docs/EXAMPLES.md)
- **GitHub Issues**: [GitHub Issues](https://github.com/ismoy/ImagePickerKMP/issues)
- **Discussions**: [GitHub Discussions](https://github.com/ismoy/ImagePickerKMP/discussions)
- **Email**: belizairesmoy72@gmail.com

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

See [CONTRIBUTING.md](docs/CONTRIBUTING.md) for detailed guidelines.

---

**Still have questions?** Feel free to ask in our [GitHub Discussions](https://github.com/ismoy/ImagePickerKMP/discussions) or contact us directly.
