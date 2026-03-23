package io.github.ismoy.imagepickerkmp.features.ocr.di

import io.github.ismoy.imagepickerkmp.features.ocr.data.analyzers.CloudOCRAnalyzer
import io.github.ismoy.imagepickerkmp.features.ocr.data.repository.OCRRepositoryImpl
import io.github.ismoy.imagepickerkmp.features.ocr.domain.repository.OCRRepository
import io.github.ismoy.imagepickerkmp.features.ocr.domain.usecases.AnalyzeImageWithOCRUseCase

internal class OCRDIContainer(apiKey: String) {

    private val analyzer = CloudOCRAnalyzer(apiKey)

    val repository: OCRRepository = OCRRepositoryImpl(analyzer)

    val analyzeImageUseCase = AnalyzeImageWithOCRUseCase(repository)
}
