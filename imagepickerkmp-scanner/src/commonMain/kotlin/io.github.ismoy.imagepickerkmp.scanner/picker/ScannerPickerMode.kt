package io.github.ismoy.imagepickerkmp.scanner.picker

sealed class ScannerPickerMode {
    data class Camera(
        val onDismiss: (() -> Unit)? = null,
        val onError: ((Exception) -> Unit)? = null
    ) : ScannerPickerMode()

    data object None : ScannerPickerMode()
}
