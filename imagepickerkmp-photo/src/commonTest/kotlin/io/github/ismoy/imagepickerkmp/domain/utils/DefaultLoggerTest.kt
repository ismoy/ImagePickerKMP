package io.github.ismoy.imagepickerkmp.domain.utils

import io.github.ismoy.imagepickerkmp.logger.PhotoLogger

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultLoggerTest {

    @BeforeTest
    fun setUp() {
        PhotoLogger.debugMode = true
    }

    @AfterTest
    fun tearDown() {
        PhotoLogger.debugMode = true
    }

    @Test
    fun debugMode_defaultIsTrue() {
        assertTrue(PhotoLogger.debugMode)
    }

    @Test
    fun debug_doesNotThrow() {
        PhotoLogger.debug("debug message")
        assertTrue(true)
    }

    @Test
    fun info_doesNotThrow() {
        PhotoLogger.info("info message")
        assertTrue(true)
    }

    @Test
    fun error_doesNotThrow() {
        PhotoLogger.error("error message")
        assertTrue(true)
    }

    @Test
    fun error_withThrowable_doesNotThrow() {
        PhotoLogger.error("error with cause", RuntimeException("test"))
        assertTrue(true)
    }

    @Test
    fun warning_doesNotThrow() {
        PhotoLogger.warning("warning message")
        assertTrue(true)
    }

    @Test
    fun canToggleDebugMode() {
        PhotoLogger.debugMode = false
        assertFalse(PhotoLogger.debugMode)
        PhotoLogger.debugMode = true
        assertTrue(PhotoLogger.debugMode)
    }

    @Test
    fun debug_emptyMessage_doesNotThrow() {
        PhotoLogger.debug("")
        assertTrue(true)
    }

    @Test
    fun error_nullThrowable_doesNotThrow() {
        PhotoLogger.error("error", null)
        assertTrue(true)
    }

    @Test
    fun debug_longMessage_doesNotThrow() {
        PhotoLogger.debug("A".repeat(10_000))
        assertTrue(true)
    }
}
