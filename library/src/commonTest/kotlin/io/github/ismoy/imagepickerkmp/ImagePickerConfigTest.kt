package io.github.ismoy.imagepickerkmp

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.CameraCallbacks
import io.github.ismoy.imagepickerkmp.domain.config.CameraPermissionDialogConfig
import io.github.ismoy.imagepickerkmp.domain.config.CameraPreviewConfig
import io.github.ismoy.imagepickerkmp.domain.config.GalleryConfig
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.domain.config.PermissionAndConfirmationConfig
import io.github.ismoy.imagepickerkmp.domain.config.UiConfig
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ImagePickerConfigTest {
    
    @Test
    fun `UiConfig default values should be correct`() {
        val config = UiConfig()
        
        assertNull(config.buttonColor)
        assertNull(config.iconColor)
        assertNull(config.buttonSize)
        assertNull(config.flashIcon)
        assertNull(config.switchCameraIcon)
        assertNull(config.galleryIcon)
    }
    
    @Test
    fun `UiConfig with custom values should work`() {
        val config = UiConfig(
            buttonColor = Color.Red,
            iconColor = Color.Blue,
            buttonSize = 50.dp
        )
        
        assertEquals(Color.Red, config.buttonColor)
        assertEquals(Color.Blue, config.iconColor)
        assertEquals(50.dp, config.buttonSize)
    }
    
    @Test
    fun `CameraCallbacks default values should be null`() {
        val callbacks = CameraCallbacks()
        
        assertNull(callbacks.onCameraReady)
        assertNull(callbacks.onCameraSwitch)
        assertNull(callbacks.onPermissionError)
        assertNull(callbacks.onGalleryOpened)
    }
    
    @Test
    fun `CameraCallbacks with custom callbacks should work`() {
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
        
        callbacks.onCameraReady?.invoke()
        callbacks.onCameraSwitch?.invoke()
        callbacks.onPermissionError?.invoke(Exception())
        callbacks.onGalleryOpened?.invoke()
        
        assertTrue(cameraReadyCalled)
        assertTrue(cameraSwitchCalled)
        assertTrue(permissionErrorCalled)
        assertTrue(galleryOpenedCalled)
    }
    
    @Test
    fun `GalleryConfig default values should be correct`() {
        val config = GalleryConfig()
        
        assertFalse(config.allowMultiple)
        assertEquals(listOf("image/*"), config.mimeTypes)
        assertEquals(30, config.selectionLimit)
    }
    
    @Test
    fun `GalleryConfig with custom values should work`() {
        val config = GalleryConfig(
            allowMultiple = true,
            mimeTypes = listOf("image/jpeg", "image/png"),
            selectionLimit = 10
        )
        
        assertTrue(config.allowMultiple)
        assertEquals(listOf("image/jpeg", "image/png"), config.mimeTypes)
        assertEquals(10, config.selectionLimit)
    }
    
    @Test
    fun `CameraCaptureConfig default values should be correct`() {
        val config = CameraCaptureConfig()
        
        assertEquals(CapturePhotoPreference.QUALITY, config.preference)
        assertEquals(72.dp, config.captureButtonSize)
        assertNotNull(config.uiConfig)
        assertNotNull(config.cameraCallbacks)
        assertNotNull(config.permissionAndConfirmationConfig)
        assertNotNull(config.galleryConfig)
    }
    
    @Test
    fun `CameraCaptureConfig with custom values should work`() {
        val customUiConfig = UiConfig(buttonColor = Color.Green)
        val config = CameraCaptureConfig(
            preference = CapturePhotoPreference.QUALITY, // Use QUALITY instead of SPEED
            captureButtonSize = 80.dp,
            uiConfig = customUiConfig
        )
        
        assertEquals(CapturePhotoPreference.QUALITY, config.preference)
        assertEquals(80.dp, config.captureButtonSize)
        assertEquals(Color.Green, config.uiConfig.buttonColor)
    }
    
    @Test
    fun `ImagePickerConfig should work with required parameters`() {
        var photoCaptured: PhotoResult? = null
        var errorOccurred: Exception? = null
        var dismissed = false
        
        val config = ImagePickerConfig(
            onPhotoCaptured = { photoCaptured = it },
            onError = { errorOccurred = it },
            onDismiss = { dismissed = true }
        )
        
        // Test callbacks
        val testPhoto = PhotoResult("test://uri", 100, 100)
        config.onPhotoCaptured(testPhoto)
        assertEquals(testPhoto, photoCaptured)
        
        val testError = Exception("Test error")
        config.onError(testError)
        assertEquals(testError, errorOccurred)
        
        config.onDismiss()
        assertTrue(dismissed)
        
        // Test default values
        assertEquals("Select option", config.dialogTitle)
        assertEquals("Take photo", config.takePhotoText)
        assertEquals("Select from gallery", config.selectFromGalleryText)
        assertEquals("Cancel", config.cancelText)
        assertNull(config.customPickerDialog)
        assertNotNull(config.cameraCaptureConfig)
    }
    
    @Test
    fun `CameraPreviewConfig default values should be correct`() {
        val config = CameraPreviewConfig()
        
        assertEquals(72.dp, config.captureButtonSize)
        assertNotNull(config.uiConfig)
        assertNotNull(config.cameraCallbacks)
    }
    
    @Test
    fun `CameraPermissionDialogConfig should store all values correctly`() {
        val config = CameraPermissionDialogConfig(
            titleDialogConfig = "Camera Permission",
            descriptionDialogConfig = "We need camera access",
            btnDialogConfig = "Allow",
            titleDialogDenied = "Permission Denied",
            descriptionDialogDenied = "Camera access denied",
            btnDialogDenied = "Settings"
        )
        
        assertEquals("Camera Permission", config.titleDialogConfig)
        assertEquals("We need camera access", config.descriptionDialogConfig)
        assertEquals("Allow", config.btnDialogConfig)
        assertEquals("Permission Denied", config.titleDialogDenied)
        assertEquals("Camera access denied", config.descriptionDialogDenied)
        assertEquals("Settings", config.btnDialogDenied)
        assertNull(config.customDeniedDialog)
        assertNull(config.customSettingsDialog)
    }
}