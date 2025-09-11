package io.github.ismoy.imagepickerkmp.domain.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import io.mockk.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import androidx.core.graphics.createBitmap

class CreateCircularBitmapTest {

    @Test
    fun testCreateCircularBitmap_squareBitmap() {
        // Arrange
        val mockBitmap = mockk<Bitmap>()
        val mockOutputBitmap = mockk<Bitmap>()
        val mockCanvas = mockk<Canvas>()
        val mockPaint = mockk<Paint>()

        every { mockBitmap.width } returns 100
        every { mockBitmap.height } returns 100

        mockkStatic("androidx.core.graphics.BitmapKt")
        every { createBitmap(100, 100) } returns mockOutputBitmap

        mockkConstructor(Canvas::class)
        every { anyConstructed<Canvas>().drawOval(any(), any()) } just Runs
        every { anyConstructed<Canvas>().drawBitmap(any<Bitmap>(), any(), any(), any()) } just Runs

        mockkConstructor(Paint::class)
        every { anyConstructed<Paint>().isAntiAlias = any() } just Runs
        every { anyConstructed<Paint>().xfermode = any() } just Runs

        mockkConstructor(PorterDuffXfermode::class)

        // Act
        val result = createCircularBitmap(mockBitmap)

        // Assert
        assertEquals(mockOutputBitmap, result)
        verify { createBitmap(100, 100) }
    }

    @Test
    fun testCreateCircularBitmap_rectangularBitmap() {
        // Arrange
        val mockBitmap = mockk<Bitmap>()
        val mockOutputBitmap = mockk<Bitmap>()

        every { mockBitmap.width } returns 200
        every { mockBitmap.height } returns 100

        mockkStatic("androidx.core.graphics.BitmapKt")
        every { createBitmap(100, 100) } returns mockOutputBitmap

        mockkConstructor(Canvas::class)
        every { anyConstructed<Canvas>().drawOval(any(), any()) } just Runs
        every { anyConstructed<Canvas>().drawBitmap(any<Bitmap>(), any(), any(), any()) } just Runs

        mockkConstructor(Paint::class)
        every { anyConstructed<Paint>().isAntiAlias = any() } just Runs
        every { anyConstructed<Paint>().xfermode = any() } just Runs

        mockkConstructor(PorterDuffXfermode::class)

        // Act
        val result = createCircularBitmap(mockBitmap)

        // Assert
        assertEquals(mockOutputBitmap, result)
        // Should use minimum dimension (100) for square output
        verify { createBitmap(100, 100) }
    }

    @Test
    fun testCreateCircularBitmap_portraitBitmap() {
        // Arrange
        val mockBitmap = mockk<Bitmap>()
        val mockOutputBitmap = mockk<Bitmap>()

        every { mockBitmap.width } returns 100
        every { mockBitmap.height } returns 200

        mockkStatic("androidx.core.graphics.BitmapKt")
        every { createBitmap(100, 100) } returns mockOutputBitmap

        mockkConstructor(Canvas::class)
        every { anyConstructed<Canvas>().drawOval(any(), any()) } just Runs
        every { anyConstructed<Canvas>().drawBitmap(any<Bitmap>(), any(), any(), any()) } just Runs

        mockkConstructor(Paint::class)
        every { anyConstructed<Paint>().isAntiAlias = any() } just Runs
        every { anyConstructed<Paint>().xfermode = any() } just Runs

        mockkConstructor(PorterDuffXfermode::class)

        // Act
        val result = createCircularBitmap(mockBitmap)

        // Assert
        assertEquals(mockOutputBitmap, result)
        // Should use minimum dimension (100) for square output
        verify { createBitmap(100, 100) }
    }

    @Test
    fun testCreateCircularBitmap_paintConfiguration() {
        // Arrange
        val mockBitmap = mockk<Bitmap>()
        val mockOutputBitmap = mockk<Bitmap>()

        every { mockBitmap.width } returns 100
        every { mockBitmap.height } returns 100

        mockkStatic("androidx.core.graphics.BitmapKt")
        every { createBitmap(100, 100) } returns mockOutputBitmap

        mockkConstructor(Canvas::class)
        every { anyConstructed<Canvas>().drawOval(any(), any()) } just Runs
        every { anyConstructed<Canvas>().drawBitmap(any<Bitmap>(), any(), any(), any()) } just Runs

        mockkConstructor(Paint::class)
        every { anyConstructed<Paint>().isAntiAlias = any() } just Runs
        every { anyConstructed<Paint>().xfermode = any() } just Runs

        mockkConstructor(PorterDuffXfermode::class)
        
        // Act
        val result = createCircularBitmap(mockBitmap)

        // Assert
        verify { anyConstructed<Paint>().isAntiAlias = true }
        verify { anyConstructed<Paint>().xfermode = any<PorterDuffXfermode>() }
        verify { anyConstructed<PorterDuffXfermode>() }
    }

    @Test
    fun testCreateCircularBitmap_canvasOperations() {
        // Arrange
        val mockBitmap = mockk<Bitmap>()
        val mockOutputBitmap = mockk<Bitmap>()

        every { mockBitmap.width } returns 100
        every { mockBitmap.height } returns 100

        mockkStatic("androidx.core.graphics.BitmapKt")
        every { createBitmap(100, 100) } returns mockOutputBitmap

        mockkConstructor(Canvas::class)
        every { anyConstructed<Canvas>().drawOval(any(), any()) } just Runs
        every { anyConstructed<Canvas>().drawBitmap(any<Bitmap>(), any(), any(), any()) } just Runs

        mockkConstructor(Paint::class)
        every { anyConstructed<Paint>().isAntiAlias = any() } just Runs
        every { anyConstructed<Paint>().xfermode = any() } just Runs

        mockkConstructor(PorterDuffXfermode::class)

        // Act
        val result = createCircularBitmap(mockBitmap)

        // Assert
        verify { anyConstructed<Canvas>().drawOval(any(), any()) }
        verify { anyConstructed<Canvas>().drawBitmap(mockBitmap, any(), any(), any()) }
    }

    @Test
    fun testCreateCircularBitmap_verySmallBitmap() {
        // Arrange
        val mockBitmap = mockk<Bitmap>()
        val mockOutputBitmap = mockk<Bitmap>()

        every { mockBitmap.width } returns 1
        every { mockBitmap.height } returns 1

        mockkStatic("androidx.core.graphics.BitmapKt")
        every { createBitmap(1, 1) } returns mockOutputBitmap

        mockkConstructor(Canvas::class)
        every { anyConstructed<Canvas>().drawOval(any(), any()) } just Runs
        every { anyConstructed<Canvas>().drawBitmap(any<Bitmap>(), any(), any(), any()) } just Runs

        mockkConstructor(Paint::class)
        every { anyConstructed<Paint>().isAntiAlias = any() } just Runs
        every { anyConstructed<Paint>().xfermode = any() } just Runs

        mockkConstructor(PorterDuffXfermode::class)

        // Act
        val result = createCircularBitmap(mockBitmap)

        // Assert
        assertEquals(mockOutputBitmap, result)
        verify { createBitmap(1, 1) }
    }
}
