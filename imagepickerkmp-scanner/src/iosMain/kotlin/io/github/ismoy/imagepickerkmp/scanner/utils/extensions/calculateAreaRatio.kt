package io.github.ismoy.imagepickerkmp.scanner.utils.extensions

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.AVFoundation.AVMetadataMachineReadableCodeObject

@OptIn(ExperimentalForeignApi::class)
internal fun AVMetadataMachineReadableCodeObject.calculateAreaRatio(): Float {
    return bounds.useContents { size.width * size.height }.toFloat()
}