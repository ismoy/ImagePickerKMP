# Documentación de ImagePickerLauncher

## Descripción General

`ImagePickerLauncher` es una función de Kotlin Multiplatform Compose que proporciona una interfaz unificada para lanzar funcionalidades de captura de cámara y selección de galería en las plataformas Android e iOS.

## Firma de la Función

```kotlin
@Composable
expect fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onPhotosSelected: ((List<GalleryPhotoHandler.PhotoResult>) -> Unit)? = null,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null,
    dialogTitle: String = getStringResource(StringResource.SELECT_OPTION_DIALOG_TITLE),
    takePhotoText: String = getStringResource(StringResource.TAKE_PHOTO_OPTION),
    selectFromGalleryText: String = getStringResource(StringResource.SELECT_FROM_GALLERY_OPTION),
    cancelText: String = getStringResource(StringResource.CANCEL_OPTION),
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
| `context` | `Any?` | El contexto/actividad para el lanzador. Debe ser `ComponentActivity` en Android. |
| `onPhotoCaptured` | `(PhotoResult) -> Unit` | Callback invocado cuando se captura una foto desde la cámara. |
| `onError` | `(Exception) -> Unit` | Callback invocado cuando ocurre un error durante el proceso. |

### Parámetros Opcionales

| Parámetro | Tipo | Valor por Defecto | Descripción |
|-----------|------|-------------------|-------------|
| `onPhotosSelected` | `((List<GalleryPhotoHandler.PhotoResult>) -> Unit)?` | `null` | Callback para selección múltiple de fotos desde la galería. |
| `customPermissionHandler` | `((PermissionConfig) -> Unit)?` | `null` | Lógica personalizada de manejo de permisos. |
| `customConfirmationView` | `(@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` | `null` | Vista de confirmación personalizada para fotos capturadas. |
| `preference` | `CapturePhotoPreference?` | `null` | Preferencias de captura de fotos (FAST, QUALITY, etc.). |
| `dialogTitle` | `String` | "Seleccionar opción" localizado | Título del diálogo de selección. |
| `takePhotoText` | `String` | "Tomar foto" localizado | Texto para la opción de cámara. |
| `selectFromGalleryText` | `String` | "Seleccionar de galería" localizado | Texto para la opción de galería. |
| `cancelText` | `String` | "Cancelar" localizado | Texto para la opción de cancelar. |
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

## Comportamiento Específico por Plataforma

### Implementación Android
- Utiliza `CameraCaptureView` para funcionalidad de cámara
- Requiere `ComponentActivity` como contexto
- Soporta CameraX para operaciones de cámara
- Integración nativa con galería de Android

### Implementación iOS
- Utiliza cámara nativa de iOS y selector de fotos
- Muestra hoja de acción para selección de opciones
- Maneja permisos a través de diálogos del sistema iOS
- Soporta acceso tanto a cámara como a biblioteca de fotos

## Ejemplos de Uso

### Uso Básico
```kotlin
@Composable
fun MiSelectorDeImagenes() {
    ImagePickerLauncher(
        context = LocalContext.current,
        config = ImagePickerConfig(
            onPhotoCaptured = { resultado ->
                // Manejar foto capturada
                println("Foto capturada: ${resultado.uri}")
            },
            onError = { excepcion ->
                // Manejar error
                println("Error: ${excepcion.message}")
            }
        )
    )
}
```

### Uso Avanzado con Personalización
```kotlin
@Composable
fun SelectorDeImagenesPersonalizado() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { resultado ->
            // Manejar foto capturada
        },
        onPhotosSelected = { resultados ->
            // Manejar múltiples fotos seleccionadas
        },
        onError = { excepcion ->
            // Manejar error
        },
        preference = CapturePhotoPreference.QUALITY,
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
fun SelectorConPermisosPersonalizados() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { resultado ->
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

## Manejo de Errores

La función maneja varios escenarios de error:

1. **Contexto Inválido**: Lanza excepción si el contexto no es `ComponentActivity` en Android
2. **Permisos Denegados**: Manejado a través del callback `onPermissionError`
3. **Cámara No Disponible**: Manejado a través del callback `onError`
4. **Problemas de Acceso a Galería**: Manejado a través del callback `onError`

## Permisos

### Permisos Android
- `CAMERA`: Requerido para funcionalidad de cámara
- `READ_EXTERNAL_STORAGE`: Requerido para acceso a galería
- `WRITE_EXTERNAL_STORAGE`: Requerido para guardar fotos

### Permisos iOS
- `NSCameraUsageDescription`: Requerido para acceso a cámara
- `NSPhotoLibraryUsageDescription`: Requerido para acceso a biblioteca de fotos

## Localización

La función soporta localización a través del sistema `StringResource`:

- `SELECT_OPTION_DIALOG_TITLE`: Título del diálogo
- `TAKE_PHOTO_OPTION`: Texto de opción de cámara
- `SELECT_FROM_GALLERY_OPTION`: Texto de opción de galería
- `CANCEL_OPTION`: Texto de opción de cancelar

## Mejores Prácticas

1. **Siempre manejar errores**: Proporcionar manejo adecuado de errores en el callback `onError`
2. **Verificar permisos**: Usar `customPermissionHandler` para lógica personalizada de permisos
3. **Validar contexto**: Asegurar que se proporcione el contexto adecuado (ComponentActivity en Android)
4. **Manejar selección múltiple**: Usar `onPhotosSelected` para manejo de múltiples fotos
5. **Personalizar UI**: Usar parámetros de color e iconos para theming consistente

## Dependencias

### Dependencias Android
```kotlin
implementation("androidx.camera:camera-core:1.3.0")
implementation("androidx.camera:camera-camera2:1.3.0")
implementation("androidx.camera:camera-lifecycle:1.3.0")
implementation("androidx.camera:camera-view:1.3.0")
```

### Dependencias iOS
- No se requieren dependencias adicionales (usa APIs nativas de iOS)

## Compatibilidad de Versiones

- **Kotlin**: 1.8.0+
- **Compose**: 1.4.0+
- **Android**: API 21+
- **iOS**: 13.0+

## Guía de Migración

### Desde Versiones Anteriores
1. Actualizar parámetro context para usar tipo `Any?`
2. Asegurar implementación adecuada de manejo de errores
3. Actualizar manejo de permisos si se usa lógica personalizada
4. Revisar firmas de callbacks para cualquier cambio

## Solución de Problemas

### Problemas Comunes

1. **Error de Contexto en Android**
   - Asegurar que el contexto sea `ComponentActivity`
   - Verificar ciclo de vida de la actividad

2. **Problemas de Permisos**
   - Verificar permisos en el manifest
   - Verificar entradas en Info.plist de iOS
   - Implementar manejo personalizado de permisos

3. **Cámara No Funciona**
   - Verificar disponibilidad de cámara en el dispositivo
   - Verificar permisos de cámara
   - Asegurar contexto de actividad adecuado

4. **Problemas de Acceso a Galería**
   - Verificar permisos de almacenamiento
   - Verificar configuración de tipos MIME
   - Asegurar configuración adecuada del file provider

## Soporte

Para problemas y preguntas:
- GitHub Issues: [URL del Repositorio]
- Documentación: [URL de Documentación]
- Ejemplos: [URL de Ejemplos] 