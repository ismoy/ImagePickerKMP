Este documento también está disponible en inglés: [API_REFERENCE.md](API_REFERENCE.md)

# Referencia de API

Documentación completa de la API para la librería ImagePickerKMP.

## Tabla de Contenidos

- [Componente principal](#componente-principal)
- [Clases de estado y resultado](#clases-de-estado-y-resultado)
- [Configuración](#configuración)
- [Clases de datos](#clases-de-datos)
- [Enums](#enums)
- [Funciones de extensión](#funciones-de-extensión)
- [Permisos de cámara](#permisos-de-cámara)
- [Utilidades](#utilidades)
- [Excepciones](#excepciones)

---

## Componente principal

### rememberImagePickerKMP

API principal idiomática de Compose. Un único state holder — sin booleanos manuales, sin necesidad de llamar `Render()`.

```kotlin
@Composable
fun rememberImagePickerKMP(
    config: ImagePickerKMPConfig = ImagePickerKMPConfig()
): ImagePickerKMPState
```

#### Parámetros

- `config: ImagePickerKMPConfig` - Configuración global aplicada a cada lanzamiento salvo que se sobreescriba por llamada. Por defecto usa valores de plataforma.

#### Uso básico

```kotlin
@Composable
fun MyScreen() {
    val picker = rememberImagePickerKMP()

    Button(onClick = { picker.launchCamera() }) { Text("Cámara") }
    Button(onClick = { picker.launchGallery(allowMultiple = true) }) { Text("Galería") }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            result.photos.forEach { photo ->
                println("URI: ${photo.uri}")
            }
        }
        is ImagePickerResult.Error -> {
            Text("Error: ${result.exception.message}")
        }
        is ImagePickerResult.Dismissed -> { /* usuario canceló */ }
        ImagePickerResult.Loading -> { /* cargando */ }
        ImagePickerResult.Idle -> { /* sin acción */ }
    }
}
```

#### Ejemplo avanzado con configuración completa

```kotlin
@Composable
fun AdvancedScreen() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                preference = CapturePhotoPreference.QUALITY,
                compressionLevel = CompressionLevel.MEDIUM,
                includeExif = true
            ),
            galleryConfig = GalleryConfig(
                allowMultiple = true,
                selectionLimit = 10,
                mimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG)
            ),
            cropConfig = CropConfig(enabled = true, squareCrop = true),
            uiConfig = UiConfig(buttonColor = Color.Blue),
            permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                skipConfirmation = true
            )
        )
    )

    Column {
        Button(onClick = { picker.launchCamera() }) {
            Text("Tomar foto")
        }
        Button(onClick = { picker.launchGallery() }) {
            Text("Seleccionar de galería")
        }
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            result.photos.forEach { photo ->
                println("Foto: ${photo.uri}, Tamaño: ${photo.fileSize} bytes")
                photo.exif?.let { exif ->
                    println("Cámara: ${exif.cameraModel}")
                    println("Fecha: ${exif.dateTaken}")
                }
            }
        }
        is ImagePickerResult.Error -> {
            println("Error: ${result.exception.message}")
        }
        else -> {}
    }
}
```

#### Sobreescrituras por lanzamiento

Cualquier parámetro pasado directamente a `launchCamera()` o `launchGallery()` sobreescribe la configuración global solo para esa invocación, sin mutar la configuración recordada.

```kotlin
@Composable
fun PerLaunchOverrideExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                compressionLevel = CompressionLevel.MEDIUM
            )
        )
    )

    // Usa la configuración global (compresión MEDIUM)
    Button(onClick = { picker.launchCamera() }) {
        Text("Foto normal")
    }

    // Sobreescribe solo para esta llamada (compresión HIGH)
    Button(onClick = {
        picker.launchCamera(
            cameraCaptureConfig = CameraCaptureConfig(
                compressionLevel = CompressionLevel.HIGH
            )
        )
    }) {
        Text("Foto comprimida")
    }

    // Galería con sobreescritura
    Button(onClick = {
        picker.launchGallery(
            allowMultiple = true,
            selectionLimit = 5,
            includeExif = true
        )
    }) {
        Text("Seleccionar múltiples")
    }
}
```

---

## Clases de estado y resultado

### ImagePickerKMPState

State holder retornado por `rememberImagePickerKMP`. Expone el resultado como estado observable reactivo y los métodos de lanzamiento.

```kotlin
@Stable
class ImagePickerKMPState {
    val result: ImagePickerResult
    val isCropActive: Boolean

    fun launchCamera(
        cameraCaptureConfig: CameraCaptureConfig? = null,
        onDismiss: (() -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    )

    fun launchGallery(
        allowMultiple: Boolean? = null,
        mimeTypes: List<MimeType>? = null,
        selectionLimit: Int? = null,
        includeExif: Boolean? = null,
        redactGpsData: Boolean? = null,
        mimeTypeMismatchMessage: String? = null,
        cameraCaptureConfig: CameraCaptureConfig? = null,
        onDismiss: (() -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    )

    fun reset()
}
```

#### Métodos

##### launchCamera

Lanza el picker de cámara. Todos los parámetros son opcionales y sobreescriben los valores de la configuración global para esa invocación.

- `cameraCaptureConfig` - Configuración de cámara por lanzamiento. `null` = usa el global.
- `onDismiss` - Callback cuando el usuario cierra sin capturar. `null` = transiciona a `Dismissed`.
- `onError` - Callback si ocurre un error. `null` = transiciona a `Error`.

##### launchGallery

Lanza el picker de galería. Todos los parámetros son opcionales.

- `allowMultiple` - Permite selección múltiple. `null` = usa `galleryConfig.allowMultiple`.
- `mimeTypes` - Tipos MIME aceptados. `null` = usa `galleryConfig.mimeTypes`.
- `selectionLimit` - Máximo de archivos seleccionables. `null` = usa `galleryConfig.selectionLimit`.
- `includeExif` - Extrae metadatos EXIF. `null` = usa `galleryConfig.includeExif`.
- `redactGpsData` - Elimina coordenadas GPS del EXIF. `null` = usa `galleryConfig.redactGpsData`.
- `mimeTypeMismatchMessage` - Mensaje personalizado para tipos no compatibles.
- `cameraCaptureConfig` - Configuración de cámara para botón integrado de cámara en galería.
- `onDismiss` - Callback cuando el usuario cierra sin seleccionar. `null` = transiciona a `Dismissed`.
- `onError` - Callback si ocurre un error. `null` = transiciona a `Error`.

##### reset

Reinicia el estado a `Idle` y permite iniciar una nueva sesión.

---

### ImagePickerResult

Representa el estado reactivo del resultado del picker.

```kotlin
sealed class ImagePickerResult {
    data object Idle : ImagePickerResult()
    data object Loading : ImagePickerResult()
    data class Success(val photos: List<PhotoResult>) : ImagePickerResult() {
        val first: PhotoResult? get() = photos.firstOrNull()
    }
    data object Dismissed : ImagePickerResult()
    data class Error(val exception: Exception) : ImagePickerResult()
}
```

#### Estados

| Estado | Descripción |
|--------|-------------|
| `Idle` | Sin acción. Estado inicial. |
| `Loading` | El picker está activo esperando interacción del usuario. |
| `Success` | Captura/selección exitosa. Contiene lista de `PhotoResult`. |
| `Dismissed` | El usuario canceló sin seleccionar/capturar. |
| `Error` | Ocurrió un error durante la operación. |

---

## Configuración

### ImagePickerKMPConfig

Configuración global para `rememberImagePickerKMP`.

```kotlin
data class ImagePickerKMPConfig(
    val cameraCaptureConfig: CameraCaptureConfig = CameraCaptureConfig(),
    val galleryConfig: GalleryConfig = GalleryConfig(),
    val cropConfig: CropConfig = CropConfig(),
    val uiConfig: UiConfig = UiConfig(),
    val permissionAndConfirmationConfig: PermissionAndConfirmationConfig = PermissionAndConfirmationConfig()
)
```

#### Propiedades

| Propiedad | Tipo | Descripción |
|-----------|------|-------------|
| `cameraCaptureConfig` | `CameraCaptureConfig` | Comportamiento de la cámara (compresión, EXIF, tamaño botón, etc.) |
| `galleryConfig` | `GalleryConfig` | Comportamiento de la galería (selección múltiple, MIME types, etc.) |
| `cropConfig` | `CropConfig` | Activar y ajustar la UI de recorte |
| `uiConfig` | `UiConfig` | Colores e iconos personalizados para la UI de la cámara |
| `permissionAndConfirmationConfig` | `PermissionAndConfirmationConfig` | Diálogos personalizados para permisos y confirmación |

#### Dónde configurar cada opción

| Tipo de opción | Dónde configurar |
|---|---|
| Diálogos personalizados (`@Composable`) | `permissionAndConfirmationConfig` en este config |
| Colores / iconos de la UI de la cámara | `uiConfig` en este config |
| Comportamiento de cámara/galería por pantalla | parámetros de `launchCamera()` / `launchGallery()` |
| Config fina de cámara (compresión, EXIF, etc.) | `cameraCaptureConfig` en este config |

---

### CameraCaptureConfig

Configuración para la captura de cámara.

```kotlin
data class CameraCaptureConfig(
    val preference: CapturePhotoPreference = CapturePhotoPreference.BALANCED,
    val captureButtonSize: Dp = 72.dp,
    val compressionLevel: CompressionLevel? = CompressionLevel.MEDIUM,
    val includeExif: Boolean = false,
    val redactGpsData: Boolean = true,
    val uiConfig: UiConfig = UiConfig(),
    val cameraCallbacks: CameraCallbacks = CameraCallbacks(),
    val permissionAndConfirmationConfig: PermissionAndConfirmationConfig = PermissionAndConfirmationConfig(),
    val cropConfig: CropConfig = CropConfig(),
    val cameraScaleType: CameraScaleType = CameraScaleType.FILL_CENTER,
    val enableShutterSound: Boolean = true
)
```

#### Propiedades

- `preference` - Preferencia de calidad de captura (`FAST`, `BALANCED`, `QUALITY`). Por defecto: `BALANCED`.
- `captureButtonSize` - Tamaño del botón de captura. Por defecto: `72.dp`.
- `compressionLevel` - Nivel de compresión automática (`null` = sin compresión). Por defecto: `MEDIUM`.
- `includeExif` - Extraer metadatos EXIF incluyendo GPS, modelo de cámara, timestamps. Por defecto: `false`.
- `redactGpsData` - Elimina coordenadas GPS del EXIF antes de entregar. Por defecto: `true`.
- `uiConfig` - Personalización visual de la UI de cámara.
- `cameraCallbacks` - Callbacks del ciclo de vida de la cámara.
- `permissionAndConfirmationConfig` - Diálogos de permisos y confirmación.
- `cropConfig` - Configuración del recorte interactivo tras captura.
- `cameraScaleType` - Cómo se escala la vista previa en el viewport (`FILL_CENTER` o `FIT_CENTER`). Solo Android.
- `enableShutterSound` - Si se reproduce el sonido del obturador al capturar. Por defecto: `true`. Solo Android: incluso activado, el sonido se silencia cuando el dispositivo está en modo silencio o vibración. En iOS el sonido del obturador lo reproduce el sistema, las apps no pueden desactivarlo y ya respeta el interruptor de silencio (salvo en dispositivos donde es obligatorio por ley, p. ej. Japón/Corea).

---

### GalleryConfig

Configuración para la selección de galería.

```kotlin
data class GalleryConfig(
    val allowMultiple: Boolean = false,
    val mimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL),
    val selectionLimit: Int = 30,
    val includeExif: Boolean = false,
    val redactGpsData: Boolean = true,
    val mimeTypeMismatchMessage: String? = null
)
```

#### Propiedades

- `allowMultiple` - Permite selección múltiple. Por defecto: `false`.
- `mimeTypes` - Tipos MIME aceptados para filtrar archivos. Por defecto: `IMAGE_ALL`.
- `selectionLimit` - Máximo de archivos seleccionables (cuando `allowMultiple = true`). Por defecto: `30`.
- `includeExif` - Extraer metadatos EXIF de imágenes seleccionadas. Por defecto: `false`.
- `redactGpsData` - Elimina coordenadas GPS del EXIF. Por defecto: `true`.
- `mimeTypeMismatchMessage` - Mensaje personalizado cuando un archivo no coincide con los tipos permitidos.

---

### CropConfig

Configuración para la UI interactiva de recorte de imagen.

```kotlin
data class CropConfig(
    val enabled: Boolean = false,
    val aspectRatioLocked: Boolean = false,
    val circularCrop: Boolean = true,
    val squareCrop: Boolean = true,
    val freeformCrop: Boolean = false
)
```

#### Propiedades

- `enabled` - Muestra la UI de recorte. Por defecto: `false`.
- `aspectRatioLocked` - Mantiene aspect ratio fijo. Por defecto: `false`.
- `circularCrop` - Ofrece opción de recorte circular. Por defecto: `true`.
- `squareCrop` - Ofrece opción de recorte cuadrado (1:1). Por defecto: `true`.
- `freeformCrop` - Permite recorte libre sin restricciones. Por defecto: `false`.

---

### UiConfig

Configuración para el estilo visual de la UI de cámara.

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

#### Propiedades

- `buttonColor` - Color de los botones de acción principales.
- `iconColor` - Color de los iconos en la UI de cámara.
- `buttonSize` - Tamaño del botón de captura principal.
- `flashIcon` - Icono personalizado para el toggle de flash.
- `switchCameraIcon` - Icono personalizado para cambio de cámara frontal/trasera.
- `galleryIcon` - Icono personalizado para acceso a galería.

---

### CameraCallbacks

Callbacks del ciclo de vida de la cámara.

```kotlin
data class CameraCallbacks(
    val onCameraReady: (() -> Unit)? = null,
    val onCameraSwitch: (() -> Unit)? = null,
    val onPermissionError: ((Exception) -> Unit)? = null,
    val onGalleryOpened: (() -> Unit)? = null
)
```

#### Propiedades

- `onCameraReady` - Se llama cuando la vista previa de cámara está inicializada y lista para capturar.
- `onCameraSwitch` - Se llama después de cambiar entre cámara frontal y trasera.
- `onPermissionError` - Se llama cuando el permiso de cámara es denegado o no está disponible.
- `onGalleryOpened` - Se llama cuando el usuario navega de la cámara al picker de galería.

---

### PermissionAndConfirmationConfig

Configuración para diálogos de permisos y pantalla de confirmación post-captura.

```kotlin
data class PermissionAndConfirmationConfig(
    val customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    val customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    val customDeniedDialog: (@Composable (onRetry: () -> Unit, onDismiss: () -> Unit) -> Unit)? = null,
    val customSettingsDialog: (@Composable (onOpenSettings: () -> Unit, onDismiss: () -> Unit) -> Unit)? = null,
    val skipConfirmation: Boolean = false,
    val cancelButtonTextIOS: String? = "Cancel",
    val onCancelPermissionConfigIOS: (() -> Unit)? = null,
    val confirmationImageContentScale: ContentScale = ContentScale.Crop
)
```

#### Propiedades

- `customPermissionHandler` - Manejador personalizado invocado en lugar del diálogo de permisos por defecto.
- `customConfirmationView` - Composable personalizado que reemplaza la pantalla de confirmación. Recibe `PhotoResult`, callback de confirmación y callback de reintentar.
- `customDeniedDialog` - Composable personalizado cuando el permiso es denegado. Recibe `onRetry` y `onDismiss`.
- `customSettingsDialog` - Composable personalizado para abrir configuración del sistema. Recibe `onOpenSettings` y `onDismiss`.
- `skipConfirmation` - Si es `true`, salta la pantalla de confirmación y entrega la foto directamente. Por defecto: `false`. Solo Android.
- `cancelButtonTextIOS` - Texto del botón cancelar en el alert de configuración de permisos en iOS.
- `onCancelPermissionConfigIOS` - Callback cuando el usuario toca cancelar en el alert de permisos en iOS.
- `confirmationImageContentScale` - Cómo se escala la imagen en la pantalla de confirmación.

#### Ejemplo con confirmación personalizada (solo Android)

```kotlin
val picker = rememberImagePickerKMP(
    config = ImagePickerKMPConfig(
        permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
            customConfirmationView = { photoResult, onConfirm, onRetry ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Tu UI de confirmación personalizada
                    AsyncImage(model = photoResult.uri, contentDescription = null)
                    Row {
                        Button(onClick = { onConfirm(photoResult) }) {
                            Text("Aceptar")
                        }
                        Button(onClick = onRetry) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        )
    )
)
```

#### Ejemplo sin pantalla de confirmación (solo Android)

```kotlin
val picker = rememberImagePickerKMP(
    config = ImagePickerKMPConfig(
        permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
            skipConfirmation = true
        )
    )
)
```

---

### CameraPermissionDialogConfig

Configuración para los diálogos de permisos de cámara.

```kotlin
data class CameraPermissionDialogConfig(
    val titleDialogConfig: String,
    val descriptionDialogConfig: String,
    val btnDialogConfig: String,
    val titleDialogDenied: String,
    val descriptionDialogDenied: String,
    val btnDialogDenied: String,
    val customDeniedDialog: (@Composable (onRetry: () -> Unit, onDismiss: () -> Unit) -> Unit)? = null,
    val customSettingsDialog: (@Composable (onOpenSettings: () -> Unit, onDismiss: () -> Unit) -> Unit)? = null,
    val cancelButtonText: String? = "Cancel",
    val onCancelPermissionConfigIOS: (() -> Unit)? = null
)
```

#### Propiedades

- `titleDialogConfig` - Título del diálogo de configuración de permisos.
- `descriptionDialogConfig` - Descripción del diálogo de configuración de permisos.
- `btnDialogConfig` - Texto del botón del diálogo de configuración.
- `titleDialogDenied` - Título cuando el permiso es denegado.
- `descriptionDialogDenied` - Descripción cuando el permiso es denegado.
- `btnDialogDenied` - Texto del botón cuando el permiso es denegado.
- `customDeniedDialog` - Diálogo composable personalizado cuando se deniega el permiso.
- `customSettingsDialog` - Diálogo composable personalizado para abrir configuración.
- `cancelButtonText` - Texto del botón cancelar (solo iOS).
- `onCancelPermissionConfigIOS` - Callback cuando el usuario cancela en iOS.

---

## Clases de datos

### PhotoResult

Representa el resultado de una captura de foto o selección de galería.

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

#### Propiedades

- `uri` - URI de la imagen capturada/seleccionada.
- `width` - Ancho de la imagen en píxeles.
- `height` - Alto de la imagen en píxeles.
- `fileName` - Nombre del archivo (opcional).
- `fileSize` - Tamaño del archivo en bytes (opcional).
- `mimeType` - Tipo MIME de la imagen (opcional).
- `exif` - Metadatos EXIF extraídos (opcional, requiere `includeExif = true`).

---

### ExifData

Contiene metadatos EXIF completos extraídos de imágenes. Disponible solo en Android e iOS.

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

#### Ejemplo de uso con EXIF

```kotlin
@Composable
fun ExifExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                includeExif = true,
                redactGpsData = false // Permitir GPS (por defecto se elimina)
            )
        )
    )

    Button(onClick = { picker.launchCamera() }) {
        Text("Capturar con EXIF")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            result.first?.exif?.let { exif ->
                println("GPS: ${exif.latitude}, ${exif.longitude}")
                println("Cámara: ${exif.cameraModel} (${exif.cameraManufacturer})")
                println("Fecha: ${exif.dateTaken}")
                println("Config: ISO ${exif.iso}, f/${exif.aperture}")
                println("Orientación: ${exif.orientation}")
            }
        }
        else -> {}
    }
}
```

#### Galería con EXIF

```kotlin
@Composable
fun GalleryExifExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(
                allowMultiple = true,
                includeExif = true
            )
        )
    )

    Button(onClick = { picker.launchGallery() }) {
        Text("Seleccionar con EXIF")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            result.photos.forEachIndexed { index, photo ->
                println("Imagen $index:")
                photo.exif?.let { exif ->
                    println("  Ubicación: ${exif.latitude}, ${exif.longitude}")
                    println("  Cámara: ${exif.cameraModel}")
                    println("  Fecha: ${exif.dateTaken}")
                } ?: println("  Sin datos EXIF disponibles")
            }
        }
        else -> {}
    }
}
```

#### Soporte de Plataforma para EXIF

| Plataforma | Soporte |
|------------|---------|
| Android | ✅ Completo vía `androidx.exifinterface` |
| iOS | ✅ Completo vía framework nativo ImageIO |
| Desktop/Web/Wasm | ❌ No soportado (devuelve `null`) |

---

## Funciones de extensión

### PhotoResult.toPath()

Convierte el URI de la foto a un `kotlinx.io.files.Path` para operaciones de archivo multiplataforma.

```kotlin
fun PhotoResult.toPath(): Path?
```

- **Disponible desde:** v1.0.38
- **Retorna:** `Path?` — `null` si la conversión falla.
- **Requiere:** dependencia `kotlinx-io`.

### PhotoResult.absolutePath

Retorna la ruta absoluta del sistema de archivos como String.

```kotlin
val PhotoResult.absolutePath: String?
```

- **Disponible desde:** v1.0.40
- **Implementación por plataforma:**
  - Android: usa `ContentResolver`
  - iOS: usa `URL.path`
  - Desktop/Web: extracción directa de ruta

#### Ejemplo de extensiones

```kotlin
@Composable
fun PathExample() {
    val picker = rememberImagePickerKMP()

    Button(onClick = { picker.launchCamera() }) {
        Text("Capturar")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            result.first?.let { photo ->
                // Usando kotlinx.io Path (multiplataforma, v1.0.38+)
                photo.toPath()?.let { path ->
                    println("Ruta del archivo: $path")
                }

                // Usando ruta absoluta String (v1.0.40+)
                photo.absolutePath?.let { path ->
                    println("Ruta absoluta: $path")
                }
            }
        }
        else -> {}
    }
}
```

---

## Permisos de cámara

### RequestCameraPermission

Composable para gestionar permisos de cámara de forma independiente.

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

- `dialogConfig` - Configuración de los diálogos de permisos.
- `onPermissionPermanentlyDenied` - Callback cuando el permiso es denegado permanentemente.
- `onResult` - Callback con el resultado del permiso (`true` = concedido).
- `customPermissionHandler` - Manejador personalizado de permisos (opcional).

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

## 🗜️ Compresión de Imágenes

### Descripción

La funcionalidad de compresión optimiza automáticamente el tamaño de las imágenes manteniendo una calidad aceptable. Funciona tanto para captura de cámara como selección de galería, con niveles de compresión configurables y procesamiento asíncrono.

### Niveles de Compresión

| Nivel | Calidad | Dimensión Máx | Caso de Uso |
|-------|---------|---------------|-------------|
| `LOW` | 95% | 2560px | Compartir alta calidad, uso profesional |
| `MEDIUM` | 75% | 1920px | **Recomendado** - Redes sociales, uso general |
| `HIGH` | 50% | 1280px | Optimización de almacenamiento, miniaturas |

### Cámara con Compresión

```kotlin
@Composable
fun CompressedCameraExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                compressionLevel = CompressionLevel.MEDIUM
            )
        )
    )

    Button(onClick = { picker.launchCamera() }) {
        Text("Capturar con compresión")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            result.first?.let { photo ->
                val fileSizeKB = (photo.fileSize ?: 0) / 1024.0
                println("Tamaño comprimido: ${String.format("%.2f", fileSizeKB)}KB")
                println("Tamaño exacto: ${photo.fileSize} bytes")
            }
        }
        else -> {}
    }
}
```

### Galería con Compresión

```kotlin
@Composable
fun CompressedGalleryExample() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(
                allowMultiple = true,
                mimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG)
            ),
            cameraCaptureConfig = CameraCaptureConfig(
                compressionLevel = CompressionLevel.HIGH
            )
        )
    )

    Button(onClick = { picker.launchGallery() }) {
        Text("Seleccionar con compresión")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            result.photos.forEach { photo ->
                val fileSizeKB = (photo.fileSize ?: 0) / 1024.0
                println("Archivo: ${photo.fileName}")
                println("Tamaño comprimido: ${String.format("%.2f", fileSizeKB)}KB")
            }
        }
        else -> {}
    }
}
```

### Proceso de Compresión

1. **Carga de Imagen**: La imagen original se carga desde cámara/galería
2. **Escalado de Dimensiones**: La imagen se redimensiona si es mayor que la dimensión máxima
3. **Compresión de Calidad**: Se aplica compresión JPEG basada en el nivel
4. **Archivo Temporal**: La imagen comprimida se guarda en caché de la app
5. **Entrega de Resultado**: Se retorna nueva URI con la imagen comprimida

### Soporte de Plataforma para Compresión

| Plataforma | Compresión Cámara | Compresión Galería | Procesamiento Asíncrono |
|------------|-------------------|-------------------|------------------------|
| Android | ✅ | ✅ | ✅ Coroutines |
| iOS | ✅ | ✅ | ✅ Coroutines |

### Consideraciones de Rendimiento

- **Uso de Memoria**: Los bitmaps originales se reciclan después de la compresión.
- **Tiempo de Procesamiento**: Se ejecuta en hilos de fondo (Dispatchers.IO).
- **Almacenamiento**: Las imágenes comprimidas se almacenan en directorio caché de la app.
- **Calidad**: Balance inteligente entre tamaño de archivo y calidad visual.

---

## Enums

### CompressionLevel

Niveles de compresión para el procesamiento de imágenes.

```kotlin
enum class CompressionLevel {
    LOW,    // 95% calidad, 2560px máx — Alta calidad, archivos más grandes
    MEDIUM, // 75% calidad, 1920px máx — Equilibrado (recomendado)
    HIGH    // 50% calidad, 1280px máx — Archivos pequeños, menor calidad
}
```

### CapturePhotoPreference

Preferencias de captura de foto.

```kotlin
enum class CapturePhotoPreference {
    FAST,     // Captura rápida con menor calidad
    BALANCED, // Balance entre velocidad y calidad
    QUALITY   // Máxima calidad (más lento)
}
```

### MimeType

Tipos MIME soportados para filtrado de archivos.

Valores principales:
- `MimeType.IMAGE_ALL` — Todos los formatos de imagen (`image/*`)
- `MimeType.IMAGE_JPEG` — Solo JPEG
- `MimeType.IMAGE_PNG` — Solo PNG
- `MimeType.APPLICATION_PDF` — Documentos PDF

### CameraScaleType

Cómo se escala la vista previa de la cámara en el viewport.

```kotlin
enum class CameraScaleType {
    FILL_CENTER, // Llena el viewport, recortando la imagen (por defecto)
    FIT_CENTER   // Letterbox — el encuadre coincide con la imagen capturada
}
```

### StringResource

Recursos de cadenas utilizados en la librería (internacionalización automática).

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

## Utilidades

### ImagePickerLogger

Interfaz para registrar mensajes dentro de la librería.

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

Constantes para la interfaz de usuario.

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

---

## Excepciones

### PhotoCaptureException

Excepción lanzada cuando ocurre un error durante la captura o procesamiento de fotos.

```kotlin
class PhotoCaptureException(message: String) : Exception(message)
```

#### Ejemplo de manejo de errores

```kotlin
@Composable
fun ErrorHandlingExample() {
    val picker = rememberImagePickerKMP()

    Button(onClick = { picker.launchCamera() }) {
        Text("Capturar")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Error -> {
            when (result.exception) {
                is PhotoCaptureException -> {
                    println("Error en captura: ${result.exception.message}")
                }
                is PermissionDeniedException -> {
                    println("Permiso denegado: ${result.exception.message}")
                }
                else -> {
                    println("Error desconocido: ${result.exception.message}")
                }
            }
        }
        else -> {}
    }
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

---

## Experiencia de usuario de la cámara

- **Vista previa**: El usuario ve la cámara en tiempo real.
- **Control de flash**: Botón para alternar entre Auto, On, Off.
- **Cambio de cámara**: Botón para alternar entre cámara trasera y frontal.
- **Captura**: Botón central para tomar la foto.
- **Confirmación**: Vista para aceptar o reintentar la foto (personalizable o desactivable).

---

## Notas y recomendaciones

- El sistema de permisos está gestionado automáticamente.
- Puedes personalizar completamente la UI de confirmación vía `PermissionAndConfirmationConfig`.
- Los textos están internacionalizados automáticamente según el idioma del dispositivo.
- El flash solo funciona en modos de calidad `BALANCED` o `QUALITY`.
- El selector inteligente de Android elige automáticamente entre galería nativa y explorador de archivos según los tipos MIME configurados.
- Usa `redactGpsData = true` (por defecto) para privacidad del usuario.
- Usa `reset()` para limpiar el estado y permitir nuevas capturas/selecciones.
