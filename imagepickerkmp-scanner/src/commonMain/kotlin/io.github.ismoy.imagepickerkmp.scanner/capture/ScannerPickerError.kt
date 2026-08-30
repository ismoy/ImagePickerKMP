package io.github.ismoy.imagepickerkmp.scanner.capture

import io.github.ismoy.imagepickerkmp.scanner.I18nKonfig

sealed class ScannerPickerError : Exception() {
    data class Unknown(val error: Throwable) : ScannerPickerError() {
        override val message: String get() = error.message ?: "Unknown error"
    }
    data class CameraError(override val message: String) : ScannerPickerError()
    data class PermissionDenied(
        override val message: String = I18nKonfig.General.camera_permission_denied
    ) : ScannerPickerError()
}
