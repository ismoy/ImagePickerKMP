package io.github.ismoy.imagepickerkmp.scanner.picker

import io.github.ismoy.imagepickerkmp.scanner.camera.config.InactiveOverlayStyle
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerAdvancedFeaturesConfig
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerBehaviorConfig
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerOverlayStyle
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerUIConfig
import io.github.ismoy.imagepickerkmp.scanner.camera.config.EnterpriseOverlayConfig
import io.github.ismoy.imagepickerkmp.scanner.domain.BarcodeFormat
import io.github.ismoy.imagepickerkmp.scanner.domain.HapticFeedbackMode
import io.github.ismoy.imagepickerkmp.scanner.permission.ScannerPermissionConfig
import io.github.ismoy.imagepickerkmp.scanner.ui.ScannerUIExtensions
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.collections.shouldContain

class ScannerPickerConfigTest : DescribeSpec({

    // ── ScannerPickerConfig ───────────────────────────────────────────────────

    describe("ScannerPickerConfig") {
        it("default() creates a config with all defaults") {
            val config = ScannerPickerConfig.default()
            config.camera shouldBe ScannerCameraConfig()
            config.permissions shouldBe ScannerPermissionConfig()
            config.uiExtensions shouldBe ScannerUIExtensions()
        }

        it("equality of two default configs") {
            ScannerPickerConfig() shouldBe ScannerPickerConfig()
        }

        it("custom camera config is stored") {
            val camConfig = ScannerCameraConfig.minimal()
            val config = ScannerPickerConfig(camera = camConfig)
            config.camera shouldBe camConfig
        }

        it("copy changes only specified field") {
            val original = ScannerPickerConfig()
            val modified = original.copy(permissions = ScannerPermissionConfig(
                titleDialogConfig = "Custom title",
                descriptionDialogConfig = "desc",
                btnDialogConfig = "btn",
                titleDialogDenied = "denied title",
                descriptionDialogDenied = "denied desc",
                btnDialogDenied = "settings",
                cancelButtonText = "cancel"
            ))
            modified.permissions.titleDialogConfig shouldBe "Custom title"
            modified.camera shouldBe original.camera
        }
    }

    // ── ScannerBehaviorConfig ─────────────────────────────────────────────────

    describe("ScannerBehaviorConfig defaults") {
        val config = ScannerBehaviorConfig()

        it("playSound defaults to true") { config.playSound shouldBe true }
        it("hapticFeedback defaults to SOUND_AND_VIBRATE") {
            config.hapticFeedback shouldBe HapticFeedbackMode.SOUND_AND_VIBRATE
        }
        it("delayToNextScan defaults to 2000") { config.delayToNextScan shouldBe 2000L }
        it("areaRatioThreshold defaults to 0.01f") { config.areaRatioThreshold shouldBe 0.01f }
        it("tooCloseAreaRatioThreshold defaults to 0.4f") {
            config.tooCloseAreaRatioThreshold shouldBe 0.4f
        }
        it("requiredConsecutiveReadings defaults to 2") {
            config.requiredConsecutiveReadings shouldBe 2
        }
        it("distanceBufferSize defaults to 3") { config.distanceBufferSize shouldBe 3 }
        it("maliciousCodeCooldown defaults to 3000") { config.maliciousCodeCooldown shouldBe 3000L }
        it("maxCodeLength defaults to 2000") { config.maxCodeLength shouldBe 2000 }
        it("maxSpecialCharRatio defaults to 0.15f") { config.maxSpecialCharRatio shouldBe 0.15f }
        it("enableSecurityAlerts defaults to true") { config.enableSecurityAlerts shouldBe true }
        it("enableFlashControl defaults to true") { config.enableFlashControl shouldBe true }
        it("showFlashButton defaults to true") { config.showFlashButton shouldBe true }
        it("enableInactivity defaults to true") { config.enableInactivity shouldBe true }
        it("inactivityDelay defaults to 30000") { config.inactivityDelay shouldBe 30_000L }
        it("allowedFormats defaults to [BarcodeFormat.ALL]") {
            config.allowedFormats shouldContain BarcodeFormat.ALL
        }
        it("enablePinchToZoom defaults to true") { config.enablePinchToZoom shouldBe true }
        it("enableTapToFocus defaults to true") { config.enableTapToFocus shouldBe true }
        it("supportInvertedBarcodes defaults to false") {
            config.supportInvertedBarcodes shouldBe true
        }
        it("allowDuplicates defaults to false") { config.allowDuplicates shouldBe false }
        it("regionOfInterest defaults to null") { config.regionOfInterest.shouldBeNull() }
    }

    describe("ScannerBehaviorConfig custom values") {
        it("custom delayToNextScan is stored") {
            val config = ScannerBehaviorConfig(delayToNextScan = 5000L)
            config.delayToNextScan shouldBe 5000L
        }

        it("allowDuplicates = true is stored") {
            val config = ScannerBehaviorConfig(allowDuplicates = true)
            config.allowDuplicates shouldBe true
        }

        it("equality of same values") {
            ScannerBehaviorConfig() shouldBe ScannerBehaviorConfig()
        }

        it("copy changes only specified field") {
            val original = ScannerBehaviorConfig()
            val copy = original.copy(playSound = false)
            copy.playSound shouldBe false
            copy.delayToNextScan shouldBe original.delayToNextScan
        }
    }

    // ── ScannerAdvancedFeaturesConfig ─────────────────────────────────────────

    describe("ScannerAdvancedFeaturesConfig defaults") {
        val config = ScannerAdvancedFeaturesConfig()

        it("batchMode defaults to false") { config.batchMode shouldBe false }
        it("enableMetallicMode defaults to false") { config.enableMetallicMode shouldBe false }
        it("showZoomControl defaults to true") { config.showZoomControl shouldBe true }
        it("enableAROverlays defaults to false") { config.enableAROverlays shouldBe false }
        it("enablePickAndPack defaults to false") { config.enablePickAndPack shouldBe false }
        it("enableAutoZoom defaults to false") { config.enableAutoZoom shouldBe false }
        it("customBarcodeContent defaults to null") { config.customBarcodeContent.shouldBeNull() }
    }

    // ── ScannerCameraConfig factory methods ───────────────────────────────────

    describe("ScannerCameraConfig factory methods") {
        it("default() creates a config equal to ScannerCameraConfig()") {
            ScannerCameraConfig.default() shouldBe ScannerCameraConfig()
        }

        it("minimal() has playSound = false") {
            ScannerCameraConfig.minimal().behavior.playSound shouldBe false
        }

        it("minimal() has enableSecurityAlerts = false") {
            ScannerCameraConfig.minimal().behavior.enableSecurityAlerts shouldBe false
        }

        it("minimal() has empty watermark") {
            ScannerCameraConfig.minimal().ui.watermark shouldBe ""
        }

        it("highSecurity() has delayToNextScan = 3000") {
            ScannerCameraConfig.highSecurity().behavior.delayToNextScan shouldBe 3000L
        }

        it("highSecurity() has requiredConsecutiveReadings = 3") {
            ScannerCameraConfig.highSecurity().behavior.requiredConsecutiveReadings shouldBe 3
        }

        it("highSecurity() has maxCodeLength = 1000") {
            ScannerCameraConfig.highSecurity().behavior.maxCodeLength shouldBe 1000
        }

        it("three factory configs are all different") {
            val d = ScannerCameraConfig.default()
            val m = ScannerCameraConfig.minimal()
            val h = ScannerCameraConfig.highSecurity()
            (d == m) shouldBe false
            (d == h) shouldBe false
            (m == h) shouldBe false
        }
    }

    // ── ScannerOverlayStyle / InactiveOverlayStyle ────────────────────────────

    describe("ScannerOverlayStyle") {
        it("has CLASSIC and ANIMATED_LINE") {
            ScannerOverlayStyle.entries.size shouldBe 2
            ScannerOverlayStyle.valueOf("CLASSIC") shouldBe ScannerOverlayStyle.CLASSIC
            ScannerOverlayStyle.valueOf("ANIMATED_LINE") shouldBe ScannerOverlayStyle.ANIMATED_LINE
        }
    }

    describe("InactiveOverlayStyle") {
        it("has SIMPLE and ENTERPRISE") {
            InactiveOverlayStyle.entries.size shouldBe 2
            InactiveOverlayStyle.valueOf("SIMPLE") shouldBe InactiveOverlayStyle.SIMPLE
            InactiveOverlayStyle.valueOf("ENTERPRISE") shouldBe InactiveOverlayStyle.ENTERPRISE
        }
    }

    // ── EnterpriseOverlayConfig ───────────────────────────────────────────────

    describe("EnterpriseOverlayConfig") {
        it("all fields default to null or empty") {
            val config = EnterpriseOverlayConfig()
            config.title.shouldBeNull()
            config.tag.shouldBeNull()
            config.statusLabel.shouldBeNull()
            config.statusValue.shouldBeNull()
            config.infoLine1Label.shouldBeNull()
            config.infoLine1Value.shouldBeNull()
            config.infoLine2Label.shouldBeNull()
            config.infoLine2Value.shouldBeNull()
            config.footerLeftLines shouldBe emptyList()
            config.footerRightLines shouldBe emptyList()
            config.showIdleStats shouldBe false
        }

        it("custom values are stored correctly") {
            val config = EnterpriseOverlayConfig(
                title = "Scanner v2",
                tag = "PROD",
                statusLabel = "Status",
                statusValue = "Active",
                showIdleStats = true,
                footerLeftLines = listOf("Line 1", "Line 2"),
                footerRightLines = listOf("Right 1")
            )
            config.title shouldBe "Scanner v2"
            config.tag shouldBe "PROD"
            config.showIdleStats shouldBe true
            config.footerLeftLines.size shouldBe 2
            config.footerRightLines.size shouldBe 1
        }
    }

    // ── ScannerPermissionConfig ───────────────────────────────────────────────

    describe("ScannerPermissionConfig") {
        it("customDeniedDialog defaults to null") {
            ScannerPermissionConfig().customDeniedDialog.shouldBeNull()
        }

        it("customSettingsDialog defaults to null") {
            ScannerPermissionConfig().customSettingsDialog.shouldBeNull()
        }

        it("custom title is stored") {
            val config = ScannerPermissionConfig(titleDialogConfig = "Custom title")
            config.titleDialogConfig shouldBe "Custom title"
        }

        it("equality for same values") {
            // Same I18nKonfig values → equal
            ScannerPermissionConfig() shouldBe ScannerPermissionConfig()
        }

        it("copy changes only specified field") {
            val original = ScannerPermissionConfig()
            val copy = original.copy(cancelButtonText = "Dismiss")
            copy.cancelButtonText shouldBe "Dismiss"
            copy.titleDialogConfig shouldBe original.titleDialogConfig
        }
    }

    // ── ScannerUIExtensions ───────────────────────────────────────────────────

    describe("ScannerUIExtensions") {
        it("all slots default to null") {
            val ext = ScannerUIExtensions()
            ext.customOverlay.shouldBeNull()
            ext.customFlashButton.shouldBeNull()
            ext.customBatchDoneButton.shouldBeNull()
            ext.customInactiveOverlay.shouldBeNull()
            ext.customLayout.shouldBeNull()
        }

        it("equality of two default instances") {
            ScannerUIExtensions() shouldBe ScannerUIExtensions()
        }
    }

    // ── ScannerPickerMode ─────────────────────────────────────────────────────

    describe("ScannerPickerMode") {
        it("None is a singleton data object") {
            (ScannerPickerMode.None === ScannerPickerMode.None) shouldBe true
        }

        it("Camera stores onDismiss") {
            var called = false
            val mode = ScannerPickerMode.Camera(onDismiss = { called = true })
            mode.onDismiss.shouldNotBeNull()
            mode.onDismiss.invoke()
            called shouldBe true
        }

        it("Camera stores onError") {
            var caught: Exception? = null
            val mode = ScannerPickerMode.Camera(onError = { caught = it })
            mode.onError.shouldNotBeNull()
            mode.onError.invoke(RuntimeException("err"))
            caught?.message shouldBe "err"
        }

        it("Camera with null callbacks is valid") {
            val mode = ScannerPickerMode.Camera()
            mode.onDismiss.shouldBeNull()
            mode.onError.shouldBeNull()
        }

        it("two Camera modes with same lambdas are equal by reference") {
            val dm: () -> Unit = {}
            val mode1 = ScannerPickerMode.Camera(onDismiss = dm)
            val mode2 = ScannerPickerMode.Camera(onDismiss = dm)
            mode1 shouldBe mode2
        }

        it("None != Camera") {
            (ScannerPickerMode.None == ScannerPickerMode.Camera()) shouldBe false
        }
    }
})
