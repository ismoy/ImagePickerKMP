package io.github.ismoy.imagepickerkmp.scanner.camera

import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerAdvancedFeaturesConfig
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerBehaviorConfig
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig

import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerCaptureManager
import io.github.ismoy.imagepickerkmp.scanner.domain.model.BarcodeData
import io.github.ismoy.imagepickerkmp.scanner.domain.model.ScannerRect
import io.github.ismoy.imagepickerkmp.scanner.domain.BarcodeFormat
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldBeNull

/** Fake [ScannerCaptureManager] that records zoom calls. */
class FakeScannerCaptureManager : ScannerCaptureManager {
    var lastZoom: Float? = null
    override fun startScanning() {}
    override fun stopScanning() {}
    override fun pauseScanning() {}
    override fun resumeScanning() {}
    override fun toggleFlash() {}
    override fun setZoom(scale: Float) { lastZoom = scale }
    override fun setZoomProgress(progress: Float) {}
    override fun setFocus(x: Float, y: Float) {}
}

class ScannerAutoZoomControllerTest : DescribeSpec({

    fun makeController(enableAutoZoom: Boolean = true, threshold: Float = 0.01f): ScannerAutoZoomController {
        val config = ScannerCameraConfig(
            advanced = ScannerAdvancedFeaturesConfig(enableAutoZoom = enableAutoZoom),
            behavior = ScannerBehaviorConfig(areaRatioThreshold = threshold)
        )
        return ScannerAutoZoomController(config)
    }

    fun makeBarcode(
        width: Float, height: Float,
        srcW: Float = 1920f, srcH: Float = 1080f
    ): BarcodeData {
        val rect = ScannerRect(0f, 0f, width, height, srcW, srcH, 0)
        return BarcodeData("code", BarcodeFormat.QR_CODE, rect, null)
    }

    describe("handleAutoZoom — disabled") {
        it("does nothing when enableAutoZoom = false") {
            val controller = makeController(enableAutoZoom = false)
            val fake = FakeScannerCaptureManager()
            val barcode = makeBarcode(50f, 50f)
            controller.handleAutoZoom(listOf(barcode), 1f, 10f, fake)
            fake.lastZoom.shouldBeNull()
        }
    }

    describe("handleAutoZoom — empty barcodes") {
        it("does nothing for empty barcode list") {
            val controller = makeController()
            val fake = FakeScannerCaptureManager()
            controller.handleAutoZoom(emptyList(), 1f, 10f, fake)
            fake.lastZoom.shouldBeNull()
        }
    }

    describe("handleAutoZoom — at max zoom") {
        it("does nothing when currentZoom >= maxZoom") {
            val controller = makeController()
            val fake = FakeScannerCaptureManager()
            val barcode = makeBarcode(10f, 10f) // tiny area → would zoom
            controller.handleAutoZoom(listOf(barcode), 10f, 10f, fake) // at max
            fake.lastZoom.shouldBeNull()
        }
    }

    describe("handleAutoZoom — area too small → triggers zoom") {
        it("zooms in by 0.5 when barcode area ratio < threshold") {
            // barcode 10x10 in 1920x1080 → ratio = 100 / 2_073_600 ≈ 0.000048 < 0.01
            val controller = makeController(threshold = 0.01f)
            val fake = FakeScannerCaptureManager()
            val barcode = makeBarcode(10f, 10f)
            controller.handleAutoZoom(listOf(barcode), 2f, 10f, fake)
            fake.lastZoom shouldBe 1.25f // target 2.5 / current 2 = relative multiplier 1.25
        }

        it("clamps zoom to maxZoom when increment would exceed it") {
            val controller = makeController(threshold = 0.01f)
            val fake = FakeScannerCaptureManager()
            val barcode = makeBarcode(10f, 10f)
            controller.handleAutoZoom(listOf(barcode), 9.8f, 10f, fake)
            fake.lastZoom shouldBe 10f / 9.8f // target 10 / current 9.8 = relative multiplier
        }
    }

    describe("handleAutoZoom — area large enough → no zoom") {
        it("does not zoom when barcode area ratio >= threshold") {
            // barcode 500x400 in 1920x1080 → ratio = 200000 / 2073600 ≈ 0.096 > 0.01
            val controller = makeController(threshold = 0.01f)
            val fake = FakeScannerCaptureManager()
            val barcode = makeBarcode(500f, 400f)
            controller.handleAutoZoom(listOf(barcode), 2f, 10f, fake)
            fake.lastZoom.shouldBeNull()
        }
    }

    describe("handleAutoZoom — null scanner") {
        it("does not crash when scanner is null") {
            val controller = makeController()
            val barcode = makeBarcode(10f, 10f)
            // No exception expected
            controller.handleAutoZoom(listOf(barcode), 1f, 10f, null)
        }
    }

    describe("handleAutoZoom — barcode without boundingBox") {
        it("skips barcodes with null boundingBox") {
            val controller = makeController()
            val fake = FakeScannerCaptureManager()
            val barcode = BarcodeData("x", BarcodeFormat.QR_CODE, null, null)
            controller.handleAutoZoom(listOf(barcode), 1f, 10f, fake)
            fake.lastZoom.shouldBeNull()
        }
    }

    describe("handleAutoZoom — largest barcode is selected") {
        it("picks the largest barcode by area for zoom decision") {
            // large barcode: area ratio >> threshold → no zoom
            // small barcode: area ratio << threshold → would zoom
            // largest wins → no zoom
            val controller = makeController(threshold = 0.01f)
            val fake = FakeScannerCaptureManager()
            val large = makeBarcode(800f, 600f) // ratio = 480000/2073600 ≈ 0.23 > 0.01
            val small = makeBarcode(5f, 5f)     // ratio = 25/2073600 ≈ tiny
            controller.handleAutoZoom(listOf(small, large), 2f, 10f, fake)
            fake.lastZoom.shouldBeNull()  // largest barcode is big enough
        }
    }
})
