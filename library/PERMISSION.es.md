Este documento también está disponible en inglés: [PERMISSION.md](PERMISSION.md)

# Manejo de permisos de cámara en Android e iOS

## Resumen

Este documento explica cómo se gestionan los permisos de cámara en la librería ImagePickerKMP para Android e iOS, incluyendo diferencias de comportamiento, detalles de implementación y ejemplos.

## Diferencias entre plataformas

### Sistema de permisos en Android

Android utiliza un sistema donde:
- Los permisos se solicitan en tiempo de ejecución (API 23+)
- El usuario puede conceder/denegar permisos varias veces
- La app puede volver a solicitar permisos tras una denegación
- El usuario puede habilitar/deshabilitar permisos manualmente en Ajustes

### Sistema de permisos en iOS

iOS utiliza un sistema más restrictivo donde:
- Los permisos se solicitan una sola vez por instalación
- Tras la primera denegación, el usuario debe ir a Ajustes para habilitar el permiso
- No existe un mecanismo automático de reintento
- Los controles parentales pueden restringir permisos

## Detalles de implementación

### Implementación en Android

#### Estados de permiso en Android

```kotlin
// Estados de permiso en Android
PackageManager.PERMISSION_GRANTED    // Permiso concedido
PackageManager.PERMISSION_DENIED     // Permiso denegado
```

#### Flujo de permisos en Android

```kotlin
@Composable
actual fun RequestCameraPermission(
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit
) {
    var permissionDeniedCount by remember { mutableIntStateOf(0) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onResult(true)
        } else {
            permissionDeniedCount++
            when {
                permissionDeniedCount >= 2 -> {
                    // Tras la segunda denegación, mostrar diálogo de ajustes
                    showSettingsDialog = true
                    onPermissionPermanentlyDenied()
                }
                else -> {
                    // Primera denegación, mostrar diálogo de reintento
                    showRationale = true
                }
            }
        }
    }
}
```

#### Ejemplo de solicitud de permiso en Android

```kotlin
// Ejemplo: Solicitar permiso de cámara en Android
val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
) { isGranted ->
    if (isGranted) {
        // Permiso concedido - iniciar cámara
        startCamera()
    } else {
        // Permiso denegado - mostrar diálogo de reintento
        showPermissionDialog()
    }
}

// Lanzar solicitud de permiso
permissionLauncher.launch(Manifest.permission.CAMERA)
```

### Implementación en iOS

#### Estados de permiso en iOS

```kotlin
// Estados de permiso en iOS
AVAuthorizationStatusAuthorized      // Permiso concedido
AVAuthorizationStatusNotDetermined  // Primera solicitud
AVAuthorizationStatusDenied         // Permiso denegado
AVAuthorizationStatusRestricted     // Restringido por controles parentales
```

#### Flujo de permisos en iOS

```kotlin
@Composable
actual fun RequestCameraPermission(
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        val currentStatus = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
        when (currentStatus) {
            AVAuthorizationStatusAuthorized -> {
                onResult(true)
            }
            AVAuthorizationStatusNotDetermined -> {
                requestCameraAccess { granted ->
                    if (granted) {
                        onResult(true)
                    } else {
                        // En iOS, mostrar diálogo de ajustes tras la primera denegación
                        isPermissionDeniedPermanently = true
                        showDialog = true
                    }
                }
            }
            AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> {
                // Permiso ya denegado - mostrar diálogo de ajustes
                isPermissionDeniedPermanently = true
                showDialog = true
            }
        }
    }
}
```

#### Ejemplo de solicitud de permiso en iOS

```kotlin
// Ejemplo: Solicitar permiso de cámara en iOS
fun requestCameraAccess(callback: (Boolean) -> Unit) {
    AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
        callback(granted)
    }
}

// Comprobar estado actual del permiso
val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
when (status) {
    AVAuthorizationStatusAuthorized -> {
        // Permiso ya concedido
        startCamera()
    }
    AVAuthorizationStatusNotDetermined -> {
        // Solicitar permiso
        requestCameraAccess { granted ->
            if (granted) {
                startCamera()
            } else {
                showSettingsDialog()
            }
        }
    }
    AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> {
        // Permiso denegado - mostrar diálogo de ajustes
        showSettingsDialog()
    }
}
```

## Flujo de experiencia de usuario

### Experiencia en Android

1. **Primera solicitud**
   ```
   La app solicita permiso de cámara
   ↓
   El sistema muestra el diálogo de permiso
   ↓
   Usuario concede permiso → Se inicia la cámara
   Usuario deniega permiso → Mostrar diálogo de reintento (permissionDeniedCount = 1)
   ```

2. **Diálogo de reintento**
   ```
   Mostrar diálogo "Conceder permiso"
   ↓
   Usuario pulsa "Conceder permiso"
   ↓
   El sistema muestra el diálogo de permiso de nuevo
   ↓
   Usuario concede → Se inicia la cámara
   Usuario deniega → Mostrar diálogo de ajustes (permissionDeniedCount = 2)
   ```

3. **Diálogo de ajustes (denegación permanente)**
   ```
   Mostrar diálogo "Abrir ajustes"
   ↓
   Usuario pulsa "Abrir ajustes"
   ↓
   La app abre los ajustes del sistema
   ↓
   Usuario habilita el permiso manualmente
   ↓
   Vuelve a la app → Se inicia la cámara
   ```

### Experiencia en iOS

1. **Primera solicitud**
   ```
   La app solicita permiso de cámara
   ↓
   El sistema muestra el diálogo de permiso
   ↓
   Usuario concede permiso → Se inicia la cámara
   Usuario deniega permiso → Mostrar diálogo de ajustes
   ```

2. **Diálogo de ajustes**
   ```
   Mostrar diálogo "Abrir ajustes"
   ↓
   Usuario pulsa "Abrir ajustes"
   ↓
   La app abre los ajustes del sistema
   ↓
   Usuario habilita el permiso manualmente
   ↓
   Vuelve a la app → Se inicia la cámara
   ```

## Implementación de diálogos

### Diálogos de permisos en Android

```kotlin
// Diálogo de reintento
CustomPermissionDialog(
    title = "Permiso de cámara denegado",
    description = "Se requiere el permiso de cámara para capturar fotos. Por favor, concédelo",
    confirmationButtonText = "Conceder permiso",
    onConfirm = {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }
)

// Diálogo de ajustes
CustomPermissionDialog(
    title = "Permiso de cámara requerido",
    description = "Se requiere el permiso de cámara para capturar fotos. Por favor, concédelo en los ajustes",
    confirmationButtonText = "Abrir ajustes",
    onConfirm = {
        openAppSettings(context)
    }
)
```

### Diálogos de permisos en iOS

```kotlin
// Diálogo de ajustes (único diálogo mostrado en iOS)
CustomPermissionDialog(
    title = "Permiso de cámara requerido",
    description = "Se requiere el permiso de cámara para capturar fotos. Por favor, concédelo en los ajustes",
    confirmationButtonText = "Abrir ajustes",
    onConfirm = {
        openSettings()
    }
)
```

## Navegación a ajustes

### Navegación a ajustes en Android

```kotlin
fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}
```

### Navegación a ajustes en iOS

```kotlin
fun openSettings() {
    val settingsUrl = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
    if (settingsUrl != null && UIApplication.sharedApplication.canOpenURL(settingsUrl)) {
        UIApplication.sharedApplication.openURL(settingsUrl)
    }
}
```

## Manejo de errores

### Manejo de errores en Android

```kotlin
// Manejar errores de permisos
when {
    permissionDeniedCount >= 2 -> {
        onError(PhotoCaptureException("Permiso de cámara denegado permanentemente"))
    }
    else -> {
        // Mostrar diálogo de reintento
        showRationale = true
    }
}
```

### Manejo de errores en iOS

```kotlin
// Manejar errores de permisos
when (currentStatus) {
    AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> {
        onError(PhotoCaptureException("Permiso de cámara denegado"))
    }
    else -> {
        // Otros casos
    }
}
```

## Buenas prácticas

### Buenas prácticas en Android

1. **Solicitar permisos en el momento adecuado**
   ```kotlin
   // Solicitar permiso cuando el usuario realmente lo necesita
   LaunchedEffect(Unit) {
       if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) 
           == PackageManager.PERMISSION_GRANTED) {
           onResult(true)
       } else {
           permissionLauncher.launch(Manifest.permission.CAMERA)
       }
   }
   ```

2. **Proporcionar una justificación clara**
   ```kotlin
   // Explicar por qué se necesita el permiso
   description = "Se requiere el permiso de cámara para capturar fotos de tu perfil"
   ```

3. **Manejar todos los estados de permiso**
   ```kotlin
   when (currentPermission) {
       PackageManager.PERMISSION_GRANTED -> onResult(true)
       PackageManager.PERMISSION_DENIED -> requestPermission()
   }
   ```

### Buenas prácticas en iOS

1. **Comprobar primero el estado del permiso**
   ```kotlin
   val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
   when (status) {
       AVAuthorizationStatusAuthorized -> startCamera()
       AVAuthorizationStatusNotDetermined -> requestPermission()
       else -> showSettingsDialog()
   }
   ```

2. **Proporcionar instrucciones claras**
   ```kotlin
   // Instrucciones claras para ajustes
   description = "Por favor, ve a Ajustes > Privacidad y seguridad > Cámara y habilita el acceso para esta app"
   ```

3. **Manejar permisos restringidos**
   ```kotlin
   // Comprobar restricciones parentales
   if (status == AVAuthorizationStatusRestricted) {
       showParentalControlDialog()
   }
   ```

## Escenarios de prueba

### Pruebas en Android

1. **Primera solicitud de permiso**
   - Conceder permiso → Debe iniciar la cámara
   - Denegar permiso → Debe mostrar diálogo de reintento

2. **Reintento de solicitud de permiso**
   - Conceder permiso → Debe iniciar la cámara
   - Denegar permiso → Debe mostrar diálogo de ajustes

3. **Navegación a ajustes**
   - Pulsar "Abrir ajustes" → Debe abrir los ajustes de la app
   - Habilitar permiso → Debe iniciar la cámara al volver

### Pruebas en iOS

1. **Primera solicitud de permiso**
   - Conceder permiso → Debe iniciar la cámara
   - Denegar permiso → Debe mostrar diálogo de ajustes

2. **Navegación a ajustes**
   - Pulsar "Abrir ajustes" → Debe abrir los ajustes del sistema
   - Habilitar permiso → Debe iniciar la cámara al volver

3. **Permiso ya denegado**
   - Abrir la app → Debe mostrar el diálogo de ajustes directamente

## Problemas comunes y soluciones

### Problemas en Android

1. **Permiso no solicitado**
   ```kotlin
   // Solución: Comprobar si el permiso ya está concedido
   if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) 
       != PackageManager.PERMISSION_GRANTED) {
       permissionLauncher.launch(Manifest.permission.CAMERA)
   }
   ```

2. **Múltiples solicitudes de permiso**
   ```kotlin
   // Solución: Usar contador de denegaciones
   var permissionDeniedCount by remember { mutableIntStateOf(0) }
   ```

### Problemas en iOS

1. **Estado de permiso no actualizado**
   ```kotlin
   // Solución: Comprobar estado tras volver de ajustes
   LaunchedEffect(Unit) {
       val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
       if (status == AVAuthorizationStatusAuthorized) {
           onResult(true)
       }
   }
   ```

2. **No se abren los ajustes**
   ```kotlin
   // Solución: Comprobar si la URL se puede abrir
   if (UIApplication.sharedApplication.canOpenURL(settingsUrl)) {
       UIApplication.sharedApplication.openURL(settingsUrl)
   }
   ```

## Resumen

El sistema de manejo de permisos en ImagePickerKMP proporciona una experiencia consistente entre plataformas respetando el comportamiento nativo de cada sistema operativo:

- **Android**: Permite reintentos con diálogos de justificación
- **iOS**: Muestra el diálogo de ajustes tras la primera denegación
- **Ambos**: Proporcionan instrucciones claras y manejo de errores
- **Ambos**: Soportan diálogos y callbacks personalizados

Esta implementación asegura que los usuarios entiendan por qué se requieren los permisos y cómo habilitarlos si los deniegan inicialmente. 