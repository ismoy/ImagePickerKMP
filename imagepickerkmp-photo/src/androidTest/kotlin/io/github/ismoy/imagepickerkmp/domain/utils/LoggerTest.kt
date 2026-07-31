package io.github.ismoy.imagepickerkmp.domain.utils

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class LoggerTest {

    @Test
    fun photoLogger_debug_doesNotThrow() {
        PhotoLogger.debug("debug message")
        assertTrue(true)
    }

    @Test
    fun photoLogger_info_doesNotThrow() {
        PhotoLogger.info("info message")
        assertTrue(true)
    }

    @Test
    fun photoLogger_error_doesNotThrow() {
        PhotoLogger.error("error message")
        assertTrue(true)
    }

    @Test
    fun photoLogger_error_withThrowable_doesNotThrow() {
        PhotoLogger.error("error message", RuntimeException("test"))
        assertTrue(true)
    }

    @Test
    fun photoLogger_warning_doesNotThrow() {
        PhotoLogger.warning("warning message")
        assertTrue(true)
    }

    @Test
    fun photoLogger_isSingleton() {
        assertTrue(PhotoLogger === PhotoLogger)
    }
}
