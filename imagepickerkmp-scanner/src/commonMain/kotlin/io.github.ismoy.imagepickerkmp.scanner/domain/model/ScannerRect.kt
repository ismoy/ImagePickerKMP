package io.github.ismoy.imagepickerkmp.scanner.domain.model

data class ScannerRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val sourceWidth: Float,
    val sourceHeight: Float,
    val rotation: Int
) {
    val width: Float get() = right - left
    val height: Float get() = bottom - top
}
