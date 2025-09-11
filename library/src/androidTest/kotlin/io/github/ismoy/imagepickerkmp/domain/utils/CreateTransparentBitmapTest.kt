package io.github.ismoy.imagepickerkmp.domain.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import io.mockk.*
import kotlin.test.Test
import kotlin.test.assertEquals
import androidx.core.graphics.createBitmap

class CreateTransparentBitmapTest {

    @Test
    fun testCreateTransparentBitmap_basicFunctionality() {
        // Arrange
        val mockInputBitmap = mockk<Bitmap>()
        val mockOutputBitmap = mockk<Bitmap>()

        every { mockInputBitmap.width } returns 100
        every { mockInputBitmap.height } returns 150

        mockkStatic("androidx.core.graphics.BitmapKt")
        every { createBitmap(100, 150) } returns mockOutputBitmap

        mockkConstructor(Canvas::class)
        every { anyConstructed<Canvas>().drawBitmap(mockInputBitmap, 0f, 0f, null) } just Runs

        // Act
        val result = createTransparentBitmap(mockInputBitmap)

        // Assert
        assertEquals(mockOutputBitmap, result)
        verify { createBitmap(100, 150) }
        verify { anyConstructed<Canvas>().drawBitmap(mockInputBitmap, 0f, 0f, null) }
    }

    @Test
    fun testCreateTransparentBitmap_squareBitmap() {
        // Arrange
        val mockInputBitmap = mockk<Bitmap>()
        val mockOutputBitmap = mockk<Bitmap>()

        every { mockInputBitmap.width } returns 200
        every { mockInputBitmap.height } returns 200

        mockkStatic("androidx.core.graphics.BitmapKt")
        every { createBitmap(200, 200) } returns mockOutputBitmap

        mockkConstructor(Canvas::class)
        every { anyConstructed<Canvas>().drawBitmap(mockInputBitmap, 0f, 0f, null) } just Runs

        // Act
        val result = createTransparentBitmap(mockInputBitmap)

        // Assert
        assertEquals(mockOutputBitmap, result)
        verify { createBitmap(200, 200) }
    }

    @Test
    fun testCreateTransparentBitmap_landscapeBitmap() {
        // Arrange
        val mockInputBitmap = mockk<Bitmap>()
        val mockOutputBitmap = mockk<Bitmap>()

        every { mockInputBitmap.width } returns 300
        every { mockInputBitmap.height } returns 200

        mockkStatic("androidx.core.graphics.BitmapKt")
        every { createBitmap(300, 200) } returns mockOutputBitmap

        mockkConstructor(Canvas::class)
        every { anyConstructed<Canvas>().drawBitmap(mockInputBitmap, 0f, 0f, null) } just Runs

        // Act
        val result = createTransparentBitmap(mockInputBitmap)

        // Assert
        assertEquals(mockOutputBitmap, result)
        verify { createBitmap(300, 200) }
        verify { anyConstructed<Canvas>().drawBitmap(mockInputBitmap, 0f, 0f, null) }
    }

    @Test
    fun testCreateTransparentBitmap_portraitBitmap() {
        // Arrange
        val mockInputBitmap = mockk<Bitmap>()
        val mockOutputBitmap = mockk<Bitmap>()

        every { mockInputBitmap.width } returns 150
        every { mockInputBitmap.height } returns 250

        mockkStatic("androidx.core.graphics.BitmapKt")
        every { createBitmap(150, 250) } returns mockOutputBitmap

        mockkConstructor(Canvas::class)
        every { anyConstructed<Canvas>().drawBitmap(mockInputBitmap, 0f, 0f, null) } just Runs

        // Act
        val result = createTransparentBitmap(mockInputBitmap)

        // Assert
        assertEquals(mockOutputBitmap, result)
        verify { createBitmap(150, 250) }
    }

    @Test
    fun testCreateTransparentBitmap_verySmallBitmap() {
        // Arrange
        val mockInputBitmap = mockk<Bitmap>()
        val mockOutputBitmap = mockk<Bitmap>()

        every { mockInputBitmap.width } returns 1
        every { mockInputBitmap.height } returns 1

        mockkStatic("androidx.core.graphics.BitmapKt")
        every { createBitmap(1, 1) } returns mockOutputBitmap

        mockkConstructor(Canvas::class)
        every { anyConstructed<Canvas>().drawBitmap(mockInputBitmap, 0f, 0f, null) } just Runs

        // Act
        val result = createTransparentBitmap(mockInputBitmap)

        // Assert
        assertEquals(mockOutputBitmap, result)
        verify { createBitmap(1, 1) }
        verify { anyConstructed<Canvas>().drawBitmap(mockInputBitmap, 0f, 0f, null) }
    }

    @Test
    fun testCreateTransparentBitmap_canvasConstructor() {
        // Arrange
        val mockInputBitmap = mockk<Bitmap>()
        val mockOutputBitmap = mockk<Bitmap>()

        every { mockInputBitmap.width } returns 100
        every { mockInputBitmap.height } returns 100

        mockkStatic("androidx.core.graphics.BitmapKt")
        every { createBitmap(100, 100) } returns mockOutputBitmap

        mockkConstructor(Canvas::class)
        every { anyConstructed<Canvas>().drawBitmap(mockInputBitmap, 0f, 0f, null) } just Runs

        // Act
        val result = createTransparentBitmap(mockInputBitmap)

        // Assert
        verify(exactly = 1) { constructedWith<Canvas>(EqMatcher(mockOutputBitmap)) }
    }

    @Test
    fun testCreateTransparentBitmap_preservesDimensions() {
        // Test that output bitmap has same dimensions as input
        val mockInputBitmap = mockk<Bitmap>()
        val mockOutputBitmap = mockk<Bitmap>()

        every { mockInputBitmap.width } returns 456
        every { mockInputBitmap.height } returns 789

        mockkStatic("androidx.core.graphics.BitmapKt")
        every { createBitmap(456, 789) } returns mockOutputBitmap

        mockkConstructor(Canvas::class)
        every { anyConstructed<Canvas>().drawBitmap(mockInputBitmap, 0f, 0f, null) } just Runs

        // Act
        val result = createTransparentBitmap(mockInputBitmap)

        // Assert
        verify { createBitmap(456, 789) }
    }
}
