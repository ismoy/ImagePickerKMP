[![CI](https://github.com/ismoy/ImagePickerKMP/workflows/CI/badge.svg)](https://github.com/ismoy/ImagePickerKMP/actions)
[![Code Coverage](https://codecov.io/gh/ismoy/ImagePickerKMP/branch/main/graph/badge.svg)](https://codecov.io/gh/ismoy/ImagePickerKMP)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-lightgrey.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.ismoy/imagepickerkmp.svg?label=Maven%20Central)](https://search.maven.org/artifact/io.github.ismoy/imagepickerkmp)
[![GitHub Repo stars](https://img.shields.io/github/stars/ismoy/ImagePickerKMP?style=social)](https://github.com/ismoy/ImagePickerKMP/stargazers)
[![GitHub last commit](https://img.shields.io/github/last-commit/ismoy/ImagePickerKMP)](https://github.com/ismoy/ImagePickerKMP/commits/main)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](https://github.com/ismoy/ImagePickerKMP/pulls)
[![Discord](https://img.shields.io/discord/1393705692484993114.svg?label=Discord&logo=discord&color=7289da)](https://discord.com/channels/1393705692484993114/1393706133864190133)
[![official project](http://jb.gg/badges/official.svg)](https://github.com/JetBrains#jetbrains-on-github)

# ImagePickerKMP

Una librería moderna y multiplataforma para selección de imágenes y cámara en Kotlin Multiplatform (KMP), con experiencia nativa en Android e iOS.

Este documento también está disponible en inglés: [README.md](README.md)

## Características

- 📱 **Multiplataforma**: Funciona en Android y iOS
- 📸 **Integración de cámara**: Acceso directo a la cámara y captura de fotos
- 🎨 **UI personalizable**: Diálogos y vistas de confirmación personalizables
- 🔒 **Gestión de permisos**: Manejo inteligente de permisos en ambas plataformas
- 🎯 **Fácil integración**: API simple con Compose Multiplatform
- 🔧 **Altamente configurable**: Muchas opciones de personalización

## Inicio rápido

### Instalación

Agrega la dependencia en tu `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.0")
}
```

### Uso básico

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

## Compatibilidad de plataformas

| Plataforma | Versión mínima | Estado |
|------------|----------------|--------|
| Android    | API 21+        | ✅     |
| iOS        | iOS 12.0+      | ✅     |

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
- 💬 Discord: [Canal de la comunidad](https://discord.com/channels/1393705692484993114/1393706133864190133)

## Changelog

Consulta [CHANGELOG.es.md](docs/CHANGELOG.es.md) para una lista completa de cambios y actualizaciones.

---

**Hecho con ❤️ para la comunidad Kotlin Multiplatform**

[![Discord](https://img.shields.io/discord/1393705692484993114.svg?label=Discord&logo=discord&color=7289da)](https://discord.com/channels/1393705692484993114/1393706133864190133) 