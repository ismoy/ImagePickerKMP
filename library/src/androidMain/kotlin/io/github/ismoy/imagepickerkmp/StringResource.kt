package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import io.github.ismoy.R

@Composable
internal actual fun stringResource(id: StringResource): String {
    val context = LocalContext.current
    return context.getString(getAndroidStringResId(id))
}

private val androidStringResMap = mapOf(
    StringResource.CAMERA_PERMISSION_REQUIRED to R.string.camera_permission_required,
    StringResource.CAMERA_PERMISSION_DESCRIPTION to R.string.camera_permission_description,
    StringResource.OPEN_SETTINGS to R.string.open_settings,
    StringResource.CAMERA_PERMISSION_DENIED to R.string.camera_permission_denied,
    StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION to R.string.camera_permission_denied_description,
    StringResource.GRANT_PERMISSION to R.string.grant_permission,
    StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED to R.string.camera_permission_permanently_denied,
    StringResource.IMAGE_CONFIRMATION_TITLE to R.string.image_confirmation_title,
    StringResource.ACCEPT_BUTTON to R.string.accept_button,
    StringResource.RETRY_BUTTON to R.string.retry_button,
    StringResource.SELECT_OPTION_DIALOG_TITLE to R.string.select_option_dialog_title,
    StringResource.TAKE_PHOTO_OPTION to R.string.take_photo_option,
    StringResource.SELECT_FROM_GALLERY_OPTION to R.string.select_from_gallery_option,
    StringResource.CANCEL_OPTION to R.string.cancel_option,
    StringResource.PREVIEW_IMAGE_DESCRIPTION to R.string.preview_image_description,
    StringResource.HD_QUALITY_DESCRIPTION to R.string.hd_quality_description,
    StringResource.SD_QUALITY_DESCRIPTION to R.string.sd_quality_description,
    StringResource.INVALID_CONTEXT_ERROR to R.string.invalid_context_error,
    StringResource.PHOTO_CAPTURE_ERROR to R.string.photo_capture_error,
    StringResource.GALLERY_SELECTION_ERROR to R.string.gallery_selection_error,
    StringResource.PERMISSION_ERROR to R.string.permission_error,
    StringResource.GALLERY_PERMISSION_REQUIRED to R.string.gallery_permission_required,
    StringResource.GALLERY_PERMISSION_DESCRIPTION to R.string.gallery_permission_description,
    StringResource.GALLERY_PERMISSION_DENIED to R.string.gallery_permission_denied,
    StringResource.GALLERY_PERMISSION_DENIED_DESCRIPTION to R.string.gallery_permission_denied_description,
    StringResource.GALLERY_GRANT_PERMISSION to R.string.gallery_grant_permission,
    StringResource.GALLERY_BTN_SETTINGS to R.string.gallery_btn_settings
)
private fun getAndroidStringResId(id: StringResource): Int =
    androidStringResMap[id] ?: error("Missing Android string resource mapping for $id")

@Suppress("CyclomaticComplexMethod")
internal fun getStringResource(id: StringResource): String {
    return when (id) {
        StringResource.CAMERA_PERMISSION_REQUIRED -> "Camera permission required"
        StringResource.CAMERA_PERMISSION_DESCRIPTION -> "Camera permission is required to capture photos." +
                " Please grant it in settings"
        StringResource.OPEN_SETTINGS -> "Open settings"
        StringResource.CAMERA_PERMISSION_DENIED -> "Camera permission denied"
        StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION -> "Camera permission is required to" +
                " capture photos. Please grant the permissions"
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
        StringResource.GALLERY_PERMISSION_REQUIRED -> "Storage permission required"
        StringResource.GALLERY_PERMISSION_DESCRIPTION -> "Access to storage is required to select " +
                "images from your gallery. Please grant the permission."
        StringResource.GALLERY_PERMISSION_DENIED -> "Storage permission denied"
        StringResource.GALLERY_PERMISSION_DENIED_DESCRIPTION -> "Storage permission is required " +
                "to select images. Please enable it in app settings."
        StringResource.GALLERY_GRANT_PERMISSION -> "Grant permission"
        StringResource.GALLERY_BTN_SETTINGS -> "Open settings"

    }
}
