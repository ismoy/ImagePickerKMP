package io.github.ismoy.imagepickerkmp.data.camera

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.camera.view.PreviewView
import io.github.ismoy.imagepickerkmp.data.managers.FileManager
import io.github.ismoy.imagepickerkmp.data.processors.ImageOrientationCorrector
import io.github.ismoy.imagepickerkmp.data.processors.ImageProcessor
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CameraXManagerTest {
    
    private lateinit var mockCameraController: CameraController
    private lateinit var mockImageProcessor: ImageProcessor
    private lateinit var mockContext: Context
    private lateinit var mockActivity: ComponentActivity
    private lateinit var mockPreviewView: PreviewView
    private lateinit var mockPreference: CapturePhotoPreference
    private lateinit var cameraXManager: CameraXManager
    
    @Before
    fun setUp() {
        mockCameraController = mockk(relaxed = true)
        mockImageProcessor = mockk(relaxed = true)
        mockContext = mockk(relaxed = true)
        mockActivity = mockk(relaxed = true)
        mockPreviewView = mockk(relaxed = true)
        mockPreference = mockk(relaxed = true)
        
        cameraXManager = CameraXManager(mockCameraController, mockImageProcessor)
    }
    
    @After
    fun tearDown() {
        unmockkAll()
    }
    
    
    @Test
    fun `constructor should initialize manager with dependencies`() {
        // Act & Assert
        assertNotNull(cameraXManager)
        
        // Test constructor creates manager correctly
        val newManager = CameraXManager(mockCameraController, mockImageProcessor)
        assertNotNull(newManager)
    }
    
    @Test
    fun `secondary constructor should work with context and activity`() {
        // Arrange - Mock the required constructors
        val testManager = CameraXManager(mockContext, mockActivity)
        
        // Assert
        assertNotNull(testManager)
    }
    
    @Test
    fun `startCamera should delegate to cameraController`() = runTest {
        // Act
        cameraXManager.startCamera(mockPreviewView, mockPreference)
        
        // Assert
        coVerify { mockCameraController.startCamera(mockPreviewView, mockPreference) }
    }
    
    @Test
    fun `stopCamera should delegate to cameraController`() {
        // Act
        cameraXManager.stopCamera()
        
        // Assert
        verify { mockCameraController.stopCamera() }
    }
    
    @Test
    fun `setFlashMode should delegate to cameraController with correct mode`() {
        // Test OFF mode
        cameraXManager.setFlashMode(CameraController.FlashMode.OFF)
        verify { mockCameraController.setFlashMode(CameraController.FlashMode.OFF) }
        
        // Test ON mode
        cameraXManager.setFlashMode(CameraController.FlashMode.ON)
        verify { mockCameraController.setFlashMode(CameraController.FlashMode.ON) }
        
        // Test AUTO mode
        cameraXManager.setFlashMode(CameraController.FlashMode.AUTO)
        verify { mockCameraController.setFlashMode(CameraController.FlashMode.AUTO) }
    }
    
    @Test
    fun `switchCamera should delegate to cameraController`() {
        // Act
        cameraXManager.switchCamera()
        
        // Assert
        verify { mockCameraController.switchCamera() }
    }
    
    @Test
    fun `flashModes property should return all available modes`() {
        // Act
        val flashModes = cameraXManager.flashModes
        
        // Assert
        assertNotNull(flashModes)
        assertTrue(flashModes.isNotEmpty())
        assertEquals(CameraController.FlashMode.entries, flashModes)
        
        // Verify all expected flash modes are present
        assertTrue(flashModes.contains(CameraController.FlashMode.OFF))
        assertTrue(flashModes.contains(CameraController.FlashMode.ON))
        assertTrue(flashModes.contains(CameraController.FlashMode.AUTO))
    }
    
    @Test
    fun `takePicture should coordinate camera and image processing - success case`() {
        // Arrange
        val mockImageFile = mockk<File>(relaxed = true)
        val mockPhotoResult = mockk<PhotoResult>(relaxed = true)
        var capturedResult: PhotoResult? = null
        var capturedError: Exception? = null
        
        // Setup camera controller to simulate successful capture
        every { 
            mockCameraController.takePicture(any(), any()) 
        } answers {
            val onImageCaptured = firstArg<(File, CameraController.CameraType) -> Unit>()
            onImageCaptured(mockImageFile, CameraController.CameraType.BACK)
        }
        
        // Setup image processor to simulate successful processing
        every { 
            mockImageProcessor.processImage(any(), any(), any(), any()) 
        } answers {
            val onPhotoCaptured = thirdArg<(PhotoResult) -> Unit>()
            onPhotoCaptured(mockPhotoResult)
        }
        
        // Act
        cameraXManager.takePicture(
            onPhotoResult = { capturedResult = it },
            onError = { capturedError = it }
        )
        
        // Assert
        assertNotNull(capturedResult)
        assertEquals(mockPhotoResult, capturedResult)
        assertEquals(null, capturedError)
        
        verify { mockCameraController.takePicture(any(), any()) }
        verify { mockImageProcessor.processImage(mockImageFile, CameraController.CameraType.BACK, any(), any()) }
    }
    
    @Test
    fun `takePicture should handle camera controller error`() {
        // Arrange
        val testException = RuntimeException("Camera error")
        var capturedResult: PhotoResult? = null
        var capturedError: Exception? = null
        
        every { 
            mockCameraController.takePicture(any(), any()) 
        } answers {
            val onError = secondArg<(Exception) -> Unit>()
            onError(testException)
        }
        
        // Act
        cameraXManager.takePicture(
            onPhotoResult = { capturedResult = it },
            onError = { capturedError = it }
        )
        
        // Assert
        assertEquals(null, capturedResult)
        assertNotNull(capturedError)
        assertEquals(testException, capturedError)
        
        verify { mockCameraController.takePicture(any(), any()) }
        verify(exactly = 0) { mockImageProcessor.processImage(any(), any(), any(), any()) }
    }
    
    @Test
    fun `takePicture should handle image processor error`() {
        // Arrange
        val mockImageFile = mockk<File>(relaxed = true)
        val testException = RuntimeException("Processing error")
        var capturedResult: PhotoResult? = null
        var capturedError: Exception? = null
        
        every { 
            mockCameraController.takePicture(any(), any()) 
        } answers {
            val onImageCaptured = firstArg<(File, CameraController.CameraType) -> Unit>()
            onImageCaptured(mockImageFile, CameraController.CameraType.FRONT)
        }
        
        every { 
            mockImageProcessor.processImage(any(), any(), any(), any()) 
        } answers {
            val onError = lastArg<(Exception) -> Unit>()
            onError(testException)
        }
        
        // Act
        cameraXManager.takePicture(
            onPhotoResult = { capturedResult = it },
            onError = { capturedError = it }
        )
        
        // Assert
        assertEquals(null, capturedResult)
        assertNotNull(capturedError)
        assertEquals(testException, capturedError)
        
        verify { mockCameraController.takePicture(any(), any()) }
        verify { mockImageProcessor.processImage(mockImageFile, CameraController.CameraType.FRONT, any(), any()) }
    }
    
    @Test
    fun `takePicture should work with FRONT camera type`() {
        // Arrange
        val mockImageFile = mockk<File>(relaxed = true)
        val mockPhotoResult = mockk<PhotoResult>(relaxed = true)
        var capturedResult: PhotoResult? = null
        
        every { 
            mockCameraController.takePicture(any(), any()) 
        } answers {
            val onImageCaptured = firstArg<(File, CameraController.CameraType) -> Unit>()
            onImageCaptured(mockImageFile, CameraController.CameraType.FRONT)
        }
        
        every { 
            mockImageProcessor.processImage(any(), any(), any(), any()) 
        } answers {
            val onPhotoCaptured = thirdArg<(PhotoResult) -> Unit>()
            onPhotoCaptured(mockPhotoResult)
        }
        
        // Act
        cameraXManager.takePicture(
            onPhotoResult = { capturedResult = it },
            onError = { }
        )
        
        // Assert
        assertEquals(mockPhotoResult, capturedResult)
        verify { mockImageProcessor.processImage(mockImageFile, CameraController.CameraType.FRONT, any(), any()) }
    }
    
    @Test
    fun `takePicture should work with BACK camera type`() {
        // Arrange
        val mockImageFile = mockk<File>(relaxed = true)
        val mockPhotoResult = mockk<PhotoResult>(relaxed = true)
        var capturedResult: PhotoResult? = null
        
        every { 
            mockCameraController.takePicture(any(), any()) 
        } answers {
            val onImageCaptured = firstArg<(File, CameraController.CameraType) -> Unit>()
            onImageCaptured(mockImageFile, CameraController.CameraType.BACK)
        }
        
        every { 
            mockImageProcessor.processImage(any(), any(), any(), any()) 
        } answers {
            val onPhotoCaptured = thirdArg<(PhotoResult) -> Unit>()
            onPhotoCaptured(mockPhotoResult)
        }
        
        // Act
        cameraXManager.takePicture(
            onPhotoResult = { capturedResult = it },
            onError = { }
        )
        
        // Assert
        assertEquals(mockPhotoResult, capturedResult)
        verify { mockImageProcessor.processImage(mockImageFile, CameraController.CameraType.BACK, any(), any()) }
    }
    
    @Test
    fun `multiple operations should work together`() = runTest {
        // Test complete workflow
        
        // 1. Start camera
        cameraXManager.startCamera(mockPreviewView, mockPreference)
        
        // 2. Set flash mode
        cameraXManager.setFlashMode(CameraController.FlashMode.AUTO)
        
        // 3. Switch camera
        cameraXManager.switchCamera()
        
        // 4. Stop camera
        cameraXManager.stopCamera()
        
        // Verify all operations
        coVerify { mockCameraController.startCamera(mockPreviewView, mockPreference) }
        verify { mockCameraController.setFlashMode(CameraController.FlashMode.AUTO) }
        verify { mockCameraController.switchCamera() }
        verify { mockCameraController.stopCamera() }
    }
    
    @Test
    fun `flashModes should contain correct number of modes`() {
        // Act
        val flashModes = cameraXManager.flashModes
        
        // Assert
        assertEquals(3, flashModes.size) // OFF, ON, AUTO
        
        // Verify exact modes
        assertTrue(CameraController.FlashMode.OFF in flashModes)
        assertTrue(CameraController.FlashMode.ON in flashModes)
        assertTrue(CameraController.FlashMode.AUTO in flashModes)
    }
    
    @Test
    fun `setFlashMode should handle multiple consecutive calls`() {
        // Act - Multiple flash mode changes
        cameraXManager.setFlashMode(CameraController.FlashMode.OFF)
        cameraXManager.setFlashMode(CameraController.FlashMode.ON)
        cameraXManager.setFlashMode(CameraController.FlashMode.AUTO)
        cameraXManager.setFlashMode(CameraController.FlashMode.OFF)
        
        // Assert
        verify(exactly = 2) { mockCameraController.setFlashMode(CameraController.FlashMode.OFF) }
        verify(exactly = 1) { mockCameraController.setFlashMode(CameraController.FlashMode.ON) }
        verify(exactly = 1) { mockCameraController.setFlashMode(CameraController.FlashMode.AUTO) }
    }
    
    @Test
    fun `multiple camera switches should work`() {
        // Act
        cameraXManager.switchCamera()
        cameraXManager.switchCamera()
        cameraXManager.switchCamera()
        
        // Assert
        verify(exactly = 3) { mockCameraController.switchCamera() }
    }
    
    @Test
    fun `start and stop camera multiple times should work`() = runTest {
        // Act
        cameraXManager.startCamera(mockPreviewView, mockPreference)
        cameraXManager.stopCamera()
        cameraXManager.startCamera(mockPreviewView, mockPreference)
        cameraXManager.stopCamera()
        
        // Assert
        coVerify(exactly = 2) { mockCameraController.startCamera(mockPreviewView, mockPreference) }
        verify(exactly = 2) { mockCameraController.stopCamera() }
    }
}
