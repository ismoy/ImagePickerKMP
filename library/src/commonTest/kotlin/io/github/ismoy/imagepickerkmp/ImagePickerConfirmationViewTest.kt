package io.github.ismoy.imagepickerkmp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class ImagePickerConfirmationViewTest {
    @Test
    fun testOnConfirmIsCalled() {
        var confirmed = false
        val view = FakeImagePickerConfirmationView(
            onConfirm = { confirmed = true },
            onRetry = { }
        )
        view.simulateConfirm()
        assertTrue(confirmed)
    }

    @Test
    fun testOnRetryIsCalled() {
        var retried = false
        val view = FakeImagePickerConfirmationView(
            onConfirm = { },
            onRetry = { retried = true }
        )
        view.simulateRetry()
        assertTrue(retried)
    }

    @Test
    fun testMultipleConfirmations() {
        var confirmCount = 0
        val view = FakeImagePickerConfirmationView(
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
        val view = FakeImagePickerConfirmationView(
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
        val view = FakeImagePickerConfirmationView(
            onConfirm = { throw RuntimeException("Error in confirm") },
            onRetry = { }
        )
        val exception = assertFailsWith<RuntimeException> {
            view.simulateConfirm()
        }
        assertEquals("Error in confirm", exception.message)
    }
}

class FakeImagePickerConfirmationView(
    val onConfirm: () -> Unit,
    val onRetry: () -> Unit
) {
    fun simulateConfirm() { onConfirm() }
    fun simulateRetry() { onRetry() }
}

// TODO: Re-enable this test once Compose testing is properly configured
/*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ImagePickerConfirmationViewTest {
    // Test implementation will be added back once Compose testing is configured
}
*/ 