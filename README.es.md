This document is also available in English: [README.md](README.md)

# ImagePickerKMP

[![CI](https://github.com/ismoy/ImagePickerKMP/workflows/CI/badge.svg)](https://github.com/ismoy/ImagePickerKMP/actions)
[![Code Coverage](https://codecov.io/gh/ismoy/ImagePickerKMP/branch/main/graph/badge.svg)](https://codecov.io/gh/ismoy/ImagePickerKMP)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-lightgrey.svg)](https://kotlinlang.org/docs/multiplatform.html)

Una librería moderna y multiplataforma para selección de imágenes y cámara en Kotlin Multiplatform (KMP), con experiencia nativa en Android e iOS.

## Características

- 📱 **Multiplataforma**: Funciona en Android y iOS
- 📸 **Integración de cámara**: Acceso directo a la cámara y captura de fotos
- 🎨 **UI personalizable**: Diálogos y vistas de confirmación personalizables
- 🔒 **Gestión de permisos**: Manejo inteligente de permisos en ambas plataformas
- 🎯 **Fácil integración**: API simple con Compose Multiplatform
- 🔧 **Altamente configurable**: Muchas opciones de personalización

## Instalación

Agrega la dependencia en tu `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.0")
}
```

## Uso básico

```kotlin
@Composable
fun MiImagePicker() {
    var mostrarPicker by remember { mutableStateOf(false) }
    
    if (mostrarPicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                // Manejar la foto capturada
                println("Foto capturada: ${result.uri}")
                mostrarPicker = false
            },
            onError = { exception ->
                // Manejar errores
                println("Error: ${exception.message}")
                mostrarPicker = false
            }
        )
    }
    
    Button(onClick = { mostrarPicker = true }) {
        Text("Tomar foto")
    }
}
```

## Compatibilidad

| Plataforma | Versión mínima |
|------------|----------------|
| Android    | API 21+        |
| iOS        | iOS 12.0+      |

## Requisitos

### Android
- SDK mínimo: 21
- Kotlin 1.8+
- Compose Multiplatform

### iOS
- iOS 12.0+
- Xcode 14+
- Kotlin Multiplatform

## Funcionalidades detalladas

### Integración de cámara
- Acceso directo a la cámara
- Captura de fotos con previsualización
- Diálogos de confirmación personalizables
- Procesamiento y optimización de imágenes

### Manejo de permisos
- Gestión inteligente de permisos
- Comportamiento específico por plataforma
- Diálogos de permisos personalizables
- Navegación a ajustes

### Personalización
- Temas de UI personalizados
- Diálogos personalizados
- Callbacks personalizados
- Configuraciones avanzadas

### Internacionalización (i18n)
- Soporte multilenguaje (inglés, español, francés)
- Detección automática de idioma
- Recursos de strings type-safe
- Sin dependencias externas
- **Traducción automática**: Los diálogos y textos se traducen automáticamente

## Selección de galería y personalización de diálogos en iOS

- Los usuarios pueden seleccionar imágenes de la galería en Android e iOS.
- En Android, aparece un icono de galería en la UI de la cámara (abajo a la izquierda). En iOS, puedes mostrar un diálogo para elegir entre cámara y galería.

### Personalización de textos de diálogo en iOS

Puedes personalizar los textos del diálogo (título, tomar foto, seleccionar de galería, cancelar) en iOS:

```kotlin
ImagePickerLauncher(
    context = ..., // contexto de la plataforma
    onPhotoCaptured = { result -> /* ... */ },
    onError = { exception -> /* ... */ },
    dialogTitle = "Elegir acción", // Solo iOS
    takePhotoText = "Cámara",      // Solo iOS
    selectFromGalleryText = "Galería", // Solo iOS
    cancelText = "Cancelar"         // Solo iOS
)
```

- En Android, estos parámetros se ignoran.
- En iOS, si no se proveen, los textos estarán en inglés por defecto.

Consulta la [Guía de Integración](INTEGRATION_GUIDE.es.md) y [Ejemplos](EXAMPLES.es.md) para más detalles.

## Documentación

- [Guía de Integración](INTEGRATION_GUIDE.es.md)
- [Guía de Personalización](CUSTOMIZATION_GUIDE.es.md)
- [Guía de Internacionalización](I18N_GUIDE.es.md)
- [Guía de Permisos](PERMISSION.es.md)
- [Guía de Cobertura](COVERAGE_GUIDE.es.md)
- [Configuración de Notificaciones](NOTIFICATIONS_SETUP.es.md) - Configuración de notificaciones de Discord
- [Referencia de API](API_REFERENCE.es.md)
- [Ejemplos](EXAMPLES.es.md)

## Troubleshooting (Solución de problemas)

### Problema: El flash no se activa
- Asegúrate de no usar el modo ZERO_SHUTTER_LAG si quieres flash. Usa los modos "Balanced" o "High Quality".

### Problema: No cambia a la cámara frontal
- Verifica que el dispositivo tenga cámara frontal y permisos de cámara activos.

### Problema: Los textos no aparecen en el idioma deseado
- Usa los parámetros `questionText`, `retryText`, `acceptText` o pasa tu propio `customConfirmationView`.

### Problema: Permisos de cámara
- Asegúrate de declarar los permisos en el AndroidManifest y de solicitarlos correctamente en la app.

### Problema: Personalización de UI
- Usa el parámetro `customConfirmationView` para reemplazar toda la UI de confirmación por tu propio Composable.

## Tabla de props principales (`ImagePickerLauncher`)

| Propiedad                | Tipo                                      | Default                | Descripción                                                                 |
|--------------------------|-------------------------------------------|------------------------|-----------------------------------------------------------------------------|
| `context`                | `Any?`                                   | -                      | Contexto de la Activity/UIViewController                                    |
| `onPhotoCaptured`        | `(PhotoResult) -> Unit`                   | -                      | Callback cuando se toma una foto con la cámara                              |
| `onPhotosSelected`       | `(List<PhotoResult>) -> Unit`             | -                      | Callback cuando se seleccionan imágenes de la galería                       |
| `onError`                | `(Exception) -> Unit`                     | -                      | Callback para errores                                                       |
| `preference`             | `CapturePhotoPreference?`                 | `FAST`                 | Preferencia de calidad/velocidad de captura                                 |
| `dialogTitle`            | `String`                                  | "Seleccionar imagen"  | Título del diálogo inicial                                                  |
| `takePhotoText`          | `String`                                  | "Tomar foto"           | Texto para la opción de cámara                                              |
| `selectFromGalleryText`  | `String`                                  | "Seleccionar de galería"| Texto para la opción de galería                                             |
| `cancelText`             | `String`                                  | "Cancelar"              | Texto para cancelar                                                         |
| `allowMultiple`          | `Boolean`                                 | `false`                 | Permite seleccionar varias imágenes de la galería                           |
| `mimeTypes`              | `List<String>`                            | `["image/*"]`          | Tipos MIME permitidos en la galería                                         |
| `buttonColor`            | `Color?`                                  | `null`                  | Color de fondo de los botones                                               |
| `iconColor`              | `Color?`                                  | `null`                  | Color de los iconos                                                         |
| `buttonSize`             | `Dp?`                                     | `null`                  | Tamaño de los botones                                                       |
| `layoutPosition`         | `String?`                                 | `null`                  | Personalización de layout                                                   |
| `flashIcon`              | `ImageVector?`                            | `null`                  | Icono personalizado para el flash                                           |
| `switchCameraIcon`       | `ImageVector?`                            | `null`                  | Icono personalizado para cambiar cámara                                     |
| `captureIcon`            | `ImageVector?`                            | `null`                  | Icono personalizado para capturar                                           |
| `galleryIcon`            | `ImageVector?`                            | `null`                  | Icono personalizado para galería                                            |
| ...                      | ...                                       | ...                    | ...                                                                         |

> Consulta la [API_REFERENCE.es.md](API_REFERENCE.es.md) para la lista completa y detalles avanzados.

## Guía de migración

### Cambios importantes

- **Selección múltiple**: El parámetro `onPhotoSelected` fue reemplazado por `onPhotosSelected: (List<PhotoResult>) -> Unit`.
    - Ahora, siempre recibirás una lista, aunque solo se seleccione una imagen.
    - Si usas selección simple, toma el primer elemento de la lista.
- **Firma multiplataforma**: La API es coherente en Android/iOS. El callback de galería siempre es una lista.
- **Personalización**: Se han añadido más props para personalizar colores, iconos y layout.

### ¿Cómo migrar?

- Cambia tu callback de galería:
    ```kotlin
    // Antes
    GalleryPickerLauncher(
        context = ...,
        onPhotoSelected = { result -> ... },
        ...
    )
    // Ahora
    GalleryPickerLauncher(
        context = ...,
        onPhotosSelected = { results -> /* results es List<PhotoResult> */ },
        ...
    )
    ```
- Si usas `ImagePickerLauncher`, agrega el parámetro `onPhotosSelected` y adapta tu lógica para listas.
- Consulta los ejemplos en [EXAMPLES.es.md](EXAMPLES.es.md).

---

**Hecho con ❤️ para la comunidad Kotlin Multiplatform** 