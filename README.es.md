[![CI](https://github.com/ismoy/ImagePickerKMP/workflows/CI/badge.svg)](https://github.com/ismoy/ImagePickerKMP/actions)
[![Code Coverage](https://codecov.io/gh/ismoy/ImagePickerKMP/branch/main/graph/badge.svg)](https://codecov.io/gh/ismoy/ImagePickerKMP)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-lightgrey.svg)](https://kotlinlang.org/docs/multiplatform.html)

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

Para la guía detallada de integración en Android, consulta: [Guía de Integración Android](INTEGRATION_GUIDE.es.md#android-nativo-jetpack-compose)

### iOS Nativo (Swift/SwiftUI)

Para la guía detallada de integración en iOS, consulta: [Guía de Integración iOS](INTEGRATION_GUIDE.es.md#ios-nativo-swiftswiftui)

### Kotlin Multiplatform/Compose Multiplatform

Para la guía detallada de integración en KMP, consulta: [Guía de Integración Kotlin Multiplatform](INTEGRATION_GUIDE.es.md#kotlin-multiplatformcompose-multiplatform)

## Documentación

- [Guía de Integración](INTEGRATION_GUIDE.es.md) - Guía completa de configuración e integración
- [Guía de Personalización](CUSTOMIZATION_GUIDE.es.md) - Personalización de UI y comportamiento
- [Guía de Internacionalización](I18N_GUIDE.es.md) - Guía de soporte multilenguaje
- [Guía de Permisos](PERMISSION.es.md) - Detalles del manejo de permisos
- [Guía de Cobertura](COVERAGE_GUIDE.es.md) - Guía de cobertura de código y testing
- [Configuración de Notificaciones](NOTIFICATIONS_SETUP.es.md) - Configuración de notificaciones de Discord
- [Referencia de API](API_REFERENCE.es.md) - Documentación completa de la API
- [Ejemplos](EXAMPLES.es.md) - Ejemplos de código y casos de uso

## Contribuir

¡Aceptamos contribuciones! Consulta nuestra [Guía de Contribución](CONTRIBUTING.es.md) para más detalles.

## Licencia

Este proyecto está licenciado bajo la Licencia MIT - consulta el archivo [LICENSE](LICENSE) para más detalles.

## Soporte

- 📧 Email: support@imagepickerkmp.com
- 🐛 Issues: [GitHub Issues](https://github.com/ismoy/ImagePickerKMP/issues)
- 📖 Documentación: [Wiki](https://github.com/ismoy/ImagePickerKMP/wiki)

## Changelog

Consulta [CHANGELOG.es.md](CHANGELOG.es.md) para una lista completa de cambios y actualizaciones.

---

**Hecho con ❤️ para la comunidad Kotlin Multiplatform** 