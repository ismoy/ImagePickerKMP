package io.github.ismoy.imagepickerkmp

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable

@Composable
actual fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (CameraPhotoHandler.PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)?,
    customConfirmationView: (@Composable (CameraPhotoHandler.PhotoResult, (CameraPhotoHandler.PhotoResult) -> Unit, () -> Unit) -> Unit)?,
    preference: CapturePhotoPreference?
) {
    if (context is ComponentActivity) {
        CameraCaptureView(
            activity = context,
            preference = preference ?: CapturePhotoPreference.FAST,
            onPhotoResult = onPhotoCaptured,
            onError = onError,
            customPermissionHandler = customPermissionHandler,
            customConfirmationView = customConfirmationView
        )
    } else {
        onError(Exception("Invalid context. Must be ComponentActivity"))
    }
}