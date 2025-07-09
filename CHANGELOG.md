# Changelog

All notable changes to ImagePickerKMP will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Analytics integration support
- Custom permission dialogs
- Custom confirmation views
- High-quality photo capture option
- Memory optimization improvements
- Better error handling and recovery

### Changed
- Improved permission handling flow
- Enhanced UI components
- Better cross-platform compatibility

### Fixed
- iOS permission denial flow
- Android camera initialization issues
- Memory leaks in photo processing
- Permission dialog display issues

## [1.0.0] - 2024-01-15

### Added
- Initial release of ImagePickerKMP
- Cross-platform camera integration
- Basic photo capture functionality
- Permission handling for Android and iOS
- Simple UI components
- Error handling and exceptions
- Photo result data class
- Capture preferences (FAST, BALANCED, HIGH_QUALITY)

### Features
- **Android Support**: Full camera integration using CameraX
- **iOS Support**: Native camera integration using AVFoundation
- **Permission Management**: Smart permission handling for both platforms
- **Photo Capture**: High-quality photo capture with preview
- **Error Handling**: Comprehensive error handling and user feedback
- **Customization**: Basic customization options for UI and behavior

### Technical Details
- **Minimum SDK**: Android API 21+, iOS 12.0+
- **Kotlin Version**: 1.8+
- **Compose Multiplatform**: Full support
- **Dependencies**: Minimal external dependencies

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

### Version 1.0.0 (Current Stable)
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

### From 0.9.0 to 1.0.0

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
   implementation("io.github.ismoy:imagepickerkmp:1.0.0")
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
| 1.0.0   | 21+         | 12.0+       | 1.8+           | 1.4+            |
| 0.9.0   | 21+         | 12.0+       | 1.8+           | 1.4+            |
| 0.8.0   | 21+         | N/A         | 1.8+           | 1.4+            |
| 0.7.0   | 21+         | N/A         | 1.8+           | 1.4+            |

## Known Issues

### Version 1.0.0
- **Issue**: Memory usage high with large photos
  - **Status**: Fixed in next release
  - **Workaround**: Use image compression

- **Issue**: iOS permission dialog sometimes doesn't show
  - **Status**: Fixed in next release
  - **Workaround**: Use custom permission handler

### Version 0.9.0
- **Issue**: Camera initialization slow on some devices
  - **Status**: Fixed in 1.0.0
  - **Workaround**: Use FAST capture preference

### Version 0.8.0
- **Issue**: Permission handling incomplete
  - **Status**: Fixed in 0.9.0
  - **Workaround**: Manual permission handling

## Roadmap

### Version 1.1.0 (Planned)
- **Features**:
  - Advanced analytics integration
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
- **Documentation**: [README.md](README.md)
- **Issues**: [GitHub Issues](https://github.com/ismoy/ImagePickerKMP/issues)
- **Discussions**: [GitHub Discussions](https://github.com/ismoy/ImagePickerKMP/discussions)
- **Email**: support@imagepickerkmp.com

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