package io.github.ismoy.imagepickerkmp.ui

import android.Manifest
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import io.github.ismoy.imagepickerkmp.config.CameraPermissionDialogConfig
import io.github.ismoy.imagepickerkmp.core.CoreServices
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionStatus
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionType
import kotlinx.coroutines.launch

@Composable
internal actual fun RequestCameraPermission(
    dialogConfig: CameraPermissionDialogConfig,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    val permissionManager = remember(context) {
        CoreServices.permissionManager { activity }
    }
    var showRationale by remember { mutableStateOf(false) }
    var permissionDeniedPermanently by remember { mutableStateOf(false) }
    var hasCalledPermanentlyDenied by remember { mutableStateOf(false) }
    var waitingForSettingsReturn by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onResult(true)
        } else {
            scope.launch {
                val status = permissionManager.checkPermission(PermissionType.Camera)
                if (status is PermissionStatus.DeniedPermanently) {
                    permissionDeniedPermanently = true
                    showRationale = false
                } else {
                    showRationale = true
                    permissionDeniedPermanently = false
                }
            }
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        scope.launch {
            val status = permissionManager.checkPermission(PermissionType.Camera)
            if (status is PermissionStatus.Granted) {
                permissionDeniedPermanently = false
                showRationale = false
                waitingForSettingsReturn = false
                onResult(true)
            } else if (waitingForSettingsReturn) {
                waitingForSettingsReturn = false
                onPermissionPermanentlyDenied()
            } else if (!showRationale && !permissionDeniedPermanently) {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    if (showRationale) {
        if (dialogConfig.customDeniedDialog != null) {
            dialogConfig.customDeniedDialog.invoke(
                {
                    showRationale = false
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                {
                    showRationale = false
                    onPermissionPermanentlyDenied()
                }
            )
        } else {
            PhotosPermissionSheet(
                title = dialogConfig.titleDialogDenied,
                description = dialogConfig.descriptionDialogDenied,
                confirmationButtonText = dialogConfig.btnDialogDenied,
                cancelButtonText = dialogConfig.cancelButtonText,
                isSettingsDialog = false,
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
                {
                    hasCalledPermanentlyDenied = true
                    permissionDeniedPermanently = false
                    waitingForSettingsReturn = true
                    openAppSettings(context)
                },
                {
                    hasCalledPermanentlyDenied = true
                    permissionDeniedPermanently = false
                    onPermissionPermanentlyDenied()
                }
            )
        } else {
            PhotosPermissionSheet(
                title = dialogConfig.titleDialogConfig,
                description = dialogConfig.descriptionDialogConfig,
                confirmationButtonText = dialogConfig.btnDialogConfig,
                cancelButtonText = "Cancel",
                isSettingsDialog = true,
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
