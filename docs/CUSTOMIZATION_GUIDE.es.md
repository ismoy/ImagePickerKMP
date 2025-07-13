This document is also available in English: [CUSTOMIZATION_GUIDE.md](docs/CUSTOMIZATION_GUIDE.md)

# Guía de Personalización

Esta guía explica cómo personalizar ImagePickerKMP para que se adapte al diseño y requerimientos de tu app.

## Tabla de Contenidos

- [Visión General](#visión-general)
- [Personalización de UI](#personalización-de-ui)
- [Diálogos de Permisos](#diálogos-de-permisos)
- [Vistas de Confirmación](#vistas-de-confirmación)
- [Temas y Estilos](#temas-y-estilos)
- [Configuración Avanzada](#configuración-avanzada)
- [Callbacks Personalizados](#callbacks-personalizados)
- [Ejemplos](#ejemplos)
- [Corrección Automática de Orientación](#corrección-automática-de-orientación)

## Visión General

ImagePickerKMP ofrece amplias opciones de personalización para integrarse perfectamente en el diseño y experiencia de usuario de tu app:

- **Componentes de UI personalizados**: Reemplaza los diálogos y vistas por defecto
- **Integración de temas**: Adapta los colores y tipografía de tu app
- **Personalización de comportamiento**: Controla cómo se comporta el picker
- **Callbacks personalizados**: Maneja los eventos a tu manera

## Personalización de UI

### 1. Diálogos de Permiso Personalizados

Crea tus propios diálogos para solicitar permisos:

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

### 2. Vistas de Confirmación Personalizadas

Crea vistas de confirmación de foto personalizadas:

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

### 3. Vistas de Carga Personalizadas

Crea indicadores de carga personalizados:

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

## Diálogos de Permisos

### 1. Manejador de Permisos Personalizado

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

### 2. Diálogo de Permiso con Marca

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

## Vistas de Confirmación

### 1. Vista de Confirmación Mínima

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

### 2. Vista de Confirmación Detallada

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

## Temas y Estilos

### 1. Integración de Tema Personalizado

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

### 2. Soporte para Tema Oscuro

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

### 3. Tipografía Personalizada

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

## Configuración Avanzada

### 1. Preferencias de Foto Personalizadas

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
        preference = CapturePhotoPreference.HIGH_QUALITY // Preferencia personalizada
    )
}
```

### 2. Manejo de Errores Personalizado

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
                    // Manejar errores de permisos
                    showPermissionErrorDialog()
                }
                is PhotoCaptureException -> {
                    // Manejar errores de captura
                    showCaptureErrorDialog()
                }
                else -> {
                    // Manejar errores genéricos
                    showGenericErrorDialog(exception.message)
                }
            }
        }
    )
}
```

### 3. Estados de Carga Personalizados

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
                // Procesar foto
                processPhoto(result) {
                    isLoading = false
                }
            },
            onError = { exception ->
                // Manejar errores
            }
        )
    }
}
```

## Callbacks Personalizados

### 1. Callbacks de Permiso Personalizados

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
            // Manejo de permisos personalizado
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

### 2. Callbacks de Confirmación Personalizados

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
            // Vista de confirmación personalizada
            CustomConfirmationView(
                result = result,
                onConfirm = { 
                    // Procesamiento adicional antes de confirmar
                    processPhoto(result) { processedResult ->
                        onConfirm(processedResult)
                    }
                },
                onRetry = {
                    // Lógica adicional antes de reintentar
                    resetCamera()
                    onRetry()
                }
            )
        }
    )
}
```

## Ejemplos

### 1. Implementación Personalizada Completa

```kotlin
@Composable
fun CompleteCustomImagePicker() {
    var showPicker by remember { mutableStateOf(false) }
    
    if (showPicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                // Manejar captura exitosa
                showPicker = false
                processAndSavePhoto(result)
            },
            onError = { exception ->
                // Manejar errores
                showPicker = false
                showErrorDialog(exception)
            },
            customPermissionHandler = { config ->
                // Manejo de permisos personalizado
                CustomPermissionDialog(
                    title = config.titleDialogConfig,
                    description = config.descriptionDialogConfig,
                    onConfirm = { requestPermission() },
                    onDismiss = { showPicker = false }
                )
            },
            customConfirmationView = { result, onConfirm, onRetry ->
                // Vista de confirmación personalizada
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

### 2. Implementación con Marca

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
                // Lógica específica de marca
                trackBrandEvent("photo_captured")
                processPhotoWithBranding(result)
            },
            onError = { exception ->
                // Mensajería de error específica de marca
                showBrandedErrorDialog(exception)
            },
            customPermissionHandler = { config ->
                // Diálogo de permisos con marca
                BrandedPermissionDialog(
                    config = config,
                    onConfirm = { requestPermission() }
                )
            },
            customConfirmationView = { result, onConfirm, onRetry ->
                // Vista de confirmación con marca
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

### 3. Implementación Mínima

```kotlin
@Composable
fun MinimalImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Manejo simple de la foto
            savePhoto(result)
        },
        onError = { exception ->
            // Manejo simple de errores
            showToast(exception.message)
        }
        // Usa los componentes de UI por defecto
    )
}
```

## Mejores Prácticas

### 1. Tematización Consistente

```kotlin
// Usa un tema consistente en toda tu app
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

### 2. Accesibilidad

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
            // Vista de confirmación accesible
            AccessibleConfirmationView(
                result = result,
                onConfirm = { onConfirm(result) },
                onRetry = { onRetry() }
            )
        }
    )
}
```

### 3. Rendimiento

```kotlin
@Composable
fun PerformantImagePicker() {
    val processedResult = remember { mutableStateOf<PhotoResult?>(null) }
    
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Procesar foto en background
            lifecycleScope.launch(Dispatchers.IO) {
                val processed = processPhoto(result)
                processedResult.value = processed
            }
        },
        onError = { exception ->
            // Manejar errores
        }
    )
    
    // Mostrar resultado procesado
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

## Soporte

Para problemas relacionados con personalización:

1. **Revisa los ejemplos**: Consulta los ejemplos proporcionados
2. **Consistencia de tema**: Asegúrate de que tus personalizaciones coincidan con el tema de tu app
3. **Rendimiento**: Prueba las personalizaciones en diferentes dispositivos
4. **Accesibilidad**: Asegúrate de que las personalizaciones sean accesibles

Para más información, consulta:
- [Referencia de API](docs/API_REFERENCE.es.md)
- [Ejemplos](docs/EXAMPLES.es.md)
- [Guía de Integración](docs/INTEGRATION_GUIDE.es.md) 