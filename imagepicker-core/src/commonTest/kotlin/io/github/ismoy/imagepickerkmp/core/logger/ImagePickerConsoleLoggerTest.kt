package io.github.ismoy.imagepickerkmp.core.logger

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ImagePickerConsoleLoggerTest : DescribeSpec({

    fun makeLogger() = ImagePickerConsoleLogger()

    describe("default minimumLevel") {
        it("is DEBUG") {
            makeLogger().minimumLevel shouldBe LogLevel.DEBUG
        }
    }

    describe("minimumLevel filtering — all methods at DEBUG threshold") {
        it("debug() does not throw when level is DEBUG") {
            val logger = makeLogger()
            logger.minimumLevel = LogLevel.DEBUG
            logger.debug("TAG", "debug message")   // should not throw
        }
        it("info() does not throw when level is DEBUG") {
            makeLogger().info("TAG", "info message")
        }
        it("warning() without throwable does not throw") {
            makeLogger().warning("TAG", "warn message")
        }
        it("warning() with throwable does not throw") {
            makeLogger().warning("TAG", "warn", RuntimeException("cause"))
        }
        it("error() without throwable does not throw") {
            makeLogger().error("TAG", "error message")
        }
        it("error() with throwable does not throw") {
            makeLogger().error("TAG", "error", RuntimeException("cause"))
        }
    }

    describe("minimumLevel filtering — suppression") {
        it("debug() is suppressed when minimumLevel is INFO") {
            val logger = makeLogger()
            logger.minimumLevel = LogLevel.INFO
            logger.debug("TAG", "suppressed debug") // should not throw, simply skipped
        }
        it("info() is suppressed when minimumLevel is WARNING") {
            val logger = makeLogger()
            logger.minimumLevel = LogLevel.WARNING
            logger.info("TAG", "suppressed info")
        }
        it("warning() is suppressed when minimumLevel is ERROR") {
            val logger = makeLogger()
            logger.minimumLevel = LogLevel.ERROR
            logger.warning("TAG", "suppressed warning")
        }
        it("error() always fires even at ERROR threshold") {
            val logger = makeLogger()
            logger.minimumLevel = LogLevel.ERROR
            logger.error("TAG", "this fires") // must not throw
        }
    }

    describe("minimumLevel can be changed at runtime") {
        it("level changes from DEBUG to ERROR and back") {
            val logger = makeLogger()
            logger.minimumLevel = LogLevel.ERROR
            logger.minimumLevel shouldBe LogLevel.ERROR
            logger.minimumLevel = LogLevel.DEBUG
            logger.minimumLevel shouldBe LogLevel.DEBUG
        }
    }

    describe("implements MediaLogger interface") {
        it("is a MediaLogger") {
            val logger: MediaLogger = makeLogger()
            logger.debug("TAG", "msg")
            logger.info("TAG", "msg")
            logger.warning("TAG", "msg")
            logger.error("TAG", "msg")
        }
    }

    describe("enabled() logic via minimumLevel.priority") {
        it("level DEBUG passes when minimumLevel is DEBUG (0 <= 0)") {
            val logger = makeLogger()
            logger.minimumLevel = LogLevel.DEBUG
            // debug internally calls enabled(DEBUG): 0 <= 0 = true → prints
            logger.debug("T", "x")  // no throw = passes
        }
        it("level WARNING is allowed when minimumLevel is WARNING (2 <= 2)") {
            val logger = makeLogger()
            logger.minimumLevel = LogLevel.WARNING
            logger.warning("T", "x")
        }
        it("level INFO is blocked when minimumLevel is WARNING (1 < 2)") {
            val logger = makeLogger()
            logger.minimumLevel = LogLevel.WARNING
            logger.info("T", "suppressed") // no output, no throw
        }
    }
})
