package io.github.ismoy.imagepickerkmp.data.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.test.platform.app.InstrumentationRegistry
import io.github.ismoy.imagepickerkmp.domain.exceptions.PhotoCaptureException
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class CameraControllerTest {

    @Mock
    private lateinit var lifecycleOwner: LifecycleOwner

    @Mock
    private lateinit var previewView: PreviewView

    private lateinit var context: Context
    private lateinit var cameraController: CameraController

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testCameraControllerCreation() {
        assertNotNull("CameraController should be created successfully", cameraController)
    }

    @Test
    fun testFlashModeEnum() {
        val flashModes = CameraController.FlashMode.entries.toTypedArray()
        
        assertEquals("Should have 3 flash modes", 3, flashModes.size)
        assertTrue("Should contain AUTO", flashModes.contains(CameraController.FlashMode.AUTO))
        assertTrue("Should contain ON", flashModes.contains(CameraController.FlashMode.ON))
        assertTrue("Should contain OFF", flashModes.contains(CameraController.FlashMode.OFF))
    }

    @Test
    fun testFlashModeEnumOrdinals() {
        assertEquals("AUTO should be ordinal 0", 0, CameraController.FlashMode.AUTO.ordinal)
        assertEquals("ON should be ordinal 1", 1, CameraController.FlashMode.ON.ordinal)
        assertEquals("OFF should be ordinal 2", 2, CameraController.FlashMode.OFF.ordinal)
    }

    @Test
    fun testFlashModeEnumNames() {
        assertEquals("AUTO name should match", "AUTO", CameraController.FlashMode.AUTO.name)
        assertEquals("ON name should match", "ON", CameraController.FlashMode.ON.name)
        assertEquals("OFF name should match", "OFF", CameraController.FlashMode.OFF.name)
    }

    @Test
    fun testFlashModeValueOf() {
        assertEquals("valueOf AUTO should work", CameraController.FlashMode.AUTO, 
            CameraController.FlashMode.valueOf("AUTO"))
        assertEquals("valueOf ON should work", CameraController.FlashMode.ON, 
            CameraController.FlashMode.valueOf("ON"))
        assertEquals("valueOf OFF should work", CameraController.FlashMode.OFF, 
            CameraController.FlashMode.valueOf("OFF"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFlashModeValueOfInvalidName() {
        CameraController.FlashMode.valueOf("INVALID")
    }

    @Test
    fun testCameraTypeEnum() {
        val cameraTypes = CameraController.CameraType.entries.toTypedArray()
        
        assertEquals("Should have 2 camera types", 2, cameraTypes.size)
        assertTrue("Should contain BACK", cameraTypes.contains(CameraController.CameraType.BACK))
        assertTrue("Should contain FRONT", cameraTypes.contains(CameraController.CameraType.FRONT))
    }

    @Test
    fun testCameraTypeEnumOrdinals() {
        assertEquals("BACK should be ordinal 0", 0, CameraController.CameraType.BACK.ordinal)
        assertEquals("FRONT should be ordinal 1", 1, CameraController.CameraType.FRONT.ordinal)
    }

    @Test
    fun testCameraTypeEnumNames() {
        assertEquals("BACK name should match", "BACK", CameraController.CameraType.BACK.name)
        assertEquals("FRONT name should match", "FRONT", CameraController.CameraType.FRONT.name)
    }

    @Test
    fun testCameraTypeValueOf() {
        assertEquals("valueOf BACK should work", CameraController.CameraType.BACK, 
            CameraController.CameraType.valueOf("BACK"))
        assertEquals("valueOf FRONT should work", CameraController.CameraType.FRONT, 
            CameraController.CameraType.valueOf("FRONT"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCameraTypeValueOfInvalidName() {
        CameraController.CameraType.valueOf("INVALID")
    }

    @Test
    fun testSwitchCamera() {
        assertDoesNotThrow {
            cameraController.switchCamera()
        }
    }

    @Test
    fun testSetFlashMode() {
        val flashModes = CameraController.FlashMode.entries.toTypedArray()
        
        for (mode in flashModes) {
            assertDoesNotThrow("Setting flash mode $mode should not throw") {
                cameraController.setFlashMode(mode)
            }
        }
    }

    @Test
    fun testStopCamera() {
        assertDoesNotThrow {
            cameraController.stopCamera()
        }
    }

    @Test
    fun testTakePictureWithoutInitialization() {
        var capturedError: Exception? = null
        var capturedFile: File? = null
        var capturedCameraType: CameraController.CameraType? = null

        cameraController.takePicture(
            onImageCaptured = { file, cameraType ->
                capturedFile = file
                capturedCameraType = cameraType
            },
            onError = { error ->
                capturedError = error
            }
        )

        assertNotNull("Should have error when camera not initialized", capturedError)
        assertTrue("Error should be PhotoCaptureException", capturedError is PhotoCaptureException)
        assertEquals("Error message should indicate camera not initialized", 
            "Camera not initialized.", capturedError?.message)
        assertNull("Should not have captured file", capturedFile)
        assertNull("Should not have captured camera type", capturedCameraType)
    }

    @Test
    fun testFlashModeEnumEquality() {
        val mode1 = CameraController.FlashMode.AUTO
        val mode2 = CameraController.FlashMode.AUTO
        val mode3 = CameraController.FlashMode.ON

        assertEquals("Same flash modes should be equal", mode1, mode2)
        assertNotEquals("Different flash modes should not be equal", mode1, mode3)
        assertTrue("Same flash mode instances should have same identity", mode1 === mode2)
    }

    @Test
    fun testFlashModeEnumHashCode() {
        val mode1 = CameraController.FlashMode.AUTO
        val mode2 = CameraController.FlashMode.AUTO
        val mode3 = CameraController.FlashMode.ON

        assertEquals("Same flash modes should have same hash code", mode1.hashCode(), mode2.hashCode())
        assertNotEquals("Different flash modes should have different hash codes", 
            mode1.hashCode(), mode3.hashCode())
    }

    @Test
    fun testCameraTypeEnumEquality() {
        val type1 = CameraController.CameraType.BACK
        val type2 = CameraController.CameraType.BACK
        val type3 = CameraController.CameraType.FRONT

        assertEquals("Same camera types should be equal", type1, type2)
        assertNotEquals("Different camera types should not be equal", type1, type3)
        assertTrue("Same camera type instances should have same identity", type1 === type2)
    }

    @Test
    fun testCameraTypeEnumHashCode() {
        val type1 = CameraController.CameraType.BACK
        val type2 = CameraController.CameraType.BACK
        val type3 = CameraController.CameraType.FRONT

        assertEquals("Same camera types should have same hash code", type1.hashCode(), type2.hashCode())
        assertNotEquals("Different camera types should have different hash codes", 
            type1.hashCode(), type3.hashCode())
    }

    @Test
    fun testFlashModeEnumToString() {
        assertEquals("AUTO toString should match", "AUTO", CameraController.FlashMode.AUTO.toString())
        assertEquals("ON toString should match", "ON", CameraController.FlashMode.ON.toString())
        assertEquals("OFF toString should match", "OFF", CameraController.FlashMode.OFF.toString())
    }

    @Test
    fun testCameraTypeEnumToString() {
        assertEquals("BACK toString should match", "BACK", CameraController.CameraType.BACK.toString())
        assertEquals("FRONT toString should match", "FRONT", CameraController.CameraType.FRONT.toString())
    }

    @Test
    fun testFlashModeEnumCompareTo() {
        val auto = CameraController.FlashMode.AUTO
        val on = CameraController.FlashMode.ON
        val off = CameraController.FlashMode.OFF

        assertTrue("AUTO should be less than ON", auto < on)
        assertTrue("ON should be less than OFF", on < off)
        assertTrue("AUTO should be less than OFF", auto < off)
        assertEquals("Enum should be equal to itself", 0, auto.compareTo(auto))
    }

    @Test
    fun testCameraTypeEnumCompareTo() {
        val back = CameraController.CameraType.BACK
        val front = CameraController.CameraType.FRONT

        assertTrue("BACK should be less than FRONT", back < front)
        assertEquals("Enum should be equal to itself", 0, back.compareTo(back))
        assertTrue("FRONT should be greater than BACK", front > back)
    }

    @Test
    fun testEnumConstantsAreUnique() {
        // Test FlashMode
        val flashModes = CameraController.FlashMode.values()
        val uniqueFlashModes = flashModes.toSet()
        assertEquals("All flash mode values should be unique", flashModes.size, uniqueFlashModes.size)

        // Test CameraType
        val cameraTypes = CameraController.CameraType.values()
        val uniqueCameraTypes = cameraTypes.toSet()
        assertEquals("All camera type values should be unique", cameraTypes.size, uniqueCameraTypes.size)
    }

    @Test
    fun testEnumNamesAreUnique() {
        // Test FlashMode names
        val flashModeNames = CameraController.FlashMode.values().map { it.name }
        val uniqueFlashModeNames = flashModeNames.toSet()
        assertEquals("All flash mode names should be unique", flashModeNames.size, uniqueFlashModeNames.size)

        // Test CameraType names
        val cameraTypeNames = CameraController.CameraType.values().map { it.name }
        val uniqueCameraTypeNames = cameraTypeNames.toSet()
        assertEquals("All camera type names should be unique", cameraTypeNames.size, uniqueCameraTypeNames.size)
    }

    @Test
    fun testEnumOrdinalsAreSequential() {
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
    fun testFlashModeFormatConsistency() {
        val flashModes = CameraController.FlashMode.values()
        
        for (mode in flashModes) {
            assertTrue("Flash mode name should be uppercase", mode.name == mode.name.uppercase())
            assertTrue("Flash mode name should contain only letters", 
                mode.name.matches(Regex("^[A-Z]+$")))
        }
    }

    @Test
    fun testCameraTypeFormatConsistency() {
        val cameraTypes = CameraController.CameraType.values()
        
        for (type in cameraTypes) {
            assertTrue("Camera type name should be uppercase", type.name == type.name.uppercase())
            assertTrue("Camera type name should contain only letters", 
                type.name.matches(Regex("^[A-Z]+$")))
        }
    }

    private fun assertDoesNotThrow(message: String = "", action: () -> Unit) {
        try {
            action()
        } catch (e: Exception) {
            fail("$message - Should not have thrown exception: ${e.message}")
        }
    }
}
