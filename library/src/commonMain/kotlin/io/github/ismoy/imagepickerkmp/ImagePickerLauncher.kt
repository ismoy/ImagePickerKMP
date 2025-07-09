package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler.PhotoResult

@Composable
expect fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)?,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?,
    preference: CapturePhotoPreference?
)