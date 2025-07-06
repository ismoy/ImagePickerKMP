package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable

@Composable
expect fun RequestCameraPermission(
    titleDialogConfig:String,
    descriptionDialogConfig:String,
    btnDialogConfig:String,
    titleDialogDenied:String,
    descriptionDialogDenied:String,
    btnDialogDenied:String,
    customDeniedDialog: (@Composable (onRetry: () -> Unit) -> Unit)? =null,
    customSettingsDialog: (@Composable (onOpenSettings: () -> Unit) -> Unit)? =null,
    onPermissionPermanentlyDenied: () -> Unit = {},
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)? =null,
)