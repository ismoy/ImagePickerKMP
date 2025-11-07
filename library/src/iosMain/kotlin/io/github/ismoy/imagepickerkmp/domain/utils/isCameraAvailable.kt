package io.github.ismoy.imagepickerkmp.domain.utils

import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType

/**
 * Checks if camera hardware is available on the current device.
 * 
 * **Important:** iOS Simulators do NOT have camera support.
 * 
 * This function returns:
 * - `true` on physical iOS devices with camera hardware
 * - `false` on iOS Simulators or devices without camera
 * 
 * ## Testing Camera Features
 * 
 * To test camera functionality, you must:
 * 1. Use a physical iOS device (iPhone/iPad)
 * 2. Build and deploy your app to the device
 * 3. Ensure Info.plist contains `NSCameraUsageDescription`
 * 
 * ## Simulator Limitations
 * 
 * The iOS Simulator does NOT support:
 * - Camera hardware access
 * - AVCaptureDevice for photo/video capture
 * - Camera permission requests (always returns denied/unavailable)
 * 
 * ## Recommended Approach
 * 
 * Always check camera availability before requesting permissions or launching camera:
 * 
 * ```kotlin
 * if (isCameraAvailable()) {
 *     // Request camera permission and launch camera
 * } else {
 *     // Show appropriate message to user
 *     // "Camera not available. Please test on a physical device."
 * }
 * ```
 * 
 * @return true if camera hardware is available, false otherwise
 */
fun isCameraAvailable(): Boolean {
    return UIImagePickerController.isSourceTypeAvailable(
        UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
    )
}

/**
 * Checks if running on iOS Simulator.
 * 
 * This is a convenience function that inverts [isCameraAvailable].
 * In most cases, if camera is not available, the app is running on a simulator.
 * 
 * @return true if likely running on iOS Simulator, false if on physical device
 */
fun isRunningOnSimulator(): Boolean {
    return !isCameraAvailable()
}
