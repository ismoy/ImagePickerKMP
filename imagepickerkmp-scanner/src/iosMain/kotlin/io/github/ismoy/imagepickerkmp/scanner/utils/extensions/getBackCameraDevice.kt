package io.github.ismoy.imagepickerkmp.scanner.utils.extensions

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceDiscoverySession
import platform.AVFoundation.AVCaptureDevicePositionBack
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInUltraWideCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInWideAngleCamera
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.deviceType

@OptIn(ExperimentalForeignApi::class)
internal fun getBackCameraDevice(): AVCaptureDevice? {
    val discoverySession = AVCaptureDeviceDiscoverySession.discoverySessionWithDeviceTypes(
        deviceTypes = listOf(
            AVCaptureDeviceTypeBuiltInUltraWideCamera,
            AVCaptureDeviceTypeBuiltInWideAngleCamera
        ),
        mediaType = AVMediaTypeVideo,
        position = AVCaptureDevicePositionBack
    )

    return (discoverySession.devices.firstOrNull { device ->
        (device as? AVCaptureDevice)?.deviceType == AVCaptureDeviceTypeBuiltInUltraWideCamera
    } as? AVCaptureDevice) ?: (discoverySession.devices.firstOrNull() as? AVCaptureDevice)
}