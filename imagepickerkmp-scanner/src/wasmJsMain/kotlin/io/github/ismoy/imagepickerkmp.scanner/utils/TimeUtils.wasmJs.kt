package io.github.ismoy.imagepickerkmp.scanner.utils

actual fun getCurrentTimeMillis(): Long {
    throw UnsupportedOperationException(
        "Scanner timing is unavailable because Wasm JS scanning is not supported."
    )
}