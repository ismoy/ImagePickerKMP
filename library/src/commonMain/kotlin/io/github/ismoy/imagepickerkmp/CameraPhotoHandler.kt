package io.github.ismoy.imagepickerkmp

/**
 * Interface for handling photo capture results from the camera.
 *
 * Implementations of this interface are responsible for processing images captured by the user
 * and providing relevant metadata via [PhotoResult].
 */
interface CameraPhotoHandler {
    /**
     * Data class representing the result of a photo captured by the camera.
     *
     * @property uri The URI of the captured photo as a string.
     * @property width The width of the photo in pixels.
     * @property height The height of the photo in pixels.
     */
    data class PhotoResult(
        val uri: String,
        val width: Int,
        val height: Int,
    )
}
