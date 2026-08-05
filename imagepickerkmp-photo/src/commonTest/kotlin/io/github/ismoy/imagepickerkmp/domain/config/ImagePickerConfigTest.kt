package io.github.ismoy.imagepickerkmp.domain.config

import io.github.ismoy.imagepickerkmp.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.config.GalleryConfig
import io.github.ismoy.imagepickerkmp.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.config.PermissionAndConfirmationConfig
import io.github.ismoy.imagepickerkmp.picker.MimeType
import io.github.ismoy.imagepickerkmp.picker.PhotoResult
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ImagePickerConfigTest {

    @Test
    fun `GalleryConfig should have default values`() {
        val config = GalleryConfig()

        assertEquals(30, config.selectionLimit)
        assertEquals(listOf(MimeType.IMAGE_ALL), config.mimeTypes)
        assertFalse(config.allowMultiple)
    }

    @Test
    fun `GalleryConfig should allow custom values`() {
        val customMimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG)
        val config = GalleryConfig(
            selectionLimit = 5,
            mimeTypes = customMimeTypes,
            allowMultiple = true
        )

        assertEquals(5, config.selectionLimit)
        assertEquals(customMimeTypes, config.mimeTypes)
        assertTrue(config.allowMultiple)
    }

    @Test
    fun `CameraCaptureConfig should have default values`() {
        val config = CameraCaptureConfig()

        assertEquals(null, config.compressionLevel)
        assertFalse(config.includeExif)
        assertTrue(config.redactGpsData)
    }

    @Test
    fun `CameraCaptureConfig should allow custom values`() {
        val config = CameraCaptureConfig(
            compressionLevel = CompressionLevel.HIGH,
            includeExif = true
        )

        assertEquals(CompressionLevel.HIGH, config.compressionLevel)
        assertTrue(config.includeExif)
    }

    @Test
    fun `PermissionAndConfirmationConfig should have default values`() {
        val config = PermissionAndConfirmationConfig()

        assertEquals(null, config.customPermissionHandler)
        assertEquals(null, config.customDeniedDialog)
        assertEquals(null, config.customSettingsDialog)
    }

    @Test
    fun `ImagePickerConfig should work with lambda parameters`() {
        var photoCaptured = false
        var errorOccurred = false
        var dismissed = false

        val config = ImagePickerConfig(
            onPhotoCaptured = { photoCaptured = true },
            onError = { errorOccurred = true },
            onDismiss = { dismissed = true }
        )

        config.onPhotoCaptured(PhotoResult("test", 100, 100))
        config.onError(Exception("test"))
        config.onDismiss()

        assertTrue(photoCaptured)
        assertTrue(errorOccurred)
        assertTrue(dismissed)
    }

    @Test
    fun `ImagePickerConfig should have correct defaults`() {
        val config = ImagePickerConfig(
            onPhotoCaptured = {},
            onError = {}
        )

        assertFalse(config.enableCrop)
        assertNotNull(config.cameraCaptureConfig)
    }
}
