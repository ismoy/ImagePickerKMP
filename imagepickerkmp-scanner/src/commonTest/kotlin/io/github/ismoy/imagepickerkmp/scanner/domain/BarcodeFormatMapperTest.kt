package io.github.ismoy.imagepickerkmp.scanner.domain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

class BarcodeFormatMapperTest : DescribeSpec({

    describe("BaseBarcodeFormatMapper with String keys") {

        fun makeMapper(): BaseBarcodeFormatMapper<String> = BaseBarcodeFormatMapper()

        it("registerMapping and getDomainFormat round-trip") {
            val mapper = makeMapper()
            mapper.registerMapping("256", BarcodeFormat.QR_CODE)
            mapper.getDomainFormat("256") shouldBe BarcodeFormat.QR_CODE
        }

        it("registerMapping and getNativeFormat round-trip") {
            val mapper = makeMapper()
            mapper.registerMapping("256", BarcodeFormat.QR_CODE)
            mapper.getNativeFormat(BarcodeFormat.QR_CODE) shouldBe "256"
        }

        it("getDomainFormat for unknown key returns UNKNOWN") {
            val mapper = makeMapper()
            mapper.getDomainFormat("999") shouldBe BarcodeFormat.UNKNOWN
        }

        it("getNativeFormat for unmapped domain returns null") {
            val mapper = makeMapper()
            mapper.getNativeFormat(BarcodeFormat.EAN_13).shouldBeNull()
        }

        it("multiple mappings can be registered") {
            val mapper = makeMapper()
            mapper.registerMapping("256", BarcodeFormat.QR_CODE)
            mapper.registerMapping("1", BarcodeFormat.CODE_128)
            mapper.registerMapping("2", BarcodeFormat.EAN_13)

            mapper.getDomainFormat("256") shouldBe BarcodeFormat.QR_CODE
            mapper.getDomainFormat("1") shouldBe BarcodeFormat.CODE_128
            mapper.getDomainFormat("2") shouldBe BarcodeFormat.EAN_13
        }

        it("overwriting a mapping updates both directions") {
            val mapper = makeMapper()
            mapper.registerMapping("256", BarcodeFormat.QR_CODE)
            mapper.registerMapping("256", BarcodeFormat.AZTEC)   // overwrite
            mapper.getDomainFormat("256") shouldBe BarcodeFormat.AZTEC
        }

        it("getNativeFormat returns non-null for registered format") {
            val mapper = makeMapper()
            mapper.registerMapping("pdf417", BarcodeFormat.PDF_417)
            mapper.getNativeFormat(BarcodeFormat.PDF_417).shouldNotBeNull()
            mapper.getNativeFormat(BarcodeFormat.PDF_417) shouldBe "pdf417"
        }

        it("mapper works with Int keys") {
            val mapper = BaseBarcodeFormatMapper<Int>()
            mapper.registerMapping(1, BarcodeFormat.EAN_8)
            mapper.getDomainFormat(1) shouldBe BarcodeFormat.EAN_8
            mapper.getNativeFormat(BarcodeFormat.EAN_8) shouldBe 1
        }

        it("all BarcodeFormats can be registered and retrieved") {
            val mapper = makeMapper()
            BarcodeFormat.entries.forEachIndexed { i, fmt ->
                mapper.registerMapping("native_$i", fmt)
            }
            BarcodeFormat.entries.forEachIndexed { i, fmt ->
                mapper.getDomainFormat("native_$i") shouldBe fmt
                mapper.getNativeFormat(fmt).shouldNotBeNull()
            }
        }

        it("getDomainFormat for empty string returns UNKNOWN when not registered") {
            val mapper = makeMapper()
            mapper.getDomainFormat("") shouldBe BarcodeFormat.UNKNOWN
        }

        it("concrete subclass works as BarcodeFormatMapper interface") {
            val mapper: BarcodeFormatMapper<String> = BaseBarcodeFormatMapper()
            mapper.registerMapping("qr", BarcodeFormat.QR_CODE)
            mapper.getDomainFormat("qr") shouldBe BarcodeFormat.QR_CODE
            mapper.getNativeFormat(BarcodeFormat.QR_CODE) shouldBe "qr"
            mapper.getNativeFormat(BarcodeFormat.UNKNOWN).shouldBeNull()
        }
    }

    describe("HapticFeedbackMode") {
        it("has exactly 3 entries") {
            HapticFeedbackMode.entries.size shouldBe 3
        }

        it("valueOf SOUND_AND_VIBRATE works") {
            HapticFeedbackMode.valueOf("SOUND_AND_VIBRATE") shouldBe HapticFeedbackMode.SOUND_AND_VIBRATE
        }

        it("valueOf SOUND_ONLY works") {
            HapticFeedbackMode.valueOf("SOUND_ONLY") shouldBe HapticFeedbackMode.SOUND_ONLY
        }

        it("valueOf VIBRATE_ONLY works") {
            HapticFeedbackMode.valueOf("VIBRATE_ONLY") shouldBe HapticFeedbackMode.VIBRATE_ONLY
        }

        it("all entries have unique names") {
            val names = HapticFeedbackMode.entries.map { it.name }
            names.toSet().size shouldBe names.size
        }

        it("exhaustive when covers all modes") {
            HapticFeedbackMode.entries.forEach { mode ->
                val label = when (mode) {
                    HapticFeedbackMode.SOUND_AND_VIBRATE -> "both"
                    HapticFeedbackMode.SOUND_ONLY -> "sound"
                    HapticFeedbackMode.VIBRATE_ONLY -> "vibrate"
                }
                label.isNotEmpty() shouldBe true
            }
        }
    }
})
