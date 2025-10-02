This document is also available in Spanish: [CUSTOMIZATION_GUIDE.es.md](CUSTOMIZATION_GUIDE.es.md)

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
- [Corrección Automática de Orientación](#corrección-automática-de-orientación)
- [Full custom implementation](#full-custom-implementation)

## Overview

ImagePickerKMP provides extensive customization options to ensure it fits seamlessly into your app's design and user experience:

- **Custom UI Components**: Replace default dialogs and views
- **Theme Integration**: Match your app's color scheme and typography
- **Behavior Customization**: Control how the picker behaves
- **Callback Customization**: Handle events your way

## UI Customization

## Full custom implementation
```kotlin
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.domain.config.PermissionAndConfirmationConfig
import io.github.ismoy.imagepickerkmp.domain.extensions.loadBytes
import io.github.ismoy.imagepickerkmp.domain.extensions.loadPainter
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.presentation.ui.components.GalleryPickerLauncher
import io.github.ismoy.imagepickerkmp.presentation.ui.components.ImagePickerLauncher
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CameraScreen(context: Any?) {
    var showGalleryPicker by remember { mutableStateOf(false) }
    var showCameraPicker by remember { mutableStateOf(false) }
    var isPickerSheetVisible by remember { mutableStateOf(false) }
    var selectedImages by remember { mutableStateOf<List<GalleryPhotoResult>>(emptyList()) }
    var cameraPhoto by remember { mutableStateOf<PhotoResult?>(null) }
    var showScanner by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }
    var valorBase64 by remember { mutableStateOf("") }


    Scaffold (
        bottomBar = {
            if (!isPickerSheetVisible) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedButton(
                        onClick = {
                            selectedImages = emptyList()
                            cameraPhoto = null
                            showCameraPicker = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Open Camera")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            selectedImages = emptyList()
                            cameraPhoto = null
                            showGalleryPicker = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
                    ) {
                        Text("Select from Gallery", color = Color.White)
                    }
                }
            }
        }
    ){ innerPadding ->
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
                when {
                    showGalleryPicker -> {
                        GalleryPickerLauncher(
                            onPhotosSelected = { results ->
                                selectedImages = results
                                showGalleryPicker = false
                                results.forEach { result ->
                                    println("URI: Tamaño: ${result.fileSize}")
                                }
                            },
                            onError = {
                                showGalleryPicker = false
                            },
                            onDismiss = {
                                showGalleryPicker = false
                            },
                            mimeTypes = MimeType.ALL_SUPPORTED_TYPES,
                            allowMultiple = true
                        )
                    }

                    showCameraPicker -> {
                        ImagePickerLauncher(
                            config = ImagePickerConfig(
                                onPhotoCaptured = { result ->
                                    cameraPhoto = result
                                    println("DEBUG: Photo captured: ${result.uri}")
                                    val imageBytes = result.loadBytes()
                                    println("DEBUG: Image byte array: ${imageBytes.size}")
                                    showCameraPicker = false
                                    isPickerSheetVisible = false
                                },
                                onError = {
                                    showCameraPicker = false
                                    isPickerSheetVisible = false
                                },
                                onDismiss = {
                                    showCameraPicker = false
                                    isPickerSheetVisible = false
                                    println("DEBUG: Camera picker dismissed")
                                },
                                directCameraLaunch = true,
                                enableCrop = false,
                                customPickerDialog = { onTakePhoto, onSelectFromGallery, onCancel ->
                                    CustomIOSBottomSheet(
                                        onTakePhoto = onTakePhoto,
                                        onSelectFromGallery = onSelectFromGallery,
                                        onDismiss = {
                                            isPickerSheetVisible = false
                                            onCancel()
                                            showCameraPicker = false
                                        }
                                    )
                                },
                                cameraCaptureConfig = CameraCaptureConfig(
                                    compressionLevel = CompressionLevel.HIGH,
                                    permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                                        customConfirmationView = { photoResult, onConfirm, onRetry ->
                                            CustomAndroidConfirmationView(
                                                result = photoResult,
                                                onConfirm = onConfirm,
                                                onRetry = onRetry
                                            )
                                        },
                                        customDeniedDialog = { onRetry ->
                                            CustomPermissionDialog(
                                                title = "🎥 Permiso Necesario",
                                                message = "Necesitamos acceso a la cámara para tomar fotos",
                                                onRetry = onRetry
                                            )
                                        },
                                        customSettingsDialog = { onOpenSettings ->
                                            CustomPermissionSettingsDialog(
                                                title = "⚙️ Ir a Configuración",
                                                message = "Ve a Configuración > Permisos > Cámara",
                                                onOpenSettings = onOpenSettings
                                            )
                                        }
                                    )
                                )
                            )
                        )
                    }

                    selectedImages.isNotEmpty() -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            items(selectedImages) { photo ->
                                Card(
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = 6.dp,
                                ) {
                                    photo.loadPainter()?.let {
                                        Image(
                                            painter = it,
                                            contentDescription = "Selected image",
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    cameraPhoto != null -> {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = 8.dp,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            AsyncImage(
                                    model = cameraPhoto!!.uri,
                                    contentDescription = "Captured photo",
                                    modifier = Modifier.fillMaxSize()
                                )


                        }
                    }

                    else -> {
                        Text("No image selected", color = Color.Gray)
                    }
                }
            }
        }

    }
    }

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
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Black,
                            contentColor = Color.White
                        )
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
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Black,
                            contentColor = Color.White
                        )
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

## Corrección Automática de Orientación

### Problema Resuelto

Las cámaras frontales de Android capturan imágenes con orientación incorrecta, causando que las fotos aparezcan "espejadas" o rotadas. Esto es un problema común en aplicaciones móviles.

### Solución Implementada

ImagePickerKMP incluye **corrección automática de orientación** que:

- ✅ **Detecta automáticamente** si la foto fue tomada con cámara frontal
- ✅ **Aplica corrección de espejo** solo cuando es necesario
- ✅ **Mantiene la calidad** de la imagen original
- ✅ **Es transparente** para el desarrollador
- ✅ **Es eficiente** - solo procesa cuando realmente necesita corrección

### Cómo Funciona

```kotlin
// No necesitas hacer nada especial
ImagePickerLauncher(
    context = LocalContext.current,
    onPhotoCaptured = { result ->
        // La imagen ya viene corregida automáticamente
        // Tu cabeza aparecerá en la dirección correcta
    }
)
```

### Detalles Técnicos

La corrección incluye:

1. **Lectura de metadatos EXIF**: Se lee la orientación original
2. **Aplicación de rotación**: Se corrige basada en los metadatos
3. **Corrección de espejo**: Solo para cámara frontal
4. **Optimización**: Solo procesa si es necesario

### Beneficios

- **Experiencia de usuario mejorada**: Las fotos se ven naturales
- **Sin configuración adicional**: Funciona automáticamente
- **Rendimiento optimizado**: No afecta el rendimiento
- **Compatibilidad**: Funciona en todos los dispositivos Android

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
