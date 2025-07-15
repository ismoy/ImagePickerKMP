
package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable

@Composable
expect fun RequestCameraPermission(
    dialogConfig: CameraPermissionDialogConfig,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
)
