[![CI](https://github.com/ismoy/ImagePickerKMP/workflows/CI/badge.svg)](https://github.com/ismoy/ImagePickerKMP/actions)
[![Code Coverage](https://codecov.io/gh/ismoy/ImagePickerKMP/branch/main/graph/badge.svg)](https://codecov.io/gh/ismoy/ImagePickerKMP)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-lightgrey.svg)](https://kotlinlang.org/docs/multiplatform.html)

[![official project](http://jb.gg/badges/official.svg)](https://github.com/JetBrains#jetbrains-on-github)

# ImagePickerKMP

A modern, cross-platform image picker library for Kotlin Multiplatform (KMP) that provides a seamless camera experience on both Android and iOS platforms.

Este documento también está disponible en español: [README.es.md](README.es.md)

## Features

- 📱 **Cross-platform**: Works on Android and iOS
- 📸 **Camera Integration**: Direct camera access with photo capture
- 🎨 **Customizable UI**: Custom dialogs and confirmation views
- 🔒 **Permission Handling**: Smart permission management for both platforms
- 🎯 **Easy Integration**: Simple API with Compose Multiplatform
- 🔧 **Highly Configurable**: Extensive customization options

## Quick Start

### Installation

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.0")
}
```

### Basic Usage

```kotlin
@Composable
fun MyImagePicker() {
    var showImagePicker by remember { mutableStateOf(false) }
    
    if (showImagePicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                // Handle captured photo
                println("Photo captured: ${result.uri}")
                showImagePicker = false
            },
            onError = { exception ->
                // Handle errors
                println("Error: ${exception.message}")
                showImagePicker = false
            }
        )
    }
    
    Button(onClick = { showImagePicker = true }) {
        Text("Take Photo")
    }
}
```

## Platform Support

| Platform | Minimum Version | Status |
|----------|----------------|--------|
| Android  | API 21+        | ✅     |
| iOS      | iOS 12.0+      | ✅     |

## Requirements

### Android
- Minimum SDK: 21
- Kotlin 1.8+
- Compose Multiplatform

### iOS
- iOS 12.0+
- Xcode 14+
- Kotlin Multiplatform

## Features in Detail

### Camera Integration
- Direct camera access
- Photo capture with preview
- Custom confirmation dialogs
- Image processing and optimization

### Permission Handling
- Smart permission management
- Platform-specific behavior
- Custom permission dialogs
- Settings navigation

### Customization
- Custom UI themes
- Personalized dialogs
- Custom callbacks
- Advanced configurations

### Internationalization (i18n)
- Multi-language support (English, Spanish, French)
- Automatic language detection
- Type-safe string resources
- No external dependencies
- **Automatic translations**: Permission dialogs and UI texts are automatically translated

## Gallery Selection & iOS Dialog Customization

### Multiplatform Gallery Support

- Users can select images from the gallery on both Android and iOS.
- On Android, a gallery icon appears in the camera UI (bottom left). On iOS, you can offer a dialog to choose between camera and gallery.

### iOS Dialog Text Customization

You can customize the dialog texts (title, take photo, select from gallery, cancel) on iOS:

```kotlin
ImagePickerLauncher(
    context = ..., // platform context
    onPhotoCaptured = { result -> /* ... */ },
    onError = { exception -> /* ... */ },
    dialogTitle = "Choose action", // iOS only
    takePhotoText = "Camera",      // iOS only
    selectFromGalleryText = "Gallery", // iOS only
    cancelText = "Dismiss"         // iOS only
)
```

- On Android, these parameters are ignored.
- On iOS, if not provided, defaults are in English.

See the [Integration Guide](INTEGRATION_GUIDE.md) and [Examples](EXAMPLES.md) for more details.

## Documentation

- [Integration Guide](INTEGRATION_GUIDE.md) - Complete setup and integration guide
- [Customization Guide](CUSTOMIZATION_GUIDE.md) - UI and behavior customization
- [Internationalization Guide](I18N_GUIDE.md) - Multi-language support guide
- [Permission Guide](PERMISSION.md) - Permission handling details
- [Coverage Guide](COVERAGE_GUIDE.md) - Code coverage and testing guide
- [Notifications Setup](NOTIFICATIONS_SETUP.md) - Discord notifications configuration
- [API Reference](API_REFERENCE.md) - Complete API documentation
- [Examples](EXAMPLES.md) - Code examples and use cases

## Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- 📧 Email: support@imagepickerkmp.com
- 🐛 Issues: [GitHub Issues](https://github.com/ismoy/ImagePickerKMP/issues)
- 📖 Documentation: [Wiki](https://github.com/ismoy/ImagePickerKMP/wiki)

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for a complete list of changes and updates.

---

**Made with ❤️ for the Kotlin Multiplatform community**

# Next steps
- Share your library with the Kotlin Community in the `#feed` channel in the [Kotlin Slack](https://kotlinlang.slack.com/) (To sign up visit https://kotl.in/slack.)
- Add [shield.io badges](https://shields.io/badges/maven-central-version) to your README.
- Create a documentation site for your project using [Writerside](https://www.jetbrains.com/writerside/). 
- Share API documentation for your project using [Dokka](https://kotl.in/dokka).
- Add [Renovate](https://docs.renovatebot.com/) to automatically update dependencies.

# Other resources
* [Publishing via the Central Portal](https://central.sonatype.org/publish-ea/publish-ea-guide/)
* [Gradle Maven Publish Plugin \- Publishing to Maven Central](https://vanniktech.github.io/gradle-maven-publish-plugin/central/)

# ImagePickerKMP – Android Modern Camera Features

## ✨ Nuevas funcionalidades

### 1. Control de Flash Moderno
- Botón en la parte superior central para alternar entre modos de flash: **Auto**, **On**, **Off**.
- Iconos visuales (rayo, rayo con A, rayo tachado) para cada modo.
- El flash se activa correctamente al capturar la foto (no solo linterna).

### 2. Cambio de Cámara Frontal/Trasera
- Botón circular en la parte inferior derecha para alternar entre cámara trasera y frontal.
- Cambio instantáneo de la vista previa.

### 3. Vista de Confirmación Moderna y Personalizable
- Tarjeta elegante con la foto capturada, fondo oscuro, esquinas redondeadas.
- Icono decorativo de HD/SD según la resolución.
- Texto de pregunta y botones grandes con iconos (Retry/Accept).
- **Totalmente personalizable**: puedes pasar tu propio Composable para la confirmación.
- Textos por defecto en inglés, pero puedes personalizarlos fácilmente.

---

## 🚀 Ejemplo de uso básico

```kotlin
ImagePickerLauncher(
    context = context,
    onPhotoCaptured = { result ->
        // Handle photo result
    },
    onError = { exception ->
        // Handle error
    }
)
```

---

## 🛠️ Ejemplo de uso avanzado: Personalización de la confirmación

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

## ⚙️ Parámetros relevantes

- **customConfirmationView**: Permite reemplazar la UI de confirmación por cualquier Composable propio.
- **questionText, retryText, acceptText**: Textos personalizables para la confirmación.
- **Control de flash**: El usuario puede alternar el modo de flash en la UI.
- **Cambio de cámara**: El usuario puede alternar entre cámara trasera y frontal en la UI.

---

## 📋 Notas de integración
- El sistema de permisos y la inicialización de la cámara están gestionados automáticamente.
- El diseño es responsivo y moderno, pero puedes personalizar cualquier aspecto visual.
- Los textos por defecto están en inglés, pero puedes localizarlos fácilmente.
- Si necesitas aún más control, puedes pasar tu propio Composable a `customConfirmationView`.

---

## 🧑‍💻 ¿Preguntas o feedback?
¡No dudes en abrir un issue o PR para sugerencias y mejoras!

---

## 🖼️ Ejemplo visual de la experiencia

```
┌───────────────────────────────┐
│         [Flash Icon]         │
│ ┌───────────────────────────┐ │
│ │      Foto capturada       │ │
│ │      (esquinas redondeadas)│ │
│ └───────────────────────────┘ │
│   [HD/SD Icon]               │
│                             │
│  Are you satisfied with the photo? │
│                             │
│ [⟳ Retry]   [✔ Accept]      │
│                             │
│         [Switch Camera]     │
└───────────────────────────────┘
```

- El usuario puede alternar flash, cambiar cámara, y confirmar o reintentar la foto.
- Todos los textos y botones son personalizables.

---

## 🛠 Troubleshooting (Solución de problemas)

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

---

## Ejemplos visuales

> **¡Contribuye!** Puedes agregar capturas de pantalla o GIFs mostrando:
> - Selección de una imagen
> - Selección múltiple desde galería
> - Pantalla de confirmación
> - Personalización de colores/iconos
>
> Ejemplo:
>
> ![Demo selección múltiple](images/demo_multiple_selection.gif)
>
> Para agregar tus propios ejemplos, sube las imágenes/GIFs a la carpeta `images/` y enlázalos aquí.

---

## Tabla de props/argumentos principales (`ImagePickerLauncher`)

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

> Consulta la [API_REFERENCE.md](API_REFERENCE.md) para la lista completa y detalles avanzados.

---

## Guía de migración (v2.x → v3.x)

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
- Consulta los ejemplos en [EXAMPLES.md](EXAMPLES.md).

---
