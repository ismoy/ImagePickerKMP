<p align="center">
  <a href="https://github.com/ismoy/ImagePickerKMP/actions"><img src="https://github.com/ismoy/ImagePickerKMP/workflows/CI/badge.svg" alt="CI"></a>
  <a href="https://codecov.io/gh/ismoy/ImagePickerKMP"><img src="https://codecov.io/gh/ismoy/ImagePickerKMP/branch/main/graph/badge.svg" alt="Code Coverage"></a>
  <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License"></a>
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-1.9.0-blue.svg" alt="Kotlin"></a>
</p>

<p align="center">
  <a href="https://kotlinlang.org/docs/multiplatform.html"><img src="https://img.shields.io/badge/Platform-Android%20%7C%20iOS-lightgrey.svg" alt="Platform"></a>
  <a href="https://search.maven.org/artifact/io.github.ismoy/imagepickerkmp"><img src="https://img.shields.io/maven-central/v/io.github.ismoy/imagepickerkmp.svg?label=Maven%20Central" alt="Maven Central"></a>
  <a href="https://github.com/ismoy/ImagePickerKMP/releases"><img src="https://img.shields.io/github/v/release/ismoy/ImagePickerKMP?label=GitHub%20Release" alt="GitHub Release"></a>
  <a href="https://github.com/ismoy/ImagePickerKMP/stargazers"><img src="https://img.shields.io/github/stars/ismoy/ImagePickerKMP?style=social" alt="GitHub Repo stars"></a>
</p>

<p align="center">
  <a href="https://github.com/ismoy/ImagePickerKMP/commits/main"><img src="https://img.shields.io/github/last-commit/ismoy/ImagePickerKMP" alt="GitHub last commit"></a>
  <a href="https://github.com/ismoy/ImagePickerKMP/pulls"><img src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square" alt="PRs Welcome"></a>
  <a href="https://discord.gg/EjSQTeyh"><img src="https://img.shields.io/discord/1393705692484993114.svg?label=Discord&logo=discord&color=7289da" alt="Discord"></a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Compose%20Multiplatform-green" alt="Compose Multiplatform">
  <img src="https://img.shields.io/badge/Platform-Android-green" alt="Android">
  <img src="https://img.shields.io/badge/Platform-iOS-blue" alt="iOS">
  <img src="https://img.shields.io/codecov/c/github/ismoy/ImagePickerKMP" alt="Coverage Status">
  <a href="https://github.com/ismoy/ImagePickerKMP/actions/workflows/detekt.yml"><img src="https://github.com/ismoy/ImagePickerKMP/actions/workflows/detekt.yml/badge.svg?branch=main" alt="Detekt"></a>
</p>

## Demo

<video width="100%" autoplay loop muted playsinline>
  <source src="https://user-images.githubusercontent.com/ismoy/ImagePickerKMP/main/demo/demo.mp4" type="video/mp4">
  Tu navegador no soporta el elemento de video.
</video>

*¡Mira el demo de arriba para ver ImagePickerKMP en acción - captura de cámara, selección de galería y UI personalizada en funcionamiento!*

### Funcionalidades Mostradas en el Demo:
- ** Captura de Cámara**: Acceso directo a la cámara con control de flash
- ** Cambio de Cámara**: Alternancia fluida entre cámara frontal y trasera
- ** UI Personalizada**: Diálogos de confirmación personalizados
- ** Selección de Galería**: Selección múltiple de imágenes desde la galería
- ** Rendimiento**: Interacciones suaves y responsivas
- ** Permisos**: Manejo inteligente de permisos

<h1 align="center">ImagePickerKMP</h1>

<p align="center">
  <a href="https://search.maven.org/search?q=g:%22io.github.ismoy%22%20AND%20a:%22imagepickerkmp%22"><img src="https://img.shields.io/maven-central/v/io.github.ismoy/imagepickerkmp.svg?label=Maven%20Central" alt="Maven Central"></a>
  <a href="http://kotlinlang.org"><img src="https://img.shields.io/badge/kotlin-1.9.20-blue.svg?logo=kotlin" alt="Kotlin"></a>
  <a href="https://github.com/JetBrains/compose-multiplatform"><img src="https://img.shields.io/badge/Compose%20Multiplatform-1.5.11-blue" alt="Compose Multiplatform"></a>
  <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License: MIT"></a>
</p>

<p align="center">
  Una librería moderna de Kotlin Multiplatform para captura de imágenes y selección de galería, diseñada específicamente para <strong>Compose Multiplatform</strong>. Proporciona una API unificada y nativa para Android e iOS con personalización completa de UI.
</p>

---

<h2 align="center"> Acerca de ImagePickerKMP</h2>

<p align="center">
   <strong>Soporte Multiplataforma</strong>: Android e iOS con una sola API<br>
   <strong>Integración de Cámara</strong>: Captura directa con vista previa<br>
   <strong>Selección de Galería</strong>: Simple y múltiple selección<br>
   <strong>UI Personalizable</strong>: Diálogos y vistas completamente personalizables<br>
   <strong>Manejo de Permisos</strong>: Gestión automática e inteligente
</p>

---

##  Características

-  **Soporte Nativo Multiplataforma**: Android e iOS con una sola API
-  **Captura de Cámara**: Acceso directo a la cámara con vista previa
-  **Selección de Galería**: Selección simple y múltiple de imágenes
-  **UI Completamente Personalizable**: Diálogos, vistas de confirmación y UI de cámara personalizables
-  **Manejo Inteligente de Permisos**: Gestión automática de permisos con alternativas elegantes
-  **Optimizado para Rendimiento**: Procesamiento eficiente de imágenes
-  **API Simple**: Fácil integración con manejo integral de errores
-  **Soporte de Internacionalización**: Textos personalizables para múltiples idiomas

##  Instalación

### Gradle (Kotlin DSL)

```kotlin
commonMain {
    dependencies {
        implementation("io.github.ismoy:imagepickerkmp:1.0.2")
    }
}
```

### Gradle (Groovy)

```groovy
commonMain {
    dependencies {
        implementation 'io.github.ismoy:imagepickerkmp:1.0.2'
    }
}
```

<h2 align="center"> Inicio Rápido</h2>

### Captura de Cámara

```kotlin
@Composable
fun MiCapturaCamara() {
    var mostrarCamara by remember { mutableStateOf(false) }
    var fotoCapturada by remember { mutableStateOf<CameraPhotoHandler.PhotoResult?>(null) }

    if (mostrarCamara) {
        ImagePickerLauncher(
            config = ImagePickerConfig(
                onPhotoCaptured = { result ->
                    fotoCapturada = result
                    mostrarCamara = false
                },
                onError = {
                    mostrarCamara = false
                },
                onDismiss = {
                    mostrarCamara = false // Resetear estado cuando el usuario no selecciona nada
                },
                // Diálogo personalizado solo iOS opcional
                customPickerDialog = { onTakePhoto, onSelectFromGallery, onCancel ->
                    MiBottomSheetPersonalizado(
                        onTakePhoto = onTakePhoto,
                        onSelectFromGallery = onSelectFromGallery,
                        onDismiss = {
                            onCancel()
                            mostrarCamara = false
                        }
                    )
                },
                // Solo Android opcional
                cameraCaptureConfig = CameraCaptureConfig(
                    permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                        customConfirmationView = { photoResult, onConfirm, onRetry ->
                            TuVistaConfirmacionPersonalizada(
                                result = photoResult,
                                onConfirm = onConfirm,
                                onRetry = onRetry
                            )
                        }
                    )
                )
            )
        )
    }

    Button(onClick = { mostrarCamara = true }) {
        Text("Tomar Foto")
    }
}
```

### Selección de Galería

```kotlin
@Composable
fun MiSelectorGaleria() {
    var mostrarGaleria by remember { mutableStateOf(false) }
    var imagenesSeleccionadas by remember { mutableStateOf<List<GalleryPhotoHandler.PhotoResult>>(emptyList()) }

    if (mostrarGaleria) {
        GalleryPickerLauncher(
            onPhotosSelected = { fotos ->
                imagenesSeleccionadas = fotos
                mostrarGaleria = false
            },
            onError = { error ->
                mostrarGaleria = false
            },
            onDismiss = { 
                println("Usuario canceló o cerró el selector")
                mostrarGaleria = false // Resetear estado cuando el usuario no selecciona nada
            },
            allowMultiple = true, // False para selección simple
            mimeTypes = listOf("image/jpeg", "image/png"), // Opcional: filtrar por tipo
            // Solo Android opcional
            cameraCaptureConfig = CameraCaptureConfig(
                permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                    customConfirmationView = { photoResult, onConfirm, onRetry ->
                        TuVistaConfirmacionPersonalizada(
                            result = photoResult,
                            onConfirm = onConfirm,
                            onRetry = onRetry
                        )
                    }
                )
            )
        )
    }

    Button(onClick = { mostrarGaleria = true }) {
        Text("Elegir de la Galería")
    }
}
```
Para más personalización (vistas de confirmación, filtrado MIME, etc.), [consulta la guía de integración para KMP.](https://github.com/ismoy/ImagePickerKMP/blob/main/docs/INTEGRATION_GUIDE.es.md#kotlin-multiplatform--compose-multiplatform)

### Uso de ImagePickerKMP en Android Nativo (Jetpack Compose)
<p>Incluso si no estás usando KMP, puedes usar ImagePickerKMP en proyectos Android puros con Jetpack Compose.</p>

#### Paso 1: Agregar la dependencia
```kotlin
implementation("io.github.ismoy:imagepickerkmp:1.0.2")
```
#### Paso 2: Ejemplo de Lanzador de Cámara
```kotlin
var mostrarCamara by remember { mutableStateOf(false) }
var fotoCapturada by remember { mutableStateOf<CameraPhotoHandler.PhotoResult?>(null) }
```
```kotlin
if (mostrarCamara) {
    ImagePickerLauncher(
        config = ImagePickerConfig(
            onPhotoCaptured = { result ->
                fotoCapturada = result
                mostrarCamara = false
            },
            onError = {
                mostrarCamara = false
            },
            onDismiss = {
                mostrarImagePicker = false // Resetear estado cuando el usuario no selecciona nada
            },
            // Diálogo personalizado solo iOS opcional
            customPickerDialog = { onTakePhoto, onSelectFromGallery, onCancel ->
                MiBottomSheetPersonalizado(
                    onTakePhoto = onTakePhoto,
                    onSelectFromGallery = onSelectFromGallery,
                    onDismiss = {
                        isPickerSheetVisible = false
                        onCancel()
                        mostrarCameraPicker = false
                    }
                )
            },
            // Solo Android opcional
            cameraCaptureConfig = CameraCaptureConfig(
                permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                    customConfirmationView = { photoResult, onConfirm, onRetry ->
                        TuVistaConfirmacionPersonalizada(
                            result = photoResult,
                            onConfirm = onConfirm,
                            onRetry = onRetry
                        )
                    },
                    // Opcional: manejador de permisos personalizado
                    customPermissionHandler = { config ->
                        // Manejar permisos aquí
                    }
                )
            )
        )
    )
}

Button(onClick = { mostrarCamara = true }) {
    Text("Tomar Foto")
}
```

#### Paso 3: Ejemplo de Selector de Galería
```kotlin
var mostrarGaleria by remember { mutableStateOf(false) }
var imagenesSeleccionadas by remember { mutableStateOf<List<GalleryPhotoHandler.PhotoResult>>(emptyList()) }
```
```kotlin
if (mostrarGaleria) {
    GalleryPickerLauncher(
        onPhotosSelected = { fotos ->
            imagenesSeleccionadas = fotos
            mostrarGaleria = false
        },
        onError = { error ->
            mostrarGaleria = false
        },
        onDismiss = {
            mostrarCamara = false
           },
        allowMultiple = true, // False para selección simple
        mimeTypes = listOf("image/jpeg", "image/png"), // Opcional: filtrar por tipo
        // Solo Android opcional
        cameraCaptureConfig = CameraCaptureConfig(
            permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                customConfirmationView = { photoResult, onConfirm, onRetry ->
                    TuVistaConfirmacionPersonalizada(
                        result = photoResult,
                        onConfirm = onConfirm,
                        onRetry = onRetry
                    )
                }
            )
        )
    )
}

Button(onClick = { mostrarGaleria = true }) {
    Text("Elegir de la Galería")
}
```
Consulta la [guía de integración nativa de Android](https://github.com/ismoy/ImagePickerKMP/blob/main/docs/INTEGRATION_GUIDE.es.md#android-nativo-jetpack-compose) para más detalles de uso.

- En **Android**, el usuario verá el selector de galería del sistema.
- En **iOS**, se usa el selector nativo de galería. En iOS 14+ se soporta selección múltiple. El sistema gestiona permisos y acceso limitado de forma nativa.
- El callback `onPhotosSelected` siempre recibe una lista, incluso para selección simple.
- Puedes usar `allowMultiple` para habilitar o deshabilitar la selección múltiple de imágenes.
- El parámetro `mimeTypes` es opcional y permite filtrar los tipos de archivos seleccionables.

## Corrección de Cierre de GalleryPickerLauncher

El `GalleryPickerLauncher` ahora incluye un callback `onDismiss` para manejar los casos donde los usuarios cierran el selector sin seleccionar ninguna imagen. Esto resuelve el problema donde el selector no se podía volver a abrir después de ser cerrado.

El callback `onDismiss` se activa cuando:
- Usuario cancela el diálogo de selección (Android)
- Usuario toca "Cancelar" (iOS)
- Usuario cancela la solicitud de permisos de cámara (iOS)
- Usuario cancela la interfaz de cámara (toca "Cancel" o "X") (iOS)

<h2 align="center"> Soporte de Plataformas</h2>

<p align="center">
  <strong>Compatibilidad multiplataforma con gestión automática de contexto</strong>
</p>

- **🤖 Android:** La librería gestiona automáticamente el contexto usando `LocalContext.current`. No es necesario pasar el contexto manualmente.
- ** iOS:** No se requiere contexto ya que la librería usa APIs nativas de iOS.

| Plataforma              | Versión Mínima | Estado |
|-------------------------|----------------|--------|
| Android                 | API 21+        | ✅     |
| iOS                     | iOS 12.0+      | ✅     |
| Compose Multiplatform   | 1.5.0+         | ✅     |

<h2 align="center"> ¿Por qué elegir ImagePickerKMP?</h2>

<p align="center">
  <strong>La librería más completa y amigable para desarrolladores en Kotlin Multiplatform</strong>
</p>

### Comparación con Otras Librerías

| Característica                     | ImagePickerKMP | Peekaboo        | KMPImagePicker   |
|-----------------------------------|----------------|-----------------|------------------|
| **Soporte Compose Multiplatform** | ✅ Nativo      | ❌ Solo Android  | ⚠️ Limitado       |
| **Personalización de UI**         | ✅ Control total | ⚠️ Básico         | ⚠️ Básico         |
| **Permisos Unificados**           | ✅ Manejo inteligente | ❌ Manual      | ⚠️ Específico por plataforma |
| **Manejo de Errores**             | ✅ Integral | ⚠️ Básico        | ⚠️ Básico         |
| **Integración de Cámara**         | ✅ Acceso directo | ✅ Acceso directo | ⚠️ Solo galería  |
| **Soporte de Galería**            | ✅ Selección múltiple | ✅ Selección múltiple | ✅ Selección múltiple |
| **API Multiplataforma**           | ✅ Código único | ❌ Específico por plataforma | ⚠️ Parcial  |

### Ventajas Clave

- ** Nativo de Compose Multiplatform**: Construido específicamente para Compose Multiplatform, garantizando comportamiento consistente en todas las plataformas
- ** Personalización Completa de UI**: Control total sobre diálogos, vistas de confirmación y UI de cámara
- ** Manejo Inteligente de Permisos**: Gestión unificada de permisos con alternativas inteligentes
- ** Optimizado para Rendimiento**: Procesamiento eficiente de imágenes y gestión de memoria
- ** Amigable para Desarrolladores**: API simple con manejo integral de errores

## Requisitos

### Android
- SDK mínimo: 21
- Kotlin 1.8+
- Compose Multiplatform

### iOS
- iOS 12.0+
- Xcode 14+
- Kotlin Multiplatform

## Integración Específica por Plataforma

### Android Nativo (Jetpack Compose)

Para la guía detallada de integración en Android, consulta: [Guía de Integración Android](docs/INTEGRATION_GUIDE.md#android-native-jetpack-compose)

### iOS Nativo (Swift/SwiftUI)

Para la guía detallada de integración en iOS, consulta: [Guía de Integración iOS](docs/INTEGRATION_GUIDE.md#ios-native-swiftswiftui)

### Kotlin Multiplatform/Compose Multiplatform

Para la guía detallada de integración en KMP, consulta: [Guía de Integración Kotlin Multiplatform](docs/INTEGRATION_GUIDE.es.md#kotlin-multiplatform--compose-multiplatform)

<h2 align="center"> Documentación</h2>

<p align="center">
  <strong>Guías completas y documentación detallada</strong>
</p>

-  [Guía de Integración](docs/INTEGRATION_GUIDE.es.md) - Guía completa de configuración e integración
-  [Guía de Personalización](docs/CUSTOMIZATION_GUIDE.es.md) - Personalización de UI y comportamiento
-  [Guía de Internacionalización](docs/I18N_GUIDE.es.md) - Guía de soporte multilenguaje
-  [Guía de Permisos](library/PERMISSION.es.md) - Detalles del manejo de permisos
-  [Guía de Cobertura](docs/COVERAGE_GUIDE.es.md) - Guía de cobertura de código y testing
-  [Configuración de Notificaciones](docs/NOTIFICATIONS_SETUP.es.md) - Configuración de notificaciones de Discord
-  [Referencia de API](docs/API_REFERENCE.es.md) - Documentación completa de la API
-  [Ejemplos](docs/EXAMPLES.es.md) - Ejemplos de código y casos de uso

<h2 align="center"> Contribuir</h2>

<p align="center">
  <strong>¡Aceptamos contribuciones de la comunidad!</strong><br>
  Consulta nuestra <a href="docs/CONTRIBUTING.es.md">Guía de Contribución</a> para más detalles.
</p>

---

<h2 align="center"> Licencia</h2>

<p align="center">
  Este proyecto está licenciado bajo la <strong>Licencia MIT</strong><br>
  Consulta el archivo <a href="docs/LICENSE">LICENSE</a> para más detalles.
</p>

---

<h2 align="center"> Soporte y Comunidad</h2>

<p align="center">
  <strong>Obtén ayuda, reporta problemas o únete a nuestra comunidad</strong>
</p>

<p align="center">
  <a href="mailto:belizairesmoy72@gmail.com">📧 Email</a> • 
  <a href="https://github.com/ismoy/ImagePickerKMP/issues">🐛 Issues</a> • 
  <a href="https://github.com/ismoy/ImagePickerKMP/wiki">📖 Wiki</a> • 
  <a href="https://discord.gg/EjSQTeyh">💬 Discord</a>
</p>

<h2 align="center"> Changelog</h2>

<p align="center">
  Consulta <a href="docs/CHANGELOG.es.md">CHANGELOG.es.md</a> para una lista completa de cambios y actualizaciones.
</p>

---

<p align="center">
  <strong>Hecho con ❤️ para la comunidad Kotlin Multiplatform</strong><br>
  <em>¡Dale una estrella ⭐ a este repo si te ayudó!</em>
</p>

<p align="center">
  <a href="https://github.com/ismoy/ImagePickerKMP/stargazers">⭐ Estrella en GitHub</a> • 
  <a href="https://github.com/ismoy/ImagePickerKMP/fork">🍴 Fork</a> • 
  <a href="https://github.com/ismoy/ImagePickerKMP/issues/new">🐛 Reportar Bug</a> • 
  <a href="https://github.com/ismoy/ImagePickerKMP/issues/new">💡 Solicitar Feature</a>
</p>