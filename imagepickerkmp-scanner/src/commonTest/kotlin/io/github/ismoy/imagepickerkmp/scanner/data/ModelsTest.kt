package io.github.ismoy.imagepickerkmp.scanner.data

import io.github.ismoy.imagepickerkmp.scanner.domain.model.BarcodeData
import io.github.ismoy.imagepickerkmp.scanner.domain.model.CameraPositionDistance
import io.github.ismoy.imagepickerkmp.scanner.domain.model.FlashMode
import io.github.ismoy.imagepickerkmp.scanner.domain.model.ScannerPoint
import io.github.ismoy.imagepickerkmp.scanner.domain.model.ScannerRect
import io.github.ismoy.imagepickerkmp.scanner.domain.BarcodeFormat
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.collections.shouldHaveSize

class ModelsTest : DescribeSpec({

    // ── ScannerPoint ──────────────────────────────────────────────────────────

    describe("ScannerPoint") {
        it("stores x and y correctly") {
            val p = ScannerPoint(1.5f, 2.5f)
            p.x shouldBe 1.5f
            p.y shouldBe 2.5f
        }

        it("equality works for same values") {
            ScannerPoint(1f, 2f) shouldBe ScannerPoint(1f, 2f)
        }

        it("inequality for different values") {
            ScannerPoint(1f, 2f) shouldNotBe ScannerPoint(3f, 4f)
        }

        it("copy changes only specified field") {
            val original = ScannerPoint(10f, 20f)
            val copy = original.copy(x = 99f)
            copy.x shouldBe 99f
            copy.y shouldBe 20f
        }

        it("zero values are valid") {
            val p = ScannerPoint(0f, 0f)
            p.x shouldBe 0f
            p.y shouldBe 0f
        }

        it("negative values are valid") {
            val p = ScannerPoint(-5f, -10f)
            p.x shouldBe -5f
            p.y shouldBe -10f
        }

        it("hashCode is consistent for equal instances") {
            ScannerPoint(1f, 2f).hashCode() shouldBe ScannerPoint(1f, 2f).hashCode()
        }
    }

    // ── ScannerRect ───────────────────────────────────────────────────────────

    describe("ScannerRect") {
        val rect = ScannerRect(
            left = 10f, top = 20f, right = 110f, bottom = 80f,
            sourceWidth = 1920f, sourceHeight = 1080f, rotation = 0
        )

        it("stores all fields correctly") {
            rect.left shouldBe 10f
            rect.top shouldBe 20f
            rect.right shouldBe 110f
            rect.bottom shouldBe 80f
            rect.sourceWidth shouldBe 1920f
            rect.sourceHeight shouldBe 1080f
            rect.rotation shouldBe 0
        }

        it("computed width is right - left") {
            rect.width shouldBe 100f
        }

        it("computed height is bottom - top") {
            rect.height shouldBe 60f
        }

        it("width is zero when left equals right") {
            val squashed = rect.copy(left = 50f, right = 50f)
            squashed.width shouldBe 0f
        }

        it("height is zero when top equals bottom") {
            val squashed = rect.copy(top = 30f, bottom = 30f)
            squashed.height shouldBe 0f
        }

        it("equality works for same values") {
            val a = ScannerRect(0f, 0f, 100f, 100f, 200f, 200f, 0)
            val b = ScannerRect(0f, 0f, 100f, 100f, 200f, 200f, 0)
            a shouldBe b
        }

        it("inequality for different rotation") {
            val a = ScannerRect(0f, 0f, 100f, 100f, 200f, 200f, 0)
            val b = a.copy(rotation = 90)
            a shouldNotBe b
        }

        it("copy changes only specified field") {
            val modified = rect.copy(rotation = 180)
            modified.rotation shouldBe 180
            modified.left shouldBe rect.left
            modified.sourceWidth shouldBe rect.sourceWidth
        }

        it("area calculation using width * height") {
            val area = rect.width * rect.height
            area shouldBe 6000f
        }

        it("areaRatio relative to source image") {
            val ratio = (rect.width * rect.height) / (rect.sourceWidth * rect.sourceHeight)
            // 100*60 / (1920*1080) ≈ 0.00289
            (ratio > 0f) shouldBe true
            (ratio < 1f) shouldBe true
        }
    }

    // ── BarcodeData ───────────────────────────────────────────────────────────

    describe("BarcodeData") {
        it("stores rawValue and format") {
            val data = BarcodeData(
                rawValue = "https://example.com",
                format = BarcodeFormat.QR_CODE,
                boundingBox = null,
                cornerPoints = null
            )
            data.rawValue shouldBe "https://example.com"
            data.format shouldBe BarcodeFormat.QR_CODE
        }

        it("boundingBox can be null") {
            val data = BarcodeData("val", BarcodeFormat.EAN_13, null, null)
            data.boundingBox.shouldBeNull()
        }

        it("cornerPoints can be null") {
            val data = BarcodeData("val", BarcodeFormat.EAN_13, null, null)
            data.cornerPoints.shouldBeNull()
        }

        it("stores non-null boundingBox") {
            val rect = ScannerRect(0f, 0f, 100f, 100f, 1920f, 1080f, 0)
            val data = BarcodeData("val", BarcodeFormat.QR_CODE, rect, null)
            data.boundingBox.shouldNotBeNull()
            data.boundingBox.width shouldBe 100f
        }

        it("stores cornerPoints list") {
            val points = listOf(
                ScannerPoint(0f, 0f),
                ScannerPoint(100f, 0f),
                ScannerPoint(100f, 100f),
                ScannerPoint(0f, 100f)
            )
            val data = BarcodeData("val", BarcodeFormat.QR_CODE, null, points)
            data.cornerPoints.shouldNotBeNull()
            data.cornerPoints shouldHaveSize 4
        }

        it("equality works for same values") {
            val a = BarcodeData("abc", BarcodeFormat.CODE_128, null, null)
            val b = BarcodeData("abc", BarcodeFormat.CODE_128, null, null)
            a shouldBe b
        }

        it("inequality for different rawValue") {
            val a = BarcodeData("abc", BarcodeFormat.CODE_128, null, null)
            val b = BarcodeData("xyz", BarcodeFormat.CODE_128, null, null)
            a shouldNotBe b
        }

        it("copy changes only specified field") {
            val original = BarcodeData("abc", BarcodeFormat.QR_CODE, null, null)
            val copy = original.copy(rawValue = "def")
            copy.rawValue shouldBe "def"
            copy.format shouldBe BarcodeFormat.QR_CODE
        }
    }

    // ── CameraPositionDistance ────────────────────────────────────────────────

    describe("CameraPositionDistance") {
        it("has exactly 4 entries") {
            CameraPositionDistance.entries.size shouldBe 4
        }

        it("TOO_FAR entry exists") {
            CameraPositionDistance.valueOf("TOO_FAR") shouldBe CameraPositionDistance.TOO_FAR
        }

        it("TOO_CLOSE entry exists") {
            CameraPositionDistance.valueOf("TOO_CLOSE") shouldBe CameraPositionDistance.TOO_CLOSE
        }

        it("OPTIMAL entry exists") {
            CameraPositionDistance.valueOf("OPTIMAL") shouldBe CameraPositionDistance.OPTIMAL
        }

        it("UNKNOWN entry exists") {
            CameraPositionDistance.valueOf("UNKNOWN") shouldBe CameraPositionDistance.UNKNOWN
        }

        it("all entries have unique ordinals") {
            val ordinals = CameraPositionDistance.entries.map { it.ordinal }
            ordinals.toSet().size shouldBe ordinals.size
        }

        it("exhaustive when covers all values") {
            CameraPositionDistance.entries.forEach { d ->
                val label = when (d) {
                    CameraPositionDistance.TOO_FAR -> "far"
                    CameraPositionDistance.TOO_CLOSE -> "close"
                    CameraPositionDistance.OPTIMAL -> "ok"
                    CameraPositionDistance.UNKNOWN -> "unk"
                }
                label.isNotEmpty() shouldBe true
            }
        }
    }

    // ── FlashMode ─────────────────────────────────────────────────────────────

    describe("FlashMode") {
        it("has exactly 2 entries") {
            FlashMode.entries.size shouldBe 2
        }

        it("ON entry exists") {
            FlashMode.valueOf("ON") shouldBe FlashMode.ON
        }

        it("OFF entry exists") {
            FlashMode.valueOf("OFF") shouldBe FlashMode.OFF
        }

        it("ON and OFF are not equal") {
            FlashMode.ON shouldNotBe FlashMode.OFF
        }

        it("exhaustive when covers both modes") {
            FlashMode.entries.forEach { mode ->
                val label = when (mode) {
                    FlashMode.ON -> "on"
                    FlashMode.OFF -> "off"
                }
                label.isNotEmpty() shouldBe true
            }
        }

        it("toggle logic — ON becomes OFF") {
            val current = FlashMode.ON
            val toggled = if (current == FlashMode.ON) FlashMode.OFF else FlashMode.ON
            toggled shouldBe FlashMode.OFF
        }

        it("toggle logic — OFF becomes ON") {
            val current = FlashMode.OFF
            val toggled = if (current == FlashMode.ON) FlashMode.OFF else FlashMode.ON
            toggled shouldBe FlashMode.ON
        }
    }
})
