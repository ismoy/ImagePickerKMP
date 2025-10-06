package io.github.ismoy.imagepickerkmp.data.dataSource

import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference

internal actual fun getCaptureMode(preference: CapturePhotoPreference): Int {
    // En JS/Web, devolvemos constantes arbitrarias
    return when (preference) {
        CapturePhotoPreference.FAST -> 0
        CapturePhotoPreference.BALANCED -> 1
        CapturePhotoPreference.QUALITY -> 2
    }
}
