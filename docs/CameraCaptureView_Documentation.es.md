# Documentación de CameraCaptureView

## Descripción General

`CameraCaptureView` es un componente Compose específico para Android que proporciona una interfaz completa de captura de cámara con capacidades de selección de galería. Maneja permisos de cámara, captura de fotos, vistas de confirmación e integración con galería en un solo composable.

## Firma de la Función

```kotlin
@Composable
fun CameraCaptureView(
    activity: ComponentActivity,
    preference: CapturePhotoPreference = CapturePhotoPreference.FAST,
    onPhotoResult: (PhotoResult) -> Unit,
    onPhotosSelected: ((List<GalleryPhotoHandler.PhotoResult>) -> Unit)? = null,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    buttonColor: Color? = null,
    iconColor: Color? = null,
    buttonSize: Dp? = null,
    layoutPosition: String? = null,
    flashIcon: ImageVector? = null,
    switchCameraIcon: ImageVector? = null,
    captureIcon: ImageVector? = null,
    galleryIcon: ImageVector? = null,
    onCameraReady: (() -> Unit)? = null,
    onCameraSwitch: (() -> Unit)? = null,
    onPermissionError: ((Exception) -> Unit)? = null,
    onGalleryOpened: (() -> Unit)? = null,
    allowMultiple: Boolean = false,
    mimeTypes: List<String> = listOf("image/*")
)
```

## Parámetros

### Parámetros Requeridos

| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `activity` | `ComponentActivity` | La actividad Android que aloja la vista de cámara. |
| `onPhotoResult` | `(PhotoResult) -> Unit` | Callback invocado cuando se captura y confirma una foto. |
| `onError` | `(Exception) -> Unit` | Callback invocado cuando ocurre un error durante el proceso. |

### Parámetros Opcionales

| Parámetro | Tipo | Valor por Defecto | Descripción |
|-----------|------|-------------------|-------------|
| `preference` | `CapturePhotoPreference` | `CapturePhotoPreference.FAST` | Preferencia de calidad de captura de fotos. |
| `onPhotosSelected` | `((List<GalleryPhotoHandler.PhotoResult>) -> Unit)?` | `null` | Callback para selección múltiple de fotos desde la galería. |
| `customPermissionHandler` | `((PermissionConfig) -> Unit)?` | `null` | Lógica personalizada de manejo de permisos. |
| `customConfirmationView` | `(@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` | `null` | Vista de confirmación personalizada para fotos capturadas. |
| `buttonColor` | `Color?` | `null` | Color personalizado para botones de la UI. |
| `iconColor` | `Color?` | `null` | Color personalizado para iconos de la UI. |
| `buttonSize` | `Dp?` | `null` | Tamaño personalizado para botones de la UI. |
| `layoutPosition` | `String?` | `null` | Posición de layout personalizada. |
| `flashIcon` | `ImageVector?` | `null` | Icono personalizado de flash. |
| `switchCameraIcon` | `ImageVector?` | `null` | Icono personalizado de cambio de cámara. |
| `captureIcon` | `ImageVector?` | `null` | Icono personalizado del botón de captura. |
| `galleryIcon` | `ImageVector?` | `null` | Icono personalizado de galería. |
| `onCameraReady` | `(() -> Unit)?` | `null` | Callback cuando la cámara está lista. |
| `onCameraSwitch` | `(() -> Unit)?` | `null` | Callback cuando se cambia la cámara. |
| `onPermissionError` | `((Exception) -> Unit)?` | `null` | Callback para errores de permisos. |
| `onGalleryOpened` | `(() -> Unit)?` | `null` | Callback cuando se abre la galería. |
| `allowMultiple` | `Boolean` | `false` | Permitir selección múltiple de fotos. |
| `mimeTypes` | `List<String>` | `["image/*"]` | Tipos MIME permitidos para selección de archivos. |

## Tipos de Retorno

### PhotoResult
```kotlin
data class PhotoResult(
    val uri: String,
    val width: Int,
    val height: Int
)
```

### GalleryPhotoHandler.PhotoResult
```kotlin
data class PhotoResult(
    val uri: String,
    val width: Int,
    val height: Int
)
```

## Estados del Componente

El `CameraCaptureView` maneja tres estados principales:

1. **Estado de Permisos**: Verifica y solicita permisos de cámara
2. **Estado de Cámara**: Muestra vista previa de cámara con controles de captura
3. **Estado de Confirmación**: Muestra foto capturada con opciones de confirmar/reintentar

## Componentes Internos

### CameraXManager
- Maneja el ciclo de vida y operaciones de la cámara
- Gestiona vista previa de cámara y captura de fotos
- Proporciona cambio de cámara y control de flash

### CameraCapturePreview
- Renderiza vista previa de cámara
- Proporciona botón de captura y controles de cámara
- Maneja interacciones de cámara

### ImageConfirmationViewWithCustomButtons
- Muestra foto capturada para confirmación
- Proporciona opciones de confirmar y reintentar
- Soporta vistas de confirmación personalizadas

### GalleryPickerLauncher
- Maneja selección de fotos de galería
- Soporta selección múltiple de fotos
- Integra con galería del sistema

## Ejemplos de Uso

### Captura Básica de Cámara
```kotlin
@Composable
fun MiVistaDeCamara() {
    CameraCaptureView(
        activity = LocalContext.current as ComponentActivity,
        onPhotoResult = { resultado ->
            // Manejar foto capturada
            println("Foto capturada: ${resultado.uri}")
        },
        onError = { excepcion ->
            // Manejar error
            println("Error: ${excepcion.message}")
        }
    )
}
```

### Uso Avanzado con Personalización
```kotlin
@Composable
fun VistaDeCamaraPersonalizada() {
    CameraCaptureView(
        activity = LocalContext.current as ComponentActivity,
        preference = CapturePhotoPreference.QUALITY,
        onPhotoResult = { resultado ->
            // Manejar foto capturada
        },
        onPhotosSelected = { resultados ->
            // Manejar múltiples fotos seleccionadas
        },
        onError = { excepcion ->
            // Manejar error
        },
        buttonColor = Color.Blue,
        iconColor = Color.White,
        allowMultiple = true,
        mimeTypes = listOf("image/jpeg", "image/png")
    )
}
```

### Manejo Personalizado de Permisos
```kotlin
@Composable
fun CamaraConPermisosPersonalizados() {
    CameraCaptureView(
        activity = LocalContext.current as ComponentActivity,
        onPhotoResult = { resultado ->
            // Manejar foto capturada
        },
        onError = { excepcion ->
            // Manejar error
        },
        customPermissionHandler = { configuracion ->
            // Lógica personalizada de manejo de permisos
            when (configuracion) {
                is PermissionConfig.Camera -> {
                    // Manejar permiso de cámara
                }
                is PermissionConfig.Gallery -> {
                    // Manejar permiso de galería
                }
            }
        }
    )
}
```

### Vista de Confirmación Personalizada
```kotlin
@Composable
fun CamaraConConfirmacionPersonalizada() {
    CameraCaptureView(
        activity = LocalContext.current as ComponentActivity,
        onPhotoResult = { resultado ->
            // Manejar foto capturada
        },
        onError = { excepcion ->
            // Manejar error
        },
        customConfirmationView = { resultado, onConfirm, onRetry ->
            // Vista de confirmación personalizada
            Column {
                AsyncImage(
                    model = resultado.uri,
                    contentDescription = "Foto capturada"
                )
                Row {
                    Button(onClick = { onConfirm(resultado) }) {
                        Text("Confirmar")
                    }
                    Button(onClick = onRetry) {
                        Text("Reintentar")
                    }
                }
            }
        }
    )
}
```

## Manejo de Errores

El componente maneja varios escenarios de error:

1. **Permisos Denegados**: Solicita automáticamente permisos de cámara
2. **Cámara No Disponible**: Manejado a través del callback `onError`
3. **Problemas de Acceso a Galería**: Manejado a través del callback `onError`
4. **Fallos de Captura de Fotos**: Manejado a través del callback `onError`

## Permisos

### Permisos Requeridos
- `CAMERA`: Requerido para funcionalidad de cámara
- `READ_EXTERNAL_STORAGE`: Requerido para acceso a galería
- `WRITE_EXTERNAL_STORAGE`: Requerido para guardar fotos

### Flujo de Permisos
1. El componente verifica el permiso de cámara
2. Si no está concedido, muestra diálogo de solicitud de permiso
3. Si es denegado, muestra diálogo de configuración
4. Si es permanentemente denegado, llama `onError` con `PhotoCaptureException`

## Características de Cámara

### Características Soportadas
- **Vista Previa de Cámara**: Vista previa de cámara en tiempo real
- **Captura de Fotos**: Captura de fotos de alta calidad
- **Control de Flash**: Modos flash encendido/apagado/automático
- **Cambio de Cámara**: Cambio entre cámara frontal/trasera
- **Integración con Galería**: Acceso directo a galería
- **Selección Múltiple**: Selección múltiple de fotos desde galería

### Controles de Cámara
- **Botón de Captura**: Botón de captura grande y accesible
- **Alternar Flash**: Control de modo flash
- **Cambio de Cámara**: Cambiar entre cámaras frontal/trasera
- **Botón de Galería**: Acceso rápido a galería

## Personalización de UI

### Personalización de Colores
```kotlin
buttonColor = Color.Blue,      // Color personalizado de botón
iconColor = Color.White,       // Color personalizado de icono
```

### Personalización de Iconos
```kotlin
flashIcon = Icons.Default.FlashOn,           // Icono personalizado de flash
switchCameraIcon = Icons.Default.Cameraswitch, // Icono personalizado de cambio de cámara
captureIcon = Icons.Default.Camera,          // Icono personalizado de captura
galleryIcon = Icons.Default.PhotoLibrary,    // Icono personalizado de galería
```

### Personalización de Tamaño
```kotlin
buttonSize = 24.dp,  // Tamaño personalizado de botón
```

## Consideraciones de Rendimiento

### Gestión de Memoria
- Los recursos de cámara se disponen correctamente cuando el componente se destruye
- Los resultados de fotos se gestionan eficientemente
- Las superficies de vista previa se limpian automáticamente

### Optimización de Batería
- La cámara se detiene cuando el componente no es visible
- El uso de flash está optimizado
- La calidad de vista previa está balanceada para rendimiento

## Mejores Prácticas

1. **Siempre manejar errores**: Proporcionar manejo adecuado de errores en el callback `onError`
2. **Verificar permisos**: Usar `customPermissionHandler` para lógica personalizada de permisos
3. **Validar actividad**: Asegurar que se proporcione `ComponentActivity` adecuada
4. **Manejar ciclo de vida**: El componente maneja automáticamente el ciclo de vida de la cámara
5. **Personalizar UI**: Usar parámetros de color e iconos para theming consistente
6. **Probar en dispositivos**: Probar en varios dispositivos Android y niveles de API

## Dependencias

### Dependencias Requeridas
```kotlin
implementation("androidx.camera:camera-core:1.3.0")
implementation("androidx.camera:camera-camera2:1.3.0")
implementation("androidx.camera:camera-lifecycle:1.3.0")
implementation("androidx.camera:camera-view:1.3.0")
implementation("androidx.activity:activity-compose:1.7.0")
```

### Dependencias Opcionales
```kotlin
implementation("io.coil-kt:coil-compose:2.4.0")  // Para AsyncImage
```

## Compatibilidad de Versiones

- **Android**: API 21+
- **Compose**: 1.4.0+
- **CameraX**: 1.3.0+
- **Kotlin**: 1.8.0+

## Solución de Problemas

### Problemas Comunes

1. **Cámara No Inicia**
   - Verificar permisos de cámara
   - Verificar que la actividad sea ComponentActivity
   - Verificar disponibilidad de cámara en el dispositivo

2. **Problemas de Permisos**
   - Verificar permisos en el manifest
   - Verificar manejo de permisos en tiempo de ejecución
   - Probar en diferentes versiones de Android

3. **Fallos de Captura de Fotos**
   - Verificar permisos de almacenamiento
   - Verificar configuración del file provider
   - Verificar espacio de almacenamiento disponible

4. **Galería No Se Abre**
   - Verificar permisos de almacenamiento
   - Verificar configuración de tipos MIME
   - Probar en diferentes versiones de Android

5. **UI No Se Renderiza**
   - Verificar compatibilidad de versión de Compose
   - Verificar ciclo de vida de la actividad
   - Verificar componentes UI conflictivos

### Consejos de Depuración

1. **Habilitar Logging**: Agregar logging a callbacks para depuración
2. **Probar Permisos**: Probar flujos de permisos exhaustivamente
3. **Verificar Ciclo de Vida**: Verificar manejo del ciclo de vida de la actividad
4. **Monitorear Memoria**: Verificar memory leaks en uso de cámara
5. **Probar Dispositivos**: Probar en varios dispositivos Android y versiones

## Guía de Migración

### Desde Versiones Anteriores
1. Actualizar para usar `ComponentActivity` en lugar de contexto genérico
2. Asegurar implementación adecuada de manejo de errores
3. Actualizar manejo de permisos si se usa lógica personalizada
4. Revisar firmas de callbacks para cualquier cambio
5. Actualizar dependencias a las últimas versiones

## Soporte

Para problemas y preguntas:
- GitHub Issues: [URL del Repositorio]
- Documentación: [URL de Documentación]
- Ejemplos: [URL de Ejemplos] 