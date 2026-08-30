package io.github.ismoy.imagepickerkmp.scanner.capture

import io.github.ismoy.imagepickerkmp.scanner.domain.model.ScannerResult

import io.github.ismoy.imagepickerkmp.scanner.domain.BarcodeFormat
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

class ScannerResultTest : DescribeSpec({

    describe("ScannerResult construction") {
        it("stores code, format, and timestamp") {
            val result = ScannerResult(
                code = "https://example.com",
                format = BarcodeFormat.QR_CODE,
                timestamp = 1_700_000_000L
            )
            result.code shouldBe "https://example.com"
            result.format shouldBe BarcodeFormat.QR_CODE
            result.timestamp shouldBe 1_700_000_000L
        }

        it("format defaults to null") {
            val result = ScannerResult(code = "12345", timestamp = 100L)
            result.format.shouldBeNull()
        }

        it("format can be set explicitly") {
            val result = ScannerResult(code = "abc", format = BarcodeFormat.CODE_128, timestamp = 0L)
            result.format.shouldNotBeNull()
            result.format shouldBe BarcodeFormat.CODE_128
        }

        it("timestamp zero is valid") {
            val result = ScannerResult(code = "x", timestamp = 0L)
            result.timestamp shouldBe 0L
        }

        it("empty code string is allowed") {
            val result = ScannerResult(code = "", timestamp = 1L)
            result.code shouldBe ""
        }

        it("long code string is stored correctly") {
            val longCode = "A".repeat(2000)
            val result = ScannerResult(code = longCode, timestamp = 1L)
            result.code.length shouldBe 2000
        }
    }

    describe("ScannerResult equality") {
        it("two identical results are equal") {
            val a = ScannerResult("abc", BarcodeFormat.QR_CODE, 100L)
            val b = ScannerResult("abc", BarcodeFormat.QR_CODE, 100L)
            a shouldBe b
        }

        it("different code means not equal") {
            val a = ScannerResult("abc", BarcodeFormat.QR_CODE, 100L)
            val b = ScannerResult("xyz", BarcodeFormat.QR_CODE, 100L)
            a shouldNotBe b
        }

        it("different timestamp means not equal") {
            val a = ScannerResult("abc", BarcodeFormat.QR_CODE, 100L)
            val b = ScannerResult("abc", BarcodeFormat.QR_CODE, 200L)
            a shouldNotBe b
        }

        it("different format means not equal") {
            val a = ScannerResult("abc", BarcodeFormat.QR_CODE, 100L)
            val b = ScannerResult("abc", BarcodeFormat.EAN_13, 100L)
            a shouldNotBe b
        }
    }

    describe("ScannerResult copy") {
        it("copy changes only specified field") {
            val original = ScannerResult("abc", BarcodeFormat.QR_CODE, 100L)
            val copy = original.copy(code = "def")
            copy.code shouldBe "def"
            copy.format shouldBe BarcodeFormat.QR_CODE
            copy.timestamp shouldBe 100L
        }

        it("copy with new timestamp preserves other fields") {
            val original = ScannerResult("abc", BarcodeFormat.QR_CODE, 100L)
            val copy = original.copy(timestamp = 999L)
            copy.timestamp shouldBe 999L
            copy.code shouldBe "abc"
        }
    }

    describe("ScannerResult hashCode") {
        it("same values produce same hash") {
            val a = ScannerResult("abc", BarcodeFormat.QR_CODE, 100L)
            val b = ScannerResult("abc", BarcodeFormat.QR_CODE, 100L)
            a.hashCode() shouldBe b.hashCode()
        }
    }
})
