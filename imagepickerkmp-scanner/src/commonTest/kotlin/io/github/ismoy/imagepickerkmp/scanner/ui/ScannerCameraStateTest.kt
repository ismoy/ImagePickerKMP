package io.github.ismoy.imagepickerkmp.scanner.ui

import io.github.ismoy.imagepickerkmp.scanner.camera.DefaultScannerCameraStateManager
import io.github.ismoy.imagepickerkmp.scanner.camera.ScannerCameraState

import io.github.ismoy.imagepickerkmp.scanner.domain.model.FlashMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

class ScannerCameraStateTest : DescribeSpec({

    describe("ScannerCameraState sealed variants") {
        it("CameraReady is a data object singleton") {
            (ScannerCameraState.CameraReady === ScannerCameraState.CameraReady) shouldBe true
        }

        it("StartingCamera is a data object singleton") {
            (ScannerCameraState.StartingCamera === ScannerCameraState.StartingCamera) shouldBe true
        }

        it("Scanning is a data object singleton") {
            (ScannerCameraState.Scanning === ScannerCameraState.Scanning) shouldBe true
        }

        it("Paused is a data object singleton") {
            (ScannerCameraState.Paused === ScannerCameraState.Paused) shouldBe true
        }

        it("CodeDetected stores code and format") {
            val state = ScannerCameraState.CodeDetected("QR_DATA", "QR_CODE")
            state.code shouldBe "QR_DATA"
            state.format shouldBe "QR_CODE"
        }

        it("CodeDetected format defaults to null") {
            val state = ScannerCameraState.CodeDetected("QR_DATA")
            state.format.shouldBeNull()
        }

        it("CodeDetected equality based on code and format") {
            val a = ScannerCameraState.CodeDetected("x", "QR")
            val b = ScannerCameraState.CodeDetected("x", "QR")
            a shouldBe b
        }

        it("Error stores message") {
            val state = ScannerCameraState.Error("Camera sensor failed")
            state.message shouldBe "Camera sensor failed"
        }

        it("exhaustive when covers all variants") {
            val variants: List<ScannerCameraState> = listOf(
                ScannerCameraState.CameraReady,
                ScannerCameraState.StartingCamera,
                ScannerCameraState.Scanning,
                ScannerCameraState.Paused,
                ScannerCameraState.CodeDetected("x"),
                ScannerCameraState.Error("e")
            )
            val labels = variants.map { state ->
                when (state) {
                    is ScannerCameraState.CameraReady -> "ready"
                    is ScannerCameraState.StartingCamera -> "starting"
                    is ScannerCameraState.Scanning -> "scanning"
                    is ScannerCameraState.Paused -> "paused"
                    is ScannerCameraState.CodeDetected -> "detected"
                    is ScannerCameraState.Error -> "error"
                }
            }
            labels shouldBe listOf("ready", "starting", "scanning", "paused", "detected", "error")
        }
    }

    describe("DefaultScannerCameraStateManager") {
        it("initial state is CameraReady") {
            val manager = DefaultScannerCameraStateManager()
            manager.currentState.value shouldBe ScannerCameraState.CameraReady
        }

        it("updateState changes currentState") {
            val manager = DefaultScannerCameraStateManager()
            manager.updateState(ScannerCameraState.Scanning)
            manager.currentState.value shouldBe ScannerCameraState.Scanning
        }

        it("updateState to Paused reflects correctly") {
            val manager = DefaultScannerCameraStateManager()
            manager.updateState(ScannerCameraState.Paused)
            manager.currentState.value shouldBe ScannerCameraState.Paused
        }

        it("updateState to Error reflects correctly") {
            val manager = DefaultScannerCameraStateManager()
            manager.updateState(ScannerCameraState.Error("sensor issue"))
            val state = manager.currentState.value as ScannerCameraState.Error
            state.message shouldBe "sensor issue"
        }

        it("initial flash mode is OFF") {
            val manager = DefaultScannerCameraStateManager()
            manager.flashMode.value shouldBe FlashMode.OFF
        }

        it("setFlashMode ON updates to ON") {
            val manager = DefaultScannerCameraStateManager()
            manager.setFlashMode("ON")
            manager.flashMode.value shouldBe FlashMode.ON
        }

        it("setFlashMode OFF updates to OFF") {
            val manager = DefaultScannerCameraStateManager()
            manager.setFlashMode("ON")
            manager.setFlashMode("OFF")
            manager.flashMode.value shouldBe FlashMode.OFF
        }

        it("setFlashMode with unknown string maps to OFF") {
            val manager = DefaultScannerCameraStateManager()
            manager.setFlashMode("UNKNOWN")
            manager.flashMode.value shouldBe FlashMode.OFF
        }

        it("multiple state transitions work correctly") {
            val manager = DefaultScannerCameraStateManager()
            manager.updateState(ScannerCameraState.StartingCamera)
            manager.updateState(ScannerCameraState.Scanning)
            manager.updateState(ScannerCameraState.Paused)
            manager.updateState(ScannerCameraState.CameraReady)
            manager.currentState.value shouldBe ScannerCameraState.CameraReady
        }
    }
})
