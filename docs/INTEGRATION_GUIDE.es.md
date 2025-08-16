This document is also available in English: [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)

# Guía de Integración

Esta guía te ayudará a integrar ImagePickerKMP en tu proyecto Kotlin Multiplatform para plataformas Android e iOS y Android Nativo.

## Tabla de Contenidos

- [Prerrequisitos](#prerrequisitos)
- [Configuración en Android](#configuración-en-android)
- [Configuración en KMP](#configuración-en-kmp)

## Prerrequisitos

Antes de integrar ImagePickerKMP, asegúrate de tener:

- **Proyecto Kotlin Multiplatform** configurado
- **Android Studio** o **IntelliJ IDEA** con plugin de Kotlin
- **Xcode** (para desarrollo iOS)
- **Versiones mínimas de SDK**:
  - Android: API 21+
  - iOS: iOS 12.0+

## Configuración en Android

### 1. Añadir Dependencias

En tu `build.gradle.kts` (nivel app):

```kotlin
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.22")
}
```

### 2. Añadir Permisos
En tu `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA" /> //Opcional
<uses-feature android:name="android.hardware.camera" android:required="true" /> //Opcional
``` 
## Configuración en KMP

### 1. Añadir Dependencias

En tu `build.gradle.kts` (commonMain):

```kotlin
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.22")
}
```
### 2. Añadir Permiso de Cámara

En tu `ComposeApp/iosMain/iosApp/` `Info.plist`:

```xml
<key>NSCameraUsageDescription</key>
<string>Esta app necesita acceso a la cámara para capturar fotos</string>
```

## Personalización Específica de Plataforma

<h1>Android && Compose Multiplatform</h1>

### ImagePickerLauncher

```kotlin
    var showCameraPicker by remember { mutableStateOf(false) }
    var photoResult by remember { mutableStateOf<PhotoResult?>(null) }
    var isPickerSheetVisible by remember { mutableStateOf(false) }

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
                when {
                    showCameraPicker -> {
                        ImagePickerLauncher(
                            config = ImagePickerConfig(
                                onPhotoCaptured = { result ->
                                    photoResult = result
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
                                },
                                 cameraCaptureConfig = CameraCaptureConfig(
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
                                                title = "Permiso Requerido",
                                                message = "Necesitamos acceso a la cámara para tomar fotos",
                                                onRetry = onRetry
                                            )
                                        },
                                        customSettingsDialog = { onOpenSettings ->
                                            CustomPermissionSettingsDialog(
                                                title = "Ir a Configuración",
                                                message = "El permiso de cámara es requerido para capturar fotos. Por favor concédelo en configuración",
                                                onOpenSettings = onOpenSettings
                                            )
                                        }
                                    )
                                )
                            )
                        )
                    }

                    photoResult != null -> {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = 8.dp,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            AsyncImage(
                                model = cameraPhoto?.uri,
                                contentDescription = "Foto capturada",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    else -> {
                        Text("No hay imagen seleccionada", color = Color.Gray)
                    }
                }
            }

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
                        Text("Abrir Cámara")
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

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
                    text = "Seleccionar fuente de imagen",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.87f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Elige una opción para continuar",
                    fontSize = 13.sp,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                SheetAction(
                    emoji = "📷",
                    title = "Tomar una foto",
                    subtitle = "Abrir la cámara",
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
                    title = "Seleccionar de galería",
                    subtitle = "Explorar imágenes de tu dispositivo",
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
                        text = "Cancelar",
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
            text = "Revisar foto",
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
                contentDescription = "Vista previa de foto capturada",
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
                Text(text = "Reintentar")
            }

            Button(
                onClick = { onConfirm(result) },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Confirmar", color = Color.White)
            }
        }
    }
}
```
## Seleccionando Imágenes de la Galería
<h1>GalleryPicker</h1>

```kotlin
@Composable
fun CameraScreen() {
  var showGalleryPicker by remember { mutableStateOf(false) }

  GalleryPickerLauncher(
    onPhotosSelected = { results ->
      selectedImages = results
      showGalleryPicker = false
      results.forEach { result ->},
      onError = {
        showGalleryPicker = false
      },
      onDismiss = {
        showGalleryPicker = false
      },
      allowMultiple = true,
      mimeTypes = mutableListOf("image/jpeg", "image/png"),
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
          text = "Revisar foto",
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
            contentDescription = "Vista previa de foto capturada",
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
            Text(text = "Reintentar")
          }

          Button(
            onClick = { onConfirm(result) },
            modifier = Modifier
              .weight(1f)
              .height(52.dp),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(text = "Confirmar", color = Color.White)
          }
        }
      }
    }
```