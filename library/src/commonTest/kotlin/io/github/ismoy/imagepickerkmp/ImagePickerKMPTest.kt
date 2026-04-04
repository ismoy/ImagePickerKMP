package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.CropConfig
import io.github.ismoy.imagepickerkmp.domain.config.GalleryConfig
import io.github.ismoy.imagepickerkmp.domain.config.PermissionAndConfirmationConfig
import io.github.ismoy.imagepickerkmp.domain.config.UiConfig
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.features.imagepicker.config.ImagePickerKMPConfig
import io.github.ismoy.imagepickerkmp.features.imagepicker.model.ImagePickerResult
import io.github.ismoy.imagepickerkmp.features.imagepicker.model.PickerMode
import io.github.ismoy.imagepickerkmp.features.imagepicker.state.ImagePickerKMPState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

// ─────────────────────────────────────────────────────────────────────────────
// ImagePickerResult
// ─────────────────────────────────────────────────────────────────────────────

class ImagePickerResultTest {

    // ── Idle ─────────────────────────────────────────────────────────────────

    @Test
    fun idle_isCorrectType() {
        val result: ImagePickerResult = ImagePickerResult.Idle
        assertIs<ImagePickerResult.Idle>(result)
    }

    @Test
    fun idle_equalityWithSelf() {
        assertEquals(ImagePickerResult.Idle, ImagePickerResult.Idle)
    }

    // ── Loading ──────────────────────────────────────────────────────────────

    @Test
    fun loading_isCorrectType() {
        val result: ImagePickerResult = ImagePickerResult.Loading
        assertIs<ImagePickerResult.Loading>(result)
    }

    @Test
    fun loading_equalityWithSelf() {
        assertEquals(ImagePickerResult.Loading, ImagePickerResult.Loading)
    }

    // ── Dismissed ────────────────────────────────────────────────────────────

    @Test
    fun dismissed_isCorrectType() {
        val result: ImagePickerResult = ImagePickerResult.Dismissed
        assertIs<ImagePickerResult.Dismissed>(result)
    }

    @Test
    fun dismissed_equalityWithSelf() {
        assertEquals(ImagePickerResult.Dismissed, ImagePickerResult.Dismissed)
    }

    // ── Error ─────────────────────────────────────────────────────────────────

    @Test
    fun error_storesException() {
        val exception = RuntimeException("Camera unavailable")
        val result = ImagePickerResult.Error(exception)
        assertIs<ImagePickerResult.Error>(result)
        assertEquals("Camera unavailable", result.exception.message)
    }

    @Test
    fun error_equalityBasedOnException() {
        val ex = RuntimeException("fail")
        val a = ImagePickerResult.Error(ex)
        val b = ImagePickerResult.Error(ex)
        assertEquals(a, b)
    }

    @Test
    fun error_differentExceptions_notEqual() {
        val a = ImagePickerResult.Error(RuntimeException("A"))
        val b = ImagePickerResult.Error(RuntimeException("B"))
        // Different instances → not equal (data class default)
        assertTrue(a != b)
    }

    // ── Success ──────────────────────────────────────────────────────────────

    @Test
    fun success_singlePhoto_firstReturnsIt() {
        val photo = PhotoResult(uri = "content://media/1")
        val result = ImagePickerResult.Success(listOf(photo))
        assertIs<ImagePickerResult.Success>(result)
        assertEquals(1, result.photos.size)
        assertNotNull(result.first)
        assertEquals("content://media/1", result.first!!.uri)
    }

    @Test
    fun success_multiplePhotos_firstReturnsFirstElement() {
        val photos = listOf(
            PhotoResult(uri = "content://media/1"),
            PhotoResult(uri = "content://media/2"),
            PhotoResult(uri = "content://media/3")
        )
        val result = ImagePickerResult.Success(photos)
        assertEquals(3, result.photos.size)
        assertEquals("content://media/1", result.first!!.uri)
    }

    @Test
    fun success_emptyList_firstReturnsNull() {
        val result = ImagePickerResult.Success(emptyList())
        assertNull(result.first)
    }

    @Test
    fun success_equality_samePhotos() {
        val photo = PhotoResult(uri = "content://media/1")
        val a = ImagePickerResult.Success(listOf(photo))
        val b = ImagePickerResult.Success(listOf(photo))
        assertEquals(a, b)
    }

    @Test
    fun success_equality_differentPhotos_notEqual() {
        val a = ImagePickerResult.Success(listOf(PhotoResult(uri = "content://1")))
        val b = ImagePickerResult.Success(listOf(PhotoResult(uri = "content://2")))
        assertTrue(a != b)
    }

    // ── Sealed hierarchy exhaustiveness ──────────────────────────────────────

    @Test
    fun whenExpression_coversAllVariants() {
        val variants: List<ImagePickerResult> = listOf(
            ImagePickerResult.Idle,
            ImagePickerResult.Loading,
            ImagePickerResult.Dismissed,
            ImagePickerResult.Success(emptyList()),
            ImagePickerResult.Error(RuntimeException())
        )

        val labels = variants.map { result ->
            when (result) {
                is ImagePickerResult.Idle      -> "idle"
                is ImagePickerResult.Loading   -> "loading"
                is ImagePickerResult.Dismissed -> "dismissed"
                is ImagePickerResult.Success   -> "success"
                is ImagePickerResult.Error     -> "error"
            }
        }

        assertEquals(listOf("idle", "loading", "dismissed", "success", "error"), labels)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ImagePickerKMPConfig
// ─────────────────────────────────────────────────────────────────────────────

class ImagePickerKMPConfigTest {

    @Test
    fun defaultConfig_cropConfigDisabledByDefault() {
        val config = ImagePickerKMPConfig()
        assertFalse(config.cropConfig.enabled)
    }

    @Test
    fun defaultConfig_galleryConfigHasDefaults() {
        val config = ImagePickerKMPConfig()
        assertFalse(config.galleryConfig.allowMultiple)
        assertEquals(30, config.galleryConfig.selectionLimit)
        assertFalse(config.galleryConfig.includeExif)
        assertNull(config.galleryConfig.mimeTypeMismatchMessage)
    }

    @Test
    fun defaultConfig_cropConfigHasDefaults() {
        val config = ImagePickerKMPConfig()
        assertFalse(config.cropConfig.enabled)
        assertFalse(config.cropConfig.aspectRatioLocked)
        assertTrue(config.cropConfig.circularCrop)
        assertTrue(config.cropConfig.squareCrop)
        assertFalse(config.cropConfig.freeformCrop)
    }

    @Test
    fun customGalleryConfig_isApplied() {
        val galleryConfig = GalleryConfig(
            allowMultiple = true,
            selectionLimit = 10,
            includeExif = true
        )
        val config = ImagePickerKMPConfig(galleryConfig = galleryConfig)
        assertTrue(config.galleryConfig.allowMultiple)
        assertEquals(10, config.galleryConfig.selectionLimit)
        assertTrue(config.galleryConfig.includeExif)
    }

    @Test
    fun cropConfig_enabled_true_isStored() {
        val config = ImagePickerKMPConfig(cropConfig = CropConfig(enabled = true))
        assertTrue(config.cropConfig.enabled)
    }

    @Test
    fun customCropConfig_isApplied() {
        val cropConfig = CropConfig(
            enabled = true,
            freeformCrop = true,
            circularCrop = false
        )
        val config = ImagePickerKMPConfig(cropConfig = cropConfig)
        assertTrue(config.cropConfig.enabled)
        assertTrue(config.cropConfig.freeformCrop)
        assertFalse(config.cropConfig.circularCrop)
    }

    @Test
    fun dataClass_equality_sameValues() {
        val a = ImagePickerKMPConfig(cropConfig = CropConfig(enabled = true))
        val b = ImagePickerKMPConfig(cropConfig = CropConfig(enabled = true))
        assertEquals(a, b)
    }

    @Test
    fun dataClass_equality_differentValues_notEqual() {
        val a = ImagePickerKMPConfig(cropConfig = CropConfig(enabled = false))
        val b = ImagePickerKMPConfig(cropConfig = CropConfig(enabled = true))
        assertTrue(a != b)
    }

    @Test
    fun copy_changesOnlySpecifiedField() {
        val original = ImagePickerKMPConfig(cropConfig = CropConfig(enabled = false))
        val modified = original.copy(cropConfig = CropConfig(enabled = true))
        assertFalse(original.cropConfig.enabled)
        assertTrue(modified.cropConfig.enabled)
        assertEquals(original.galleryConfig, modified.galleryConfig)
        assertEquals(original.cameraCaptureConfig, modified.cameraCaptureConfig)
    }

    @Test
    fun customCameraCaptureConfig_isApplied() {
        val cameraConfig = CameraCaptureConfig()
        val config = ImagePickerKMPConfig(cameraCaptureConfig = cameraConfig)
        assertEquals(cameraConfig, config.cameraCaptureConfig)
    }

    @Test
    fun defaultConfig_uiConfigIsDefault() {
        val config = ImagePickerKMPConfig()
        assertEquals(UiConfig(), config.uiConfig)
    }

    @Test
    fun customUiConfig_isApplied() {
        val ui = UiConfig()
        val config = ImagePickerKMPConfig(uiConfig = ui)
        assertEquals(ui, config.uiConfig)
    }

    @Test
    fun defaultConfig_permissionConfigIsDefault() {
        val config = ImagePickerKMPConfig()
        assertEquals(PermissionAndConfirmationConfig(), config.permissionAndConfirmationConfig)
    }

    @Test
    fun customPermissionConfig_skipConfirmation_isApplied() {
        val permConfig = PermissionAndConfirmationConfig(skipConfirmation = true)
        val config = ImagePickerKMPConfig(permissionAndConfirmationConfig = permConfig)
        assertTrue(config.permissionAndConfirmationConfig.skipConfirmation)
    }

    @Test
    fun customPermissionConfig_customDeniedDialog_isStored() {
        val customDialog: @Composable (onRetry: () -> Unit, onDismiss: () -> Unit) -> Unit = { _, _ -> }
        val permConfig = PermissionAndConfirmationConfig(customDeniedDialog = customDialog)
        val config = ImagePickerKMPConfig(permissionAndConfirmationConfig = permConfig)
        assertNotNull(config.permissionAndConfirmationConfig.customDeniedDialog)
    }

    @Test
    fun customPermissionConfig_customSettingsDialog_isStored() {
        val customDialog: @Composable (onOpenSettings: () -> Unit, onDismiss: () -> Unit) -> Unit = { _, _ -> }
        val permConfig = PermissionAndConfirmationConfig(customSettingsDialog = customDialog)
        val config = ImagePickerKMPConfig(permissionAndConfirmationConfig = permConfig)
        assertNotNull(config.permissionAndConfirmationConfig.customSettingsDialog)
    }

    @Test
    fun customPermissionConfig_customConfirmationView_isStored() {
        val customView: @Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit =
            { _, _, _ -> }
        val permConfig = PermissionAndConfirmationConfig(customConfirmationView = customView)
        val config = ImagePickerKMPConfig(permissionAndConfirmationConfig = permConfig)
        assertNotNull(config.permissionAndConfirmationConfig.customConfirmationView)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ImagePickerKMPState — launchCamera
// ─────────────────────────────────────────────────────────────────────────────

class ImagePickerKMPStateLaunchCameraTest {

    private fun makeState(config: ImagePickerKMPConfig = ImagePickerKMPConfig()) =
        ImagePickerKMPState(config)

    @Test
    fun launchCamera_defaultParams_setsLoadingResult() {
        val state = makeState()
        state.launchCamera()
        assertIs<ImagePickerResult.Loading>(state.result)
    }

    @Test
    fun launchCamera_defaultParams_setsCameraMode() {
        val state = makeState()
        state.launchCamera()
        assertIs<PickerMode.Camera>(state.activeMode)
    }

    @Test
    fun launchCamera_usesCameraConfigFromGlobalConfig() {
        val customConfig = CameraCaptureConfig()
        val state = makeState(ImagePickerKMPConfig(cameraCaptureConfig = customConfig))
        state.launchCamera()
        val mode = state.activeMode as PickerMode.Camera
        assertEquals(customConfig, mode.cameraCaptureConfig)
    }

    @Test
    fun launchCamera_overrideCameraConfig_usesOverride() {
        val override = CameraCaptureConfig()
        val state = makeState()
        state.launchCamera(cameraCaptureConfig = override)
        val mode = state.activeMode as PickerMode.Camera
        assertEquals(override, mode.cameraCaptureConfig)
    }

    @Test
    fun launchCamera_cropConfig_enabled_activatesCrop() {
        val state = makeState(ImagePickerKMPConfig(cropConfig = CropConfig(enabled = true)))
        state.launchCamera()
        val mode = state.activeMode as PickerMode.Camera
        assertTrue(mode.enableCrop)
    }

    @Test
    fun launchCamera_cropConfig_disabled_noCrop() {
        val state = makeState(ImagePickerKMPConfig(cropConfig = CropConfig(enabled = false)))
        state.launchCamera()
        val mode = state.activeMode as PickerMode.Camera
        assertFalse(mode.enableCrop)
    }

    @Test
    fun launchCamera_customOnDismiss_storedInMode() {
        var called = false
        val state = makeState()
        state.launchCamera(onDismiss = { called = true })
        val mode = state.activeMode as PickerMode.Camera
        assertNotNull(mode.onDismiss)
        mode.onDismiss?.invoke()
        assertTrue(called)
    }

    @Test
    fun launchCamera_nullOnDismiss_modeHasNullDismiss() {
        val state = makeState()
        state.launchCamera(onDismiss = null)
        val mode = state.activeMode as PickerMode.Camera
        assertNull(mode.onDismiss)
    }

    @Test
    fun launchCamera_customOnError_storedInMode() {
        var caught: Exception? = null
        val state = makeState()
        state.launchCamera(onError = { caught = it })
        val mode = state.activeMode as PickerMode.Camera
        assertNotNull(mode.onError)
        mode.onError?.invoke(RuntimeException("cam-err"))
        assertEquals("cam-err", caught?.message)
    }

    @Test
    fun launchCamera_nullOnError_modeHasNullError() {
        val state = makeState()
        state.launchCamera(onError = null)
        val mode = state.activeMode as PickerMode.Camera
        assertNull(mode.onError)
    }

    @Test
    fun launchCamera_whileAlreadyActive_isIgnored() {
        val state = makeState()
        state.launchCamera()
        val firstMode = state.activeMode
        state.launchCamera() // segundo tap — debe ignorarse
        assertEquals(firstMode, state.activeMode)
        assertIs<ImagePickerResult.Loading>(state.result)
    }

    @Test
    fun launchCamera_whileGalleryActive_isIgnored() {
        val state = makeState()
        state.launchGallery()
        val galleryMode = state.activeMode
        state.launchCamera() // no debe sobreescribir
        assertEquals(galleryMode, state.activeMode)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ImagePickerKMPState — launchGallery
// ─────────────────────────────────────────────────────────────────────────────

class ImagePickerKMPStateLaunchGalleryTest {

    private fun makeState(config: ImagePickerKMPConfig = ImagePickerKMPConfig()) =
        ImagePickerKMPState(config)

    @Test
    fun launchGallery_defaultParams_setsLoadingResult() {
        val state = makeState()
        state.launchGallery()
        assertIs<ImagePickerResult.Loading>(state.result)
    }

    @Test
    fun launchGallery_defaultParams_setsGalleryMode() {
        val state = makeState()
        state.launchGallery()
        assertIs<PickerMode.Gallery>(state.activeMode)
    }

    @Test
    fun launchGallery_allowMultiple_overrideApplied() {
        val state = makeState()
        state.launchGallery(allowMultiple = true)
        val mode = state.activeMode as PickerMode.Gallery
        assertTrue(mode.allowMultiple)
    }

    @Test
    fun launchGallery_allowMultiple_fallsToGalleryConfig() {
        val state = makeState(ImagePickerKMPConfig(galleryConfig = GalleryConfig(allowMultiple = true)))
        state.launchGallery()
        val mode = state.activeMode as PickerMode.Gallery
        assertTrue(mode.allowMultiple)
    }

    @Test
    fun launchGallery_mimeTypes_overrideApplied() {
        val state = makeState()
        state.launchGallery(mimeTypes = listOf(MimeType.IMAGE_PNG))
        val mode = state.activeMode as PickerMode.Gallery
        assertEquals(listOf(MimeType.IMAGE_PNG), mode.mimeTypes)
    }

    @Test
    fun launchGallery_selectionLimit_overrideApplied() {
        val state = makeState()
        state.launchGallery(selectionLimit = 5)
        val mode = state.activeMode as PickerMode.Gallery
        assertEquals(5L, mode.selectionLimit)
    }

    @Test
    fun launchGallery_selectionLimit_fallsToGalleryConfig() {
        val state = makeState(ImagePickerKMPConfig(galleryConfig = GalleryConfig(selectionLimit = 20)))
        state.launchGallery()
        val mode = state.activeMode as PickerMode.Gallery
        assertEquals(20L, mode.selectionLimit)
    }

    @Test
    fun launchGallery_cropConfig_enabled_activatesCrop() {
        val state = makeState(ImagePickerKMPConfig(cropConfig = CropConfig(enabled = true)))
        state.launchGallery()
        val mode = state.activeMode as PickerMode.Gallery
        assertTrue(mode.enableCrop)
    }

    @Test
    fun launchGallery_cropConfig_disabled_noCrop() {
        val state = makeState(ImagePickerKMPConfig(cropConfig = CropConfig(enabled = false)))
        state.launchGallery()
        val mode = state.activeMode as PickerMode.Gallery
        assertFalse(mode.enableCrop)
    }

    @Test
    fun launchGallery_includeExif_overrideApplied() {
        val state = makeState()
        state.launchGallery(includeExif = true)
        val mode = state.activeMode as PickerMode.Gallery
        assertTrue(mode.includeExif)
    }

    @Test
    fun launchGallery_includeExif_fallsToGalleryConfig() {
        val state = makeState(ImagePickerKMPConfig(galleryConfig = GalleryConfig(includeExif = true)))
        state.launchGallery()
        val mode = state.activeMode as PickerMode.Gallery
        assertTrue(mode.includeExif)
    }

    @Test
    fun launchGallery_mimeTypeMismatchMessage_stored() {
        val state = makeState()
        state.launchGallery(mimeTypeMismatchMessage = "Only PNG allowed")
        val mode = state.activeMode as PickerMode.Gallery
        assertEquals("Only PNG allowed", mode.mimeTypeMismatchMessage)
    }

    @Test
    fun launchGallery_mimeTypeMismatchMessage_nullByDefault() {
        val state = makeState()
        state.launchGallery()
        val mode = state.activeMode as PickerMode.Gallery
        assertNull(mode.mimeTypeMismatchMessage)
    }

    @Test
    fun launchGallery_mimeTypeMismatchMessage_fallsToGalleryConfig() {
        val state = makeState(
            ImagePickerKMPConfig(
                galleryConfig = GalleryConfig(mimeTypeMismatchMessage = "Solo imágenes PNG")
            )
        )
        state.launchGallery()
        val mode = state.activeMode as PickerMode.Gallery
        assertEquals("Solo imágenes PNG", mode.mimeTypeMismatchMessage)
    }

    @Test
    fun launchGallery_mimeTypeMismatchMessage_overrideWinsOverGalleryConfig() {
        val state = makeState(
            ImagePickerKMPConfig(
                galleryConfig = GalleryConfig(mimeTypeMismatchMessage = "Global message")
            )
        )
        state.launchGallery(mimeTypeMismatchMessage = "Override message")
        val mode = state.activeMode as PickerMode.Gallery
        assertEquals("Override message", mode.mimeTypeMismatchMessage)
    }

    @Test
    fun launchGallery_cameraCaptureConfig_stored() {
        val camConfig = CameraCaptureConfig()
        val state = makeState()
        state.launchGallery(cameraCaptureConfig = camConfig)
        val mode = state.activeMode as PickerMode.Gallery
        assertEquals(camConfig, mode.cameraCaptureConfig)
    }

    @Test
    fun launchGallery_cameraCaptureConfig_nullByDefault() {
        val state = makeState()
        state.launchGallery()
        val mode = state.activeMode as PickerMode.Gallery
        assertNull(mode.cameraCaptureConfig)
    }

    @Test
    fun launchGallery_customOnDismiss_storedInMode() {
        var called = false
        val state = makeState()
        state.launchGallery(onDismiss = { called = true })
        val mode = state.activeMode as PickerMode.Gallery
        assertNotNull(mode.onDismiss)
        mode.onDismiss.invoke()
        assertTrue(called)
    }

    @Test
    fun launchGallery_nullOnDismiss_modeHasNullDismiss() {
        val state = makeState()
        state.launchGallery(onDismiss = null)
        val mode = state.activeMode as PickerMode.Gallery
        assertNull(mode.onDismiss)
    }

    @Test
    fun launchGallery_customOnError_storedInMode() {
        var caught: Exception? = null
        val state = makeState()
        state.launchGallery(onError = { caught = it })
        val mode = state.activeMode as PickerMode.Gallery
        assertNotNull(mode.onError)
        mode.onError.invoke(RuntimeException("gallery-err"))
        assertEquals("gallery-err", caught?.message)
    }

    @Test
    fun launchGallery_nullOnError_modeHasNullError() {
        val state = makeState()
        state.launchGallery(onError = null)
        val mode = state.activeMode as PickerMode.Gallery
        assertNull(mode.onError)
    }

    @Test
    fun launchGallery_whileAlreadyActive_isIgnored() {
        val state = makeState()
        state.launchGallery()
        val firstMode = state.activeMode
        state.launchGallery() // segundo tap — debe ignorarse
        assertEquals(firstMode, state.activeMode)
        assertIs<ImagePickerResult.Loading>(state.result)
    }

    @Test
    fun launchGallery_whileCameraActive_isIgnored() {
        val state = makeState()
        state.launchCamera()
        val cameraMode = state.activeMode
        state.launchGallery() // no debe sobreescribir
        assertEquals(cameraMode, state.activeMode)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ImagePickerKMPState — reset / internal callbacks
// ─────────────────────────────────────────────────────────────────────────────

class ImagePickerKMPStateCallbacksTest {

    private fun makeState() = ImagePickerKMPState(ImagePickerKMPConfig())

    @Test
    fun initialResult_isIdle() {
        assertIs<ImagePickerResult.Idle>(makeState().result)
    }

    @Test
    fun initialActiveMode_isNone() {
        assertIs<PickerMode.None>(makeState().activeMode)
    }

    @Test
    fun reset_afterLaunchCamera_returnsToIdle() {
        val state = makeState()
        state.launchCamera()
        state.reset()
        assertIs<ImagePickerResult.Idle>(state.result)
        assertIs<PickerMode.None>(state.activeMode)
    }

    @Test
    fun reset_afterLaunchGallery_returnsToIdle() {
        val state = makeState()
        state.launchGallery()
        state.reset()
        assertIs<ImagePickerResult.Idle>(state.result)
        assertIs<PickerMode.None>(state.activeMode)
    }

    @Test
    fun onSuccess_setsSuccessResultAndClearsMode() {
        val state = makeState()
        val photo = PhotoResult(uri = "content://1")
        state.launchCamera()
        state.notifySuccess(listOf(photo))
        val result = state.result as ImagePickerResult.Success
        assertEquals(1, result.photos.size)
        assertIs<PickerMode.None>(state.activeMode)
    }

    @Test
    fun onDismiss_setsDismissedResultAndClearsMode() {
        val state = makeState()
        state.launchGallery()
        state.notifyDismiss()
        assertIs<ImagePickerResult.Dismissed>(state.result)
        assertIs<PickerMode.None>(state.activeMode)
    }

    @Test
    fun onError_setsErrorResultAndClearsMode() {
        val state = makeState()
        state.launchCamera()
        state.onError(RuntimeException("test"))
        val result = state.result as ImagePickerResult.Error
        assertEquals("test", result.exception.message)
        assertIs<ImagePickerResult.Error>(state.result)
    }
}
