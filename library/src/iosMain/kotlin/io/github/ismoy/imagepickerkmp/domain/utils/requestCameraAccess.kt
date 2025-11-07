package io.github.ismoy.imagepickerkmp.domain.utils

import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.requestAccessForMediaType
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/**
 * Requests camera access permission from the user.
 * 
 * Note: On iOS Simulator, camera is not available and this will return false.
 * Camera functionality must be tested on physical devices.
 * 
 * @param callback Called with true if permission is granted, false otherwise
 */
fun requestCameraAccess(callback: (Boolean) -> Unit) {
    // Check if camera hardware is available (not available on simulator)
    if (!UIImagePickerController.isSourceTypeAvailable(
        UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
    )) {
        println("⚠️ Camera not available - iOS Simulator does not support camera")
        dispatch_async(dispatch_get_main_queue()) {
            callback(false)
        }
        return
    }
    
    // Request camera permission
    AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
        dispatch_async(dispatch_get_main_queue()) {
            callback(granted)
        }
    }
}
