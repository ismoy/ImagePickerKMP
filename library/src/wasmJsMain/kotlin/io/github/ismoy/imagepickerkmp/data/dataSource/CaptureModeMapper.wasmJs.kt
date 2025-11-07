package io.github.ismoy.imagepickerkmp.data.dataSource

import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference

/**
 * WASM platform implementation of getCaptureMode.
 * Note: Camera capture modes are not applicable in WASM platform.
 * This is a stub implementation for compatibility.
 */
internal actual fun getCaptureMode(preference: CapturePhotoPreference): Int {
    // Stub implementation for WASM - return default value
    return 0
}
