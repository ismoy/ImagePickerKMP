package io.github.ismoy.imagepickerkmp

enum class CapturePhotoPreference {
    FAST,
    BALANCED,
    QUALITY
}
internal expect fun getCaptureMode(preference: CapturePhotoPreference): Int