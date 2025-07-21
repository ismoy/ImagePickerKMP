# Contributing to ImagePickerKMP

Thank you for your interest in contributing to ImagePickerKMP! This document provides guidelines and information for contributors.

## Table of Contents

- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Code Style](#code-style)
- [Testing](#testing)
- [Pull Request Process](#pull-request-process)
- [Issue Reporting](#issue-reporting)
- [Feature Requests](#feature-requests)
- [Code of Conduct](#code-of-conduct)

## Getting Started

### Prerequisites

Before contributing, ensure you have:

- **Kotlin 1.8+** installed
- **Android Studio** or **IntelliJ IDEA** with Kotlin plugin
- **Xcode** (for iOS development)
- **Git** for version control
- **Gradle** for build management

### Required Tools

- **Android SDK**: API 21+ for Android development
- **Xcode**: Latest version for iOS development
- **Simulators/Emulators**: For testing on both platforms

## Development Setup

### 1. Fork and Clone

```bash
# Fork the repository on GitHub
# Then clone your fork
git clone https://github.com/ismoy/ImagePickerKMP.git
cd ImagePickerKMP
```

### 2. Set Up Development Environment

```bash
# Add the original repository as upstream
git remote add upstream https://github.com/ismoy/ImagePickerKMP.git

# Create a development branch
git checkout -b development

# Build the project
./gradlew build
```

### 3. Verify Setup

```bash
# Run tests
./gradlew test

# Run Android tests
./gradlew androidTest

# Run iOS tests (requires macOS)
./gradlew iosTest
```

### 4. IDE Configuration

#### Android Studio / IntelliJ IDEA

1. **Import Project**: Open the project in Android Studio
2. **SDK Setup**: Ensure Android SDK is properly configured
3. **Kotlin Plugin**: Verify Kotlin plugin is installed and enabled
4. **Gradle Sync**: Sync the project with Gradle

#### Xcode (for iOS development)

1. **Open Project**: Open the iOS project in Xcode
2. **Framework Setup**: Ensure the Kotlin framework is properly linked
3. **Simulator**: Set up iOS simulator for testing

## Code Style

### Kotlin Style Guide

We follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html).

#### Naming Conventions

```kotlin
// Classes and objects
class ImagePickerLauncher
object Constants

// Functions and variables
fun capturePhoto()
var isCapturing: Boolean = false

// Constants
const val CAMERA_PERMISSION = "android.permission.CAMERA"

// Enums
enum class CapturePhotoPreference {
    FAST,
    BALANCED,
    HIGH_QUALITY
}
```

#### Code Formatting

```kotlin
// Use 4 spaces for indentation
@Composable
fun MyComponent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Hello World")
    }
}

// Line length: 120 characters max
// Use trailing commas for better git diffs
val longList = listOf(
    "item1",
    "item2",
    "item3",
)
```

#### Documentation

```kotlin
/**
 * Captures a photo using the device camera.
 *
 * @param context The context for camera operations
 * @param onPhotoCaptured Callback when photo is captured
 * @param onError Callback when error occurs
 * @param preference Photo capture quality preference
 */
@Composable
fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    preference: CapturePhotoPreference = CapturePhotoPreference.BALANCED
) {
    // Implementation
}
```

### Platform-Specific Code

#### Android

```kotlin
// Use expect/actual for platform-specific code
expect fun requestCameraPermission(context: Context): Boolean

// Android implementation
actual fun requestCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}
```

#### iOS

```kotlin
// iOS implementation
actual fun requestCameraPermission(context: Context): Boolean {
    return AVCaptureDevice.authorizationStatus(for: .video) == .authorized
}
```

### Static Analysis

Before submitting a pull request, please run static analysis to ensure your code meets the project's quality standards:

```sh
./gradlew detekt
```

Fix any issues reported by Detekt before submitting your PR.

## Testing

### Writing Tests

#### Unit Tests

```kotlin
class ImagePickerTest {
    @Test
    fun `should capture photo successfully`() {
        // Given
        val mockContext = mock<Context>()
        val mockResult = PhotoResult(
            uri = Uri.parse("content://photo"),
            size = 1024L,
            format = "JPEG",
            width = 1920,
            height = 1080
        )
        
        // When
        val result = capturePhoto(mockContext)
        
        // Then
        assertEquals(mockResult, result)
    }
}
```

#### Integration Tests

```kotlin
@RunWith(AndroidJUnit4::class)
class ImagePickerIntegrationTest {
    @Test
    fun testPhotoCaptureFlow() {
        // Test complete photo capture flow
        // Including permission handling and UI interactions
    }
}
```

#### Platform Tests

```kotlin
// Android tests
@RunWith(AndroidJUnit4::class)
class AndroidImagePickerTest {
    @Test
    fun testAndroidCameraIntegration() {
        // Test Android-specific functionality
    }
}

// iOS tests
class IOSImagePickerTest {
    @Test
    fun testIOSCameraIntegration() {
        // Test iOS-specific functionality
    }
}
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests ImagePickerTest

# Run with coverage
./gradlew test jacocoTestReport

# Run Android tests
./gradlew androidTest

# Run iOS tests
./gradlew iosTest
```

### Test Coverage

We aim for at least 80% test coverage. Focus on:

- **Core functionality**: Photo capture, permission handling
- **Error scenarios**: Permission denied, camera unavailable
- **Platform differences**: Android vs iOS behavior
- **UI interactions**: User interactions and callbacks

## Pull Request Process

### 1. Create a Feature Branch

```bash
# Create a new branch for your feature
git checkout -b feature/your-feature-name

# Or for bug fixes
git checkout -b fix/your-bug-description
```

### 2. Make Your Changes

- **Write code**: Implement your feature or fix
- **Add tests**: Include unit and integration tests
- **Update documentation**: Update relevant documentation
- **Follow style guide**: Ensure code follows our conventions

### 3. Commit Your Changes

```bash
# Use conventional commit messages
git commit -m "feat: add custom permission dialog support"
git commit -m "fix: resolve iOS permission denial issue"
git commit -m "docs: update API documentation"
```

### 4. Push and Create PR

```bash
# Push your branch
git push origin feature/your-feature-name

# Create pull request on GitHub
# Include detailed description of changes
```

### 5. PR Guidelines

#### Required Information

- **Title**: Clear, descriptive title
- **Description**: Detailed description of changes
- **Type**: Feature, bug fix, documentation, etc.
- **Testing**: How you tested your changes
- **Breaking Changes**: Any breaking changes
- **Related Issues**: Link to related issues

#### Example PR Description

```markdown
## Description
Adds support for custom permission dialogs in ImagePickerKMP.

## Changes
- Added `CustomPermissionDialog` composable
- Updated `ImagePickerLauncher` to support custom dialogs
- Added documentation and examples

## Testing
- Added unit tests for custom dialog functionality
- Tested on Android and iOS simulators
- Verified backward compatibility

## Breaking Changes
None - this is a backward-compatible addition

## Related Issues
Closes #123
```

### 6. Review Process

1. **Automated Checks**: CI/CD pipeline runs tests
2. **Code Review**: Maintainers review your code
3. **Address Feedback**: Make requested changes
4. **Merge**: Once approved, PR is merged

## Issue Reporting

### Bug Reports

When reporting bugs, include:

```markdown
## Bug Description
Brief description of the bug

## Steps to Reproduce
1. Step 1
2. Step 2
3. Step 3

## Expected Behavior
What you expected to happen

## Actual Behavior
What actually happened

## Environment
- Platform: Android/iOS
- Version: 1.0.1
- Device: Pixel 6 / iPhone 13
- OS Version: Android 12 / iOS 15

## Additional Information
- Screenshots (if applicable)
- Logs (if applicable)
- Code examples (if applicable)
```

### Feature Requests

For feature requests, include:

```markdown
## Feature Description
Brief description of the feature

## Use Case
Why this feature is needed

## Proposed Implementation
How you think it should be implemented

## Alternatives Considered
Other approaches you considered

## Additional Information
Any other relevant information
```

## Feature Requests

### Guidelines

- **Check existing issues**: Search for similar requests
- **Be specific**: Provide detailed requirements
- **Include examples**: Show how the feature would be used
- **Consider impact**: Think about breaking changes
- **Provide context**: Explain the use case

### Feature Request Template

```markdown
## Feature Request: [Feature Name]

### Problem Statement
Describe the problem this feature would solve

### Proposed Solution
Describe your proposed solution

### Use Cases
- Use case 1
- Use case 2
- Use case 3

### Implementation Details
Technical details about the implementation

### Alternatives
Other approaches that could be considered

### Additional Context
Any other relevant information
```

## Code of Conduct

### Our Standards

We are committed to providing a welcoming and inspiring community for all. We expect all contributors to:

- **Be respectful**: Treat others with respect
- **Be collaborative**: Work together constructively
- **Be constructive**: Provide helpful feedback
- **Be inclusive**: Welcome diverse perspectives
- **Be professional**: Maintain professional behavior

### Unacceptable Behavior

- **Harassment**: Any form of harassment or discrimination
- **Trolling**: Deliberately provocative behavior
- **Spam**: Unwanted promotional content
- **Inappropriate content**: Offensive or inappropriate material

### Enforcement

- **Warning**: First offense results in a warning
- **Temporary ban**: Repeated violations may result in temporary ban
- **Permanent ban**: Severe violations may result in permanent ban

### Reporting

If you experience or witness unacceptable behavior:

1. **Contact maintainers**: Reach out to project maintainers
2. **Provide details**: Include specific details about the incident
3. **Confidentiality**: Reports will be handled confidentially
4. **Action**: Appropriate action will be taken

## Development Workflow

### Branch Strategy

```bash
main                    # Production-ready code
├── develop            # Development branch
├── feature/*          # Feature branches
├── fix/*              # Bug fix branches
├── hotfix/*           # Hotfix branches
└── release/*          # Release branches
```

### Release Process

1. **Feature Development**: Develop features in feature branches
2. **Integration**: Merge features into develop branch
3. **Testing**: Test thoroughly on develop branch
4. **Release**: Create release branch from develop
5. **Production**: Merge release into main
6. **Tagging**: Tag releases with version numbers

### Version Management

We use [Semantic Versioning](https://semver.org/):

- **Major**: Breaking changes
- **Minor**: New features (backward compatible)
- **Patch**: Bug fixes (backward compatible)

## Getting Help

### Resources

- **Documentation**: [README.md](../README.md)
- **API Reference**: [API_REFERENCE.md](docs/API_REFERENCE.md)
- **Examples**: [EXAMPLES.md](docs/EXAMPLES.md)
- **Issues**: [GitHub Issues](https://github.com/ismoy/ImagePickerKMP/issues)
- **Discussions**: [GitHub Discussions](https://github.com/ismoy/ImagePickerKMP/discussions)

### Communication

- **GitHub Issues**: For bugs and feature requests
- **GitHub Discussions**: For questions and general discussion
- **Email**: belizairesmoy72@gmail.com
- **Discord**: [Comunidad Discord](https://discord.com/channels/1393705692484993114/1393706133864190133)

### Mentorship

New contributors can:

- **Ask questions**: Don't hesitate to ask for help
- **Request reviews**: Ask for code reviews from maintainers
- **Join discussions**: Participate in community discussions
- **Start small**: Begin with documentation or small fixes

## Recognition

### Contributors

We recognize contributors in several ways:

- **Contributors list**: Added to project contributors
- **Release notes**: Mentioned in release notes
- **Documentation**: Credit in documentation
- **Community**: Recognition in community discussions

### Hall of Fame

Top contributors are featured in our Hall of Fame:

- **Gold Contributors**: 50+ contributions
- **Silver Contributors**: 20+ contributions
- **Bronze Contributors**: 10+ contributions

---

**Thank you for contributing to ImagePickerKMP!** Your contributions help make this project better for everyone. 