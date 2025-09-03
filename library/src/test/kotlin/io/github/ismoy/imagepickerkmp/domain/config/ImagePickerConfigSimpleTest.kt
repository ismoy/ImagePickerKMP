package io.github.ismoy.imagepickerkmp.domain.config

import androidx.compose.ui.unit.dp
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import junit.framework.TestCase

class ImagePickerConfigSimpleTest : TestCase() {

    fun testUiConfigCreation() {
        val uiConfig = UiConfig()
        assertNotNull("UiConfig should not be null", uiConfig)
    }

    fun testGalleryConfigCreation() {
        val galleryConfig = GalleryConfig()
        assertNotNull("GalleryConfig should not be null", galleryConfig)
    }

    fun testCameraCaptureConfigCreation() {
        val cameraConfig = CameraCaptureConfig()
        assertNotNull("CameraCaptureConfig should not be null", cameraConfig)
    }

    fun testImagePickerConfigCreation() {
        val config = ImagePickerConfig(
            onPhotoCaptured = { },
            onError = { }
        )
        
        assertNotNull("ImagePickerConfig should not be null", config)
        assertTrue("Config should be instance of ImagePickerConfig", config is ImagePickerConfig)
    }

    fun testPermissionAndConfirmationConfigCreation() {
        val permissionConfig = PermissionAndConfirmationConfig()
        assertNotNull("PermissionAndConfirmationConfig should not be null", permissionConfig)
    }

    fun testCameraCallbacksCreation() {
        val callbacks = CameraCallbacks()
        assertNotNull("CameraCallbacks should not be null", callbacks)
    }

    fun testCameraCaptureConfigWithParameters() {
        val captureConfig = CameraCaptureConfig(
            preference = CapturePhotoPreference.FAST,
            captureButtonSize = 100.dp
        )
        
        assertNotNull("CameraCaptureConfig should not be null", captureConfig)
        assertEquals("Preference should match", CapturePhotoPreference.FAST, captureConfig.preference)
    }

    fun testGalleryConfigWithMimeTypes() {
        val mimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG)
        val galleryConfig = GalleryConfig(
            allowMultiple = true,
            mimeTypes = mimeTypes
        )
        
        assertNotNull("GalleryConfig should not be null", galleryConfig)
        assertTrue("Allow multiple should be true", galleryConfig.allowMultiple)
        assertEquals("MimeTypes should match", mimeTypes, galleryConfig.mimeTypes)
    }

    fun testConfigurationComplexity() {
        // Test that all configurations can be created together
        val uiConfig = UiConfig()
        val galleryConfig = GalleryConfig()
        val cameraConfig = CameraCaptureConfig()
        val permissionConfig = PermissionAndConfirmationConfig()
        val callbacks = CameraCallbacks()
        
        assertNotNull("All configs should be creatable", uiConfig)
        assertNotNull("All configs should be creatable", galleryConfig)
        assertNotNull("All configs should be creatable", cameraConfig)
        assertNotNull("All configs should be creatable", permissionConfig)
        assertNotNull("All configs should be creatable", callbacks)
    }
}
