package io.github.ismoy.imagepickerkmp

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp

@Composable
actual fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (CameraPhotoHandler.PhotoResult) -> Unit,
    onPhotosSelected: ((List<GalleryPhotoHandler.PhotoResult>) -> Unit)?,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)?,
    customConfirmationView: (@Composable (CameraPhotoHandler.PhotoResult, (CameraPhotoHandler.PhotoResult) -> Unit, () -> Unit) -> Unit)?,
    preference: CapturePhotoPreference?,
    dialogTitle: String,
    takePhotoText: String,
    selectFromGalleryText: String,
    cancelText: String,
    buttonColor: Color?,
    iconColor: Color?,
    buttonSize: Dp?,
    layoutPosition: String?,
    flashIcon: ImageVector?,
    switchCameraIcon: ImageVector?,
    captureIcon: ImageVector?,
    galleryIcon: ImageVector?,
    onCameraReady: (() -> Unit)?,
    onCameraSwitch: (() -> Unit)?,
    onPermissionError: ((Exception) -> Unit)?,
    onGalleryOpened: (() -> Unit)?,
    allowMultiple: Boolean,
    mimeTypes: List<String>
) {
    if (context is ComponentActivity) {
        CameraCaptureView(
            activity = context,
            preference = preference ?: CapturePhotoPreference.FAST,
            onPhotoResult = { result ->
                onPhotoCaptured(result)
            },
            onPhotosSelected = onPhotosSelected,
            onError = { exception ->
                onError(exception)
            },
            customPermissionHandler = customPermissionHandler,
            customConfirmationView = customConfirmationView,
            buttonColor = buttonColor,
            iconColor = iconColor,
            buttonSize = buttonSize,
            layoutPosition = layoutPosition,
            flashIcon = flashIcon,
            switchCameraIcon = switchCameraIcon,
            captureIcon = captureIcon,
            galleryIcon = galleryIcon,
            onCameraReady = onCameraReady,
            onCameraSwitch = onCameraSwitch,
            onPermissionError = onPermissionError,
            onGalleryOpened = onGalleryOpened,
            allowMultiple = allowMultiple,
            mimeTypes = mimeTypes
        )
    } else {
        val exception = Exception("Invalid context. Must be ComponentActivity")
        onError(exception)
    }
}