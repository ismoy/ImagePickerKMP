package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ImagePickerLauncherTest {
    @Test
    fun testOnPhotoCapturedIsCalled() {
        val captured = mutableListOf<GalleryPhotoResult>()
        val fakeResult = GalleryPhotoResult(
            uri = "file:///tmp/photo2.jpg",
            width = 300,
            height = 400,
            fileName = "photo2.jpg",
            fileSize = 54321L
        )
        val launcher = FakeImagePickerLauncher(
            onPhotoCaptured = { result -> captured.add(result) },
            onError = { }
        )
        launcher.simulateCapture(fakeResult)
        assertEquals(1, captured.size)
        assertEquals(fakeResult, captured[0])
    }

    @Test
    fun testOnErrorIsCalledOnFailure() {
        var error: Exception? = null
        val launcher = FakeImagePickerLauncher(
            onPhotoCaptured = { },
            onError = { e -> error = e }
        )
        launcher.simulateError(Exception("Capture error"))
        assertNotNull(error)
        assertEquals("Capture error", error?.message)
    }
}

class FakeImagePickerLauncher(
    val onPhotoCaptured: (GalleryPhotoResult) -> Unit,
    val onError: (Exception) -> Unit
) {
    fun simulateCapture(result: GalleryPhotoResult) {
        onPhotoCaptured(result)
    }
    fun simulateError(e: Exception) {
        onError(e)
    }
}

// TODO: Re-enable this test once Compose testing is properly configured
/*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ImagePickerLauncherTest {
    // Test implementation will be added back once Compose testing is configured
}
*/ 