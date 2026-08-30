package io.github.ismoy.imagepickerkmp.scanner.domain.model

import io.github.ismoy.imagepickerkmp.scanner.domain.BarcodeFormat

data class ScannerResult(
    val code: String,
    val format: BarcodeFormat? = null,
    val timestamp: Long
)
