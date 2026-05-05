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
    // true when the user already went to Settings — on return it evaluates if permission was granted
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
            // The user returned from Settings without granting the permission → close the picker
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
                // onDismiss — the user closed the dialog without retrying
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
                // onOpenSettings — go to Settings and wait for the user to return
                {
                    hasCalledPermanentlyDenied = true
                    permissionDeniedPermanently = false  // hides the dialog immediately
                    waitingForSettingsReturn = true       // ON_RESUME will decide what to do
                    openAppSettings(context)
                },
                // onDismiss — the user closed the dialog without going to settings
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
