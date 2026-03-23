package io.github.ismoy.imagepickerkmp.domain.repository

import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult

internal interface ImageProcessorRepository {
    suspend fun processGalleryImage(
        uriString: String,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false
    ): GalleryPhotoResult?
    suspend fun processGalleryFile(
        uriString: String,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false
    ): GalleryPhotoResult?
}
