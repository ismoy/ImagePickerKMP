package io.github.ismoy.imagepickerkmp.domain.utils

import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import java.io.File
import javax.imageio.ImageIO

fun createGalleryPhotoResult(file: File): GalleryPhotoResult? {
    return try {
        if (!file.exists() || !file.isFile) return null

        val bufferedImage = ImageIO.read(file)
        if (bufferedImage == null) return null

        val width = bufferedImage.width
        val height = bufferedImage.height
        val fileName = file.name
        val fileSize = (file.length() / 1024)
        val uri = file.toURI().toString()

        GalleryPhotoResult(
            uri = uri,
            width = width,
            height = height,
            fileName = fileName,
            fileSize = fileSize
        )
    } catch (_: Exception) {
        null
    }
}
