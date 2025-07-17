
package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable

@Composable
fun defaultGalleryPermissionDialogConfig(): CameraPermissionDialogConfig {
    return CameraPermissionDialogConfig(
        titleDialogConfig = getStringResource(StringResource.GALLERY_PERMISSION_DENIED),
        descriptionDialogConfig = getStringResource(StringResource.GALLERY_PERMISSION_DENIED_DESCRIPTION),
        btnDialogConfig = getStringResource(StringResource.GALLERY_BTN_SETTINGS),
        titleDialogDenied = getStringResource(StringResource.GALLERY_PERMISSION_REQUIRED),
        descriptionDialogDenied = getStringResource(StringResource.GALLERY_PERMISSION_DESCRIPTION),
        btnDialogDenied = getStringResource(StringResource.GALLERY_GRANT_PERMISSION))
}
