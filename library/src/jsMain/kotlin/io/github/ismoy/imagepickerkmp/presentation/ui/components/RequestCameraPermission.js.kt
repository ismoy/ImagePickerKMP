package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.ismoy.imagepickerkmp.domain.config.CameraPermissionDialogConfig

@Composable
actual fun RequestCameraPermission(
    dialogConfig: CameraPermissionDialogConfig,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
) {
    LaunchedEffect(dialogConfig) {
        onResult(true)
    }
}
