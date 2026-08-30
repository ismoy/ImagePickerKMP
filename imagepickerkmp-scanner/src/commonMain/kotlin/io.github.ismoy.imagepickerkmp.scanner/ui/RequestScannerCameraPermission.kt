package io.github.ismoy.imagepickerkmp.scanner.ui

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.scanner.permission.ScannerPermissionConfig

@Composable
internal expect fun RequestScannerCameraPermission(
    dialogConfig: ScannerPermissionConfig,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)? = null
)
