package io.github.ismoy.imagepickerkmp.domain.config

import io.github.ismoy.imagepickerkmp.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.config.CropConfig
import io.github.ismoy.imagepickerkmp.config.GalleryConfig
import io.github.ismoy.imagepickerkmp.config.PermissionAndConfirmationConfig
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.ImagePickerKMPConfig
import io.github.ismoy.imagepickerkmp.picker.MimeType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ImagePickerConfigAdvancedTest {

    // ── GalleryConfig defaults ────────────────────────────────────────────────

    @Test
    fun galleryConfig_defaultAllowMultiple_isFalse() {
        assertFalse(GalleryConfig().allowMultiple)
    }

    @Test
    fun galleryConfig_defaultMimeTypes_isImageAll() {
        assertEquals(listOf(MimeType.IMAGE_ALL), GalleryConfig().mimeTypes)
    }

    @Test
    fun galleryConfig_defaultSelectionLimit_is30() {
        assertEquals(30, GalleryConfig().selectionLimit)
    }

    @Test
    fun galleryConfig_defaultIncludeExif_isFalse() {
        assertFalse(GalleryConfig().includeExif)
    }

    @Test
    fun galleryConfig_defaultRedactGpsData_isTrue() {
        assertTrue(GalleryConfig().redactGpsData)
    }

    @Test
    fun galleryConfig_defaultMimeTypeMismatchMessage_isNull() {
        assertNull(GalleryConfig().mimeTypeMismatchMessage)
    }

    @Test
    fun galleryConfig_customMimeTypeMismatchMessage_stored() {
        val config = GalleryConfig(mimeTypeMismatchMessage = "PNG only")
        assertEquals("PNG only", config.mimeTypeMismatchMessage)
    }

    @Test
    fun galleryConfig_equality_sameValues() {
        assertEquals(GalleryConfig(), GalleryConfig())
    }

    @Test
    fun galleryConfig_copy_changesOnlyField() {
        val original = GalleryConfig()
        val copy = original.copy(allowMultiple = true)
        assertTrue(copy.allowMultiple)
        assertEquals(original.selectionLimit, copy.selectionLimit)
    }

    // ── CropConfig defaults ───────────────────────────────────────────────────

    @Test
    fun cropConfig_defaultEnabled_isFalse() {
        assertFalse(CropConfig().enabled)
    }

    @Test
    fun cropConfig_defaultAspectRatioLocked_isFalse() {
        assertFalse(CropConfig().aspectRatioLocked)
    }

    @Test
    fun cropConfig_defaultCircularCrop_isTrue() {
        assertTrue(CropConfig().circularCrop)
    }

    @Test
    fun cropConfig_defaultSquareCrop_isTrue() {
        assertTrue(CropConfig().squareCrop)
    }

    @Test
    fun cropConfig_defaultFreeformCrop_isFalse() {
        assertFalse(CropConfig().freeformCrop)
    }

    @Test
    fun cropConfig_allEnabled() {
        val config = CropConfig(
            enabled = true,
            aspectRatioLocked = true,
            circularCrop = true,
            squareCrop = true,
            freeformCrop = true
        )
        assertTrue(config.enabled)
        assertTrue(config.aspectRatioLocked)
        assertTrue(config.circularCrop)
        assertTrue(config.squareCrop)
        assertTrue(config.freeformCrop)
    }

    @Test
    fun cropConfig_equality_sameValues() {
        assertEquals(CropConfig(), CropConfig())
    }

    // ── CameraCaptureConfig defaults ──────────────────────────────────────────

    @Test
    fun cameraCaptureConfig_defaultCompressionLevel_isNull() {
        assertNull(CameraCaptureConfig().compressionLevel)
    }

    @Test
    fun cameraCaptureConfig_defaultIncludeExif_isFalse() {
        assertFalse(CameraCaptureConfig().includeExif)
    }

    @Test
    fun cameraCaptureConfig_defaultRedactGpsData_isTrue() {
        assertTrue(CameraCaptureConfig().redactGpsData)
    }

    @Test
    fun cameraCaptureConfig_nullCompressionLevel_isAllowed() {
        val config = CameraCaptureConfig(compressionLevel = null)
        assertNull(config.compressionLevel)
    }

    @Test
    fun cameraCaptureConfig_highCompression_stored() {
        val config = CameraCaptureConfig(compressionLevel = CompressionLevel.HIGH)
        assertEquals(CompressionLevel.HIGH, config.compressionLevel)
    }

    @Test
    fun cameraCaptureConfig_includeExif_andRedactGpsFalse() {
        val config = CameraCaptureConfig(includeExif = true, redactGpsData = false)
        assertTrue(config.includeExif)
        assertFalse(config.redactGpsData)
    }

    @Test
    fun cameraCaptureConfig_equality_sameValues() {
        assertEquals(CameraCaptureConfig(), CameraCaptureConfig())
    }

    @Test
    fun cameraCaptureConfig_copy_changesField() {
        val original = CameraCaptureConfig()
        val copy = original.copy(includeExif = true)
        assertTrue(copy.includeExif)
        assertFalse(original.includeExif)
    }

    // ── PermissionAndConfirmationConfig ───────────────────────────────────────

    @Test
    fun permissionAndConfirmation_defaults_allNull() {
        val config = PermissionAndConfirmationConfig()
        assertNull(config.customPermissionHandler)
        assertNull(config.customDeniedDialog)
        assertNull(config.customSettingsDialog)
        assertNull(config.cancelButtonTextIOS)
        assertNull(config.onCancelPermissionConfigIOS)
    }

    @Test
    fun permissionAndConfirmation_customHandler_stored() {
        val handler: (io.github.ismoy.imagepickerkmp.config.PermissionConfig) -> Unit = {}
        val config = PermissionAndConfirmationConfig(customPermissionHandler = handler)
        assertNotNull(config.customPermissionHandler)
    }

    @Test
    fun permissionAndConfirmation_cancelButtonTextIOS_stored() {
        val config = PermissionAndConfirmationConfig(cancelButtonTextIOS = "Cancelar")
        assertEquals("Cancelar", config.cancelButtonTextIOS)
    }

    @Test
    fun permissionAndConfirmation_onCancelPermissionConfigIOS_invokable() {
        var called = false
        val config = PermissionAndConfirmationConfig(onCancelPermissionConfigIOS = { called = true })
        config.onCancelPermissionConfigIOS!!.invoke()
        assertTrue(called)
    }

    @Test
    fun permissionAndConfirmation_equality_bothDefault() {
        assertEquals(PermissionAndConfirmationConfig(), PermissionAndConfirmationConfig())
    }

    // ── ImagePickerKMPConfig composition ──────────────────────────────────────

    @Test
    fun imagePickerKMPConfig_defaults_allSubconfigsAreDefault() {
        val config = ImagePickerKMPConfig()
        assertEquals(CameraCaptureConfig(), config.cameraCaptureConfig)
        assertEquals(GalleryConfig(), config.galleryConfig)
        assertEquals(CropConfig(), config.cropConfig)
        assertEquals(PermissionAndConfirmationConfig(), config.permissionAndConfirmationConfig)
    }

    @Test
    fun imagePickerKMPConfig_hashCode_sameForEqualInstances() {
        assertEquals(ImagePickerKMPConfig().hashCode(), ImagePickerKMPConfig().hashCode())
    }

    @Test
    fun imagePickerKMPConfig_toString_containsClassName() {
        assertTrue(ImagePickerKMPConfig().toString().contains("ImagePickerKMPConfig"))
    }

    @Test
    fun galleryConfig_multipleMimeTypes_stored() {
        val types = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG, MimeType.APPLICATION_PDF)
        val config = GalleryConfig(mimeTypes = types)
        assertEquals(3, config.mimeTypes.size)
        assertTrue(config.mimeTypes.contains(MimeType.APPLICATION_PDF))
    }
}
