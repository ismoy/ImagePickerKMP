package io.github.ismoy.imagepickerkmp

import androidx.camera.view.PreviewView
import io.github.ismoy.imagepickerkmp.data.camera.CameraController
import io.github.ismoy.imagepickerkmp.data.camera.CameraXManager
import io.github.ismoy.imagepickerkmp.data.processors.ImageProcessor
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CameraXManagerTest {

    @Test
    fun `CameraXManager class should be accessible`() {
        // Basic test to ensure the class is properly accessible
        assertTrue(CameraXManager::class.simpleName == "CameraXManager")
    }
    
    @Test
    fun `CameraXManager should delegate startCamera to controller`() = runTest {
        val mockController = mockk<CameraController>(relaxed = true)
        val mockProcessor = mockk<ImageProcessor>(relaxed = true)
        val manager = CameraXManager(mockController, mockProcessor)
        
        val previewView = mockk<PreviewView>(relaxed = true)
        val preference = CapturePhotoPreference.QUALITY
        
        coEvery { mockController.startCamera(any(), any()) } just runs
        
        manager.startCamera(previewView, preference)
        
        coVerify { mockController.startCamera(previewView, preference) }
    }
    
    @Test
    fun `CameraXManager should delegate takePicture to controller and processor`() {
        val mockController = mockk<CameraController>(relaxed = true)
        val mockProcessor = mockk<ImageProcessor>(relaxed = true)
        val manager = CameraXManager(mockController, mockProcessor)
        
        val onPhotoResult: (PhotoResult) -> Unit = mockk(relaxed = true)
        val onError: (Exception) -> Unit = mockk(relaxed = true)
        
        every { mockController.takePicture(any(), any()) } just runs
        
        manager.takePicture(onPhotoResult, onError)
        
        verify { mockController.takePicture(any(), onError) }
    }
    
    @Test
    fun `CameraXManager should delegate all camera operations`() {
        val mockController = mockk<CameraController>(relaxed = true)
        val mockProcessor = mockk<ImageProcessor>(relaxed = true)
        val manager = CameraXManager(mockController, mockProcessor)
        
        // Configure mocks
        every { mockController.stopCamera() } just runs
        every { mockController.setFlashMode(any()) } just runs
        every { mockController.switchCamera() } just runs
        
        // Test delegation
        manager.stopCamera()
        manager.setFlashMode(CameraController.FlashMode.ON)
        manager.switchCamera()
        val modes = manager.flashModes
        
        // Verify delegation
        verify { mockController.stopCamera() }
        verify { mockController.setFlashMode(CameraController.FlashMode.ON) }
        verify { mockController.switchCamera() }
        
        // Test flashModes property
        assertNotNull(modes)
        assertEquals(3, modes.size)
    }
}