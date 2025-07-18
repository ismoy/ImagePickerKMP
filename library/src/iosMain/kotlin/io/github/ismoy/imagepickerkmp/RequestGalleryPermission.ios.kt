package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable

@Suppress("EmptyFunctionBlock")
@Composable
actual fun RequestGalleryPermission(
    onGranted: () -> Unit,
    onLimited: () -> Unit,
    onDenied: () -> Unit,
    customDeniedDialog: @Composable (() -> Unit)?,
    dialogConfig: CameraPermissionDialogConfig
)  {}
