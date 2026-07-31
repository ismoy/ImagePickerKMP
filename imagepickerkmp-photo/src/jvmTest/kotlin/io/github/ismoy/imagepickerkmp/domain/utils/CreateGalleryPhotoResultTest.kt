package io.github.ismoy.imagepickerkmp.domain.utils

import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.Rule
import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CreateGalleryPhotoResultTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private fun createPngFile(name: String = "test.png", width: Int = 100, height: Int = 80): File {
        val file = tempFolder.newFile(name)
        val img = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        ImageIO.write(img, "png", file)
        return file
    }

    @Test
    fun validPngFile_returnsGalleryPhotoResult() {
        val file = createPngFile("image.png", 200, 150)
        val result = createGalleryPhotoResult(file)
        assertNotNull(result)
    }

    @Test
    fun validPngFile_uriIsFileUri() {
        val file = createPngFile()
        val result = createGalleryPhotoResult(file)!!
        assertTrue(result.uri.startsWith("file:/"), "URI should start with 'file:/', got ${result.uri}")
    }

    @Test
    fun validPngFile_widthAndHeightAreCorrect() {
        val file = createPngFile("sized.png", 320, 240)
        val result = createGalleryPhotoResult(file)!!
        assertEquals(320, result.width)
        assertEquals(240, result.height)
    }

    @Test
    fun validPngFile_fileNameIsCorrect() {
        val file = createPngFile("myphoto.png")
        val result = createGalleryPhotoResult(file)!!
        assertEquals("myphoto.png", result.fileName)
    }

    @Test
    fun validPngFile_fileSizeIsNonNegative() {
        val file = createPngFile()
        val result = createGalleryPhotoResult(file)!!
        assertTrue(result.fileSize!! >= 0L)
    }

    @Test
    fun nonExistentFile_returnsNull() {
        val file = File(tempFolder.root, "nonexistent.png")
        val result = createGalleryPhotoResult(file)
        assertNull(result)
    }

    @Test
    fun directory_returnsNull() {
        val dir = tempFolder.newFolder("somedir")
        val result = createGalleryPhotoResult(dir)
        assertNull(result)
    }

    @Test
    fun nonImageFile_textFile_returnsNull() {
        val file = tempFolder.newFile("readme.txt")
        file.writeText("this is not an image")
        val result = createGalleryPhotoResult(file)
        assertNull(result)
    }

    @Test
    fun emptyFile_returnsNull() {
        val file = tempFolder.newFile("empty.png")
        // File exists but has no content — ImageIO.read returns null
        val result = createGalleryPhotoResult(file)
        assertNull(result)
    }

    @Test
    fun smallImage_1x1_returnsResult() {
        val file = createPngFile("tiny.png", 1, 1)
        val result = createGalleryPhotoResult(file)
        assertNotNull(result)
        assertEquals(1, result!!.width)
        assertEquals(1, result.height)
    }

    @Test
    fun largeImage_returnsResult() {
        val file = createPngFile("large.png", 1920, 1080)
        val result = createGalleryPhotoResult(file)
        assertNotNull(result)
        assertEquals(1920, result!!.width)
        assertEquals(1080, result.height)
    }
}
