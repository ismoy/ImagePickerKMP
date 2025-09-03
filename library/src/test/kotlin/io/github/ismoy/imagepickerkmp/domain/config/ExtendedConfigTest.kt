package io.github.ismoy.imagepickerkmp.domain.config

import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import org.junit.Test
import org.junit.Assert.*

class ExtendedConfigTest {

    @Test
    fun galleryConfig_copy_shouldWork() {
        val original = GalleryConfig(allowMultiple = true, selectionLimit = 10)
        val copy = original.copy(selectionLimit = 20)
        
        assertTrue(copy.allowMultiple)
        assertEquals(20, copy.selectionLimit)
    }

    @Test
    fun galleryConfig_equals_shouldWork() {
        val config1 = GalleryConfig(allowMultiple = true, selectionLimit = 10)
        val config2 = GalleryConfig(allowMultiple = true, selectionLimit = 10)
        val config3 = GalleryConfig(allowMultiple = false, selectionLimit = 10)
        
        assertEquals(config1, config2)
        assertNotEquals(config1, config3)
    }

    @Test
    fun cameraCaptureConfig_copy_shouldWork() {
        val original = CameraCaptureConfig(preference = CapturePhotoPreference.FAST)
        val copy = original.copy(preference = CapturePhotoPreference.QUALITY)
        
        assertEquals(CapturePhotoPreference.QUALITY, copy.preference)
    }

    @Test
    fun cameraCaptureConfig_equals_shouldWork() {
        val config1 = CameraCaptureConfig(preference = CapturePhotoPreference.FAST)
        val config2 = CameraCaptureConfig(preference = CapturePhotoPreference.FAST)
        val config3 = CameraCaptureConfig(preference = CapturePhotoPreference.QUALITY)
        
        assertEquals(config1, config2)
        assertNotEquals(config1, config3)
    }

    @Test
    fun uiConfig_copy_shouldWork() {
        val original = UiConfig()
        val copy = original.copy()
        
        assertEquals(original, copy)
    }

    @Test
    fun uiConfig_equals_shouldWork() {
        val config1 = UiConfig()
        val config2 = UiConfig()
        
        assertEquals(config1, config2)
    }

    @Test
    fun cameraCallbacks_copy_shouldWork() {
        var called = false
        val original = CameraCallbacks(onCameraReady = { called = true })
        val copy = original.copy()
        
        copy.onCameraReady?.invoke()
        assertTrue(called)
    }

    @Test
    fun permissionAndConfirmationConfig_copy_shouldWork() {
        val original = PermissionAndConfirmationConfig()
        val copy = original.copy()
        
        assertEquals(original, copy)
    }

    @Test
    fun cameraPreviewConfig_copy_shouldWork() {
        val original = CameraPreviewConfig()
        val copy = original.copy()
        
        assertEquals(original.captureButtonSize, copy.captureButtonSize)
    }

    @Test
    fun imagePickerConfig_copy_shouldWork() {
        var called = false
        val original = ImagePickerConfig(
            onPhotoCaptured = { called = true },
            onError = { }
        )
        
        val copy = original.copy(dialogTitle = "New Title")
        
        assertEquals("New Title", copy.dialogTitle)
        copy.onPhotoCaptured(PhotoResult("test", 100, 100))
        assertTrue(called)
    }

    @Test
    fun imagePickerConfig_defaultStrings_shouldBeCorrect() {
        val config = ImagePickerConfig(
            onPhotoCaptured = {},
            onError = {}
        )
        
        assertEquals("Select option", config.dialogTitle)
        assertEquals("Take photo", config.takePhotoText)
        assertEquals("Select from gallery", config.selectFromGalleryText)
        assertEquals("Cancel", config.cancelText)
    }

    @Test
    fun cameraPermissionDialogConfig_copy_shouldWork() {
        val original = CameraPermissionDialogConfig(
            titleDialogConfig = "Title",
            descriptionDialogConfig = "Description",
            btnDialogConfig = "Button",
            titleDialogDenied = "Denied Title",
            descriptionDialogDenied = "Denied Description",
            btnDialogDenied = "Denied Button"
        )
        
        val copy = original.copy(titleDialogConfig = "New Title")
        
        assertEquals("New Title", copy.titleDialogConfig)
        assertEquals("Description", copy.descriptionDialogConfig)
    }
}
