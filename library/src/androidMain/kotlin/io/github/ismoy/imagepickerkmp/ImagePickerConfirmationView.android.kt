package io.github.ismoy.imagepickerkmp

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable

@Composable
actual fun BoxScope.ImagePickerConfirmationView(
    result: CameraPhotoHandler.PhotoResult,
    onConfirm: (CameraPhotoHandler.PhotoResult) -> Unit,
    onRetry: () -> Unit
) {
    ImageConfirmationViewWithCustomButtons(
        result = result,
        onConfirm = onConfirm,
        onRetry = onRetry
    )
}
