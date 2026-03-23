package io.github.ismoy.imagepickerkmp.domain.utils

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultLoggerTest {

    @BeforeTest
    fun setUp() {
        DefaultLogger.debugMode = false
    }

    @AfterTest
    fun tearDown() {
        DefaultLogger.debugMode = false
    }

    // ───────────── debugMode off ─────────────

    @Test
    fun debugMode_defaultIsFalse() {
        assertFalse(DefaultLogger.debugMode)
    }

    @Test
    fun log_debugModeOff_doesNotThrow() {
        DefaultLogger.debugMode = false
        // Verification: no exception thrown
        DefaultLogger.log("Should not print")
    }

    @Test
    fun logError_debugModeOff_doesNotThrow() {
        DefaultLogger.debugMode = false
        DefaultLogger.logError("Error message")
    }

    @Test
    fun logError_withThrowable_debugModeOff_doesNotThrow() {
        DefaultLogger.debugMode = false
        DefaultLogger.logError("Error", RuntimeException("test error"))
    }

    @Test
    fun logDebug_debugModeOff_doesNotThrow() {
        DefaultLogger.debugMode = false
        DefaultLogger.logDebug("Debug message")
    }

    // ───────────── debugMode on ─────────────

    @Test
    fun log_debugModeOn_doesNotThrow() {
        DefaultLogger.debugMode = true
        DefaultLogger.log("Active log message")
    }

    @Test
    fun logError_debugModeOn_doesNotThrow() {
        DefaultLogger.debugMode = true
        DefaultLogger.logError("Active error message")
    }

    @Test
    fun logError_debugModeOn_withThrowable_doesNotThrow() {
        DefaultLogger.debugMode = true
        val throwable = IllegalStateException("forced test exception")
        DefaultLogger.logError("Error with cause", throwable)
    }

    @Test
    fun logDebug_debugModeOn_doesNotThrow() {
        DefaultLogger.debugMode = true
        DefaultLogger.logDebug("Active debug message")
    }

    // ───────────── Toggle ─────────────

    @Test
    fun canToggleDebugModeOnAndOff() {
        DefaultLogger.debugMode = true
        assertTrue(DefaultLogger.debugMode)
        DefaultLogger.debugMode = false
        assertFalse(DefaultLogger.debugMode)
    }

    // ───────────── Interface conformance ─────────────

    @Test
    fun defaultLogger_implementsImagePickerLogger() {
        val logger: ImagePickerLogger = DefaultLogger
        logger.log("interface test")
        logger.logError("interface error test")
        logger.logDebug("interface debug test")
    }

    // ───────────── Empty / edge case messages ─────────────

    @Test
    fun log_emptyMessage_doesNotThrow() {
        DefaultLogger.debugMode = true
        DefaultLogger.log("")
    }

    @Test
    fun logError_nullThrowable_doesNotThrow() {
        DefaultLogger.debugMode = true
        DefaultLogger.logError("error", null)
    }

    @Test
    fun log_longMessage_doesNotThrow() {
        DefaultLogger.debugMode = true
        DefaultLogger.log("A".repeat(10_000))
    }
}
