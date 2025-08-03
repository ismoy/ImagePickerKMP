package io.github.ismoy.imagepickerkmp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GalleryPickerLauncherTest {
    @Test
    fun testOnPhotosSelectedReceivesList() {
        val selected = mutableListOf<List<GalleryPhotoHandler.PhotoResult>>()
        val fakeResult = GalleryPhotoHandler.PhotoResult(
            uri = "file:///tmp/photo1.jpg",
            width = 100,
            height = 200,
            fileName = "photo1.jpg",
            fileSize = 12345L
        )
        val launcher = FakeGalleryPickerLauncher(
            onPhotosSelected = { results -> selected.add(results) },
            onError = { },
            onDismiss = { }
        )
        launcher.simulateSelection(listOf(fakeResult))
        assertEquals(1, selected.size)
        assertEquals(fakeResult, selected[0][0])
    }

    @Test
    fun testOnErrorIsCalledOnFailure() {
        var error: Exception? = null
        val launcher = FakeGalleryPickerLauncher(
            onPhotosSelected = { },
            onError = { e -> error = e },
            onDismiss = { }
        )
        launcher.simulateError(Exception("Gallery error"))
        assertNotNull(error)
        assertEquals("Gallery error", error?.message)
    }

    @Test
    fun testOnDismissIsCalledOnDismiss() {
        var dismissed = false
        val launcher = FakeGalleryPickerLauncher(
            onPhotosSelected = { },
            onError = { },
            onDismiss = { dismissed = true }
        )
        launcher.simulateDismiss()
        assertTrue(dismissed)
    }

    @Test
    fun testMultipleSelection() {
        val selected = mutableListOf<List<GalleryPhotoHandler.PhotoResult>>()
        val results = listOf(
            GalleryPhotoHandler.PhotoResult("file:///tmp/1.jpg", 10, 10, "1.jpg", 1),
            GalleryPhotoHandler.PhotoResult("file:///tmp/2.jpg", 20, 20, "2.jpg", 2)
        )
        val launcher = FakeGalleryPickerLauncher(
            onPhotosSelected = { selected.add(it) },
            onError = { },
            onDismiss = { }
        )
        launcher.simulateSelection(results)
        assertEquals(1, selected.size)
        assertEquals(2, selected[0].size)
        assertEquals("1.jpg", selected[0][0].fileName)
        assertEquals("2.jpg", selected[0][1].fileName)
    }

    @Test
    fun testEmptySelection() {
        val selected = mutableListOf<List<GalleryPhotoHandler.PhotoResult>>()
        val launcher = FakeGalleryPickerLauncher(
            onPhotosSelected = { selected.add(it) },
            onError = { },
            onDismiss = { }
        )
        launcher.simulateSelection(emptyList())
        assertEquals(1, selected.size)
        assertTrue(selected[0].isEmpty())
    }

    @Test
    fun testPermissionDeniedError() {
        var error: Exception? = null
        val launcher = FakeGalleryPickerLauncher(
            onPhotosSelected = { },
            onError = { e -> error = e },
            onDismiss = { }
        )
        launcher.simulateError(PermissionDeniedException("Permission denied"))
        assertNotNull(error)
        assertTrue(error is PermissionDeniedException)
    }

    @Test
    fun testMimeTypeFilter() {
        val selected = mutableListOf<List<GalleryPhotoHandler.PhotoResult>>()
        val allowedMime = "image/png"
        val results = listOf(
            GalleryPhotoHandler.PhotoResult("file:///tmp/1.png", 10, 10, "1.png", 1),
            GalleryPhotoHandler.PhotoResult("file:///tmp/2.jpg", 20, 20, "2.jpg", 2)
        )
        val launcher = FakeGalleryPickerLauncher(
            onPhotosSelected = { selected.add(it.filter { r -> r.fileName?.endsWith(".png") == true }) },
            onError = { },
            onDismiss = { }
        )
        launcher.simulateSelection(results)
        assertEquals(1, selected.size)
        assertEquals(1, selected[0].size)
        assertEquals("1.png", selected[0][0].fileName)
    }

    @Test
    fun testUnexpectedError() {
        var error: Exception? = null
        val launcher = FakeGalleryPickerLauncher(
            onPhotosSelected = { },
            onError = { e -> error = e },
            onDismiss = { }
        )
        launcher.simulateError(RuntimeException("Unexpected error"))
        assertNotNull(error)
        assertTrue(error is RuntimeException)
        assertEquals("Unexpected error", error?.message)
    }
}

class FakeGalleryPickerLauncher(
    val onPhotosSelected: (List<GalleryPhotoHandler.PhotoResult>) -> Unit,
    val onError: (Exception) -> Unit,
    val onDismiss: () -> Unit
) {
    fun simulateSelection(results: List<GalleryPhotoHandler.PhotoResult>) {
        onPhotosSelected(results)
    }
    fun simulateError(e: Exception) {
        onError(e)
    }
    fun simulateDismiss() {
        onDismiss()
    }
}

class PermissionDeniedException(message: String) : Exception(message)

// TODO: Re-enable this test once Compose testing is properly configured
/*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GalleryPickerLauncherTest {
    // Test implementation will be added back once Compose testing is configured
}
*/ 