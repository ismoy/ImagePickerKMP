package io.github.ismoy.imagepickerkmp.scanner.capture

import io.github.ismoy.imagepickerkmp.scanner.domain.model.ScannerResult

import io.github.ismoy.imagepickerkmp.scanner.domain.BarcodeFormat
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.nulls.shouldBeNull

class ScannerPickerStateTest : DescribeSpec({

    val result = ScannerResult("https://example.com", BarcodeFormat.QR_CODE, 1000L)

    describe("ScannerPickerState.Idle") {
        it("is correct type") {
            val state: ScannerPickerState = ScannerPickerState.Idle
            state.shouldBeInstanceOf<ScannerPickerState.Idle>()
        }

        it("equals itself") {
            ScannerPickerState.Idle shouldBe ScannerPickerState.Idle
        }
    }

    describe("ScannerPickerState.Loading") {
        it("is correct type") {
            val state: ScannerPickerState = ScannerPickerState.Loading
            state.shouldBeInstanceOf<ScannerPickerState.Loading>()
        }

        it("equals itself") {
            ScannerPickerState.Loading shouldBe ScannerPickerState.Loading
        }
    }

    describe("ScannerPickerState.Cancelled") {
        it("is correct type") {
            val state: ScannerPickerState = ScannerPickerState.Cancelled
            state.shouldBeInstanceOf<ScannerPickerState.Cancelled>()
        }

        it("equals itself") {
            ScannerPickerState.Cancelled shouldBe ScannerPickerState.Cancelled
        }
    }

    describe("ScannerPickerState.Success") {
        it("stores the ScannerResult") {
            val state = ScannerPickerState.Success(result)
            state.result shouldBe result
        }

        it("equality based on result") {
            val a = ScannerPickerState.Success(result)
            val b = ScannerPickerState.Success(result)
            a shouldBe b
        }

        it("inequality for different result") {
            val a = ScannerPickerState.Success(ScannerResult("a", null, 1L))
            val b = ScannerPickerState.Success(ScannerResult("b", null, 1L))
            a shouldNotBe b
        }
    }

    describe("ScannerPickerState.BatchSuccess") {
        it("stores list of results") {
            val results = listOf(
                ScannerResult("a", BarcodeFormat.QR_CODE, 1L),
                ScannerResult("b", BarcodeFormat.EAN_13, 2L)
            )
            val state = ScannerPickerState.BatchSuccess(results)
            state.results.size shouldBe 2
        }

        it("empty list is valid") {
            val state = ScannerPickerState.BatchSuccess(emptyList())
            state.results.isEmpty() shouldBe true
        }

        it("equality based on results list") {
            val results = listOf(ScannerResult("a", null, 1L))
            val a = ScannerPickerState.BatchSuccess(results)
            val b = ScannerPickerState.BatchSuccess(results)
            a shouldBe b
        }
    }

    describe("ScannerPickerState.Error") {
        it("stores the ScannerPickerError") {
            val error = ScannerPickerError.CameraError("Camera unavailable")
            val state = ScannerPickerState.Error(error)
            state.error shouldBe error
        }

        it("equality based on error") {
            val error = ScannerPickerError.CameraError("Camera failed")
            val a = ScannerPickerState.Error(error)
            val b = ScannerPickerState.Error(error)
            a shouldBe b
        }
    }

    describe("ScannerPickerState sealed when expression") {
        it("covers all variants") {
            val states: List<ScannerPickerState> = listOf(
                ScannerPickerState.Idle,
                ScannerPickerState.Loading,
                ScannerPickerState.Cancelled,
                ScannerPickerState.Success(result),
                ScannerPickerState.BatchSuccess(listOf(result)),
                ScannerPickerState.Error(ScannerPickerError.CameraError("err"))
            )

            val labels = states.map { state ->
                when (state) {
                    is ScannerPickerState.Idle -> "idle"
                    is ScannerPickerState.Loading -> "loading"
                    is ScannerPickerState.Cancelled -> "cancelled"
                    is ScannerPickerState.Success -> "success"
                    is ScannerPickerState.BatchSuccess -> "batch"
                    is ScannerPickerState.Error -> "error"
                }
            }

            labels shouldBe listOf("idle", "loading", "cancelled", "success", "batch", "error")
        }
    }

    describe("ScannerPickerError") {
        it("CameraError stores message") {
            val err = ScannerPickerError.CameraError("Sensor unavailable")
            err.message shouldBe "Sensor unavailable"
        }

        it("Unknown stores throwable") {
            val cause = RuntimeException("root cause")
            val err = ScannerPickerError.Unknown(cause)
            err.error shouldBe cause
        }

        it("PermissionDenied has a message") {
            val err = ScannerPickerError.PermissionDenied("Custom message")
            err.message shouldBe "Custom message"
        }

        it("ScannerPickerError is a sealed class of Exception") {
            val err = ScannerPickerError.CameraError("e")
            err.shouldBeInstanceOf<Exception>()
        }
    }
})
