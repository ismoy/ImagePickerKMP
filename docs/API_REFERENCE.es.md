Este documento también está disponible en inglés: [API_REFERENCE.md](API_REFERENCE.md)

# Referencia de API

Documentación completa de la API para la librería ImagePickerKMP.

## Tabla de Contenidos

- [Componentes principales](#componentes-principales)
- [Clases de datos](#clases-de-datos)
- [Enums](#enums)
- [Configuración](#configuración)
- [APIs específicas de plataforma](#apis-específicas-de-plataforma)

## 📸 Captura de Foto – Documentación Específica

## Descripción
La funcionalidad de captura de foto en ImagePickerKMP permite a los desarrolladores integrar una experiencia de cámara moderna, personalizable y multiplataforma en sus aplicaciones. Incluye control de flash, cambio de cámara, vista previa, confirmación y personalización total de la UI.

---

## Ejemplo básico de captura de foto para Android y iOS

```kotlin
ImagePickerLauncher(
    config = ImagePickerConfig(
        onPhotoCaptured = { result ->
            // Manejar el resultado de la foto capturada
            println("Foto capturada: ${result.uri}")
        },
        onError = { exception ->
            // Manejar errores
            println("Error: ${exception.message}")
        },
        onDismiss = {
            // Manejar cuando el usuario cancela
            println("Usuario canceló")
        }
    )
)
```

---

## Ejemplo avanzado: Personalización de la confirmación (solo Android)

```kotlin
ImagePickerLauncher(
    config = ImagePickerConfig(
        onPhotoCaptured = { result ->
            // Manejar el resultado de la foto capturada
            cameraPhoto = result
            showCameraPicker = false
        },
        onError = { exception ->
            // Manejar errores
            showCameraPicker = false
        },
        onDismiss = {
            // Manejar cancelación
            showCameraPicker = false
        },
        // Vista de confirmación personalizada (solo Android)
        cameraCaptureConfig = CameraCaptureConfig(
            preference = CapturePhotoPreference.QUALITY,
            permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                customConfirmationView = { photoResult, onConfirm, onRetry ->
                    MiVistaDeConfirmacionPersonalizada(
                        result = photoResult,
                        onConfirm = onConfirm,
                        onRetry = onRetry
                    )
                }
            )
        )
    )
)
```

## Ejemplo: Deshabilitar pantalla de confirmación (solo Android)

```kotlin
ImagePickerLauncher(
    config = ImagePickerConfig(
        onPhotoCaptured = { result ->
            // La foto se toma automáticamente sin confirmación
            cameraPhoto = result
            showCameraPicker = false
        },
        onError = { exception ->
            // Manejar errores
            showCameraPicker = false
        },
        onDismiss = {
            // Manejar cancelación
            showCameraPicker = false
        },
        // Configuración para saltar la confirmación
        cameraCaptureConfig = CameraCaptureConfig(
            preference = CapturePhotoPreference.QUALITY,
            permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                skipConfirmation = true // ¡Nuevo! Evita la pantalla de confirmación
            )
        )
    )
)
```

---

## Parámetros relevantes

- **onPhotoCaptured**: Callback con el resultado de la foto (`CameraPhotoHandler.PhotoResult`)
- **onError**: Callback para manejar errores (`Exception`)
- **onDismiss**: Callback cuando el usuario cancela
- **preference**: Preferencia de calidad de la foto (`CapturePhotoPreference.FAST`, `BALANCED`, `QUALITY`)
- **customConfirmationView**: Composable para personalizar la UI de confirmación (solo Android)
- **customPickerDialog**: Composable para personalizar el diálogo de selección (solo iOS)

---

## Experiencia de usuario
- **Vista previa**: El usuario ve la cámara en tiempo real.
- **Control de flash**: Botón para alternar entre Auto, On, Off (iconos visuales).
- **Cambio de cámara**: Botón para alternar entre cámara trasera y frontal.
- **Captura**: Botón central para tomar la foto.
- **Confirmación**: Vista moderna para aceptar o reintentar la foto, con textos e iconos personalizables.

---

## Notas y recomendaciones
- El sistema de permisos está gestionado automáticamente.
- Puedes personalizar completamente la UI de confirmación.
- Los textos por defecto están en inglés, pero puedes localizarlos fácilmente.
- El flash solo funciona en modos de calidad `BALANCED` o `QUALITY`.
- Si necesitas aún más control, implementa tu propio `customConfirmationView`.

---

## Referencias de código en el proyecto
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/CameraCapturePreview.kt**: Lógica de la vista previa y controles de cámara.
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/ImageConfirmationViewWithCustomButtons.kt**: UI de confirmación de foto.
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/CameraController.kt**: Lógica de control de cámara, flash y cambio de cámara.
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/CameraXManager.kt**: Abstracción para gestión de cámara.

---

## 🖼️ Selección de Imágenes desde Galería – Documentación Específica

## Descripción
La funcionalidad de galería en ImagePickerKMP permite a los desarrolladores integrar una experiencia moderna y personalizable para seleccionar imágenes desde la galería del dispositivo. Soporta selección simple o múltiple, filtros por tipo de archivo y confirmación personalizada.

---
## Componentes principales

### ImagePickerLauncher

## Ejemplo básico de selección de imagen

```kotlin
GalleryPickerLauncher(
    onPhotosSelected = { results ->
        // Manejar las imágenes seleccionadas
        println("Imágenes seleccionadas: ${results}")
    },
    onError = { exception ->
        // Manejar errores
        println("Error: ${exception.message}")
    },
    onDismiss = {
        // Manejar cuando el usuario cancela
        println("Usuario canceló la selección")
    }
)
```

---

## Ejemplo avanzado: Selección múltiple con filtros

```kotlin
GalleryPickerLauncher(
    onPhotosSelected = { results ->
        // Manejar múltiples imágenes seleccionadas
        selectedImages = results
        showGalleryPicker = false
    },
    onError = { exception ->
        // Manejar errores
        showGalleryPicker = false
    },
    onDismiss = {
        // Manejar cancelación
        showGalleryPicker = false
    },
    allowMultiple = true, // Permitir selección múltiple
    mimeTypes = listOf("image/jpeg", "image/png"), // Filtrar por tipos de archivo
    selectionLimit = 30 // Límite de selección (máximo 30 imágenes) solo IOS en android no hay limite
)
```

---

## Parámetros relevantes

- **onPhotosSelected**: Callback con la lista de imágenes seleccionadas (`List<GalleryPhotoHandler.PhotoResult>`)
- **onError**: Callback para manejar errores (`Exception`)
- **onDismiss**: Callback cuando el usuario cancela
- **allowMultiple**: Permite seleccionar varias imágenes (por defecto: `false`)
- **mimeTypes**: Lista de tipos MIME permitidos (por defecto: `listOf("image/*")`)
- **selectionLimit**: Límite máximo de selección (por defecto: `30`)

---

## Experiencia de usuario
- **Selector de galería**: El usuario puede elegir una imagen o varias (si `allowMultiple` está habilitado)
- **Filtros**: Se pueden aplicar filtros por tipo de archivo usando `mimeTypes`
- **Límites**: Se puede establecer un límite máximo de selección con `selectionLimit`

---

## Notas y recomendaciones
- El sistema de permisos está gestionado automáticamente
- La selección múltiple está soportada en ambas plataformas (Android e iOS)
- Los tipos MIME permiten filtrar por formatos específicos de imagen
- El límite de selección ayuda a controlar el rendimiento de la aplicación

---

## Referencias de código en el proyecto
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/GalleryPickerLauncher.android.kt**: Lógica de selección de imágenes en Android.
- **library/src/commonMain/kotlin/io/github/ismoy/imagepickerkmp/GalleryPickerLauncher.kt**: Abstracción multiplataforma para la galería.
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/ImageConfirmationViewWithCustomButtons.kt**: UI de confirmación de imagen.

---

## 🗜️ Compresión de Imágenes – Documentación Específica

## Descripción
La funcionalidad de compresión de imágenes en ImagePickerKMP optimiza automáticamente el tamaño de las imágenes manteniendo una calidad aceptable. Funciona tanto para captura de cámara como selección de galería, con niveles de compresión configurables y procesamiento asíncrono.

---

## Características
- **Compresión automática**: Aplica compresión de forma transparente durante el procesamiento de imágenes
- **Niveles configurables**: Opciones de compresión BAJA, MEDIA, ALTA
- **Soporte multi-formato**: JPEG, PNG, HEIC, HEIF, WebP, GIF, BMP
- **Procesamiento asíncrono**: UI no bloqueante con Kotlin Coroutines
- **Optimización inteligente**: Combina escalado de dimensiones + compresión de calidad
- **Eficiencia de memoria**: Reciclado y limpieza adecuada de bitmaps

---

## Niveles de Compresión

| Nivel | Calidad | Dimensión Máx | Caso de Uso |
|-------|---------|---------------|-------------|
| BAJA | 95% | 2560px | Compartir alta calidad, uso profesional |
| MEDIA | 75% | 1920px | **Recomendado** - Redes sociales, uso general |
| ALTA | 50% | 1280px | Optimización de almacenamiento, miniaturas |

---

## Cámara con Compresión

```kotlin
ImagePickerLauncher(
    config = ImagePickerConfig(
        onPhotoCaptured = { result ->
            val fileSizeKB = (result.fileSize ?: 0) / 1024.0
            println("Tamaño de imagen comprimida: ${String.format("%.2f", fileSizeKB)}KB")
            println("Tamaño exacto: ${result.fileSize} bytes")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        },
        cameraCaptureConfig = CameraCaptureConfig(
            compressionLevel = CompressionLevel.MEDIUM
        )
    )
)
```

---

## Galería con Compresión

```kotlin
GalleryPickerLauncher(
    onPhotosSelected = { results ->
        results.forEach { photo ->
            val fileSizeKB = (photo.fileSize ?: 0) / 1024.0
            println("Original: ${photo.fileName}")
            println("Tamaño comprimido: ${String.format("%.2f", fileSizeKB)}KB")
            println("Tamaño exacto: ${photo.fileSize} bytes")
        }
    },
    onError = { exception ->
        println("Error: ${exception.message}")
    },
    allowMultiple = true,
    mimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG),
    cameraCaptureConfig = CameraCaptureConfig(
        compressionLevel = CompressionLevel.HIGH 
    )
)
```

---

## Proceso de Compresión

1. **Carga de Imagen**: La imagen original se carga desde cámara/galería
2. **Escalado de Dimensiones**: La imagen se redimensiona si es mayor que la dimensión máxima
3. **Compresión de Calidad**: Se aplica compresión JPEG basada en el nivel
4. **Archivo Temporal**: La imagen comprimida se guarda en caché de la app
5. **Entrega de Resultado**: Se retorna nueva URI con la imagen comprimida

---

## Soporte de Plataforma

| Plataforma | Compresión Cámara | Compresión Galería | Procesamiento Asíncrono |
|------------|-------------------|-------------------|------------------------|
| Android | ✅ | ✅ | ✅ Coroutines |
| iOS | ✅ | ✅ | ✅ Coroutines |

---

## Consideraciones de Rendimiento

- **Uso de Memoria**: Los bitmaps originales se reciclan después de la compresión
- **Tiempo de Procesamiento**: Se ejecuta en hilos de fondo (Dispatchers.IO)
- **Almacenamiento**: Las imágenes comprimidas se almacenan en directorio caché de la app
- **Calidad**: Balance inteligente entre tamaño de archivo y calidad visual

---

## Referencias de Código
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/data/processors/ImageProcessor.kt**: Lógica de compresión de cámara
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/presentation/ui/components/GalleryPickerLauncher.android.kt**: Implementación de compresión de galería
- **library/src/commonMain/kotlin/io/github/ismoy/imagepickerkmp/domain/models/CompressionLevel.kt**: Definiciones de niveles de compresión

---

### ImagePickerLauncher

Composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
expect fun ImagePickerLauncher(
    config: ImagePickerConfig
)
```

#### Parámetros

- `config: ImagePickerConfig` - Configuración completa del selector de imágenes

---

### GalleryPickerLauncher

Composable para seleccionar imágenes y documentos desde la galería o explorador de archivos.

> **🚀 Nuevo**: Selector inteligente que automáticamente decide entre galería vs explorador de archivos

```kotlin
@Composable
expect fun GalleryPickerLauncher(
    onPhotosSelected: (List<GalleryPhotoHandler.PhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit = {},
    allowMultiple: Boolean = false,
    mimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL),
    selectionLimit: Long = SELECTION_LIMIT,
    cameraCaptureConfig: CameraCaptureConfig? = null,
    enableCrop: Boolean = false,
    fileFilterDescription: String = "Image files",
    includeExif: Boolean = false
)
```

#### Parámetros

- `onPhotosSelected` - Callback con la lista de imágenes/archivos seleccionados
- `onError` - Callback para manejar errores
- `onDismiss` - Callback cuando el usuario cancela
- `allowMultiple` - Permite selección múltiple (por defecto: `false`)
- `mimeTypes` - Lista de tipos MIME permitidos (afecta el tipo de picker usado)
- `selectionLimit` - Límite máximo de selección
- `cameraCaptureConfig` - Configuración para compresión y otras opciones
- `enableCrop` - Habilita funcionalidad de recorte de imagen
- `fileFilterDescription` - Descripción del filtro de archivos (Desktop)
- `includeExif` - **Extrae metadatos EXIF** (ubicación, cámara, etc.) - **Por defecto: `false`**

#### Comportamiento del Selector Inteligente (Android)

| Tipos MIME | Picker Usado | Resultado |
|------------|--------------|-----------|
| Solo imágenes (`image/*`) | **Galería nativa** | ✅ Mejor UX para fotos |
| PDFs (`application/pdf`) | **Explorador de archivos** | ✅ Acceso a documentos |
| Tipos mixtos | **Explorador de archivos** | ✅ Máxima compatibilidad |

#### Ejemplo con EXIF

```kotlin
GalleryPickerLauncher(
    onPhotosSelected = { photos ->
        photos.forEach { photo ->
            photo.exif?.let { exif ->
                println(" Ubicación GPS: ${exif.latitude}, ${exif.longitude}")
                println(" Cámara: ${exif.camera}")
                println(" Fecha/Hora: ${exif.dateTime}")
            }
        }
    },
    includeExif = true 
)
```

---

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
expect fun RequestCameraPermission(
    dialogConfig: CameraPermissionDialogConfig,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `dialogConfig: CameraPermissionDialogConfig` - Configuración de los diálogos de permisos
- `onPermissionPermanentlyDenied: () -> Unit` - Callback cuando el permiso es denegado permanentemente
- `onResult: (Boolean) -> Unit` - Callback con el resultado del permiso
- `customPermissionHandler: (() -> Unit)?` - Manejador personalizado de permisos

---

## Clases de datos

### CameraPhotoHandler.PhotoResult

Representa el resultado de una captura de foto desde la cámara.

```kotlin
data class PhotoResult(
    val uri: String,
    val width: Int,
    val height: Int,
    val fileName: String? = null,
    val fileSize: Long? = null,
    val mimeType: String? = null,
    val exif: ExifData? = null 
)
```

### GalleryPhotoHandler.PhotoResult

Representa el resultado de una imagen seleccionada desde la galería.

```kotlin
data class PhotoResult(
    val uri: String,
    val width: Int,
    val height: Int,
    val fileName: String? = null,
    val fileSize: Long? = null,
    val mimeType: String? = null,
    val exif: ExifData? = null 
)
```

### ImagePickerConfig

Configuración principal para el selector de imágenes.

```kotlin
data class ImagePickerConfig(
    val onPhotoCaptured: (CameraPhotoHandler.PhotoResult) -> Unit,
    val onPhotosSelected: ((List<GalleryPhotoHandler.PhotoResult>) -> Unit)? = null,
    val onError: (Exception) -> Unit,
    val onDismiss: () -> Unit = {},
    val dialogTitle: String = "Select option",
    val takePhotoText: String = "Take photo",
    val selectFromGalleryText: String = "Select from gallery",
    val cancelText: String = "Cancel",
    val customPickerDialog: (@Composable (
        onTakePhoto: () -> Unit,
        onSelectFromGallery: () -> Unit,
        onCancel: () -> Unit
    ) -> Unit)? = null,
    val cameraCaptureConfig: CameraCaptureConfig = CameraCaptureConfig()
)
```

### CameraCaptureConfig

Configuración para la captura de cámara.

```kotlin
data class CameraCaptureConfig(
    val preference: CapturePhotoPreference = CapturePhotoPreference.QUALITY,
    val captureButtonSize: Dp = 72.dp,
    val compressionLevel: CompressionLevel? = null, 
    val includeExif: Boolean = false, 
    val uiConfig: UiConfig = UiConfig(),
    val cameraCallbacks: CameraCallbacks = CameraCallbacks(),
    val permissionAndConfirmationConfig: PermissionAndConfirmationConfig = PermissionAndConfirmationConfig(),
    val galleryConfig: GalleryConfig = GalleryConfig()
)
```

**Parámetros:**
- `preference` - Preferencia de calidad de captura de foto
- `captureButtonSize` - Tamaño del botón de captura
- `compressionLevel` - Nivel de compresión automática de imagen (null = deshabilitado, MEDIUM = recomendado)
- `includeExif` - **NUEVO**: Extraer metadatos EXIF incluyendo GPS, modelo de cámara, timestamps (solo Android/iOS)
- `uiConfig` - Configuración de personalización de UI
- `cameraCallbacks` - Callbacks del ciclo de vida de la cámara
- `permissionAndConfirmationConfig` - Diálogos de permisos y confirmación
- `galleryConfig` - Configuración de selección de galería

### PermissionAndConfirmationConfig

Configuración para permisos y confirmación.

```kotlin
data class PermissionAndConfirmationConfig(
    val customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    val customConfirmationView: (@Composable (CameraPhotoHandler.PhotoResult, (CameraPhotoHandler.PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    val customDeniedDialog: (@Composable ((onRetry: () -> Unit) -> Unit))? = null,
    val customSettingsDialog: (@Composable ((onOpenSettings: () -> Unit) -> Unit))? = null,
    val skipConfirmation: Boolean = false
)
```

#### Parámetros

- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejador personalizado de permisos para personalización basada en texto
- `customConfirmationView: (@Composable (...) -> Unit)?` - Composable personalizado para confirmación de foto
- `customDeniedDialog: (@Composable ((onRetry: () -> Unit) -> Unit))?` - Diálogo composable personalizado cuando se deniega el permiso
- `customSettingsDialog: (@Composable ((onOpenSettings: () -> Unit) -> Unit))?` - Diálogo composable personalizado para abrir configuración
- `skipConfirmation: Boolean` - Si es true, confirma automáticamente la foto sin mostrar la pantalla de confirmación (solo Android)

### UiConfig

Configuración para el estilo de la interfaz de usuario.

```kotlin
data class UiConfig(
    val buttonColor: Color? = null,
    val iconColor: Color? = null,
    val buttonSize: Dp? = null,
    val flashIcon: ImageVector? = null,
    val switchCameraIcon: ImageVector? = null,
    val galleryIcon: ImageVector? = null
)
```

### CameraCallbacks

Configuración para callbacks de la cámara.

```kotlin
data class CameraCallbacks(
    val onCameraReady: (() -> Unit)? = null,
    val onCameraSwitch: (() -> Unit)? = null,
    val onPermissionError: ((Exception) -> Unit)? = null,
    val onGalleryOpened: (() -> Unit)? = null
)
```

### ExifData

Contiene metadatos EXIF completos extraídos de imágenes. **Disponible solo en Android e iOS.**

```kotlin
data class ExifData(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val altitude: Double? = null,
    
    val dateTaken: String? = null,
    val dateTime: String? = null,
    val digitizedTime: String? = null,
    val originalTime: String? = null,
    
    val cameraModel: String? = null,
    val cameraManufacturer: String? = null,
    val software: String? = null,
    val owner: String? = null,
    
    val orientation: String? = null,
    val colorSpace: String? = null,
    val whiteBalance: String? = null,
    val flash: String? = null,
    val focalLength: String? = null,
    val aperture: String? = null,
    val shutterSpeed: String? = null,
    val iso: String? = null,
    val imageWidth: Int? = null,
    val imageHeight: Int? = null
)
```

**Ejemplo de Uso:**

```kotlin
ImagePickerLauncher(
    config = ImagePickerConfig(
        onPhotoCaptured = { result ->
            result.exif?.let { exif ->
                println(" GPS: ${exif.latitude}, ${exif.longitude}")
                println(" Cámara: ${exif.cameraModel}")
                println(" Fecha: ${exif.dateTaken}")
                println(" Config: ISO ${exif.iso}, f/${exif.aperture}")
            }
        },
        cameraCaptureConfig = CameraCaptureConfig(
            includeExif = true 
        )
    )
)

GalleryPickerLauncher(
    onPhotosSelected = { results ->
        results.forEachIndexed { index, result ->
            println("Imagen $index:")
            result.exif?.let { exif ->
                println("   Ubicación: ${exif.latitude}, ${exif.longitude}")
                println("   Cámara: ${exif.cameraModel}")
                println("   Fecha: ${exif.dateTaken}")
            } ?: println("  Sin datos EXIF disponibles")
        }
    },
    allowMultiple = true,
    includeExif = true 
)
```

**Soporte de Plataforma:**
- ✅ **Android**: Soporte completo vía `androidx.exifinterface`
- ✅ **iOS**: Soporte completo vía framework nativo ImageIO
- ❌ **Desktop/Web/Wasm**: No soportado (devuelve null)

---

## Utilidades

### ImagePickerLogger

Interfaz para registrar mensajes dentro de la librería ImagePicker.

```kotlin
interface ImagePickerLogger {
    fun log(message: String)
}
```

#### Implementación por defecto

```kotlin
object DefaultLogger : ImagePickerLogger {
    override fun log(message: String) {
        println(message)
    }
}
```

#### Ejemplo de uso

```kotlin
class CustomLogger : ImagePickerLogger {
    override fun log(message: String) {
        Log.d("ImagePicker", message)
    }
}
```

### ImagePickerUiConstants

Constantes para la interfaz de usuario de ImagePicker.

```kotlin
object ImagePickerUiConstants {
    const val ORIENTATION_ROTATE_90 = 90f
    const val ORIENTATION_ROTATE_180 = 180f
    const val ORIENTATION_ROTATE_270 = 270f
    const val ORIENTATION_FLIP_HORIZONTAL_X = -1f
    const val ORIENTATION_FLIP_HORIZONTAL_Y = 1f
    const val ORIENTATION_FLIP_VERTICAL_X = 1f
    const val ORIENTATION_FLIP_VERTICAL_Y = -1f
    const val SYSTEM_VERSION_10 = 10.0
    const val DELAY_TO_TAKE_PHOTO = 60L
    const val SELECTION_LIMIT = 30L
}
```

### RequestCameraPermission

Composable para manejar permisos de cámara.

```kotlin
@Composable
expect fun RequestCameraPermission(
    dialogConfig: CameraPermissionDialogConfig,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `dialogConfig: CameraPermissionDialogConfig` - Configuración de diálogos de permisos
- `onPermissionPermanentlyDenied: () -> Unit` - Callback cuando el permiso es denegado permanentemente
- `onResult: (Boolean) -> Unit` - Callback con el resultado del permiso
- `customPermissionHandler: (() -> Unit)?` - Manejador personalizado de permisos

#### Ejemplo

```kotlin
@Composable
fun ManejadorDePermisosPersonalizado() {
    val dialogConfig = CameraPermissionDialogConfig(
        titleDialogConfig = "Permiso de cámara requerido",
        descriptionDialogConfig = "Por favor, habilita el acceso a la cámara en configuración",
        btnDialogConfig = "Abrir configuración",
        titleDialogDenied = "Permiso denegado",
        descriptionDialogDenied = "Se requiere permiso de cámara",
        btnDialogDenied = "Conceder permiso"
    )
    
    RequestCameraPermission(
        dialogConfig = dialogConfig,
        onPermissionPermanentlyDenied = {
            println("Permiso denegado permanentemente")
        },
        onResult = { granted ->
            println("Permiso concedido: $granted")
        },
        customPermissionHandler = null
    )
}
```

---

## Excepciones

### PhotoCaptureException

Excepción lanzada cuando ocurre un error durante la captura o procesamiento de fotos.

```kotlin
class PhotoCaptureException(message: String) : Exception(message)
```

#### Descripción

Utilizada para señalar fallos de cámara o procesamiento de imágenes en la librería ImagePicker.

#### Ejemplo de uso

```kotlin
@Composable
fun ErrorHandlingExample() {
    ImagePickerLauncher(
        config = ImagePickerConfig(
            onPhotoCaptured = { result -> /* ... */ },
            onError = { exception ->
                when (exception) {
                    is PhotoCaptureException -> {
                        println("Error en captura de foto: ${exception.message}")
                        // Mostrar mensaje de error amigable al usuario
                    }
                    else -> {
                        println("Error desconocido: ${exception.message}")
                        // Manejar error genérico
                    }
                }
            }
        )
    )
}
```

#### Ejemplo de uso

```kotlin
try {
    // Operación de captura de foto
    capturePhoto()
} catch (e: PhotoCaptureException) {
    println("Error al capturar foto: ${e.message}")
    // Manejar el error apropiadamente
}
```

### ImagePickerException

Excepción base para errores de la librería ImagePicker.

```kotlin
open class ImagePickerException(message: String) : Exception(message)
```

### PermissionDeniedException

Excepción lanzada cuando los permisos son denegados.

```kotlin
class PermissionDeniedException(message: String) : ImagePickerException(message)
```

#### Ejemplo de uso

```kotlin
try {
    // Solicitar permisos
    requestCameraPermission()
} catch (e: PermissionDeniedException) {
    println("Permisos denegados: ${e.message}")
    // Mostrar diálogo para ir a configuración
}
```

---

## Enums

### CompressionLevel

Representa diferentes niveles de compresión para el procesamiento de imágenes.

```kotlin
enum class CompressionLevel {
    LOW,    // Compresión baja - mantiene alta calidad pero archivos más grandes (95% calidad, 2560px máx)
    MEDIUM, // Compresión media - calidad y tamaño equilibrados (75% calidad, 1920px máx)
    HIGH    // Compresión alta - archivos más pequeños pero menor calidad (50% calidad, 1280px máx)
}
```

**Mapeo de Calidad:**
- `LOW`: 95% calidad, dimensión máxima 2560px - Mejor para compartir alta calidad
- `MEDIUM`: 75% calidad, dimensión máxima 1920px - Recomendado para la mayoría de casos de uso
- `HIGH`: 50% calidad, dimensión máxima 1280px - Mejor para optimización de almacenamiento

### CapturePhotoPreference

Representa las preferencias de captura de foto.

```kotlin
enum class CapturePhotoPreference {
    FAST,    // Captura rápida con menor calidad
    BALANCED, // Balance entre velocidad y calidad
    QUALITY   // Máxima calidad (más lento)
}
```

### StringResource

Recursos de cadenas utilizados en la librería.

```kotlin
enum class StringResource {
    CAMERA_PERMISSION_REQUIRED,
    CAMERA_PERMISSION_DESCRIPTION,
    OPEN_SETTINGS,
    CAMERA_PERMISSION_DENIED,
    CAMERA_PERMISSION_DENIED_DESCRIPTION,
    GRANT_PERMISSION,
    CAMERA_PERMISSION_PERMANENTLY_DENIED,
    IMAGE_CONFIRMATION_TITLE,
    ACCEPT_BUTTON,
    RETRY_BUTTON,
    SELECT_OPTION_DIALOG_TITLE,
    TAKE_PHOTO_OPTION,
    SELECT_FROM_GALLERY_OPTION,
    CANCEL_OPTION,
    PREVIEW_IMAGE_DESCRIPTION,
    HD_QUALITY_DESCRIPTION,
    SD_QUALITY_DESCRIPTION,
    INVALID_CONTEXT_ERROR,
    PHOTO_CAPTURE_ERROR,
    GALLERY_SELECTION_ERROR,
    PERMISSION_ERROR,
    GALLERY_PERMISSION_REQUIRED,
    GALLERY_PERMISSION_DESCRIPTION,
    GALLERY_PERMISSION_DENIED,
    GALLERY_PERMISSION_DENIED_DESCRIPTION,
    GALLERY_GRANT_PERMISSION,
    GALLERY_BTN_SETTINGS
}
```

---

## Excepciones

### ImagePickerException

Representa una excepción específica de ImagePickerKMP.

```kotlin
class ImagePickerException(message: String) : Exception(message)
```

---

## Configuración Principal

### ImagePickerConfig

Clase de configuración principal para lanzar el selector de imágenes.

```kotlin
data class ImagePickerConfig(
    val onPhotoCaptured: (CameraPhotoHandler.PhotoResult) -> Unit,
    val onPhotosSelected: ((List<GalleryPhotoHandler.PhotoResult>) -> Unit)? = null,
    val onError: (Exception) -> Unit,
    val onDismiss: () -> Unit = {},
    val dialogTitle: String = "Select option",
    val takePhotoText: String = "Take photo",
    val selectFromGalleryText: String = "Select from gallery",
    val cancelText: String = "Cancel",
    val customPickerDialog: (
        @Composable (
            onTakePhoto: () -> Unit,
            onSelectFromGallery: () -> Unit,
            onCancel: () -> Unit
        ) -> Unit
    )? = null,
    val cameraCaptureConfig: CameraCaptureConfig = CameraCaptureConfig()
)
```

#### Propiedades

- `onPhotoCaptured: (CameraPhotoHandler.PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onPhotosSelected: ((List<GalleryPhotoHandler.PhotoResult>) -> Unit)?` - Callback para selección múltiple de galería
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `onDismiss: () -> Unit` - Callback cuando se cierra el selector
- `dialogTitle: String` - Título del diálogo de selección
- `takePhotoText: String` - Texto para la opción de cámara
- `selectFromGalleryText: String` - Texto para la opción de galería
- `cancelText: String` - Texto para cancelar
- `customPickerDialog: (@Composable (...) -> Unit)?` - Diálogo personalizado para iOS
- `cameraCaptureConfig: CameraCaptureConfig` - Configuración de captura de cámara

#### Ejemplo

```kotlin
val config = ImagePickerConfig(
    onPhotoCaptured = { resultado ->
        println("Foto capturada: ${resultado.uri}")
    },
    onPhotosSelected = { resultados ->
        println("${resultados.size} fotos seleccionadas")
    },
    onError = { excepcion ->
        println("Error: ${excepcion.message}")
    },
    onDismiss = {
        println("Selector cerrado")
    },
    dialogTitle = "Seleccionar opción",// Título del diálogo de selección para iOS
    takePhotoText = "Tomar foto",// Texto para la opción de cámara para iOS
    selectFromGalleryText = "Seleccionar de galería", // Texto para la opción de galería para iOS
    cancelText = "Cancelar", // Texto para cancelar para iOS
     // Diálogo personalizado para iOS
    customPickerDialog = { onTakePhoto, onSelectFromGallery, onCancel ->
        Column {
            Button(onClick = onTakePhoto) {
                Text("Cámara")
            }
            Button(onClick = onSelectFromGallery) {
                Text("Galería")
            }
            Button(onClick = onCancel) {
                Text("Cancelar")
            }
        }
    },
    cameraCaptureConfig = CameraCaptureConfig(
        preference = CapturePhotoPreference.HIGH_QUALITY,
        permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
            customConfirmationView = { photoResult, onConfirm, onRetry ->
                // Vista de confirmación personalizada
            }
        )
    )
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit = {},
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

---

### AndroidGalleryConfig (Android)

Configuración específica para el comportamiento del selector de galería en Android.

```kotlin
data class AndroidGalleryConfig(
    val forceGalleryOnly: Boolean = true,
    val localOnly: Boolean = true
) {
    companion object {
        fun forMimeTypes(mimeTypes: List<MimeType>): AndroidGalleryConfig
        fun forMimeTypeStrings(mimeTypes: List<String>): AndroidGalleryConfig
    }
}
```

#### Propiedades

- `forceGalleryOnly` - **Fuerza el uso de galería vs explorador de archivos**
  - `true`: Usa `Intent.ACTION_PICK` + `MediaStore` (abre galería nativa)
  - `false`: Usa `ActivityResultContracts.GetContent()` (puede abrir explorador de archivos)
  - **Por defecto**: `true`, pero se ajusta automáticamente según los tipos MIME

- `localOnly` - **Incluye solo imágenes locales**
  - `true`: Agrega `EXTRA_LOCAL_ONLY` al intent (no almacenamiento en la nube)
  - `false`: Permite imágenes de almacenamiento en la nube
  - **Por defecto**: `true`

#### Métodos de Conveniencia

```kotlin
// ✅ Configuración automática basada en tipos MIME
val autoConfig = AndroidGalleryConfig.forMimeTypes(listOf(MimeType.APPLICATION_PDF))
// Resultado: forceGalleryOnly = false (usa explorador para PDFs)

// ✅ Configuración manual
GalleryPickerLauncher(
    // ... otros parámetros ...
    androidGalleryConfig = AndroidGalleryConfig(
        forceGalleryOnly = false, // Fuerza explorador de archivos
        localOnly = true
    )
)
```

#### Comportamiento de Detección Automática

| Tipos MIME Detectados | `forceGalleryOnly` | Resultado |
|----------------------|-------------------|-----------|
| Solo `image/*` | `true` | Galería nativa |
| PDFs (`application/pdf`) | `false` | Explorador de archivos |
| Tipos mixtos (imagen + otros) | `false` | Explorador de archivos |
| Tipos no-imagen | `false` | Explorador de archivos |

---