package io.github.ismoy.imagepickerkmp.domain.config

import junit.framework.TestCase

class ImagePickerConfigSimpleTest : TestCase() {

    fun testUiConfigBasicFunctionality() {
        val uiConfig = UiConfig()
        
        assertNotNull("UiConfig should not be null", uiConfig)
        // Test that we can create UiConfig without issues
        assertTrue("UiConfig should be instance of UiConfig", uiConfig is UiConfig)
    }

    fun testUiConfigWithParameters() {
        val showCancelButton = true
        val backgroundColor = 0xFF000000.toInt()
        
        val uiConfig = UiConfig(
            showCancelButton = showCancelButton,
            backgroundColor = backgroundColor
        )
        
        assertNotNull("UiConfig should not be null", uiConfig)
        assertEquals("ShowCancelButton should match", showCancelButton, uiConfig.showCancelButton)
        assertEquals("BackgroundColor should match", backgroundColor, uiConfig.backgroundColor)
    }

    fun testCameraCaptureConfigBasicFunctionality() {
        val config = CameraCaptureConfig()
        
        assertNotNull("CameraCaptureConfig should not be null", config)
        assertTrue("Config should be instance of CameraCaptureConfig", config is CameraCaptureConfig)
    }

    fun testGalleryConfigBasicFunctionality() {
        val config = GalleryConfig()
        
        assertNotNull("GalleryConfig should not be null", config)
        assertTrue("Config should be instance of GalleryConfig", config is GalleryConfig)
    }

    fun testImagePickerConfigBasicFunctionality() {
        val config = ImagePickerConfig()
        
        assertNotNull("ImagePickerConfig should not be null", config)
        assertTrue("Config should be instance of ImagePickerConfig", config is ImagePickerConfig)
    }

    fun testImagePickerConfigWithSubConfigs() {
        val uiConfig = UiConfig(showCancelButton = true)
        val galleryConfig = GalleryConfig()
        val cameraConfig = CameraCaptureConfig()
        
        val imagePickerConfig = ImagePickerConfig(
            uiConfig = uiConfig,
            galleryConfig = galleryConfig,
            cameraCaptureConfig = cameraConfig
        )
        
        assertNotNull("ImagePickerConfig should not be null", imagePickerConfig)
        assertEquals("UiConfig should match", uiConfig, imagePickerConfig.uiConfig)
        assertEquals("GalleryConfig should match", galleryConfig, imagePickerConfig.galleryConfig)
        assertEquals("CameraCaptureConfig should match", cameraConfig, imagePickerConfig.cameraCaptureConfig)
    }

    fun testCameraPreviewConfigBasicFunctionality() {
        val config = CameraPreviewConfig()
        
        assertNotNull("CameraPreviewConfig should not be null", config)
        assertTrue("Config should be instance of CameraPreviewConfig", config is CameraPreviewConfig)
    }

    fun testPermissionAndConfirmationConfigBasicFunctionality() {
        val config = PermissionAndConfirmationConfig()
        
        assertNotNull("PermissionAndConfirmationConfig should not be null", config)
        assertTrue("Config should be instance of PermissionAndConfirmationConfig", config is PermissionAndConfirmationConfig)
    }

    fun testCameraCallbacksBasicFunctionality() {
        val callbacks = CameraCallbacks()
        
        assertNotNull("CameraCallbacks should not be null", callbacks)
        assertTrue("Callbacks should be instance of CameraCallbacks", callbacks is CameraCallbacks)
    }

    fun testConfigDataClasses() {
        // Test that all config classes can be instantiated
        val configs = listOf(
            UiConfig(),
            GalleryConfig(),
            CameraCaptureConfig(),
            ImagePickerConfig(),
            CameraPreviewConfig(),
            PermissionAndConfirmationConfig(),
            CameraCallbacks()
        )
        
        configs.forEach { config ->
            assertNotNull("Config should not be null", config)
        }
        
        assertEquals("Should have 7 configs", 7, configs.size)
    }

    fun testConfigEquality() {
        val config1 = UiConfig(showCancelButton = true, backgroundColor = 0xFF000000.toInt())
        val config2 = UiConfig(showCancelButton = true, backgroundColor = 0xFF000000.toInt())
        val config3 = UiConfig(showCancelButton = false, backgroundColor = 0xFF000000.toInt())
        
        assertEquals("Same configs should be equal", config1, config2)
        assertFalse("Different configs should not be equal", config1 == config3)
    }

    fun testConfigHashCode() {
        val config1 = UiConfig(showCancelButton = true)
        val config2 = UiConfig(showCancelButton = true)
        
        assertEquals("Same configs should have same hash code", 
                    config1.hashCode(), config2.hashCode())
    }

    fun testConfigToString() {
        val config = UiConfig(showCancelButton = true)
        val configString = config.toString()
        
        assertNotNull("Config toString should not be null", configString)
        assertTrue("Config toString should not be empty", configString.isNotEmpty())
        assertTrue("Config toString should contain class name", 
                  configString.contains("UiConfig"))
    }

    fun testNestedConfigStructure() {
        val uiConfig = UiConfig(showCancelButton = false)
        val galleryConfig = GalleryConfig()
        val cameraConfig = CameraCaptureConfig()
        val permissionConfig = PermissionAndConfirmationConfig()
        val callbacks = CameraCallbacks()
        
        val mainConfig = ImagePickerConfig(
            uiConfig = uiConfig,
            galleryConfig = galleryConfig,
            cameraCaptureConfig = cameraConfig,
            permissionAndConfirmationConfig = permissionConfig,
            cameraCallbacks = callbacks
        )
        
        // Test that nested structure works
        assertNotNull("Main config should not be null", mainConfig)
        assertEquals("Nested UI config should match", uiConfig, mainConfig.uiConfig)
        assertEquals("Nested gallery config should match", galleryConfig, mainConfig.galleryConfig)
        assertEquals("Nested camera config should match", cameraConfig, mainConfig.cameraCaptureConfig)
    }

    fun testConfigDefaults() {
        // Test that configs can be created with default values
        val defaultConfigs = listOf(
            UiConfig(),
            GalleryConfig(),
            CameraCaptureConfig(),
            ImagePickerConfig(),
            CameraPreviewConfig(),
            PermissionAndConfirmationConfig(),
            CameraCallbacks()
        )
        
        defaultConfigs.forEach { config ->
            assertNotNull("Default config should not be null", config)
            // Ensure toString doesn't crash with default values
            assertNotNull("Default config toString should work", config.toString())
        }
    }
}
