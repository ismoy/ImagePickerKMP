
package io.github.ismoy.imagepickerkmp

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalZeroShutterLag
import androidx.camera.core.ImageCapture

@OptIn(ExperimentalZeroShutterLag::class)
internal actual fun getCaptureMode(preference: CapturePhotoPreference): Int {
    return when (preference) {
        CapturePhotoPreference.FAST -> ImageCapture.CAPTURE_MODE_ZERO_SHUTTER_LAG
        CapturePhotoPreference.BALANCED -> ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
        CapturePhotoPreference.QUALITY -> ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
    }
}
