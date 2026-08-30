package io.github.ismoy.imagepickerkmp.scanner.utils

import io.github.ismoy.imagepickerkmp.core.logger.MediaLogger
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/** Minimal fake logger that records calls for assertion. */
class FakeLogger : MediaLogger {
    val infoMessages = mutableListOf<Pair<String, String>>()
    val errorMessages = mutableListOf<Triple<String, String, Throwable?>>()
    val warningMessages = mutableListOf<Triple<String, String, Throwable?>>()

    override fun debug(tag: String, message: String) {}
    override fun info(tag: String, message: String) {
        infoMessages.add(tag to message)
    }
    override fun warning(tag: String, message: String, throwable: Throwable?) {
        warningMessages.add(Triple(tag, message, throwable))
    }
    override fun error(tag: String, message: String, throwable: Throwable?) {
        errorMessages.add(Triple(tag, message, throwable))
    }
}

class LoggerTest : DescribeSpec({

    describe("LoggerFactory") {
        it("getLogger returns non-null logger") {
            LoggerFactory.getLogger() shouldNotBe null
        }

        it("setLogger replaces the logger") {
            val fake = FakeLogger()
            val original = LoggerFactory.getLogger()
            LoggerFactory.setLogger(fake)
            LoggerFactory.getLogger() shouldBe fake
            // Restore original for test isolation
            LoggerFactory.setLogger(original)
        }

        it("getLogger returns new logger after setLogger") {
            val fake = FakeLogger()
            val original = LoggerFactory.getLogger()
            LoggerFactory.setLogger(fake)
            val got = LoggerFactory.getLogger()
            got.shouldBeInstanceOf<FakeLogger>()
            LoggerFactory.setLogger(original)
        }
    }

    describe("MediaLogger.scanner extension") {
        it("logs info when no throwable") {
            val fake = FakeLogger()
            fake.scanner("barcode detected")
            fake.infoMessages.size shouldBe 1
            fake.infoMessages[0].first shouldBe "Scanner"
            fake.infoMessages[0].second shouldBe "barcode detected"
        }

        it("logs error when throwable provided") {
            val fake = FakeLogger()
            val cause = RuntimeException("fail")
            fake.scanner("scanner error", cause)
            fake.errorMessages.size shouldBe 1
            fake.errorMessages[0].second shouldBe "scanner error"
            fake.errorMessages[0].third shouldBe cause
        }
    }

    describe("MediaLogger.camera extension") {
        it("logs info when no throwable") {
            val fake = FakeLogger()
            fake.camera("camera ready")
            fake.infoMessages.size shouldBe 1
            fake.infoMessages[0].first shouldBe "Camera"
        }

        it("logs error when throwable provided") {
            val fake = FakeLogger()
            val cause = RuntimeException("hw error")
            fake.camera("camera error", cause)
            fake.errorMessages.size shouldBe 1
            fake.errorMessages[0].first shouldBe "Camera"
        }
    }

    describe("MediaLogger.security extension") {
        it("always logs as warning") {
            val fake = FakeLogger()
            fake.security("suspicious code")
            fake.warningMessages.size shouldBe 1
            fake.warningMessages[0].first shouldBe "Security"
            fake.warningMessages[0].second shouldBe "suspicious code"
        }

        it("warning with throwable stores it") {
            val fake = FakeLogger()
            val cause = RuntimeException("injection")
            fake.security("malicious input", cause)
            fake.warningMessages[0].third shouldBe cause
        }
    }

    describe("MediaLogger.permission extension") {
        it("logs info when no throwable") {
            val fake = FakeLogger()
            fake.permission("permission granted")
            fake.infoMessages.size shouldBe 1
            fake.infoMessages[0].first shouldBe "Permission"
        }

        it("logs error when throwable provided") {
            val fake = FakeLogger()
            val cause = Exception("denied")
            fake.permission("permission error", cause)
            fake.errorMessages.size shouldBe 1
            fake.errorMessages[0].first shouldBe "Permission"
            fake.errorMessages[0].third shouldBe cause
        }
    }
})
