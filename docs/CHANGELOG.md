# Changelog

All notable changes to ImagePickerKMP will be documented here.

Format: [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) · Versioning: [Semantic Versioning](https://semver.org/spec/v2.0.0.html)

---

## [Unreleased]

### Fixed
- **Android: crash when no camera app is present** — `launchCamera()` launched `MediaStore.ACTION_IMAGE_CAPTURE` unguarded, so an unresolvable intent killed the consuming app with `ActivityNotFoundException`. The throw happened inside the library's own `LaunchedEffect`, so callers could not catch it. It now reports `onError` then `onDismiss` and leaves `result` as `ImagePickerResult.Error`, matching the iOS `CameraPresenter`. Regression introduced in 1.1.0 when the in-app CameraX preview was replaced by the system camera intent
- **A dismissal following an error no longer overwrites the error** — platforms report an unavailable camera as `onError(...)` then `onDismiss()`, which previously left `result` as `Dismissed`, making "no camera app" indistinguishable from "user cancelled". Affected iOS as well, which now reports `Error` for an unavailable camera instead of `Dismissed`
- **Terminal outcomes no longer wedge the picker** — supplying a custom `onError`/`onDismiss` to `launchCamera()`/`launchGallery()` left the internal mode active with `result` stuck on `Loading`, so the next launch was silently ignored. Internal state now settles before the caller's callback runs
- **Android: gallery launch failures are reported with a localized message** rather than the raw `ActivityNotFoundException` text, and follow the same `onError` then `onDismiss` contract as the camera path

### Added
- `camera_unavailable_error` / `gallery_unavailable_error` translations across all 12 supported languages

---

## [2.0.1] — 2026-07-28

### Added
- **Unit test coverage across all modules** — `imagepicker-core`, `imagepickerkmp-photo`, `imagepickerkmp-video`, `imagepickerkmp-audio`, `imagepickerkmp-audio-player`, `imagepickerkmp-scanner`, `imagepickerkmp-video-player`
- **Kover coverage enforcement** — 90% line coverage minimum enforced per module via `koverVerify`
- **Aggregated coverage report** — `./gradlew koverMergedXmlReport` merges all per-module reports into a single `build/reports/kover/report.xml` for Codecov
- **Codecov integration** — CI uploads merged coverage report; badge shows real coverage

### Changed
- CI workflow updated to JDK 21 (matching module compile targets)
- CI now runs `jvmTest` and `testDebugUnitTest` per module in parallel
- Documentation restructured: removed Spanish duplicates, consolidated docs, rewrote all READMEs

---

## [1.0.41] — 2026-05-05

### Added
- **`CameraScaleType` enum** — configurable camera preview scale type on Android (`FILL_CENTER`, `FILL_START`, `FILL_END`, `FIT_CENTER`, `FIT_START`, `FIT_END`)
- `CameraCaptureConfig.cameraScaleType` defaults to `FILL_CENTER`
- `PermissionAndConfirmationConfig.confirmationImageContentScale` — controls how the captured photo is scaled in the post-capture confirmation screen (Android); defaults to `ContentScale.Crop`

### Changed
- All Spanish inline comments across the codebase translated to English

---

## [1.0.40] — 2026-04-29

### Added
- **`PhotoResult.absolutePath`** — returns the absolute file system path as a `String?`
  - Android: resolves `content://` URIs via `ContentResolver`
  - iOS: extracts path from `URL.path`
  - Desktop/Web: strips `file://` prefix

---

## [1.0.39] — 2026-04-29

### Fixed
- **Android — Camera preview black/blank on API 24–30** — `PreviewView` now uses `COMPATIBLE` mode (TextureView) on API ≤ 30 instead of `PERFORMANCE` (SurfaceView), which does not render in Compose on those versions
- Camera initialization delay extended to Android 7–11 (was only Android 10)

---

## [1.0.38] — 2026-04-29

### Added
- **`PhotoResult.toPath()`** — converts a photo URI to a `kotlinx.io.files.Path`

### Changed
- Kotlin upgraded to `2.3.20`
- Compose Multiplatform upgraded to `1.10.3`
- Android Gradle Plugin upgraded to `8.13.2`

---

## [1.0.35-alpha1] — 2026-03-28

### Added
- **`rememberImagePickerKMP`** — new unified Compose state-holder API
  - `ImagePickerKMPState.launchCamera(...)` / `.launchGallery(...)` with per-launch overrides
  - `ImagePickerKMPState.result: ImagePickerResult` — observable sealed state (`Idle | Loading | Success | Dismissed | Error`)
  - `ImagePickerKMPState.reset()`
  - `ImagePickerKMPConfig` — single configuration object for all defaults
  - `ImagePickerResult.Success.photos: List<PhotoResult>` and `.first: PhotoResult?`

### Deprecated
- `ImagePickerLauncher` — replaced by `rememberImagePickerKMP`. Still works; compiler emits a migration warning.
- `GalleryPickerLauncher` — same deprecation policy.

### Fixed
- `ImagePickerLauncher` now renders inside a full-screen `Dialog` on Android, fixing invisible camera preview when the composable is placed outside a `fillMaxSize` container

### ⚠️ Breaking Changes
- **Minimum Kotlin version is now 2.3.20** — the library is compiled with Kotlin 2.3.20 and the KMP ABI is not backward-compatible. Projects on Kotlin < 2.3.x will get a compile error.

### Changed
- Kotlin `2.1.21` → `2.3.20`
- Compose Multiplatform `1.9.1` → `1.10.3`
- Ktor `3.0.2` → `3.4.1`
- CameraX `1.5.1` → `1.5.3`
- `fileSize` now returns **bytes** instead of KB — divide by 1024 to get KB: `val kb = (photo.fileSize ?: 0) / 1024.0`

---

## [1.0.22] — 2025-XX-XX

### Added
- `customDeniedDialog` and `customSettingsDialog` in `PermissionAndConfirmationConfig` — fully custom Compose permission dialogs
- Automatic image compression for camera and gallery (`CompressionLevel.LOW / MEDIUM / HIGH`)
- Smart gallery vs file-explorer picker on Android — images open the native gallery, PDFs open the file explorer
- `AndroidGalleryConfig` for manual control over picker strategy
- Multi-image selection on iOS via `PHPickerViewController` (iOS 14+)
- `selectionLimit` parameter in `GalleryConfig` (max 30)

### Fixed
- `GalleryPickerLauncher` opened the Downloads folder instead of the gallery on some Android versions
- iOS crop coordinate calculations causing off-center crops
- Layout z-index conflict where crop controls appeared behind other UI layers
- Zoomed images appearing above crop header controls

---

## [1.0.1] — 2025-01-15

### Added
- First official stable release
- Cross-platform camera integration for Android and iOS
- Photo capture, gallery selection, crop, compression
- Smart permission handling with customisable dialogs
- EXIF metadata extraction (Android / iOS)
- Internationalization — English, Spanish, French (auto-detected)
- Front-camera orientation correction
- Extension functions: `loadPainter()`, `loadBytes()`, `loadBase64()`, `loadImageBitmap()`
- PDF selection support

---

## Migration Guide

### From 1.0.x to 2.0.x

#### `ImagePickerLauncher` → `rememberImagePickerKMP`

```kotlin
// Before (still works with deprecation warning)
ImagePickerLauncher(
    config = ImagePickerConfig(
        onPhotoCaptured = { photo -> /* ... */ },
        onError = { e -> /* ... */ }
    )
)

// After
val picker = rememberImagePickerKMP(
    config = ImagePickerKMPConfig(
        cameraCaptureConfig = CameraCaptureConfig(...)
    )
)
picker.launchCamera()
when (val result = picker.result) {
    is ImagePickerResult.Success -> result.photos.forEach { /* ... */ }
    else -> Unit
}
```

#### `fileSize` is now in bytes

```kotlin
// Before (was KB)
val fileSizeKb = photo.fileSize  // e.g. 350

// After (bytes)
val fileSizeKb = (photo.fileSize ?: 0) / 1024.0  // convert manually
```

---

*For questions about a specific version, open an [issue](https://github.com/ismoy/ImagePickerKMP/issues) or check the [FAQ](FAQ.md).*
