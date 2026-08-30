package io.github.ismoy.imagepickerkmp.scanner.picker

import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerPickerError
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerPickerState
import io.github.ismoy.imagepickerkmp.scanner.domain.model.ScannerResult
import io.github.ismoy.imagepickerkmp.scanner.domain.BarcodeFormat
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.collections.shouldHaveSize

class ScannerPickerStateHolderTest : DescribeSpec({

    fun makeHolder(config: ScannerPickerConfig = ScannerPickerConfig()) =
        ScannerPickerStateHolder(config)

    // ── Initial state ─────────────────────────────────────────────────────────

    describe("initial state") {
        it("result is Idle") {
            makeHolder().result.shouldBeInstanceOf<ScannerPickerState.Idle>()
        }

        it("activeMode is None") {
            makeHolder().activeMode.shouldBeInstanceOf<ScannerPickerMode.None>()
        }

        it("scannedCodes is empty") {
            makeHolder().scannedCodes shouldHaveSize 0
        }
    }

    // ── launchScanner ─────────────────────────────────────────────────────────

    describe("launchScanner") {
        it("sets result to Loading") {
            val holder = makeHolder()
            holder.launchScanner()
            holder.result.shouldBeInstanceOf<ScannerPickerState.Loading>()
        }

        it("sets activeMode to Camera") {
            val holder = makeHolder()
            holder.launchScanner()
            holder.activeMode.shouldBeInstanceOf<ScannerPickerMode.Camera>()
        }

        it("stores onDismiss callback in mode") {
            var called = false
            val holder = makeHolder()
            holder.launchScanner(onDismiss = { called = true })
            val mode = holder.activeMode as ScannerPickerMode.Camera
            mode.onDismiss.shouldNotBeNull()
            mode.onDismiss.invoke()
            called shouldBe true
        }

        it("stores onError callback in mode") {
            var caught: Exception? = null
            val holder = makeHolder()
            holder.launchScanner(onError = { caught = it })
            val mode = holder.activeMode as ScannerPickerMode.Camera
            mode.onError.shouldNotBeNull()
            mode.onError.invoke(RuntimeException("cam error"))
            caught?.message shouldBe "cam error"
        }

        it("onDismiss is null when not provided") {
            val holder = makeHolder()
            holder.launchScanner()
            val mode = holder.activeMode as ScannerPickerMode.Camera
            mode.onDismiss.shouldBeNull()
        }

        it("onError is null when not provided") {
            val holder = makeHolder()
            holder.launchScanner()
            val mode = holder.activeMode as ScannerPickerMode.Camera
            mode.onError.shouldBeNull()
        }

        it("clears scannedCodes on new launch") {
            val holder = makeHolder()
            val r = ScannerResult("abc", BarcodeFormat.QR_CODE, 1L)
            holder.addScannedCode(r)
            holder.scannedCodes shouldHaveSize 1
            holder.launchScanner()
            holder.scannedCodes shouldHaveSize 0
        }

        it("is blocked while Loading (second call ignored)") {
            val holder = makeHolder()
            holder.launchScanner()
            val firstMode = holder.activeMode
            holder.launchScanner()    // second call while Loading → no-op
            holder.activeMode shouldBe firstMode
        }
    }

    // ── reset ─────────────────────────────────────────────────────────────────

    describe("reset") {
        it("sets result back to Idle") {
            val holder = makeHolder()
            holder.launchScanner()
            holder.reset()
            holder.result.shouldBeInstanceOf<ScannerPickerState.Idle>()
        }

        it("sets activeMode back to None") {
            val holder = makeHolder()
            holder.launchScanner()
            holder.reset()
            holder.activeMode.shouldBeInstanceOf<ScannerPickerMode.None>()
        }

        it("clears scannedCodes") {
            val holder = makeHolder()
            holder.addScannedCode(ScannerResult("x", null, 1L))
            holder.reset()
            holder.scannedCodes shouldHaveSize 0
        }

        it("allows relaunch after reset") {
            val holder = makeHolder()
            holder.launchScanner()
            holder.reset()
            holder.launchScanner()
            holder.result.shouldBeInstanceOf<ScannerPickerState.Loading>()
        }
    }

    // ── notifySuccess ─────────────────────────────────────────────────────────

    describe("notifySuccess") {
        it("sets result to Success with the scanned result") {
            val holder = makeHolder()
            holder.launchScanner()
            val r = ScannerResult("https://example.com", BarcodeFormat.QR_CODE, 1000L)
            holder.notifySuccess(r)
            val state = holder.result as ScannerPickerState.Success
            state.result shouldBe r
        }

        it("clears activeMode after success") {
            val holder = makeHolder()
            holder.launchScanner()
            holder.notifySuccess(ScannerResult("x", null, 1L))
            holder.activeMode.shouldBeInstanceOf<ScannerPickerMode.None>()
        }

        it("allows relaunch after success") {
            val holder = makeHolder()
            holder.launchScanner()
            holder.notifySuccess(ScannerResult("x", null, 1L))
            holder.launchScanner()
            holder.result.shouldBeInstanceOf<ScannerPickerState.Loading>()
        }
    }

    // ── notifyBatchSuccess ────────────────────────────────────────────────────

    describe("notifyBatchSuccess") {
        it("sets result to BatchSuccess with the list") {
            val holder = makeHolder()
            holder.launchScanner()
            val results = listOf(
                ScannerResult("a", BarcodeFormat.QR_CODE, 1L),
                ScannerResult("b", BarcodeFormat.EAN_13, 2L)
            )
            holder.notifyBatchSuccess(results)
            val state = holder.result as ScannerPickerState.BatchSuccess
            state.results shouldHaveSize 2
        }

        it("empty batch is valid") {
            val holder = makeHolder()
            holder.notifyBatchSuccess(emptyList())
            val state = holder.result as ScannerPickerState.BatchSuccess
            state.results.isEmpty() shouldBe true
        }
    }

    // ── notifyDismiss ─────────────────────────────────────────────────────────

    describe("notifyDismiss") {
        it("sets result to Cancelled") {
            val holder = makeHolder()
            holder.launchScanner()
            holder.notifyDismiss()
            holder.result.shouldBeInstanceOf<ScannerPickerState.Cancelled>()
        }

        it("clears activeMode") {
            val holder = makeHolder()
            holder.launchScanner()
            holder.notifyDismiss()
            holder.activeMode.shouldBeInstanceOf<ScannerPickerMode.None>()
        }

        it("clears scannedCodes") {
            val holder = makeHolder()
            holder.addScannedCode(ScannerResult("x", null, 1L))
            holder.notifyDismiss()
            holder.scannedCodes shouldHaveSize 0
        }

        it("allows relaunch after dismiss") {
            val holder = makeHolder()
            holder.launchScanner()
            holder.notifyDismiss()
            holder.launchScanner()
            holder.result.shouldBeInstanceOf<ScannerPickerState.Loading>()
        }
    }

    // ── notifyError ───────────────────────────────────────────────────────────

    describe("notifyError") {
        it("sets result to Error with the error") {
            val holder = makeHolder()
            holder.launchScanner()
            val error = ScannerPickerError.CameraError("Sensor failed")
            holder.notifyError(error)
            val state = holder.result as ScannerPickerState.Error
            state.error shouldBe error
        }

        it("clears activeMode") {
            val holder = makeHolder()
            holder.launchScanner()
            holder.notifyError(ScannerPickerError.CameraError("err"))
            holder.activeMode.shouldBeInstanceOf<ScannerPickerMode.None>()
        }

        it("allows relaunch after error") {
            val holder = makeHolder()
            holder.launchScanner()
            holder.notifyError(ScannerPickerError.CameraError("err"))
            holder.launchScanner()
            holder.result.shouldBeInstanceOf<ScannerPickerState.Loading>()
        }

        it("PermissionDenied error is stored correctly") {
            val holder = makeHolder()
            val error = ScannerPickerError.PermissionDenied("denied")
            holder.notifyError(error)
            val state = holder.result as ScannerPickerState.Error
            (state.error as ScannerPickerError.PermissionDenied).message shouldBe "denied"
        }
    }

    // ── addScannedCode ────────────────────────────────────────────────────────

    describe("addScannedCode") {
        it("adds a code to scannedCodes") {
            val holder = makeHolder()
            holder.addScannedCode(ScannerResult("abc", BarcodeFormat.QR_CODE, 1L))
            holder.scannedCodes shouldHaveSize 1
        }

        it("adding multiple codes accumulates them") {
            val holder = makeHolder()
            holder.addScannedCode(ScannerResult("a", null, 1L))
            holder.addScannedCode(ScannerResult("b", null, 2L))
            holder.addScannedCode(ScannerResult("c", null, 3L))
            holder.scannedCodes shouldHaveSize 3
        }

        it("duplicate code is skipped when allowDuplicates = false (default)") {
            val holder = makeHolder() // default: allowDuplicates = false
            val r1 = ScannerResult("same", BarcodeFormat.QR_CODE, 1L)
            val r2 = ScannerResult("same", BarcodeFormat.QR_CODE, 2L)  // same code
            holder.addScannedCode(r1)
            holder.addScannedCode(r2)
            holder.scannedCodes shouldHaveSize 1
        }

        it("duplicate code is added when allowDuplicates = true") {
            val config = ScannerPickerConfig(
                camera = io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig(
                    behavior = io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerBehaviorConfig(
                        allowDuplicates = true
                    )
                )
            )
            val holder = makeHolder(config)
            holder.addScannedCode(ScannerResult("same", null, 1L))
            holder.addScannedCode(ScannerResult("same", null, 2L))
            holder.scannedCodes shouldHaveSize 2
        }

        it("different codes are always added") {
            val holder = makeHolder()
            holder.addScannedCode(ScannerResult("a", null, 1L))
            holder.addScannedCode(ScannerResult("b", null, 2L))
            holder.scannedCodes shouldHaveSize 2
        }
    }
})
