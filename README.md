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
![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-green)
![Android](https://img.shields.io/badge/Platform-Android-green)
![iOS](https://img.shields.io/badge/Platform-iOS-blue)
![Coverage Status](https://img.shields.io/codecov/c/github/ismoy/ImagePickerKMP)



# ImagePickerKMP
**Cross‑platform Image Picker & Camera Library (Android & iOS)**  
Built with **Kotlin Multiplatform** + **Compose Multiplatform** + **Kotlin/Native**.


Este documento también está disponible en español: [README.es.md](README.es.md)

## Features – Camera, Image Picker & Gallery for Android & iOS

- 📱 **Cross-platform**: Works on Android and iOS
- 📸 **Camera Integration**: Direct camera access with photo capture
- 🎨 **Customizable UI**: Custom dialogs and confirmation views
- 🔒 **Permission Handling**: Smart permission management for both platforms
- 🎯 **Easy Integration**: Simple API with Compose Multiplatform
- 🔧 **Highly Configurable**: Extensive customization options

## Quick Start – Kotlin Multiplatform Image Picker Integration

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

## Platform-Specific Integration

### Android Native (Jetpack Compose)

For detailed Android integration guide, see: [Android Integration Guide](docs/INTEGRATION_GUIDE.md#android-native-jetpack-compose)

### iOS Native (Swift/SwiftUI)

For detailed iOS integration guide, see: [iOS Integration Guide](docs/INTEGRATION_GUIDE.md#ios-native-swiftswiftui)

### Kotlin Multiplatform/Compose Multiplatform

For detailed KMP integration guide, see: [Kotlin Multiplatform Integration Guide](docs/INTEGRATION_GUIDE.md#kotlin-multiplatform--compose-multiplatform)

## Documentation

- [Integration Guide](docs/INTEGRATION_GUIDE.md) - Complete setup and integration guide
- [Customization Guide](docs/CUSTOMIZATION_GUIDE.md) - UI and behavior customization
- [Internationalization Guide](docs/I18N_GUIDE.md) - Multi-language support guide
- [Permissions Guide](docs/PERMISSION.md) - Permission handling details
- [Coverage Guide](docs/COVERAGE_GUIDE.md) - Code coverage and testing guide
- [Notifications Setup](docs/NOTIFICATIONS_SETUP.md) - Discord notifications setup
- [API Reference](docs/API_REFERENCE.md) - Complete API documentation
- [Examples](docs/EXAMPLES.md) - Code examples and use cases

## Contributing

We welcome contributions! See our [Contributing Guide](docs/CONTRIBUTING.md) for details.

## License

This project is licensed under the MIT License - see the [LICENSE](docs/LICENSE) file for details.

## Support

- 📧 Email: belizairesmoy72@gmail.com
- 🐛 Issues: [GitHub Issues](https://github.com/ismoy/ImagePickerKMP/issues)
- 📖 Documentation: [Wiki](https://github.com/ismoy/ImagePickerKMP/wiki)
- 💬 Discord: [Community Channel](https://discord.com/channels/1393705692484993114/1393706133864190133)

## Changelog

See [CHANGELOG.md](docs/CHANGELOG.md) for a complete list of changes and updates.

---

**Made with ❤️ for the Kotlin Multiplatform community**
