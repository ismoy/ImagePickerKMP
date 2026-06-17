This document is also available in English: [EXAMPLES.md](EXAMPLES.md)

# Ejemplos de uso - ImagePickerKMP

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
    var nivelCompresion by remember { mutableStateOf(CompressionLevel.MEDIUM) }

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                compressionLevel = nivelCompresion
            )
        )
    )

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

        Button(onClick = { picker.launchCamera() }) {
            Text("Capturar Foto con Compresión ${nivelCompresion.name}")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val foto = result.photos.first()
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
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            is ImagePickerResult.Dismissed -> {
                Text("Captura cancelada")
            }
            is ImagePickerResult.Loading -> {
                CircularProgressIndicator()
            }
            is ImagePickerResult.Idle -> { /* Estado inicial */ }
        }
    }
}
```

## Ejemplos de Recorte de Imágenes

### Recorte Simple con Opciones Predeterminadas

```kotlin
@Composable
fun EjemploRecorteSimple() {
    var bytesImagenRecortada by remember { mutableStateOf<ByteArray?>(null) }

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(),
            cropConfig = CropConfig(enabled = true)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { picker.launchGallery() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar Imagen para Recortar")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val foto = result.photos.first()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Imagen Seleccionada y Recortada:")
                Image(
                    bitmap = foto.photoBytes.toComposeImageBitmap(),
                    contentDescription = "Imagen recortada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            is ImagePickerResult.Dismissed -> { /* cancelado */ }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* estado inicial */ }
        }
    }
}
```

### Recorte con Selección de Relación de Aspecto

```kotlin
@Composable
fun RecorteConRelacionesAspecto() {
    var relacionAspectoSeleccionada by remember { mutableStateOf<AspectRatio?>(null) }

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(),
            cropConfig = CropConfig(
                enabled = true,
                aspectRatio = relacionAspectoSeleccionada
            )
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { picker.launchGallery() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar Imagen para Recortar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Selecciona Relación de Aspecto:")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { relacionAspectoSeleccionada = AspectRatio.SQUARE }) {
                Text("1:1")
            }
            Button(onClick = { relacionAspectoSeleccionada = AspectRatio.RATIO_4_3 }) {
                Text("4:3")
            }
            Button(onClick = { relacionAspectoSeleccionada = AspectRatio.RATIO_16_9 }) {
                Text("16:9")
            }
            Button(onClick = { relacionAspectoSeleccionada = AspectRatio.RATIO_9_16 }) {
                Text("9:16")
            }
        }

        relacionAspectoSeleccionada?.let { relacion ->
            Spacer(modifier = Modifier.height(8.dp))
            Text("Seleccionado: ${relacion.displayName}")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val foto = result.photos.first()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Imagen Recortada (${relacionAspectoSeleccionada?.displayName}):")
                Image(
                    bitmap = foto.photoBytes.toComposeImageBitmap(),
                    contentDescription = "Imagen recortada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            is ImagePickerResult.Dismissed -> { /* cancelado */ }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* estado inicial */ }
        }
    }
}
```

### Flujo de Trabajo Cámara con Recorte

```kotlin
@Composable
fun FlujoCamaraRecorte() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(),
            cropConfig = CropConfig(
                enabled = true,
                aspectRatio = AspectRatio.SQUARE
            )
        )
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
            Text("Capturar Foto y Recortar")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val foto = result.photos.first()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Foto Final Recortada:")
                Image(
                    bitmap = foto.photoBytes.toComposeImageBitmap(),
                    contentDescription = "Foto final recortada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            is ImagePickerResult.Dismissed -> { /* cancelado */ }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* estado inicial */ }
        }
    }
}
```

## Ejemplos Básicos

### Selector de Imagen Simple

```kotlin
@Composable
fun SelectorImagenSimple() {
    val picker = rememberImagePickerKMP()

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchCamera() }) {
            Text("Tomar Foto")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val foto = result.photos.first()
                println("Foto capturada: ${foto.uri}")
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
            is ImagePickerResult.Error -> {
                println("Error: ${result.exception.message}")
            }
            is ImagePickerResult.Dismissed -> {
                println("Usuario canceló o cerró el selector")
            }
            is ImagePickerResult.Loading -> {
                CircularProgressIndicator()
            }
            is ImagePickerResult.Idle -> { /* Estado inicial */ }
        }
    }
}
```

### Selector de Galería

```kotlin
@Composable
fun SelectorGaleria() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig()
        )
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchGallery(allowMultiple = true) }) {
            Text("Seleccionar de la Galería")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                println("Seleccionadas ${result.photos.size} imágenes")
                result.photos.forEach { foto ->
                    Image(
                        bitmap = foto.photoBytes.toComposeImageBitmap(),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            is ImagePickerResult.Error -> {
                println("Error: ${result.exception.message}")
            }
            is ImagePickerResult.Dismissed -> {
                println("Usuario canceló la selección de galería")
            }
            is ImagePickerResult.Loading -> {
                CircularProgressIndicator()
            }
            is ImagePickerResult.Idle -> { /* Estado inicial */ }
        }
    }
}
```

## Personalización Avanzada

### Colores e Iconos de UI Personalizados

```kotlin
@Composable
fun CustomUIExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
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

    Button(onClick = { picker.launchCamera() }) {
        Text("Tomar Foto con UI Personalizada")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            val foto = result.photos.first()
            Image(
                bitmap = foto.photoBytes.toComposeImageBitmap(),
                contentDescription = "Foto capturada",
                modifier = Modifier.size(200.dp)
            )
        }
        is ImagePickerResult.Error -> { Text("Error: ${result.exception.message}") }
        is ImagePickerResult.Dismissed -> { /* cancelado */ }
        is ImagePickerResult.Loading -> { CircularProgressIndicator() }
        is ImagePickerResult.Idle -> { /* estado inicial */ }
    }
}
```

### Callbacks Personalizados

```kotlin
@Composable
fun CustomCallbacksExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                onCameraReady = {
                    println("¡La cámara está lista!")
                },
                onCameraSwitch = {
                    println("¡Cámara cambiada!")
                }
            )
        )
    )

    Button(onClick = { picker.launchCamera() }) {
        Text("Tomar Foto")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            val foto = result.photos.first()
            Image(
                bitmap = foto.photoBytes.toComposeImageBitmap(),
                contentDescription = "Foto",
                modifier = Modifier.size(200.dp)
            )
        }
        is ImagePickerResult.Error -> {
            println("Error de permiso: ${result.exception.message}")
        }
        is ImagePickerResult.Dismissed -> { /* cancelado */ }
        is ImagePickerResult.Loading -> { CircularProgressIndicator() }
        is ImagePickerResult.Idle -> { /* estado inicial */ }
    }
}
```

## Manejo de Permisos

### Diálogos de Permiso Personalizados

```kotlin
@Composable
fun CustomPermissionExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                    customDeniedDialog = { onRetry ->
                        CustomRetryDialog(
                            title = "Permiso de Cámara Necesario",
                            message = "Necesitamos acceso a la cámara para tomar fotos",
                            onRetry = onRetry
                        )
                    },
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

    Button(onClick = { picker.launchCamera() }) {
        Text("Tomar Foto")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            val foto = result.photos.first()
            Image(
                bitmap = foto.photoBytes.toComposeImageBitmap(),
                contentDescription = "Foto",
                modifier = Modifier.size(200.dp)
            )
        }
        is ImagePickerResult.Error -> { Text("Error: ${result.exception.message}") }
        is ImagePickerResult.Dismissed -> { /* cancelado */ }
        is ImagePickerResult.Loading -> { CircularProgressIndicator() }
        is ImagePickerResult.Idle -> { /* estado inicial */ }
    }
}
```

### Diálogos de Permisos Composables Personalizados

```kotlin
@Composable
fun CustomPermissionDialogsExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                    customDeniedDialog = { onRetry ->
                        CustomRetryDialog(
                            title = "Permiso de Cámara Necesario",
                            message = "Necesitamos acceso a la cámara para tomar fotos",
                            onRetry = onRetry
                        )
                    },
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

    Button(onClick = { picker.launchCamera() }) {
        Text("Tomar Foto con Diálogos de Permisos Personalizados")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            val foto = result.photos.first()
            Image(
                bitmap = foto.photoBytes.toComposeImageBitmap(),
                contentDescription = "Foto",
                modifier = Modifier.size(200.dp)
            )
        }
        is ImagePickerResult.Error -> { Text("Error: ${result.exception.message}") }
        is ImagePickerResult.Dismissed -> { /* cancelado */ }
        is ImagePickerResult.Loading -> { CircularProgressIndicator() }
        is ImagePickerResult.Idle -> { /* estado inicial */ }
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
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig()
        )
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchGallery() }) {
            Text("Seleccionar de la galería")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val foto = result.photos.first()
                Image(
                    bitmap = foto.photoBytes.toComposeImageBitmap(),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            is ImagePickerResult.Error -> { Text("Error: ${result.exception.message}") }
            is ImagePickerResult.Dismissed -> { /* cancelado */ }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* estado inicial */ }
        }
    }
}
```

### Selección Múltiple de Imágenes

```kotlin
@Composable
fun MultipleGallerySelectionExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(
                mimeTypes = listOf("image/jpeg", "image/png")
            )
        )
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchGallery(allowMultiple = true) }) {
            Text("Seleccionar múltiples imágenes")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                Text("${result.photos.size} imágenes seleccionadas")
                result.photos.forEach { foto ->
                    Image(
                        bitmap = foto.photoBytes.toComposeImageBitmap(),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            is ImagePickerResult.Error -> { Text("Error: ${result.exception.message}") }
            is ImagePickerResult.Dismissed -> { /* cancelado */ }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* estado inicial */ }
        }
    }
}
```

### Selección Múltiple Limitada

```kotlin
@Composable
fun LimitedGallerySelectionExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(
                mimeTypes = listOf("image/jpeg", "image/png"),
                selectionLimit = 10
            )
        )
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchGallery(allowMultiple = true) }) {
            Text("Seleccionar hasta 10 imágenes")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                Text("${result.photos.size} imágenes seleccionadas")
                result.photos.forEach { foto ->
                    Image(
                        bitmap = foto.photoBytes.toComposeImageBitmap(),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            is ImagePickerResult.Error -> { Text("Error: ${result.exception.message}") }
            is ImagePickerResult.Dismissed -> { /* cancelado */ }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* estado inicial */ }
        }
    }
}
```

### Selección de Galería de Alto Rendimiento

```kotlin
@Composable
fun HighPerformanceGalleryExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(
                selectionLimit = 5
            )
        )
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { picker.launchGallery(allowMultiple = true) }) {
            Text("Seleccionar hasta 5 imágenes (Optimizado)")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                Text("${result.photos.size} imágenes seleccionadas")
                result.photos.forEach { foto ->
                    Image(
                        bitmap = foto.photoBytes.toComposeImageBitmap(),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            is ImagePickerResult.Error -> { Text("Error: ${result.exception.message}") }
            is ImagePickerResult.Dismissed -> { /* cancelado */ }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* estado inicial */ }
        }
    }
}
```

- En **Android**, el usuario verá el selector de galería del sistema y los permisos se solicitan automáticamente si es necesario.
- En **iOS**, se usa el selector nativo de galería. En iOS 14+ se soporta selección múltiple. El sistema gestiona permisos y acceso limitado de forma nativa.
- El resultado `ImagePickerResult.Success` siempre contiene una lista de fotos en `result.photos`, incluso para selección simple.
- Puedes usar `picker.launchGallery(allowMultiple = true)` para habilitar la selección múltiple de imágenes.
- El parámetro `mimeTypes` en `GalleryConfig` es opcional y permite filtrar los tipos de archivos seleccionables.

## Internacionalización (i18n)

### Uso de Strings Localizados

La librería usa automáticamente strings localizados según el idioma del dispositivo. Todo el texto visible para el usuario se traduce automáticamente:

```kotlin
@Composable
fun InternationalizationExample() {
    // La librería usa automáticamente strings localizados
    val picker = rememberImagePickerKMP()

    Button(onClick = { picker.launchCamera() }) {
        Text("Tomar Foto")
    }

    // ¡No necesitas especificar texto, se localiza automáticamente!
    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            val foto = result.photos.first()
            Image(
                bitmap = foto.photoBytes.toComposeImageBitmap(),
                contentDescription = "Foto",
                modifier = Modifier.size(200.dp)
            )
        }
        is ImagePickerResult.Error -> { Text("Error: ${result.exception.message}") }
        is ImagePickerResult.Dismissed -> { /* cancelado */ }
        is ImagePickerResult.Loading -> { CircularProgressIndicator() }
        is ImagePickerResult.Idle -> { /* estado inicial */ }
    }
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
    val picker = rememberImagePickerKMP()

    Button(onClick = { picker.launchCamera() }) {
        Text("Tomar Foto")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            val foto = result.photos.first()
            Image(
                bitmap = foto.photoBytes.toComposeImageBitmap(),
                contentDescription = "Foto",
                modifier = Modifier.size(200.dp)
            )
        }
        is ImagePickerResult.Error -> {
            when (val exception = result.exception) {
                is PhotoCaptureException -> {
                    println("Captura de foto fallida: ${exception.message}")
                    // Mostrar mensaje de error amigable
                }
                is CameraPermissionException -> {
                    println("Permiso de cámara denegado: ${exception.message}")
                    // Manejar error de permisos
                }
                is GallerySelectionException -> {
                    println("Selección de galería fallida: ${exception.message}")
                    // Manejar error de galería
                }
                else -> {
                    println("Error desconocido: ${exception.message}")
                    // Manejar error genérico
                }
            }
        }
        is ImagePickerResult.Dismissed -> { /* cancelado */ }
        is ImagePickerResult.Loading -> { CircularProgressIndicator() }
        is ImagePickerResult.Idle -> { /* estado inicial */ }
    }
}
```

### Mensajes de Error Personalizados

```kotlin
@Composable
fun CustomErrorMessagesExample() {
    val picker = rememberImagePickerKMP()

    Button(onClick = { picker.launchCamera() }) {
        Text("Tomar Foto")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            val foto = result.photos.first()
            Image(
                bitmap = foto.photoBytes.toComposeImageBitmap(),
                contentDescription = "Foto",
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
            // Mostrar mensaje de error localizado
            Text("Error: $errorMessage")
        }
        is ImagePickerResult.Dismissed -> { /* cancelado */ }
        is ImagePickerResult.Loading -> { CircularProgressIndicator() }
        is ImagePickerResult.Idle -> { /* estado inicial */ }
    }
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
    val picker = rememberImagePickerKMP()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Mostrar imagen capturada
        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val foto = result.photos.first()
                Image(
                    bitmap = foto.photoBytes.toComposeImageBitmap(),
                    contentDescription = "Foto capturada",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            is ImagePickerResult.Dismissed -> { /* cancelado */ }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* estado inicial */ }
        }

        // Botón de cámara
        Button(
            onClick = { picker.launchCamera() },
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
    }
}
```

#### Funciones Avanzadas de Android

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

    Column(modifier = Modifier.padding(16.dp)) {
        // Selector de calidad
        Text("Calidad de Foto:", style = MaterialTheme.typography.h6)
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
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
            onClick = { picker.launchCamera() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tomar Foto con Calidad ${imageQuality.name}")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val foto = result.photos.first()
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
            is ImagePickerResult.Error -> {
                Text("Error: ${result.exception.message}")
            }
            is ImagePickerResult.Dismissed -> { /* cancelado */ }
            is ImagePickerResult.Loading -> { CircularProgressIndicator() }
            is ImagePickerResult.Idle -> { /* estado inicial */ }
        }
    }
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
                        print("Foto capturada: \(result.uri)")
                        showImagePicker = false
                        if let url = URL(string: result.uri) {
                            loadImage(from: url)
                        }
                    },
                    onError: { error in
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
        URLSession.shared.dataTask(with: url) { data, response, error in
            if let data = data, let image = UIImage(data: data) {
                DispatchQueue.main.async {
                    self.capturedImage = image
                }
            }
        }.resume()
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

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.ismoy.imagepickerkmp.*

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
                        val foto = result.photos.first()
                        AsyncImage(
                            model = foto.uri,
                            contentDescription = "Imagen capturada",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    is ImagePickerResult.Error -> {
                        Text("Error: ${result.exception.message}")
                    }
                    is ImagePickerResult.Dismissed -> {
                        Text("Captura cancelada")
                    }
                    is ImagePickerResult.Loading -> {
                        CircularProgressIndicator()
                    }
                    is ImagePickerResult.Idle -> {
                        Text("Presiona el botón para abrir la cámara")
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

#### Implementación de iOS (KMP)

```swift
// App.swift (aplicación iOS)
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
            CameraScreen()
        }
    }
}
```

#### Beneficios Clave de Este Enfoque

1. **Código Único**: El mismo componente `CameraScreen` funciona en Android e iOS
2. **Abstracción de Plataforma**: La librería maneja las diferencias específicas de plataforma internamente
3. **Sin Manejo de Context**: No necesitas pasar `context` manualmente — `rememberImagePickerKMP()` lo resuelve internamente
4. **Sin Detección de Plataforma**: No es necesario detectar manualmente la plataforma en tu código
5. **Arquitectura Limpia**: El código específico de plataforma está aislado en la capa de aplicación, no en el componente compartido

Este ejemplo muestra:
- Código base unificado para ambas plataformas
- Manejo automático de plataforma por la librería
- Separación limpia de responsabilidades
- Flujo de desarrollo simplificado

Para más información, consulta [Guía de Integración](INTEGRATION_GUIDE.es.md) y [Referencia de API](API_REFERENCE.es.md).

### Ejemplo Completo con Configuración Avanzada

```kotlin
@Composable
fun CustomImagePicker() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                preference = CapturePhotoPreference.HIGH_QUALITY,
                permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                    customDeniedDialog = { onRetry ->
                        // Manejo personalizado de permisos
                    },
                    customConfirmationView = { result, onConfirm, onRetry ->
                        // Vista de confirmación personalizada
                    }
                )
            )
        )
    )

    Button(onClick = { picker.launchCamera() }) {
        Text("Tomar foto")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            val foto = result.photos.first()
            Image(
                bitmap = foto.photoBytes.toComposeImageBitmap(),
                contentDescription = "Foto",
                modifier = Modifier.size(200.dp)
            )
        }
        is ImagePickerResult.Error -> { Text("Error: ${result.exception.message}") }
        is ImagePickerResult.Dismissed -> { /* cancelado */ }
        is ImagePickerResult.Loading -> { CircularProgressIndicator() }
        is ImagePickerResult.Idle -> { /* estado inicial */ }
    }
}
```
