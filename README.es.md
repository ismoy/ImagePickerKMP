<p align="center">
  <a href="https://github.com/ismoy/ImagePickerKMP/actions"><img src="https://github.com/ismoy/ImagePickerKMP/workflows/CI/badge.svg" alt="CI"></a>
  <a href="https://codecov.io/gh/ismoy/ImagePickerKMP"><img src="https://codecov.io/gh/ismoy/ImagePickerKMP/branch/main/graph/badge.svg" alt="Code Coverage"></a>
  <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License"></a>
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-2.1.21-blue.svg" alt="Kotlin"></a>
</p>

<p align="center">
  <a href="https://search.maven.org/artifact/io.github.ismoy/imagepickerkmp"><img src="https://img.shields.io/maven-central/v/io.github.ismoy/imagepickerkmp.svg?label=Maven%20Central" alt="Maven Central"></a>
  <a href="https://github.com/ismoy/ImagePickerKMP/releases"><img src="https://img.shields.io/github/v/release/ismoy/ImagePickerKMP?label=GitHub%20Release" alt="GitHub Release"></a>
  <a href="https://github.com/ismoy/ImagePickerKMP/stargazers"><img src="https://img.shields.io/github/stars/ismoy/ImagePickerKMP?style=social" alt="GitHub Repo stars"></a>
    <a href="https://github.com/ismoy/ImagePickerKMP/commits/main"><img src="https://img.shields.io/github/last-commit/ismoy/ImagePickerKMP" alt="GitHub last commit"></a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Compose%20Multiplatform-green" alt="Compose Multiplatform">
  <img src="https://img.shields.io/badge/Platform-Android-green" alt="Android">
  <img src="https://img.shields.io/badge/Platform-iOS-blue" alt="iOS">
  <a href="https://github.com/ismoy/ImagePickerKMP/actions/workflows/detekt.yml">
  <img src="https://github.com/ismoy/ImagePickerKMP/actions/workflows/detekt.yml/badge.svg?branch=main" alt="Detekt"></a>
</p>

---

<h1 align="center">ImagePickerKMP</h1>

<p align="center">
  <strong>Librería Multiplataforma de Selección de Imágenes y Cámara (Android & iOS)</strong><br>
  Construida con <strong>Kotlin Multiplatform</strong> + <strong>Compose Multiplatform</strong> + <strong>Kotlin/Native</strong>
</p>

<p align="center">
  <a href="README.md">English</a> • 
  <a href="https://github.com/ismoy/ImagePickerKMP">GitHub</a> • 
  <a href="https://search.maven.org/artifact/io.github.ismoy/imagepickerkmp">Maven Central</a> • 
  <a href="https://discord.gg/EjSQTeyh">Discord</a>
</p>

---

## Demo **Android** y **iOS**

<p align="center">
  <a href="https://github.com/ismoy/ImagePickerKMP/blob/develop/demo_video_library.gif">
    <img src="https://raw.githubusercontent.com/ismoy/ImagePickerKMP/develop/demo_video_library.gif" alt="Demo ImagePickerKMP" width="80%">
  </a>
</p>

---

## Acerca de ImagePickerKMP
- **Multiplataforma**: Funciona perfectamente en Android e iOS
- **Integración de Cámara**: Acceso directo a la cámara con captura de fotos
- **Selección de Galería**: Selecciona imágenes de la galería del dispositivo con soporte de compresión
- **Compresión Automática de Imágenes**: Optimiza el tamaño de imagen manteniendo la calidad
- **Niveles de Compresión Configurables**: Opciones de compresión BAJA, MEDIA, ALTA
- **Procesamiento Asíncrono**: UI no bloqueante con integración de Kotlin Coroutines
- **Soporte de Múltiples Formatos**: JPEG, PNG, HEIC, HEIF, WebP, GIF, BMP
- **UI Personalizable**: Diálogos personalizados y vistas de confirmación
- **Manejo de Permisos**: Gestión inteligente de permisos para ambas plataformas
- **Fácil Integración**: API simple con Compose Multiplatform
- **Altamente Configurable**: Opciones extensas de personalización

---

## Inicio Rápido – Integración del Selector de Imágenes de Kotlin Multiplatform

### Instalación

### Usando ImagePickerKMP en Kotlin Multiplatform / Compose Multiplatform
#### Paso 1: Agregar la dependencia
<p>En tu <code>commonMain</code> <code>build.gradle.kts</code>:</p>

```kotlin
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.23")//última versión
}
```
#### Configuración de Permisos iOS
<p>No olvides configurar los permisos específicos de iOS en tu archivo <code>Info.plist</code>:</p>

```xml
<key>NSCameraUsageDescription</key>
<string>Necesitamos acceso a la cámara para capturar una foto.</string>
```
<h1>Uso Básico</h1>
<p>Con el uso básico, se utilizarán los diseños predeterminados.</p>

#### Paso 2: Lanzar la Cámara
```kotlin
 var showCamera by remember { mutableStateOf(false) }
var capturedPhoto by remember { mutableStateOf<CameraPhotoHandler.PhotoResult?>(null) }
```
```kotlin
if (showCamera) {
    ImagePickerLauncher(
        config = ImagePickerConfig(
            onPhotoCaptured = { result ->
                capturedPhoto = result
                showCamera = false
            },
            onError = {
                showCamera = false
            },
            onDismiss = {
                showImagePicker = false // Resetear estado cuando el usuario no selecciona nada
            },
            directCameraLaunch = false // Set to true if you want to launch the camera directly Only IOS
        )
    )
}
```
#### Paso 3: Seleccionar de la Galería
```kotlin
var showGallery by remember { mutableStateOf(false) }
var selectedImages by remember { mutableStateOf<List<GalleryPhotoHandler.PhotoResult>>(emptyList()) }
```
```kotlin
if (showGallery) {
    GalleryPickerLauncher(
        onPhotosSelected = { photos ->
            selectedImages = photos
            showGallery = false
        },
        onError = { error ->
            showGallery = false
        },
        onDismiss = {
            println("El usuario canceló o cerró el selector")
            showGallery = false // Resetear estado cuando el usuario no selecciona nada
        }
                allowMultiple = true, // False para selección simple
        mimeTypes = listOf(MimeType.IMAGE_PNG) ,// Opcional: filtrar por tipo
    )
}

Button(onClick = { showGallery = true }) {
    Text("Elegir de la Galería")
}
```

## Compresión de Imágenes
**Optimiza automáticamente el tamaño de imagen manteniendo la calidad con niveles de compresión configurables.**

### Niveles de Compresión
- **BAJA (LOW)**: 95% calidad, máx 2560px dimensión - Mejor calidad, archivos más grandes
- **MEDIA (MEDIUM)**: 75% calidad, máx 1920px dimensión - Calidad/tamaño equilibrado
- **ALTA (HIGH)**: 50% calidad, máx 1280px dimensión - Archivos más pequeños, bueno para almacenamiento

### Cámara con Compresión
```kotlin
if (showCamera) {
    ImagePickerLauncher(
        config = ImagePickerConfig(
            onPhotoCaptured = { result ->
                capturedPhoto = result
                showCamera = false
            },
            onError = { showCamera = false },
            onDismiss = { showCamera = false },
            cameraCaptureConfig = CameraCaptureConfig(
                compressionLevel = CompressionLevel.MEDIUM // Habilitar compresión
            )
        )
    )
}
```

### Galería con Compresión
```kotlin
if (showGallery) {
    GalleryPickerLauncher(
        onPhotosSelected = { photos ->
            selectedImages = photos
            showGallery = false
        },
        onError = { showGallery = false },
        onDismiss = { showGallery = false },
        allowMultiple = true,
        mimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG),
        cameraCaptureConfig = CameraCaptureConfig(
            compressionLevel = CompressionLevel.HIGH // Optimizar para almacenamiento
        )
    )
}
```

### Formatos de Imagen Soportados
- **JPEG** (image/jpeg) - Soporte completo de compresión
- **PNG** (image/png) - Soporte completo de compresión
- **HEIC** (image/heic) - Soporte completo de compresión
- **HEIF** (image/heif) - Soporte completo de compresión
- **WebP** (image/webp) - Soporte completo de compresión
- **GIF** (image/gif) - Soporte completo de compresión
- **BMP** (image/bmp) - Soporte completo de compresión

### Para más personalización (vistas de confirmación, filtrado MIME, etc.), [consulta la guía de integración para KMP.](https://github.com/ismoy/ImagePickerKMP/blob/main/docs/INTEGRATION_GUIDE.es.md)

### Usando ImagePickerKMP en Android Nativo (Jetpack Compose)
<p>Incluso si no estás usando KMP, puedes usar ImagePickerKMP en proyectos Android puros con Jetpack Compose.</p>

#### Paso 1: Agregar la dependencia
```kotlin
implementation("io.github.ismoy:imagepickerkmp:1.0.22")
```
#### Paso 2: Ejemplo de Lanzador de Cámara
```kotlin
var showCamera by remember { mutableStateOf(false) }
var capturedPhoto by remember { mutableStateOf<CameraPhotoHandler.PhotoResult?>(null) }
```
```kotlin
if (showCamera) {
    ImagePickerLauncher(
        config = ImagePickerConfig(
            onPhotoCaptured = { result ->
                capturedPhoto = result
                showCamera = false
            },
            onError = {
                showCamera = false
            },
            onDismiss = {
                showImagePicker = false // Resetear estado cuando el usuario no selecciona nada
            },
            directCameraLaunch = false // Set to true if you want to launch the camera directly Only IOS
        )
    )
}

Button(onClick = { showCamera = true }) {
    Text("Tomar Foto")
}
```

#### Paso 3: Ejemplo de Selector de Galería
```kotlin
var showGallery by remember { mutableStateOf(false) }
var selectedImages by remember { mutableStateOf<List<GalleryPhotoHandler.PhotoResult>>(emptyList()) }
```
```kotlin
if (showGallery) {
    GalleryPickerLauncher(
        onPhotosSelected = { photos ->
            selectedImages = photos
            showGallery = false
        },
        onError = { error ->
            showGallery = false
        },
        onDismiss = {
            showCamera = false
        },
        allowMultiple = true, // False para selección simple
        mimeTypes = listOf(MimeType.IMAGE_PNG), // Opcional: filtrar por tipo
    )
}

Button(onClick = { showGallery = true }) {
    Text("Elegir de la Galería")
}
```
### Para más personalización (vistas de confirmación, filtrado MIME, etc.), [consulta la guía de integración para Nativo.](https://github.com/ismoy/ImagePickerKMP/blob/main/docs/INTEGRATION_GUIDE.es.md)

## Soporte de Plataformas
<p align="center">
  <strong>Compatibilidad multiplataforma con gestión inteligente de contexto</strong>
</p>

- **Android:** La librería gestiona automáticamente el contexto usando `LocalContext.current`. No es necesario pasar el contexto manualmente.
- **iOS:** No se requiere contexto ya que la librería usa APIs nativas de iOS.

| Plataforma              | Versión Mínima | Estado |
|-------------------------|----------------|--------|
| Android                 | API 21+        | ✅     |
| iOS                     | iOS 12.0+      | ✅     |
| Compose Multiplatform   | 1.5.0+         | ✅     |

## ¿Por qué elegir ImagePickerKMP?

<p align="center">
  <strong>El selector de imágenes más completo y amigable para desarrolladores en Kotlin Multiplatform</strong>
</p>

<h1>ImagePickerKMP</h1>

| Característica                     | ImagePickerKMP            
|-----------------------------------|----------------|
| **Soporte Compose Multiplatform** | ✅ Nativo      
| **Personalización de UI**         | ✅ Control total 
| **Permisos Unificados**           | ✅ Manejo inteligente 
| **Manejo de Errores**             | ✅ Integral 
| **Integración de Cámara**         | ✅ Acceso directo 
| **Soporte de Galería**            | ✅ Selección múltiple 
| **API Multiplataforma**           | ✅ Código único

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

## Documentación

<p align="center">
  <strong>Guías completas y referencias para cada aspecto de ImagePickerKMP</strong>
</p>

-  [Guía de Integración](docs/INTEGRATION_GUIDE.md) - Guía completa de configuración e integración
-  [Guía de Personalización](docs/CUSTOMIZATION_GUIDE.md) - Personalización de UI y comportamiento
-  [Guía de Internacionalización](docs/I18N_GUIDE.md) - Guía de soporte multilenguaje
-  [Guía de Permisos](docs/PERMISSION.md) - Detalles del manejo de permisos
-  [Guía de Cobertura](docs/COVERAGE_GUIDE.md) - Guía de cobertura de código y testing
-  [Configuración de Notificaciones](docs/NOTIFICATIONS_SETUP.md) - Configuración de notificaciones de Discord
-  [Referencia de API](docs/API_REFERENCE.md) - Documentación completa de la API
-  [Ejemplos](docs/EXAMPLES.md) - Ejemplos de código y casos de uso

## Contribuir

<p align="center">
  <strong>¡Aceptamos contribuciones de la comunidad!</strong><br>
  Consulta nuestra <a href="docs/CONTRIBUTING.md">Guía de Contribución</a> para más detalles.
</p>

---

## Soporte y Comunidad

<p align="center">
  <strong>Obtén ayuda, reporta problemas o únete a nuestra comunidad</strong>
</p>

<p align="center">
  <a href="mailto:belizairesmoy72@gmail.com">📧 Email</a> • 
  <a href="https://github.com/ismoy/ImagePickerKMP/issues">🐛 Issues</a> • 
  <a href="https://github.com/ismoy/ImagePickerKMP/wiki">📖 Wiki</a> • 
  <a href="https://discord.com/channels/1393705692484993114/1393706133864190133">💬 Discord</a>
</p>

<p align="center">
  <strong>Hecho con ❤️ para la comunidad Kotlin Multiplatform</strong><br>
  <em>¡Dale una estrella ⭐ a este repo si te ayudó!</em>
</p>