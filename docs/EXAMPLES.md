# Examples

This document is also available in Spanish: [EXAMPLES.es.md](EXAMPLES.es.md)

This document provides comprehensive examples for using ImagePickerKMP in various scenarios.

## Table of Contents

- [Image Compression Examples](#image-compression-examples)
- [Basic Usage](#basic-usage)
- [Advanced Customization](#advanced-customization)
- [Permission Handling](#permission-handling)
- [Gallery Selection](#gallery-selection)
- [Internationalization (i18n)](#internationalization-i18n)
- [Error Handling](#error-handling)
- [Platform-Specific Examples](#platform-specific-examples)

## Image Compression Examples

### Camera Capture with Different Compression Levels

```kotlin
@Composable
fun CameraWithCompressionLevels() {
    var showCamera by remember { mutableStateOf(false) }
    var compressionLevel by remember { mutableStateOf(CompressionLevel.MEDIUM) }
    var capturedPhoto by remember { mutableStateOf<PhotoResult?>(null) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Select Compression Level:")
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { compressionLevel = CompressionLevel.LOW }) {
                Text("LOW (Best Quality)")
            }
            Button(onClick = { compressionLevel = CompressionLevel.MEDIUM }) {
                Text("MEDIUM")
            }
            Button(onClick = { compressionLevel = CompressionLevel.HIGH }) {
                Text("HIGH (Smallest)")
            }
        }
        
        Button(onClick = { showCamera = true }) {
            Text("Capture Photo with ${compressionLevel.name} Compression")
        }
        
        capturedPhoto?.let { photo ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Photo Captured!")
                    Text("Size: ${photo.fileSize / 1024} KB")
                    Text("Dimensions: ${photo.width}×${photo.height}")
                    Text("Compression: ${compressionLevel.name}")
                    
                    AsyncImage(
                        model = photo.uri,
                        contentDescription = "Captured photo",
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        
        if (showCamera) {
            ImagePickerLauncher(
                config = ImagePickerConfig(
                    onPhotoCaptured = { result ->
                        capturedPhoto = result
                        showCamera = false
                    },
                    onError = { showCamera = false },
                    onDismiss = { showCamera = false },
                    cameraCaptureConfig = CameraCaptureConfig(
                        compressionLevel = compressionLevel
                    )
                )
            )
        }
    }
}
```

### Gallery Selection with Compression

```kotlin
@Composable
fun GalleryWithCompression() {
    var showGallery by remember { mutableStateOf(false) }
    var selectedImages by remember { mutableStateOf<List<GalleryPhotoResult>>(emptyList()) }
    var compressionLevel by remember { mutableStateOf(CompressionLevel.MEDIUM) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Compression:")
            SegmentedButton(
                options = listOf("LOW", "MEDIUM", "HIGH"),
                selectedOption = compressionLevel.name,
                onSelectionChanged = { selected ->
                    compressionLevel = CompressionLevel.valueOf(selected)
                }
            )
        }
        
        Button(
            onClick = { showGallery = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Images from Gallery")
        }
        
        if (selectedImages.isNotEmpty()) {
            Text("Selected ${selectedImages.size} images:")
            LazyColumn {
                items(selectedImages) { photo ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = photo.uri,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = photo.fileName ?: "Unknown",
                                    style = MaterialTheme.typography.body2
                                )
                                Text(
                                    text = "${(photo.fileSize ?: 0) / 1024}KB",
                                    style = MaterialTheme.typography.caption
                                )
                                Text(
                                    text = "${photo.width}×${photo.height}",
                                    style = MaterialTheme.typography.caption
                                )
                            }
                        }
                    }
                }
            }
        }
        
        if (showGallery) {
            GalleryPickerLauncher(
                onPhotosSelected = { photos ->
                    selectedImages = photos
                    showGallery = false
                },
                onError = { showGallery = false },
                onDismiss = { showGallery = false },
                allowMultiple = true,
                mimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG),
                cameraCaptureConfig = CameraCaptureConfig(
                    compressionLevel = compressionLevel
                )
            )
        }
    }
}
```

### Compression Comparison Tool

```kotlin
@Composable
fun CompressionComparisonTool() {
    var showCamera by remember { mutableStateOf(false) }
    var originalPhoto by remember { mutableStateOf<PhotoResult?>(null) }
    var compressedPhotos by remember { mutableStateOf<Map<CompressionLevel, PhotoResult>>(emptyMap()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = { showCamera = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Capture Photo for Comparison")
        }
        
        if (compressedPhotos.isNotEmpty()) {
            Text(
                "Compression Comparison Results:",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            LazyColumn {
                items(CompressionLevel.values().toList()) { level ->
                    compressedPhotos[level]?.let { photo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "${level.name} Compression",
                                    style = MaterialTheme.typography.subtitle1
                                )
                                Text("Size: ${photo.fileSize / 1024} KB")
                                Text("Dimensions: ${photo.width}×${photo.height}")
                                
                                val qualityInfo = when (level) {
                                    CompressionLevel.LOW -> "95% quality, 2560px max"
                                    CompressionLevel.MEDIUM -> "75% quality, 1920px max"
                                    CompressionLevel.HIGH -> "50% quality, 1280px max"
                                }
                                Text("Settings: $qualityInfo")
                                
                                AsyncImage(
                                    model = photo.uri,
                                    contentDescription = "${level.name} compressed image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .padding(top = 8.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
        
        if (showCamera) {
            // Capture original photo first, then compress with all levels
            ImagePickerLauncher(
                config = ImagePickerConfig(
                    onPhotoCaptured = { result ->
                        originalPhoto = result
                        showCamera = false
                        
                        // Trigger compression with all levels
                        // (This would need additional implementation to capture
                        // the same image with different compression levels)
                    },
                    onError = { showCamera = false },
                    onDismiss = { showCamera = false },
                    cameraCaptureConfig = CameraCaptureConfig(
                        compressionLevel = null // No compression for original
                    )
                )
            )
        }
    }
}
```

## Basic Usage

### Simple Photo Capture

```kotlin
@Composable
fun SimplePhotoCapture() {
    var showImagePicker by remember { mutableStateOf(false) }
    var capturedImage by remember { mutableStateOf<PhotoResult?>(null) }

    if (showImagePicker) {
        ImagePickerLauncher(
            config = ImagePickerConfig(
                onPhotoCaptured = { result ->
                    capturedImage = result
                    showImagePicker = false
                },
                onError = { exception -> 
                    println("Error: ${exception.message}")
                    showImagePicker = false
                },
                onDismiss = { 
                    println("User cancelled or dismissed the picker")
                    showImagePicker = false // Reset state when user doesn't select anything
                }
            )
        )
    }
    Button(onClick = { showImagePicker = true }) {
        Text("Take Photo")
    }
}
```

### Custom Confirmation View

```kotlin
@Composable
fun CustomConfirmationExample() {
    var showPicker by remember { mutableStateOf(false) }

    if (showPicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            config = ImagePickerConfig(
                onPhotoCaptured = { result -> showPicker = false },
                onError = { showPicker = false },
                onDismiss = { 
                    println("User cancelled or dismissed the picker")
                    showPicker = false 
                },
                cameraCaptureConfig = CameraCaptureConfig(
                    permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                        customConfirmationView = { result, onConfirm, onRetry ->
                            ImageConfirmationViewWithCustomButtons(
                                result = result,
                                onConfirm = onConfirm,
                                onRetry = onRetry,
                                questionText = "Do you like this photo?",
                                retryText = "Retake",
                                acceptText = "Perfect"
                            )
                        }
                    )
                )
            )
        )
    }

    Button(onClick = { showPicker = true }) {
        Text("Take Photo")
    }
}
```

## Advanced Customization

### Custom UI Colors and Icons

```kotlin
@Composable
fun CustomUIExample() {
    ImagePickerLauncher(
        context = LocalContext.current,
        config = ImagePickerConfig(
            onPhotoCaptured = { result -> /* ... */ },
            onError = { exception -> /* ... */ },
            buttonColor = Color(0xFF6200EE),
            iconColor = Color.White,
            buttonSize = 56.dp,
            flashIcon = Icons.Default.FlashOn,
            switchCameraIcon = Icons.Default.CameraRear,
            captureIcon = Icons.Default.Camera,
            galleryIcon = Icons.Default.PhotoLibrary
        )
    )
}
```

### Custom Callbacks

```kotlin
@Composable
fun CustomCallbacksExample() {
    ImagePickerLauncher(
        context = LocalContext.current,
        config = ImagePickerConfig(
            onPhotoCaptured = { result -> /* ... */ },
            onError = { exception -> /* ... */ },
            onDismiss = { 
                    println("User cancelled or dismissed the picker")
                    showImagePicker = false // Reset state when user doesn't select anything
                },
            onCameraReady = {
                println("Camera is ready!")
            },
            onCameraSwitch = {
                println("Camera switched!")
            },
            onPermissionError = { exception ->
                println("Permission error: ${exception.message}")
            },
            onGalleryOpened = {
                println("Gallery opened!")
            }
        )
    )
}
```

## Permission Handling

### Custom Permission Dialogs

```kotlin
@Composable
fun CustomPermissionExample() {
    var showPicker by remember { mutableStateOf(false) }

    if (showPicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            config = ImagePickerConfig(
                onPhotoCaptured = { result -> showPicker = false },
                onError = { showPicker = false },
                cameraCaptureConfig = CameraCaptureConfig(
                    permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                        customPermissionHandler = { config ->
                            CustomPermissionDialog(
                                title = config.titleDialogConfig,
                                description = config.descriptionDialogConfig,
                                confirmationButtonText = config.btnDialogConfig,
                                onConfirm = {
                                    // Handle permission request
                                }
                            )
                        }
                    )
                )
            )
        )
    }

    Button(onClick = { showPicker = true }) {
        Text("Take Photo")
    }
}

// Ejemplo con traducciones automáticas
@Composable
fun LocalizedPermissionExample() {
    val config = PermissionConfig.createLocalizedComposable()
    
    ImagePickerLauncher(
        context = LocalContext.current,
        config = ImagePickerConfig(
            onPhotoCaptured = { result -> /* ... */ },
            onError = { exception -> /* ... */ },
            customPermissionHandler = { _ ->
                // Usar la configuración localizada
                CustomPermissionDialog(
                    title = config.titleDialogConfig,
                    description = config.descriptionDialogConfig,
                    confirmationButtonText = config.btnDialogConfig,
                    onConfirm = {
                        // Handle permission request
                    }
                )
            }
        )
    )
}

// Ejemplo de prueba del flujo de permisos
@Composable
fun TestPermissionFlow() {
    var showPermissionTest by remember { mutableStateOf(false) }
    
    if (showPermissionTest) {
        RequestCameraPermission(
            titleDialogConfig = "Camera Permission Required",
            descriptionDialogConfig = "Please enable camera access in settings",
            btnDialogConfig = "Open Settings",
            titleDialogDenied = "Permission Denied",
            descriptionDialogDenied = "Camera permission is required. Please try again.",
            btnDialogDenied = "Try Again",
            onPermissionPermanentlyDenied = {
                println("Permission permanently denied - should show settings dialog")
            },
            onResult = { granted ->
                println("Permission result: $granted")
                showPermissionTest = false
            }
        )
    }
    
    Button(onClick = { showPermissionTest = true }) {
        Text("Test Permission Flow")
    }
}
```

### Custom Permission Dialog Composables (New in v1.0.22)

```kotlin
@Composable
fun CustomPermissionDialogsExample() {
    var showPicker by remember { mutableStateOf(false) }

    if (showPicker) {
        ImagePickerLauncher(
            config = ImagePickerConfig(
                onPhotoCaptured = { result -> showPicker = false },
                onError = { showPicker = false },
                onDismiss = { showPicker = false },
                cameraCaptureConfig = CameraCaptureConfig(
                    permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                        // Custom dialog when permission is denied
                        customDeniedDialog = { onRetry ->
                            CustomRetryDialog(
                                title = "Camera Permission Needed",
                                message = "We need camera access to take photos",
                                onRetry = onRetry
                            )
                        },
                        // Custom dialog when permission is permanently denied
                        customSettingsDialog = { onOpenSettings ->
                            CustomSettingsDialog(
                                title = "Open Settings",
                                message = "Please enable camera permission in Settings",
                                onOpenSettings = onOpenSettings
                            )
                        }
                    )
                )
            )
        )
    }

    Button(onClick = { showPicker = true }) {
        Text("Take Photo with Custom Permission Dialogs")
    }
}

@Composable
fun CustomRetryDialog(
    title: String,
    message: String,
    onRetry: () -> Unit
) {
    Dialog(onDismissRequest = { }) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp)
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

@Composable
fun CustomSettingsDialog(
    title: String,
    message: String,
    onOpenSettings: () -> Unit
) {
    Dialog(onDismissRequest = { }) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open Settings")
                }
            }
        }
    }
}
```

## Gallery Selection

> **Note:** You do not need to request gallery permissions manually. The library automatically handles permission requests and user flows for both Android and iOS, providing a native experience on each platform.

### Single Image Selection

```kotlin
@Composable
fun GallerySelectionExample() {
    var showGallery by remember { mutableStateOf(false) }

    if (showGallery) {
        GalleryPickerLauncher(
            context = LocalContext.current, // Android only; ignored on iOS
            onPhotosSelected = { results -> showGallery = false },
            onError = { showGallery = false },
            allowMultiple = false,
            mimeTypes = listOf("image/*") // Optional
        )
    }

    Button(onClick = { showGallery = true }) {
        Text("Select from Gallery")
    }
}
```

### Multiple Image Selection

```kotlin
@Composable
fun MultipleGallerySelectionExample() {
    var showGallery by remember { mutableStateOf(false) }

    if (showGallery) {
        GalleryPickerLauncher(
            context = LocalContext.current, // Android only; ignored on iOS
            onPhotosSelected = { results -> showGallery = false },
            onError = { showGallery = false },
            allowMultiple = true,
            mimeTypes = listOf("image/jpeg", "image/png") // Optional
        )
    }

    Button(onClick = { showGallery = true }) {
        Text("Select Multiple Images")
    }
}
```

### Limited Multiple Image Selection

```kotlin
@Composable
fun LimitedGallerySelectionExample() {
    var showGallery by remember { mutableStateOf(false) }

    if (showGallery) {
        ImagePickerLauncher(
            context = LocalContext.current,
            config = ImagePickerConfig(
                onPhotoCaptured = { result -> showGallery = false },
                onPhotosSelected = { results -> showGallery = false },
                onError = { exception -> showGallery = false },
                cameraCaptureConfig = CameraCaptureConfig(
                    galleryConfig = GalleryConfig(
                        allowMultiple = true,
                        mimeTypes = listOf("image/jpeg", "image/png"),
                        selectionLimit = 10 // Allow up to 10 images
                    )
                )
            )
        )
    }

    Button(onClick = { showGallery = true }) {
        Text("Select Up to 10 Images")
    }
}
```

### High-Performance Gallery Selection

```kotlin
@Composable
fun HighPerformanceGalleryExample() {
    var showGallery by remember { mutableStateOf(false) }

    if (showGallery) {
        ImagePickerLauncher(
            context = LocalContext.current,
            config = ImagePickerConfig(
                onPhotoCaptured = { result -> showGallery = false },
                onPhotosSelected = { results -> showGallery = false },
                onError = { exception -> showGallery = false },
                cameraCaptureConfig = CameraCaptureConfig(
                    galleryConfig = GalleryConfig(
                        allowMultiple = true,
                        selectionLimit = 5 // Conservative limit for better performance
                    )
                )
            )
        )
    }

    Button(onClick = { showGallery = true }) {
        Text("Select Up to 5 Images (Optimized)")
    }
}
```

- On **Android**, the user will see the system gallery picker, and permissions are requested automatically if needed.
- On **iOS**, the native gallery picker is used. On iOS 14+, multiple selection is supported. The system handles permissions and limited access natively.
- The callback `onPhotosSelected` always receives a list, even for single selection.
- You can use `allowMultiple` to enable or disable multi-image selection.
- The `mimeTypes` parameter is optional and lets you filter selectable file types.

## Internationalization (i18n)

### Using Localized Strings

The library now automatically uses localized strings based on the device language. All user-facing text is automatically translated:

```kotlin
@Composable
fun InternationalizationExample() {
    // The library automatically uses localized strings
    ImagePickerLauncher(
        context = LocalContext.current,
        config = ImagePickerConfig(
            onPhotoCaptured = { result -> /* ... */ },
            onError = { exception -> /* ... */ }
            // No need to specify text - it's automatically localized!
        )
    )
}
```

### Custom Localized Strings

If you need to use localized strings in your own components:

```kotlin
@Composable
fun CustomLocalizedComponent() {
    Column {
        Text(
            text = stringResource(StringResource.IMAGE_CONFIRMATION_TITLE)
        )
        Text(
            text = stringResource(StringResource.ACCEPT_BUTTON)
        )
        Text(
            text = stringResource(StringResource.RETRY_BUTTON)
        )
    }
}
```

### Adding New Languages

To add support for a new language (e.g., French):

#### For Android
Create `res/values-fr/strings.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="camera_permission_required">Permission d\'appareil photo requise</string>
    <string name="image_confirmation_title">Êtes-vous satisfait de la photo ?</string>
    <string name="accept_button">Accepter</string>
    <string name="retry_button">Réessayer</string>
    <!-- Add all other strings... -->
</resources>
```

#### For iOS
Create `fr.lproj/Localizable.strings`:
```
"camera_permission_required" = "Permission d'appareil photo requise";
"image_confirmation_title" = "Êtes-vous satisfait de la photo ?";
"accept_button" = "Accepter";
"retry_button" = "Réessayer";
/* Add all other strings... */
```

### Available String Resources

```kotlin
// Permission strings
StringResource.CAMERA_PERMISSION_REQUIRED
StringResource.CAMERA_PERMISSION_DESCRIPTION
StringResource.OPEN_SETTINGS
StringResource.CAMERA_PERMISSION_DENIED
StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION
StringResource.GRANT_PERMISSION
StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED

// Confirmation strings
StringResource.IMAGE_CONFIRMATION_TITLE
StringResource.ACCEPT_BUTTON
StringResource.RETRY_BUTTON

// Dialog strings
StringResource.SELECT_OPTION_DIALOG_TITLE
StringResource.TAKE_PHOTO_OPTION
StringResource.SELECT_FROM_GALLERY_OPTION
StringResource.CANCEL_OPTION

// Accessibility strings
StringResource.PREVIEW_IMAGE_DESCRIPTION
StringResource.HD_QUALITY_DESCRIPTION
StringResource.SD_QUALITY_DESCRIPTION

// Error strings
StringResource.INVALID_CONTEXT_ERROR
StringResource.PHOTO_CAPTURE_ERROR
StringResource.GALLERY_SELECTION_ERROR
StringResource.PERMISSION_ERROR
```

## Error Handling

### Comprehensive Error Handling

```kotlin
@Composable
fun ErrorHandlingExample() {
    ImagePickerLauncher(
        context = LocalContext.current,
        config = ImagePickerConfig(
            onPhotoCaptured = { result -> /* ... */ },
            onError = { exception ->
                when (exception) {
                    is PhotoCaptureException -> {
                        println("Photo capture failed: ${exception.message}")
                        // Show user-friendly error message
                    }
                    is CameraPermissionException -> {
                        println("Camera permission denied: ${exception.message}")
                        // Handle permission error
                    }
                    is GallerySelectionException -> {
                        println("Gallery selection failed: ${exception.message}")
                        // Handle gallery error
                    }
                    else -> {
                        println("Unknown error: ${exception.message}")
                        // Handle generic error
                    }
                }
            }
        )
    )
}
```

### Custom Error Messages

```kotlin
@Composable
fun CustomErrorMessagesExample() {
    ImagePickerLauncher(
        context = LocalContext.current,
        config = ImagePickerConfig(
            onPhotoCaptured = { result -> /* ... */ },
            onError = { exception ->
                val errorMessage = when (exception) {
                    is PhotoCaptureException -> getStringResource(StringResource.PHOTO_CAPTURE_ERROR)
                    is CameraPermissionException -> getStringResource(StringResource.PERMISSION_ERROR)
                    is GallerySelectionException -> getStringResource(StringResource.GALLERY_SELECTION_ERROR)
                    else -> getStringResource(StringResource.INVALID_CONTEXT_ERROR)
                }
                
                // Show localized error message
                println("Error: $errorMessage")
            }
        )
    )
}
```

## Platform-Specific Examples

### Android Native (Jetpack Compose)

#### Basic Android Implementation

```kotlin
// build.gradle.kts (app level)
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.22")
    implementation("androidx.compose.ui:ui:1.4.0")
    implementation("androidx.compose.material:material:1.4.0")
    implementation("androidx.activity:activity-compose:1.7.0")
}

// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImagePickerApp()
        }
    }
}

@Composable
fun ImagePickerApp() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            ImagePickerScreen()
        }
    }
}

@Composable
fun ImagePickerScreen() {
    var showPicker by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Display captured image
        capturedImageUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Captured photo",
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Camera button
        Button(
            onClick = { showPicker = true },
            modifier = Modifier.padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Camera,
                contentDescription = "Camera",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Take Photo")
        }
        
        if (showPicker) {
            ImagePickerLauncher(
                context = LocalContext.current,
                config = ImagePickerConfig(
                    onPhotoCaptured = { result ->
                        capturedImageUri = result.uri
                        showPicker = false
                        Toast.makeText(
                            LocalContext.current,
                            "Photo captured successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onError = { exception ->
                        showPicker = false
                        Toast.makeText(
                            LocalContext.current,
                            "Error: ${exception.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            )
        }
    }
}
```

#### Advanced Android Features

```kotlin
@Composable
fun AdvancedAndroidImagePicker() {
    var showPicker by remember { mutableStateOf(false) }
    var imageQuality by remember { mutableStateOf(CapturePhotoPreference.BALANCED) }
    
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        // Quality selector
        Text("Photo Quality:", style = MaterialTheme.typography.h6)
        Row(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            RadioButton(
                selected = imageQuality == CapturePhotoPreference.FAST,
                onClick = { imageQuality = CapturePhotoPreference.FAST }
            )
            Text("Fast", modifier = Modifier.padding(start = 8.dp))
            
            RadioButton(
                selected = imageQuality == CapturePhotoPreference.BALANCED,
                onClick = { imageQuality = CapturePhotoPreference.BALANCED }
            )
            Text("Balanced", modifier = Modifier.padding(start = 8.dp))
            
            RadioButton(
                selected = imageQuality == CapturePhotoPreference.HIGH_QUALITY,
                onClick = { imageQuality = CapturePhotoPreference.HIGH_QUALITY }
            )
            Text("High Quality", modifier = Modifier.padding(start = 8.dp))
        }
        
        Button(
            onClick = { showPicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Take Photo with ${imageQuality.name} Quality")
        }
        
        if (showPicker) {
            ImagePickerLauncher(
                context = LocalContext.current,
                config = ImagePickerConfig(
                    onPhotoCaptured = { result ->
                        // Process the captured photo
                        processImage(result.uri)
                        showPicker = false
                    },
                    onError = { exception ->
                        handleError(exception)
                        showPicker = false
                    },
                    preference = imageQuality,
                    cameraCaptureConfig = CameraCaptureConfig(
                        permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                            customConfirmationView = { result, onConfirm, onRetry ->
                                CustomConfirmationDialog(
                                    result = result,
                                    onConfirm = onConfirm,
                                    onRetry = onRetry,
                                    questionText = "¿Te gusta esta foto?",
                                    retryText = "Otra vez",
                                    acceptText = "Perfecto"
                                )
                            }
                        )
                    )
                )
            )
        }
    }
}

private fun processImage(uri: Uri) {
    // Image processing logic
    println("Processing image: $uri")
}

private fun handleError(exception: Exception) {
    println("Error occurred: ${exception.message}")
}
```

### iOS Native (Swift/SwiftUI)

#### Basic iOS Implementation

```swift
// Podfile
target 'YourApp' do
  use_frameworks!
  pod 'ImagePickerKMP', :path => '../path/to/your/library'
end

// ContentView.swift
import SwiftUI
import ImagePickerKMP

struct ContentView: View {
    @State private var showImagePicker = false
    @State private var capturedImage: UIImage?
    @State private var showingAlert = false
    @State private var alertMessage = ""
    
    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                // Display captured image
                if let image = capturedImage {
                    Image(uiImage: image)
                        .resizable()
                        .scaledToFit()
                        .frame(height: 200)
                        .cornerRadius(8)
                } else {
                    RoundedRectangle(cornerRadius: 8)
                        .fill(Color.gray.opacity(0.3))
                        .frame(height: 200)
                        .overlay(
                            Image(systemName: "camera")
                                .font(.system(size: 40))
                                .foregroundColor(.gray)
                        )
                }
                
                // Camera button
                Button(action: {
                    showImagePicker = true
                }) {
                    HStack {
                        Image(systemName: "camera")
                            .font(.system(size: 20))
                        Text("Take Photo")
                            .font(.headline)
                    }
                    .foregroundColor(.white)
                    .padding()
                    .background(Color.blue)
                    .cornerRadius(10)
                }
                
                Spacer()
            }
            .padding()
            .navigationTitle("Image Picker Demo")
            .sheet(isPresented: $showImagePicker) {
                ImagePickerView(
                    onPhotoCaptured: { result in
                        // Handle successful photo capture
                        print("Photo captured: \(result.uri)")
                        showImagePicker = false
                        
                        // Load the image
                        if let url = URL(string: result.uri) {
                            loadImage(from: url)
                        }
                    },
                    onError: { error in
                        // Handle errors
                        print("Error: \(error.localizedDescription)")
                        alertMessage = error.localizedDescription
                        showingAlert = true
                        showImagePicker = false
                    }
                )
            }
            .alert("Error", isPresented: $showingAlert) {
                Button("OK") { }
            } message: {
                Text(alertMessage)
            }
        }
    }
    
    private func loadImage(from url: URL) {
        // Load image from URL
        URLSession.shared.dataTask(with: url) { data, response, error in
            if let data = data, let image = UIImage(data: data) {
                DispatchQueue.main.async {
                    self.capturedImage = image
                }
            }
        }.resume()
    }
}

// ImagePickerView.swift
import SwiftUI
import ImagePickerKMP

struct ImagePickerView: UIViewControllerRepresentable {
    let onPhotoCaptured: (PhotoResult) -> Void
    let onError: (Error) -> Void
    
    func makeUIViewController(context: Context) -> UIViewController {
        let controller = UIViewController()
        
        // Create ImagePickerKMP launcher
        let imagePicker = ImagePickerLauncher(
            context: nil, // iOS doesn't need context
            config = ImagePickerConfig(
                onPhotoCaptured: onPhotoCaptured,
                onError: onError
            )
        )
        
        // Present the image picker
        controller.present(imagePicker, animated: true)
        
        return controller
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // Update if needed
    }
}
```

#### Advanced iOS Features

```swift
// AdvancedContentView.swift
import SwiftUI
import ImagePickerKMP

struct AdvancedContentView: View {
    @State private var showImagePicker = false
    @State private var capturedImage: UIImage?
    @State private var selectedQuality: PhotoQuality = .balanced
    
    enum PhotoQuality: String, CaseIterable {
        case fast = "Fast"
        case balanced = "Balanced"
        case highQuality = "High Quality"
    }
    
    var body: some View {
        VStack(spacing: 20) {
            // Quality selector
            VStack(alignment: .leading) {
                Text("Photo Quality:")
                    .font(.headline)
                
                Picker("Quality", selection: $selectedQuality) {
                    ForEach(PhotoQuality.allCases, id: \.self) { quality in
                        Text(quality.rawValue).tag(quality)
                    }
                }
                .pickerStyle(SegmentedPickerStyle())
            }
            .padding()
            
            // Display captured image
            if let image = capturedImage {
                Image(uiImage: image)
                    .resizable()
                    .scaledToFit()
                    .frame(height: 200)
                    .cornerRadius(8)
            }
            
            // Camera button
            Button(action: {
                showImagePicker = true
            }) {
                HStack {
                    Image(systemName: "camera")
                        .font(.system(size: 20))
                    Text("Take Photo with \(selectedQuality.rawValue) Quality")
                        .font(.headline)
                }
                .foregroundColor(.white)
                .padding()
                .background(Color.blue)
                .cornerRadius(10)
            }
            
            Spacer()
        }
        .padding()
        .navigationTitle("Advanced Image Picker")
        .sheet(isPresented: $showImagePicker) {
            AdvancedImagePickerView(
                quality: selectedQuality,
                onPhotoCaptured: { result in
                    print("Photo captured with \(selectedQuality.rawValue) quality: \(result.uri)")
                    showImagePicker = false
                    loadImage(from: result.uri)
                },
                onError: { error in
                    print("Error: \(error.localizedDescription)")
                    showImagePicker = false
                }
            )
        }
    }
    
    private func loadImage(from uriString: String) {
        guard let url = URL(string: uriString) else { return }
        
        URLSession.shared.dataTask(with: url) { data, response, error in
            if let data = data, let image = UIImage(data: data) {
                DispatchQueue.main.async {
                    self.capturedImage = image
                }
            }
        }.resume()
    }
}

struct AdvancedImagePickerView: UIViewControllerRepresentable {
    let quality: PhotoQuality
    let onPhotoCaptured: (PhotoResult) -> Void
    let onError: (Error) -> Void
    
    func makeUIViewController(context: Context) -> UIViewController {
        let controller = UIViewController()
        
        // Create ImagePickerKMP launcher with custom configuration
        let imagePicker = ImagePickerLauncher(
            context: nil,
            config = ImagePickerConfig(
                onPhotoCaptured: onPhotoCaptured,
                onError: onError,
                preference = getPhotoPreference(for: quality)
            )
        )
        
        controller.present(imagePicker, animated: true)
        
        return controller
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // Update if needed
    }
    
    private func getPhotoPreference(for quality: PhotoQuality) -> CapturePhotoPreference {
        switch quality {
        case .fast:
            return .FAST
        case .balanced:
            return .BALANCED
        case .highQuality:
            return .HIGH_QUALITY
        }
    }
}
```

### Kotlin Multiplatform / Compose Multiplatform

#### Shared Module Configuration

```kotlin
// build.gradle.kts (shared module)
kotlin {
    android {
        // Android configuration
    }
    
    ios {
        binaries {
            framework {
                baseName = "Shared"
            }
        }
    }
    
    sourceSets {
        commonMain {
            dependencies {
                implementation("io.github.ismoy:imagepickerkmp:1.0.22")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")
                implementation("org.jetbrains.compose.runtime:runtime:1.4.0")
            }
        }
        
        androidMain {
            dependencies {
                implementation("androidx.compose.ui:ui:1.4.0")
                implementation("androidx.compose.material:material:1.4.0")
                implementation("androidx.activity:activity-compose:1.7.0")
            }
        }
        
        iosMain {
            dependencies {
                // iOS-specific dependencies if needed
            }
        }
    }
}

// CameraScreen.kt (shared module)
package io.github.ismoy.belzspeedscan.core.camera.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler
import io.github.ismoy.imagepickerkmp.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.ImagePickerLauncher

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CameraScreen(context: Any?) {
    var showImagePicker by remember { mutableStateOf(false) }
    var capturedImage by remember { mutableStateOf<CameraPhotoHandler.PhotoResult?>(null) }

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
                if (showImagePicker) {
                    ImagePickerLauncher(
                        context = context,
                        config = ImagePickerConfig(
                            onPhotoCaptured = { photoResult ->
                                capturedImage = photoResult
                                showImagePicker = false
                            },
                            onError = { exception ->
                                showImagePicker = false
                            },
                            preference = CapturePhotoPreference.QUALITY
                        )
                    )
                } else if (capturedImage != null) {
                    AsyncImage(
                        model = capturedImage?.uri,
                        contentDescription = "Imagen capturada",
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedButton(
                    onClick = {
                        showImagePicker = true
                    },
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

#### Android Implementation (KMP)

```kotlin
// App.kt (Android app)
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    // Pass the activity context to the shared component
                    CameraScreen(context = LocalContext.current)
                }
            }
        }
    }
}

// Alternative: Using LocalContext directly in the screen
@Composable
fun AndroidCameraScreen() {
    val context = LocalContext.current
    CameraScreen(context = context)
}
```

#### iOS Implementation (KMP)

```kotlin
// App.kt (iOS app)
import SwiftUI
import ComposeUI

@main
struct ImagePickerApp: App {
    var body: some Scene {
        WindowGroup {
            ComposeView {
                // Pass null context for iOS - the library handles it internally
                CameraScreen(context = null)
            }
        }
    }
}

// Alternative: Using SwiftUI wrapper
struct CameraScreenWrapper: View {
    var body: some View {
        ComposeView {
            CameraScreen(context = null)
        }
    }
}
```

#### Cross-Platform App Example

```kotlin
// App.kt (shared module)
@Composable
fun ImagePickerApp() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            // The same component works on both platforms
            // The library handles platform differences internally
            CameraScreen(context = null) // Context will be provided by the platform-specific app
        }
    }
}
```

#### Key Benefits of This Approach

1. **Single Codebase**: The same `CameraScreen` component works on both Android and iOS
2. **Platform Abstraction**: The library handles platform-specific differences internally
3. **Context Handling**: 
   - Android: Pass `LocalContext.current` or `context` parameter
   - iOS: Pass `null` - the library handles it automatically
4. **No Platform Detection**: No need for manual platform detection in your code
5. **Clean Architecture**: Platform-specific code is isolated in the app layer, not the shared component

This example shows:
- Unified codebase for both platforms
- Automatic platform handling by the library
- Clean separation of concerns
- Simplified development workflow

For more information, see [Integration Guide](INTEGRATION_GUIDE.md) and [API Reference](API_REFERENCE.md). 

```kotlin
@Composable
fun CustomImagePicker() {
    var showPicker by remember { mutableStateOf(false) }
    if (showPicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            config = ImagePickerConfig(
                onPhotoCaptured = { result -> showPicker = false },
                onError = { exception -> showPicker = false },
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
    }
    Button(onClick = { showPicker = true }) {
        Text("Take Photo")
    }
}
```