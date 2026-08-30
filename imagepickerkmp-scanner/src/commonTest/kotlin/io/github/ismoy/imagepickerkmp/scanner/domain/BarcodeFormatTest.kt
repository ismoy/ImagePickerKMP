package io.github.ismoy.imagepickerkmp.scanner.domain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize

class BarcodeFormatTest : DescribeSpec({

    describe("BarcodeFormat enum entries") {
        it("should have exactly 20 entries") {
            BarcodeFormat.entries shouldHaveSize 20
        }

        it("ALL entry exists") {
            BarcodeFormat.entries shouldContain BarcodeFormat.ALL
        }

        it("QR_CODE entry exists") {
            BarcodeFormat.entries shouldContain BarcodeFormat.QR_CODE
        }

        it("AZTEC entry exists") {
            BarcodeFormat.entries shouldContain BarcodeFormat.AZTEC
        }

        it("CODE_128 entry exists") {
            BarcodeFormat.entries shouldContain BarcodeFormat.CODE_128
        }

        it("CODE_39 entry exists") {
            BarcodeFormat.entries shouldContain BarcodeFormat.CODE_39
        }

        it("CODE_39_MOD_43 entry exists") {
            BarcodeFormat.entries shouldContain BarcodeFormat.CODE_39_MOD_43
        }

        it("CODE_93 entry exists") {
            BarcodeFormat.entries shouldContain BarcodeFormat.CODE_93
        }

        it("CODABAR entry exists") {
            BarcodeFormat.entries shouldContain BarcodeFormat.CODABAR
        }

        it("DATA_MATRIX entry exists") {
            BarcodeFormat.entries shouldContain BarcodeFormat.DATA_MATRIX
        }

        it("EAN_13 entry exists") {
            BarcodeFormat.entries shouldContain BarcodeFormat.EAN_13
        }

        it("EAN_8 entry exists") {
            BarcodeFormat.entries shouldContain BarcodeFormat.EAN_8
        }

        it("GS1_DATA_BAR entry exists") {
            BarcodeFormat.entries shouldContain BarcodeFormat.GS1_DATA_BAR
        }

        it("ITF entry exists") {
            BarcodeFormat.entries shouldContain BarcodeFormat.ITF
        }

        it("ITF_14 entry exists") {
            BarcodeFormat.entries shouldContain BarcodeFormat.ITF_14
        }

        it("MICRO_PDF_417 entry exists") {
            BarcodeFormat.entries shouldContain BarcodeFormat.MICRO_PDF_417
        }

        it("MICRO_QR_CODE entry exists") {
            BarcodeFormat.entries shouldContain BarcodeFormat.MICRO_QR_CODE
        }

        it("PDF_417 entry exists") {
            BarcodeFormat.entries shouldContain BarcodeFormat.PDF_417
        }

        it("UPC_A entry exists") {
            BarcodeFormat.entries shouldContain BarcodeFormat.UPC_A
        }

        it("UPC_E entry exists") {
            BarcodeFormat.entries shouldContain BarcodeFormat.UPC_E
        }

        it("UNKNOWN entry exists") {
            BarcodeFormat.entries shouldContain BarcodeFormat.UNKNOWN
        }
    }

    describe("BarcodeFormat valueOf") {
        it("valueOf QR_CODE returns correct entry") {
            BarcodeFormat.valueOf("QR_CODE") shouldBe BarcodeFormat.QR_CODE
        }

        it("valueOf EAN_13 returns correct entry") {
            BarcodeFormat.valueOf("EAN_13") shouldBe BarcodeFormat.EAN_13
        }

        it("valueOf UNKNOWN returns correct entry") {
            BarcodeFormat.valueOf("UNKNOWN") shouldBe BarcodeFormat.UNKNOWN
        }

        it("valueOf ALL returns correct entry") {
            BarcodeFormat.valueOf("ALL") shouldBe BarcodeFormat.ALL
        }
    }

    describe("BarcodeFormat name and ordinal") {
        it("QR_CODE name is QR_CODE") {
            BarcodeFormat.QR_CODE.name shouldBe "QR_CODE"
        }

        it("ALL is first entry (ordinal 0)") {
            BarcodeFormat.ALL.ordinal shouldBe 0
        }

        it("UNKNOWN is last entry") {
            BarcodeFormat.UNKNOWN.ordinal shouldBe BarcodeFormat.entries.size - 1
        }

        it("all entries have unique names") {
            val names = BarcodeFormat.entries.map { it.name }
            names.toSet().size shouldBe names.size
        }

        it("all entries have unique ordinals") {
            val ordinals = BarcodeFormat.entries.map { it.ordinal }
            ordinals.toSet().size shouldBe ordinals.size
        }
    }

    describe("BarcodeFormat when expression") {
        it("exhaustive when covers all variants") {
            BarcodeFormat.entries.forEach { fmt ->
                val label = when (fmt) {
                    BarcodeFormat.ALL -> "all"
                    BarcodeFormat.QR_CODE -> "qr"
                    BarcodeFormat.AZTEC -> "aztec"
                    BarcodeFormat.CODE_128 -> "128"
                    BarcodeFormat.CODE_39 -> "39"
                    BarcodeFormat.CODE_39_MOD_43 -> "39m"
                    BarcodeFormat.CODE_93 -> "93"
                    BarcodeFormat.CODABAR -> "coda"
                    BarcodeFormat.DATA_MATRIX -> "dm"
                    BarcodeFormat.EAN_13 -> "ean13"
                    BarcodeFormat.EAN_8 -> "ean8"
                    BarcodeFormat.GS1_DATA_BAR -> "gs1"
                    BarcodeFormat.ITF -> "itf"
                    BarcodeFormat.ITF_14 -> "itf14"
                    BarcodeFormat.MICRO_PDF_417 -> "mpdf"
                    BarcodeFormat.MICRO_QR_CODE -> "mqr"
                    BarcodeFormat.PDF_417 -> "pdf"
                    BarcodeFormat.UPC_A -> "upca"
                    BarcodeFormat.UPC_E -> "upce"
                    BarcodeFormat.UNKNOWN -> "unk"
                }
                label.isNotEmpty() shouldBe true
            }
        }
    }
})
