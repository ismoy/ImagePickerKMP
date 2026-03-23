package io.github.ismoy.imagepickerkmp.domain.usecases

import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.repository.ImageProcessorRepository


internal class ProcessGalleryFileUseCase(
    private val imageProcessorRepository: ImageProcessorRepository
) {
    suspend operator fun invoke(
        uriString: String,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false
    ): GalleryPhotoResult? = imageProcessorRepository.processGalleryFile(
        uriString = uriString,
        compressionLevel = compressionLevel,
        includeExif = includeExif
    )
}
