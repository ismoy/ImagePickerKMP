# Customization Guide

This guide explains how to customize ImagePickerKMP to match your app's design and requirements.

## Table of Contents

- [Overview](#overview)
- [UI Customization](#ui-customization)
- [Permission Dialogs](#permission-dialogs)
- [Confirmation Views](#confirmation-views)
- [Themes and Styling](#themes-and-styling)
- [Advanced Configuration](#advanced-configuration)
- [Custom Callbacks](#custom-callbacks)
- [Examples](#examples)

## Overview

ImagePickerKMP provides extensive customization options to ensure it fits seamlessly into your app's design and user experience:

- **Custom UI Components**: Replace default dialogs and views
- **Theme Integration**: Match your app's color scheme and typography
- **Behavior Customization**: Control how the picker behaves
- **Callback Customization**: Handle events your way

## UI Customization

### 1. Custom Permission Dialogs

Create your own permission request dialogs:

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
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.primary
            )
        },
        text = {
            Text(
                text = description,
                style = MaterialTheme.typography.body1
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary
                )
            ) {
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

### 2. Custom Confirmation Views

Create custom photo confirmation views:

```kotlin
@Composable
fun CustomConfirmationView(
    result: PhotoResult,
    onConfirm: (PhotoResult) -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Photo preview
        AsyncImage(
            model = result.uri,
            contentDescription = "Captured photo",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        
        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { onRetry() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Red
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Retry")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
            
            Button(
                onClick = { onConfirm(result) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = "Confirm")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Use Photo")
            }
        }
    }
}
```

### 3. Custom Loading Views

Create custom loading indicators:

```kotlin
@Composable
fun CustomLoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colors.primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Preparing camera...",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}
```

## Permission Dialogs

### 1. Custom Permission Handler

```kotlin
@Composable
fun CustomPermissionHandler(config: PermissionConfig) {
    var showDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        showDialog = true
    }
    
    if (showDialog) {
        CustomPermissionDialog(
            title = config.titleDialogConfig,
            description = config.descriptionDialogConfig,
            onConfirm = {
                // Handle permission request
                showDialog = false
            },
            onDismiss = {
                showDialog = false
            }
        )
    }
}
```

### 2. Branded Permission Dialog

```kotlin
@Composable
fun BrandedPermissionDialog(
    config: PermissionConfig,
    onConfirm: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 8.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App logo
            Icon(
                imageVector = Icons.Default.Camera,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colors.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = config.titleDialogConfig,
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = config.descriptionDialogConfig,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary
                )
            ) {
                Text("Grant Permission")
            }
        }
    }
}
```

## Confirmation Views

### 1. Minimal Confirmation View

```kotlin
@Composable
fun MinimalConfirmationView(
    result: PhotoResult,
    onConfirm: (PhotoResult) -> Unit,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Photo preview
        AsyncImage(
            model = result.uri,
            contentDescription = "Captured photo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Overlay with actions
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FloatingActionButton(
                    onClick = onRetry,
                    backgroundColor = Color.Red
                ) {
                    Icon(Icons.Default.Refresh, "Retry")
                }
                
                FloatingActionButton(
                    onClick = { onConfirm(result) },
                    backgroundColor = Color.Green
                ) {
                    Icon(Icons.Default.Check, "Confirm")
                }
            }
        }
    }
}
```

### 2. Detailed Confirmation View

```kotlin
@Composable
fun DetailedConfirmationView(
    result: PhotoResult,
    onConfirm: (PhotoResult) -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header with photo info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Photo Captured",
                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = "Size: ${result.size} bytes",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "Format: ${result.format}",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
            }
        }
        
        // Photo preview
        AsyncImage(
            model = result.uri,
            contentDescription = "Captured photo",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        
        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = onRetry,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Red
                )
            ) {
                Icon(Icons.Default.Refresh, "Retry")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
            
            Button(
                onClick = { onConfirm(result) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary
                )
            ) {
                Icon(Icons.Default.Check, "Confirm")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Use Photo")
            }
        }
    }
}
```

## Themes and Styling

### 1. Custom Theme Integration

```kotlin
@Composable
fun ThemedImagePicker() {
    val customTheme = remember {
        MaterialTheme.colors.copy(
            primary = Color(0xFF6200EE),
            primaryVariant = Color(0xFF3700B3),
            secondary = Color(0xFF03DAC6),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF000000)
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

### 2. Dark Theme Support

```kotlin
@Composable
fun DarkThemeImagePicker() {
    val darkTheme = remember {
        MaterialTheme.colors.copy(
            primary = Color(0xFFBB86FC),
            primaryVariant = Color(0xFF3700B3),
            secondary = Color(0xFF03DAC6),
            surface = Color(0xFF121212),
            onSurface = Color(0xFFFFFFFF),
            background = Color(0xFF121212)
        )
    }
    
    MaterialTheme(colors = darkTheme) {
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

### 3. Custom Typography

```kotlin
@Composable
fun CustomTypographyImagePicker() {
    val customTypography = remember {
        MaterialTheme.typography.copy(
            h6 = MaterialTheme.typography.h6.copy(
                fontFamily = FontFamily.Cursive
            ),
            body1 = MaterialTheme.typography.body1.copy(
                fontFamily = FontFamily.Serif
            )
        )
    }
    
    MaterialTheme(typography = customTypography) {
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

## Advanced Configuration

### 1. Custom Photo Preferences

```kotlin
@Composable
fun CustomPhotoPreferences() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Handle photo capture
        },
        onError = { exception ->
            // Handle errors
        },
        preference = CapturePhotoPreference.HIGH_QUALITY // Custom preference
    )
}
```

### 2. Custom Error Handling

```kotlin
@Composable
fun CustomErrorHandling() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Handle photo capture
        },
        onError = { exception ->
            when (exception) {
                is CameraPermissionException -> {
                    // Handle permission errors
                    showPermissionErrorDialog()
                }
                is PhotoCaptureException -> {
                    // Handle capture errors
                    showCaptureErrorDialog()
                }
                else -> {
                    // Handle generic errors
                    showGenericErrorDialog(exception.message)
                }
            }
        }
    )
}
```

### 3. Custom Loading States

```kotlin
@Composable
fun CustomLoadingStates() {
    var isLoading by remember { mutableStateOf(false) }
    
    if (isLoading) {
        CustomLoadingView()
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
                // Handle errors
            }
        )
    }
}
```

## Custom Callbacks

### 1. Custom Permission Callbacks

```kotlin
@Composable
fun CustomPermissionCallbacks() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Handle photo capture
        },
        onError = { exception ->
            // Handle errors
        },
        customPermissionHandler = { config ->
            // Custom permission handling
            when {
                shouldShowPermissionRationale() -> {
                    showRationaleDialog(config)
                }
                isPermissionPermanentlyDenied() -> {
                    showSettingsDialog(config)
                }
                else -> {
                    requestPermission()
                }
            }
        }
    )
}
```

### 2. Custom Confirmation Callbacks

```kotlin
@Composable
fun CustomConfirmationCallbacks() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Handle photo capture
        },
        onError = { exception ->
            // Handle errors
        },
        customConfirmationView = { result, onConfirm, onRetry ->
            // Custom confirmation view
            CustomConfirmationView(
                result = result,
                onConfirm = { 
                    // Additional processing before confirming
                    processPhoto(result) { processedResult ->
                        onConfirm(processedResult)
                    }
                },
                onRetry = {
                    // Additional logic before retrying
                    resetCamera()
                    onRetry()
                }
            )
        }
    )
}
```

## Examples

### 1. Complete Custom Implementation

```kotlin
@Composable
fun CompleteCustomImagePicker() {
    var showPicker by remember { mutableStateOf(false) }
    
    if (showPicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                // Handle successful photo capture
                showPicker = false
                processAndSavePhoto(result)
            },
            onError = { exception ->
                // Handle errors
                showPicker = false
                showErrorDialog(exception)
            },
            customPermissionHandler = { config ->
                // Custom permission handling
                CustomPermissionDialog(
                    title = config.titleDialogConfig,
                    description = config.descriptionDialogConfig,
                    onConfirm = { requestPermission() },
                    onDismiss = { showPicker = false }
                )
            },
            customConfirmationView = { result, onConfirm, onRetry ->
                // Custom confirmation view
                CustomConfirmationView(
                    result = result,
                    onConfirm = { onConfirm(result) },
                    onRetry = { onRetry() }
                )
            },
            preference = CapturePhotoPreference.HIGH_QUALITY
        )
    }
    
    Button(
        onClick = { showPicker = true },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary
        )
    ) {
        Icon(Icons.Default.Camera, "Camera")
        Spacer(modifier = Modifier.width(8.dp))
        Text("Take Photo")
    }
}
```

### 2. Branded Implementation

```kotlin
@Composable
fun BrandedImagePicker() {
    val brandColors = remember {
        mapOf(
            "primary" to Color(0xFF1976D2),
            "secondary" to Color(0xFF42A5F5),
            "accent" to Color(0xFFFF5722)
        )
    }
    
    MaterialTheme(
        colors = MaterialTheme.colors.copy(
            primary = brandColors["primary"]!!,
            secondary = brandColors["secondary"]!!
        )
    ) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                // Handle photo capture with brand-specific logic
                trackBrandEvent("photo_captured")
                processPhotoWithBranding(result)
            },
            onError = { exception ->
                // Handle errors with brand-specific messaging
                showBrandedErrorDialog(exception)
            },
            customPermissionHandler = { config ->
                // Branded permission dialog
                BrandedPermissionDialog(
                    config = config,
                    onConfirm = { requestPermission() }
                )
            },
            customConfirmationView = { result, onConfirm, onRetry ->
                // Branded confirmation view
                BrandedConfirmationView(
                    result = result,
                    onConfirm = { onConfirm(result) },
                    onRetry = { onRetry() }
                )
            }
        )
    }
}
```

### 3. Minimal Implementation

```kotlin
@Composable
fun MinimalImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Simple photo handling
            savePhoto(result)
        },
        onError = { exception ->
            // Simple error handling
            showToast(exception.message)
        }
        // Use default UI components
    )
}
```

## Best Practices

### 1. Consistent Theming

```kotlin
// Use consistent theme across your app
@Composable
fun ConsistentImagePicker() {
    val appTheme = remember { getAppTheme() }
    
    MaterialTheme(colors = appTheme.colors) {
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

### 2. Accessibility

```kotlin
@Composable
fun AccessibleImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Handle photo capture
        },
        onError = { exception ->
            // Handle errors
        },
        customConfirmationView = { result, onConfirm, onRetry ->
            // Accessible confirmation view
            AccessibleConfirmationView(
                result = result,
                onConfirm = { onConfirm(result) },
                onRetry = { onRetry() }
            )
        }
    )
}
```

### 3. Performance

```kotlin
@Composable
fun PerformantImagePicker() {
    val processedResult = remember { mutableStateOf<PhotoResult?>(null) }
    
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Process photo in background
            lifecycleScope.launch(Dispatchers.IO) {
                val processed = processPhoto(result)
                processedResult.value = processed
            }
        },
        onError = { exception ->
            // Handle errors
        }
    )
    
    // Show processed result
    processedResult.value?.let { result ->
        ProcessedPhotoView(result = result)
    }
}
```

## Support

For customization-related issues:

1. **Check the examples**: Review the provided examples
2. **Theme consistency**: Ensure your customizations match your app's theme
3. **Performance**: Test customizations on different devices
4. **Accessibility**: Ensure customizations are accessible

For more information, refer to:
- [API Reference](API_REFERENCE.md)
- [Examples](EXAMPLES.md)
- [Integration Guide](INTEGRATION_GUIDE.md) 