package io.github.ismoy.imagepickerkmp.domain.config

import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import junit.framework.TestCase

class ConfigurationComprehensiveTest : TestCase() {

    fun testImagePickerConfigCreation() {
        val config = ImagePickerConfig(
            onPhotoCaptured = { },
            onPhotosSelected = { },
            onError = { },
            onDismiss = { },
            dialogTitle = "Test Dialog",
            takePhotoText = "Take Photo",
            selectFromGalleryText = "Select from Gallery",
            cancelText = "Cancel"
        )
        
        assertNotNull("Config should not be null", config)
        assertEquals("Dialog title should match", "Test Dialog", config.dialogTitle)
        assertEquals("Take photo text should match", "Take Photo", config.takePhotoText)
        assertEquals("Gallery text should match", "Select from Gallery", config.selectFromGalleryText)
        assertEquals("Cancel text should match", "Cancel", config.cancelText)
        assertFalse("Direct camera launch should be false by default", config.directCameraLaunch)
    }

    fun testCameraCaptureConfigCreation() {
        val uiConfig = UiConfig()
        val cameraCallbacks = CameraCallbacks()
        val permissionConfig = PermissionAndConfirmationConfig()
        val galleryConfig = GalleryConfig()
        
        val captureConfig = CameraCaptureConfig(
            preference = CapturePhotoPreference.FRONT,
            captureButtonSize = 80.0f,
            uiConfig = uiConfig,
            cameraCallbacks = cameraCallbacks,
            permissionAndConfirmationConfig = permissionConfig,
            galleryConfig = galleryConfig
        )
        
        assertNotNull("Capture config should not be null", captureConfig)
        assertEquals("Preference should match", CapturePhotoPreference.FRONT, captureConfig.preference)
        assertEquals("UI config should match", uiConfig, captureConfig.uiConfig)
        assertEquals("Gallery config should match", galleryConfig, captureConfig.galleryConfig)
    }

    fun testGalleryConfigCreation() {
        val mimeTypes = listOf(MimeType.JPEG, MimeType.PNG)
        val galleryConfig = GalleryConfig(
            allowMultiple = true,
            mimeTypes = mimeTypes,
            selectionLimit = 5
        )
        
        assertNotNull("Gallery config should not be null", galleryConfig)
        assertTrue("Allow multiple should be true", galleryConfig.allowMultiple)
        assertEquals("Mime types should match", mimeTypes, galleryConfig.mimeTypes)
        assertEquals("Selection limit should match", 5, galleryConfig.selectionLimit)
    }

    fun testCameraCallbacksCreation() {
        var cameraReadyCalled = false
        var cameraSwitchCalled = false
        var permissionErrorCalled = false
        var galleryOpenedCalled = false
        
        val callbacks = CameraCallbacks(
            onCameraReady = { cameraReadyCalled = true },
            onCameraSwitch = { cameraSwitchCalled = true },
            onPermissionError = { permissionErrorCalled = true },
            onGalleryOpened = { galleryOpenedCalled = true }
        )
        
        assertNotNull("Callbacks should not be null", callbacks)
        
        // Test callback execution
        callbacks.onCameraReady.invoke()
        callbacks.onCameraSwitch.invoke()
        callbacks.onPermissionError.invoke(Exception("Test"))
        callbacks.onGalleryOpened.invoke()
        
        assertTrue("Camera ready callback should be called", cameraReadyCalled)
        assertTrue("Camera switch callback should be called", cameraSwitchCalled)
        assertTrue("Permission error callback should be called", permissionErrorCalled)
        assertTrue("Gallery opened callback should be called", galleryOpenedCalled)
    }

    fun testPermissionAndConfirmationConfigCreation() {
        val permissionConfig = PermissionAndConfirmationConfig(
            customPermissionHandler = { true },
            customConfirmationView = { _, _, _, _, _ -> },
            customDeniedDialog = { _, _, _ -> },
            customSettingsDialog = { _, _, _ -> }
        )
        
        assertNotNull("Permission config should not be null", permissionConfig)
        assertNotNull("Custom permission handler should not be null", permissionConfig.customPermissionHandler)
        assertNotNull("Custom confirmation view should not be null", permissionConfig.customConfirmationView)
        assertNotNull("Custom denied dialog should not be null", permissionConfig.customDeniedDialog)
        assertNotNull("Custom settings dialog should not be null", permissionConfig.customSettingsDialog)
    }

    fun testUiConfigCreation() {
        val uiConfig = UiConfig()
        
        assertNotNull("UI config should not be null", uiConfig)
        assertNotNull("Button color should not be null", uiConfig.getButtonColor())
        assertNotNull("Icon color should not be null", uiConfig.getIconColor())
        assertNotNull("Button size should not be null", uiConfig.getButtonSize())
        assertNotNull("Flash icon should not be null", uiConfig.flashIcon)
        assertNotNull("Switch camera icon should not be null", uiConfig.switchCameraIcon)
        assertNotNull("Gallery icon should not be null", uiConfig.galleryIcon)
    }

    fun testCameraPreviewConfigCreation() {
        val uiConfig = UiConfig()
        val callbacks = CameraCallbacks()
        
        val previewConfig = CameraPreviewConfig(
            captureButtonSize = 100.0f,
            uiConfig = uiConfig,
            cameraCallbacks = callbacks
        )
        
        assertNotNull("Preview config should not be null", previewConfig)
        assertEquals("UI config should match", uiConfig, previewConfig.uiConfig)
        assertEquals("Camera callbacks should match", callbacks, previewConfig.cameraCallbacks)
    }

    fun testCameraPermissionDialogConfigCreation() {
        val dialogConfig = CameraPermissionDialogConfig(
            titleDialogConfig = "Permission Required",
            descriptionDialogConfig = "Camera access needed",
            btnDialogConfig = "Grant Permission",
            titleDialogDenied = "Permission Denied",
            descriptionDialogDenied = "Please enable camera permission",
            btnDialogDenied = "Settings"
        )
        
        assertNotNull("Dialog config should not be null", dialogConfig)
        assertEquals("Title should match", "Permission Required", dialogConfig.titleDialogConfig)
        assertEquals("Description should match", "Camera access needed", dialogConfig.descriptionDialogConfig)
        assertEquals("Button text should match", "Grant Permission", dialogConfig.btnDialogConfig)
        assertEquals("Denied title should match", "Permission Denied", dialogConfig.titleDialogDenied)
        assertEquals("Denied description should match", "Please enable camera permission", dialogConfig.descriptionDialogDenied)
        assertEquals("Denied button should match", "Settings", dialogConfig.btnDialogDenied)
    }
}
