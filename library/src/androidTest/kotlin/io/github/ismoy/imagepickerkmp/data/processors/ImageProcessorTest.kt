package io.github.ismoy.imagepickerkmp.data.processors

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.platform.app.InstrumentationRegistry
import io.github.ismoy.imagepickerkmp.data.camera.CameraController.CameraType
import io.github.ismoy.imagepickerkmp.data.managers.FileManager
import io.github.ismoy.imagepickerkmp.domain.exceptions.ImageProcessingException
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ImageProcessorTest {

    @Mock
    private lateinit var fileManager: FileManager
    
    @Mock
    private lateinit var orientationCorrector: ImageOrientationCorrector
    
    private lateinit var imageProcessor: ImageProcessor
    private lateinit var context: Context
    private lateinit var testImageFile: File

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = InstrumentationRegistry.getInstrumentation().targetContext
        imageProcessor = ImageProcessor(fileManager, orientationCorrector)
        
        // Create a test image file
        testImageFile = createTestImageFile()
    }

    private fun createTestImageFile(): File {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val file = File(context.cacheDir, "test_image.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        bitmap.recycle()
        return file
    }

    @Test
    fun testProcessImageWithValidFileAndFrontCamera() = runTest {
        // Arrange
        val correctedFile = File(context.cacheDir, "corrected_test_image.jpg")
        val expectedUri = "file://${correctedFile.absolutePath}"
        val latch = CountDownLatch(1)
        var capturedResult: PhotoResult? = null
        var capturedError: Exception? = null

        `when`(orientationCorrector.correctImageOrientation(testImageFile, CameraType.FRONT))
            .thenReturn(correctedFile)
        `when`(fileManager.fileToUriString(correctedFile)).thenReturn(expectedUri)

        // Create the corrected file with the same content as test file
        createTestImageFile().copyTo(correctedFile, overwrite = true)

        // Act
        imageProcessor.processImage(
            imageFile = testImageFile,
            cameraType = CameraType.FRONT,
            onPhotoCaptured = { result ->
                capturedResult = result
                latch.countDown()
            },
            onError = { error ->
                capturedError = error
                latch.countDown()
            }
        )

        // Wait for async operation
        assertTrue("Operation should complete within timeout", latch.await(5, TimeUnit.SECONDS))

        // Assert
        assertNull("Should not have error", capturedError)
        assertNotNull("Should have captured result", capturedResult)
        assertEquals("URI should match", expectedUri, capturedResult?.uri)
        assertTrue("Width should be positive", capturedResult?.width ?: 0 > 0)
        assertTrue("Height should be positive", capturedResult?.height ?: 0 > 0)
        
        // Verify interactions
        verify(orientationCorrector).correctImageOrientation(testImageFile, CameraType.FRONT)
        verify(fileManager).fileToUriString(correctedFile)
        
        // Cleanup
        correctedFile.delete()
    }

    @Test
    fun testProcessImageWithValidFileAndBackCamera() = runTest {
        // Arrange
        val correctedFile = File(context.cacheDir, "corrected_back_test_image.jpg")
        val expectedUri = "file://${correctedFile.absolutePath}"
        val latch = CountDownLatch(1)
        var capturedResult: PhotoResult? = null
        var capturedError: Exception? = null

        `when`(orientationCorrector.correctImageOrientation(testImageFile, CameraType.BACK))
            .thenReturn(correctedFile)
        `when`(fileManager.fileToUriString(correctedFile)).thenReturn(expectedUri)

        createTestImageFile().copyTo(correctedFile, overwrite = true)

        // Act
        imageProcessor.processImage(
            imageFile = testImageFile,
            cameraType = CameraType.BACK,
            onPhotoCaptured = { result ->
                capturedResult = result
                latch.countDown()
            },
            onError = { error ->
                capturedError = error
                latch.countDown()
            }
        )

        // Wait for async operation
        assertTrue("Operation should complete within timeout", latch.await(5, TimeUnit.SECONDS))

        // Assert
        assertNull("Should not have error", capturedError)
        assertNotNull("Should have captured result", capturedResult)
        assertEquals("URI should match", expectedUri, capturedResult?.uri)
        
        verify(orientationCorrector).correctImageOrientation(testImageFile, CameraType.BACK)
        verify(fileManager).fileToUriString(correctedFile)
        
        correctedFile.delete()
    }

    @Test
    fun testProcessImageWithNullBitmap() = runTest {
        // Arrange
        val emptyFile = File(context.cacheDir, "empty_file.jpg")
        emptyFile.createNewFile() // Empty file that can't be decoded as bitmap
        
        val latch = CountDownLatch(1)
        var capturedResult: PhotoResult? = null
        var capturedError: Exception? = null

        `when`(orientationCorrector.correctImageOrientation(emptyFile, CameraType.FRONT))
            .thenReturn(emptyFile)

        // Act
        imageProcessor.processImage(
            imageFile = emptyFile,
            cameraType = CameraType.FRONT,
            onPhotoCaptured = { result ->
                capturedResult = result
                latch.countDown()
            },
            onError = { error ->
                capturedError = error
                latch.countDown()
            }
        )

        // Wait for async operation
        assertTrue("Operation should complete within timeout", latch.await(5, TimeUnit.SECONDS))

        // Assert
        assertNull("Should not have captured result", capturedResult)
        assertNotNull("Should have error", capturedError)
        assertTrue("Should be ImageProcessingException", capturedError is ImageProcessingException)
        assertEquals("Error message should match", "Failed to decode captured image.", capturedError?.message)
        
        verify(orientationCorrector).correctImageOrientation(emptyFile, CameraType.FRONT)
        verifyNoInteractions(fileManager)
        
        emptyFile.delete()
    }

    @Test
    fun testProcessImageWithOrientationCorrectorException() = runTest {
        // Arrange
        val latch = CountDownLatch(1)
        var capturedResult: PhotoResult? = null
        var capturedError: Exception? = null
        val originalException = RuntimeException("Orientation correction failed")

        `when`(orientationCorrector.correctImageOrientation(testImageFile, CameraType.FRONT))
            .thenThrow(originalException)

        // Act
        imageProcessor.processImage(
            imageFile = testImageFile,
            cameraType = CameraType.FRONT,
            onPhotoCaptured = { result ->
                capturedResult = result
                latch.countDown()
            },
            onError = { error ->
                capturedError = error
                latch.countDown()
            }
        )

        // Wait for async operation
        assertTrue("Operation should complete within timeout", latch.await(5, TimeUnit.SECONDS))

        // Assert
        assertNull("Should not have captured result", capturedResult)
        assertNotNull("Should have error", capturedError)
        assertTrue("Should be ImageProcessingException", capturedError is ImageProcessingException)
        assertTrue("Error message should contain original exception message", 
            capturedError?.message?.contains("Orientation correction failed") == true)
        assertEquals("Cause should be original exception", originalException, capturedError?.cause)
        
        verify(orientationCorrector).correctImageOrientation(testImageFile, CameraType.FRONT)
        verifyNoInteractions(fileManager)
    }

    @Test
    fun testProcessImageWithFileManagerException() = runTest {
        // Arrange
        val correctedFile = File(context.cacheDir, "corrected_fm_test_image.jpg")
        val latch = CountDownLatch(1)
        var capturedResult: PhotoResult? = null
        var capturedError: Exception? = null
        val originalException = RuntimeException("File manager failed")

        `when`(orientationCorrector.correctImageOrientation(testImageFile, CameraType.FRONT))
            .thenReturn(correctedFile)
        `when`(fileManager.fileToUriString(correctedFile)).thenThrow(originalException)

        createTestImageFile().copyTo(correctedFile, overwrite = true)

        // Act
        imageProcessor.processImage(
            imageFile = testImageFile,
            cameraType = CameraType.FRONT,
            onPhotoCaptured = { result ->
                capturedResult = result
                latch.countDown()
            },
            onError = { error ->
                capturedError = error
                latch.countDown()
            }
        )

        // Wait for async operation
        assertTrue("Operation should complete within timeout", latch.await(5, TimeUnit.SECONDS))

        // Assert
        assertNull("Should not have captured result", capturedResult)
        assertNotNull("Should have error", capturedError)
        assertTrue("Should be ImageProcessingException", capturedError is ImageProcessingException)
        assertTrue("Error message should contain original exception message", 
            capturedError?.message?.contains("File manager failed") == true)
        
        verify(orientationCorrector).correctImageOrientation(testImageFile, CameraType.FRONT)
        verify(fileManager).fileToUriString(correctedFile)
        
        correctedFile.delete()
    }

    @Test
    fun testProcessImageWithNonExistentFile() = runTest {
        // Arrange
        val nonExistentFile = File(context.cacheDir, "non_existent.jpg")
        val latch = CountDownLatch(1)
        var capturedResult: PhotoResult? = null
        var capturedError: Exception? = null

        `when`(orientationCorrector.correctImageOrientation(nonExistentFile, CameraType.FRONT))
            .thenReturn(nonExistentFile)

        // Act
        imageProcessor.processImage(
            imageFile = nonExistentFile,
            cameraType = CameraType.FRONT,
            onPhotoCaptured = { result ->
                capturedResult = result
                latch.countDown()
            },
            onError = { error ->
                capturedError = error
                latch.countDown()
            }
        )

        // Wait for async operation
        assertTrue("Operation should complete within timeout", latch.await(5, TimeUnit.SECONDS))

        // Assert
        assertNull("Should not have captured result", capturedResult)
        assertNotNull("Should have error", capturedError)
        assertTrue("Should be ImageProcessingException", capturedError is ImageProcessingException)
        
        verify(orientationCorrector).correctImageOrientation(nonExistentFile, CameraType.FRONT)
        verifyNoInteractions(fileManager)
    }

    @Test
    fun testProcessImageCreatesPhotoResultWithCorrectDimensions() = runTest {
        // Arrange - Create a bitmap with specific dimensions
        val specificWidth = 200
        val specificHeight = 150
        val specificBitmap = Bitmap.createBitmap(specificWidth, specificHeight, Bitmap.Config.ARGB_8888)
        val specificFile = File(context.cacheDir, "specific_size_image.jpg")
        FileOutputStream(specificFile).use { out ->
            specificBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        specificBitmap.recycle()

        val correctedFile = File(context.cacheDir, "corrected_specific_image.jpg")
        val expectedUri = "content://test/uri"
        val latch = CountDownLatch(1)
        var capturedResult: PhotoResult? = null

        `when`(orientationCorrector.correctImageOrientation(specificFile, CameraType.FRONT))
            .thenReturn(correctedFile)
        `when`(fileManager.fileToUriString(correctedFile)).thenReturn(expectedUri)

        specificFile.copyTo(correctedFile, overwrite = true)

        // Act
        imageProcessor.processImage(
            imageFile = specificFile,
            cameraType = CameraType.FRONT,
            onPhotoCaptured = { result ->
                capturedResult = result
                latch.countDown()
            },
            onError = { latch.countDown() }
        )

        assertTrue("Operation should complete within timeout", latch.await(5, TimeUnit.SECONDS))

        // Assert
        assertNotNull("Should have captured result", capturedResult)
        assertEquals("Width should match", specificWidth, capturedResult?.width)
        assertEquals("Height should match", specificHeight, capturedResult?.height)
        assertEquals("URI should match", expectedUri, capturedResult?.uri)
        
        // Cleanup
        specificFile.delete()
        correctedFile.delete()
    }

    @Test
    fun testProcessImageWithDifferentCameraTypes() = runTest {
        // Test both camera types to ensure proper handling
        val cameraTypes = arrayOf(CameraType.FRONT, CameraType.BACK)
        
        for (cameraType in cameraTypes) {
            val correctedFile = File(context.cacheDir, "corrected_${cameraType.name.lowercase()}_image.jpg")
            val expectedUri = "file://${correctedFile.absolutePath}"
            val latch = CountDownLatch(1)
            var capturedResult: PhotoResult? = null

            `when`(orientationCorrector.correctImageOrientation(testImageFile, cameraType))
                .thenReturn(correctedFile)
            `when`(fileManager.fileToUriString(correctedFile)).thenReturn(expectedUri)

            createTestImageFile().copyTo(correctedFile, overwrite = true)

            // Act
            imageProcessor.processImage(
                imageFile = testImageFile,
                cameraType = cameraType,
                onPhotoCaptured = { result ->
                    capturedResult = result
                    latch.countDown()
                },
                onError = { latch.countDown() }
            )

            assertTrue("Operation should complete within timeout for $cameraType", 
                latch.await(5, TimeUnit.SECONDS))

            // Assert
            assertNotNull("Should have captured result for $cameraType", capturedResult)
            assertEquals("URI should match for $cameraType", expectedUri, capturedResult?.uri)
            
            // Cleanup
            correctedFile.delete()
            
            // Reset mocks for next iteration
            reset(orientationCorrector, fileManager)
        }
    }
}
