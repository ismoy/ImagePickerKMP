package io.github.ismoy.imagepickerkmp.picker

import io.github.ismoy.imagepickerkmp.config.CameraCaptureConfig

internal sealed interface PickerMode {
    data object None : PickerMode
    data class Camera(
        val cameraCaptureConfig: CameraCaptureConfig,
        val enableCrop: Boolean
    ) : PickerMode
    data class Gallery(
        val allowMultiple: Boolean,
        val mimeTypes: List<MimeType>,
        val selectionLimit: Long,
        val enableCrop: Boolean,
        val includeExif: Boolean,
        val redactGpsData: Boolean,
        val mimeTypeMismatchMessage: String?,
        val cameraCaptureConfig: CameraCaptureConfig?,
        val compressionLevel: CompressionLevel?
    ) : PickerMode
}
