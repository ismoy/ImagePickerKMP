package io.github.ismoy.imagepickerkmp.features.ocr.model

import kotlinx.serialization.Serializable

@Serializable
 data class CustomOCRResponse(
    val text: String? = null,
    val result: String? = null,
    val content: String? = null,
    val confidence: Float? = null,
    val success: Boolean? = null,
    val error: String? = null,
    val message: String? = null,
    val data: String? = null
)
