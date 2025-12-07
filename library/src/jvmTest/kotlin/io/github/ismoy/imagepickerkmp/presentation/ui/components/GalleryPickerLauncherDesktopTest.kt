package io.github.ismoy.imagepickerkmp.presentation.ui.components

import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GalleryPickerLauncherDesktopTest {

    @Test
    fun `test mime type extensions mapping`() {
        val jpegExtensions = getMimeTypeExtensions(MimeType.IMAGE_JPEG)
        assertTrue(jpegExtensions.contains("jpg"))
        assertTrue(jpegExtensions.contains("jpeg"))

        val pngExtensions = getMimeTypeExtensions(MimeType.IMAGE_PNG)
        assertTrue(pngExtensions.contains("png"))

        val allExtensions = getMimeTypeExtensions(MimeType.IMAGE_ALL)
        assertTrue(allExtensions.contains("jpg"))
        assertTrue(allExtensions.contains("png"))
        assertTrue(allExtensions.contains("gif"))
    }

    @Test
    fun `test gallery photo result creation`() {
        // Esta es una prueba básica de estructura
        val result = GalleryPhotoResult(
            uri = "file:///test/image.jpg",
            width = 1920,
            height = 1080,
            fileName = "image.jpg",
            fileSize = 500
        )

        assertEquals("file:///test/image.jpg", result.uri)
        assertEquals(1920, result.width)
        assertEquals(1080, result.height)
        assertEquals("image.jpg", result.fileName)
        assertEquals(500, result.fileSize)
    }

    // Función helper para obtener extensiones de tipos MIME
    private fun getMimeTypeExtensions(mimeType: MimeType): List<String> {
        return when (mimeType) {
            MimeType.IMAGE_JPEG -> listOf("jpg", "jpeg")
            MimeType.IMAGE_PNG -> listOf("png")
            MimeType.IMAGE_GIF -> listOf("gif")
            MimeType.IMAGE_WEBP -> listOf("webp")
            MimeType.IMAGE_BMP -> listOf("bmp")
            MimeType.IMAGE_HEIC -> listOf("heic")
            MimeType.IMAGE_HEIF -> listOf("heif")
            MimeType.IMAGE_ALL -> listOf("jpg", "jpeg", "png", "gif", "webp", "bmp", "heic", "heif")
            MimeType.APPLICATION_PDF -> listOf("pdf")
        }
    }
}
