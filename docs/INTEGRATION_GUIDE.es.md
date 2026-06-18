This document is also available in English: [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)

# Guía de Integración

Esta guía te ayudará a integrar ImagePickerKMP en tu proyecto Kotlin Multiplatform para plataformas Android e iOS y Android Nativo.

## Tabla de Contenidos

- [Prerrequisitos](#prerrequisitos)
- [Configuración en Android](#configuración-en-android)
- [Configuración en KMP](#configuración-en-kmp)
- [Uso Básico](#uso-básico)
- [Captura de Cámara](#captura-de-cámara)
- [Selección de Galería](#selección-de-galería)
- [Configuración Avanzada](#configuración-avanzada)

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

### 3. Añadir `CoreLocation.framework` (obligatorio)

ImagePickerKMP usa `CoreLocation` internamente. En algunas configuraciones de Xcode / KMP este framework **no se enlaza automáticamente**, lo que provoca el siguiente error de enlazador en tiempo de compilación:

```
Could not find or use auto-linked framework '_LocationEssentials'
ld: Undefined symbols: _OBJC_CLASS_$_CLLocation
linker command failed with exit code 1
```

**Para solucionarlo, añade el framework manualmente:**

1. Abre tu proyecto iOS en **Xcode**.
2. Selecciona el target de tu app → **Build Phases → Link Binary With Libraries**.
3. Haz clic en **+**, busca **CoreLocation** y haz clic en **Add**.
4. Limpia (**⇧⌘K**) y vuelve a compilar.

> ✅ No se requieren cambios en el código — es una configuración única a nivel de proyecto Xcode.

## Uso Básico

### Inicialización del Picker

```kotlin
@Composable
fun MyScreen() {
    val picker = rememberImagePickerKMP()

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            // Fotos disponibles
            val photos = result.photos
        }
        is ImagePickerResult.Error -> {
            // Manejar error
            val exception = result.exception
        }
        is ImagePickerResult.Dismissed -> {
            // El usuario canceló
        }
        is ImagePickerResult.Loading -> {
            // Cargando...
        }
        is ImagePickerResult.Idle -> {
            // Estado inicial
        }
    }
}
```

## Captura de Cámara

### Ejemplo Básico

```kotlin
@Composable
fun CameraScreen() {
    val picker = rememberImagePickerKMP()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    AsyncImage(
                        model = photo.uri,
                        contentDescription = "Foto capturada",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}", color = Color.Red)
            }
            is ImagePickerResult.Dismissed -> {
                Text("Captura cancelada", color = Color.Gray)
            }
            is ImagePickerResult.Loading -> {
                CircularProgressIndicator()
            }
            is ImagePickerResult.Idle -> {
                Text("No hay imagen seleccionada", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { picker.launchCamera() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("Abrir Cámara")
        }
    }
}
```

### Cámara con Configuración Personalizada

```kotlin
@Composable
fun CameraWithConfigScreen() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
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

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    AsyncImage(
                        model = photo.uri,
                        contentDescription = "Foto capturada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}", color = Color.Red)
            }
            is ImagePickerResult.Dismissed -> {
                Text("Captura cancelada", color = Color.Gray)
            }
            is ImagePickerResult.Loading -> {
                CircularProgressIndicator()
            }
            is ImagePickerResult.Idle -> {
                Text("No hay imagen seleccionada", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { picker.launchCamera() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("Abrir Cámara")
        }
    }
}
```

## Selección de Galería

### Selección Simple (una imagen)

```kotlin
@Composable
fun GalleryScreen() {
    val picker = rememberImagePickerKMP()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                AsyncImage(
                    model = photo.uri,
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}", color = Color.Red)
            }
            is ImagePickerResult.Dismissed -> {
                Text("Selección cancelada", color = Color.Gray)
            }
            is ImagePickerResult.Loading -> {
                CircularProgressIndicator()
            }
            is ImagePickerResult.Idle -> {
                Text("No hay imagen seleccionada", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { picker.launchGallery() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("Seleccionar de Galería")
        }
    }
}
```

### Selección Múltiple

```kotlin
@Composable
fun MultiGalleryScreen() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(
                allowMultiple = true,
                mimeTypes = listOf("image/jpeg", "image/png")
            )
        )
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(result.photos) { photo ->
                        AsyncImage(
                            model = photo.uri,
                            contentDescription = "Imagen seleccionada",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(4.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}", color = Color.Red)
            }
            is ImagePickerResult.Dismissed -> {
                Text("Selección cancelada", color = Color.Gray)
            }
            is ImagePickerResult.Loading -> {
                CircularProgressIndicator()
            }
            is ImagePickerResult.Idle -> {
                Text("No hay imágenes seleccionadas", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { picker.launchGallery(allowMultiple = true) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("Seleccionar Múltiples Imágenes")
        }
    }
}
```

## Configuración Avanzada

### Configuración Completa con Recorte

```kotlin
@Composable
fun FullConfigScreen() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
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
            ),
            galleryConfig = GalleryConfig(
                allowMultiple = true,
                mimeTypes = listOf("image/jpeg", "image/png")
            ),
            cropConfig = CropConfig(enabled = true)
        )
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                result.photos.forEach { photo ->
                    AsyncImage(
                        model = photo.uri,
                        contentDescription = "Imagen",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(4.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}", color = Color.Red)
            }
            is ImagePickerResult.Dismissed -> {
                Text("Operación cancelada", color = Color.Gray)
            }
            is ImagePickerResult.Loading -> {
                CircularProgressIndicator()
            }
            is ImagePickerResult.Idle -> {
                Text("No hay imágenes", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { picker.launchCamera() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cámara")
            }

            OutlinedButton(
                onClick = { picker.launchGallery() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Galería")
            }
        }
    }
}
```

### Componentes de UI Personalizados

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
