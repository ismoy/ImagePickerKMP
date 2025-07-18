
package io.github.ismoy.imagepickerkmp

import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.requestAccessForMediaType

fun requestCameraAccess(callback: (Boolean) -> Unit) {
    AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
        callback(granted)
    }
}
