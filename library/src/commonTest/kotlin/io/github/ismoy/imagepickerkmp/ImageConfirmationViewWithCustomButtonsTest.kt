package io.github.ismoy.imagepickerkmp

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ImageConfirmationViewWithCustomButtonsTest {
    @Test
    fun testOnConfirmIsCalled() {
        var confirmed = false
        val view = FakeImageConfirmationViewWithCustomButtons(
            onConfirm = { confirmed = true },
            onRetry = { }
        )
        view.simulateConfirm()
        assertTrue(confirmed)
    }

    @Test
    fun testOnRetryIsCalled() {
        var retried = false
        val view = FakeImageConfirmationViewWithCustomButtons(
            onConfirm = { },
            onRetry = { retried = true }
        )
        view.simulateRetry()
        assertTrue(retried)
    }

    @Test
    fun testMultipleConfirmations() {
        var confirmCount = 0
        val view = FakeImageConfirmationViewWithCustomButtons(
            onConfirm = { confirmCount++ },
            onRetry = { }
        )
        view.simulateConfirm()
        view.simulateConfirm()
        assertEquals(2, confirmCount)
    }

    @Test
    fun testConfirmAfterRetry() {
        var retried = false
        var confirmed = false
        val view = FakeImageConfirmationViewWithCustomButtons(
            onConfirm = { confirmed = true },
            onRetry = { retried = true }
        )
        view.simulateRetry()
        view.simulateConfirm()
        assertTrue(retried)
        assertTrue(confirmed)
    }

    @Test
    fun testErrorInConfirmCallback() {
        val view = FakeImageConfirmationViewWithCustomButtons(
            onConfirm = { throw RuntimeException("Error in confirm") },
            onRetry = { }
        )
        val exception = assertFailsWith<RuntimeException> {
            view.simulateConfirm()
        }
        assertEquals("Error in confirm", exception.message)
    }
}

class FakeImageConfirmationViewWithCustomButtons(
    val onConfirm: () -> Unit,
    val onRetry: () -> Unit
) {
    fun simulateConfirm() { onConfirm() }
    fun simulateRetry() { onRetry() }
}
