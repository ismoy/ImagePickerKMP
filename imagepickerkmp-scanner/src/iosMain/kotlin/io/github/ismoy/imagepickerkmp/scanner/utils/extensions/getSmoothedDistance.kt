package io.github.ismoy.imagepickerkmp.scanner.utils.extensions

import io.github.ismoy.imagepickerkmp.scanner.domain.model.CameraPositionDistance

internal fun List<CameraPositionDistance>.getSmoothedDistance(): CameraPositionDistance {
    if (isEmpty()) return CameraPositionDistance.TOO_FAR
    return groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: CameraPositionDistance.TOO_FAR
}