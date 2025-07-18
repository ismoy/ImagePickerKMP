package io.github.ismoy.imagepickerkmp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CameraControllerTest {
    @Test
    fun testInitialCameraTypeIsBack() {
        val controller = FakeCameraController()
        assertEquals(FakeCameraController.CameraType.BACK, controller.currentCameraType)
    }

    @Test
    fun testSwitchCameraChangesType() {
        val controller = FakeCameraController()
        controller.switchCamera()
        assertEquals(FakeCameraController.CameraType.FRONT, controller.currentCameraType)
        controller.switchCamera()
        assertEquals(FakeCameraController.CameraType.BACK, controller.currentCameraType)
    }

    @Test
    fun testSetFlashMode() {
        val controller = FakeCameraController()
        controller.setFlashMode(FakeCameraController.FlashMode.ON)
        assertEquals(FakeCameraController.FlashMode.ON, controller.currentFlashMode)
        controller.setFlashMode(FakeCameraController.FlashMode.OFF)
        assertEquals(FakeCameraController.FlashMode.OFF, controller.currentFlashMode)
    }

    @Test
    fun testStartCameraThrowsOnError() {
        val controller = FakeCameraController(shouldFail = true)
        val exception = assertFailsWith<PhotoCaptureException> {
            controller.startCamera()
        }
        assertTrue(exception.message!!.contains("Failed to start camera"))
    }

    @Test
    fun testCapturePhotoThrowsOnError() {
        val controller = FakeCameraController()
        controller.shouldFailCapture = true
        val exception = assertFailsWith<PhotoCaptureException> {
            controller.capturePhoto()
        }
        assertTrue(exception.message!!.contains("Failed to capture photo"))
    }
}

class FakeCameraController(val shouldFail: Boolean = false) {
    enum class CameraType { BACK, FRONT }
    enum class FlashMode { AUTO, ON, OFF }
    var currentCameraType: CameraType = CameraType.BACK
    var currentFlashMode: FlashMode = FlashMode.AUTO
    var shouldFailCapture: Boolean = false

    fun switchCamera() {
        currentCameraType = when (currentCameraType) {
            CameraType.BACK -> CameraType.FRONT
            CameraType.FRONT -> CameraType.BACK
        }
    }

    fun setFlashMode(mode: FlashMode) {
        currentFlashMode = mode
    }

    fun startCamera() {
        if (shouldFail) throw PhotoCaptureException("Failed to start camera")
    }

    fun capturePhoto() {
        if (shouldFailCapture) throw PhotoCaptureException("Failed to capture photo")
    }
} 