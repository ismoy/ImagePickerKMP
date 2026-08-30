package io.github.ismoy.imagepickerkmp.scanner.utils.extensions

import io.github.ismoy.imagepickerkmp.scanner.utils.LoggerFactory
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureExposureModeAutoExpose
import platform.AVFoundation.AVCaptureFocusModeAutoFocus
import platform.AVFoundation.exposureMode
import platform.AVFoundation.exposurePointOfInterest
import platform.AVFoundation.focusMode
import platform.AVFoundation.focusPointOfInterest
import platform.AVFoundation.isExposurePointOfInterestSupported
import platform.AVFoundation.isFocusPointOfInterestSupported
import platform.CoreGraphics.CGPointMake

@OptIn(ExperimentalForeignApi::class)
internal fun AVCaptureDevice.updateFocusPoint(x: Float, y: Float) {
    try {
        lockForConfiguration(null)
        if (isFocusPointOfInterestSupported()) {
            focusPointOfInterest = CGPointMake(x.toDouble(), y.toDouble())
            focusMode = AVCaptureFocusModeAutoFocus
        }
        if (isExposurePointOfInterestSupported()) {
            exposurePointOfInterest = CGPointMake(x.toDouble(), y.toDouble())
            exposureMode = AVCaptureExposureModeAutoExpose
        }
        unlockForConfiguration()
    } catch (e: Exception) {
        LoggerFactory.getLogger().error("Camera", "Error configuring device focus: ${e.message}", e)
    }
}