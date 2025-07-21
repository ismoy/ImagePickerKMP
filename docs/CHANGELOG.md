# Changelog

All notable changes to ImagePickerKMP will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Custom permission dialogs
- Custom confirmation views
- High-quality photo capture option
- Memory optimization improvements
- Better error handling and recovery
- Gallery selection support for Android and iOS
- Customizable dialog texts for iOS and Android
- Accessibility improvements: contentDescription and configurable button sizes
- JPEG compression quality configuration for image processing
- Logger interface for configurable logging
- Unit test for PhotoResult
- Linter and static analysis (ktlint, detekt) configuration
- Example and documentation for internationalization
- CI and coverage badges in README
- **Internationalization (i18n) support**: Complete multi-language system with type-safe string resources
- **Automatic language detection**: Strings adapt to device language automatically
- **Support for English, Spanish, and French**: Ready-to-use translations for all three languages
- **Extensible language system**: Easy to add new languages without external dependencies
- **Automatic translations**: Permission dialogs and UI texts are now automatically translated by default
- **Front camera orientation correction**: Automatic correction of front camera image orientation to fix mirrored/rotated photos
- Automatic gallery permission handling: `GalleryPickerLauncher` now manages gallery permissions automatically on both Android and iOS, no manual request needed.
- Custom gallery permission dialog (Android): Added a dedicated, localizable dialog for gallery permissions, separate from the camera dialog.
- Multi-image selection on iOS: Implemented using `PHPickerViewController` (iOS 14+), replacing the old single-image picker.
- New instrumented and unit tests: Added and re-enabled tests to cover new permission flows and multi-image selection.
- Improved localization: New strings and translations for gallery permission dialogs.
- **Configurable selection limit for gallery picker**: Added `selectionLimit` parameter to `GalleryConfig` to control the maximum number of images that can be selected in the gallery picker. The limit is enforced at compile time with a maximum value of 30 images to prevent performance issues and crashes when selecting too many images on iOS.

### Changed
- Improved permission handling flow
- Enhanced UI components
- Better cross-platform compatibility
- Preview image in confirmation now uses FIT_CENTER to avoid zoom on gallery images
- Enhanced image processing with automatic orientation correction for front camera photos
- iOS gallery permission flow: Now requests permission directly via the system dialog, with no pre-permission custom dialog, matching native iOS behavior.
- Refactored permission logic: Gallery and camera permission handling is now more consistent and cross-platform.
- **Gallery selection limit**: Changed from unlimited selection (0) to configurable limit with maximum of 30 images to prevent performance issues and crashes on iOS when selecting too many images.

### Fixed
- iOS permission denial flow
- Android camera initialization issues
- Memory leaks in photo processing
- Permission dialog display issues
- Disparador (capture button) always centered, gallery button does not push it
- Front camera photos appearing mirrored or incorrectly oriented
- Dialog text errors: Gallery permission dialog no longer shows camera texts.
- Multi-image selection bugs: Fixed performance issues and crashes when selecting many images on iOS.
- Threading and casting errors: Resolved concurrency and type conversion issues in image selection.
- **iOS gallery performance issues**: Fixed crashes and performance degradation when selecting more than 30 images by implementing a configurable selection limit with compile-time validation.

### Documentation

- Thoroughly updated all Markdown documentation files to ensure all usage examples of `ImagePickerLauncher`, `GalleryPickerLauncher`, and related components reflect the current, real API.
- Replaced all outdated examples using the old API (with top-level callbacks and handlers) with the correct pattern: all configuration and handlers are now shown nested inside `config = ImagePickerConfig(...)`, with custom handlers further nested as required.
- Ensured all documentation in both English and Spanish is fully synchronized and accurate.
- All examples and guides updated to reflect new automatic permission handling and multi-image selection on iOS.
- Added notes on platform differences for permission and selection behavior (Android vs iOS).

## [1.0.1] - 2025-01-15

### Added
- **First Official Release** of ImagePickerKMP
- **Cross-platform camera integration** for Android and iOS
- **High-quality photo capture** with preview and confirmation
- **Smart permission handling** for both platforms
- **Customizable UI components** with Compose Multiplatform
- **Comprehensive error handling** and user feedback
- **Photo result data class** with metadata
- **Capture preferences** (FAST, BALANCED, HIGH_QUALITY)
- **Gallery selection support** for both platforms
- **Internationalization (i18n)** with English, Spanish, and French
- **Front camera orientation correction** to fix mirrored photos
- **Memory optimization** and performance improvements
- **Extensive documentation** and examples
- **CI/CD pipeline** with automated testing and deployment
- **Code coverage** and quality assurance

### Features
- **Android Support**: Full camera integration using CameraX with modern UI
- **iOS Support**: Native camera integration using AVFoundation
- **Permission Management**: Smart permission handling with custom dialogs
- **Photo Capture**: High-quality photo capture with automatic orientation correction
- **Error Handling**: Comprehensive error handling with specific exception types
- **Customization**: Extensive customization options for UI and behavior
- **Internationalization**: Multi-language support with automatic detection
- **Testing**: Complete test suite with unit and UI tests
- **Documentation**: Comprehensive guides and API reference

### Technical Details
- **Minimum SDK**: Android API 21+, iOS 12.0+
- **Kotlin Version**: 1.9+
- **Compose Multiplatform**: Full support with Material components
- **Dependencies**: Optimized external dependencies
- **Code Coverage**: >40% with comprehensive testing
- **CI/CD**: Automated build, test, and deployment pipeline

## [0.9.0] - 2024-01-10

### Added
- Beta release for testing
- Core camera functionality
- Basic permission handling
- Photo capture and preview
- Error handling framework

### Known Issues
- iOS permission flow incomplete
- Memory optimization needed
- Limited customization options

## [0.8.0] - 2024-01-05

### Added
- Alpha release
- Basic camera integration
- Permission request handling
- Photo capture functionality

### Limitations
- Android only
- Basic UI
- Limited error handling

## [0.7.0] - 2024-01-01

### Added
- Initial development release
- Project structure setup
- Basic camera functionality
- Permission handling

---

## Version History

### Version 1.0.1 (Current Stable)
- **Release Date**: January 15, 2024
- **Status**: Stable
- **Key Features**:
  - Cross-platform camera integration
  - Smart permission handling
  - High-quality photo capture
  - Comprehensive error handling
  - Customizable UI components

### Version 0.9.0 (Beta)
- **Release Date**: January 10, 2024
- **Status**: Beta
- **Key Features**:
  - Core functionality complete
  - Basic permission handling
  - Photo capture and preview
  - Error handling framework

### Version 0.8.0 (Alpha)
- **Release Date**: January 5, 2024
- **Status**: Alpha
- **Key Features**:
  - Basic camera integration
  - Permission request handling
  - Photo capture functionality

### Version 0.7.0 (Development)
- **Release Date**: January 1, 2024
- **Status**: Development
- **Key Features**:
  - Project structure setup
  - Basic camera functionality
  - Permission handling

## Migration Guide

### From 0.9.0 to 1.0.1

#### Breaking Changes
- Updated permission handling API
- Changed error handling structure
- Modified photo result data class

#### Migration Steps
1. **Update Dependencies**
   ```kotlin
   // Old
   implementation("io.github.ismoy:imagepickerkmp:0.9.0")
   
   // New
   implementation("io.github.ismoy:imagepickerkmp:1.0.1")
   ```

2. **Update Permission Handling**
   ```kotlin
   // Old
   RequestCameraPermission(
       onPermissionGranted = { /* ... */ },
       onPermissionDenied = { /* ... */ }
   )
   
   // New
   RequestCameraPermission(
       onPermissionPermanentlyDenied = { /* ... */ },
       onResult = { granted -> /* ... */ }
   )
   ```

3. **Update Error Handling**
   ```kotlin
   // Old
   onError = { error ->
       // Handle generic error
   }
   
   // New
   onError = { exception ->
       when (exception) {
           is CameraPermissionException -> { /* ... */ }
           is PhotoCaptureException -> { /* ... */ }
           else -> { /* ... */ }
       }
   }
   ```

### From 0.8.0 to 0.9.0

#### Breaking Changes
- Added iOS support
- Updated API structure
- Changed permission handling

#### Migration Steps
1. **Update Platform Support**
   ```kotlin
   // Old (Android only)
   ImagePickerLauncher(
       context = LocalContext.current,
       // ...
   )
   
   // New (Cross-platform)
   ImagePickerLauncher(
       context = LocalContext.current, // null for iOS
       // ...
   )
   ```

2. **Update Permission Handling**
   ```kotlin
   // Old
   requestCameraPermission()
   
   // New
   RequestCameraPermission(
       onPermissionGranted = { /* ... */ },
       onPermissionDenied = { /* ... */ }
   )
   ```

## Deprecation Policy

### Deprecated Features
- No deprecated features in current version

### Removal Schedule
- Deprecated features will be removed in the next major version
- Users will be notified 6 months before removal
- Migration guides will be provided

## Compatibility Matrix

| Version | Android API | iOS Version | Kotlin Version | Compose Version |
|---------|-------------|-------------|----------------|-----------------|
| 1.0.1   | 21+         | 12.0+       | 1.8+           | 1.4+            |
| 0.9.0   | 21+         | 12.0+       | 1.8+           | 1.4+            |
| 0.8.0   | 21+         | N/A         | 1.8+           | 1.4+            |
| 0.7.0   | 21+         | N/A         | 1.8+           | 1.4+            |

## Known Issues

### Version 1.0.1
- **Issue**: Memory usage high with large photos
  - **Status**: Fixed in next release
  - **Workaround**: Use image compression

- **Issue**: iOS permission dialog sometimes doesn't show
  - **Status**: Fixed in next release
  - **Workaround**: Use custom permission handler

### Version 0.9.0
- **Issue**: Camera initialization slow on some devices
  - **Status**: Fixed in 1.0.1
  - **Workaround**: Use FAST capture preference

### Version 0.8.0
- **Issue**: Permission handling incomplete
  - **Status**: Fixed in 0.9.0
  - **Workaround**: Manual permission handling

## Roadmap

### Version 1.1.0 (Planned)
- **Features**:
  
  - Custom UI themes
  - Video capture support
  - Image filters and effects
  - Batch photo capture

### Version 1.2.0 (Planned)
- **Features**:
  - AR camera integration
  - Real-time filters
  - Social media sharing
  - Cloud storage integration
  - Advanced editing tools

### Version 2.0.0 (Future)
- **Features**:
  - Complete UI redesign
  - Advanced customization
  - Plugin system
  - Performance optimizations
  - Extended platform support

## Contributing

### How to Contribute
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

### Development Setup
```bash
# Clone the repository
git clone https://github.com/ismoy/ImagePickerKMP.git

# Navigate to project directory
cd ImagePickerKMP

# Build the project
./gradlew build

# Run tests
./gradlew test
```

### Release Process
1. **Version Bump**: Update version in `build.gradle.kts`
2. **Changelog**: Update this changelog
3. **Tests**: Run all tests
4. **Documentation**: Update documentation
5. **Release**: Create GitHub release
6. **Publish**: Publish to Maven Central

## Support

### Getting Help
- **Documentation**: [README.md](../README.md)
- **Issues**: [GitHub Issues](https://github.com/ismoy/ImagePickerKMP/issues)
- **Discussions**: [GitHub Discussions](https://github.com/ismoy/ImagePickerKMP/discussions)
- **Email**: belizairesmoy72@gmail.com

### Reporting Issues
When reporting issues, please include:
- Version number
- Platform (Android/iOS)
- Device information
- Steps to reproduce
- Expected vs actual behavior
- Logs (if applicable)

### Feature Requests
For feature requests, please:
- Check existing issues first
- Provide detailed description
- Include use case examples
- Consider implementation complexity

---

**Note**: This changelog is maintained by the ImagePickerKMP team. For questions or suggestions, please contact us. 