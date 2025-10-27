
package io.github.ismoy.imagepickerkmp.data.dataSource

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalZeroShutterLag
import androidx.camera.core.ImageCapture
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference

@OptIn(ExperimentalZeroShutterLag::class)
internal actual fun getCaptureMode(preference: CapturePhotoPreference): Int {
    return when (preference) {
        CapturePhotoPreference.FAST -> ImageCapture.CAPTURE_MODE_ZERO_SHUTTER_LAG // Best for flagship devices
        CapturePhotoPreference.BALANCED -> ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY // Optimized balance
        CapturePhotoPreference.QUALITY -> ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY // High quality when needed
    }
}
