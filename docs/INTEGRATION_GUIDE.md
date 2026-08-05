

# Integration Guide

This guide will help you integrate ImagePickerKMP into your Kotlin Multiplatform project for both Android and iOS platforms and Android Native.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Android Setup](#android-setup)
- [KMP Setup](#kmp-setup)
- [Basic Usage](#basic-usage)
- [Selecting Images from Gallery](#selecting-images-from-gallery)
- [Image Compression Integration](#image-compression-integration)

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
    // Escoge los módulos que necesites:
    implementation("io.github.ismoy:imagepickerkmp-photo:1.1.0")
    implementation("io.github.ismoy:imagepickerkmp-video:1.1.0")
    implementation("io.github.ismoy:imagepickerkmp-audio:1.1.0")
    implementation("io.github.ismoy:imagepickerkmp-scanner:1.1.0")
    implementation("io.github.ismoy:imagepickerkmp-videoplayer:1.1.0")
    implementation("io.github.ismoy:imagepickerkmp-audioplayer:1.1.0")
}
```

### 2. Add Permissions
In your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA" /> //Optional
<uses-feature android:name="android.hardware.camera" android:required="true" /> //Optional
``` 
## KMP Setup

### 1. Add Dependencies

In your `build.gradle.kts` (commonMain):

```kotlin
dependencies {
    // Escoge los módulos que necesites en commonMain:
    implementation("io.github.ismoy:imagepickerkmp-photo:1.1.0")
    implementation("io.github.ismoy:imagepickerkmp-video:1.1.0")
    implementation("io.github.ismoy:imagepickerkmp-audio:1.1.0")
    implementation("io.github.ismoy:imagepickerkmp-scanner:1.1.0")
    implementation("io.github.ismoy:imagepickerkmp-videoplayer:1.1.0")
    implementation("io.github.ismoy:imagepickerkmp-audioplayer:1.1.0")
}
```
### 2. Add Camera Permission

In your `ComposeApp/iosMain/iosApp/` `Info.plist`:

```xml
<key>NSCameraUsageDescription</key>
<string>This app needs camera access to capture photos</string>
```

### 3. Add `CoreLocation.framework` (required)

ImagePickerKMP uses `CoreLocation` internally. In some Xcode / KMP configurations this framework is **not auto-linked**, which causes the following linker error at build time:

```
Could not find or use auto-linked framework '_LocationEssentials'
ld: Undefined symbols: _OBJC_CLASS_$_CLLocation
linker command failed with exit code 1
```

**To fix it, add the framework manually:**

1. Open your iOS project in **Xcode**.
2. Select your app target → **Build Phases → Link Binary With Libraries**.
3. Click **+**, search for **CoreLocation**, and click **Add**.
4. Clean (**⇧⌘K**) and rebuild.

> ✅ No code changes are required — this is a one-time Xcode project configuration.

## Basic Usage

<h1>Android && Compose Multiplatform</h1>

### Camera Capture

```kotlin
@Composable
fun CameraScreen() {
    var photoResult by remember { mutableStateOf<PhotoResult?>(null) }

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                    customConfirmationView = { result, onConfirm, onRetry ->
                        CustomAndroidConfirmationView(
                            result = result,
                            onConfirm = onConfirm,
                            onRetry = onRetry
                        )
                    },
                    customDeniedDialog = { onRetry ->
                        CustomPermissionDialog(
                            title = "Permission Required",
                            message = "We need access to the camera to take photos",
                            onRetry = onRetry
                        )
                    },
                    customSettingsDialog = { onOpenSettings ->
                        CustomPermissionSettingsDialog(
                            title = "Go to Settings",
                            message = "Camera permission is required to capture photos. Please grant it in settings",
                            onOpenSettings = onOpenSettings
                        )
                    }
                )
            )
        )
    )

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when (val result = picker.result) {
                    is ImagePickerResult.Success -> {
                        photoResult = result.photos.first()
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = 8.dp,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            AsyncImage(
                                model = photoResult?.uri,
                                contentDescription = "Captured photo",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    is ImagePickerResult.Error -> {
                        Text("Error: ${result.exception.message}", color = Color.Red)
                    }
                    is ImagePickerResult.Dismissed -> {
                        Text("No image selected", color = Color.Gray)
                    }
                    is ImagePickerResult.Loading -> {
                        CircularProgressIndicator()
                    }
                    is ImagePickerResult.Idle -> {
                        Text("No image selected", color = Color.Gray)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedButton(
                    onClick = { picker.launchCamera() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Open Camera")
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
```

### Custom UI Components

```kotlin
@Composable
fun CustomPermissionSettingsDialog(title: String, message: String, onOpenSettings: () -> Unit) {
    Dialog(onDismissRequest = { }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⚙️",
                    fontSize = 48.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = message,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Button(
                    onClick = onOpenSettings,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black, contentColor = Color.White)
                ) {
                    Text("Abrir Configuración")
                }
            }
        }
    }
}

@Composable
fun CustomPermissionDialog(
    title: String,
    message: String,
    onRetry: () -> Unit
) {
    Dialog(onDismissRequest = { }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📸",
                    fontSize = 48.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = message,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black, contentColor = Color.White)
                ) {
                    Text("Conceder Permiso")
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomIOSBottomSheet(
    onTakePhoto: () -> Unit,
    onSelectFromGallery: () -> Unit,
    onDismiss: () -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Expanded,
        skipHalfExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(bottomSheetState.currentValue) {
        if (bottomSheetState.currentValue == ModalBottomSheetValue.Hidden) {
            onDismiss()
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetElevation = 16.dp,
        sheetBackgroundColor = MaterialTheme.colors.surface,
        scrimColor = Color.Black.copy(alpha = 0.35f),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 20.dp, end = 20.dp, bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .height(5.dp)
                        .padding(bottom = 20.dp)
                        .align(Alignment.CenterHorizontally)
                        .then(
                            Modifier
                                .padding(top = 2.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .align(Alignment.Center)
                            .padding(horizontal = 12.dp)
                            .background(
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(50)
                            )
                    )
                }

                Text(
                    text = "Select image source",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.87f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Choose an option to continue",
                    fontSize = 13.sp,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                SheetAction(
                    emoji = "📷",
                    title = "Take a photo",
                    subtitle = "Open the camera",
                    tint = MaterialTheme.colors.primary,
                    onClick = {
                        coroutineScope.launch {
                            bottomSheetState.hide()
                            onTakePhoto()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SheetAction(
                    emoji = "🖼️",
                    title = "Select from gallery",
                    subtitle = "Explore images from your device",
                    tint = MaterialTheme.colors.primary,
                    onClick = {
                        coroutineScope.launch {
                            bottomSheetState.hide()
                            onSelectFromGallery()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            bottomSheetState.hide()
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        fontSize = 15.sp
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun SheetAction(
    emoji: String,
    title: String,
    subtitle: String?,
    tint: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    androidx.compose.material.Surface(
        shape = shape,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.04f),
        contentColor = MaterialTheme.colors.onSurface,
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(shape)
            .padding(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colors.onSurface
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomAndroidConfirmationView(
    result: PhotoResult,
    onConfirm: (PhotoResult) -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Review photo",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.9f),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AsyncImage(
                model = result.uri,
                contentDescription = "Captured photo preview",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { onRetry() },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Text(text = "Retry")
            }

            Button(
                onClick = { onConfirm(result) },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Confirm", color = Color.White)
            }
        }
    }
}
```

## Selecting Images from Gallery

<h1>GalleryPicker</h1>

```kotlin
@Composable
fun GalleryScreen() {
    var selectedImages by remember { mutableStateOf<List<PhotoResult>>(emptyList()) }

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(
                allowMultiple = true,
                mimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG)
            ),
            cameraCaptureConfig = CameraCaptureConfig(
                permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                    customConfirmationView = { photoResult, onConfirm, onRetry ->
                        CustomAndroidConfirmationView(
                            result = photoResult,
                            onConfirm = onConfirm,
                            onRetry = onRetry
                        )
                    }
                )
            )
        )
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { picker.launchGallery() }) {
            Text("Select from Gallery")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                selectedImages = result.photos
                selectedImages.forEach { photo ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        elevation = 10.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(bottom = 8.dp)
                    ) {
                        AsyncImage(
                            model = photo.uri,
                            contentDescription = "Selected photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}", color = Color.Red)
            }
            is ImagePickerResult.Dismissed -> {
                Text("Selection cancelled", color = Color.Gray)
            }
            is ImagePickerResult.Loading -> {
                CircularProgressIndicator()
            }
            is ImagePickerResult.Idle -> {
                Text("No images selected", color = Color.Gray)
            }
        }
    }
}
```

## Image Compression Integration

ImagePickerKMP supports optional image compression for both camera capture and gallery selection. Compression is **disabled by default** (`compressionLevel = null`) — the original file is returned untouched.

### Basic Compression Setup

#### Camera with Compression
```kotlin
@Composable
fun CompressedCameraScreen() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                compressionLevel = CompressionLevel.MEDIUM
            )
        )
    )

    Column {
        Button(onClick = { picker.launchCamera() }) {
            Text("Capture Photo (Compressed)")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                Text("Photo captured!")
                AsyncImage(
                    model = photo.uri,
                    contentDescription = "Captured photo",
                    modifier = Modifier.size(200.dp)
                )
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}", color = Color.Red)
            }
            is ImagePickerResult.Dismissed -> { /* cancelled */ }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* initial state */ }
        }
    }
}
```

#### Gallery with Compression

Gallery compression is configured via `GalleryConfig.compressionLevel`, not `CameraCaptureConfig`:

```kotlin
@Composable
fun CompressedGalleryScreen() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(
                allowMultiple = true,
                mimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG),
                compressionLevel = CompressionLevel.HIGH  // ← set here for gallery
            )
        )
    )

    Column {
        Button(onClick = { picker.launchGallery() }) {
            Text("Select from Gallery (Compressed)")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                result.photos.forEach { photo ->
                    AsyncImage(
                        model = photo.uri,
                        contentDescription = "Selected photo",
                        modifier = Modifier.size(200.dp)
                    )
                }
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}", color = Color.Red)
            }
            is ImagePickerResult.Dismissed -> { /* cancelled */ }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* initial state */ }
        }
    }
}
```

### Compression Levels

| Level | JPEG Quality | Max Dimension | Default? |
|-------|-------------|---------------|----------|
| `null` | — | original | ✅ Yes — no compression |
| `LOW` | 85% | 3840 px | — |
| `MEDIUM` | 70% | 1920 px | — |
| `HIGH` | 50% | 1280 px | — |

```kotlin
// No compression — return original (default)
CameraCaptureConfig()  // compressionLevel = null

// Low compression — near-lossless, large files
CameraCaptureConfig(compressionLevel = CompressionLevel.LOW)

// Medium compression — balanced quality/size, recommended
CameraCaptureConfig(compressionLevel = CompressionLevel.MEDIUM)

// High compression — smallest files
CameraCaptureConfig(compressionLevel = CompressionLevel.HIGH)
```

### Supported Image Formats

All common image formats are supported for compression:
- **JPEG** (image/jpeg) - Full compression support
- **PNG** (image/png) - Full compression support  
- **HEIC** (image/heic) - Full compression support
- **HEIF** (image/heif) - Full compression support
- **WebP** (image/webp) - Full compression support
- **GIF** (image/gif) - Preserved as-is (animation safe)
- **BMP** (image/bmp) - Full compression support

### Performance Considerations

- **Async Processing**: Compression runs on background threads (`Dispatchers.IO` on Android, background queue on iOS)
- **Memory Management**: Original bitmaps are automatically recycled after processing
- **Storage**: Compressed images are saved to the app cache directory
- **No compression (`null`)**: On Android, the original file URI is returned directly — no copy is made. On iOS, the original bytes are returned without re-encoding

### Real-world Example

```kotlin
@Composable
fun PhotoCaptureWithCompression() {
    var capturedPhoto by remember { mutableStateOf<PhotoResult?>(null) }

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                compressionLevel = CompressionLevel.MEDIUM
            )
        )
    )

    Column {
        Button(onClick = { picker.launchCamera() }) {
            Text("Capture Photo (Compressed)")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                capturedPhoto = result.photos.first()
                capturedPhoto?.let { photo ->
                    Text("Photo captured!")
                    Text("Size: ${photo.fileSize} bytes")
                    Text("Dimensions: ${photo.width}x${photo.height}")

                    AsyncImage(
                        model = photo.uri,
                        contentDescription = "Captured photo",
                        modifier = Modifier.size(200.dp)
                    )
                }
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}", color = Color.Red)
            }
            is ImagePickerResult.Dismissed -> { /* cancelled */ }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* initial state */ }
        }
    }
}
```
