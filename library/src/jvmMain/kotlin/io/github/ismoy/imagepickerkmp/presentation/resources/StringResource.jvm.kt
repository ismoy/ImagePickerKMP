package io.github.ismoy.imagepickerkmp.presentation.resources

import androidx.compose.runtime.Composable

/**
 * Desktop implementation of stringResource.
 * Returns a simple English translation for each string resource.
 */
@Composable
internal actual fun stringResource(id: StringResource): String {
    return when (id) {
        StringResource.CAMERA_PERMISSION_REQUIRED -> "Camera Permission Required"
        StringResource.CAMERA_PERMISSION_DESCRIPTION -> "This app needs camera permission to take photos"
        StringResource.OPEN_SETTINGS -> "Open Settings"
        StringResource.CAMERA_PERMISSION_DENIED -> "Camera Permission Denied"
        StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION -> "Camera permission was denied. This app needs camera permission to take photos."
        StringResource.GRANT_PERMISSION -> "Grant Permission"
        StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED -> "Camera permission was permanently denied. Please enable it in settings."
        StringResource.IMAGE_CONFIRMATION_TITLE -> "Use this image?"
        StringResource.ACCEPT_BUTTON -> "Use Image"
        StringResource.RETRY_BUTTON -> "Try Again"
        StringResource.SELECT_OPTION_DIALOG_TITLE -> "Select Image Source"
        StringResource.TAKE_PHOTO_OPTION -> "Take Photo"
        StringResource.SELECT_FROM_GALLERY_OPTION -> "Choose from Gallery"
        StringResource.CANCEL_OPTION -> "Cancel"
        StringResource.PREVIEW_IMAGE_DESCRIPTION -> "Preview of captured image"
        StringResource.HD_QUALITY_DESCRIPTION -> "HD Quality"
        StringResource.SD_QUALITY_DESCRIPTION -> "Standard Quality"
        StringResource.INVALID_CONTEXT_ERROR -> "Invalid context provided"
        StringResource.PHOTO_CAPTURE_ERROR -> "Failed to capture image"
        StringResource.GALLERY_SELECTION_ERROR -> "Failed to select image from gallery"
        StringResource.PERMISSION_ERROR -> "Permission error"
        StringResource.GALLERY_PERMISSION_REQUIRED -> "Gallery Permission Required"
        StringResource.GALLERY_PERMISSION_DESCRIPTION -> "This app needs gallery permission to access photos"
        StringResource.GALLERY_PERMISSION_DENIED -> "Gallery Permission Denied"
        StringResource.GALLERY_PERMISSION_DENIED_DESCRIPTION -> "Gallery permission was denied. This app needs gallery permission to access photos."
        StringResource.GALLERY_GRANT_PERMISSION -> "Grant Gallery Permission"
        StringResource.GALLERY_BTN_SETTINGS -> "Settings"
    }
}
