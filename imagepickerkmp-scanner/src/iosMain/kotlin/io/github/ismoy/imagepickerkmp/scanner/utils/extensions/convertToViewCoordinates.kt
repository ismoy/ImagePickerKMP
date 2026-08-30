package io.github.ismoy.imagepickerkmp.scanner.utils.extensions

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVMetadataMachineReadableCodeObject
import platform.CoreGraphics.CGRect

@OptIn(ExperimentalForeignApi::class)
internal fun AVMetadataMachineReadableCodeObject.convertToViewCoordinates(
    previewLayer: AVCaptureVideoPreviewLayer?
): CValue<CGRect>? {
    val transformedObject = previewLayer?.transformedMetadataObjectForMetadataObject(this)
            as? AVMetadataMachineReadableCodeObject

    return transformedObject?.bounds
}