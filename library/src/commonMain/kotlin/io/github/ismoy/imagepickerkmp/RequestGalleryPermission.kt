package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable

@Composable
expect fun RequestGalleryPermission(
    onGranted: () -> Unit,
    onLimited: () -> Unit = {},
    onDenied: () -> Unit,
    customDeniedDialog: (@Composable (() -> Unit))? = null,
    dialogConfig: CameraPermissionDialogConfig
)
