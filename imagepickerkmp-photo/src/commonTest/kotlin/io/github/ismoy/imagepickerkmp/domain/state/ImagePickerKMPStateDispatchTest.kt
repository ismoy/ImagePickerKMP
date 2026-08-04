package io.github.ismoy.imagepickerkmp.domain.state

import io.github.ismoy.imagepickerkmp.picker.ImagePickerKMPConfig
import io.github.ismoy.imagepickerkmp.picker.ImagePickerKMPState
import io.github.ismoy.imagepickerkmp.picker.ImagePickerResult
import io.github.ismoy.imagepickerkmp.picker.PickerMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Terminal-outcome dispatch shared by every platform renderer: activeMode must settle even when
 * the caller supplied its own callback.
 */
class ImagePickerKMPStateDispatchTest {

    private fun makeState(config: ImagePickerKMPConfig = ImagePickerKMPConfig()) =
        ImagePickerKMPState(config)

    // ── dispatchError without a caller-supplied callback ──────────────────────

    @Test
    fun dispatchError_withDefaultCallback_setsErrorAndClearsMode() {
        val state = makeState()
        val failure = RuntimeException("no camera app")

        state.launchCamera()
        state.dispatchError(failure)

        val result = assertIs<ImagePickerResult.Error>(state.result)
        assertSame(failure, result.exception)
        assertEquals(PickerMode.None, state.activeMode)
    }

    // ── dispatchError with a caller-supplied callback ─────────────────────────

    @Test
    fun dispatchError_withConsumerCallback_stillClearsMode() {
        val state = makeState()
        var received: Exception? = null
        val failure = RuntimeException("no camera app")

        state.launchCamera(onError = { received = it })
        state.dispatchError(failure)

        assertSame(failure, received)
        assertSame(failure, assertIs<ImagePickerResult.Error>(state.result).exception)
        assertEquals(PickerMode.None, state.activeMode)
    }

    @Test
    fun launchCamera_afterErrorWithConsumerCallback_relaunches() {
        val state = makeState()

        state.launchCamera(onError = {})
        state.dispatchError(RuntimeException("no camera app"))

        state.launchCamera(onError = {})

        assertIs<ImagePickerResult.Loading>(state.result)
        assertIs<PickerMode.Camera>(state.activeMode)
    }

    @Test
    fun dispatchError_onGalleryWithConsumerCallback_stillClearsMode() {
        val state = makeState()
        var received: Exception? = null
        val failure = RuntimeException("no gallery app")

        state.launchGallery(onError = { received = it })
        state.dispatchError(failure)

        assertSame(failure, received)
        assertEquals(PickerMode.None, state.activeMode)

        state.launchGallery(onError = {})
        assertIs<PickerMode.Gallery>(state.activeMode)
    }

    // ── dispatchDismiss ───────────────────────────────────────────────────────

    @Test
    fun dispatchDismiss_withConsumerCallback_stillClearsMode() {
        val state = makeState()
        var dismissed = false

        state.launchCamera(onDismiss = { dismissed = true })
        state.dispatchDismiss()

        assertTrue(dismissed)
        assertIs<ImagePickerResult.Dismissed>(state.result)
        assertEquals(PickerMode.None, state.activeMode)
    }

    @Test
    fun dispatchDismiss_onGalleryWithConsumerCallback_stillClearsMode() {
        val state = makeState()
        var dismissed = false

        state.launchGallery(onDismiss = { dismissed = true })
        state.dispatchDismiss()

        assertTrue(dismissed)
        assertIs<ImagePickerResult.Dismissed>(state.result)
        assertEquals(PickerMode.None, state.activeMode)
    }

    @Test
    fun launchCamera_afterDismissWithConsumerCallback_relaunches() {
        val state = makeState()

        state.launchCamera(onDismiss = {})
        state.dispatchDismiss()

        state.launchCamera(onDismiss = {})

        assertIs<ImagePickerResult.Loading>(state.result)
        assertIs<PickerMode.Camera>(state.activeMode)
    }

    // ── error followed by dismiss (the camera-unavailable sequence) ───────────

    /** The dismiss must not overwrite the Error, or "no camera app" looks like "user cancelled". */
    @Test
    fun dispatchError_thenDispatchDismiss_keepsTheError() {
        val state = makeState()
        val failure = RuntimeException("no camera app")

        state.launchCamera()
        state.dispatchError(failure)
        state.dispatchDismiss()

        assertSame(failure, assertIs<ImagePickerResult.Error>(state.result).exception)
        assertEquals(PickerMode.None, state.activeMode)
    }

    @Test
    fun dispatchError_thenDispatchDismiss_invokesBothConsumerCallbacks() {
        val state = makeState()
        var received: Exception? = null
        var dismissed = false
        val failure = RuntimeException("no camera app")

        state.launchCamera(onDismiss = { dismissed = true }, onError = { received = it })
        state.dispatchError(failure)
        state.dispatchDismiss()

        assertSame(failure, received)
        assertTrue(dismissed)
        assertIs<ImagePickerResult.Error>(state.result)
    }

    @Test
    fun launchCamera_afterErrorThenDismiss_relaunches() {
        val state = makeState()

        state.launchCamera()
        state.dispatchError(RuntimeException("no camera app"))
        state.dispatchDismiss()

        state.launchCamera()

        assertIs<ImagePickerResult.Loading>(state.result)
        assertIs<PickerMode.Camera>(state.activeMode)
    }

    /** A plain cancellation after a fresh launch must still report Dismissed. */
    @Test
    fun dispatchDismiss_afterRelaunch_reportsDismissedNotTheStaleError() {
        val state = makeState()

        state.launchCamera()
        state.dispatchError(RuntimeException("no camera app"))

        state.launchCamera()
        state.dispatchDismiss()

        assertIs<ImagePickerResult.Dismissed>(state.result)
    }

    @Test
    fun dispatchDismiss_whenIdle_isSafeAndClearsMode() {
        val state = makeState()

        state.dispatchDismiss()

        assertIs<ImagePickerResult.Dismissed>(state.result)
        assertEquals(PickerMode.None, state.activeMode)
    }

    // ── latched callbacks don't leak across launches ──────────────────────────

    @Test
    fun dispatchError_afterRelaunchWithoutCallback_doesNotCallThePreviousOne() {
        val state = makeState()
        var staleCallbackCount = 0

        state.launchCamera(onError = { staleCallbackCount++ })
        state.dispatchError(RuntimeException("first"))
        assertEquals(1, staleCallbackCount)

        state.launchCamera()
        state.dispatchError(RuntimeException("second"))

        assertEquals(1, staleCallbackCount)
    }

    @Test
    fun dispatchError_afterReset_doesNotCallThePreviousCallback() {
        val state = makeState()
        var called = false

        state.launchCamera(onError = { called = true })
        state.reset()
        state.dispatchError(RuntimeException("boom"))

        assertFalse(called)
    }
}
