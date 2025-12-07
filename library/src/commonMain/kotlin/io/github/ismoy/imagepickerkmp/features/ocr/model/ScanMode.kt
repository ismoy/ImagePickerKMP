package io.github.ismoy.imagepickerkmp.features.ocr.model

import io.github.ismoy.imagepickerkmp.features.ocr.data.providers.CloudOCRProvider

/**
 * Scan modes for OCR functionality
 */
sealed class ScanMode {
    data class Cloud(
        val provider: CloudOCRProvider,
        val config: OCRRequestConfig = OCRRequestConfig()
    ) : ScanMode()
}