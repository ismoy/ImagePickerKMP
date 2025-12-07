package io.github.ismoy.imagepickerkmp.features.ocr.data.providers

import io.github.ismoy.imagepickerkmp.features.ocr.model.OCRResult
import io.github.ismoy.imagepickerkmp.features.ocr.model.OCRRequestConfig
import io.github.ismoy.imagepickerkmp.features.ocr.model.ProviderInfo

interface CloudOCRProviderInterface {
    val providerName: String
    suspend fun extractText(
        imageData: ByteArray, 
        config: OCRRequestConfig = OCRRequestConfig()
    ): OCRResult

    suspend fun isAvailable(): Boolean
    fun getProviderInfo(): ProviderInfo

    fun close()
}
