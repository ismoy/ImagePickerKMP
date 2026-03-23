package io.github.ismoy.imagepickerkmp.data.repository

import io.github.ismoy.imagepickerkmp.data.models.CameraType
import io.github.ismoy.imagepickerkmp.data.processors.ImageProcessor
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.domain.repository.CameraRepository
import java.io.File

internal class AndroidCameraRepository(
    private val imageProcessor: ImageProcessor
) : CameraRepository {

    override suspend fun processCapturedImage(
        imageFilePath: String,
        isFrontCamera: Boolean,
        compressionLevel: CompressionLevel?,
        includeExif: Boolean,
        redactGpsData: Boolean
    ): PhotoResult {
        val cameraType = if (isFrontCamera) CameraType.FRONT else CameraType.BACK
        return imageProcessor.processImage(
            imageFile = File(imageFilePath),
            cameraType = cameraType,
            compressionLevel = compressionLevel,
            includeExif = includeExif,
            redactGpsData = redactGpsData
        )
    }
}
