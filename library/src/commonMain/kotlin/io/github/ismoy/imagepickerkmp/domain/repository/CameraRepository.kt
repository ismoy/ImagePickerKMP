package io.github.ismoy.imagepickerkmp.domain.repository

import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult

internal interface CameraRepository {

    suspend fun processCapturedImage(
        imageFilePath: String,
        isFrontCamera: Boolean,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false,
        redactGpsData: Boolean = true
    ): PhotoResult
}
