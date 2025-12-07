package io.github.ismoy.imagepickerkmp.features.ocr.model

import kotlinx.serialization.Serializable

@Serializable
 data class OCRSpaceResponse(
    val parsedResults: List<OCRSpaceParsedResult>? = null,
    val oCRExitCode: String? = null,
    val isErroredOnProcessing: Boolean? = null,
    val errorMessage: List<String>? = null,
    val errorDetails: String? = null,
    val processingTimeInMilliseconds: String? = null,
    val searchablePDFURL: String? = null
)
