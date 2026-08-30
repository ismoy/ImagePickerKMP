package io.github.ismoy.imagepickerkmp.scanner.camera

import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEvent
import io.github.ismoy.imagepickerkmp.scanner.domain.BarcodeFormat
import io.github.ismoy.imagepickerkmp.scanner.domain.model.BarcodeData
import io.github.ismoy.imagepickerkmp.scanner.domain.model.CameraPositionDistance
import io.github.ismoy.imagepickerkmp.scanner.domain.model.FlashMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ScannerEventProcessorTest : DescribeSpec({
    describe("ScannerEventProcessor") {
        it("routes every supported scanner event to its corresponding state or callback") {
            val scannedCodes = mutableListOf<Pair<String, String?>>()
            val cameraErrors = mutableListOf<String>()
            val permissionResults = mutableListOf<Boolean>()
            var permanentlyDeniedCount = 0
            val distances = mutableListOf<CameraPositionDistance>()
            val flashModes = mutableListOf<FlashMode>()
            val cameraStates = mutableListOf<ScannerCameraState>()
            val zoomStates = mutableListOf<Triple<Float, Float, Float>>()
            val detectedBarcodeBatches = mutableListOf<List<BarcodeData>>()

            val processor = ScannerEventProcessor(
                onCodeScanned = { code, format -> scannedCodes += code to format },
                onCameraError = cameraErrors::add,
                onPermissionResult = permissionResults::add,
                onPermissionPermanentlyDenied = { permanentlyDeniedCount++ },
                onDistanceChanged = distances::add,
                onFlashStateChanged = flashModes::add,
                onCameraStateChanged = cameraStates::add,
                onZoomStateChanged = { min, max, current -> zoomStates += Triple(min, max, current) },
                onBarcodesDetected = detectedBarcodeBatches::add
            )
            val barcode = BarcodeData("payload", BarcodeFormat.QR_CODE, null, null)

            processor.onEvent(ScannerEvent.CodeScanned("payload", "QR_CODE"))
            processor.onEvent(ScannerEvent.CameraError("camera unavailable"))
            processor.onEvent(ScannerEvent.PermissionResult(true))
            processor.onEvent(ScannerEvent.PermissionPermanentlyDenied("denied"))
            processor.onEvent(ScannerEvent.DistanceChanged(CameraPositionDistance.OPTIMAL))
            processor.onEvent(ScannerEvent.FlashStateChanged(isEnabled = true, mode = "ON"))
            processor.onEvent(ScannerEvent.FlashStateChanged(isEnabled = false, mode = "TORCH"))
            processor.onEvent(ScannerEvent.TorchAvailabilityChanged(isAvailable = true))
            processor.onEvent(ScannerEvent.ScanningStarted)
            processor.onEvent(ScannerEvent.ScanningStopped)
            processor.onEvent(ScannerEvent.ScanningPaused)
            processor.onEvent(ScannerEvent.ScanningResumed)
            processor.onEvent(ScannerEvent.ZoomStateChanged(1f, 8f, 3f))
            processor.onEvent(ScannerEvent.BarcodesDetected(listOf(barcode)))

            scannedCodes shouldBe listOf("payload" to "QR_CODE")
            cameraErrors shouldBe listOf("camera unavailable")
            permissionResults shouldBe listOf(true)
            permanentlyDeniedCount shouldBe 1
            distances shouldBe listOf(CameraPositionDistance.OPTIMAL)
            flashModes shouldBe listOf(FlashMode.ON, FlashMode.OFF)
            cameraStates shouldBe listOf(
                ScannerCameraState.Scanning,
                ScannerCameraState.CameraReady,
                ScannerCameraState.Paused,
                ScannerCameraState.Scanning
            )
            zoomStates shouldBe listOf(Triple(1f, 8f, 3f))
            detectedBarcodeBatches shouldBe listOf(listOf(barcode))
        }
    }
})
