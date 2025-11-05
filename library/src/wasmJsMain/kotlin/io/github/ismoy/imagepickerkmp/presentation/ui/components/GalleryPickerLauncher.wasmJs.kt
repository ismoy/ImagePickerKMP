package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType

/**
 * WASM platform implementation of GalleryPickerLauncher.
 * Uses the file picker functionality available in web browsers through WASM.
 */
@Suppress("LongParameterList")
@Composable
actual fun GalleryPickerLauncher(
    onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    allowMultiple: Boolean,
    mimeTypes: List<MimeType>,
    selectionLimit: Long,
    cameraCaptureConfig: CameraCaptureConfig?,
    enableCrop: Boolean,
    fileFilterDescription: String,
    includeExif: Boolean
) {
    Text("GalleryPicker not supported on WASM platform")
}
