package io.github.ismoy.imagepickerkmp.domain.config

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import org.junit.Test
import org.junit.Assert.*

class ImagePickerConfigComprehensiveTest {

    @Test
    fun testUiConfigCreation() {
        val uiConfig = UiConfig(
            buttonColor = Color.Blue,
            iconColor = Color.White,
            buttonSize = 56.dp,
            flashIcon = Icons.Default.FlashOn,
            switchCameraIcon = Icons.Default.CameraAlt,
            galleryIcon = Icons.Default.PhotoLibrary
        )
        
        assertEquals(Color.Blue, uiConfig.buttonColor)
        assertEquals(Color.White, uiConfig.iconColor)
        assertEquals(56.dp, uiConfig.buttonSize)
        assertEquals(Icons.Default.FlashOn, uiConfig.flashIcon)
        assertEquals(Icons.Default.CameraAlt, uiConfig.switchCameraIcon)
        assertEquals(Icons.Default.PhotoLibrary, uiConfig.galleryIcon)
    }

    @Test
    fun testUiConfigDefaultValues() {
        val uiConfig = UiConfig()
        
        // Test that default constructor works
        assertNotNull(uiConfig)
    }

    @Test
    fun testCameraCallbacksCreation() {
        var onCameraReadyCalled = false
        var onCameraSwitchCalled = false
        var onPermissionErrorCalled = false
        var onGalleryOpenedCalled = false
        
        val callbacks = CameraCallbacks(
            onCameraReady = { onCameraReadyCalled = true },
            onCameraSwitch = { onCameraSwitchCalled = true },
            onPermissionError = { onPermissionErrorCalled = true },
            onGalleryOpened = { onGalleryOpenedCalled = true }
        )
        
        // Test callback properties
        assertNotNull(callbacks.onCameraReady)
        assertNotNull(callbacks.onCameraSwitch)
        assertNotNull(callbacks.onPermissionError)
        assertNotNull(callbacks.onGalleryOpened)
        
        // Test callback execution
        callbacks.onCameraReady?.invoke()
        callbacks.onCameraSwitch?.invoke()
        callbacks.onPermissionError?.invoke(RuntimeException("Test error"))
        callbacks.onGalleryOpened?.invoke()
        
        assertTrue(onCameraReadyCalled)
        assertTrue(onCameraSwitchCalled)
        assertTrue(onPermissionErrorCalled)
        assertTrue(onGalleryOpenedCalled)
    }

    @Test
    fun testCameraCallbacksDefaultValues() {
        val callbacks = CameraCallbacks()
        
        // Test that default constructor works with nullable callbacks
        assertNotNull(callbacks)
    }

    @Test
    fun testPermissionAndConfirmationConfigCreation() {
        var permissionHandlerCalled = false
        var confirmationViewCalled = false
        var deniedDialogCalled = false
        var settingsDialogCalled = false
        
        val config = PermissionAndConfirmationConfig(
            customPermissionHandler = { 
                permissionHandlerCalled = true 
            },
            customConfirmationView = { _, _, _ -> 
                confirmationViewCalled = true 
            },
            customDeniedDialog = { _ -> 
                deniedDialogCalled = true 
            },
            customSettingsDialog = { _ -> 
                settingsDialogCalled = true 
            }
        )
        
        // Test config properties
        assertNotNull(config.customPermissionHandler)
        assertNotNull(config.customConfirmationView)
        assertNotNull(config.customDeniedDialog)
        assertNotNull(config.customSettingsDialog)
    }

    @Test
    fun testPermissionAndConfirmationConfigDefaults() {
        val config = PermissionAndConfirmationConfig()
        
        // Test that default constructor works
        assertNotNull(config)
    }

    @Test
    fun testGalleryConfigCreation() {
        val mimeTypes = listOf(MimeType.IMAGE_ALL)
        val config = GalleryConfig(
            allowMultiple = true,
            mimeTypes = mimeTypes,
            selectionLimit = 5
        )
        
        assertTrue(config.allowMultiple)
        assertEquals(mimeTypes, config.mimeTypes)
        assertEquals(5, config.selectionLimit)
    }

    @Test
    fun testGalleryConfigDefaults() {
        val config = GalleryConfig()
        
        // Test that default constructor works
        assertNotNull(config)
        assertNotNull(config.mimeTypes)
        assertTrue("Should have positive selection limit", config.selectionLimit > 0)
    }

    @Test
    fun testCameraCaptureConfigCreation() {
        val uiConfig = UiConfig()
        val callbacks = CameraCallbacks()
        val permissionConfig = PermissionAndConfirmationConfig()
        val galleryConfig = GalleryConfig()
        
        val config = CameraCaptureConfig(
            preference = CapturePhotoPreference.QUALITY,
            captureButtonSize = 80.dp,
            uiConfig = uiConfig,
            cameraCallbacks = callbacks,
            permissionAndConfirmationConfig = permissionConfig,
            galleryConfig = galleryConfig
        )
        
        assertEquals(CapturePhotoPreference.QUALITY, config.preference)
        assertEquals(80.dp, config.captureButtonSize)
        assertEquals(uiConfig, config.uiConfig)
        assertEquals(callbacks, config.cameraCallbacks)
        assertEquals(permissionConfig, config.permissionAndConfirmationConfig)
        assertEquals(galleryConfig, config.galleryConfig)
    }

    @Test
    fun testCameraCaptureConfigDefaults() {
        val config = CameraCaptureConfig()
        
        // Test that default constructor works
        assertNotNull(config)
        assertNotNull(config.preference)
        assertNotNull(config.captureButtonSize)
        assertNotNull(config.uiConfig)
        assertNotNull(config.cameraCallbacks)
        assertNotNull(config.permissionAndConfirmationConfig)
        assertNotNull(config.galleryConfig)
    }

    @Test
    fun testCameraPreviewConfigCreation() {
        val uiConfig = UiConfig()
        val callbacks = CameraCallbacks()
        
        val config = CameraPreviewConfig(
            captureButtonSize = 70.dp,
            uiConfig = uiConfig,
            cameraCallbacks = callbacks
        )
        
        assertEquals(70.dp, config.captureButtonSize)
        assertEquals(uiConfig, config.uiConfig)
        assertEquals(callbacks, config.cameraCallbacks)
    }

    @Test
    fun testCameraPreviewConfigDefaults() {
        val config = CameraPreviewConfig()
        
        // Test that default constructor works
        assertNotNull(config)
        assertNotNull(config.captureButtonSize)
        assertNotNull(config.uiConfig)
        assertNotNull(config.cameraCallbacks)
    }

    @Test
    fun testImagePickerConfigCreation() {
        var photoCapturedCalled = false
        var photoSelectedCalled = false
        var errorCalled = false
        var dismissCalled = false
        
        val config = ImagePickerConfig(
            onPhotoCaptured = { photoCapturedCalled = true },
            onPhotosSelected = { photoSelectedCalled = true },
            onError = { errorCalled = true },
            onDismiss = { dismissCalled = true },
            dialogTitle = "Select Photo",
            takePhotoText = "Take Photo",
            selectFromGalleryText = "Gallery",
            cancelText = "Cancel",
            cameraCaptureConfig = CameraCaptureConfig(),
            directCameraLaunch = false
        )
        
        assertNotNull(config.onPhotoCaptured)
        assertNotNull(config.onPhotosSelected)
        assertNotNull(config.onError)
        assertNotNull(config.onDismiss)
        assertEquals("Select Photo", config.dialogTitle)
        assertEquals("Take Photo", config.takePhotoText)
        assertEquals("Gallery", config.selectFromGalleryText)
        assertEquals("Cancel", config.cancelText)
        assertNotNull(config.cameraCaptureConfig)
        assertFalse(config.directCameraLaunch)
        
        // Test callbacks
        config.onPhotoCaptured(PhotoResult("", 0, 0))
        config.onPhotosSelected?.invoke(emptyList())
        config.onError(RuntimeException("Test"))
        config.onDismiss()
        
        assertTrue(photoCapturedCalled)
        assertTrue(photoSelectedCalled)
        assertTrue(errorCalled)
        assertTrue(dismissCalled)
    }

    @Test
    fun testImagePickerConfigDefaults() {
        val config = ImagePickerConfig(
            onPhotoCaptured = {},
            onError = {}
        )
        
        // Test that constructor works with required params
        assertNotNull(config)
        assertNotNull(config.onPhotoCaptured)
        assertNotNull(config.onError)
        assertNotNull(config.onDismiss)
        assertNotNull(config.dialogTitle)
        assertNotNull(config.takePhotoText)
        assertNotNull(config.selectFromGalleryText)
        assertNotNull(config.cancelText)
        assertNotNull(config.cameraCaptureConfig)
        assertNotNull(config.directCameraLaunch)
    }

    @Test
    fun testConfigurationChaining() {
        // Test creating a full configuration chain
        val uiConfig = UiConfig()
        val callbacks = CameraCallbacks()
        val permissionConfig = PermissionAndConfirmationConfig()
        val galleryConfig = GalleryConfig(allowMultiple = true, selectionLimit = 3)
        val cameraCaptureConfig = CameraCaptureConfig(
            uiConfig = uiConfig,
            cameraCallbacks = callbacks,
            permissionAndConfirmationConfig = permissionConfig,
            galleryConfig = galleryConfig
        )
        val imagePickerConfig = ImagePickerConfig(
            onPhotoCaptured = {},
            onError = {},
            cameraCaptureConfig = cameraCaptureConfig,
            directCameraLaunch = true
        )
        
        // Verify the chain is properly linked
        assertEquals(cameraCaptureConfig, imagePickerConfig.cameraCaptureConfig)
        assertEquals(uiConfig, imagePickerConfig.cameraCaptureConfig.uiConfig)
        assertEquals(callbacks, imagePickerConfig.cameraCaptureConfig.cameraCallbacks)
        assertEquals(permissionConfig, imagePickerConfig.cameraCaptureConfig.permissionAndConfirmationConfig)
        assertEquals(galleryConfig, imagePickerConfig.cameraCaptureConfig.galleryConfig)
    }
}
