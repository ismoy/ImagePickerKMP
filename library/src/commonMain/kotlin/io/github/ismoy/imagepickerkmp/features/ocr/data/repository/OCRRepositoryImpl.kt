package io.github.ismoy.imagepickerkmp.features.ocr.data.repository

import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.features.ocr.data.analyzers.OCRAnalyzer
import io.github.ismoy.imagepickerkmp.features.ocr.domain.repository.OCRRepository
import io.github.ismoy.imagepickerkmp.features.ocr.model.OCRResult

internal class OCRRepositoryImpl(
    private val analyzer: OCRAnalyzer
) : OCRRepository {
    override suspend fun analyzeImage(photoResult: PhotoResult): OCRResult =
        analyzer.analyzeImage(photoResult)
}
