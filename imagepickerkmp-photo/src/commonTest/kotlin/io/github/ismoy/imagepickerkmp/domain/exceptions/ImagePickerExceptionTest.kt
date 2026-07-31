package io.github.ismoy.imagepickerkmp.domain.exceptions

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Tests for the internal exception hierarchy: [PhotoCaptureException],
 * [PermissionDeniedException], [ImageProcessingException].
 * All are `internal` and live in the `io.github.ismoy.imagepickerkmp.picker` package,
 * so they are accessible from within the same module's test source set.
 */
class ImagePickerExceptionTest {

    @Test
    fun photoCaptureException_message_isStored() {
        val ex = io.github.ismoy.imagepickerkmp.picker.PhotoCaptureException("Camera error")
        assertEquals("Camera error", ex.message)
    }

    @Test
    fun photoCaptureException_isImagePickerException() {
        val ex = io.github.ismoy.imagepickerkmp.picker.PhotoCaptureException("err")
        assertIs<io.github.ismoy.imagepickerkmp.picker.ImagePickerException>(ex)
    }

    @Test
    fun photoCaptureException_isException() {
        val ex = io.github.ismoy.imagepickerkmp.picker.PhotoCaptureException("err")
        assertIs<Exception>(ex)
    }

    @Test
    fun photoCaptureException_cause_isNull_whenNotProvided() {
        val ex = io.github.ismoy.imagepickerkmp.picker.PhotoCaptureException("err")
        assertNull(ex.cause)
    }

    @Test
    fun photoCaptureException_cause_isStoredWhenProvided() {
        val cause = RuntimeException("original")
        val ex = io.github.ismoy.imagepickerkmp.picker.PhotoCaptureException("wrapped", cause)
        assertNotNull(ex.cause)
        assertEquals("original", ex.cause!!.message)
    }

    @Test
    fun permissionDeniedException_message_isStored() {
        val ex = io.github.ismoy.imagepickerkmp.picker.PermissionDeniedException("Permission denied")
        assertEquals("Permission denied", ex.message)
    }

    @Test
    fun permissionDeniedException_isImagePickerException() {
        val ex = io.github.ismoy.imagepickerkmp.picker.PermissionDeniedException("err")
        assertIs<io.github.ismoy.imagepickerkmp.picker.ImagePickerException>(ex)
    }

    @Test
    fun permissionDeniedException_cause_isNull_whenNotProvided() {
        val ex = io.github.ismoy.imagepickerkmp.picker.PermissionDeniedException("err")
        assertNull(ex.cause)
    }

    @Test
    fun permissionDeniedException_cause_isStoredWhenProvided() {
        val cause = SecurityException("sec")
        val ex = io.github.ismoy.imagepickerkmp.picker.PermissionDeniedException("denied", cause)
        assertEquals("sec", ex.cause!!.message)
    }

    @Test
    fun imageProcessingException_message_isStored() {
        val ex = io.github.ismoy.imagepickerkmp.picker.ImageProcessingException("Processing failed")
        assertEquals("Processing failed", ex.message)
    }

    @Test
    fun imageProcessingException_isImagePickerException() {
        val ex = io.github.ismoy.imagepickerkmp.picker.ImageProcessingException("err")
        assertIs<io.github.ismoy.imagepickerkmp.picker.ImagePickerException>(ex)
    }

    @Test
    fun imageProcessingException_cause_stored() {
        val cause = IllegalStateException("state")
        val ex = io.github.ismoy.imagepickerkmp.picker.ImageProcessingException("processing", cause)
        assertEquals("state", ex.cause!!.message)
    }

    @Test
    fun allThreeExceptions_areDistinctTypes() {
        val photo = io.github.ismoy.imagepickerkmp.picker.PhotoCaptureException("a")
        val perm  = io.github.ismoy.imagepickerkmp.picker.PermissionDeniedException("b")
        val proc  = io.github.ismoy.imagepickerkmp.picker.ImageProcessingException("c")
        assertIs<io.github.ismoy.imagepickerkmp.picker.PhotoCaptureException>(photo)
        assertIs<io.github.ismoy.imagepickerkmp.picker.PermissionDeniedException>(perm)
        assertIs<io.github.ismoy.imagepickerkmp.picker.ImageProcessingException>(proc)
    }
}
