# ImagePickerKMP

**A complete media library for Kotlin Multiplatform — photos, video, audio, scanning, and playback in one ecosystem.**

<img referrerpolicy="no-referrer-when-downgrade" src="https://static.scarf.sh/a.png?x-pxid=b9cde436-d518-45b7-9a4a-7b4e084aeffa" />

<p align="center">
  <img src="https://raw.githubusercontent.com/ismoy/CameraKMP/main/thumnailImagePickerKMP.png" alt="ImagePickerKMP Banner" width="100%">
</p>

<p align="center">
  <a href="https://github.com/ismoy/ImagePickerKMP/actions"><img src="https://github.com/ismoy/ImagePickerKMP/workflows/CI/badge.svg" alt="CI"></a>
  <a href="https://codecov.io/gh/ismoy/ImagePickerKMP"><img src="https://codecov.io/gh/ismoy/ImagePickerKMP/branch/main/graph/badge.svg" alt="Coverage"></a>
  <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License"></a>
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-2.3.20-blue.svg" alt="Kotlin"></a>
  <a href="https://github.com/ismoy/ImagePickerKMP/stargazers"><img src="https://img.shields.io/github/stars/ismoy/ImagePickerKMP?style=social" alt="Stars"></a>
</p>

<p align="center">
  <a href="https://search.maven.org/search?q=g:io.github.ismoy+a:imagepicker-core"><img src="https://img.shields.io/maven-central/v/io.github.ismoy/imagepicker-core.svg?label=Core" alt="Core"></a>
  <a href="https://search.maven.org/search?q=g:io.github.ismoy+a:imagepickerkmp"><img src="https://img.shields.io/maven-central/v/io.github.ismoy/imagepickerkmp.svg?label=Photo" alt="Photo"></a>
  <a href="https://search.maven.org/search?q=g:io.github.ismoy+a:imagepickerkmp-video"><img src="https://img.shields.io/maven-central/v/io.github.ismoy/imagepickerkmp-video.svg?label=Video" alt="Video"></a>
  <a href="https://search.maven.org/search?q=g:io.github.ismoy+a:imagepickerkmp-audio"><img src="https://img.shields.io/maven-central/v/io.github.ismoy/imagepickerkmp-audio.svg?label=Audio" alt="Audio"></a>
  <a href="https://search.maven.org/search?q=g:io.github.ismoy+a:imagepickerkmp-scanner"><img src="https://img.shields.io/maven-central/v/io.github.ismoy/imagepickerkmp-scanner.svg?label=Scanner" alt="Scanner"></a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-API%2024+-green" alt="Android">
  <img src="https://img.shields.io/badge/iOS-16.0+-blue" alt="iOS">
  <img src="https://img.shields.io/badge/Desktop-JDK%2021+-orange" alt="Desktop">
  <img src="https://img.shields.io/badge/Web-JS%20%7C%20WASM-yellow" alt="Web">
  <img src="https://img.shields.io/badge/Compose%20Multiplatform-1.11.1-brightgreen" alt="Compose">
</p>

<p align="center">
  <a href="https://imagepickerkmp.dev/">
    <img src="https://img.shields.io/badge/Documentation-imagepickerkmp.dev-0ea5e9?style=for-the-badge" alt="Docs">
  </a>
  &nbsp;
  <a href="https://github.com/ismoy/CameraKMP">
    <img src="https://img.shields.io/badge/Example%20App-CameraKMP-6366f1?style=for-the-badge" alt="Example">
  </a>
  &nbsp;
  <a href="https://github.com/sponsors/ismoy">
    <img src="https://img.shields.io/badge/Sponsor-%E2%9D%A4-red?style=for-the-badge&logo=github" alt="Sponsor">
  </a>
</p>

---

## The Ecosystem

ImagePickerKMP is a **modular** media library. Each module is independent — include only what your app needs.

| Module | Artifact | Description |
|--------|----------|-------------|
| **Photo** | `imagepickerkmp` | Camera capture and gallery image picking |
| **Video** | `imagepickerkmp-video` | Video recording and gallery video picking |
| **Audio** | `imagepickerkmp-audio` | Audio recording with waveform visualization |
| **Audio Player** | `imagepickerkmp-audio-player` | Voice message / audio file playback |
| **Scanner** | `imagepickerkmp-scanner` | Barcode and QR code scanning via live camera |
| **Video Player** | `imagepickerkmp-video-player` | Full-featured video playback with controls |

> **Requirements:** Kotlin **2.3.20** · Compose Multiplatform **1.11.1** · Android `minSdk` **24** · iOS **16.0+**

---
## Installation

Add only the modules you need. All modules are published to Maven Central.

```kotlin
// build.gradle.kts (commonMain)
dependencies {
    // Photo — camera capture and gallery image picking
    implementation("io.github.ismoy:imagepickerkmp:1.1.0")

    // Video — video recording and gallery video picking
    implementation("io.github.ismoy:imagepickerkmp-video:1.1.0") // SOON

    // Audio — audio recording
    implementation("io.github.ismoy:imagepickerkmp-audio:1.1.0") // SOON

    // Audio Player — voice message and audio file playback
    implementation("io.github.ismoy:imagepickerkmp-audioplayer:1.1.0") // SOON

    // Scanner — live barcode and QR code scanning
    implementation("io.github.ismoy:imagepickerkmp-scanner:1.1.0") // SOON

    // Video Player — full-featured video playback
    implementation("io.github.ismoy:imagepickerkmp-videoplayer:1.1.0") // SOON
}
```

### iOS — `Info.plist`

Every module that uses the camera or microphone requires a usage description. Add the ones relevant to your app:

```xml
<!-- Camera (Photo, Video, Scanner) -->
<key>NSCameraUsageDescription</key>
<string>Required for camera features.</string>

<!-- Microphone (Video, Audio) -->
<key>NSMicrophoneUsageDescription</key>
<string>Required to record audio.</string>

<!-- Photo Library (Photo, Video) -->
<key>NSPhotoLibraryUsageDescription</key>
<string>Required to select media from your library.</string>
```
---

## Module Quick Start

### Photo — `imagepickerkmp`

Pick images from the gallery or capture with the camera using a single Compose state holder.

```kotlin
@Composable
fun PhotoScreen() {
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(allowMultiple = true, selectionLimit = 10),
            cropConfig    = CropConfig(enabled = true)
        )
    )

    Button(onClick = { picker.launchCamera() }) { Text("Camera") }
    Button(onClick = { picker.launchGallery() }) { Text("Gallery") }

    when (val result = picker.result) {
        is ImagePickerResult.Success   -> result.photos.forEach { photo ->
            Image(painter = photo.loadPainter(), contentDescription = null)
        }
        is ImagePickerResult.Loading   -> CircularProgressIndicator()
        is ImagePickerResult.Error     -> Text("Error: ${result.exception.message}")
        is ImagePickerResult.Dismissed -> Unit
        is ImagePickerResult.Idle      -> Unit
    }
}
```

→ [Full Photo documentation](imagepickerkmp-photo/README.md)

---

### Video — `imagepickerkmp-video`

Record video or pick from the gallery. Supports compression, metadata, and multiple formats.

```kotlin
@Composable
fun VideoScreen() {
    val picker = rememberVideoPicker(
        config = VideoPickerConfig(
            audio = AudioConfig.Default,
            output = VideoOutputConfig(
                format = VideoOutputFormat.MP4,
                removeMetadata = false
            ),
            allowedMimeTypes = listOf(VideoMimeType.All)
        )
    )

    Button(onClick = { picker.launchCamera() })  { Text("Record") }
    Button(onClick = { picker.launchGallery() }) { Text("Pick Video") }

    when (val result = picker.result) {
        is VideoPickerState.Success -> Text("Duration: ${result.video.durationMs}ms")
        is VideoPickerState.Error   -> Text("Error: ${result.cause}")
        else -> Unit
    }
}
```

→ [Full Video documentation](imagepickerkmp-video/README.md)

---

### Audio — `imagepickerkmp-audio`

Two APIs — an inline chat-style mic widget and a modal state-holder.

**Inline widget** — embeds a hold-to-record mic button directly in your layout:

```kotlin
@Composable
fun ChatInputBar() {
    AudioRecorder(
        config   = AudioRecorderConfig(),
        onResult = { audioResult: AudioResult? ->
            if (audioResult != null) {
                println("Recorded: ${audioResult.uri}, ${audioResult.durationMs}ms")
            }
        }
    )
}
```

**Modal picker** — opens recorder or gallery in a dialog, same state-holder pattern as the other modules:

```kotlin
@Composable
fun AudioScreen() {
    val picker = rememberAudioPicker()

    Button(onClick = { picker.launchRecorder() }) { Text("Record") }
    Button(onClick = { picker.launchGallery() })  { Text("Pick Audio") }

    when (val state = picker.result) {
        is AudioPickerState.Success -> Text("Saved: ${state.audio.fileName}")
        else -> Unit
    }
}
```

→ [Full Audio documentation](imagepickerkmp-audio/README.md)

---

### Audio Player — `imagepickerkmp-audio-player`

Low-level playback engine. Powers the `ImagePickerAudioPlayer` composable in `imagepickerkmp-audio` and can be used directly to build fully custom player UIs.

```kotlin
@Composable
fun CustomPlayerScreen(audioUri: String) {
    val playerManager = rememberAudioPlayerManager()
    val state by playerManager.playbackState.collectAsState()

    LaunchedEffect(audioUri) { playerManager.prepare(audioUri) }

    LinearProgressIndicator(
        progress = {
            if (state.durationMs > 0) state.currentPositionMs.toFloat() / state.durationMs else 0f
        }
    )

    FloatingActionButton(
        onClick = {
            if (state.isPlaying) playerManager.pause()
            else playerManager.play(audioUri)
        }
    ) {
        Icon(
            imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = null
        )
    }
}
```

→ [Full Audio Player documentation](imagepickerkmp-audio-player/README.md)

---

### Scanner — `imagepickerkmp-scanner`

Scan barcodes and QR codes from a live camera feed. Supports 19 barcode formats.

```kotlin
@Composable
fun ScannerScreen() {
    val scanner = rememberScannerPicker(
        config = ScannerPickerConfig(
            camera = ScannerCameraConfig(
                behavior = ScannerBehaviorConfig(
                    allowedFormats = listOf(BarcodeFormat.QR_CODE, BarcodeFormat.EAN_13)
                )
            )
        )
    )

    Button(onClick = { scanner.launchScanner() }) { Text("Scan") }

    when (val result = scanner.result) {
        is ScannerPickerState.Success -> Text("Scanned: ${result.result.code}")
        is ScannerPickerState.Error   -> Text("Error: ${result.error}")
        else -> Unit
    }
}
```

→ [Full Scanner documentation](imagepickerkmp-scanner/README.md)

---

### Video Player — `imagepickerkmp-video-player`

Full-featured video player with play/pause, seek, volume, fullscreen, and quality selection.

```kotlin
@Composable
fun VideoPlayerScreen(videoUrl: String) {
    ImagePickerVideoPlayer(
        source = VideoSource.Url(videoUrl),
        config = VideoPlayerConfig(
            behavior = VideoBehaviorConfig(autoPlay = true)
        )
    )
}
```

For programmatic control (play/pause from code), use `rememberVideoPlayerState`:

```kotlin
val player = rememberVideoPlayerState(source = VideoSource.Url(videoUrl))
ImagePickerVideoPlayer(state = player, config = VideoPlayerConfig())
player.seekTo(30_000L)
```

→ [Full Video Player documentation](imagepickerkmp-video-player/README.md)

---

## Platform Support Matrix

| Feature | Android | iOS | Desktop | JS/Web | WASM |
|---------|:-------:|:---:|:-------:|:------:|:----:|
| Photo — Camera | ✅ | ✅ | ❌ | ❌ | ❌ |
| Photo — Gallery | ✅ | ✅ | ✅ | ✅ | ✅ |
| Photo — Crop | ✅ | ✅ | ❌ | ❌ | ❌ |
| Photo — EXIF | ✅ | ✅ | ❌ | ❌ | ❌ |
| Video — Camera | ✅ | ✅ | ❌ | ❌ | ❌ |
| Video — Gallery | ✅ | ✅ | ✅ | ✅ | ✅ |
| Audio — Record | ✅ | ✅ | ✅ | ❌ | ❌ |
| Audio — Player | ✅ | ✅ | ✅ | ✅ | ✅ |
| Scanner | ✅ | ✅ | ❌ | ❌ | ❌ |
| Video Player | ✅ | ✅ | ✅ | ✅ | ✅ |

---

## `PhotoResult` — Shared Result Model

All photo operations return a `PhotoResult` with consistent extension functions across platforms:

```kotlin
val photo: PhotoResult = result.photos.first()

// Compose UI
val painter = photo.loadPainter()       // Painter for Image()
val bitmap  = photo.loadImageBitmap()  // ImageBitmap for Canvas

// Raw data
val bytes  = photo.loadBytes()         // ByteArray
val base64 = photo.loadBase64()        // Base64-encoded string

// File system (kotlinx-io)
val path   = photo.asPath()            // kotlinx.io.files.Path
val exists = photo.exists()            // Boolean
val source = photo.asSource()          // Buffered Source

// Transfer
photo.transferToSink(mySink)           // Copy to RawSink
```

---

## Documentation

| Guide | Description |
|-------|-------------|
| [Changelog](docs/CHANGELOG.md) | Release history and migration notes |
| [Contributing](docs/CONTRIBUTING.md) | How to contribute |
| [Examples](docs/EXAMPLES.md) | Full code examples |
| [Integration Guide](docs/INTEGRATION_GUIDE.md) | Gradle setup, iOS config, ProGuard |
| [FAQ](docs/FAQ.md) | Common questions and troubleshooting |
| [Privacy Guide](docs/PRIVACY_GUIDE.md) | GDPR, GPS redaction, metadata handling |
| [Security](docs/SECURITY.md) | Reporting vulnerabilities |

---

## Contributing

Contributions are welcome. See [CONTRIBUTING.md](docs/CONTRIBUTING.md) for setup instructions, code style, and the pull request process.

- [Report a bug](https://github.com/ismoy/ImagePickerKMP/issues/new?template=bug_report.md)
- [Request a feature](https://github.com/ismoy/ImagePickerKMP/issues/new?template=feature_request.md)
- [Discord community](https://discord.gg/EjSQTeyh)

---

## Sponsors

ImagePickerKMP is free and open source. Maintaining it across five platforms with every Kotlin and Compose Multiplatform release takes significant effort. If this library saves you time in production, please consider sponsoring.

<!-- SPONSORS-LIST:START - Do not remove or modify this section -->
<table>
  <tr>
    <td align="center">
      <a href="https://github.com/james-codersHT" title="james-codersHT · Silver Sponsor">
        <img src="https://avatars.githubusercontent.com/james-codersHT?s=80" width="80px" alt="james-codersHT"/><br/>
        <sub><b>james-codersHT</b></sub>
      </a>
    </td>
  </tr>
</table>
<!-- SPONSORS-LIST:END -->

[→ Become a sponsor](https://github.com/sponsors/ismoy)

---

## Architecture

ImagePickerKMP follows a layered architecture. All platform-specific logic lives inside each module's `androidMain` / `iosMain` source sets. Your app code only ever touches the `commonMain` API.

```mermaid
flowchart TD
    %% Layer 1
    App["Your App (commonMain UI)<br/><br/>rememberImagePickerKMP()<br/>rememberVideoPicker()<br/>rememberAudioPicker()<br/>rememberScannerPicker()<br/>ImagePickerVideoPlayer()<br/>ImagePickerAudioPlayer()"]

    %% Layer 2
    M1(["imagepickerkmp (photo)"])
    M2(["imagepickerkmp-video"])
    M3(["imagepickerkmp-video-player"])
    M4(["imagepickerkmp-audio"])
    M5(["imagepickerkmp-audio-player"])
    M6(["imagepickerkmp-scanner"])

    %% Layer 3
    Core{{"imagepicker-core<br/><br/>Permissions, FileSystem, I18n, MediaLogger"}}

    %% Layer 4
    Android["Android<br/>Camera Intents, CameraX, MediaPlayer"]
    iOS["iOS<br/>AVFoundation, PHPickerVC"]
    Desktop["Desktop<br/>JVM / AWT"]
    Web["Web<br/>JS / WASM, WebRTC"]

    App --> M1 & M2 & M3 & M4 & M5 & M6
    M1 & M2 & M3 & M4 & M5 & M6 --> Core
    Core --> Android & iOS & Desktop & Web

    style App fill:transparent,stroke:#777,stroke-width:2px,rx:10,ry:10
    style M1 fill:#8e24aa,color:#fff,stroke:none
    style M2 fill:#8e24aa,color:#fff,stroke:none
    style M3 fill:#8e24aa,color:#fff,stroke:none
    style M4 fill:#8e24aa,color:#fff,stroke:none
    style M5 fill:#8e24aa,color:#fff,stroke:none
    style M6 fill:#8e24aa,color:#fff,stroke:none
    style Core fill:transparent,stroke:#777,stroke-width:2px
    style Android fill:#388e3c,color:#fff,stroke:none
    style iOS fill:#1565c0,color:#fff,stroke:none
    style Desktop fill:#388e3c,color:#fff,stroke:none
    style Web fill:#388e3c,color:#fff,stroke:none
```

### Module dependency graph

```mermaid
flowchart BT
    Photo["imagepickerkmp"]
    Video["imagepickerkmp-video"]
    Scanner["imagepickerkmp-scanner"]
    VideoPlayer["imagepickerkmp-video-player"]
    Audio["imagepickerkmp-audio"]
    AudioPlayer["imagepickerkmp-audio-player"]
    
    Core(("imagepicker-core<br/>(API)"))
    
    Photo -.-> Core
    Video -.-> Core
    Scanner -.-> Core
    VideoPlayer -.-> Core
    Audio -.-> Core
    AudioPlayer -.-> Core

    style Core fill:transparent,stroke:#777,stroke-width:2px
    style Photo fill:#0288d1,color:#fff,stroke:none
    style Video fill:#0288d1,color:#fff,stroke:none
    style Scanner fill:#0288d1,color:#fff,stroke:none
    style VideoPlayer fill:#0288d1,color:#fff,stroke:none
    style Audio fill:#0288d1,color:#fff,stroke:none
    style AudioPlayer fill:#0288d1,color:#fff,stroke:none
```

All modules expose `imagepicker-core` as an `api` dependency, so `PermissionManager`, `MediaLogger`, `I18nKonfig`, and `PlatformUri` are available in your app without an extra dependency declaration.

### Data flow — Architecture Lifecycle

```mermaid
sequenceDiagram
    autonumber
    actor User as User
    participant App as @Composable UI
    participant State as StateHolder (commonMain)
    participant Engine as Platform Engine (expect/actual)
    
    User->>App: Interaction (Tap "Camera", "Scan", "Play")
    activate App
    App->>State: launchCamera() / launchScanner() / play()
    activate State
    State->>State: Update State (e.g., Loading, Playing)
    State->>Engine: Request Native Action
    activate Engine
    
    rect rgb(240, 248, 255)
        note right of Engine: Platform Specific Implementations
        alt Photo & Video
            Engine->>Permissions: Granted
            Engine->>Engine: Camera Intents / UIImagePicker → Capture & Compress
        else Scanner
            Engine->>Engine: MLKit / Vision API → Detect & Decode QR/Barcode
        else Audio Record
            Engine->>Engine: MediaRecorder / AVAudioRecorder → Encode
        else Media Players
            Engine->>Engine: MediaPlayer / AVPlayer → Buffer & Play
        end
    end
    
    Engine-->>State: Native Callbacks (URIs, Bytes, Playback Info)
    deactivate Engine
    
    State->>State: Map to commonMain models (PhotoResult, AudioResult, etc.)
    State-->>App: Emit State (picker.result / playbackState)
    deactivate State
    
    App->>App: Recompose UI with media/data
    deactivate App
```

---

## Contributors

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%">
        <a href="https://github.com/ismoy">
          <img src="https://avatars.githubusercontent.com/u/72107070?v=4" width="100px;" alt="ismoy"/><br />
          <sub><b>ismoy</b></sub>
        </a><br />
        <a href="https://github.com/ismoy/ImagePickerKMP/commits?author=ismoy" title="Contributions">💻 📖 🚧 🎨 🤔</a>
      </td>
      <td align="center" valign="top" width="14.28%">
        <a href="https://github.com/medAndro">
          <img src="https://avatars.githubusercontent.com/u/88672474?v=4" width="100px;" alt="medAndro"/><br />
          <sub><b>medAndro</b></sub>
        </a><br />
        <a href="https://github.com/ismoy/ImagePickerKMP/commits?author=medAndro" title="Contributions">💻 🐛</a>
      </td>
      <td align="center" valign="top" width="14.28%">
        <a href="https://github.com/YaminMahdi">
          <img src="https://avatars.githubusercontent.com/u/48239104?v=4" width="100px;" alt="YaminMahdi"/><br />
          <sub><b>YaminMahdi</b></sub>
        </a><br />
        <a href="https://github.com/ismoy/ImagePickerKMP/commits?author=YaminMahdi" title="Contributions">💻</a>
      </td>
      <td align="center" valign="top" width="14.28%">
        <a href="https://github.com/jadlr">
          <img src="https://avatars.githubusercontent.com/u/696999?v=4" width="100px;" alt="jadlr"/><br />
          <sub><b>jadlr</b></sub>
        </a><br />
        <a href="https://github.com/ismoy/ImagePickerKMP/commits?author=jadlr" title="Contributions">💻</a>
      </td>
      <td align="center" valign="top" width="14.28%">
        <a href="https://github.com/daniil-pastuhov">
          <img src="https://avatars.githubusercontent.com/u/8494442?v=4" width="100px;" alt="daniil-pastuhov"/><br />
          <sub><b>daniil-pastuhov</b></sub>
        </a><br />
        <a href="https://github.com/ismoy/ImagePickerKMP/commits?author=daniil-pastuhov" title="Contributions">💻</a>
      </td>
      <td align="center" valign="top" width="14.28%">
        <a href="https://github.com/azevio">
          <img src="https://avatars.githubusercontent.com/u/167407234?v=4" width="100px;" alt="azevio"/><br />
          <sub><b>azevio</b></sub>
        </a><br />
        <a href="https://github.com/ismoy/ImagePickerKMP/commits?author=azevio" title="Contributions">💻</a>
      </td>
      <td align="center" valign="top" width="14.28%">
        <a href="https://github.com/fanqieVip">
          <img src="https://avatars.githubusercontent.com/u/42194904?v=4" width="100px;" alt="fanqieVip"/><br />
          <sub><b>fanqieVip</b></sub>
        </a><br />
        <a href="https://github.com/ismoy/ImagePickerKMP/commits?author=fanqieVip" title="Contributions">💻</a>
      </td>
    </tr>
  </tbody>
</table>
<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->
<!-- ALL-CONTRIBUTORS-LIST:END -->

---

**MIT License** · Made with ❤️ for the Kotlin Multiplatform community · [⭐ Star this repo](https://github.com/ismoy/ImagePickerKMP)
