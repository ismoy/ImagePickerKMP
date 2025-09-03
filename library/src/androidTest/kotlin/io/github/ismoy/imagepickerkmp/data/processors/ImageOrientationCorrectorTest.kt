package io.github.ismoy.imagepickerkmp.data.processors

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.test.platform.app.InstrumentationRegistry
import io.github.ismoy.imagepickerkmp.data.camera.CameraController.CameraType
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.io.File
import java.io.FileOutputStream

@RunWith(MockitoJUnitRunner::class)
class ImageOrientationCorrectorTest {

    private lateinit var orientationCorrector: ImageOrientationCorrector
    private lateinit var context: Context
    private lateinit var testImageFile: File

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        orientationCorrector = ImageOrientationCorrector()
        testImageFile = createTestImageFile()
    }

    private fun createTestImageFile(): File {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val file = File(context.cacheDir, "test_orientation_image.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        bitmap.recycle()
        return file
    }

    @Test
    fun testCorrectImageOrientationWithNormalOrientation() {
        // Act
        val result = orientationCorrector.correctImageOrientation(testImageFile, CameraType.BACK)

        // Assert
        assertNotNull("Result should not be null", result)
        assertEquals("Should return same file for normal orientation", testImageFile, result)
        assertTrue("File should exist", result.exists())
    }

    @Test
    fun testCorrectImageOrientationWithFrontCamera() {
        // Act
        val result = orientationCorrector.correctImageOrientation(testImageFile, CameraType.FRONT)

        // Assert
        assertNotNull("Result should not be null", result)
        assertTrue("Result file should exist", result.exists())
        
        // For front camera, the image should be processed (mirrored)
        // The result might be a new file or the same file depending on whether transformation was needed
    }

    @Test
    fun testCorrectImageOrientationWithBackCamera() {
        // Act
        val result = orientationCorrector.correctImageOrientation(testImageFile, CameraType.BACK)

        // Assert
        assertNotNull("Result should not be null", result)
        assertTrue("Result file should exist", result.exists())
    }

    @Test
    fun testCorrectImageOrientationWithNonExistentFile() {
        // Arrange
        val nonExistentFile = File(context.cacheDir, "non_existent.jpg")

        // Act
        val result = orientationCorrector.correctImageOrientation(nonExistentFile, CameraType.BACK)

        // Assert
        // The method should handle this gracefully and return the input file
        assertEquals("Should return input file when processing fails", nonExistentFile, result)
    }

    @Test
    fun testCorrectImageOrientationWithInvalidImageFile() {
        // Arrange
        val invalidFile = File(context.cacheDir, "invalid_image.txt")
        invalidFile.writeText("This is not an image")

        // Act
        val result = orientationCorrector.correctImageOrientation(invalidFile, CameraType.BACK)

        // Assert
        assertEquals("Should return input file when image is invalid", invalidFile, result)
        
        // Cleanup
        invalidFile.delete()
    }

    @Test
    fun testCorrectImageOrientationPreservesFileWhenNoRotationNeeded() {
        // Act
        val result = orientationCorrector.correctImageOrientation(testImageFile, CameraType.BACK)

        // Assert
        assertEquals("Should return same file when no rotation needed", testImageFile, result)
        assertTrue("Original file should still exist", testImageFile.exists())
    }

    @Test
    fun testCorrectImageOrientationWithDifferentCameraTypes() {
        val cameraTypes = arrayOf(CameraType.FRONT, CameraType.BACK)
        
        for (cameraType in cameraTypes) {
            // Act
            val result = orientationCorrector.correctImageOrientation(testImageFile, cameraType)

            // Assert
            assertNotNull("Result should not be null for $cameraType", result)
            assertTrue("Result file should exist for $cameraType", result.exists())
        }
    }

    @Test
    fun testGetRotationMatrixWithNormalOrientation() {
        // Use reflection to test private method
        val method = ImageOrientationCorrector::class.java.getDeclaredMethod("getRotationMatrix", Int::class.java)
        method.isAccessible = true

        // Act
        val matrix = method.invoke(orientationCorrector, ExifInterface.ORIENTATION_NORMAL) as Matrix

        // Assert
        assertNotNull("Matrix should not be null", matrix)
        assertTrue("Matrix should be identity for normal orientation", matrix.isIdentity)
    }

    @Test
    fun testGetRotationMatrixWithRotate90() {
        val method = ImageOrientationCorrector::class.java.getDeclaredMethod("getRotationMatrix", Int::class.java)
        method.isAccessible = true

        // Act
        val matrix = method.invoke(orientationCorrector, ExifInterface.ORIENTATION_ROTATE_90) as Matrix

        // Assert
        assertNotNull("Matrix should not be null", matrix)
        assertFalse("Matrix should not be identity for 90° rotation", matrix.isIdentity)
    }

    @Test
    fun testGetRotationMatrixWithRotate180() {
        val method = ImageOrientationCorrector::class.java.getDeclaredMethod("getRotationMatrix", Int::class.java)
        method.isAccessible = true

        // Act
        val matrix = method.invoke(orientationCorrector, ExifInterface.ORIENTATION_ROTATE_180) as Matrix

        // Assert
        assertNotNull("Matrix should not be null", matrix)
        assertFalse("Matrix should not be identity for 180° rotation", matrix.isIdentity)
    }

    @Test
    fun testGetRotationMatrixWithRotate270() {
        val method = ImageOrientationCorrector::class.java.getDeclaredMethod("getRotationMatrix", Int::class.java)
        method.isAccessible = true

        // Act
        val matrix = method.invoke(orientationCorrector, ExifInterface.ORIENTATION_ROTATE_270) as Matrix

        // Assert
        assertNotNull("Matrix should not be null", matrix)
        assertFalse("Matrix should not be identity for 270° rotation", matrix.isIdentity)
    }

    @Test
    fun testGetRotationMatrixWithFlipHorizontal() {
        val method = ImageOrientationCorrector::class.java.getDeclaredMethod("getRotationMatrix", Int::class.java)
        method.isAccessible = true

        // Act
        val matrix = method.invoke(orientationCorrector, ExifInterface.ORIENTATION_FLIP_HORIZONTAL) as Matrix

        // Assert
        assertNotNull("Matrix should not be null", matrix)
        assertFalse("Matrix should not be identity for horizontal flip", matrix.isIdentity)
    }

    @Test
    fun testGetRotationMatrixWithFlipVertical() {
        val method = ImageOrientationCorrector::class.java.getDeclaredMethod("getRotationMatrix", Int::class.java)
        method.isAccessible = true

        // Act
        val matrix = method.invoke(orientationCorrector, ExifInterface.ORIENTATION_FLIP_VERTICAL) as Matrix

        // Assert
        assertNotNull("Matrix should not be null", matrix)
        assertFalse("Matrix should not be identity for vertical flip", matrix.isIdentity)
    }

    @Test
    fun testGetRotationMatrixWithTranspose() {
        val method = ImageOrientationCorrector::class.java.getDeclaredMethod("getRotationMatrix", Int::class.java)
        method.isAccessible = true

        // Act
        val matrix = method.invoke(orientationCorrector, ExifInterface.ORIENTATION_TRANSPOSE) as Matrix

        // Assert
        assertNotNull("Matrix should not be null", matrix)
        assertFalse("Matrix should not be identity for transpose", matrix.isIdentity)
    }

    @Test
    fun testGetRotationMatrixWithTransverse() {
        val method = ImageOrientationCorrector::class.java.getDeclaredMethod("getRotationMatrix", Int::class.java)
        method.isAccessible = true

        // Act
        val matrix = method.invoke(orientationCorrector, ExifInterface.ORIENTATION_TRANSVERSE) as Matrix

        // Assert
        assertNotNull("Matrix should not be null", matrix)
        assertFalse("Matrix should not be identity for transverse", matrix.isIdentity)
    }

    @Test
    fun testCorrectImageOrientationHandlesExceptionGracefully() {
        // Arrange - Create a file that will cause issues during processing
        val problematicFile = File(context.cacheDir, "problematic.jpg")
        // Create an empty file that exists but can't be processed as an image
        problematicFile.createNewFile()

        // Act
        val result = orientationCorrector.correctImageOrientation(problematicFile, CameraType.FRONT)

        // Assert
        assertNotNull("Result should not be null even on error", result)
        assertEquals("Should return original file on error", problematicFile, result)

        // Cleanup
        problematicFile.delete()
    }

    @Test
    fun testOrientationConstantsAreUsedCorrectly() {
        // Verify that the constants are being used correctly by testing matrix operations
        val method = ImageOrientationCorrector::class.java.getDeclaredMethod("getRotationMatrix", Int::class.java)
        method.isAccessible = true

        // Test different orientations and verify they create different matrices
        val orientations = arrayOf(
            ExifInterface.ORIENTATION_NORMAL,
            ExifInterface.ORIENTATION_ROTATE_90,
            ExifInterface.ORIENTATION_ROTATE_180,
            ExifInterface.ORIENTATION_ROTATE_270,
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL,
            ExifInterface.ORIENTATION_FLIP_VERTICAL,
            ExifInterface.ORIENTATION_TRANSPOSE,
            ExifInterface.ORIENTATION_TRANSVERSE
        )

        val matrices = mutableListOf<Matrix>()
        
        for (orientation in orientations) {
            val matrix = method.invoke(orientationCorrector, orientation) as Matrix
            matrices.add(matrix)
            assertNotNull("Matrix should not be null for orientation $orientation", matrix)
        }

        // Verify that different orientations produce different matrices (except normal)
        val normalMatrix = matrices[0]
        assertTrue("Normal orientation should produce identity matrix", normalMatrix.isIdentity)
        
        for (i in 1 until matrices.size) {
            assertFalse("Matrix for orientation ${orientations[i]} should not be identity", 
                matrices[i].isIdentity)
        }
    }

    @Test
    fun testCorrectImageOrientationWithLargeImage() {
        // Create a larger test image to ensure it handles different sizes
        val largeBitmap = Bitmap.createBitmap(500, 300, Bitmap.Config.ARGB_8888)
        val largeFile = File(context.cacheDir, "large_test_image.jpg")
        FileOutputStream(largeFile).use { out ->
            largeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        largeBitmap.recycle()

        // Act
        val result = orientationCorrector.correctImageOrientation(largeFile, CameraType.FRONT)

        // Assert
        assertNotNull("Result should not be null", result)
        assertTrue("Result file should exist", result.exists())

        // Cleanup
        largeFile.delete()
        if (result != largeFile && result.exists()) {
            result.delete()
        }
    }

    @Test
    fun testCorrectImageOrientationCreatesNewFileWhenNeeded() {
        // For front camera, the method should create a new corrected file if transformation is applied
        
        // Act
        val result = orientationCorrector.correctImageOrientation(testImageFile, CameraType.FRONT)

        // Assert
        assertNotNull("Result should not be null", result)
        assertTrue("Result file should exist", result.exists())
        
        // If a new file was created, it should have "corrected_" prefix
        if (result != testImageFile) {
            assertTrue("Corrected file should have 'corrected_' prefix", 
                result.name.startsWith("corrected_"))
            // Clean up the corrected file
            result.delete()
        }
    }
}
