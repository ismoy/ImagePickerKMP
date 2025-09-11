package io.github.ismoy.imagepickerkmp.domain.config

import kotlin.test.Test
import kotlin.test.assertEquals

class ImagePickerUiConstantsTest {

    @Test
    fun testFlashToggleConstants() {
        assertEquals(50, ImagePickerUiConstants.FlashToggleCornerRadius)
    }

    @Test
    fun testConfirmationCardConstants() {
        assertEquals(1f, ImagePickerUiConstants.ConfirmationCardImageAspectRatio)
    }

    @Test
    fun testOrientationConstants() {
        assertEquals(90f, ImagePickerUiConstants.ORIENTATION_ROTATE_90)
        assertEquals(180f, ImagePickerUiConstants.ORIENTATION_ROTATE_180)
        assertEquals(270f, ImagePickerUiConstants.ORIENTATION_ROTATE_270)
        assertEquals(-1f, ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_X)
        assertEquals(1f, ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_Y)
        assertEquals(1f, ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_X)
        assertEquals(-1f, ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_Y)
    }

    @Test
    fun testSystemAndTimingConstants() {
        assertEquals(10.0, ImagePickerUiConstants.SYSTEM_VERSION_10)
        assertEquals(60L, ImagePickerUiConstants.DELAY_TO_TAKE_PHOTO)
        assertEquals(30L, ImagePickerUiConstants.SELECTION_LIMIT)
    }

    @Test
    fun testRotationAngleValues() {
        // Test that rotation angles are valid
        assertEquals(90f, ImagePickerUiConstants.ORIENTATION_ROTATE_90)
        assertEquals(180f, ImagePickerUiConstants.ORIENTATION_ROTATE_180)
        assertEquals(270f, ImagePickerUiConstants.ORIENTATION_ROTATE_270)
        
        // Test that angles are in expected range
        assert(ImagePickerUiConstants.ORIENTATION_ROTATE_90 >= 0f && ImagePickerUiConstants.ORIENTATION_ROTATE_90 <= 360f)
        assert(ImagePickerUiConstants.ORIENTATION_ROTATE_180 >= 0f && ImagePickerUiConstants.ORIENTATION_ROTATE_180 <= 360f)
        assert(ImagePickerUiConstants.ORIENTATION_ROTATE_270 >= 0f && ImagePickerUiConstants.ORIENTATION_ROTATE_270 <= 360f)
    }

    @Test
    fun testFlipValues() {
        // Test flip values are either -1 or 1
        assert(ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_X == -1f || ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_X == 1f)
        assert(ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_Y == -1f || ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_Y == 1f)
        assert(ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_X == -1f || ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_X == 1f)
        assert(ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_Y == -1f || ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_Y == 1f)
    }

    @Test
    fun testTimingValues() {
        // Test that timing values are positive
        assert(ImagePickerUiConstants.DELAY_TO_TAKE_PHOTO > 0L)
        assert(ImagePickerUiConstants.SELECTION_LIMIT > 0L)
        assert(ImagePickerUiConstants.SYSTEM_VERSION_10 > 0.0)
    }

    @Test
    fun testCornerRadiusValues() {
        // Test that corner radius values are reasonable
        assert(ImagePickerUiConstants.FlashToggleCornerRadius > 0)
        assert(ImagePickerUiConstants.FlashToggleCornerRadius <= 100) // Reasonable upper bound
    }

    @Test
    fun testAspectRatioValue() {
        // Test that aspect ratio is 1:1 (square)
        assertEquals(1f, ImagePickerUiConstants.ConfirmationCardImageAspectRatio)
        assert(ImagePickerUiConstants.ConfirmationCardImageAspectRatio > 0f)
    }
}
