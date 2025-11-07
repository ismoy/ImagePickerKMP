package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.ismoy.imagepickerkmp.domain.config.CameraPermissionDialogConfig

/**
 * WASM platform implementation of RequestCameraPermission.
 * Note: Camera permissions are not applicable in WASM platform.
 * This is a stub implementation for compatibility.
 */
@Composable
actual fun RequestCameraPermission(
    dialogConfig: CameraPermissionDialogConfig,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
) {
    LaunchedEffect(Unit) {
        onResult(true)
    }
}
