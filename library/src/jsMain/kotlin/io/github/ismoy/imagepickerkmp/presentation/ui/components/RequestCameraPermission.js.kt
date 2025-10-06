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
    // En navegadores web, no necesitamos permisos explícitos para acceso a archivos
    // El navegador maneja los permisos automáticamente cuando el usuario interactúa
    LaunchedEffect(dialogConfig) {
        // Simulamos que siempre tenemos permiso para seleccionar archivos
        onResult(true)
    }
}
