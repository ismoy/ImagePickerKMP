This document is also available in English: [EXAMPLES.md](EXAMPLES.md)

# Ejemplos de uso - ImagePickerKMP

// TODO: Traducir el contenido detallado

Consulta la versión en inglés para más ejemplos y casos de uso. 

# Ejemplos

Este documento proporciona ejemplos completos para usar ImagePickerKMP en varios escenarios.

## Tabla de Contenidos

- [Ejemplos de Compresión de Imágenes](#ejemplos-de-compresión-de-imágenes)
- [Ejemplos de Recorte de Imágenes](#ejemplos-de-recorte-de-imágenes)
- [Uso Básico](#uso-básico)
- [Personalización Avanzada](#personalización-avanzada)
- [Manejo de Permisos](#manejo-de-permisos)
- [Selección de Galería](#selección-de-galería)
- [Internacionalización (i18n)](#internacionalización-i18n)
- [Manejo de Errores](#manejo-de-errores)
- [Ejemplos Específicos de Plataforma](#ejemplos-específicos-de-plataforma)

## Ejemplos de Compresión de Imágenes

### Captura de Cámara con Diferentes Niveles de Compresión

```kotlin
@Composable
fun CamaraConNivelesCompresion() {
    var mostrarCamara by remember { mutableStateOf(false) }
    var nivelCompresion by remember { mutableStateOf(CompressionLevel.MEDIUM) }
    var fotoCapturada by remember { mutableStateOf<PhotoResult?>(null) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Selecciona Nivel de Compresión:")
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { nivelCompresion = CompressionLevel.LOW }) {
                Text("BAJA (Mejor Calidad)")
            }
            Button(onClick = { nivelCompresion = CompressionLevel.MEDIUM }) {
                Text("MEDIA")
            }
            Button(onClick = { nivelCompresion = CompressionLevel.HIGH }) {
                Text("ALTA (Menor Tamaño)")
            }
        }
        
        Button(onClick = { mostrarCamara = true }) {
            Text("Capturar Foto con Compresión ${nivelCompresion.name}")
        }
        
        fotoCapturada?.let { foto ->
            val fileSizeKB = (foto.fileSize ?: 0) / 1024.0
            Text("Foto capturada - Tamaño: ${String.format("%.2f", fileSizeKB)}KB (${foto.fileSize} bytes)")
            Image(
                bitmap = foto.photoBytes.toComposeImageBitmap(),
                contentDescription = "Foto capturada",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }

    if (mostrarCamara) {
        CameraCaptureView(
            onPhotoTaken = { foto ->
                fotoCapturada = foto
                mostrarCamara = false
            },
            onError = { mostrarCamara = false },
            onDismiss = { mostrarCamara = false },
            cameraCaptureConfig = CameraCaptureConfig(
                compressionLevel = nivelCompresion
            )
        )
    }
}
```

## Ejemplos de Recorte de Imágenes

### Recorte Simple con Opciones Predeterminadas

```kotlin
@Composable
fun EjemploRecorteSimple() {
    var mostrarSelectorImagen by remember { mutableStateOf(false) }
    var imagenSeleccionada by remember { mutableStateOf<ByteArray?>(null) }
    var mostrarVistaRecorte by remember { mutableStateOf(false) }
    var bytesImagenRecortada by remember { mutableStateOf<ByteArray?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { mostrarSelectorImagen = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar Imagen para Recortar")
        }

        imagenSeleccionada?.let { bytesImagen ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("Imagen Original:")
            Image(
                bitmap = bytesImagen.toComposeImageBitmap(),
                contentDescription = "Imagen original",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Button(
                onClick = { mostrarVistaRecorte = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Recortar Imagen")
            }
        }

        bytesImagenRecortada?.let { bytesRecortados ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("Imagen Recortada:")
            Image(
                bitmap = bytesRecortados.toComposeImageBitmap(),
                contentDescription = "Imagen recortada",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }

    if (mostrarSelectorImagen) {
        GalleryPickerLauncher(
            onPhotosSelected = { fotos ->
                fotos.firstOrNull()?.let { foto ->
                    imagenSeleccionada = foto.photoBytes
                }
                mostrarSelectorImagen = false
            },
            onError = { mostrarSelectorImagen = false },
            onDismiss = { mostrarSelectorImagen = false },
            allowMultiple = false
        )
    }

    if (mostrarVistaRecorte && imagenSeleccionada != null) {
        ImageCropView(
            originalImageBytes = imagenSeleccionada!!,
            onCropComplete = { bytesRecortados ->
                bytesImagenRecortada = bytesRecortados
                mostrarVistaRecorte = false
            },
            onDismiss = { mostrarVistaRecorte = false }
        )
    }
}
```

### Recorte con Selección de Relación de Aspecto

```kotlin
@Composable
fun RecorteConRelacionesAspecto() {
    var mostrarSelectorImagen by remember { mutableStateOf(false) }
    var imagenSeleccionada by remember { mutableStateOf<ByteArray?>(null) }
    var mostrarVistaRecorte by remember { mutableStateOf(false) }
    var bytesImagenRecortada by remember { mutableStateOf<ByteArray?>(null) }
    var relacionAspectoSeleccionada by remember { mutableStateOf<AspectRatio?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { mostrarSelectorImagen = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar Imagen para Recortar")
        }

        imagenSeleccionada?.let { bytesImagen ->
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Selecciona Relación de Aspecto:")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { relacionAspectoSeleccionada = AspectRatio.SQUARE }
                ) {
                    Text("1:1")
                }
                Button(
                    onClick = { relacionAspectoSeleccionada = AspectRatio.RATIO_4_3 }
                ) {
                    Text("4:3")
                }
                Button(
                    onClick = { relacionAspectoSeleccionada = AspectRatio.RATIO_16_9 }
                ) {
                    Text("16:9")
                }
                Button(
                    onClick = { relacionAspectoSeleccionada = AspectRatio.RATIO_9_16 }
                ) {
                    Text("9:16")
                }
            }
            
            relacionAspectoSeleccionada?.let { relacion ->
                Spacer(modifier = Modifier.height(8.dp))
                Text("Seleccionado: ${relacion.displayName}")
                
                Button(
                    onClick = { mostrarVistaRecorte = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Recortar con Relación ${relacion.displayName}")
                }
            }
        }

        bytesImagenRecortada?.let { bytesRecortados ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("Imagen Recortada (${relacionAspectoSeleccionada?.displayName}):")
            Image(
                bitmap = bytesRecortados.toComposeImageBitmap(),
                contentDescription = "Imagen recortada",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }

    if (mostrarSelectorImagen) {
        GalleryPickerLauncher(
            onPhotosSelected = { fotos ->
                fotos.firstOrNull()?.let { foto ->
                    imagenSeleccionada = foto.photoBytes
                }
                mostrarSelectorImagen = false
            },
            onError = { mostrarSelectorImagen = false },
            onDismiss = { mostrarSelectorImagen = false },
            allowMultiple = false
        )
    }

    if (mostrarVistaRecorte && imagenSeleccionada != null && relacionAspectoSeleccionada != null) {
        ImageCropView(
            originalImageBytes = imagenSeleccionada!!,
            onCropComplete = { bytesRecortados ->
                bytesImagenRecortada = bytesRecortados
                mostrarVistaRecorte = false
            },
            onDismiss = { mostrarVistaRecorte = false },
            initialAspectRatio = relacionAspectoSeleccionada
        )
    }
}
```

### Flujo de Trabajo Cámara con Recorte

```kotlin
@Composable
fun FlujoCamaraRecorte() {
    var mostrarCamara by remember { mutableStateOf(false) }
    var fotoCapturada by remember { mutableStateOf<PhotoResult?>(null) }
    var mostrarVistaRecorte by remember { mutableStateOf(false) }
    var bytesImagenRecortada by remember { mutableStateOf<ByteArray?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { mostrarCamara = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Capturar Foto")
        }

        fotoCapturada?.let { foto ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("Foto Capturada:")
            Image(
                bitmap = foto.photoBytes.toComposeImageBitmap(),
                contentDescription = "Foto capturada",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Button(
                onClick = { mostrarVistaRecorte = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Recortar Foto")
            }
        }

        bytesImagenRecortada?.let { bytesRecortados ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("Foto Final Recortada:")
            Image(
                bitmap = bytesRecortados.toComposeImageBitmap(),
                contentDescription = "Foto final recortada",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }

    if (mostrarCamara) {
        CameraCaptureView(
            onPhotoTaken = { foto ->
                fotoCapturada = foto
                mostrarCamara = false
            },
            onError = { mostrarCamara = false },
            onDismiss = { mostrarCamara = false }
        )
    }

    if (mostrarVistaRecorte && fotoCapturada != null) {
        ImageCropView(
            originalImageBytes = fotoCapturada!!.photoBytes,
            onCropComplete = { bytesRecortados ->
                bytesImagenRecortada = bytesRecortados
                mostrarVistaRecorte = false
            },
            onDismiss = { mostrarVistaRecorte = false },
            initialAspectRatio = AspectRatio.SQUARE
        )
    }
}
```

## Ejemplos Básicos

### Selector de Imagen Simple

```kotlin
@Composable
fun SelectorImagenSimple() {
    var mostrarSelector by remember { mutableStateOf(false) }
    if (mostrarSelector) {
        ImagePickerLauncher(
            config = ImagePickerConfig(
                onPhotoCaptured = { resultado -> 
                    println("Foto capturada: ${resultado.uri}")
                    mostrarSelector = false
                },
                onError = { excepcion -> 
                    println("Error: ${excepcion.message}")
                    mostrarSelector = false
                },
                onDismiss = { 
                    println("Usuario canceló o cerró el selector")
                    mostrarSelector = false // Resetear estado cuando el usuario no selecciona nada
                }
            )
        )
    }
    Button(onClick = { mostrarSelector = true }) {
        Text("Tomar Foto")
    }
}
```

### Selector de Galería

```kotlin
@Composable
fun SelectorGaleria() {
    var mostrarGaleria by remember { mutableStateOf(false) }
    if (mostrarGaleria) {
        GalleryPickerLauncher(
            onPhotosSelected = { resultados -> 
                println("Seleccionadas ${resultados.size} imágenes")
                mostrarGaleria = false
            },
            onError = { excepcion -> 
                println("Error: ${excepcion.message}")
                mostrarGaleria = false
            },
            onDismiss = { 
                println("Usuario canceló la selección de galería")
                mostrarGaleria = false // Resetear estado cuando el usuario no selecciona nada
            },
            allowMultiple = true
        )
    }
    Button(onClick = { mostrarGaleria = true }) {
        Text("Seleccionar de la Galería")
    }
}
```

## Personalización Avanzada

### Colores e Iconos de UI Personalizados

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

### Callbacks Personalizados

```kotlin
@Composable
fun CustomCallbacksExample() {
    ImagePickerLauncher(
        context = LocalContext.current,
        config = ImagePickerConfig(
            onPhotoCaptured = { result -> /* ... */ },
            onError = { exception -> /* ... */ },
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

## Manejo de Permisos

### Diálogos de Permiso Personalizados

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
                                    // Manejar solicitud de permiso
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
```

// Ejemplo con traducciones automáticas
```kotlin
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
                        // Manejar solicitud de permiso
                    }
                )
            }
        )
    )
}
```

// Ejemplo de prueba del flujo de permisos
```kotlin
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

### Diálogos de Permisos Composables Personalizados (Nuevo en v1.0.22)

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
                        // Diálogo personalizado cuando se deniega el permiso
                        customDeniedDialog = { onRetry ->
                            CustomRetryDialog(
                                title = "Permiso de Cámara Necesario",
                                message = "Necesitamos acceso a la cámara para tomar fotos",
                                onRetry = onRetry
                            )
                        },
                        // Diálogo personalizado cuando el permiso es denegado permanentemente
                        customSettingsDialog = { onOpenSettings ->
                            CustomSettingsDialog(
                                title = "Abrir Configuración",
                                message = "Por favor habilita el permiso de cámara en Configuración",
                                onOpenSettings = onOpenSettings
                            )
                        }
                    )
                )
            )
        )
    }

    Button(onClick = { showPicker = true }) {
        Text("Tomar Foto con Diálogos de Permisos Personalizados")
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
                    Text("Conceder Permiso")
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
                    Text("Abrir Configuración")
                }
            }
        }
    }
}
```

## Selección de Galería

> **Nota:** No necesitas solicitar permisos de galería manualmente. La librería gestiona automáticamente la solicitud de permisos y el flujo de usuario tanto en Android como en iOS, proporcionando una experiencia nativa en cada plataforma.

### Selección de Imagen Única

```kotlin
@Composable
fun GallerySelectionExample() {
    var showGallery by remember { mutableStateOf(false) }

    if (showGallery) {
        GalleryPickerLauncher(
            context = LocalContext.current, // Solo Android; ignorado en iOS
            onPhotosSelected = { results -> showGallery = false },
            onError = { showGallery = false },
            allowMultiple = false,
            mimeTypes = listOf("image/*") // Opcional
        )
    }

    Button(onClick = { showGallery = true }) {
        Text("Seleccionar de la galería")
    }
}
```

### Selección Múltiple de Imágenes

```kotlin
@Composable
fun MultipleGallerySelectionExample() {
    var showGallery by remember { mutableStateOf(false) }

    if (showGallery) {
        GalleryPickerLauncher(
            context = LocalContext.current, // Solo Android; ignorado en iOS
            onPhotosSelected = { results -> showGallery = false },
            onError = { showGallery = false },
            allowMultiple = true,
            mimeTypes = listOf("image/jpeg", "image/png") // Opcional
        )
    }

    Button(onClick = { showGallery = true }) {
        Text("Seleccionar múltiples imágenes")
    }
}
```

### Selección Múltiple Limitada

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
                        selectionLimit = 10 // Permitir hasta 10 imágenes
                    )
                )
            )
        )
    }

    Button(onClick = { showGallery = true }) {
        Text("Seleccionar hasta 10 imágenes")
    }
}
```

### Selección de Galería de Alto Rendimiento

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
                        selectionLimit = 5 // Límite conservador para mejor rendimiento
                    )
                )
            )
        )
    }

    Button(onClick = { showGallery = true }) {
        Text("Seleccionar hasta 5 imágenes (Optimizado)")
    }
}
```

- En **Android**, el usuario verá el selector de galería del sistema y los permisos se solicitan automáticamente si es necesario.
- En **iOS**, se usa el selector nativo de galería. En iOS 14+ se soporta selección múltiple. El sistema gestiona permisos y acceso limitado de forma nativa.
- El callback `onPhotosSelected` siempre recibe una lista, incluso para selección simple.
- Puedes usar `allowMultiple` para habilitar o deshabilitar la selección múltiple de imágenes.
- El parámetro `mimeTypes` es opcional y permite filtrar los tipos de archivos seleccionables.

## Internacionalización (i18n)

### Uso de Strings Localizados

La librería ahora usa automáticamente strings localizados según el idioma del dispositivo. Todo el texto visible para el usuario se traduce automáticamente:

```kotlin
@Composable
fun InternationalizationExample() {
    // La librería usa automáticamente strings localizados
    ImagePickerLauncher(
        context = LocalContext.current,
        config = ImagePickerConfig(
            onPhotoCaptured = { result -> /* ... */ },
            onError = { exception -> /* ... */ }
            // ¡No necesitas especificar texto, se localiza automáticamente!
        )
    )
}
```

### Strings Localizados Personalizados

Si necesitas usar strings localizados en tus propios componentes:

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

### Añadir Nuevos Idiomas

Para añadir soporte para un nuevo idioma (por ejemplo, francés):

#### Para Android
Crea `res/values-fr/strings.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="camera_permission_required">Permission d'appareil photo requise</string>
    <string name="image_confirmation_title">Êtes-vous satisfait de la photo ?</string>
    <string name="accept_button">Accepter</string>
    <string name="retry_button">Réessayer</string>
    <!-- Agrega el resto de los strings... -->
</resources>
```

#### Para iOS
Crea `fr.lproj/Localizable.strings`:
```
"camera_permission_required" = "Permission d'appareil photo requise";
"image_confirmation_title" = "Êtes-vous satisfait de la photo ?";
"accept_button" = "Accepter";
"retry_button" = "Réessayer";
/* Agrega el resto de los strings... */
```

### Recursos de String Disponibles

```kotlin
// Strings de permisos
StringResource.CAMERA_PERMISSION_REQUIRED
StringResource.CAMERA_PERMISSION_DESCRIPTION
StringResource.OPEN_SETTINGS
StringResource.CAMERA_PERMISSION_DENIED
StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION
StringResource.GRANT_PERMISSION
StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED

// Strings de confirmación
StringResource.IMAGE_CONFIRMATION_TITLE
StringResource.ACCEPT_BUTTON
StringResource.RETRY_BUTTON

// Strings de diálogos
StringResource.SELECT_OPTION_DIALOG_TITLE
StringResource.TAKE_PHOTO_OPTION
StringResource.SELECT_FROM_GALLERY_OPTION
StringResource.CANCEL_OPTION

// Strings de accesibilidad
StringResource.PREVIEW_IMAGE_DESCRIPTION
StringResource.HD_QUALITY_DESCRIPTION
StringResource.SD_QUALITY_DESCRIPTION

// Strings de error
StringResource.INVALID_CONTEXT_ERROR
StringResource.PHOTO_CAPTURE_ERROR
StringResource.GALLERY_SELECTION_ERROR
StringResource.PERMISSION_ERROR
```

## Manejo de Errores

### Manejo de Errores Completo

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
                        // Mostrar mensaje de error amigable
                    }
                    is CameraPermissionException -> {
                        println("Camera permission denied: ${exception.message}")
                        // Manejar error de permisos
                    }
                    is GallerySelectionException -> {
                        println("Gallery selection failed: ${exception.message}")
                        // Manejar error de galería
                    }
                    else -> {
                        println("Unknown error: ${exception.message}")
                        // Manejar error genérico
                    }
                }
            }
        )
    )
}
```

### Mensajes de Error Personalizados

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
                
                // Mostrar mensaje de error localizado
                println("Error: $errorMessage")
            }
        )
    )
}
```

## Ejemplos Específicos de Plataforma

### Android Nativo (Jetpack Compose)

#### Implementación Básica de Android

```kotlin
// build.gradle.kts (nivel de app)
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
        // Mostrar imagen capturada
        capturedImageUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Foto capturada",
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Botón de cámara
        Button(
            onClick = { showPicker = true },
            modifier = Modifier.padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Camera,
                contentDescription = "Cámara",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Tomar Foto")
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
                            "¡Foto capturada exitosamente!",
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

#### Funciones Avanzadas de Android

```kotlin
@Composable
fun AdvancedAndroidImagePicker() {
    var showPicker by remember { mutableStateOf(false) }
    var imageQuality by remember { mutableStateOf(CapturePhotoPreference.BALANCED) }
    
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        // Selector de calidad
        Text("Calidad de Foto:", style = MaterialTheme.typography.h6)
        Row(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            RadioButton(
                selected = imageQuality == CapturePhotoPreference.FAST,
                onClick = { imageQuality = CapturePhotoPreference.FAST }
            )
            Text("Rápida", modifier = Modifier.padding(start = 8.dp))
            
            RadioButton(
                selected = imageQuality == CapturePhotoPreference.BALANCED,
                onClick = { imageQuality = CapturePhotoPreference.BALANCED }
            )
            Text("Equilibrada", modifier = Modifier.padding(start = 8.dp))
            
            RadioButton(
                selected = imageQuality == CapturePhotoPreference.HIGH_QUALITY,
                onClick = { imageQuality = CapturePhotoPreference.HIGH_QUALITY }
            )
            Text("Alta Calidad", modifier = Modifier.padding(start = 8.dp))
        }
        
        Button(
            onClick = { showPicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tomar Foto con Calidad ${imageQuality.name}")
        }
        
        if (showPicker) {
            ImagePickerLauncher(
                context = LocalContext.current,
                config = ImagePickerConfig(
                    onPhotoCaptured = { result ->
                        // Procesar la foto capturada
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
    // Lógica de procesamiento de imagen
    println("Procesando imagen: $uri")
}

private fun handleError(exception: Exception) {
    println("Error ocurrido: ${exception.message}")
}
```

### iOS Nativo (Swift/SwiftUI)

#### Implementación Básica de iOS

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
                // Mostrar imagen capturada
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
                
                // Botón de cámara
                Button(action: {
                    showImagePicker = true
                }) {
                    HStack {
                        Image(systemName: "camera")
                            .font(.system(size: 20))
                        Text("Tomar Foto")
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
            .navigationTitle("Demo Image Picker")
            .sheet(isPresented: $showImagePicker) {
                ImagePickerView(
                    onPhotoCaptured: { result in
                        // Manejar captura exitosa
                        print("Foto capturada: \(result.uri)")
                        showImagePicker = false
                        
                        // Cargar la imagen
                        if let url = URL(string: result.uri) {
                            loadImage(from: url)
                        }
                    },
                    onError: { error in
                        // Manejar errores
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
        // Cargar imagen desde URL
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
        
        // Crear ImagePickerKMP launcher
        let imagePicker = ImagePickerLauncher(
            context: nil, // iOS no necesita context
            config = ImagePickerConfig(
                onPhotoCaptured: onPhotoCaptured,
                onError: onError
            )
        )
        
        // Presentar el image picker
        controller.present(imagePicker, animated: true)
        
        return controller
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // Actualizar si es necesario
    }
}
```

#### Funciones Avanzadas de iOS

```swift
// AdvancedContentView.swift
import SwiftUI
import ImagePickerKMP

struct AdvancedContentView: View {
    @State private var showImagePicker = false
    @State private var capturedImage: UIImage?
    @State private var selectedQuality: PhotoQuality = .balanced
    
    enum PhotoQuality: String, CaseIterable {
        case fast = "Rápida"
        case balanced = "Equilibrada"
        case highQuality = "Alta Calidad"
    }
    
    var body: some View {
        VStack(spacing: 20) {
            // Selector de calidad
            VStack(alignment: .leading) {
                Text("Calidad de Foto:")
                    .font(.headline)
                
                Picker("Calidad", selection: $selectedQuality) {
                    ForEach(PhotoQuality.allCases, id: \.self) { quality in
                        Text(quality.rawValue).tag(quality)
                    }
                }
                .pickerStyle(SegmentedPickerStyle())
            }
            .padding()
            
            // Mostrar imagen capturada
            if let image = capturedImage {
                Image(uiImage: image)
                    .resizable()
                    .scaledToFit()
                    .frame(height: 200)
                    .cornerRadius(8)
            }
            
            // Botón de cámara
            Button(action: {
                showImagePicker = true
            }) {
                HStack {
                    Image(systemName: "camera")
                        .font(.system(size: 20))
                    Text("Tomar Foto con Calidad \(selectedQuality.rawValue)")
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
        .navigationTitle("Image Picker Avanzado")
        .sheet(isPresented: $showImagePicker) {
            AdvancedImagePickerView(
                quality: selectedQuality,
                onPhotoCaptured: { result in
                    print("Foto capturada con calidad \(selectedQuality.rawValue): \(result.uri)")
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
        
        // Crear ImagePickerKMP launcher con configuración personalizada
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
        // Actualizar si es necesario
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

#### Configuración del Módulo Compartido

```kotlin
// build.gradle.kts (módulo compartido)
kotlin {
    android {
        // Configuración de Android
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
                // Dependencias específicas de iOS si son necesarias
            }
        }
    }
}

// CameraScreen.kt (módulo compartido)
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
                    Text("Abrir Cámara")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
```

#### Implementación de Android (KMP)

```kotlin
// App.kt (aplicación Android)
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
                    // Pasar el context de la actividad al componente compartido
                    CameraScreen(context = LocalContext.current)
                }
            }
        }
    }
}

// Alternativa: Usar LocalContext directamente en la pantalla
@Composable
fun AndroidCameraScreen() {
    val context = LocalContext.current
    CameraScreen(context = context)
}
```

#### Implementación de iOS (KMP)

```kotlin
// App.kt (aplicación iOS)
import SwiftUI
import ComposeUI

@main
struct ImagePickerApp: App {
    var body: some Scene {
        WindowGroup {
            ComposeView {
                // Pasar context null para iOS - la librería lo maneja internamente
                CameraScreen(context = null)
            }
        }
    }
}

// Alternativa: Usar wrapper de SwiftUI
struct CameraScreenWrapper: View {
    var body: some View {
        ComposeView {
            CameraScreen(context = null)
        }
    }
}
```

#### Ejemplo de App Multiplataforma

```kotlin
// App.kt (módulo compartido)
@Composable
fun ImagePickerApp() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            // El mismo componente funciona en ambas plataformas
            // La librería maneja las diferencias de plataforma internamente
            CameraScreen(context = null) // El context será proporcionado por la aplicación específica de la plataforma
        }
    }
}
```

#### Beneficios Clave de Este Enfoque

1. **Código Único**: El mismo componente `CameraScreen` funciona en Android e iOS
2. **Abstracción de Plataforma**: La librería maneja las diferencias específicas de plataforma internamente
3. **Manejo de Context**: 
   - Android: Pasar `LocalContext.current` o parámetro `context`
   - iOS: Pasar `null` - la librería lo maneja automáticamente
4. **Sin Detección de Plataforma**: No es necesario detectar manualmente la plataforma en tu código
5. **Arquitectura Limpia**: El código específico de plataforma está aislado en la capa de aplicación, no en el componente compartido

Este ejemplo muestra:
- Código base unificado para ambas plataformas
- Manejo automático de plataforma por la librería
- Separación limpia de responsabilidades
- Flujo de desarrollo simplificado

Para más información, consulta [Guía de Integración](INTEGRATION_GUIDE.es.md) y [Referencia de API](API_REFERENCE.es.md). 

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
                            // Manejo personalizado de permisos
                        },
                        customConfirmationView = { result, onConfirm, onRetry ->
                            // Vista de confirmación personalizada
                        }
                    )
                )
            )
        )
    }
    Button(onClick = { showPicker = true }) {
        Text("Tomar foto")
    }
}
```