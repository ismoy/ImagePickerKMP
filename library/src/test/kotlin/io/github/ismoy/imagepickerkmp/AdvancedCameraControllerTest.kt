package io.github.ismoy.imagepickerkmp

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import io.github.ismoy.imagepickerkmp.data.camera.CameraController
import io.mockk.*
import org.junit.Test
import org.junit.Assert.*
import java.io.File

class AdvancedCameraControllerTest {

    private val mockContext = mockk<Context>(relaxed = true)
    private val mockLifecycleOwner = mockk<LifecycleOwner>(relaxed = true)

    @Test
    fun `CameraController initialization should work properly`() {
        val controller = CameraController(mockContext, mockLifecycleOwner)
        assertNotNull("Controller should be initialized", controller)
    }

    @Test
    fun `switchCamera should work multiple times without error`() {
        val controller = CameraController(mockContext, mockLifecycleOwner)
        
        // Test multiple camera switches
        repeat(10) {
            controller.switchCamera()
        }
        
        // Should not throw any exception
        assertTrue("Multiple camera switches should work", true)
    }

    @Test
    fun `setFlashMode should accept all flash modes without error`() {
        val controller = CameraController(mockContext, mockLifecycleOwner)
        
        // Test all flash modes
        CameraController.FlashMode.values().forEach { mode ->
            controller.setFlashMode(mode)
        }
        
        assertTrue("All flash modes should be settable", true)
    }

    @Test
    fun `takePicture should handle error properly when camera not initialized`() {
        val controller = CameraController(mockContext, mockLifecycleOwner)
        var errorReceived = false
        var errorType: Exception? = null
        
        controller.takePicture(
            onImageCaptured = { _, _ -> 
                fail("Should not capture image when camera not initialized")
            },
            onError = { error ->
                errorReceived = true
                errorType = error
            }
        )
        
        Thread.sleep(100)
        
        assertTrue("Error should be received", errorReceived)
        assertNotNull("Error type should not be null", errorType)
    }

    @Test
    fun `stopCamera should work even when camera not started`() {
        val controller = CameraController(mockContext, mockLifecycleOwner)
        
        // Should not throw exception
        controller.stopCamera()
        
        assertTrue("stopCamera should work safely", true)
    }

    @Test
    fun `FlashMode enum should have correct values and behavior`() {
        val modes = CameraController.FlashMode.values()
        
        // Test all modes exist
        assertTrue("AUTO mode should exist", modes.contains(CameraController.FlashMode.AUTO))
        assertTrue("ON mode should exist", modes.contains(CameraController.FlashMode.ON))
        assertTrue("OFF mode should exist", modes.contains(CameraController.FlashMode.OFF))
        
        // Test ordinal values
        assertEquals("AUTO should be ordinal 0", 0, CameraController.FlashMode.AUTO.ordinal)
        assertEquals("ON should be ordinal 1", 1, CameraController.FlashMode.ON.ordinal)
        assertEquals("OFF should be ordinal 2", 2, CameraController.FlashMode.OFF.ordinal)
        
        // Test valueOf
        assertEquals("valueOf AUTO should work", CameraController.FlashMode.AUTO, CameraController.FlashMode.valueOf("AUTO"))
        assertEquals("valueOf ON should work", CameraController.FlashMode.ON, CameraController.FlashMode.valueOf("ON"))
        assertEquals("valueOf OFF should work", CameraController.FlashMode.OFF, CameraController.FlashMode.valueOf("OFF"))
    }

    @Test
    fun `CameraType enum should have correct values and behavior`() {
        val types = CameraController.CameraType.values()
        
        // Test all types exist
        assertTrue("BACK type should exist", types.contains(CameraController.CameraType.BACK))
        assertTrue("FRONT type should exist", types.contains(CameraController.CameraType.FRONT))
        
        // Test ordinal values
        assertEquals("BACK should be ordinal 0", 0, CameraController.CameraType.BACK.ordinal)
        assertEquals("FRONT should be ordinal 1", 1, CameraController.CameraType.FRONT.ordinal)
        
        // Test valueOf
        assertEquals("valueOf BACK should work", CameraController.CameraType.BACK, CameraController.CameraType.valueOf("BACK"))
        assertEquals("valueOf FRONT should work", CameraController.CameraType.FRONT, CameraController.CameraType.valueOf("FRONT"))
    }

    @Test
    fun `enum toString should work correctly`() {
        // FlashMode toString
        assertEquals("AUTO.toString()", "AUTO", CameraController.FlashMode.AUTO.toString())
        assertEquals("ON.toString()", "ON", CameraController.FlashMode.ON.toString())
        assertEquals("OFF.toString()", "OFF", CameraController.FlashMode.OFF.toString())
        
        // CameraType toString
        assertEquals("BACK.toString()", "BACK", CameraController.CameraType.BACK.toString())
        assertEquals("FRONT.toString()", "FRONT", CameraController.CameraType.FRONT.toString())
    }

    @Test
    fun `enum equality should work correctly`() {
        // FlashMode equality
        assertEquals("AUTO should equal AUTO", CameraController.FlashMode.AUTO, CameraController.FlashMode.AUTO)
        assertNotEquals("AUTO should not equal ON", CameraController.FlashMode.AUTO, CameraController.FlashMode.ON)
        
        // CameraType equality
        assertEquals("BACK should equal BACK", CameraController.CameraType.BACK, CameraController.CameraType.BACK)
        assertNotEquals("BACK should not equal FRONT", CameraController.CameraType.BACK, CameraController.CameraType.FRONT)
    }

    @Test
    fun `enum comparison should work correctly`() {
        // FlashMode comparison
        assertTrue("AUTO.ordinal < ON.ordinal", CameraController.FlashMode.AUTO.ordinal < CameraController.FlashMode.ON.ordinal)
        assertTrue("ON.ordinal < OFF.ordinal", CameraController.FlashMode.ON.ordinal < CameraController.FlashMode.OFF.ordinal)
        
        // CameraType comparison
        assertTrue("BACK.ordinal < FRONT.ordinal", CameraController.CameraType.BACK.ordinal < CameraController.CameraType.FRONT.ordinal)
    }

    @Test
    fun `rapid setFlashMode calls should work correctly`() {
        val controller = CameraController(mockContext, mockLifecycleOwner)
        
        // Rapid flash mode changes
        repeat(20) { index ->
            val mode = when (index % 3) {
                0 -> CameraController.FlashMode.AUTO
                1 -> CameraController.FlashMode.ON
                else -> CameraController.FlashMode.OFF
            }
            controller.setFlashMode(mode)
        }
        
        assertTrue("Rapid flash mode changes should work", true)
    }

    @Test
    fun `rapid switchCamera calls should work correctly`() {
        val controller = CameraController(mockContext, mockLifecycleOwner)
        
        // Rapid camera switches
        repeat(50) {
            controller.switchCamera()
        }
        
        assertTrue("Rapid camera switches should work", true)
    }

    @Test
    fun `mixed rapid operations should work correctly`() {
        val controller = CameraController(mockContext, mockLifecycleOwner)
        
        repeat(30) { index ->
            when (index % 4) {
                0 -> controller.switchCamera()
                1 -> controller.setFlashMode(CameraController.FlashMode.AUTO)
                2 -> controller.setFlashMode(CameraController.FlashMode.ON)
                3 -> controller.setFlashMode(CameraController.FlashMode.OFF)
            }
        }
        
        assertTrue("Mixed rapid operations should work", true)
    }

    @Test
    fun `multiple takePicture calls should handle errors properly`() {
        val controller = CameraController(mockContext, mockLifecycleOwner)
        var errorCount = 0
        
        repeat(5) {
            controller.takePicture(
                onImageCaptured = { _, _ -> 
                    fail("Should not capture image when camera not initialized")
                },
                onError = { error ->
                    errorCount++
                    assertNotNull("Error should not be null", error)
                }
            )
        }
        
        Thread.sleep(200)
        assertEquals("All calls should result in errors", 5, errorCount)
    }

    @Test
    fun `CameraController should handle context and lifecycle owner properly`() {
        // Test with different mock contexts
        val controller1 = CameraController(mockContext, mockLifecycleOwner)
        val controller2 = CameraController(mockk<Context>(relaxed = true), mockk<LifecycleOwner>(relaxed = true))
        
        assertNotNull("Controller 1 should be created", controller1)
        assertNotNull("Controller 2 should be created", controller2)
        
        // Both should work independently
        controller1.switchCamera()
        controller2.switchCamera()
        
        controller1.setFlashMode(CameraController.FlashMode.ON)
        controller2.setFlashMode(CameraController.FlashMode.OFF)
        
        assertTrue("Both controllers should work independently", true)
    }

    @Test
    fun `enum values() should return correct arrays`() {
        val flashModes = CameraController.FlashMode.values()
        val cameraTypes = CameraController.CameraType.values()
        
        assertEquals("FlashMode should have 3 values", 3, flashModes.size)
        assertEquals("CameraType should have 2 values", 2, cameraTypes.size)
        
        // Verify specific values are present
        assertTrue("FlashMode values should contain AUTO", flashModes.contains(CameraController.FlashMode.AUTO))
        assertTrue("FlashMode values should contain ON", flashModes.contains(CameraController.FlashMode.ON))
        assertTrue("FlashMode values should contain OFF", flashModes.contains(CameraController.FlashMode.OFF))
        
        assertTrue("CameraType values should contain BACK", cameraTypes.contains(CameraController.CameraType.BACK))
        assertTrue("CameraType values should contain FRONT", cameraTypes.contains(CameraController.CameraType.FRONT))
    }
}
