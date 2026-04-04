package io.github.ismoy.imagepickerkmp.features.imagepicker.model

import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.models.MimeType

internal sealed interface PickerMode {
    data object None : PickerMode
    data class Camera(
        val cameraCaptureConfig: CameraCaptureConfig,
        val enableCrop: Boolean,
        val onDismiss: (() -> Unit)?,
        val onError: ((Exception) -> Unit)?
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
        val onDismiss: (() -> Unit)?,
        val onError: ((Exception) -> Unit)?
    ) : PickerMode
}
