package io.github.ismoy.imagepickerkmp

interface CameraPhotoHandler {
    data class PhotoResult(
        val uri: String,
        val width: Int,
        val height: Int,
    )
}