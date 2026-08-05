package io.github.ismoy.imagepickerkmp.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import io.github.ismoy.imagepickerkmp.camera.AndroidPhotoCaptureManager
import io.github.ismoy.imagepickerkmp.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.core.I18nKonfig
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionManager
import io.github.ismoy.imagepickerkmp.picker.PhotoCaptureException
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.TestScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The camera intent is launched from a LaunchedEffect, so anything thrown there reaches the
 * consuming app as an uncaught exception.
 */
class PlatformCameraStateLaunchTest {

    private val intent = mockk<Intent>()
    private val uri = mockk<Uri>()
    private val manager = mockk<AndroidPhotoCaptureManager>(relaxed = true)
    private val permissionManager = mockk<PermissionManager>(relaxed = true)

    private var capturedError: Exception? = null
    private var dismissed = false

    private fun makeState(): PlatformCameraState {
        every { manager.buildCaptureIntent() } returns (intent to uri)
        val config = ImagePickerConfig(
            onPhotoCaptured = {},
            onError = { capturedError = it },
            onDismiss = { dismissed = true }
        )
        return PlatformCameraState(config, permissionManager, manager, TestScope())
    }

    @Test
    fun startCamera_whenIntentResolves_launchesAndReportsNoError() {
        val state = makeState()
        var launched: Intent? = null

        state.startCamera { launched = it }

        assertSame(intent, launched)
        assertNull(capturedError)
    }

    @Test
    fun startCamera_whenNoActivityHandlesIntent_reportsErrorInsteadOfThrowing() {
        val state = makeState()

        state.startCamera {
            throw ActivityNotFoundException("No Activity found to handle Intent")
        }

        val error = capturedError
        assertTrue("expected PhotoCaptureException but was $error", error is PhotoCaptureException)
        assertEquals(I18nKonfig.Errors.camera_unavailable_error, error?.message)
    }

    @Test
    fun startCamera_whenNoActivityHandlesIntent_keepsTheCauseForDiagnostics() {
        val state = makeState()
        val thrown = ActivityNotFoundException("No Activity found to handle Intent")

        state.startCamera { throw thrown }

        assertSame(thrown, capturedError?.cause)
    }

    @Test
    fun startCamera_whenNoActivityHandlesIntent_reportsThenDismisses() {
        val state = makeState()

        state.startCamera { throw ActivityNotFoundException() }

        assertTrue(capturedError is PhotoCaptureException)
        assertTrue(dismissed)
    }

    @Test
    fun startCamera_whenBuildingTheIntentFails_reportsErrorInsteadOfThrowing() {
        val failure = IllegalArgumentException("Failed to find configured root")
        every { manager.buildCaptureIntent() } throws failure
        val config = ImagePickerConfig(
            onPhotoCaptured = {},
            onError = { capturedError = it },
            onDismiss = { dismissed = true }
        )
        val state = PlatformCameraState(config, permissionManager, manager, TestScope())

        state.startCamera { }

        assertSame(failure, capturedError)
        assertTrue(dismissed)
    }
}
