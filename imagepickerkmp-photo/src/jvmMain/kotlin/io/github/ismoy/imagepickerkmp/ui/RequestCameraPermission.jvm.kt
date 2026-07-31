package io.github.ismoy.imagepickerkmp.ui

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.config.CameraPermissionDialogConfig

@Composable
internal actual fun RequestCameraPermission(
    dialogConfig: CameraPermissionDialogConfig,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
) {
    onResult(true)
}
