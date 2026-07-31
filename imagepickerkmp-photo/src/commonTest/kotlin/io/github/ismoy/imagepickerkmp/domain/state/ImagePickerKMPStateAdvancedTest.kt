package io.github.ismoy.imagepickerkmp.domain.state

import io.github.ismoy.imagepickerkmp.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.config.CropConfig
import io.github.ismoy.imagepickerkmp.config.GalleryConfig
import io.github.ismoy.imagepickerkmp.picker.ImagePickerKMPConfig
import io.github.ismoy.imagepickerkmp.picker.ImagePickerKMPState
import io.github.ismoy.imagepickerkmp.picker.ImagePickerResult
import io.github.ismoy.imagepickerkmp.picker.MimeType
import io.github.ismoy.imagepickerkmp.picker.PhotoResult
import io.github.ismoy.imagepickerkmp.picker.PickerMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Advanced / edge-case tests for [ImagePickerKMPState] that complement the
 * basic tests in [ImagePickerKMPTest].  Focus:
 *  - Relaunch after terminal states (Dismissed, Error, Success)
 *  - notifyCropPending
 *  - isCropActive flag lifecycle
 *  - reset clears isCropActive
 *  - GalleryConfig fallback for redactGpsData
 */
class ImagePickerKMPStateAdvancedTest {

    private fun makeState(config: ImagePickerKMPConfig = ImagePickerKMPConfig()) =
        ImagePickerKMPState(config)

    // ── Relaunch after Dismissed ──────────────────────────────────────────────

    @Test
    fun launchCamera_afterDismissed_allowsRelaunch() {
        val state = makeState()
        state.launchCamera()
        state.notifyDismiss()                    // terminal: Dismissed
        assertIs<ImagePickerResult.Dismissed>(state.result)

        state.launchCamera()                     // should NOT be ignored
        assertIs<ImagePickerResult.Loading>(state.result)
        assertIs<PickerMode.Camera>(state.activeMode)
    }

    @Test
    fun launchGallery_afterDismissed_allowsRelaunch() {
        val state = makeState()
        state.launchGallery()
        state.notifyDismiss()
        state.launchGallery()
        assertIs<ImagePickerResult.Loading>(state.result)
        assertIs<PickerMode.Gallery>(state.activeMode)
    }

    // ── Relaunch after Error ──────────────────────────────────────────────────

    @Test
    fun launchCamera_afterError_allowsRelaunch() {
        val state = makeState()
        state.launchCamera()
        state.onError(RuntimeException("camera error"))
        assertIs<ImagePickerResult.Error>(state.result)

        state.launchCamera()
        assertIs<ImagePickerResult.Loading>(state.result)
        assertIs<PickerMode.Camera>(state.activeMode)
    }

    @Test
    fun launchGallery_afterError_allowsRelaunch() {
        val state = makeState()
        state.launchGallery()
        state.onError(RuntimeException("gallery error"))
        state.launchGallery()
        assertIs<ImagePickerResult.Loading>(state.result)
        assertIs<PickerMode.Gallery>(state.activeMode)
    }

    // ── Relaunch after Success ────────────────────────────────────────────────

    @Test
    fun launchCamera_afterSuccess_allowsRelaunch() {
        val state = makeState()
        state.launchCamera()
        state.notifySuccess(listOf(PhotoResult(uri = "content://1")))
        assertIs<ImagePickerResult.Success>(state.result)

        state.launchCamera()
        assertIs<ImagePickerResult.Loading>(state.result)
        assertIs<PickerMode.Camera>(state.activeMode)
    }

    @Test
    fun launchGallery_afterSuccess_allowsRelaunch() {
        val state = makeState()
        state.launchGallery()
        state.notifySuccess(listOf(PhotoResult(uri = "content://1")))
        state.launchGallery()
        assertIs<ImagePickerResult.Loading>(state.result)
        assertIs<PickerMode.Gallery>(state.activeMode)
    }

    // ── Blocked while Loading ─────────────────────────────────────────────────

    @Test
    fun launchCamera_whileLoading_isBlocked() {
        val state = makeState()
        state.launchCamera()
        assertIs<ImagePickerResult.Loading>(state.result)
        val firstMode = state.activeMode

        state.launchCamera()   // second call while still Loading → blocked
        assertEquals(firstMode, state.activeMode)
    }

    @Test
    fun launchGallery_whileLoading_isBlocked() {
        val state = makeState()
        state.launchGallery()
        val firstMode = state.activeMode

        state.launchGallery()
        assertEquals(firstMode, state.activeMode)
    }

    // ── notifyCropPending ─────────────────────────────────────────────────────

    @Test
    fun notifyCropPending_setsIsCropActiveTrue() {
        val state = makeState()
        state.notifyCropPending()
        assertTrue(state.isCropActive)
    }

    @Test
    fun notifyCropPending_setsResultToIdle() {
        val state = makeState()
        state.launchCamera()                         // Loading first
        state.notifyCropPending()
        assertIs<ImagePickerResult.Idle>(state.result)
    }

    // ── isCropActive lifecycle ────────────────────────────────────────────────

    @Test
    fun isCropActive_initiallyFalse() {
        assertFalse(makeState().isCropActive)
    }

    @Test
    fun isCropActive_clearedByNotifySuccess() {
        val state = makeState()
        state.notifyCropPending()
        assertTrue(state.isCropActive)
        state.notifySuccess(emptyList())
        assertFalse(state.isCropActive)
    }

    @Test
    fun isCropActive_clearedByNotifyDismiss() {
        val state = makeState()
        state.notifyCropPending()
        state.notifyDismiss()
        assertFalse(state.isCropActive)
    }

    @Test
    fun isCropActive_clearedByOnError() {
        val state = makeState()
        state.notifyCropPending()
        state.onError(RuntimeException("err"))
        assertFalse(state.isCropActive)
    }

    @Test
    fun isCropActive_clearedByReset() {
        val state = makeState()
        state.notifyCropPending()
        state.reset()
        assertFalse(state.isCropActive)
    }

    // ── reset behaviour ───────────────────────────────────────────────────────

    @Test
    fun reset_fromIdle_staysIdle() {
        val state = makeState()
        state.reset()
        assertIs<ImagePickerResult.Idle>(state.result)
        assertIs<PickerMode.None>(state.activeMode)
        assertFalse(state.isCropActive)
    }

    @Test
    fun reset_fromLoading_returnsToIdle() {
        val state = makeState()
        state.launchCamera()
        state.reset()
        assertIs<ImagePickerResult.Idle>(state.result)
        assertIs<PickerMode.None>(state.activeMode)
    }

    @Test
    fun reset_allowsRelaunch() {
        val state = makeState()
        state.launchCamera()
        state.reset()
        state.launchCamera()
        assertIs<ImagePickerResult.Loading>(state.result)
    }

    // ── GalleryConfig.redactGpsData fallback ──────────────────────────────────

    @Test
    fun launchGallery_redactGpsData_defaultIsTrue() {
        val state = makeState()
        state.launchGallery()
        val mode = state.activeMode as PickerMode.Gallery
        assertTrue(mode.redactGpsData)
    }

    @Test
    fun launchGallery_redactGpsData_overrideToFalse() {
        val state = makeState()
        state.launchGallery(redactGpsData = false)
        val mode = state.activeMode as PickerMode.Gallery
        assertFalse(mode.redactGpsData)
    }

    @Test
    fun launchGallery_redactGpsData_galleryConfigFalse_propagates() {
        val state = makeState(
            ImagePickerKMPConfig(galleryConfig = GalleryConfig(redactGpsData = false))
        )
        state.launchGallery()
        val mode = state.activeMode as PickerMode.Gallery
        assertFalse(mode.redactGpsData)
    }

    // ── PickerMode.Gallery mimeTypes from config ──────────────────────────────

    @Test
    fun launchGallery_defaultMimeTypes_areImageAll() {
        val state = makeState()
        state.launchGallery()
        val mode = state.activeMode as PickerMode.Gallery
        assertEquals(listOf(MimeType.IMAGE_ALL), mode.mimeTypes)
    }

    @Test
    fun launchGallery_customMimeTypesViaConfig_propagate() {
        val state = makeState(
            ImagePickerKMPConfig(galleryConfig = GalleryConfig(mimeTypes = listOf(MimeType.IMAGE_JPEG)))
        )
        state.launchGallery()
        val mode = state.activeMode as PickerMode.Gallery
        assertEquals(listOf(MimeType.IMAGE_JPEG), mode.mimeTypes)
    }

    // ── PickerMode.Camera.cameraCaptureConfig override ────────────────────────

    @Test
    fun launchCamera_cameraCaptureConfigOverride_takesEffect() {
        val override = CameraCaptureConfig(includeExif = true)
        val state = makeState()
        state.launchCamera(cameraCaptureConfig = override)
        val mode = state.activeMode as PickerMode.Camera
        assertTrue(mode.cameraCaptureConfig.includeExif)
    }

    // ── notifySuccess with multiple photos ────────────────────────────────────

    @Test
    fun notifySuccess_multiplePhotos_allStoredInResult() {
        val state = makeState()
        state.launchGallery()
        val photos = listOf(
            PhotoResult("content://1"),
            PhotoResult("content://2"),
            PhotoResult("content://3")
        )
        state.notifySuccess(photos)
        val result = state.result as ImagePickerResult.Success
        assertEquals(3, result.photos.size)
        assertEquals("content://2", result.photos[1].uri)
    }

    @Test
    fun notifySuccess_emptyList_successWithEmptyPhotos() {
        val state = makeState()
        state.launchCamera()
        state.notifySuccess(emptyList())
        val result = state.result as ImagePickerResult.Success
        assertTrue(result.photos.isEmpty())
        assertNull(result.first)
    }

    // ── onError stores exception ──────────────────────────────────────────────

    @Test
    fun onError_exceptionMessage_isPreserved() {
        val state = makeState()
        state.launchCamera()
        state.onError(RuntimeException("sensor failure"))
        val result = state.result as ImagePickerResult.Error
        assertEquals("sensor failure", result.exception.message)
    }

    // ── CropConfig disabled vs enabled ────────────────────────────────────────

    @Test
    fun launchCamera_cropDisabled_modeFlagIsFalse() {
        val state = makeState(ImagePickerKMPConfig(cropConfig = CropConfig(enabled = false)))
        state.launchCamera()
        assertFalse((state.activeMode as PickerMode.Camera).enableCrop)
    }

    @Test
    fun launchGallery_cropEnabled_modeFlagIsTrue() {
        val state = makeState(ImagePickerKMPConfig(cropConfig = CropConfig(enabled = true)))
        state.launchGallery()
        assertTrue((state.activeMode as PickerMode.Gallery).enableCrop)
    }
}
