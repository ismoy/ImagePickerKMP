package io.github.ismoy.imagepickerkmp.domain.config

import junit.framework.TestCase

class ConfigBasicTest : TestCase() {

    fun testUiConfigCreation() {
        val uiConfig = UiConfig()
        
        assertNotNull("UiConfig should not be null", uiConfig)
        assertTrue("UiConfig should be instance of UiConfig", uiConfig is UiConfig)
        
        assertNull("ButtonColor should be null by default", uiConfig.buttonColor)
        assertNull("IconColor should be null by default", uiConfig.iconColor)
        assertNull("ButtonSize should be null by default", uiConfig.buttonSize)
    }

    fun testGalleryConfigCreation() {
        val galleryConfig = GalleryConfig()
        
        assertNotNull("GalleryConfig should not be null", galleryConfig)
        assertTrue("GalleryConfig should be instance of GalleryConfig", galleryConfig is GalleryConfig)
        
        assertFalse("AllowMultiple should be false by default", galleryConfig.allowMultiple)
        assertEquals("SelectionLimit should be 30 by default", 30, galleryConfig.selectionLimit)
        assertNotNull("MimeTypes should not be null", galleryConfig.mimeTypes)
    }

    fun testGalleryConfigWithParameters() {
        val allowMultiple = true
        val selectionLimit = 10
        
        val galleryConfig = GalleryConfig(
            allowMultiple = allowMultiple,
            selectionLimit = selectionLimit
        )
        
        assertEquals("AllowMultiple should match", allowMultiple, galleryConfig.allowMultiple)
        assertEquals("SelectionLimit should match", selectionLimit, galleryConfig.selectionLimit)
    }

    fun testCameraCallbacksCreation() {
        val callbacks = CameraCallbacks()
        
        assertNotNull("CameraCallbacks should not be null", callbacks)
        assertTrue("Callbacks should be instance of CameraCallbacks", callbacks is CameraCallbacks)
        
        assertNull("OnCameraReady should be null by default", callbacks.onCameraReady)
        assertNull("OnCameraSwitch should be null by default", callbacks.onCameraSwitch)
        assertNull("OnPermissionError should be null by default", callbacks.onPermissionError)
        assertNull("OnGalleryOpened should be null by default", callbacks.onGalleryOpened)
    }

    fun testPermissionAndConfirmationConfigCreation() {
        val config = PermissionAndConfirmationConfig()
        
        assertNotNull("PermissionAndConfirmationConfig should not be null", config)
        assertTrue("Config should be instance of PermissionAndConfirmationConfig", 
                  config is PermissionAndConfirmationConfig)
        
        assertNull("CustomPermissionHandler should be null by default", config.customPermissionHandler)
        assertNull("CustomConfirmationView should be null by default", config.customConfirmationView)
        assertNull("CustomDeniedDialog should be null by default", config.customDeniedDialog)
        assertNull("CustomSettingsDialog should be null by default", config.customSettingsDialog)
    }

    fun testCameraCaptureConfigCreation() {
        val config = CameraCaptureConfig()
        
        assertNotNull("CameraCaptureConfig should not be null", config)
        assertTrue("Config should be instance of CameraCaptureConfig", config is CameraCaptureConfig)
        
        assertNotNull("UiConfig should not be null", config.uiConfig)
        assertNotNull("CameraCallbacks should not be null", config.cameraCallbacks)
        assertNotNull("PermissionAndConfirmationConfig should not be null", config.permissionAndConfirmationConfig)
        assertNotNull("GalleryConfig should not be null", config.galleryConfig)
    }

    fun testCameraPreviewConfigCreation() {
        val config = CameraPreviewConfig()
        
        assertNotNull("CameraPreviewConfig should not be null", config)
        assertTrue("Config should be instance of CameraPreviewConfig", config is CameraPreviewConfig)
        
        assertNotNull("UiConfig should not be null", config.uiConfig)
        assertNotNull("CameraCallbacks should not be null", config.cameraCallbacks)
    }

    fun testConfigDefaults() {
        val configs = listOf(
            UiConfig(),
            GalleryConfig(),
            CameraCaptureConfig(),
            CameraPreviewConfig(),
            PermissionAndConfirmationConfig(),
            CameraCallbacks()
        )
        
        configs.forEach { config ->
            assertNotNull("Config should not be null", config)
            assertNotNull("Config toString should work", config.toString())
            assertTrue("Config toString should not be empty", config.toString().isNotEmpty())
        }
    }

    fun testConfigEquality() {
        val config1 = UiConfig()
        val config2 = UiConfig()
        
        assertEquals("Same default configs should be equal", config1, config2)
        
        val gallery1 = GalleryConfig(allowMultiple = true)
        val gallery2 = GalleryConfig(allowMultiple = true)
        val gallery3 = GalleryConfig(allowMultiple = false)
        
        assertEquals("Same gallery configs should be equal", gallery1, gallery2)
        assertFalse("Different gallery configs should not be equal", gallery1 == gallery3)
    }

    fun testConfigHashCode() {
        val config1 = UiConfig()
        val config2 = UiConfig()
        
        assertEquals("Same configs should have same hash code", 
                    config1.hashCode(), config2.hashCode())
        
        val gallery1 = GalleryConfig(allowMultiple = true)
        val gallery2 = GalleryConfig(allowMultiple = true)
        
        assertEquals("Same gallery configs should have same hash code", 
                    gallery1.hashCode(), gallery2.hashCode())
    }

    fun testConfigToString() {
        val configs = mapOf(
            "UiConfig" to UiConfig(),
            "GalleryConfig" to GalleryConfig(),
            "CameraCaptureConfig" to CameraCaptureConfig(),
            "CameraPreviewConfig" to CameraPreviewConfig(),
            "PermissionAndConfirmationConfig" to PermissionAndConfirmationConfig(),
            "CameraCallbacks" to CameraCallbacks()
        )
        
        configs.forEach { (name, config) ->
            val configString = config.toString()
            assertNotNull("$name toString should not be null", configString)
            assertTrue("$name toString should not be empty", configString.isNotEmpty())
            assertTrue("$name toString should contain class name", 
                      configString.contains(name.substringBefore("Config")))
        }
    }

    fun testNestedConfigValidation() {
        val uiConfig = UiConfig()
        val galleryConfig = GalleryConfig()
        val callbacks = CameraCallbacks()
        val permissionConfig = PermissionAndConfirmationConfig()
        
        val cameraConfig = CameraCaptureConfig(
            uiConfig = uiConfig,
            cameraCallbacks = callbacks,
            permissionAndConfirmationConfig = permissionConfig,
            galleryConfig = galleryConfig
        )
        
        assertNotNull("CameraCaptureConfig should not be null", cameraConfig)
        assertEquals("UiConfig should match", uiConfig, cameraConfig.uiConfig)
        assertEquals("CameraCallbacks should match", callbacks, cameraConfig.cameraCallbacks)
        assertEquals("PermissionConfig should match", permissionConfig, cameraConfig.permissionAndConfirmationConfig)
        assertEquals("GalleryConfig should match", galleryConfig, cameraConfig.galleryConfig)
    }

    fun testConfigValidation() {
        // Test that configs handle edge cases
        val galleryConfigMaxLimit = GalleryConfig(selectionLimit = Int.MAX_VALUE)
        val galleryConfigMinLimit = GalleryConfig(selectionLimit = 1)
        
        assertNotNull("Config with max limit should not be null", galleryConfigMaxLimit)
        assertNotNull("Config with min limit should not be null", galleryConfigMinLimit)
        
        assertEquals("Max limit should be set", Int.MAX_VALUE, galleryConfigMaxLimit.selectionLimit)
        assertEquals("Min limit should be set", 1, galleryConfigMinLimit.selectionLimit)
    }
}
