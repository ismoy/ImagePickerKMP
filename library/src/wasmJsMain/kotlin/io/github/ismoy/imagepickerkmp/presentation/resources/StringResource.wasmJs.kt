package io.github.ismoy.imagepickerkmp.presentation.resources

import androidx.compose.runtime.Composable

/**
 * WASM platform implementation of stringResource.
 * Provides English fallback strings for WASM platform.
 */
@Composable
internal actual fun stringResource(id: StringResource): String {
    return when (id) {
        StringResource.CAMERA_PERMISSION_REQUIRED -> "Camera Permission Required"
        StringResource.CAMERA_PERMISSION_DESCRIPTION -> "This app needs access to your camera to take photos"
        StringResource.OPEN_SETTINGS -> "Open Settings"
        StringResource.CAMERA_PERMISSION_DENIED -> "Camera Permission Denied"
        StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION -> "Camera access was denied. You can enable it in settings."
        StringResource.GRANT_PERMISSION -> "Grant Permission"
        StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED -> "Camera Permission Permanently Denied"
        StringResource.IMAGE_CONFIRMATION_TITLE -> "Are you satisfied with the photo?"
        StringResource.ACCEPT_BUTTON -> "Accept"
        StringResource.RETRY_BUTTON -> "Retry"
        StringResource.SELECT_OPTION_DIALOG_TITLE -> "Select option"
        StringResource.TAKE_PHOTO_OPTION -> "Take photo"
        StringResource.SELECT_FROM_GALLERY_OPTION -> "Select from gallery"
        StringResource.CANCEL_OPTION -> "Cancel"
        StringResource.PREVIEW_IMAGE_DESCRIPTION -> "Preview"
        StringResource.HD_QUALITY_DESCRIPTION -> "HD"
        StringResource.SD_QUALITY_DESCRIPTION -> "SD"
        StringResource.INVALID_CONTEXT_ERROR -> "Invalid context error"
        StringResource.PHOTO_CAPTURE_ERROR -> "Photo capture failed"
        StringResource.GALLERY_SELECTION_ERROR -> "Gallery selection failed"
        StringResource.PERMISSION_ERROR -> "Permission error"
        StringResource.GALLERY_PERMISSION_REQUIRED -> "Gallery Permission Required"
        StringResource.GALLERY_PERMISSION_DESCRIPTION -> "This app needs access to your gallery to select photos"
        StringResource.GALLERY_PERMISSION_DENIED -> "Gallery Permission Denied"
        StringResource.GALLERY_PERMISSION_DENIED_DESCRIPTION -> "Gallery access was denied. You can enable it in settings."
        StringResource.GALLERY_GRANT_PERMISSION -> "Grant Permission"
        StringResource.GALLERY_BTN_SETTINGS -> "Settings"
    }
}
