package io.github.ismoy.imagepickerkmp.features.ocr.domain.usecases

import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.features.ocr.domain.repository.OCRRepository
import io.github.ismoy.imagepickerkmp.features.ocr.model.OCRResult

internal class AnalyzeImageWithOCRUseCase(
    private val repository: OCRRepository
) {
    suspend operator fun invoke(photoResult: PhotoResult): OCRResult =
        repository.analyzeImage(photoResult)
}
