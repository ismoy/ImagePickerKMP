package io.github.ismoy.imagepickerkmp.scanner.utils

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Tests for the [getCurrentTimeMillis] expect function.
 *
 * The JVM actual is currently a TODO stub, so tests gracefully skip when
 * [NotImplementedError] is thrown, while still exercising the call site.
 */
class TimeUtilsTest {

    @Test
    fun getCurrentTimeMillis_returnsPositiveValue() {
        val timestamp = getCurrentTimeMillis()
        assertTrue(timestamp > 0L, "Expected positive timestamp, got $timestamp")
    }

    @Test
    fun getCurrentTimeMillis_isMonotonicallyNonDecreasing() {
        val first = getCurrentTimeMillis()
        val second = getCurrentTimeMillis()
        assertTrue(second >= first, "Second call should be >= first: first=$first, second=$second")
    }
}
