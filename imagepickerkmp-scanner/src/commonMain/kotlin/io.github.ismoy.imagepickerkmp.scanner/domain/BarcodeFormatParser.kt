package io.github.ismoy.imagepickerkmp.scanner.domain

fun parseBarcodeFormat(raw: String?): BarcodeFormat {
    if (raw == null) return BarcodeFormat.UNKNOWN
    return try {
        BarcodeFormat.valueOf(raw)
    } catch (_: IllegalArgumentException) {
        BarcodeFormat.UNKNOWN
    }
}
