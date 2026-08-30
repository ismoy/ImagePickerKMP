# imagepickerkmp-scanner — Scanner Module

Live barcode and QR code scanning for Kotlin Multiplatform.

[![Maven Central](https://img.shields.io/maven-central/v/io.github.ismoy/imagepickerkmp-scanner.svg?label=Maven%20Central)](https://search.maven.org/artifact/io.github.ismoy/imagepickerkmp-scanner)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

Part of the [ImagePickerKMP ecosystem](../README.md).

---

## Installation

```kotlin
// commonMain
implementation("io.github.ismoy:imagepickerkmp-scanner:1.0.0-alpha-cans-fix.3")
```

### iOS — `Info.plist`

```xml
<key>NSCameraUsageDescription</key>
<string>Required to scan barcodes and QR codes.</string>
```

---

## Basic Usage

```kotlin
@Composable
fun ScannerScreen() {
    val scanner = rememberScannerPicker()

    Button(onClick = { scanner.launchScanner() }) { Text("Scan Code") }

    when (val result = scanner.result) {
        is ScannerPickerState.Idle      -> Text("Ready to scan.")
        is ScannerPickerState.Loading   -> CircularProgressIndicator()
        is ScannerPickerState.Success   -> {
            Text("Code: ${result.result.code}")
            Text("Format: ${result.result.format}")
        }
        is ScannerPickerState.Cancelled -> Text("Cancelled.")
        is ScannerPickerState.Error     -> Text("Error: ${result.error}")
        is ScannerPickerState.BatchSuccess -> {
            result.results.forEach { r -> Text("${r.code} (${r.format})") }
        }
    }
}
```

---

## Configuration

```kotlin
val scanner = rememberScannerPicker(
    config = ScannerPickerConfig(
        camera = ScannerCameraConfig(
            behavior = ScannerBehaviorConfig(
                allowedFormats        = listOf(BarcodeFormat.QR_CODE, BarcodeFormat.EAN_13),
                delayToNextScan       = 2000L,       // ms between successive scans
                playSound             = true,
                hapticFeedback        = HapticFeedbackMode.SOUND_AND_VIBRATE,
                allowDuplicates       = false,
                enableInactivity      = true,
                inactivityDelay       = 30_000L      // pause after 30s with no activity
            ),
            advanced = ScannerAdvancedFeaturesConfig(
                batchMode             = false,        // true = scan multiple codes
                enableAutoZoom        = false,
                enableAROverlays      = false
            ),
            ui = ScannerUIConfig(
                watermark             = "MyApp",
                overlayStyle          = ScannerOverlayStyle.CLASSIC
            )
        )
    )
)
```

---

## Supported Barcode Formats

| Format | Constant |
|--------|----------|
| QR Code | `BarcodeFormat.QR_CODE` |
| Aztec | `BarcodeFormat.AZTEC` |
| Code 128 | `BarcodeFormat.CODE_128` |
| Code 39 | `BarcodeFormat.CODE_39` |
| Code 93 | `BarcodeFormat.CODE_93` |
| Codabar | `BarcodeFormat.CODABAR` |
| Data Matrix | `BarcodeFormat.DATA_MATRIX` |
| EAN-13 | `BarcodeFormat.EAN_13` |
| EAN-8 | `BarcodeFormat.EAN_8` |
| GS1 DataBar | `BarcodeFormat.GS1_DATA_BAR` |
| ITF | `BarcodeFormat.ITF` |
| ITF-14 | `BarcodeFormat.ITF_14` |
| PDF 417 | `BarcodeFormat.PDF_417` |
| Micro PDF 417 | `BarcodeFormat.MICRO_PDF_417` |
| Micro QR Code | `BarcodeFormat.MICRO_QR_CODE` |
| UPC-A | `BarcodeFormat.UPC_A` |
| UPC-E | `BarcodeFormat.UPC_E` |
| All formats | `BarcodeFormat.ALL` |

---

## `ScannerResult`

```kotlin
data class ScannerResult(
    val code     : String,
    val format   : BarcodeFormat?,
    val timestamp: Long
)
```

---

## Batch Mode

Enable `batchMode = true` to scan multiple codes before confirming. Scanned codes accumulate in `scanner.scannedCodes` and are returned as `ScannerPickerState.BatchSuccess`.

```kotlin
val scanner = rememberScannerPicker(
    config = ScannerPickerConfig(
        camera = ScannerCameraConfig(
            advanced = ScannerAdvancedFeaturesConfig(batchMode = true)
        )
    )
)

// Access live list while scanning:
val scanned: List<ScannerResult> = scanner.scannedCodes

// Final result when user taps Done:
is ScannerPickerState.BatchSuccess -> result.results.forEach { /* ... */ }
```

---

## Scan from an Image (Static Scanning)

Scan barcodes from an existing image `ByteArray` without opening the camera:

```kotlin
val staticScanner: StaticCodeScanner = createStaticCodeScanner()
val code: String? = staticScanner.scanImage(imageBytes)
```

You can combine this with the photo picker to scan a QR code from the gallery:

```kotlin
val imagePicker = rememberImagePickerKMP()
val scope = rememberCoroutineScope()

LaunchedEffect(imagePicker.result) {
    val result = imagePicker.result
    if (result is ImagePickerResult.Success) {
        val bytes = result.photos.first().loadBytes()
        scope.launch {
            val code = createStaticCodeScanner().scanImage(bytes)
            if (code != null) {
                playScannerSystemBeep()  // play the system beep sound
                println("Found: $code")
            }
        }
    }
}

Button(onClick = { imagePicker.launchGallery(selectionLimit = 1) }) {
    Text("Scan from Gallery")
}
```

Available on Android and iOS.

---

## Custom Layout

Use `ScannerUIExtensions.customLayout` for complete control using the stable public `ScannerCameraUIState` contract. Do not cast it to `ScannerCameraStateHolder`: implementation details remain internal so the scanner can evolve without breaking consumers.

```kotlin
ScannerPickerConfig(
    uiExtensions = ScannerUIExtensions(
        customLayout = { uiState ->
            Box(modifier = Modifier.fillMaxSize()) {
                // Custom close button
                IconButton(
                    onClick = { uiState.stopScanning() },
                    modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }

                // Custom flash toggle
                IconButton(
                    onClick = { uiState.toggleFlash() },
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                ) {
                    val icon = if (uiState.flashMode == FlashMode.ON)
                        Icons.Default.FlashOn else Icons.Default.FlashOff
                    Icon(icon, contentDescription = "Flash", tint = Color.White)
                }

            }
        }
    )
)
```

### `ScannerCameraUIState` contract

| Field | Type | Description |
|---|---|---|
| `isScanning` | `Boolean` | Whether the camera session is actively scanning. |
| `flashMode` | `FlashMode` | Current flash state. |
| `lastScannedCode` | `String?` | Most recently accepted code in the current session. |

| Method | Description |
|---|---|
| `toggleFlash()` | Toggle flash when supported by the camera. |
| `pauseScanning()` / `resumeScanning()` | Temporarily pause or resume analysis. |
| `stopScanning()` | End the active scanner session. |

---

## Permission Configuration

```kotlin
ScannerPickerConfig(
    permissions = ScannerPermissionConfig(
        titleDialogConfig      = "Camera Access",
        descriptionDialogConfig = "Allow camera access to scan codes.",
        cancelButtonText       = "Not now"
    )
)
```

---

## Platform support

> **Supported runtime targets:** Android and iOS. JVM, JS and Wasm JS source sets are present for multiplatform metadata, but scanning is intentionally unsupported there and must not be used in production.

| Feature | Android | iOS | Desktop | Web |
|---------|:-------:|:---:|:-------:|:---:|
| Live camera scanning | ✅ | ✅ | ❌ | ❌ |
| Static image scanning | ✅ | ✅ | ❌ | ❌ |
| Batch mode | ✅ | ✅ | ❌ | ❌ |
| Flash control | ✅ | ✅ | ❌ | ❌ |
| Pinch-to-zoom | ✅ | ✅ | ❌ | ❌ |
| Auto-zoom | ✅ | ✅ | ❌ | ❌ |
