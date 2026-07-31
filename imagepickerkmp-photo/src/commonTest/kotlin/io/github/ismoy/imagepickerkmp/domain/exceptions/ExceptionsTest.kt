package io.github.ismoy.imagepickerkmp.domain.exceptions

import io.github.ismoy.imagepickerkmp.picker.ImagePickerResult
import io.github.ismoy.imagepickerkmp.picker.PhotoResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Tests for exception-carrying [ImagePickerResult.Error] and sealed hierarchy integration. */
class ExceptionsTest {

    // ── ImagePickerResult.Error stores exception ──────────────────────────────

    @Test
    fun error_result_storesRuntimeException() {
        val ex = RuntimeException("Camera unavailable")
        val result = ImagePickerResult.Error(ex)
        assertEquals("Camera unavailable", result.exception.message)
    }

    @Test
    fun error_result_storesIllegalArgumentException() {
        val ex = IllegalArgumentException("Invalid URI")
        val result = ImagePickerResult.Error(ex)
        assertIs<IllegalArgumentException>(result.exception)
        assertEquals("Invalid URI", result.exception.message)
    }

    @Test
    fun error_result_exception_canHaveCause() {
        val cause = RuntimeException("Original cause")
        val wrapper = Exception("Wrapped", cause)
        val result = ImagePickerResult.Error(wrapper)
        assertNotNull(result.exception.cause)
        assertEquals("Original cause", result.exception.cause!!.message)
    }

    @Test
    fun error_result_exception_nullMessage_isAllowed() {
        val ex = RuntimeException()
        val result = ImagePickerResult.Error(ex)
        assertNull(result.exception.message)
    }

    @Test
    fun error_result_equality_sameException() {
        val ex = RuntimeException("fail")
        assertEquals(ImagePickerResult.Error(ex), ImagePickerResult.Error(ex))
    }

    @Test
    fun error_result_equality_differentExceptions_notEqual() {
        val a = ImagePickerResult.Error(RuntimeException("A"))
        val b = ImagePickerResult.Error(RuntimeException("B"))
        assertTrue(a != b)
    }

    // ── Sealed hierarchy sealed-when contract ─────────────────────────────────

    @Test
    fun sealedWhen_allVariants_coveredWithoutElse() {
        val results: List<ImagePickerResult> = listOf(
            ImagePickerResult.Idle,
            ImagePickerResult.Loading,
            ImagePickerResult.Dismissed,
            ImagePickerResult.Success(emptyList()),
            ImagePickerResult.Error(RuntimeException())
        )
        val labels = results.map { result ->
            when (result) {
                is ImagePickerResult.Idle      -> "idle"
                is ImagePickerResult.Loading   -> "loading"
                is ImagePickerResult.Dismissed -> "dismissed"
                is ImagePickerResult.Success   -> "success"
                is ImagePickerResult.Error     -> "error"
            }
        }
        assertEquals(listOf("idle", "loading", "dismissed", "success", "error"), labels)
    }

    // ── Success.first helper ──────────────────────────────────────────────────

    @Test
    fun success_emptyList_firstIsNull() {
        assertNull(ImagePickerResult.Success(emptyList()).first)
    }

    @Test
    fun success_singlePhoto_firstReturnsIt() {
        val photo = PhotoResult(uri = "content://media/1")
        val result = ImagePickerResult.Success(listOf(photo))
        assertEquals(photo, result.first)
    }

    @Test
    fun success_multiplePhotos_firstReturnsFirstElement() {
        val photos = listOf(
            PhotoResult("content://1"),
            PhotoResult("content://2"),
            PhotoResult("content://3")
        )
        assertEquals("content://1", ImagePickerResult.Success(photos).first?.uri)
    }

    // ── Object variants are singletons ────────────────────────────────────────

    @Test
    fun idle_isSameObjectAcrossReferences() {
        val a: ImagePickerResult = ImagePickerResult.Idle
        val b: ImagePickerResult = ImagePickerResult.Idle
        assertTrue(a === b)
    }

    @Test
    fun loading_isSameObjectAcrossReferences() {
        val a: ImagePickerResult = ImagePickerResult.Loading
        val b: ImagePickerResult = ImagePickerResult.Loading
        assertTrue(a === b)
    }

    @Test
    fun dismissed_isSameObjectAcrossReferences() {
        val a: ImagePickerResult = ImagePickerResult.Dismissed
        val b: ImagePickerResult = ImagePickerResult.Dismissed
        assertTrue(a === b)
    }
}
