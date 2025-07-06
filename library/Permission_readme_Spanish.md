# Manejo de Permisos de Cámara en Android e iOS

## Descripción General

Este documento explica cómo se manejan los permisos de cámara en la librería ImagePickerKMP para ambas plataformas, Android e iOS, incluyendo las diferencias en comportamiento, detalles de implementación y ejemplos.

## Diferencias entre Plataformas

### Sistema de Permisos de Android

Android utiliza un sistema de permisos donde:
- Los permisos se solicitan en tiempo de ejecución (API 23+)
- Los usuarios pueden conceder/denegar permisos múltiples veces
- Las aplicaciones pueden solicitar permisos nuevamente después de la denegación
- Los usuarios pueden habilitar/deshabilitar permisos manualmente en Configuración

### Sistema de Permisos de iOS

iOS utiliza un sistema de permisos más restrictivo donde:
- Los permisos se solicitan una vez por instalación de la aplicación
- Después de la primera denegación, los usuarios deben ir a Configuración para habilitar permisos
- No existe un mecanismo de reintento automático
- Los controles parentales pueden restringir permisos

## Detalles de Implementación

### Implementación en Android

#### Estados de Permisos en Android

```kotlin
// Estados de permisos en Android
PackageManager.PERMISSION_GRANTED    // Permiso concedido
PackageManager.PERMISSION_DENIED     // Permiso denegado
```

#### Flujo de Permisos en Android

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
                    // Marcar como denegación permanente después de 2 intentos
                    onPermissionPermanentlyDenied()
                }
                else -> {
                    // Mostrar diálogo de reintento
                    showRationale = true
                }
            }
        }
    }
}
```

#### Ejemplo de Solicitud de Permisos en Android

```kotlin
// Ejemplo: Solicitar permiso de cámara en Android
val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
) { isGranted ->
    if (isGranted) {
        // Permiso concedido - proceder con la cámara
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

#### Estados de Permisos en iOS

```kotlin
// Estados de permisos en iOS
AVAuthorizationStatusAuthorized      // Permiso concedido
AVAuthorizationStatusNotDetermined  // Primera solicitud
AVAuthorizationStatusDenied         // Permiso denegado
AVAuthorizationStatusRestricted     // Restringido por controles parentales
```

#### Flujo de Permisos en iOS

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
                        // En iOS, mostrar diálogo de configuración directamente después de la primera denegación
                        isPermissionDeniedPermanently = true
                        showDialog = true
                    }
                }
            }
            AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> {
                // Permiso ya denegado - mostrar diálogo de configuración
                isPermissionDeniedPermanently = true
                showDialog = true
            }
        }
    }
}
```

#### Ejemplo de Solicitud de Permisos en iOS

```kotlin
// Ejemplo: Solicitar permiso de cámara en iOS
fun requestCameraAccess(callback: (Boolean) -> Unit) {
    AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
        callback(granted)
    }
}

// Verificar estado actual del permiso
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
        // Permiso denegado - mostrar diálogo de configuración
        showSettingsDialog()
    }
}
```

## Flujo de Experiencia de Usuario

### Experiencia de Usuario en Android

1. **Primera Solicitud**
   ```
   La app solicita permiso de cámara
   ↓
   El sistema muestra diálogo de permiso
   ↓
   Usuario concede permiso → La cámara inicia
   Usuario deniega permiso → Mostrar diálogo de reintento
   ```

2. **Diálogo de Reintento**
   ```
   Mostrar diálogo "Conceder Permiso"
   ↓
   Usuario hace clic en "Conceder Permiso"
   ↓
   El sistema muestra diálogo de permiso nuevamente
   ↓
   Usuario concede → La cámara inicia
   Usuario deniega → Marcar como denegación permanente
   ```

3. **Denegación Permanente**
   ```
   Mostrar diálogo "Abrir Configuración"
   ↓
   Usuario hace clic en "Abrir Configuración"
   ↓
   La app abre configuración del sistema
   ↓
   Usuario habilita permiso manualmente
   ↓
   Volver a la app → La cámara inicia
   ```

### Experiencia de Usuario en iOS

1. **Primera Solicitud**
   ```
   La app solicita permiso de cámara
   ↓
   El sistema muestra diálogo de permiso
   ↓
   Usuario concede permiso → La cámara inicia
   Usuario deniega permiso → Mostrar diálogo de configuración
   ```

2. **Diálogo de Configuración**
   ```
   Mostrar diálogo "Abrir Configuración"
   ↓
   Usuario hace clic en "Abrir Configuración"
   ↓
   La app abre configuración del sistema
   ↓
   Usuario habilita permiso manualmente
   ↓
   Volver a la app → La cámara inicia
   ```

## Implementación de Diálogos

### Diálogos de Permisos en Android

```kotlin
// Diálogo de Reintento
CustomPermissionDialog(
    title = "Permiso de cámara denegado",
    description = "El permiso de cámara es necesario para capturar fotos. Por favor concede los permisos",
    confirmationButtonText = "Conceder permiso",
    onConfirm = {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }
)

// Diálogo de Configuración
CustomPermissionDialog(
    title = "Permiso de cámara requerido",
    description = "El permiso de cámara es necesario para capturar fotos. Por favor concédelo en configuración",
    confirmationButtonText = "Abrir configuración",
    onConfirm = {
        openAppSettings(context)
    }
)
```

### Diálogos de Permisos en iOS

```kotlin
// Diálogo de Configuración (único diálogo mostrado en iOS)
CustomPermissionDialog(
    title = "Permiso de cámara requerido",
    description = "El permiso de cámara es necesario para capturar fotos. Por favor concédelo en configuración",
    confirmationButtonText = "Abrir configuración",
    onConfirm = {
        openSettings()
    }
)
```

## Navegación a Configuración

### Navegación a Configuración en Android

```kotlin
fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}
```

### Navegación a Configuración en iOS

```kotlin
fun openSettings() {
    val settingsUrl = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
    if (settingsUrl != null && UIApplication.sharedApplication.canOpenURL(settingsUrl)) {
        UIApplication.sharedApplication.openURL(settingsUrl)
    }
}
```

## Manejo de Errores

### Manejo de Errores en Android

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

### Manejo de Errores en iOS

```kotlin
// Manejar errores de permisos
when (currentStatus) {
    AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> {
        onError(PhotoCaptureException("Permiso de cámara denegado"))
    }
    else -> {
        // Manejar otros casos
    }
}
```

## Mejores Prácticas

### Mejores Prácticas en Android

1. **Solicitar permisos en el momento adecuado**
   ```kotlin
   // Solicitar permiso cuando el usuario realmente necesita la cámara
   LaunchedEffect(Unit) {
       if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) 
           == PackageManager.PERMISSION_GRANTED) {
           onResult(true)
       } else {
           permissionLauncher.launch(Manifest.permission.CAMERA)
       }
   }
   ```

2. **Proporcionar justificación clara**
   ```kotlin
   // Explicar por qué se necesita el permiso
   description = "El permiso de cámara es necesario para capturar fotos para tu perfil"
   ```

3. **Manejar todos los estados de permisos**
   ```kotlin
   when (currentPermission) {
       PackageManager.PERMISSION_GRANTED -> onResult(true)
       PackageManager.PERMISSION_DENIED -> requestPermission()
   }
   ```

### Mejores Prácticas en iOS

1. **Verificar estado del permiso primero**
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
   // Instrucciones claras para configuración
   description = "Por favor ve a Configuración > Privacidad y Seguridad > Cámara y habilita el acceso para esta app"
   ```

3. **Manejar permisos restringidos**
   ```kotlin
   // Verificar restricciones parentales
   if (status == AVAuthorizationStatusRestricted) {
       showParentalControlDialog()
   }
   ```

## Escenarios de Prueba

### Pruebas en Android

1. **Primera solicitud de permiso**
   - Conceder permiso → Debería iniciar la cámara
   - Denegar permiso → Debería mostrar diálogo de reintento

2. **Reintento de solicitud de permiso**
   - Conceder permiso → Debería iniciar la cámara
   - Denegar permiso → Debería mostrar diálogo de configuración

3. **Navegación a configuración**
   - Hacer clic en "Abrir Configuración" → Debería abrir configuración de la app
   - Habilitar permiso → Debería iniciar la cámara al volver

### Pruebas en iOS

1. **Primera solicitud de permiso**
   - Conceder permiso → Debería iniciar la cámara
   - Denegar permiso → Debería mostrar diálogo de configuración

2. **Navegación a configuración**
   - Hacer clic en "Abrir Configuración" → Debería abrir configuración del sistema
   - Habilitar permiso → Debería iniciar la cámara al volver

3. **Permiso ya denegado**
   - Abrir app → Debería mostrar diálogo de configuración directamente

## Problemas Comunes y Soluciones

### Problemas en Android

1. **Permiso no solicitado**
   ```kotlin
   // Solución: Verificar si el permiso ya está concedido
   if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) 
       != PackageManager.PERMISSION_GRANTED) {
       permissionLauncher.launch(Manifest.permission.CAMERA)
   }
   ```

2. **Múltiples solicitudes de permisos**
   ```kotlin
   // Solución: Usar contador de permisos
   var permissionDeniedCount by remember { mutableIntStateOf(0) }
   ```

### Problemas en iOS

1. **Estado del permiso no actualizado**
   ```kotlin
   // Solución: Verificar estado después de volver de configuración
   LaunchedEffect(Unit) {
       val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
       if (status == AVAuthorizationStatusAuthorized) {
           onResult(true)
       }
   }
   ```

2. **Configuración no se abre**
   ```kotlin
   // Solución: Verificar si la URL se puede abrir
   if (UIApplication.sharedApplication.canOpenURL(settingsUrl)) {
       UIApplication.sharedApplication.openURL(settingsUrl)
   }
   ```

## Resumen

El sistema de manejo de permisos en ImagePickerKMP proporciona una experiencia consistente entre plataformas mientras respeta el comportamiento nativo de cada sistema operativo:

- **Android**: Permite reintentos con diálogos de justificación
- **iOS**: Muestra diálogo de configuración directamente después de la primera denegación
- **Ambas**: Proporcionan orientación clara al usuario y manejo de errores
- **Ambas**: Soportan diálogos personalizados y callbacks

Esta implementación asegura que los usuarios entiendan por qué se necesitan los permisos y cómo habilitarlos si inicialmente son denegados. 