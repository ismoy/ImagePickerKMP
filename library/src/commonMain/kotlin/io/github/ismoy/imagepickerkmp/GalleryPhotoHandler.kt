package io.github.ismoy.imagepickerkmp

interface GalleryPhotoHandler {
    data class PhotoResult(
        val uri: String,
        val width: Int,
        val height: Int,
        val fileName: String? = null,
        val fileSize: Long? = null
    )
} 