package io.github.ismoy.imagepickerkmp.ui

import android.Manifest
import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import io.github.ismoy.imagepickerkmp.config.CropConfig
import io.github.ismoy.imagepickerkmp.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.core.CoreServices

@Suppress("FunctionNaming")
@Composable
internal actual fun PlatformCameraRenderer(config: ImagePickerConfig) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    
    val permissionManager = remember(context, activity) {
        CoreServices.permissionManager { activity }
    }
    val manager = rememberPhotoCaptureManager(context)
    val state = rememberPlatformCameraState(config, permissionManager, manager)

    val permConfig = state.effectiveCamConfig.permissionAndConfirmationConfig
    val dialogConfig = defaultCameraPermissionDialogConfig().copy(
        customDeniedDialog = permConfig.customDeniedDialog,
        customSettingsDialog = permConfig.customSettingsDialog
    )

    if (state.cropCancelled && !state.showCropView) {
        SideEffect {
            state.onCropCancelledEffectHandled()
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        state.onResume()
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val success = result.resultCode == Activity.RESULT_OK
        state.onCameraResult(success)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        state.onPermissionResult(isGranted)
    }

    LaunchedEffect(Unit) {
        state.checkInitialPermission { permissionLauncher.launch(it) }
    }

    LaunchedEffect(state.permissionsGranted) {
        if (!state.permissionsGranted) return@LaunchedEffect
        state.startCamera { cameraLauncher.launch(it) }
    }

    if (state.showCropView && state.photoForCrop != null) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            ImageCropView(
                photoResult = state.photoForCrop!!,
                cropConfig = if (state.effectiveCamConfig.cropConfig.enabled) {
                    state.effectiveCamConfig.cropConfig
                } else {
                    CropConfig(enabled = true, circularCrop = true, squareCrop = true)
                },
                onAccept = { state.acceptCrop(it) },
                onCancel = { state.cancelCrop() }
            )
        }
    }

    if (state.showRationale) {
        if (dialogConfig.customDeniedDialog != null) {
            dialogConfig.customDeniedDialog.invoke(
                { state.acceptRationale { permissionLauncher.launch(it) } },
                { state.dismissRationale() }
            )
        } else {
            PhotosPermissionSheet(
                title = dialogConfig.titleDialogDenied,
                description = dialogConfig.descriptionDialogDenied,
                confirmationButtonText = dialogConfig.btnDialogDenied,
                cancelButtonText = dialogConfig.cancelButtonText,
                isSettingsDialog = false,
                onConfirm = { state.acceptRationale { permissionLauncher.launch(it) } },
                onCancel = { state.dismissRationale() }
            )
        }
    }

    if (state.showSettingsDialog) {
        if (dialogConfig.customSettingsDialog != null) {
            dialogConfig.customSettingsDialog.invoke(
                { state.acceptSettings(context) },
                { state.dismissSettings() }
            )
        } else {
            PhotosPermissionSheet(
                title = dialogConfig.titleDialogConfig,
                description = dialogConfig.descriptionDialogConfig,
                confirmationButtonText = dialogConfig.btnDialogConfig,
                cancelButtonText = dialogConfig.cancelButtonText,
                isSettingsDialog = true,
                onConfirm = { state.acceptSettings(context) },
                onCancel = { state.dismissSettings() }
            )
        }
    }
}
