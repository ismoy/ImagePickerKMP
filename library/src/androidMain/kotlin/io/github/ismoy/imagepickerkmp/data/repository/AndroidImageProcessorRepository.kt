package io.github.ismoy.imagepickerkmp.data.repository

import android.content.Context
import io.github.ismoy.imagepickerkmp.data.gallery.GalleryFileProcessor
import io.github.ismoy.imagepickerkmp.data.gallery.GalleryImageProcessor
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.repository.ImageProcessorRepository
import androidx.core.net.toUri

internal class AndroidImageProcessorRepository(
    private val context: Context
) : ImageProcessorRepository {

    override suspend fun processGalleryImage(
        uriString: String,
        compressionLevel: CompressionLevel?,
        includeExif: Boolean
    ): GalleryPhotoResult? = GalleryImageProcessor.processSelectedImageSuspend(
        context = context,
        uri = uriString.toUri(),
        compressionLevel = compressionLevel,
        includeExif = includeExif
    )

    override suspend fun processGalleryFile(
        uriString: String,
        compressionLevel: CompressionLevel?,
        includeExif: Boolean
    ): GalleryPhotoResult? = GalleryFileProcessor.processSelectedFile(
        context = context,
        uri = uriString.toUri(),
        compressionLevel = compressionLevel,
        includeExif = includeExif
    )
}
