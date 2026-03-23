package io.github.ismoy.imagepickerkmp.features.ocr.domain.repository

import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.features.ocr.model.OCRResult

internal interface OCRRepository {
    suspend fun analyzeImage(photoResult: PhotoResult): OCRResult
}
