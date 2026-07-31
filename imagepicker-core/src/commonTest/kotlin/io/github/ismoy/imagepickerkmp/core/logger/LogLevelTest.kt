package io.github.ismoy.imagepickerkmp.core.logger

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldHaveSize

class LogLevelTest : DescribeSpec({

    describe("LogLevel priorities") {
        it("DEBUG has priority 0") { LogLevel.DEBUG.priority shouldBe 0 }
        it("INFO has priority 1")  { LogLevel.INFO.priority shouldBe 1 }
        it("WARNING has priority 2") { LogLevel.WARNING.priority shouldBe 2 }
        it("ERROR has priority 3") { LogLevel.ERROR.priority shouldBe 3 }
    }

    describe("LogLevel ordering") {
        it("DEBUG < INFO") {
            (LogLevel.DEBUG.priority < LogLevel.INFO.priority) shouldBe true
        }
        it("INFO < WARNING") {
            (LogLevel.INFO.priority < LogLevel.WARNING.priority) shouldBe true
        }
        it("WARNING < ERROR") {
            (LogLevel.WARNING.priority < LogLevel.ERROR.priority) shouldBe true
        }
    }

    describe("LogLevel entries") {
        it("has exactly 4 entries") { LogLevel.entries shouldHaveSize 4 }
        it("valueOf DEBUG works") { LogLevel.valueOf("DEBUG") shouldBe LogLevel.DEBUG }
        it("valueOf ERROR works") { LogLevel.valueOf("ERROR") shouldBe LogLevel.ERROR }
        it("all entries have unique priorities") {
            val priorities = LogLevel.entries.map { it.priority }
            priorities.toSet().size shouldBe priorities.size
        }
    }

    describe("LogLevel name and ordinal") {
        it("DEBUG name is DEBUG") { LogLevel.DEBUG.name shouldBe "DEBUG" }
        it("ERROR name is ERROR") { LogLevel.ERROR.name shouldBe "ERROR" }
        it("DEBUG is first entry") { LogLevel.DEBUG.ordinal shouldBe 0 }
        it("ERROR is last entry") {
            LogLevel.ERROR.ordinal shouldBe LogLevel.entries.size - 1
        }
    }

    describe("LogLevel exhaustive when") {
        it("covers all 4 variants") {
            LogLevel.entries.forEach { level ->
                val label = when (level) {
                    LogLevel.DEBUG   -> "debug"
                    LogLevel.INFO    -> "info"
                    LogLevel.WARNING -> "warning"
                    LogLevel.ERROR   -> "error"
                }
                label.isNotEmpty() shouldBe true
            }
        }
    }
})
