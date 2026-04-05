package io.github.ismoy.imagepickerkmp.presentation.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import io.github.ismoy.imagepickerkmp.domain.config.CameraPermissionDialogConfig
import io.github.ismoy.imagepickerkmp.presentation.ui.utils.openAppSettings


@Composable
actual fun RequestCameraPermission(
    dialogConfig: CameraPermissionDialogConfig,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    var showRationale by remember { mutableStateOf(false) }
    var permissionDeniedPermanently by remember { mutableStateOf(false) }
    var hasCalledPermanentlyDenied by remember { mutableStateOf(false) }
    // true cuando el usuario ya fue a Settings — al volver se evalúa si el permiso fue concedido
    var waitingForSettingsReturn by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onResult(true)
        } else {
            val isPermanentlyDenied = activity != null &&
                !activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
            if (isPermanentlyDenied) {
                permissionDeniedPermanently = true
                showRationale = false
            } else {
                showRationale = true
                permissionDeniedPermanently = false
            }
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        val isGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        if (isGranted) {
            permissionDeniedPermanently = false
            showRationale = false
            waitingForSettingsReturn = false
            onResult(true)
        } else if (waitingForSettingsReturn) {
            // El usuario regresó de Settings sin conceder el permiso → cerrar el picker
            waitingForSettingsReturn = false
            onPermissionPermanentlyDenied()
        } else if (!showRationale && !permissionDeniedPermanently) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (showRationale) {
        if (dialogConfig.customDeniedDialog != null) {
            dialogConfig.customDeniedDialog.invoke(
                // onRetry
                {
                    showRationale = false
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                // onDismiss — el usuario cerró el dialog sin reintentar
                {
                    showRationale = false
                    onPermissionPermanentlyDenied()
                }
            )
        } else {
            CustomPermissionDialog(
                title = dialogConfig.titleDialogDenied,
                description = dialogConfig.descriptionDialogDenied,
                confirmationButtonText = dialogConfig.btnDialogDenied,
                cancelButtonText = "Cancel",
                onConfirm = {
                    showRationale = false
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onCancel = {
                    showRationale = false
                    onPermissionPermanentlyDenied()
                }
            )
        }
    }

    if (permissionDeniedPermanently && !hasCalledPermanentlyDenied) {
        if (dialogConfig.customSettingsDialog != null) {
            dialogConfig.customSettingsDialog.invoke(
                // onOpenSettings — ir a Settings y esperar que el usuario regrese
                {
                    hasCalledPermanentlyDenied = true
                    permissionDeniedPermanently = false  // oculta el dialog inmediatamente
                    waitingForSettingsReturn = true       // ON_RESUME decidirá qué hacer
                    openAppSettings(context)
                },
                // onDismiss — el usuario cerró el dialog sin ir a settings
                {
                    hasCalledPermanentlyDenied = true
                    permissionDeniedPermanently = false
                    onPermissionPermanentlyDenied()
                }
            )
        } else {
            CustomPermissionDialog(
                title = dialogConfig.titleDialogConfig,
                description = dialogConfig.descriptionDialogConfig,
                confirmationButtonText = dialogConfig.btnDialogConfig,
                cancelButtonText = "Cancel",
                onConfirm = {
                    hasCalledPermanentlyDenied = true
                    permissionDeniedPermanently = false
                    waitingForSettingsReturn = true
                    openAppSettings(context)
                },
                onCancel = {
                    hasCalledPermanentlyDenied = true
                    permissionDeniedPermanently = false
                    onPermissionPermanentlyDenied()
                }
            )
        }
    }
}
