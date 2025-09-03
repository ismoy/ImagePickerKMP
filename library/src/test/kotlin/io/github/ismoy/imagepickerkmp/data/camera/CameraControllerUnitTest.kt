package io.github.ismoy.imagepickerkmp.data.camera

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CameraControllerUnitTest {

    @Test
    fun testFlashModeEnum() {
        val flashModes = CameraController.FlashMode.values()
        
        assertEquals("Should have 3 flash modes", 3, flashModes.size)
        
        // Test all values exist
        assertTrue("Should contain AUTO", flashModes.contains(CameraController.FlashMode.AUTO))
        assertTrue("Should contain ON", flashModes.contains(CameraController.FlashMode.ON))
        assertTrue("Should contain OFF", flashModes.contains(CameraController.FlashMode.OFF))
        
        // Test ordinals
        assertEquals("AUTO ordinal should be 0", 0, CameraController.FlashMode.AUTO.ordinal)
        assertEquals("ON ordinal should be 1", 1, CameraController.FlashMode.ON.ordinal)
        assertEquals("OFF ordinal should be 2", 2, CameraController.FlashMode.OFF.ordinal)
        
        // Test names
        assertEquals("AUTO name", "AUTO", CameraController.FlashMode.AUTO.name)
        assertEquals("ON name", "ON", CameraController.FlashMode.ON.name)
        assertEquals("OFF name", "OFF", CameraController.FlashMode.OFF.name)
    }

    @Test
    fun testCameraTypeEnum() {
        val cameraTypes = CameraController.CameraType.values()
        
        assertEquals("Should have 2 camera types", 2, cameraTypes.size)
        
        // Test all values exist
        assertTrue("Should contain BACK", cameraTypes.contains(CameraController.CameraType.BACK))
        assertTrue("Should contain FRONT", cameraTypes.contains(CameraController.CameraType.FRONT))
        
        // Test ordinals
        assertEquals("BACK ordinal should be 0", 0, CameraController.CameraType.BACK.ordinal)
        assertEquals("FRONT ordinal should be 1", 1, CameraController.CameraType.FRONT.ordinal)
        
        // Test names
        assertEquals("BACK name", "BACK", CameraController.CameraType.BACK.name)
        assertEquals("FRONT name", "FRONT", CameraController.CameraType.FRONT.name)
    }

    @Test
    fun testFlashModeValueOf() {
        assertEquals("valueOf AUTO", CameraController.FlashMode.AUTO, 
            CameraController.FlashMode.valueOf("AUTO"))
        assertEquals("valueOf ON", CameraController.FlashMode.ON, 
            CameraController.FlashMode.valueOf("ON"))
        assertEquals("valueOf OFF", CameraController.FlashMode.OFF, 
            CameraController.FlashMode.valueOf("OFF"))
    }

    @Test
    fun testCameraTypeValueOf() {
        assertEquals("valueOf BACK", CameraController.CameraType.BACK, 
            CameraController.CameraType.valueOf("BACK"))
        assertEquals("valueOf FRONT", CameraController.CameraType.FRONT, 
            CameraController.CameraType.valueOf("FRONT"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFlashModeInvalidValueOf() {
        CameraController.FlashMode.valueOf("INVALID")
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCameraTypeInvalidValueOf() {
        CameraController.CameraType.valueOf("INVALID")
    }

    @Test
    fun testFlashModeEquality() {
        val auto1 = CameraController.FlashMode.AUTO
        val auto2 = CameraController.FlashMode.AUTO
        val on = CameraController.FlashMode.ON
        
        assertEquals("Same flash modes should be equal", auto1, auto2)
        assertNotEquals("Different flash modes should not be equal", auto1, on)
        assertTrue("Same flash mode instances should have same identity", auto1 === auto2)
    }

    @Test
    fun testCameraTypeEquality() {
        val back1 = CameraController.CameraType.BACK
        val back2 = CameraController.CameraType.BACK
        val front = CameraController.CameraType.FRONT
        
        assertEquals("Same camera types should be equal", back1, back2)
        assertNotEquals("Different camera types should not be equal", back1, front)
        assertTrue("Same camera type instances should have same identity", back1 === back2)
    }

    @Test
    fun testFlashModeHashCode() {
        val auto1 = CameraController.FlashMode.AUTO
        val auto2 = CameraController.FlashMode.AUTO
        val on = CameraController.FlashMode.ON
        
        assertEquals("Same flash modes should have same hash code", 
            auto1.hashCode(), auto2.hashCode())
        assertNotEquals("Different flash modes should have different hash codes", 
            auto1.hashCode(), on.hashCode())
    }

    @Test
    fun testCameraTypeHashCode() {
        val back1 = CameraController.CameraType.BACK
        val back2 = CameraController.CameraType.BACK
        val front = CameraController.CameraType.FRONT
        
        assertEquals("Same camera types should have same hash code", 
            back1.hashCode(), back2.hashCode())
        assertNotEquals("Different camera types should have different hash codes", 
            back1.hashCode(), front.hashCode())
    }

    @Test
    fun testFlashModeToString() {
        assertEquals("AUTO toString", "AUTO", CameraController.FlashMode.AUTO.toString())
        assertEquals("ON toString", "ON", CameraController.FlashMode.ON.toString())
        assertEquals("OFF toString", "OFF", CameraController.FlashMode.OFF.toString())
    }

    @Test
    fun testCameraTypeToString() {
        assertEquals("BACK toString", "BACK", CameraController.CameraType.BACK.toString())
        assertEquals("FRONT toString", "FRONT", CameraController.CameraType.FRONT.toString())
    }

    @Test
    fun testFlashModeCompareTo() {
        val auto = CameraController.FlashMode.AUTO
        val on = CameraController.FlashMode.ON
        val off = CameraController.FlashMode.OFF
        
        assertTrue("AUTO < ON", auto < on)
        assertTrue("ON < OFF", on < off)
        assertTrue("AUTO < OFF", auto < off)
        assertEquals("AUTO == AUTO", 0, auto.compareTo(auto))
    }

    @Test
    fun testCameraTypeCompareTo() {
        val back = CameraController.CameraType.BACK
        val front = CameraController.CameraType.FRONT
        
        assertTrue("BACK < FRONT", back < front)
        assertEquals("BACK == BACK", 0, back.compareTo(back))
        assertTrue("FRONT > BACK", front > back)
    }

    @Test
    fun testEnumUniqueness() {
        // Test FlashMode uniqueness
        val flashModes = CameraController.FlashMode.values()
        val uniqueFlashModes = flashModes.toSet()
        assertEquals("All flash mode values should be unique", 
            flashModes.size, uniqueFlashModes.size)
        
        // Test CameraType uniqueness
        val cameraTypes = CameraController.CameraType.values()
        val uniqueCameraTypes = cameraTypes.toSet()
        assertEquals("All camera type values should be unique", 
            cameraTypes.size, uniqueCameraTypes.size)
    }

    @Test
    fun testEnumNamingConvention() {
        // Test FlashMode naming
        for (mode in CameraController.FlashMode.values()) {
            assertTrue("Flash mode name should be uppercase", 
                mode.name == mode.name.uppercase())
            assertTrue("Flash mode name should follow convention", 
                mode.name.matches(Regex("^[A-Z]+$")))
        }
        
        // Test CameraType naming
        for (type in CameraController.CameraType.values()) {
            assertTrue("Camera type name should be uppercase", 
                type.name == type.name.uppercase())
            assertTrue("Camera type name should follow convention", 
                type.name.matches(Regex("^[A-Z]+$")))
        }
    }

    @Test
    fun testEnumOrdinalSequence() {
        // Test FlashMode ordinals
        val flashModes = CameraController.FlashMode.values()
        for (i in flashModes.indices) {
            assertEquals("Flash mode ordinal should be sequential", i, flashModes[i].ordinal)
        }
        
        // Test CameraType ordinals
        val cameraTypes = CameraController.CameraType.values()
        for (i in cameraTypes.indices) {
            assertEquals("Camera type ordinal should be sequential", i, cameraTypes[i].ordinal)
        }
    }

    @Test
    fun testEnumNameUniqueness() {
        // Test FlashMode name uniqueness
        val flashModeNames = CameraController.FlashMode.values().map { it.name }
        val uniqueFlashModeNames = flashModeNames.toSet()
        assertEquals("All flash mode names should be unique", 
            flashModeNames.size, uniqueFlashModeNames.size)
        
        // Test CameraType name uniqueness
        val cameraTypeNames = CameraController.CameraType.values().map { it.name }
        val uniqueCameraTypeNames = cameraTypeNames.toSet()
        assertEquals("All camera type names should be unique", 
            cameraTypeNames.size, uniqueCameraTypeNames.size)
    }
}
