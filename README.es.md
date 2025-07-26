[![CI](https://github.com/ismoy/ImagePickerKMP/workflows/CI/badge.svg)](https://github.com/ismoy/ImagePickerKMP/actions)
[![Code Coverage](https://codecov.io/gh/ismoy/ImagePickerKMP/branch/main/graph/badge.svg)](https://codecov.io/gh/ismoy/ImagePickerKMP)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-lightgrey.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.ismoy/imagepickerkmp.svg?label=Maven%20Central)](https://search.maven.org/artifact/io.github.ismoy/imagepickerkmp)
[![GitHub Repo stars](https://img.shields.io/github/stars/ismoy/ImagePickerKMP?style=social)](https://github.com/ismoy/ImagePickerKMP/stargazers)
[![GitHub last commit](https://img.shields.io/github/last-commit/ismoy/ImagePickerKMP)](https://github.com/ismoy/ImagePickerKMP/commits/main)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](https://github.com/ismoy/ImagePickerKMP/pulls)
[![Discord](https://img.shields.io/discord/1393705692484993114.svg?label=Discord&logo=discord&color=7289da)](https://discord.gg/EjSQTeyh)
![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-green)
![Android](https://img.shields.io/badge/Platform-Android-green)
![iOS](https://img.shields.io/badge/Platform-iOS-blue)
![Coverage Status](https://img.shields.io/codecov/c/github/ismoy/ImagePickerKMP)
[![Detekt](https://github.com/ismoy/ImagePickerKMP/actions/workflows/detekt.yml/badge.svg?branch=main)](https://github.com/ismoy/ImagePickerKMP/actions/workflows/detekt.yml)

## 🎥 Demo

<video width="100%" autoplay loop muted playsinline>
  <source src="https://user-images.githubusercontent.com/ismoy/ImagePickerKMP/main/demo/demo.mp4" type="video/mp4">
  Tu navegador no soporta el elemento de video.
</video>

*¡Mira el demo de arriba para ver ImagePickerKMP en acción - captura de cámara, selección de galería y UI personalizada en funcionamiento!*

### 📱 Funcionalidades Mostradas en el Demo:
- **📸 Captura de Cámara**: Acceso directo a la cámara con control de flash
- **🔄 Cambio de Cámara**: Alternancia fluida entre cámara frontal y trasera
- **🎨 UI Personalizada**: Diálogos de confirmación personalizados
- **📁 Selección de Galería**: Selección múltiple de imágenes desde la galería
- **⚡ Rendimiento**: Interacciones suaves y responsivas
- **🔒 Permisos**: Manejo inteligente de permisos

# ImagePickerKMP
**Librería Multiplataforma de Selección de Imágenes y Cámara (Android e iOS)**  
Construida con **Kotlin Multiplatform** + **Compose Multiplatform** + **Kotlin/Native**.

Este documento también está disponible en inglés: [README.md](README.md)

## Características – Cámara, Selector de Imágenes y Galería para Android e iOS

- 📱 **Multiplataforma**: Funciona en Android y iOS
- 📸 **Integración de cámara**: Acceso directo a la cámara y captura de fotos
- 🎨 **UI personalizable**: Diálogos y vistas de confirmación personalizables
- 🔒 **Gestión de permisos**: Manejo inteligente de permisos en ambas plataformas
- 🎯 **Fácil integración**: API simple con Compose Multiplatform
- 🔧 **Altamente configurable**: Muchas opciones de personalización

## Inicio Rápido – Integración de Selector de Imágenes Kotlin Multiplatform

### Instalación

Agrega la dependencia en tu `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.1")
}
```

### Uso básico

```kotlin
@Composable
fun MiImagePicker() {
    var mostrarPicker by remember { mutableStateOf(false) }
    var imagenCapturada by remember { mutableStateOf<PhotoResult?>(null) }

    if (mostrarPicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            config = ImagePickerConfig(
                onPhotoCaptured = { result ->
                    // Manejar la foto capturada
                    imagenCapturada = result
                    mostrarPicker = false
                },
                onError = { exception ->
                    // Manejar errores
                    mostrarPicker = false
                }
            )
        )
    }

    Button(onClick = { mostrarPicker = true }) {
        Text("Tomar foto")
    }
}
```

### 💡 Caso de Uso Real

Aquí tienes un ejemplo práctico mostrando captura de cámara con vista previa y subida:

```kotlin
@Composable
fun SelectorAvanzado() {
    var mostrarSelector by remember { mutableStateOf(false) }
    var imagenCapturada by remember { mutableStateOf<PhotoResult?>(null) }
    var subiendo by remember { mutableStateOf(false) }

    if (mostrarSelector) {
        ImagePickerLauncher(
            context = LocalContext.current,
            config = ImagePickerConfig(
                onPhotoCaptured = { result ->
                    imagenCapturada = result
                    mostrarSelector = false
                    subirImagen(result)
                },
                onError = { exception ->
                    mostrarSelector = false
                    // Manejo de errores personalizado aquí
                },
                cameraCaptureConfig = CameraCaptureConfig(
                    permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                        customConfirmationView = { result, onConfirm, onRetry ->
                            ImageConfirmationViewWithCustomButtons(
                                result = result,
                                onConfirm = onConfirm,
                                onRetry = onRetry,
                                questionText = "¿Usar esta foto?",
                                retryText = "Volver a tomar",
                                acceptText = "Usar Foto"
                            )
                        }
                    )
                )
            )
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mostrar imagen capturada
        imagenCapturada?.let { foto ->
            AsyncImage(
                model = foto.uri,
                contentDescription = "Foto capturada",
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            if (subiendo) {
                CircularProgressIndicator()
                Text("Subiendo...")
            }
        }

        Button(
            onClick = { mostrarSelector = true },
            enabled = !subiendo
        ) {
            Icon(Icons.Default.Camera, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Tomar Foto")
        }
    }
}

private fun subirImagen(photoResult: PhotoResult) {
    // Ejemplo de implementación de subida
    lifecycleScope.launch(Dispatchers.IO) {
        try {
            // Lógica de subida aquí
            withContext(Dispatchers.Main) {
                // Mostrar mensaje de éxito
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                // Mostrar mensaje de error
            }
        }
    }
}
```

### Uso del Selector de Galería

También puedes permitir que los usuarios seleccionen imágenes directamente desde la galería:

> **Nota:** No necesitas solicitar permisos de galería manualmente. La librería gestiona automáticamente la solicitud de permisos y el flujo de usuario tanto en Android como en iOS, proporcionando una experiencia nativa en cada plataforma.

```kotlin
@Composable
fun MiSelectorGaleria() {
    var mostrarGaleria by remember { mutableStateOf(false) }
    var imagenesSeleccionadas by remember { mutableStateOf<List<PhotoResult>>(emptyList()) }

    if (mostrarGaleria) {
        GalleryPickerLauncher(
            context = LocalContext.current, // Solo Android; ignorado en iOS
            onPhotosSelected = { resultados ->
                imagenesSeleccionadas = resultados
                mostrarGaleria = false
            },
            onError = { exception ->
                // Manejar errores
                mostrarGaleria = false
            },
            allowMultiple = true // o false para selección simple
            // mimeTypes = listOf("image/jpeg", "image/png") // Opcional: filtrar por tipo
        )
    }

    Button(onClick = { mostrarGaleria = true }) {
        Text("Seleccionar de la galería")
    }
}
```

- En **Android**, el usuario verá el selector de galería del sistema y los permisos se solicitan automáticamente si es necesario.
- En **iOS**, se usa el selector nativo de galería. En iOS 14+ se soporta selección múltiple. El sistema gestiona permisos y acceso limitado de forma nativa.
- El callback `onPhotosSelected` siempre recibe una lista, incluso para selección simple.
- Puedes usar `allowMultiple` para habilitar o deshabilitar la selección múltiple de imágenes.
- El parámetro `mimeTypes` es opcional y permite filtrar los tipos de archivos seleccionables.

## Compatibilidad de plataformas


| Platform                | Minimum Version | Status |
|-------------------------|----------------|--------|
| Android                 | API 21+        | ✅     |
| iOS                     | iOS 12.0+      | ✅     |
| Compose Multiplatform   | 1.5.0+         | ✅     |

## ¿Por qué elegir ImagePickerKMP?

### 🆚 Comparación con Otras Librerías

| Característica | ImagePickerKMP | Peekaboo | KMPImagePicker |
|----------------|----------------|----------|----------------|
| **Soporte Compose Multiplatform** | ✅ Nativo | ❌ Solo Android | ⚠️ Limitado |
| **Personalización de UI** | ✅ Control total | ⚠️ Básico | ⚠️ Básico |
| **Permisos Unificados** | ✅ Manejo inteligente | ❌ Manual | ⚠️ Específico por plataforma |
| **Manejo de Errores** | ✅ Integral | ⚠️ Básico | ⚠️ Básico |
| **Integración de Cámara** | ✅ Acceso directo | ✅ Acceso directo | ⚠️ Solo galería |
| **Soporte de Galería** | ✅ Selección múltiple | ✅ Selección múltiple | ✅ Selección múltiple |
| **API Multiplataforma** | ✅ Código único | ❌ Específico por plataforma | ⚠️ Parcial |

### 🎯 Ventajas Clave

- **🔄 Nativo de Compose Multiplatform**: Construido específicamente para Compose Multiplatform, garantizando comportamiento consistente en todas las plataformas
- **🎨 Personalización Completa de UI**: Control total sobre diálogos, vistas de confirmación y UI de cámara
- **🔒 Manejo Inteligente de Permisos**: Gestión unificada de permisos con alternativas inteligentes
- **⚡ Optimizado para Rendimiento**: Procesamiento eficiente de imágenes y gestión de memoria
- **🛠️ Amigable para Desarrolladores**: API simple con manejo integral de errores

## Requisitos

### Android
- SDK mínimo: 21
- Kotlin 1.8+
- Compose Multiplatform

### iOS
- iOS 12.0+
- Xcode 14+
- Kotlin Multiplatform

## Integración específica por plataforma

### Android Nativo (Jetpack Compose)

Para la guía detallada de integración en Android, consulta: [Guía de Integración Android](docs/INTEGRATION_GUIDE.es.md#android-nativo-jetpack-compose)

### iOS Nativo (Swift/SwiftUI)

Para la guía detallada de integración en iOS, consulta: [Guía de Integración iOS](docs/INTEGRATION_GUIDE.es.md#ios-nativo-swiftswiftui)

### Kotlin Multiplatform/Compose Multiplatform

Para la guía detallada de integración en KMP, consulta: [Guía de Integración Kotlin Multiplatform](docs/INTEGRATION_GUIDE.es.md#kotlin-multiplatform--compose-multiplatform)

## Documentación

- [Guía de Integración](docs/INTEGRATION_GUIDE.es.md) - Guía completa de configuración e integración
- [Guía de Personalización](docs/CUSTOMIZATION_GUIDE.es.md) - Personalización de UI y comportamiento
- [Guía de Internacionalización](docs/I18N_GUIDE.es.md) - Guía de soporte multilenguaje
- [Guía de Permisos](docs/PERMISSION.es.md) - Detalles del manejo de permisos
- [Guía de Cobertura](docs/COVERAGE_GUIDE.es.md) - Guía de cobertura de código y testing
- [Configuración de Notificaciones](docs/NOTIFICATIONS_SETUP.es.md) - Configuración de notificaciones de Discord
- [Referencia de API](docs/API_REFERENCE.es.md) - Documentación completa de la API
- [Ejemplos](docs/EXAMPLES.es.md) - Ejemplos de código y casos de uso

## Contribuir

¡Aceptamos contribuciones! Consulta nuestra [Guía de Contribución](docs/CONTRIBUTING.es.md) para más detalles.

## Licencia

Este proyecto está licenciado bajo la Licencia MIT - consulta el archivo [LICENSE](docs/LICENSE) para más detalles.

## Soporte

- 📧 Email: belizairesmoy72@gmail.com
- 🐛 Issues: [GitHub Issues](https://github.com/ismoy/ImagePickerKMP/issues)
- 📖 Documentación: [Wiki](https://github.com/ismoy/ImagePickerKMP/wiki)
- 💬 Discord: [Canal de la comunidad](https://discord.gg/EjSQTeyh)

## Changelog

Consulta [CHANGELOG.es.md](docs/CHANGELOG.es.md) para una lista completa de cambios y actualizaciones.

---

**Hecho con ❤️ para la comunidad Kotlin Multiplatform** 