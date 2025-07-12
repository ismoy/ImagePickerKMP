package io.github.ismoy.imagepickerkmp

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CameraControllerTest {
    
    @Test
    fun testFlashModeEnumValues() {
        // When
        val flashModes = CameraController.FlashMode.values()
        
        // Then
        assertEquals(3, flashModes.size)
        assertTrue(flashModes.contains(CameraController.FlashMode.AUTO))
        assertTrue(flashModes.contains(CameraController.FlashMode.ON))
        assertTrue(flashModes.contains(CameraController.FlashMode.OFF))
    }
    
    @Test
    fun testCameraTypeEnumValues() {
        // When
        val cameraTypes = CameraController.CameraType.values()
        
        // Then
        assertEquals(2, cameraTypes.size)
        assertTrue(cameraTypes.contains(CameraController.CameraType.BACK))
        assertTrue(cameraTypes.contains(CameraController.CameraType.FRONT))
    }
    
    @Test
    fun testFlashModeEnumNames() {
        // Then
        assertEquals("AUTO", CameraController.FlashMode.AUTO.name)
        assertEquals("ON", CameraController.FlashMode.ON.name)
        assertEquals("OFF", CameraController.FlashMode.OFF.name)
    }
    
    @Test
    fun testCameraTypeEnumNames() {
        // Then
        assertEquals("BACK", CameraController.CameraType.BACK.name)
        assertEquals("FRONT", CameraController.CameraType.FRONT.name)
    }
    
    @Test
    fun testFlashModeEnumOrdinal() {
        // Then
        assertEquals(0, CameraController.FlashMode.AUTO.ordinal)
        assertEquals(1, CameraController.FlashMode.ON.ordinal)
        assertEquals(2, CameraController.FlashMode.OFF.ordinal)
    }
    
    @Test
    fun testCameraTypeEnumOrdinal() {
        // Then
        assertEquals(0, CameraController.CameraType.BACK.ordinal)
        assertEquals(1, CameraController.CameraType.FRONT.ordinal)
    }
} 