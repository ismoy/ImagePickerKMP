package io.github.ismoy.imagepickerkmp.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import io.github.ismoy.imagepickerkmp.I18nKonfig
import io.github.ismoy.imagepickerkmp.picker.PhotoCaptureException
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The gallery picker is launched from a LaunchedEffect, so a throwing launcher must be reported
 * through the config callbacks rather than propagate.
 */
class GalleryPickerStateLaunchTest {

    private val multipleLauncher =
        mockk<ManagedActivityResultLauncher<Array<String>, List<Uri>>>(relaxed = true)
    private val singleLauncher =
        mockk<ManagedActivityResultLauncher<Array<String>, Uri?>>(relaxed = true)
    private val mimeTypes = arrayOf("image/*")

    private var capturedError: Exception? = null
    private var dismissed = false

    private fun makeState(allowMultiple: Boolean = false): GalleryPickerState {
        val config = GalleryPickerConfig(
            context = mockk<Context>(),
            onPhotosSelected = {},
            onError = { capturedError = it },
            onDismiss = { dismissed = true },
            allowMultiple = allowMultiple,
            mimeTypes = listOf("image/*"),
            cameraCaptureConfig = null
        )
        return GalleryPickerState(config).also { it.setShouldLaunch() }
    }

    @Test
    fun onLaunchEffectHandled_whenLauncherSucceeds_reportsNoError() {
        val state = makeState()

        state.onLaunchEffectHandled(multipleLauncher, singleLauncher, mimeTypes)

        assertNull(capturedError)
        assertFalse(dismissed)
        assertFalse(state.shouldLaunch)
    }

    @Test
    fun onLaunchEffectHandled_whenNothingHandlesTheIntent_reportsThenDismisses() {
        val state = makeState()
        val thrown = ActivityNotFoundException("No Activity found to handle Intent")
        every { singleLauncher.launch(any()) } throws thrown

        state.onLaunchEffectHandled(multipleLauncher, singleLauncher, mimeTypes)

        val error = capturedError
        assertTrue("expected PhotoCaptureException but was $error", error is PhotoCaptureException)
        assertEquals(I18nKonfig.Errors.gallery_unavailable_error, error?.message)
        assertSame(thrown, error?.cause)
        assertTrue(dismissed)
    }

    @Test
    fun onLaunchEffectHandled_whenMultiplePickerThrows_reportsThenDismisses() {
        val state = makeState(allowMultiple = true)
        every { multipleLauncher.launch(any()) } throws ActivityNotFoundException()

        state.onLaunchEffectHandled(multipleLauncher, singleLauncher, mimeTypes)

        assertTrue(capturedError is PhotoCaptureException)
        assertTrue(dismissed)
    }

    @Test
    fun onLaunchEffectHandled_whenLauncherFailsUnexpectedly_reportsThenDismisses() {
        val state = makeState()
        val failure = IllegalStateException("Launcher has been unregistered")
        every { singleLauncher.launch(any()) } throws failure

        state.onLaunchEffectHandled(multipleLauncher, singleLauncher, mimeTypes)

        assertSame(failure, capturedError)
        assertTrue(dismissed)
    }

    /** A throwing launcher must not leave shouldLaunch set, or recomposition retries forever. */
    @Test
    fun onLaunchEffectHandled_whenLauncherThrows_doesNotRelaunch() {
        val state = makeState()
        every { singleLauncher.launch(any()) } throws ActivityNotFoundException()

        state.onLaunchEffectHandled(multipleLauncher, singleLauncher, mimeTypes)

        assertFalse(state.shouldLaunch)
    }
}
