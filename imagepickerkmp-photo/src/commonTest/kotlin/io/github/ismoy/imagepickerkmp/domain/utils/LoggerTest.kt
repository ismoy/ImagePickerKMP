package io.github.ismoy.imagepickerkmp.domain.utils

import io.github.ismoy.imagepickerkmp.logger.PhotoLogger

import kotlin.test.Test
import kotlin.test.assertTrue

class LoggerTest {

    @Test
    fun photoLogger_debug_doesNotThrow() {
        PhotoLogger.debug("test debug")
        assertTrue(true)
    }

    @Test
    fun photoLogger_info_doesNotThrow() {
        PhotoLogger.info("test info")
        assertTrue(true)
    }

    @Test
    fun photoLogger_error_doesNotThrow() {
        PhotoLogger.error("test error")
        assertTrue(true)
    }

    @Test
    fun photoLogger_error_withThrowable_doesNotThrow() {
        PhotoLogger.error("test error", RuntimeException("cause"))
        assertTrue(true)
    }

    @Test
    fun photoLogger_warning_doesNotThrow() {
        PhotoLogger.warning("test warning")
        assertTrue(true)
    }

    @Test
    fun photoLogger_isSingleton() {
        val a = PhotoLogger
        val b = PhotoLogger
        assertTrue(a === b)
    }
}
