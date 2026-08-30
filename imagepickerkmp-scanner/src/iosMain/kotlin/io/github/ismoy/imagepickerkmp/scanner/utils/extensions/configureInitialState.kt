package io.github.ismoy.imagepickerkmp.scanner.utils.extensions

import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEvent
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEventManager
import io.github.ismoy.imagepickerkmp.scanner.utils.LoggerFactory
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureAutoFocusRangeRestrictionNear
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureExposureModeContinuousAutoExposure
import platform.AVFoundation.AVCaptureFocusModeContinuousAutoFocus
import platform.AVFoundation.autoFocusRangeRestriction
import platform.AVFoundation.exposureMode
import platform.AVFoundation.focusMode
import platform.AVFoundation.focusPointOfInterest
import platform.AVFoundation.isAutoFocusRangeRestrictionSupported
import platform.AVFoundation.isExposureModeSupported
import platform.AVFoundation.isFocusModeSupported
import platform.AVFoundation.isFocusPointOfInterestSupported
import platform.AVFoundation.videoZoomFactor
import platform.CoreGraphics.CGPointMake

@OptIn(ExperimentalForeignApi::class)
internal fun AVCaptureDevice.configureInitialState(eventManager: ScannerEventManager) {
    try {
        lockForConfiguration(null)

        if (isFocusModeSupported(AVCaptureFocusModeContinuousAutoFocus)) {
            focusMode = AVCaptureFocusModeContinuousAutoFocus
        }
        if (isFocusPointOfInterestSupported()) {
            focusPointOfInterest = CGPointMake(0.5, 0.5)
        }
        if (isExposureModeSupported(AVCaptureExposureModeContinuousAutoExposure)) {
            exposureMode = AVCaptureExposureModeContinuousAutoExposure
        }
        if (isAutoFocusRangeRestrictionSupported()) {
            autoFocusRangeRestriction = AVCaptureAutoFocusRangeRestrictionNear
        }

        unlockForConfiguration()

        val maxZoom = minOf(activeFormat.videoMaxZoomFactor, 5.0)
        eventManager.emitEvent(ScannerEvent.ZoomStateChanged(
            minZoom = 1.0f,
            maxZoom = maxZoom.toFloat(),
            currentZoom = videoZoomFactor.toFloat()
        ))
    } catch (e: Exception) {
        unlockForConfiguration()
        val errorMessage = "Error to configure camera: ${e.message ?: "Error unknow"}"
        LoggerFactory.getLogger().error("Camera", errorMessage, e)
        eventManager.emitEvent(ScannerEvent.CameraError(errorMessage))
    }
}