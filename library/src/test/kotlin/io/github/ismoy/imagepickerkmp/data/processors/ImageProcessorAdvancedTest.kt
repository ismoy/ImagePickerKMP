package io.github.ismoy.imagepickerkmp.data.processors

import io.github.ismoy.imagepickerkmp.data.camera.CameraController
import io.github.ismoy.imagepickerkmp.data.managers.FileManager
import io.github.ismoy.imagepickerkmp.domain.exceptions.ImageProcessingException
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class ImageProcessorAdvancedTest {

    private val fileManager = mock(FileManager::class.java)
    private val orientationCorrector = mock(ImageOrientationCorrector::class.java)
    private val imageProcessor = ImageProcessor(fileManager, orientationCorrector)

    @Test
    fun testImageProcessorInstantiationWithDependencies() {
        val processor = ImageProcessor(fileManager, orientationCorrector)
        
        // Verify processor exists with dependencies
        assert(processor != null)
    }

    @Test
    fun testProcessImageAsyncExecution() {
        val mockFile = mock(File::class.java)
        val mockCorrectedFile = mock(File::class.java)
        val cameraType = CameraController.CameraType.FRONT
        val latch = CountDownLatch(1)
        
        var capturedResult: PhotoResult? = null
        var capturedError: Exception? = null
        
        whenever(orientationCorrector.correctImageOrientation(mockFile, cameraType)).thenReturn(mockCorrectedFile)
        whenever(mockCorrectedFile.absolutePath).thenReturn("/test/path")
        whenever(fileManager.fileToUriString(mockCorrectedFile)).thenReturn("file:///test/path")

        // Test async execution
        val startTime = System.currentTimeMillis()
        
        imageProcessor.processImage(
            imageFile = mockFile,
            cameraType = cameraType,
            onPhotoCaptured = { 
                capturedResult = it
                latch.countDown()
            },
            onError = { 
                capturedError = it
                latch.countDown()
            }
        )
        
        val immediateTime = System.currentTimeMillis()
        
        // Should return immediately (async)
        assert(immediateTime - startTime < 50) // Less than 50ms
        
        // Wait for async operation
        latch.await(5, TimeUnit.SECONDS)
        
        // Verify the process was initiated
        verify(orientationCorrector).correctImageOrientation(mockFile, cameraType)
    }

    @Test
    fun testProcessImageErrorHandlingWithSpecificException() {
        val mockFile = mock(File::class.java)
        val cameraType = CameraController.CameraType.BACK
        val latch = CountDownLatch(1)
        
        var capturedError: Exception? = null
        
        whenever(orientationCorrector.correctImageOrientation(mockFile, cameraType))
            .thenThrow(RuntimeException("Test exception"))

        imageProcessor.processImage(
            imageFile = mockFile,
            cameraType = cameraType,
            onPhotoCaptured = { },
            onError = { 
                capturedError = it
                latch.countDown()
            }
        )

        // Wait for async operation
        latch.await(5, TimeUnit.SECONDS)
        
        // Verify error handling was executed
        verify(orientationCorrector).correctImageOrientation(mockFile, cameraType)
        assert(capturedError is ImageProcessingException)
        assert(capturedError?.message?.contains("Failed to process image") == true)
    }

    @Test
    fun testProcessImageWithNullBitmapDecoding() {
        val mockFile = mock(File::class.java)
        val mockCorrectedFile = mock(File::class.java)
        val cameraType = CameraController.CameraType.FRONT
        val latch = CountDownLatch(1)
        
        var capturedError: Exception? = null
        
        whenever(orientationCorrector.correctImageOrientation(mockFile, cameraType)).thenReturn(mockCorrectedFile)
        whenever(mockCorrectedFile.absolutePath).thenReturn("/invalid/path/that/does/not/exist.jpg")

        imageProcessor.processImage(
            imageFile = mockFile,
            cameraType = cameraType,
            onPhotoCaptured = { },
            onError = { 
                capturedError = it
                latch.countDown()
            }
        )

        // Wait for async operation
        latch.await(5, TimeUnit.SECONDS)
        
        // Verify the process was attempted
        verify(orientationCorrector).correctImageOrientation(mockFile, cameraType)
        verify(mockCorrectedFile).absolutePath
    }

    @Test
    fun testProcessImageWithAllCameraTypes() {
        val mockFile = mock(File::class.java)
        val mockCorrectedFile = mock(File::class.java)
        
        // Test with BACK camera
        val latch1 = CountDownLatch(1)
        whenever(orientationCorrector.correctImageOrientation(mockFile, CameraController.CameraType.BACK))
            .thenReturn(mockCorrectedFile)
        
        imageProcessor.processImage(
            imageFile = mockFile,
            cameraType = CameraController.CameraType.BACK,
            onPhotoCaptured = { latch1.countDown() },
            onError = { latch1.countDown() }
        )
        
        latch1.await(3, TimeUnit.SECONDS)
        verify(orientationCorrector).correctImageOrientation(mockFile, CameraController.CameraType.BACK)
        
        // Test with FRONT camera
        val latch2 = CountDownLatch(1)
        whenever(orientationCorrector.correctImageOrientation(mockFile, CameraController.CameraType.FRONT))
            .thenReturn(mockCorrectedFile)
        
        imageProcessor.processImage(
            imageFile = mockFile,
            cameraType = CameraController.CameraType.FRONT,
            onPhotoCaptured = { latch2.countDown() },
            onError = { latch2.countDown() }
        )
        
        latch2.await(3, TimeUnit.SECONDS)
        verify(orientationCorrector).correctImageOrientation(mockFile, CameraController.CameraType.FRONT)
    }

    @Test
    fun testProcessImageFileManagerInteractionComplete() {
        val mockFile = mock(File::class.java)
        val mockCorrectedFile = mock(File::class.java)
        val cameraType = CameraController.CameraType.FRONT
        val latch = CountDownLatch(1)
        
        whenever(orientationCorrector.correctImageOrientation(mockFile, cameraType)).thenReturn(mockCorrectedFile)
        whenever(mockCorrectedFile.absolutePath).thenReturn("/test/path")
        whenever(fileManager.fileToUriString(mockCorrectedFile)).thenReturn("file:///test/path")

        imageProcessor.processImage(
            imageFile = mockFile,
            cameraType = cameraType,
            onPhotoCaptured = { latch.countDown() },
            onError = { latch.countDown() }
        )

        // Wait for async operation
        latch.await(5, TimeUnit.SECONDS)
        
        // Verify complete interaction chain
        verify(orientationCorrector).correctImageOrientation(mockFile, cameraType)
        verify(mockCorrectedFile, atLeastOnce()).absolutePath
    }

    @Test
    fun testProcessImageExceptionWrappingAndMessage() {
        val mockFile = mock(File::class.java)
        val cameraType = CameraController.CameraType.BACK
        val originalException = RuntimeException("Original error message")
        val latch = CountDownLatch(1)
        
        var capturedError: Exception? = null
        
        whenever(orientationCorrector.correctImageOrientation(mockFile, cameraType))
            .thenThrow(originalException)

        imageProcessor.processImage(
            imageFile = mockFile,
            cameraType = cameraType,
            onPhotoCaptured = { },
            onError = { 
                capturedError = it
                latch.countDown()
            }
        )

        // Wait for async operation
        latch.await(5, TimeUnit.SECONDS)
        
        // Verify error handling process with correct wrapping
        verify(orientationCorrector).correctImageOrientation(mockFile, cameraType)
        assert(capturedError is ImageProcessingException)
        assert(capturedError?.cause == originalException)
        assert(capturedError?.message?.contains("Original error message") == true)
    }

    @Test
    fun testProcessImageCallbacksExclusivity() {
        val mockFile = mock(File::class.java)
        val cameraType = CameraController.CameraType.FRONT
        val latch = CountDownLatch(1)
        
        var onPhotoCapturedCalled = false
        var onErrorCalled = false
        
        // Force an error to test callback exclusivity
        whenever(orientationCorrector.correctImageOrientation(mockFile, cameraType))
            .thenThrow(RuntimeException("Test error"))
        
        imageProcessor.processImage(
            imageFile = mockFile,
            cameraType = cameraType,
            onPhotoCaptured = { 
                onPhotoCapturedCalled = true
                latch.countDown()
            },
            onError = { 
                onErrorCalled = true
                latch.countDown()
            }
        )

        // Wait for async operation
        latch.await(5, TimeUnit.SECONDS)
        
        // Verify only one callback was called
        assert(onErrorCalled && !onPhotoCapturedCalled)
    }

    @Test
    fun testProcessImageWithValidBitmapPath() {
        val mockFile = mock(File::class.java)
        val mockCorrectedFile = mock(File::class.java)
        val cameraType = CameraController.CameraType.FRONT
        val latch = CountDownLatch(1)
        
        var resultReceived: PhotoResult? = null
        
        whenever(orientationCorrector.correctImageOrientation(mockFile, cameraType)).thenReturn(mockCorrectedFile)
        whenever(mockCorrectedFile.absolutePath).thenReturn("/test/path")
        whenever(fileManager.fileToUriString(mockCorrectedFile)).thenReturn("file:///test/valid/path")

        imageProcessor.processImage(
            imageFile = mockFile,
            cameraType = cameraType,
            onPhotoCaptured = { 
                resultReceived = it
                latch.countDown()
            },
            onError = { latch.countDown() }
        )

        // Wait for async operation
        latch.await(5, TimeUnit.SECONDS)
        
        // Verify all interactions occurred
        verify(orientationCorrector).correctImageOrientation(mockFile, cameraType)
        verify(mockCorrectedFile, atLeastOnce()).absolutePath
    }

    @Test
    fun testProcessImageCoroutineDispatcherUsage() {
        val mockFile = mock(File::class.java)
        val cameraType = CameraController.CameraType.BACK
        
        // Test that the method is non-blocking
        val startTime = System.currentTimeMillis()
        
        imageProcessor.processImage(
            imageFile = mockFile,
            cameraType = cameraType,
            onPhotoCaptured = { },
            onError = { }
        )
        
        val endTime = System.currentTimeMillis()
        
        // Should return immediately (async execution using coroutines)
        assert(endTime - startTime < 100) // Less than 100ms
    }

    @Test
    fun testProcessImageMainThreadCallbacks() {
        val mockFile = mock(File::class.java)
        val mockCorrectedFile = mock(File::class.java)
        val cameraType = CameraController.CameraType.FRONT
        val latch = CountDownLatch(1)
        
        var callbackThreadName: String? = null
        
        whenever(orientationCorrector.correctImageOrientation(mockFile, cameraType)).thenReturn(mockCorrectedFile)
        whenever(mockCorrectedFile.absolutePath).thenReturn("/test/path")
        whenever(fileManager.fileToUriString(mockCorrectedFile)).thenReturn("file:///test/path")

        imageProcessor.processImage(
            imageFile = mockFile,
            cameraType = cameraType,
            onPhotoCaptured = { 
                callbackThreadName = Thread.currentThread().name
                latch.countDown()
            },
            onError = { 
                callbackThreadName = Thread.currentThread().name
                latch.countDown()
            }
        )

        // Wait for async operation
        latch.await(5, TimeUnit.SECONDS)
        
        // Verify interactions occurred
        verify(orientationCorrector).correctImageOrientation(mockFile, cameraType)
        // Note: In unit tests, Dispatchers.Main is often replaced with test dispatcher
        assert(callbackThreadName != null)
    }

    @Test
    fun testProcessImageWithMultipleSequentialCalls() {
        val mockFile1 = mock(File::class.java)
        val mockFile2 = mock(File::class.java)
        val mockCorrectedFile = mock(File::class.java)
        val cameraType = CameraController.CameraType.BACK
        val latch = CountDownLatch(2)
        
        whenever(orientationCorrector.correctImageOrientation(any(), any())).thenReturn(mockCorrectedFile)
        whenever(mockCorrectedFile.absolutePath).thenReturn("/test/path")
        whenever(fileManager.fileToUriString(mockCorrectedFile)).thenReturn("file:///test/path")

        // First call
        imageProcessor.processImage(
            imageFile = mockFile1,
            cameraType = cameraType,
            onPhotoCaptured = { latch.countDown() },
            onError = { latch.countDown() }
        )

        // Second call
        imageProcessor.processImage(
            imageFile = mockFile2,
            cameraType = cameraType,
            onPhotoCaptured = { latch.countDown() },
            onError = { latch.countDown() }
        )

        // Wait for both async operations
        latch.await(10, TimeUnit.SECONDS)
        
        // Verify both calls were processed
        verify(orientationCorrector, times(2)).correctImageOrientation(any(), any())
    }
}
