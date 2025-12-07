package io.github.ismoy.imagepickerkmp.features.ocr.data.analyzers

import io.github.ismoy.imagepickerkmp.features.ocr.model.OCRResult
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult

/**
 * Interface for OCR analysis implementations
 * Provides a contract for both local and cloud-based text recognition
 */
interface OCRAnalyzer {
    suspend fun analyzeImage(photoResult: PhotoResult): OCRResult
}
