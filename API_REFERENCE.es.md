Este documento también está disponible en inglés: [API_REFERENCE.md](API_REFERENCE.md)

# Referencia de API

Documentación completa de la API para la librería ImagePickerKMP.

## Tabla de Contenidos

- [Componentes principales](#componentes-principales)
- [Clases de datos](#clases-de-datos)
- [Enums](#enums)
- [Excepciones](#excepciones)
- [Configuración](#configuración)
- [APIs específicas de plataforma](#apis-específicas-de-plataforma)

## 📸 Captura de Foto – Documentación Específica

## Descripción
La funcionalidad de captura de foto en ImagePickerKMP permite a los desarrolladores integrar una experiencia de cámara moderna, personalizable y multiplataforma en sus aplicaciones. Incluye control de flash, cambio de cámara, vista previa, confirmación y personalización total de la UI.

---

## Ejemplo básico de captura de foto

```kotlin
ImagePickerLauncher(
    context = context,
    onPhotoCaptured = { result ->
        // Aquí recibes el resultado de la foto (uri, width, height)
    },
    onError = { exception ->
        // Manejo de errores
    }
)
```

---

## Ejemplo avanzado: Personalización de la confirmación

```kotlin
ImagePickerLauncher(
    context = context,
    onPhotoCaptured = { result -> /* ... */ },
    onError = { exception -> /* ... */ },
    customConfirmationView = { result, onConfirm, onRetry ->
        ImageConfirmationViewWithCustomButtons(
            result = result,
            onConfirm = onConfirm,
            onRetry = onRetry,
            questionText = "¿Estás satisfecho con la foto?",
            retryText = "Reintentar",
            acceptText = "Aceptar"
        )
    }
)
```

---

## Parámetros relevantes

- **context**: Contexto de Android (usualmente `LocalContext.current`)
- **onPhotoCaptured**: Callback con el resultado de la foto (`PhotoResult`)
- **onError**: Callback para manejar errores
- **preference**: Preferencia de calidad de la foto (`CapturePhotoPreference.FAST`, `BALANCED`, `QUALITY`)
- **customConfirmationView**: Composable para personalizar la UI de confirmación
- **questionText, retryText, acceptText**: Textos personalizables para la confirmación

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

## Ejemplo básico de selección de imagen

```kotlin
ImagePickerLauncher(
    context = context,
    onPhotoCaptured = { result ->
        // Aquí recibes el resultado de la imagen seleccionada (uri, width, height)
    },
    onError = { exception ->
        // Manejo de errores
    },
    // No es necesario customConfirmationView si solo quieres la galería estándar
)
```

---

## Ejemplo avanzado: Selección múltiple y confirmación personalizada

```kotlin
ImagePickerLauncher(
    context = context,
    onPhotoCaptured = { result -> /* ... */ },
    onError = { exception -> /* ... */ },
    customConfirmationView = { result, onConfirm, onRetry ->
        MyCustomGalleryConfirmation(
            result = result,
            onConfirm = onConfirm,
            onRetry = onRetry,
            questionText = "¿Quieres usar esta imagen?",
            retryText = "Elegir otra",
            acceptText = "Usar imagen"
        )
    },
    // Puedes agregar parámetros para selección múltiple o filtros si tu implementación lo soporta
)
```

---

## Parámetros relevantes

- **context**: Contexto de Android (usualmente `LocalContext.current`)
- **onPhotoCaptured**: Callback con el resultado de la imagen seleccionada (`PhotoResult`)
- **onError**: Callback para manejar errores
- **customConfirmationView**: Composable para personalizar la UI de confirmación
- **questionText, retryText, acceptText**: Textos personalizables para la confirmación
- **allowMultiple**: Permite seleccionar varias imágenes (si está soportado)
- **mimeTypes**: Lista de tipos MIME permitidos (por ejemplo, `listOf("image/*")`)

---

## Experiencia de usuario
- **Selector de galería**: El usuario puede elegir una imagen (o varias, si está habilitado).
- **Confirmación**: Vista moderna para aceptar o reintentar la selección, con textos e iconos personalizables.

---

## Notas y recomendaciones
- El sistema de permisos está gestionado automáticamente.
- Puedes personalizar completamente la UI de confirmación.
- Los textos por defecto están en inglés, pero puedes localizarlos fácilmente.
- Si necesitas aún más control, implementa tu propio `customConfirmationView`.
- La selección múltiple depende del soporte de la plataforma y la implementación.

---

## Referencias de código en el proyecto
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/GalleryPickerLauncher.android.kt**: Lógica de selección de imágenes en Android.
- **library/src/commonMain/kotlin/io/github/ismoy/imagepickerkmp/GalleryPickerLauncher.kt**: Abstracción multiplataforma para la galería.
- **library/src/androidMain/kotlin/io/github/ismoy/imagepickerkmp/ImageConfirmationViewWithCustomButtons.kt**: UI de confirmación de imagen.

---

## Componentes principales

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

---

## Clases de datos

### PhotoResult

Representa el resultado de una captura de foto o selección de imagen.

```kotlin
data class PhotoResult(
    val uri: Uri,
    val width: Int,
    val height: Int
)
```

### CapturePhotoPreference

Representa las preferencias de captura de foto.

```kotlin
data class CapturePhotoPreference(
    val quality: Quality,
    val preference: Preference
)
```

### Quality

Representa la calidad de la foto.

```kotlin
enum class Quality {
    FAST,
    BALANCED,
    QUALITY
}
```

### Preference

Representa la preferencia de la foto.

```kotlin
enum class Preference {
    LOW,
    MEDIUM,
    HIGH
}
```

### PermissionConfig

Representa la configuración de permisos.

```kotlin
data class PermissionConfig(
    val permission: String,
    val isGranted: Boolean
)
```

### Quality

Representa la calidad de la foto.

```kotlin
enum class Quality {
    FAST,
    BALANCED,
    QUALITY
}
```

### Preference

Representa la preferencia de la foto.

```kotlin
enum class Preference {
    LOW,
    MEDIUM,
    HIGH
}
```

---

## Enums

### Quality

Representa la calidad de la foto.

```kotlin
enum class Quality {
    FAST,
    BALANCED,
    QUALITY
}
```

### Preference

Representa la preferencia de la foto.

```kotlin
enum class Preference {
    LOW,
    MEDIUM,
    HIGH
}
```

### PermissionConfig

Representa la configuración de permisos.

```kotlin
data class PermissionConfig(
    val permission: String,
    val isGranted: Boolean
)
```

---

## Excepciones

### ImagePickerException

Representa una excepción específica de ImagePickerKMP.

```kotlin
class ImagePickerException(message: String) : Exception(message)
```

---

## Configuración

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

---

## APIs específicas de plataforma

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento
- `descriptionDialogDenied: String` - Descripción para el diálogo de reintento
- `btnDialogDenied: String` - Texto del botón para el diálogo de reintento
- `customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?` - Diálogo de reintento personalizado

### ImagePickerConfig

Representa la configuración general de ImagePickerKMP.

```kotlin
data class ImagePickerConfig(
    val imagePickerLauncher: ImagePickerLauncher,
    val requestCameraPermission: RequestCameraPermission
)
```

### ImagePickerLauncher

La función composable principal para lanzar el selector de imágenes.

```kotlin
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null
)
```

#### Parámetros

- `context: Any?` - Contexto para Android, null para iOS
- `onPhotoCaptured: (PhotoResult) -> Unit` - Callback cuando se captura una foto
- `onError: (Exception) -> Unit` - Callback cuando ocurre un error
- `customPermissionHandler: ((PermissionConfig) -> Unit)?` - Manejo personalizado de permisos
- `customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?` - Vista de confirmación personalizada
- `preference: CapturePhotoPreference?` - Preferencias de captura de foto

#### Ejemplo

```kotlin
@Composable
fun MyImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            println("Photo captured: ${result.uri}")
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        }
    )
}
```

### RequestCameraPermission

Composable para gestionar permisos de cámara.

```kotlin
@Composable
fun RequestCameraPermission(
    titleDialogConfig: String,
    descriptionDialogConfig: String,
    btnDialogConfig: String,
    titleDialogDenied: String,
    descriptionDialogDenied: String,
    btnDialogDenied: String,
    customDeniedDialog: @Composable ((onRetry: () -> Unit) -> Unit)?,
    customSettingsDialog: @Composable ((onOpenSettings: () -> Unit) -> Unit)?,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
```

#### Parámetros

- `titleDialogConfig: String` - Título para el diálogo de ajustes
- `descriptionDialogConfig: String` - Descripción para el diálogo de ajustes
- `btnDialogConfig: String` - Texto del botón para el diálogo de ajustes
- `titleDialogDenied: String` - Título para el diálogo de reintento