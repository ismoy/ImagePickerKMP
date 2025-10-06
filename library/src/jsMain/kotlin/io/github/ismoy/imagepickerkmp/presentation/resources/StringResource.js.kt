package io.github.ismoy.imagepickerkmp.presentation.resources

import androidx.compose.runtime.Composable

@Composable
internal actual fun stringResource(id: StringResource): String {
    return when (id) {
        StringResource.CAMERA_PERMISSION_REQUIRED -> "Camera Permission Required"
        StringResource.CAMERA_PERMISSION_DESCRIPTION -> "This app needs camera access to take photos. Please grant camera permission in your browser."
        StringResource.OPEN_SETTINGS -> "Open Settings"
        StringResource.CAMERA_PERMISSION_DENIED -> "Permission Denied"
        StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION -> "Camera permission was denied"
        StringResource.GRANT_PERMISSION -> "Grant Permission"
        StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED -> "Camera Permission Permanently Denied"
        StringResource.IMAGE_CONFIRMATION_TITLE -> "Confirm Image"
        StringResource.ACCEPT_BUTTON -> "Accept"
        StringResource.RETRY_BUTTON -> "Retry"
        StringResource.SELECT_OPTION_DIALOG_TITLE -> "Select Option"
        StringResource.TAKE_PHOTO_OPTION -> "Take Photo"
        StringResource.SELECT_FROM_GALLERY_OPTION -> "Select from Gallery"
        StringResource.CANCEL_OPTION -> "Cancel"
        StringResource.PREVIEW_IMAGE_DESCRIPTION -> "Preview Image"
        StringResource.HD_QUALITY_DESCRIPTION -> "HD Quality"
        StringResource.SD_QUALITY_DESCRIPTION -> "SD Quality"
        StringResource.INVALID_CONTEXT_ERROR -> "Invalid Context Error"
        StringResource.PHOTO_CAPTURE_ERROR -> "Photo Capture Error"
        StringResource.GALLERY_SELECTION_ERROR -> "Gallery Selection Error"
        StringResource.PERMISSION_ERROR -> "Permission Error"
        StringResource.GALLERY_PERMISSION_REQUIRED -> "Gallery Permission Required"
        StringResource.GALLERY_PERMISSION_DESCRIPTION -> "This app needs gallery access to select photos"
        StringResource.GALLERY_PERMISSION_DENIED -> "Gallery Permission Denied"
        StringResource.GALLERY_PERMISSION_DENIED_DESCRIPTION -> "Gallery permission was denied"
        StringResource.GALLERY_GRANT_PERMISSION -> "Grant Gallery Permission"
        StringResource.GALLERY_BTN_SETTINGS -> "Gallery Settings"
    }
}
