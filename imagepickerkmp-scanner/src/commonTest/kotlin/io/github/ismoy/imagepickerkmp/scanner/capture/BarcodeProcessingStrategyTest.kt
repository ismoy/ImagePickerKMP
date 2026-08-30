package io.github.ismoy.imagepickerkmp.scanner.capture

import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerAdvancedFeaturesConfig
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerBehaviorConfig
import io.github.ismoy.imagepickerkmp.scanner.domain.BarcodeFormat
import io.github.ismoy.imagepickerkmp.scanner.domain.model.CameraPositionDistance
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class BarcodeProcessingStrategyTest : DescribeSpec({
    fun strategy(
        behavior: ScannerBehaviorConfig = ScannerBehaviorConfig(),
        advanced: ScannerAdvancedFeaturesConfig = ScannerAdvancedFeaturesConfig()
    ) = BarcodeProcessingStrategy(behavior, advanced)

    describe("phantom detection") {
        it("accepts only similar values emitted within the configured cooldown") {
            val scanner = strategy(ScannerBehaviorConfig(maliciousCodeCooldown = 100L))

            scanner.isPhantomOfLastEmit("candidate", 1L) shouldBe false

            scanner.recordEmit("ABC123", 10L)
            scanner.isPhantomOfLastEmit("ABC123", 20L) shouldBe false
            scanner.isPhantomOfLastEmit("ABC124", 20L) shouldBe true
            scanner.isPhantomOfLastEmit("XYZ123", 20L) shouldBe false
            scanner.isPhantomOfLastEmit("A", 20L) shouldBe false
            scanner.isPhantomOfLastEmit("ABC124", 111L) shouldBe false
        }
    }

    describe("distance smoothing") {
        it("keeps the most frequent recent distance and evicts stale samples") {
            val scanner = strategy(ScannerBehaviorConfig(distanceBufferSize = 2))

            scanner.getSmoothedDistance() shouldBe CameraPositionDistance.TOO_FAR

            scanner.updateDistance(CameraPositionDistance.TOO_FAR)
            scanner.updateDistance(CameraPositionDistance.OPTIMAL)
            scanner.updateDistance(CameraPositionDistance.OPTIMAL)

            scanner.getSmoothedDistance() shouldBe CameraPositionDistance.OPTIMAL
        }

        it("classifies barcode area ratios") {
            val scanner = strategy(
                ScannerBehaviorConfig(
                    areaRatioThreshold = 0.1f,
                    tooCloseAreaRatioThreshold = 0.7f
                )
            )

            scanner.calculateDistance(0.05f) shouldBe CameraPositionDistance.TOO_FAR
            scanner.calculateDistance(0.5f) shouldBe CameraPositionDistance.OPTIMAL
            scanner.calculateDistance(0.9f) shouldBe CameraPositionDistance.TOO_CLOSE
        }
    }

    describe("format and security validation") {
        it("rejects overlong, suspicious, and unknown values while allowing valid configured formats") {
            val scanner = strategy(
                ScannerBehaviorConfig(
                    maxCodeLength = 3,
                    allowedFormats = listOf(BarcodeFormat.ALL)
                )
            )

            scanner.isValidFormat(BarcodeFormat.QR_CODE, "1234") shouldBe false
            scanner.isValidFormat(BarcodeFormat.QR_CODE, "\u0000a") shouldBe false
            scanner.isValidFormat(BarcodeFormat.UNKNOWN, "ok") shouldBe false
            scanner.isValidFormat(BarcodeFormat.QR_CODE, "ok") shouldBe true
            scanner.isValidFormat(BarcodeFormat.QR_CODE, "") shouldBe true
        }

        it("honors an explicit format allow-list and can disable security alerts") {
            val scanner = strategy(
                ScannerBehaviorConfig(
                    enableSecurityAlerts = false,
                    allowedFormats = listOf(BarcodeFormat.QR_CODE)
                )
            )

            scanner.isValidFormat(BarcodeFormat.QR_CODE, "\u0000allowed") shouldBe true
            scanner.isValidFormat(BarcodeFormat.EAN_13, "allowed") shouldBe false
        }
    }

    describe("emission policy") {
        it("requires an acceptable distance, consecutive readings, and a cooldown") {
            val scanner = strategy(
                ScannerBehaviorConfig(
                    delayToNextScan = 100L,
                    requiredConsecutiveReadings = 2
                )
            )

            scanner.shouldEmit("code", 200L) shouldBe false

            scanner.updateDistance(CameraPositionDistance.OPTIMAL)
            scanner.shouldEmit("code", 200L) shouldBe false
            scanner.shouldEmit("code", 201L) shouldBe true
            scanner.shouldEmit("code", 202L) shouldBe false

            scanner.shouldEmit("other", 400L) shouldBe false
        }

        it("allows batch mode to emit even when the camera is not at an optimal distance") {
            val scanner = strategy(
                behavior = ScannerBehaviorConfig(
                    delayToNextScan = 0L,
                    requiredConsecutiveReadings = 1
                ),
                advanced = ScannerAdvancedFeaturesConfig(batchMode = true)
            )

            scanner.shouldEmit("batch-code", 1L) shouldBe true
        }

        it("clears pending readings for invisible codes and resets all transient state") {
            val scanner = strategy(
                ScannerBehaviorConfig(
                    delayToNextScan = 0L,
                    requiredConsecutiveReadings = 2
                )
            )
            scanner.updateDistance(CameraPositionDistance.OPTIMAL)

            scanner.shouldEmit("hidden", 1L) shouldBe false
            scanner.retainVisibleCodes(emptySet())
            scanner.shouldEmit("hidden", 2L) shouldBe false

            scanner.recordEmit("last-code", 2L)
            scanner.reset()

            scanner.getSmoothedDistance() shouldBe CameraPositionDistance.TOO_FAR
            scanner.isPhantomOfLastEmit("nearby-code", 3L) shouldBe false
        }

        it("suppresses individual results only when both pick-and-pack and batch mode are enabled") {
            strategy(
                advanced = ScannerAdvancedFeaturesConfig(
                    batchMode = true,
                    enablePickAndPack = true
                )
            ).isPickAndPackSuppressed() shouldBe true

            strategy(
                advanced = ScannerAdvancedFeaturesConfig(batchMode = true)
            ).isPickAndPackSuppressed() shouldBe false
        }
    }
})
