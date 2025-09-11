<p align="center">
  <a href="https://github.com/ismoy/ImagePickerKMP/actions"><img src="https://github.com/ismoy/ImagePickerKMP/workflows/CI/badge.svg" alt="CI"></a>
  <a href="https://codecov.io/gh/ismoy/ImagePickerKMP"><img src="https://codecov.io/gh/ismoy/ImagePickerKMP/branch/main/graph/badge.svg" alt="Cobertura de Código"></a>
  <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="Licencia"></a>
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-2.1.21-blue.svg" alt="Kotlin"></a>
</p>

<p align="center">
  <a href="https://search.maven.org/artifact/io.github.ismoy/imagepickerkmp"><img src="https://img.shields.io/maven-central/v/io.github.ismoy/imagepickerkmp.svg?label=Maven%20Central" alt="Maven Central"></a>
  <a href="https://github.com/ismoy/ImagePickerKMP/releases"><img src="https://img.shields.io/github/v/release/ismoy/ImagePickerKMP?label=GitHub%20Release" alt="Lanzamiento GitHub"></a>
  <a href="https://github.com/ismoy/ImagePickerKMP/stargazers"><img src="https://img.shields.io/github/stars/ismoy/ImagePickerKMP?style=social" alt="Estrellas del repositorio GitHub"></a>
    <a href="https://github.com/ismoy/ImagePickerKMP/commits/main"><img src="https://img.shields.io/github/last-commit/ismoy/ImagePickerKMP" alt="Último commit GitHub"></a>
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
## Demos Android e iOS

### Android
| Uso de Cámara | Modo Recorte |
|---------------|--------------|
| <a href="https://raw.githubusercontent.com/ismoy/ImagePickerKMP/develop/docs/androidCameraDemo.gif"><img src="https://raw.githubusercontent.com/ismoy/ImagePickerKMP/develop/docs/androidCameraDemo.gif" alt="Demo Cámara Android" width="250"></a> | <a href="https://raw.githubusercontent.com/ismoy/ImagePickerKMP/develop/docs/androidCrop.gif"><img src="https://raw.githubusercontent.com/ismoy/ImagePickerKMP/develop/docs/androidCrop.gif" alt="Demo Recorte Android" width="250"></a> |

### iOS
| Uso de Cámara | Modo Recorte |
|---------------|--------------|
| <a href="https://raw.githubusercontent.com/ismoy/ImagePickerKMP/develop/docs/iosCameraDemo.gif"><img src="https://raw.githubusercontent.com/ismoy/ImagePickerKMP/develop/docs/iosCameraDemo.gif" alt="Demo Cámara iOS" width="250"></a> | <a href="https://raw.githubusercontent.com/ismoy/ImagePickerKMP/develop/docs/iosCrop.gif"><img src="https://raw.githubusercontent.com/ismoy/ImagePickerKMP/develop/docs/iosCrop.gif" alt="Demo Recorte iOS" width="250"></a> |

---

## Acerca de ImagePickerKMP
- **Multiplataforma**: Funciona perfectamente en Android e iOS
- **Integración de Cámara**: Acceso directo a la cámara con captura de fotos
- **Selección de Galería**: Selecciona imágenes de la galería del dispositivo con soporte de compresión
- **Recorte Avanzado de Imágenes**: Funcionalidad de recorte multiplataforma con gestión automática de contexto
- **Compresión Automática de Imágenes**: Optimiza el tamaño de imagen manteniendo la calidad
- **Niveles de Compresión Configurables**: Opciones de compresión BAJA, MEDIA, ALTA
- **Procesamiento Asíncrono**: UI no bloqueante con integración de Kotlin Coroutines
- **Soporte de Múltiples Formatos**: JPEG, PNG, HEIC, HEIF, WebP, GIF, BMP
- **UI Personalizable**: Diálogos personalizados y vistas de confirmación
- **Manejo de Permisos**: Gestión inteligente de permisos para ambas plataformas
- **Integración Fácil**: API simple con Compose Multiplatform
- **Experiencia de Usuario Mejorada**: Sistema de diseño mejorado con manejo adecuado de zoom y relación de aspecto
- **Altamente Configurable**: Opciones extensas de personalización

---

## Inicio Rápido – Integración del Selector de Imágenes Kotlin Multiplatform

### Instalación

### Usando ImagePickerKMP en Kotlin Multiplatform / Compose Multiplatform
#### Paso 1: Añadir la dependencia
<p>En tu <code>commonMain</code> <code>build.gradle.kts</code>:</p>

```kotlin
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.23")//últimaversión
}
```
### Usando ImagePickerKMP en Android Nativo (Jetpack Compose)
<p>Incluso si no estás usando KMP, puedes usar ImagePickerKMP en proyectos Android puros con Jetpack Compose.</p>

#### Paso 1: Añadir la dependencia
```kotlin
implementation("io.github.ismoy:imagepickerkmp:1.0.23")
```
#### Configuración de Permisos iOS
<p>No olvides configurar los permisos específicos de iOS en tu archivo <code>Info.plist</code>:</p>

```xml
<key>NSCameraUsageDescription</key>
<string>Necesitamos acceso a la cámara para capturar una foto.</string>
```
<h1>Uso Básico</h1>

#### Paso 2: Lanzar la Cámara
```kotlin
 var showCamera by remember { mutableStateOf(false) }
 var capturedPhoto by remember { mutableStateOf<PhotoResult?>(null) }
```
```kotlin
if (showCamera) {
    ImagePickerLauncher(
        config = ImagePickerConfig(
            enableCrop = false, // Establecer a true si quieres la opción de Recorte
            onPhotoCaptured = { result ->
                capturedPhoto = result
                // ¡Ahora puedes acceder a result.fileSize para capturas de cámara también!
                println("Tamaño de foto de cámara: ${result.fileSize}KB")
                showCamera = false
            },
            onError = {
                showCamera = false
            },
            onDismiss = {
                showImagePicker = false // Restablecer estado cuando el usuario no selecciona nada
            },
            directCameraLaunch = false, // Establecer a true si quieres lanzar la cámara directamente Solo iOS
            // Es posible comprimir imágenes, por defecto está con compresión baja en la librería
            cameraCaptureConfig = CameraCaptureConfig(
                compressionLevel = CompressionLevel.HIGH
            )

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
            println("Usuario canceló o cerró el selector")
            showGallery = false // Restablecer estado cuando el usuario no selecciona nada
        },
        enableCrop = false, // Establecer a true si quieres la opción de Recorte 
        allowMultiple = true, // False para selección única
        mimeTypes = listOf(MimeType.IMAGE_PNG) ,// Opcional: filtrar por tipo
    )
}

Button(onClick = { showGallery = true }) {
    Text("Elegir de la Galería")
}
```

### Para más personalización (vistas de confirmación, filtrado MIME, etc.), [consulta la guía de integración para KMP.](https://github.com/ismoy/ImagePickerKMP/blob/main/docs/INTEGRATION_GUIDE.es.md)

## Compresión de Imágenes
**Optimiza automáticamente el tamaño de imagen manteniendo la calidad con niveles de compresión configurables.**

### Niveles de Compresión
- **BAJA**: 95% de calidad, dimensión máxima 2560px - Mejor calidad, archivos más grandes
- **MEDIA**: 75% de calidad, dimensión máxima 1920px - Calidad/tamaño equilibrado
- **ALTA**: 50% de calidad, dimensión máxima 1280px - Archivos más pequeños, bueno para almacenamiento

### Formatos de Imagen Soportados
- **JPEG** (image/jpeg) - Soporte completo de compresión
- **PNG** (image/png) - Soporte completo de compresión
- **HEIC** (image/heic) - Soporte completo de compresión
- **HEIF** (image/heif) - Soporte completo de compresión
- **WebP** (image/webp) - Soporte completo de compresión
- **GIF** (image/gif) - Soporte completo de compresión
- **BMP** (image/bmp) - Soporte completo de compresión

## Soporte de Plataformas
<p align="center">
  <strong>Compatibilidad multiplataforma con gestión inteligente de contexto y funcionalidad de recorte mejorada</strong>
</p>

- **Android:** La librería gestiona automáticamente el contexto usando `LocalContext.current`. No necesitas pasar el contexto manualmente.
- **iOS:** El contexto no es requerido ya que la librería usa APIs nativas de iOS. Los cálculos mejorados de coordenadas de recorte aseguran comportamiento consistente con Android.
- **Recorte Multiplataforma:** Función `applyCrop` unificada con gestión automática de contexto y cálculos de coordenadas consistentes entre plataformas.

### Mejoras Recientes
- **🔄 Gestión Automática de Contexto**: La función `applyCrop` ahora es `@Composable` y maneja el contexto de Android automáticamente
- **🖼️ Precisión Mejorada de Recorte iOS**: Cálculos de coordenadas corregidos para recorte preciso de imágenes en iOS
- **🎯 Sistema de Diseño Mejorado**: Resueltos conflictos de z-index y problemas de superposición de zoom para mejor experiencia de usuario
- **📱 Mejor Soporte de Relación de Aspecto**: Manejo mejorado de relaciones de aspecto verticales (como 9:16) con gestión espacial mejorada

| Plataforma              | Versión Mínima | Estado |
|------------------------|----------------|--------|
| Android                | API 21+        | ✅     |
| iOS                    | iOS 12.0+      | ✅     |
| Compose Multiplatform  | 1.5.0+         | ✅     |

## ¿Por qué Elegir ImagePickerKMP?

<p align="center">
  <strong>El selector de imágenes más completo y amigable para desarrolladores en Kotlin Multiplatform</strong>
</p>

<h1>ImagePickerKMP</h1>

| Característica                 | ImagePickerKMP            
|-------------------------------|----------------|
| **Soporte Compose Multiplatform** | ✅ Nativo      
| **Personalización de UI**     | ✅ Control completo 
| **Permisos Unificados**       | ✅ Manejo inteligente 
| **Manejo de Errores**         | ✅ Completo 
| **Integración de Cámara**     | ✅ Acceso directo 
| **Soporte de Galería**        | ✅ Selección múltiple 
| **API Multiplataforma**       | ✅ Base de código única

### Ventajas Clave

- ** Nativo de Compose Multiplatform**: Construido específicamente para Compose Multiplatform, asegurando comportamiento consistente entre plataformas
- ** Personalización Completa de UI**: Control total sobre diálogos, vistas de confirmación, y UI de cámara
- ** Gestión Inteligente de Permisos**: Manejo unificado de permisos con respaldos inteligentes
- ** Optimizado para Rendimiento**: Procesamiento eficiente de imágenes y gestión de memoria
- ** Amigable para Desarrolladores**: API simple con manejo completo de errores

## Requisitos

### Android
- SDK Mínimo: 21
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

-  [Guía de Integración](docs/INTEGRATION_GUIDE.es.md) - Guía completa de configuración e integración
-  [Guía de Personalización](docs/CUSTOMIZATION_GUIDE.es.md) - Personalización de UI y comportamiento
-  [Guía de Internacionalización](docs/I18N_GUIDE.es.md) - Guía de soporte multi-idioma
-  [Guía de Permisos](docs/PERMISSION.es.md) - Detalles de manejo de permisos
-  [Guía de Cobertura](docs/COVERAGE_GUIDE.es.md) - Cobertura de código y guía de pruebas
-  [Configuración de Notificaciones](docs/NOTIFICATIONS_SETUP.es.md) - Configuración de notificaciones Discord
-  [Referencia API](docs/API_REFERENCE.es.md) - Documentación completa de la API
-  [Ejemplos](docs/EXAMPLES.es.md) - Ejemplos de código y casos de uso

## Contribuir

<p align="center">
  <strong>¡Damos la bienvenida a contribuciones de la comunidad!</strong><br>
  Consulta nuestra <a href="docs/CONTRIBUTING.es.md">Guía de Contribución</a> para detalles.
</p>

---

## Soporte y Comunidad

<p align="center">
  <strong>Obtén ayuda, reporta problemas, o únete a nuestra comunidad</strong>
</p>

<p align="center">
  <a href="mailto:belizairesmoy72@gmail.com">📧 Email</a> • 
  <a href="https://github.com/ismoy/ImagePickerKMP/issues">🐛 Problemas</a> • 
  <a href="https://github.com/ismoy/ImagePickerKMP/wiki">📖 Wiki</a> • 
  <a href="https://discord.com/channels/1393705692484993114/1393706133864190133">💬 Discord</a>
</p>

<p align="center">
  <strong>Hecho con ❤️ para la comunidad Kotlin Multiplatform</strong><br>
  <em>¡Dale una estrella ⭐ a este repo si te ayudó!</em>
</p>