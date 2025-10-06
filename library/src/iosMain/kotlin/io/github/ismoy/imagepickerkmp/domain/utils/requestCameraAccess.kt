package io.github.ismoy.imagepickerkmp.domain.utils

import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.requestAccessForMediaType
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

fun requestCameraAccess(callback: (Boolean) -> Unit) {
    AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
        dispatch_async(dispatch_get_main_queue()) {
            callback(granted)
        }
    }
}
