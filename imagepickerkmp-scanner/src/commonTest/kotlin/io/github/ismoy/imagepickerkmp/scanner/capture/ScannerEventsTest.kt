package io.github.ismoy.imagepickerkmp.scanner.capture

import io.github.ismoy.imagepickerkmp.scanner.domain.model.BarcodeData
import io.github.ismoy.imagepickerkmp.scanner.domain.model.CameraPositionDistance
import io.github.ismoy.imagepickerkmp.scanner.domain.BarcodeFormat
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.collections.shouldHaveSize

class ScannerEventsTest : DescribeSpec({

    describe("ScannerEvent.CodeScanned") {
        it("stores code and format") {
            val event = ScannerEvent.CodeScanned("QR_DATA", "QR_CODE")
            event.code shouldBe "QR_DATA"
            event.format shouldBe "QR_CODE"
        }

        it("format defaults to null") {
            val event = ScannerEvent.CodeScanned("QR_DATA")
            event.format.shouldBeNull()
        }

        it("equality based on fields") {
            val a = ScannerEvent.CodeScanned("x", "QR")
            val b = ScannerEvent.CodeScanned("x", "QR")
            a shouldBe b
        }
    }

    describe("ScannerEvent.DistanceChanged") {
        it("stores distance") {
            val event = ScannerEvent.DistanceChanged(CameraPositionDistance.TOO_FAR)
            event.distance shouldBe CameraPositionDistance.TOO_FAR
        }

        it("equality for same distance") {
            val a = ScannerEvent.DistanceChanged(CameraPositionDistance.OPTIMAL)
            val b = ScannerEvent.DistanceChanged(CameraPositionDistance.OPTIMAL)
            a shouldBe b
        }
    }

    describe("ScannerEvent.CameraError") {
        it("stores error string") {
            val event = ScannerEvent.CameraError("Sensor unavailable")
            event.error shouldBe "Sensor unavailable"
        }
    }

    describe("ScannerEvent.PermissionResult") {
        it("granted = true") {
            ScannerEvent.PermissionResult(true).granted shouldBe true
        }

        it("granted = false") {
            ScannerEvent.PermissionResult(false).granted shouldBe false
        }
    }

    describe("ScannerEvent.PermissionPermanentlyDenied") {
        it("stores reason") {
            val event = ScannerEvent.PermissionPermanentlyDenied("User said never")
            event.reason shouldBe "User said never"
        }
    }

    describe("ScannerEvent.FlashStateChanged") {
        it("stores isEnabled and mode") {
            val event = ScannerEvent.FlashStateChanged(true, "TORCH")
            event.isEnabled shouldBe true
            event.mode shouldBe "TORCH"
        }
    }

    describe("ScannerEvent.TorchAvailabilityChanged") {
        it("stores isAvailable") {
            ScannerEvent.TorchAvailabilityChanged(true).isAvailable shouldBe true
            ScannerEvent.TorchAvailabilityChanged(false).isAvailable shouldBe false
        }
    }

    describe("ScannerEvent.ScanningStarted / Stopped / Paused / Resumed") {
        it("ScanningStarted is a singleton object") {
            (ScannerEvent.ScanningStarted === ScannerEvent.ScanningStarted) shouldBe true
        }

        it("ScanningStopped is a singleton object") {
            (ScannerEvent.ScanningStopped === ScannerEvent.ScanningStopped) shouldBe true
        }

        it("ScanningPaused is a singleton object") {
            (ScannerEvent.ScanningPaused === ScannerEvent.ScanningPaused) shouldBe true
        }

        it("ScanningResumed is a singleton object") {
            (ScannerEvent.ScanningResumed === ScannerEvent.ScanningResumed) shouldBe true
        }
    }

    describe("ScannerEvent.ZoomStateChanged") {
        it("stores zoom values") {
            val event = ScannerEvent.ZoomStateChanged(1f, 10f, 3f)
            event.minZoom shouldBe 1f
            event.maxZoom shouldBe 10f
            event.currentZoom shouldBe 3f
        }

        it("equality for same zoom values") {
            val a = ScannerEvent.ZoomStateChanged(1f, 5f, 2f)
            val b = ScannerEvent.ZoomStateChanged(1f, 5f, 2f)
            a shouldBe b
        }
    }

    describe("ScannerEvent.BarcodesDetected") {
        it("stores barcode list") {
            val barcodes = listOf(
                BarcodeData("abc", BarcodeFormat.QR_CODE, null, null),
                BarcodeData("def", BarcodeFormat.EAN_13, null, null)
            )
            val event = ScannerEvent.BarcodesDetected(barcodes)
            event.barcodes shouldHaveSize 2
        }

        it("empty list is valid") {
            val event = ScannerEvent.BarcodesDetected(emptyList())
            event.barcodes.isEmpty() shouldBe true
        }
    }

    // ── ScannerEventManager ───────────────────────────────────────────────────

    describe("ScannerEventManager") {
        it("addListener registers listener") {
            val manager = ScannerEventManager()
            var received: ScannerEvent? = null
            val listener = object : ScannerEventListener { override fun onEvent(event: ScannerEvent) { received = event } }
            manager.addListener(listener)
            manager.emitEvent(ScannerEvent.ScanningStarted)
            received.shouldNotBeNull()
        }

        it("emitEvent calls all listeners") {
            val manager = ScannerEventManager()
            var count = 0
            val l1 = object : ScannerEventListener { override fun onEvent(event: ScannerEvent) { count++ } }
            val l2 = object : ScannerEventListener { override fun onEvent(event: ScannerEvent) { count++ } }
            manager.addListener(l1)
            manager.addListener(l2)
            manager.emitEvent(ScannerEvent.ScanningStarted)
            count shouldBe 2
        }

        it("removeListener stops delivery to removed listener") {
            val manager = ScannerEventManager()
            var count = 0
            val listener = object : ScannerEventListener { override fun onEvent(event: ScannerEvent) { count++ } }
            manager.addListener(listener)
            manager.emitEvent(ScannerEvent.ScanningStarted)
            manager.removeListener(listener)
            manager.emitEvent(ScannerEvent.ScanningStarted)
            count shouldBe 1
        }

        it("clear removes all listeners") {
            val manager = ScannerEventManager()
            var count = 0
            manager.addListener(object : ScannerEventListener { override fun onEvent(event: ScannerEvent) { count++ } })
            manager.addListener(object : ScannerEventListener { override fun onEvent(event: ScannerEvent) { count++ } })
            manager.clear()
            manager.emitEvent(ScannerEvent.ScanningStarted)
            count shouldBe 0
        }

        it("emitEvent with no listeners does not throw") {
            val manager = ScannerEventManager()
            manager.emitEvent(ScannerEvent.ScanningStarted) // no-op
        }

        it("listener receives correct event payload") {
            val manager = ScannerEventManager()
            var received: ScannerEvent? = null
            manager.addListener(object : ScannerEventListener { override fun onEvent(event: ScannerEvent) { received = event } })
            manager.emitEvent(ScannerEvent.CodeScanned("HELLO", "QR_CODE"))
            received.shouldNotBeNull()
            (received as ScannerEvent.CodeScanned).code shouldBe "HELLO"
        }

        it("multiple events can be emitted in sequence") {
            val manager = ScannerEventManager()
            val events = mutableListOf<ScannerEvent>()
            manager.addListener(object : ScannerEventListener { override fun onEvent(event: ScannerEvent) { events.add(event) } })
            manager.emitEvent(ScannerEvent.ScanningStarted)
            manager.emitEvent(ScannerEvent.ScanningPaused)
            manager.emitEvent(ScannerEvent.ScanningStopped)
            events shouldHaveSize 3
        }
    }
})
