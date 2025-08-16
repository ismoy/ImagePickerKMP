package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants
import kotlin.experimental.ExperimentalNativeApi
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalNativeApi::class)
class ImagePickerUiConstantsTest {
    
    @Test
    fun `orientation constants should have correct values`() {
        assertEquals(90f, ImagePickerUiConstants.ORIENTATION_ROTATE_90)
        assertEquals(180f, ImagePickerUiConstants.ORIENTATION_ROTATE_180)
        assertEquals(270f, ImagePickerUiConstants.ORIENTATION_ROTATE_270)
    }
    
    @Test
    fun `flip horizontal constants should have correct values`() {
        assertEquals(-1f, ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_X)
        assertEquals(1f, ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_Y)
    }
    
    @Test
    fun `flip vertical constants should have correct values`() {
        assertEquals(1f, ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_X)
        assertEquals(-1f, ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_Y)
    }
    
    @Test
    fun `rotation values should be multiples of 90`() {
        val rotations = listOf(
            ImagePickerUiConstants.ORIENTATION_ROTATE_90,
            ImagePickerUiConstants.ORIENTATION_ROTATE_180,
            ImagePickerUiConstants.ORIENTATION_ROTATE_270
        )
        
        rotations.forEach { rotation ->
            assertEquals(0f, rotation % 90f, "Rotation $rotation should be multiple of 90")
        }
    }
    
    @Test
    fun `flip values should be either 1 or -1`() {
        val flipValues = listOf(
            ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_X,
            ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_Y,
            ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_X,
            ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_Y
        )
        
        flipValues.forEach { value ->
            assert(value == 1f || value == -1f) {
                "Flip value $value should be either 1.0 or -1.0"
            }
        }
    }
    
    @Test
    fun `constants should be accessible as static values`() {
        // Test that constants can be accessed without instantiation
        val rotate90 = ImagePickerUiConstants.ORIENTATION_ROTATE_90
        val rotate180 = ImagePickerUiConstants.ORIENTATION_ROTATE_180
        val rotate270 = ImagePickerUiConstants.ORIENTATION_ROTATE_270
        
        val flipHX = ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_X
        val flipHY = ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_Y
        val flipVX = ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_X
        val flipVY = ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_Y
        
        // All should be accessible and have expected types
        assert(rotate90 is Float)
        assert(rotate180 is Float)
        assert(rotate270 is Float)
        assert(flipHX is Float)
        assert(flipHY is Float)
        assert(flipVX is Float)
        assert(flipVY is Float)
    }
    
    @Test
    fun `rotation sequence should be logical`() {
        val rotations = listOf(
            ImagePickerUiConstants.ORIENTATION_ROTATE_90,
            ImagePickerUiConstants.ORIENTATION_ROTATE_180,
            ImagePickerUiConstants.ORIENTATION_ROTATE_270
        )
        
        // Should be in ascending order
        assertEquals(90f, rotations[0])
        assertEquals(180f, rotations[1])
        assertEquals(270f, rotations[2])
        
        // Each should be 90 degrees more than the previous
        assertEquals(90f, rotations[1] - rotations[0])
        assertEquals(90f, rotations[2] - rotations[1])
    }
    
    @Test
    fun `horizontal flip should invert X but not Y`() {
        assertEquals(-1f, ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_X)
        assertEquals(1f, ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_Y)
    }
    
    @Test
    fun `vertical flip should invert Y but not X`() {
        assertEquals(1f, ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_X)
        assertEquals(-1f, ImagePickerUiConstants.ORIENTATION_FLIP_VERTICAL_Y)
    }
    
    @Test
    fun `constants should be immutable`() {
        // Test that constants maintain their values
        val initialRotate90 = ImagePickerUiConstants.ORIENTATION_ROTATE_90
        val initialFlipHX = ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_X
        
        // Access them multiple times
        repeat(5) {
            assertEquals(initialRotate90, ImagePickerUiConstants.ORIENTATION_ROTATE_90)
            assertEquals(initialFlipHX, ImagePickerUiConstants.ORIENTATION_FLIP_HORIZONTAL_X)
        }
    }
}