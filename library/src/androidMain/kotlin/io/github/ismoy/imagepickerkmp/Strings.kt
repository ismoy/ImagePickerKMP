package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import io.github.ismoy.R

@Composable
internal actual fun stringResource(id: StringResource): String {
    val context = LocalContext.current
    val resourceId = when (id) {
        StringResource.CAMERA_PERMISSION_REQUIRED -> R.string.camera_permission_required
        StringResource.CAMERA_PERMISSION_DESCRIPTION -> R.string.camera_permission_description
        StringResource.OPEN_SETTINGS -> R.string.open_settings
        StringResource.CAMERA_PERMISSION_DENIED -> R.string.camera_permission_denied
        StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION -> R.string.camera_permission_denied_description
        StringResource.GRANT_PERMISSION -> R.string.grant_permission
        StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED -> R.string.camera_permission_permanently_denied
        
        StringResource.IMAGE_CONFIRMATION_TITLE -> R.string.image_confirmation_title
        StringResource.ACCEPT_BUTTON -> R.string.accept_button
        StringResource.RETRY_BUTTON -> R.string.retry_button
        
        StringResource.SELECT_OPTION_DIALOG_TITLE -> R.string.select_option_dialog_title
        StringResource.TAKE_PHOTO_OPTION -> R.string.take_photo_option
        StringResource.SELECT_FROM_GALLERY_OPTION -> R.string.select_from_gallery_option
        StringResource.CANCEL_OPTION -> R.string.cancel_option
        
        StringResource.PREVIEW_IMAGE_DESCRIPTION -> R.string.preview_image_description
        StringResource.HD_QUALITY_DESCRIPTION -> R.string.hd_quality_description
        StringResource.SD_QUALITY_DESCRIPTION -> R.string.sd_quality_description
        
        StringResource.INVALID_CONTEXT_ERROR -> R.string.invalid_context_error
        StringResource.PHOTO_CAPTURE_ERROR -> R.string.photo_capture_error
        StringResource.GALLERY_SELECTION_ERROR -> R.string.gallery_selection_error
        StringResource.PERMISSION_ERROR -> R.string.permission_error
    }
    return context.getString(resourceId)
}

internal actual fun getStringResource(id: StringResource): String {
    return when (id) {
        StringResource.CAMERA_PERMISSION_REQUIRED -> "Camera permission required"
        StringResource.CAMERA_PERMISSION_DESCRIPTION -> "Camera permission is required to capture photos. Please grant it in settings"
        StringResource.OPEN_SETTINGS -> "Open settings"
        StringResource.CAMERA_PERMISSION_DENIED -> "Camera permission denied"
        StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION -> "Camera permission is required to capture photos. Please grant the permissions"
        StringResource.GRANT_PERMISSION -> "Grant permission"
        StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED -> "Camera permission permanently denied"
        
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
        
        StringResource.INVALID_CONTEXT_ERROR -> "Invalid context. Must be ComponentActivity"
        StringResource.PHOTO_CAPTURE_ERROR -> "Photo capture failed"
        StringResource.GALLERY_SELECTION_ERROR -> "Gallery selection failed"
        StringResource.PERMISSION_ERROR -> "Permission error occurred"
    }
} 