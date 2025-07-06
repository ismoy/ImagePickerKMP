package io.github.ismoy.imagepickerkmp

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler.PhotoResult

@Composable
expect fun BoxScope.ImagePickerConfirmationView(
    result: PhotoResult,
    onConfirm: (PhotoResult) -> Unit,
    onRetry: () -> Unit
)