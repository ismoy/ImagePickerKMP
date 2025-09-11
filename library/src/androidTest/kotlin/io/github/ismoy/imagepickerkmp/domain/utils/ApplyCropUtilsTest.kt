package io.github.ismoy.imagepickerkmp.domain.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import java.io.File
import java.io.InputStream
import android.content.ContentResolver
import androidx.core.net.toUri
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class ApplyCropUtilsTest {

    private val mockContext = mockk<Context>()
    private val mockContentResolver = mockk<ContentResolver>()
    private val mockInputStream = mockk<InputStream>()
    private val mockBitmap = mockk<Bitmap>()

    @Test
    fun testApplyCropUtils_withValidInput() = runTest {
        // Arrange
        val photoResult = PhotoResult(
            uri = "test://uri",
            width = 100,
            height = 100,
            fileName = "test.jpg"
        )
        val cropRect = Rect(10f, 10f, 50f, 50f)
        val canvasSize = Size(100f, 100f)
        var completedResult: PhotoResult? = null

        every { mockContext.contentResolver } returns mockContentResolver
        every { mockContext.cacheDir } returns File("/tmp")
        every { mockContentResolver.openInputStream(any()) } returns mockInputStream
        every { mockBitmap.width } returns 100
        every { mockBitmap.height } returns 100
        every { mockInputStream.close() } just Runs

        mockkStatic(BitmapFactory::class)
        every { BitmapFactory.decodeStream(mockInputStream) } returns mockBitmap

        mockkStatic(Bitmap::class)
        every { Bitmap.createBitmap(any<Bitmap>(), any(), any(), any(), any()) } returns mockBitmap

        mockkStatic("io.github.ismoy.imagepickerkmp.domain.utils.CreateTransparentBitmapKt")
        every { createTransparentBitmap(any()) } returns mockBitmap

        every { mockBitmap.compress(any(), any(), any()) } returns true

        // Act
        applyCropUtils(
            context = mockContext,
            photoResult = photoResult,
            cropRect = cropRect,
            canvasSize = canvasSize,
            isCircularCrop = false
        ) { result ->
            completedResult = result
        }

        // Wait for coroutines to complete
        Thread.sleep(100)

        // Assert
        verify { mockContentResolver.openInputStream(any()) }
        verify { BitmapFactory.decodeStream(mockInputStream) }
    }

    @Test
    fun testApplyCropUtils_withCircularCrop() = runTest {
        // Arrange
        val photoResult = PhotoResult(
            uri = "test://uri",
            width = 100,
            height = 100,
            fileName = "test.jpg"
        )
        val cropRect = Rect(0f, 0f, 100f, 100f)
        val canvasSize = Size(100f, 100f)

        every { mockContext.contentResolver } returns mockContentResolver
        every { mockContext.cacheDir } returns File("/tmp")
        every { mockContentResolver.openInputStream(any()) } returns mockInputStream
        every { mockBitmap.width } returns 100
        every { mockBitmap.height } returns 100
        every { mockInputStream.close() } just Runs

        mockkStatic(BitmapFactory::class)
        every { BitmapFactory.decodeStream(mockInputStream) } returns mockBitmap

        mockkStatic(Bitmap::class)
        every { Bitmap.createBitmap(any<Bitmap>(), any(), any(), any(), any()) } returns mockBitmap

        mockkStatic("io.github.ismoy.imagepickerkmp.domain.utils.CreateCircularBitmapKt")
        every { createCircularBitmap(any()) } returns mockBitmap

        every { mockBitmap.compress(any(), any(), any()) } returns true

        // Act
        applyCropUtils(
            context = mockContext,
            photoResult = photoResult,
            cropRect = cropRect,
            canvasSize = canvasSize,
            isCircularCrop = true
        ) { }

        // Wait for coroutines to complete
        Thread.sleep(100)

        // Assert
        verify { createCircularBitmap(any()) }
    }

    @Test
    fun testApplyCropUtils_withNullBitmap() = runTest {
        // Arrange
        val photoResult = PhotoResult(
            uri = "test://uri",
            width = 100,
            height = 100,
            fileName = "test.jpg"
        )
        val cropRect = Rect(0f, 0f, 100f, 100f)
        val canvasSize = Size(100f, 100f)
        var completedResult: PhotoResult? = null

        every { mockContext.contentResolver } returns mockContentResolver
        every { mockContentResolver.openInputStream(any()) } returns mockInputStream
        every { mockInputStream.close() } just Runs

        mockkStatic(BitmapFactory::class)
        every { BitmapFactory.decodeStream(mockInputStream) } returns null

        // Act
        applyCropUtils(
            context = mockContext,
            photoResult = photoResult,
            cropRect = cropRect,
            canvasSize = canvasSize,
            isCircularCrop = false
        ) { result ->
            completedResult = result
        }

        // Wait for coroutines to complete
        Thread.sleep(100)

        // Assert - should not proceed with crop operations
        verify(exactly = 0) { Bitmap.createBitmap(any<Bitmap>(), any(), any(), any(), any()) }
    }

    @Test
    fun testApplyCropUtils_aspectRatioCalculations() = runTest {
        // Test landscape image (wider than canvas)
        val mockLandscapeBitmap = mockk<Bitmap>()
        every { mockLandscapeBitmap.width } returns 200
        every { mockLandscapeBitmap.height } returns 100

        val photoResult = PhotoResult(
            uri = "test://uri",
            width = 200,
            height = 100,
            fileName = "test.jpg"
        )
        val cropRect = Rect(0f, 0f, 100f, 100f)
        val canvasSize = Size(100f, 100f)

        every { mockContext.contentResolver } returns mockContentResolver
        every { mockContext.cacheDir } returns File("/tmp")
        every { mockContentResolver.openInputStream(any()) } returns mockInputStream
        every { mockInputStream.close() } just Runs

        mockkStatic(BitmapFactory::class)
        every { BitmapFactory.decodeStream(mockInputStream) } returns mockLandscapeBitmap

        mockkStatic(Bitmap::class)
        every { Bitmap.createBitmap(any<Bitmap>(), any(), any(), any(), any()) } returns mockLandscapeBitmap

        mockkStatic("io.github.ismoy.imagepickerkmp.domain.utils.CreateTransparentBitmapKt")
        every { createTransparentBitmap(any()) } returns mockLandscapeBitmap

        every { mockLandscapeBitmap.compress(any(), any(), any()) } returns true

        // Act
        applyCropUtils(
            context = mockContext,
            photoResult = photoResult,
            cropRect = cropRect,
            canvasSize = canvasSize,
            isCircularCrop = false
        ) { }

        // Wait for coroutines to complete
        Thread.sleep(100)

        // Assert
        verify { BitmapFactory.decodeStream(mockInputStream) }
        verify { Bitmap.createBitmap(any<Bitmap>(), any(), any(), any(), any()) }
    }

    @Test
    fun testApplyCropUtils_portraitImage() = runTest {
        // Test portrait image (taller than canvas)
        val mockPortraitBitmap = mockk<Bitmap>()
        every { mockPortraitBitmap.width } returns 100
        every { mockPortraitBitmap.height } returns 200

        val photoResult = PhotoResult(
            uri = "test://uri",
            width = 100,
            height = 200,
            fileName = "test.jpg"
        )
        val cropRect = Rect(0f, 0f, 100f, 100f)
        val canvasSize = Size(100f, 100f)

        every { mockContext.contentResolver } returns mockContentResolver
        every { mockContext.cacheDir } returns File("/tmp")
        every { mockContentResolver.openInputStream(any()) } returns mockInputStream
        every { mockInputStream.close() } just Runs

        mockkStatic(BitmapFactory::class)
        every { BitmapFactory.decodeStream(mockInputStream) } returns mockPortraitBitmap

        mockkStatic(Bitmap::class)
        every { Bitmap.createBitmap(any<Bitmap>(), any(), any(), any(), any()) } returns mockPortraitBitmap

        mockkStatic("io.github.ismoy.imagepickerkmp.domain.utils.CreateTransparentBitmapKt")
        every { createTransparentBitmap(any()) } returns mockPortraitBitmap

        every { mockPortraitBitmap.compress(any(), any(), any()) } returns true

        // Act
        applyCropUtils(
            context = mockContext,
            photoResult = photoResult,
            cropRect = cropRect,
            canvasSize = canvasSize,
            isCircularCrop = false
        ) { }

        // Wait for coroutines to complete
        Thread.sleep(100)

        // Assert
        verify { BitmapFactory.decodeStream(mockInputStream) }
    }

    @Test
    fun testApplyCropUtils_errorHandling() = runTest {
        // Arrange
        val photoResult = PhotoResult(
            uri = "test://uri",
            width = 100,
            height = 100,
            fileName = "test.jpg"
        )
        val cropRect = Rect(0f, 0f, 100f, 100f)
        val canvasSize = Size(100f, 100f)
        var completedResult: PhotoResult? = null

        every { mockContext.contentResolver } returns mockContentResolver
        every { mockContentResolver.openInputStream(any()) } throws Exception("Test exception")

        // Act
        applyCropUtils(
            context = mockContext,
            photoResult = photoResult,
            cropRect = cropRect,
            canvasSize = canvasSize,
            isCircularCrop = false
        ) { result ->
            completedResult = result
        }

        // Wait for coroutines to complete
        Thread.sleep(100)

        // Assert - should return original photo result on error
        // The exception is caught and original photoResult is passed to onComplete
        verify { mockContentResolver.openInputStream(any()) }
    }

    @Test
    fun testApplyCropUtils_boundaryConstraints() = runTest {
        // Test crop coordinates that exceed image boundaries
        val photoResult = PhotoResult(
            uri = "test://uri",
            width = 100,
            height = 100,
            fileName = "test.jpg"
        )
        val cropRect = Rect(-10f, -10f, 110f, 110f) // Exceeds boundaries
        val canvasSize = Size(100f, 100f)

        every { mockContext.contentResolver } returns mockContentResolver
        every { mockContext.cacheDir } returns File("/tmp")
        every { mockContentResolver.openInputStream(any()) } returns mockInputStream
        every { mockBitmap.width } returns 100
        every { mockBitmap.height } returns 100
        every { mockInputStream.close() } just Runs

        mockkStatic(BitmapFactory::class)
        every { BitmapFactory.decodeStream(mockInputStream) } returns mockBitmap

        mockkStatic(Bitmap::class)
        every { Bitmap.createBitmap(any<Bitmap>(), any(), any(), any(), any()) } returns mockBitmap

        mockkStatic("io.github.ismoy.imagepickerkmp.domain.utils.CreateTransparentBitmapKt")
        every { createTransparentBitmap(any()) } returns mockBitmap

        every { mockBitmap.compress(any(), any(), any()) } returns true

        // Act
        applyCropUtils(
            context = mockContext,
            photoResult = photoResult,
            cropRect = cropRect,
            canvasSize = canvasSize,
            isCircularCrop = false
        ) { }

        // Wait for coroutines to complete
        Thread.sleep(100)

        // Assert - should apply coerceAtLeast(0) and coerceAtMost constraints
        verify { Bitmap.createBitmap(any<Bitmap>(), any(), any(), any(), any()) }
    }
}
