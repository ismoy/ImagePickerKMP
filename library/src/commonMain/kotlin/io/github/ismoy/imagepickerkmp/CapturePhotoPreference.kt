package io.github.ismoy.imagepickerkmp

/**
 * Enum representing the available preferences for photo capture.
 *
 * Allows selection between fast capture, balanced mode, or highest quality.
 */
enum class CapturePhotoPreference {
    FAST,
    BALANCED,
    QUALITY
}

internal expect fun getCaptureMode(preference: CapturePhotoPreference): Int
