# Examples



This document provides comprehensive examples for using ImagePickerKMP in various scenarios.

## Table of Contents

- [Image Compression Examples](#image-compression-examples)
- [Image Crop Examples](#image-crop-examples)
- [Basic Usage](#basic-usage)
- [Advanced Customization](#advanced-customization)
- [Permission Handling](#permission-handling)
- [Gallery Selection](#gallery-selection)
- [Internationalization (i18n)](#internationalization-i18n)
- [Error Handling](#error-handling)
- [Platform-Specific Examples](#platform-specific-examples)
- [ByteArray Support Examples](#bytearray-support-examples)

## Image Compression Examples

### Camera Capture with Different Compression Levels

```kotlin
@Composable
fun CameraWithCompressionLevels() {
    var compressionLevel by remember { mutableStateOf(CompressionLevel.MEDIUM) }

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                compressionLevel = compressionLevel
            )
        )
    )

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

        Button(onClick = { picker.launchCamera() }) {
            Text("Capture Photo with ${compressionLevel.name} Compression")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
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
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            is ImagePickerResult.Dismissed -> { /* User cancelled */ }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* Initial state */ }
        }
    }
}
```

### Gallery Selection with Compression

```kotlin
@Composable
fun GalleryWithCompression() {
    var compressionLevel by remember { mutableStateOf(CompressionLevel.MEDIUM) }

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                compressionLevel = compressionLevel
            )
        )
    )

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
            onClick = {
                picker.launchGallery(
                    allowMultiple = true,
                    mimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG)
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Images from Gallery")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                Text("Selected ${result.photos.size} images:")
                LazyColumn {
                    items(result.photos) { photo ->
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
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            is ImagePickerResult.Dismissed -> { /* User cancelled */ }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* Initial state */ }
        }
    }
}
```

### Compression Comparison Tool

```kotlin
@Composable
fun CompressionComparisonTool() {
    var compressedPhotos by remember { mutableStateOf<Map<CompressionLevel, PhotoResult>>(emptyMap()) }

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                compressionLevel = null // No compression for original
            )
        )
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = { picker.launchCamera() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Capture Photo for Comparison")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
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
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            is ImagePickerResult.Dismissed -> { /* User cancelled */ }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* Initial state */ }
        }
    }
}
```

## Image Crop Examples

### Simple Crop with Default Options

```kotlin
@Composable
fun SimpleCropExample() {
    var selectedImage by remember { mutableStateOf<ByteArray?>(null) }
    var showCropView by remember { mutableStateOf(false) }
    var croppedImageBytes by remember { mutableStateOf<ByteArray?>(null) }

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { picker.launchGallery(allowMultiple = false) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Image to Crop")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val imageBytes = result.photos.first().photoBytes
                selectedImage = imageBytes

                Spacer(modifier = Modifier.height(16.dp))
                Text("Original Image:")
                Image(
                    bitmap = imageBytes.toComposeImageBitmap(),
                    contentDescription = "Original image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Button(
                    onClick = { showCropView = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Crop Image")
                }
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            else -> { /* Idle, Dismissed, Loading */ }
        }

        croppedImageBytes?.let { croppedBytes ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("Cropped Image:")
            Image(
                bitmap = croppedBytes.toComposeImageBitmap(),
                contentDescription = "Cropped image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }

    if (showCropView && selectedImage != null) {
        ImageCropView(
            originalImageBytes = selectedImage!!,
            onCropComplete = { croppedBytes ->
                croppedImageBytes = croppedBytes
                showCropView = false
            },
            onDismiss = { showCropView = false }
        )
    }
}
```

### Crop with Aspect Ratio Selection

```kotlin
@Composable
fun CropWithAspectRatios() {
    var selectedImage by remember { mutableStateOf<ByteArray?>(null) }
    var showCropView by remember { mutableStateOf(false) }
    var croppedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var selectedAspectRatio by remember { mutableStateOf<AspectRatio?>(null) }

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { picker.launchGallery(allowMultiple = false) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Image to Crop")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val imageBytes = result.photos.first().photoBytes
                selectedImage = imageBytes

                Spacer(modifier = Modifier.height(16.dp))

                Text("Select Aspect Ratio:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { selectedAspectRatio = AspectRatio.SQUARE }) {
                        Text("1:1")
                    }
                    Button(onClick = { selectedAspectRatio = AspectRatio.RATIO_4_3 }) {
                        Text("4:3")
                    }
                    Button(onClick = { selectedAspectRatio = AspectRatio.RATIO_16_9 }) {
                        Text("16:9")
                    }
                    Button(onClick = { selectedAspectRatio = AspectRatio.RATIO_9_16 }) {
                        Text("9:16")
                    }
                }

                selectedAspectRatio?.let { ratio ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Selected: ${ratio.displayName}")

                    Button(
                        onClick = { showCropView = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Crop with ${ratio.displayName} Ratio")
                    }
                }
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            else -> { /* Idle, Dismissed, Loading */ }
        }

        croppedImageBytes?.let { croppedBytes ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("Cropped Image (${selectedAspectRatio?.displayName}):")
            Image(
                bitmap = croppedBytes.toComposeImageBitmap(),
                contentDescription = "Cropped image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }

    if (showCropView && selectedImage != null && selectedAspectRatio != null) {
        ImageCropView(
            originalImageBytes = selectedImage!!,
            onCropComplete = { croppedBytes ->
                croppedImageBytes = croppedBytes
                showCropView = false
            },
            onDismiss = { showCropView = false },
            initialAspectRatio = selectedAspectRatio
        )
    }
}
```

### Advanced Crop with Custom Configuration

```kotlin
@Composable
fun AdvancedCropExample() {
    var selectedImage by remember { mutableStateOf<ByteArray?>(null) }
    var showCropView by remember { mutableStateOf(false) }
    var croppedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var allowFreeform by remember { mutableStateOf(true) }
    var enableZoom by remember { mutableStateOf(true) }
    var showGrid by remember { mutableStateOf(true) }

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { picker.launchGallery(allowMultiple = false) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Image to Crop")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val imageBytes = result.photos.first().photoBytes
                selectedImage = imageBytes

                Spacer(modifier = Modifier.height(16.dp))

                Text("Crop Configuration:")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = allowFreeform,
                        onCheckedChange = { allowFreeform = it }
                    )
                    Text("Allow Freeform Crop")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = enableZoom,
                        onCheckedChange = { enableZoom = it }
                    )
                    Text("Enable Zoom")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = showGrid,
                        onCheckedChange = { showGrid = it }
                    )
                    Text("Show Grid Lines")
                }

                Button(
                    onClick = { showCropView = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open Crop View")
                }
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            else -> { /* Idle, Dismissed, Loading */ }
        }

        croppedImageBytes?.let { croppedBytes ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("Cropped Image:")
            Image(
                bitmap = croppedBytes.toComposeImageBitmap(),
                contentDescription = "Cropped image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }

    if (showCropView && selectedImage != null) {
        ImageCropView(
            originalImageBytes = selectedImage!!,
            onCropComplete = { croppedBytes ->
                croppedImageBytes = croppedBytes
                showCropView = false
            },
            onDismiss = { showCropView = false },
            allowFreeformCrop = allowFreeform,
            enableZoom = enableZoom,
            showGridLines = showGrid
        )
    }
}
```

### Direct Camera Capture with Crop

```kotlin
@Composable
fun CameraCropWorkflow() {
    var capturedPhotoBytes by remember { mutableStateOf<ByteArray?>(null) }
    var showCropView by remember { mutableStateOf(false) }
    var croppedImageBytes by remember { mutableStateOf<ByteArray?>(null) }

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { picker.launchCamera() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Capture Photo")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                capturedPhotoBytes = photo.photoBytes

                Spacer(modifier = Modifier.height(16.dp))
                Text("Captured Photo:")
                Image(
                    bitmap = photo.photoBytes.toComposeImageBitmap(),
                    contentDescription = "Captured photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Button(
                    onClick = { showCropView = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Crop Photo")
                }
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            else -> { /* Idle, Dismissed, Loading */ }
        }

        croppedImageBytes?.let { croppedBytes ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("Final Cropped Photo:")
            Image(
                bitmap = croppedBytes.toComposeImageBitmap(),
                contentDescription = "Final cropped photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }

    if (showCropView && capturedPhotoBytes != null) {
        ImageCropView(
            originalImageBytes = capturedPhotoBytes!!,
            onCropComplete = { croppedBytes ->
                croppedImageBytes = croppedBytes
                showCropView = false
            },
            onDismiss = { showCropView = false },
            initialAspectRatio = AspectRatio.SQUARE
        )
    }
}
```

## Basic Usage

### Simple Photo Capture

```kotlin
@Composable
fun SimplePhotoCapture() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchCamera() }) {
            Text("Take Photo")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                AsyncImage(
                    model = photo.uri,
                    contentDescription = "Captured photo",
                    modifier = Modifier.size(200.dp)
                )
            }
            is ImagePickerResult.Error -> {
                println("Error: ${result.exception.message}")
            }
            is ImagePickerResult.Dismissed -> {
                println("User cancelled or dismissed the picker")
            }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* Initial state */ }
        }
    }
}
```

### Custom Confirmation View

```kotlin
@Composable
fun CustomConfirmationExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
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

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchCamera() }) {
            Text("Take Photo")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                AsyncImage(
                    model = photo.uri,
                    contentDescription = "Captured photo",
                    modifier = Modifier.size(200.dp)
                )
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            is ImagePickerResult.Dismissed -> { /* User cancelled */ }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* Initial state */ }
        }
    }
}
```

## Advanced Customization

### Custom UI Colors and Icons

```kotlin
@Composable
fun CustomUIExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            uiConfig = UiConfig(
                buttonColor = Color(0xFF6200EE),
                iconColor = Color.White,
                buttonSize = 56.dp,
                flashIcon = Icons.Default.FlashOn,
                switchCameraIcon = Icons.Default.CameraRear,
                captureIcon = Icons.Default.Camera,
                galleryIcon = Icons.Default.PhotoLibrary
            )
        )
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchCamera() }) {
            Text("Take Photo")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                AsyncImage(
                    model = photo.uri,
                    contentDescription = "Captured photo",
                    modifier = Modifier.size(200.dp)
                )
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            else -> { /* Idle, Dismissed, Loading */ }
        }
    }
}
```

### Custom Callbacks

```kotlin
@Composable
fun CustomCallbacksExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCallbacks = CameraCallbacks(
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
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchCamera() }) {
            Text("Take Photo")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                AsyncImage(
                    model = photo.uri,
                    contentDescription = "Captured photo",
                    modifier = Modifier.size(200.dp)
                )
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            is ImagePickerResult.Dismissed -> {
                println("User cancelled or dismissed the picker")
            }
            else -> { /* Idle, Loading */ }
        }
    }
}
```

## Permission Handling

### Custom Permission Dialogs

```kotlin
@Composable
fun CustomPermissionExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
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

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchCamera() }) {
            Text("Take Photo")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                AsyncImage(
                    model = photo.uri,
                    contentDescription = "Captured photo",
                    modifier = Modifier.size(200.dp)
                )
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            else -> { /* Idle, Dismissed, Loading */ }
        }
    }
}

// Ejemplo con traducciones automáticas
@Composable
fun LocalizedPermissionExample() {
    val config = PermissionConfig.createLocalizedComposable()

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
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
        )
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchCamera() }) {
            Text("Take Photo")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                AsyncImage(
                    model = photo.uri,
                    contentDescription = "Captured photo",
                    modifier = Modifier.size(200.dp)
                )
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            else -> { /* Idle, Dismissed, Loading */ }
        }
    }
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

### Custom Permission Dialog Composables

```kotlin
@Composable
fun CustomPermissionDialogsExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
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

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchCamera() }) {
            Text("Take Photo with Custom Permission Dialogs")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                AsyncImage(
                    model = photo.uri,
                    contentDescription = "Captured photo",
                    modifier = Modifier.size(200.dp)
                )
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            else -> { /* Idle, Dismissed, Loading */ }
        }
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
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchGallery(allowMultiple = false) }) {
            Text("Select from Gallery")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                AsyncImage(
                    model = photo.uri,
                    contentDescription = "Selected photo",
                    modifier = Modifier.size(200.dp)
                )
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            is ImagePickerResult.Dismissed -> { /* User cancelled */ }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* Initial state */ }
        }
    }
}
```

### Multiple Image Selection

```kotlin
@Composable
fun MultipleGallerySelectionExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = {
            picker.launchGallery(
                allowMultiple = true,
                mimeTypes = listOf("image/jpeg", "image/png")
            )
        }) {
            Text("Select Multiple Images")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                Text("Selected ${result.photos.size} images")
                LazyColumn {
                    items(result.photos) { photo ->
                        AsyncImage(
                            model = photo.uri,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            else -> { /* Idle, Dismissed, Loading */ }
        }
    }
}
```

### Limited Multiple Image Selection

```kotlin
@Composable
fun LimitedGallerySelectionExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(
                allowMultiple = true,
                mimeTypes = listOf("image/jpeg", "image/png"),
                selectionLimit = 10 // Allow up to 10 images
            )
        )
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchGallery() }) {
            Text("Select Up to 10 Images")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                Text("Selected ${result.photos.size} images")
                LazyColumn {
                    items(result.photos) { photo ->
                        AsyncImage(
                            model = photo.uri,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            else -> { /* Idle, Dismissed, Loading */ }
        }
    }
}
```

### High-Performance Gallery Selection

```kotlin
@Composable
fun HighPerformanceGalleryExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(
                allowMultiple = true,
                selectionLimit = 5 // Conservative limit for better performance
            )
        )
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchGallery() }) {
            Text("Select Up to 5 Images (Optimized)")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                Text("Selected ${result.photos.size} images")
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            else -> { /* Idle, Dismissed, Loading */ }
        }
    }
}
```

- On **Android**, the user will see the system gallery picker, and permissions are requested automatically if needed.
- On **iOS**, the native gallery picker is used. On iOS 14+, multiple selection is supported. The system handles permissions and limited access natively.
- The `result.photos` always contains a list, even for single selection.
- You can use `allowMultiple` to enable or disable multi-image selection.
- The `mimeTypes` parameter is optional and lets you filter selectable file types.

## Internationalization (i18n)

### Using Localized Strings

The library now automatically uses localized strings based on the device language. All user-facing text is automatically translated:

```kotlin
@Composable
fun InternationalizationExample() {
    // The library automatically uses localized strings
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
        // No need to specify text - it's automatically localized!
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchCamera() }) {
            Text("Take Photo")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                AsyncImage(
                    model = photo.uri,
                    contentDescription = "Captured photo",
                    modifier = Modifier.size(200.dp)
                )
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            else -> { /* Idle, Dismissed, Loading */ }
        }
    }
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
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchCamera() }) {
            Text("Take Photo")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                AsyncImage(
                    model = photo.uri,
                    contentDescription = "Captured photo",
                    modifier = Modifier.size(200.dp)
                )
            }
            is ImagePickerResult.Error -> {
                when (val exception = result.exception) {
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
            is ImagePickerResult.Dismissed -> { /* User cancelled */ }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* Initial state */ }
        }
    }
}
```

### Custom Error Messages

```kotlin
@Composable
fun CustomErrorMessagesExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchCamera() }) {
            Text("Take Photo")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                AsyncImage(
                    model = photo.uri,
                    contentDescription = "Captured photo",
                    modifier = Modifier.size(200.dp)
                )
            }
            is ImagePickerResult.Error -> {
                val errorMessage = when (result.exception) {
                    is PhotoCaptureException -> getStringResource(StringResource.PHOTO_CAPTURE_ERROR)
                    is CameraPermissionException -> getStringResource(StringResource.PERMISSION_ERROR)
                    is GallerySelectionException -> getStringResource(StringResource.GALLERY_SELECTION_ERROR)
                    else -> getStringResource(StringResource.INVALID_CONTEXT_ERROR)
                }

                // Show localized error message
                Text("Error: $errorMessage", color = Color.Red)
            }
            else -> { /* Idle, Dismissed, Loading */ }
        }
    }
}
```

## Platform-Specific Examples

### Android Native (Jetpack Compose)

#### Basic Android Implementation

```kotlin
// build.gradle.kts (app level)
dependencies {
    implementation("io.github.ismoy:imagepickerkmp-photo:1.1.0")
    // ... add other modules as needed (video, audio, scanner)
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
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

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
            onClick = { picker.launchCamera() },
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

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                capturedImageUri = photo.uri
                Toast.makeText(
                    LocalContext.current,
                    "Photo captured successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            is ImagePickerResult.Error -> {
                Toast.makeText(
                    LocalContext.current,
                    "Error: ${result.exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
            is ImagePickerResult.Dismissed -> { /* User cancelled */ }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* Initial state */ }
        }
    }
}
```

#### Advanced Android Features

```kotlin
@Composable
fun AdvancedAndroidImagePicker() {
    var imageQuality by remember { mutableStateOf(CapturePhotoPreference.BALANCED) }

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                preference = imageQuality,
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
            onClick = { picker.launchCamera() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Take Photo with ${imageQuality.name} Quality")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                processImage(photo.uri)
            }
            is ImagePickerResult.Error -> {
                handleError(result.exception)
            }
            else -> { /* Idle, Dismissed, Loading */ }
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
        
        // Create and present the image picker
        // The library handles iOS-specific implementation internally
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
        
        // Create image picker with custom configuration
        // The library handles iOS-specific implementation internally
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
                implementation("io.github.ismoy:imagepickerkmp-photo:1.1.0")
                // ... add other modules as needed (video, audio, scanner)
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
package io.github.ismoy.imagepickerkmp.scanner.core.camera.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.ismoy.imagepickerkmp.ImagePickerKMPConfig
import io.github.ismoy.imagepickerkmp.ImagePickerResult
import io.github.ismoy.imagepickerkmp.rememberImagePickerKMP

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CameraScreen() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                preference = CapturePhotoPreference.QUALITY
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
                        val photo = result.photos.first()
                        AsyncImage(
                            model = photo.uri,
                            contentDescription = "Imagen capturada",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    is ImagePickerResult.Error -> {
                        Text("Error: ${result.exception.message}")
                    }
                    is ImagePickerResult.Loading -> {
                        CircularProgressIndicator()
                    }
                    else -> { /* Idle, Dismissed */ }
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

#### Android Implementation (KMP)

```kotlin
// App.kt (Android app)
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    CameraScreen()
                }
            }
        }
    }
}
```

#### iOS Implementation (KMP)

```swift
// App.swift (iOS app)
import SwiftUI
import ComposeUI

@main
struct ImagePickerApp: App {
    var body: some Scene {
        WindowGroup {
            ComposeView {
                CameraScreen()
            }
        }
    }
}

// Alternative: Using SwiftUI wrapper
struct CameraScreenWrapper: View {
    var body: some View {
        ComposeView {
            CameraScreen()
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
            CameraScreen()
        }
    }
}
```

#### Key Benefits of This Approach

1. **Single Codebase**: The same `CameraScreen` component works on both Android and iOS
2. **Platform Abstraction**: The library handles platform-specific differences internally
3. **No Context Passing**: The `rememberImagePickerKMP` composable handles platform context automatically
4. **No Platform Detection**: No need for manual platform detection in your code
5. **Clean Architecture**: Platform-specific code is isolated in the app layer, not the shared component

This example shows:
- Unified codebase for both platforms
- Automatic platform handling by the library
- Clean separation of concerns
- Simplified development workflow

For more information, see [Integration Guide](INTEGRATION_GUIDE.md) and [API Reference](API_REFERENCE.md).

### Full-Featured Example

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

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchCamera() }) {
            Text("Take Photo")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                AsyncImage(
                    model = photo.uri,
                    contentDescription = "Captured photo",
                    modifier = Modifier.size(200.dp)
                )
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            is ImagePickerResult.Dismissed -> { /* User cancelled */ }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* Initial state */ }
        }
    }
}
```

## ByteArray Support Examples

### Loading Image Data as ByteArray

The library supports loading image data as `ByteArray` for both `PhotoResult` and `GalleryPhotoResult`. This gives you control over when the full image data is loaded into memory.

#### Basic Usage

```kotlin
@Composable
fun ByteArrayExample() {
    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }

    val cameraPicker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    val galleryPicker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    Column(modifier = Modifier.padding(16.dp)) {
        // Camera capture
        Button(onClick = { cameraPicker.launchCamera() }) {
            Text("Capture Photo")
        }

        // Gallery selection
        Button(onClick = { galleryPicker.launchGallery(allowMultiple = false) }) {
            Text("Select from Gallery")
        }

        // Load bytes from camera result - Zero config!
        when (val result = cameraPicker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                Button(
                    onClick = {
                        // Just works - no setup required!
                        imageBytes = photo.loadBytes()
                    }
                ) {
                    Text("Load Camera Photo as ByteArray")
                }
            }
            else -> { /* Other states */ }
        }

        // Load bytes from gallery result - Zero config!
        when (val result = galleryPicker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                Button(
                    onClick = {
                        // Just works - no setup required!
                        imageBytes = photo.loadBytes()
                    }
                ) {
                    Text("Load Gallery Photo as ByteArray")
                }
            }
            else -> { /* Other states */ }
        }

        // Display byte array info
        imageBytes?.let { bytes ->
            Text("Image loaded: ${bytes.size} bytes")
        }
    }
}
```

#### Processing Image Data

```kotlin
@Composable
fun ImageProcessingExample() {
    var processedImage by remember { mutableStateOf<String?>(null) }

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchCamera() }) {
            Text("Capture Photo")
        }

        LaunchedEffect(picker.result) {
            val result = picker.result
            if (result is ImagePickerResult.Success) {
                val photo = result.photos.first()
                // Load image data in background thread
                withContext(Dispatchers.IO) {
                    // Zero-config loading
                    val imageBytes = photo.loadBytes()

                    if (imageBytes.isNotEmpty()) {
                        // Process the image data
                        val base64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                        processedImage = "data:image/jpeg;base64,$base64"

                        // Or save to file
                        saveImageToFile(imageBytes, "processed_image.jpg")

                        // Or upload to server
                        uploadImageToServer(imageBytes)
                    }
                }
            }
        }

        processedImage?.let {
            Text("Image processed successfully!")
        }
    }
}

private suspend fun saveImageToFile(bytes: ByteArray, fileName: String) {
    // Implementation to save bytes to file
}

private suspend fun uploadImageToServer(bytes: ByteArray) {
    // Implementation to upload bytes to server
}
```

#### Error Handling with ByteArray

```kotlin
@Composable
fun SafeByteArrayExample() {
    var loadingState by remember { mutableStateOf<LoadingState>(LoadingState.Idle) }

    sealed class LoadingState {
        object Idle : LoadingState()
        object Loading : LoadingState()
        data class Success(val bytes: ByteArray) : LoadingState()
        data class Error(val message: String) : LoadingState()
    }

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig()
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchCamera() }) {
            Text("Capture Photo")
        }

        LaunchedEffect(picker.result) {
            val result = picker.result
            if (result is ImagePickerResult.Success) {
                loadingState = LoadingState.Loading
                val photo = result.photos.first()

                try {
                    withContext(Dispatchers.IO) {
                        // Zero-config loading
                        val imageBytes = photo.loadBytes()

                        if (imageBytes.isNotEmpty()) {
                            loadingState = LoadingState.Success(imageBytes)
                        } else {
                            loadingState = LoadingState.Error("Failed to load image data")
                        }
                    }
                } catch (e: Exception) {
                    loadingState = LoadingState.Error("Error loading image: ${e.message}")
                }
            }
        }

        when (loadingState) {
            is LoadingState.Idle -> Text("No image selected")
            is LoadingState.Loading -> CircularProgressIndicator()
            is LoadingState.Success -> Text("Image loaded: ${(loadingState as LoadingState.Success).bytes.size} bytes")
            is LoadingState.Error -> Text("Error: ${(loadingState as LoadingState.Error).message}", color = Color.Red)
        }
    }
}
```

#### CommonMain Usage (Cross-platform)

```kotlin
// In your commonMain code - works on both Android and iOS
class ImageProcessor {
    fun processPhoto(photoResult: PhotoResult): ProcessedImage? {
        return try {
            // Zero-config loading works on all platforms
            val imageBytes = photoResult.loadBytes()

            if (imageBytes.isNotEmpty()) {
                // Process the raw image bytes
                ProcessedImage(
                    data = imageBytes,
                    size = imageBytes.size,
                    format = detectImageFormat(imageBytes)
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun detectImageFormat(bytes: ByteArray): String {
        return when {
            bytes.size >= 2 && bytes[0] == 0xFF.toByte() && bytes[1] == 0xD8.toByte() -> "JPEG"
            bytes.size >= 8 && bytes.sliceArray(1..3).contentEquals("PNG".toByteArray()) -> "PNG"
            else -> "UNKNOWN"
        }
    }
}

data class ProcessedImage(
    val data: ByteArray,
    val size: Int,
    val format: String
)
```

For more details about ByteArray support, see [BYTEARRAY_SUPPORT.md](BYTEARRAY_SUPPORT.md).
