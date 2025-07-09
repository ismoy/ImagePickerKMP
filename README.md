[![official project](http://jb.gg/badges/official.svg)](https://github.com/JetBrains#jetbrains-on-github)

# ImagePickerKMP

A modern, cross-platform image picker library for Kotlin Multiplatform (KMP) that provides a seamless camera experience on both Android and iOS platforms.

## Features

- 📱 **Cross-platform**: Works on Android and iOS
- 📸 **Camera Integration**: Direct camera access with photo capture
- 🎨 **Customizable UI**: Custom dialogs and confirmation views
- 🔒 **Permission Handling**: Smart permission management for both platforms
- 📊 **Analytics Support**: Optional analytics integration (Firebase)
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

### Analytics Integration
- Optional Firebase Analytics
- Privacy-focused data collection
- Custom analytics support
- Performance metrics

### Customization
- Custom UI themes
- Personalized dialogs
- Custom callbacks
- Advanced configurations

## Documentation

- [Integration Guide](INTEGRATION_GUIDE.md) - Complete setup and integration guide
- [Analytics Guide](ANALYTICS_GUIDE.md) - Analytics configuration and usage
- [Customization Guide](CUSTOMIZATION_GUIDE.md) - UI and behavior customization
- [Permission Guide](Permission_readme_English.md) - Permission handling details
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
