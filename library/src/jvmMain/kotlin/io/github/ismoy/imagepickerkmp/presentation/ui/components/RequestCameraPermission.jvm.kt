package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.domain.config.CameraPermissionDialogConfig

/**
 * Desktop implementation of RequestCameraPermission.
 * Camera permissions are not needed on Desktop.
 * This is a no-op implementation.
 */
@Composable
actual fun RequestCameraPermission(
    dialogConfig: CameraPermissionDialogConfig,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
) {
    // No camera permission required on Desktop
    // Directly call onResult with true to proceed
    onResult(true)
}
