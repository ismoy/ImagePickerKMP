package io.github.ismoy.imagepickerkmp.scanner.domain.model

import io.github.ismoy.imagepickerkmp.scanner.domain.BarcodeFormat

data class BarcodeData(
    val rawValue: String,
    val format: BarcodeFormat,
    val boundingBox: ScannerRect?,
    val cornerPoints: List<ScannerPoint>?
)
