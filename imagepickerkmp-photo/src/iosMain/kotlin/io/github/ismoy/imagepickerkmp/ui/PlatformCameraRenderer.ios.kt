package io.github.ismoy.imagepickerkmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.ismoy.imagepickerkmp.core.CoreServices
import io.github.ismoy.imagepickerkmp.config.CameraPermissionDialogConfig
import io.github.ismoy.imagepickerkmp.config.CropConfig
import io.github.ismoy.imagepickerkmp.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.I18nKonfig

@Suppress("FunctionNaming", "LongMethod", "ComplexMethod")
@Composable
internal actual fun PlatformCameraRenderer(
    config: ImagePickerConfig
) {
    val permissionManager = remember { CoreServices.permissionManager() }
    val state = rememberPlatformCameraState(config, permissionManager)

    if (state.cropCancelled && !state.showCropView) {
        SideEffect {
            state.onCropCancelledEffectHandled()
        }
    }

    AppLifecycleObserver(
        onAppBecomeActive = { state.onAppBecomeActive() },
        onAppResignActive = { state.onAppResignActive() }
    )

    LaunchedEffect(Unit) {
        state.launchCameraFlow()
    }

    if (state.showSettingsDialog) {
        val dialogConfig = CameraPermissionDialogConfig(
            titleDialogConfig = I18nKonfig.General.camera_permission_required,
            descriptionDialogConfig = I18nKonfig.General.camera_permission_description,
            btnDialogConfig = I18nKonfig.General.grant_permission,
            titleDialogDenied = I18nKonfig.General.camera_permission_denied,
            descriptionDialogDenied = I18nKonfig.General.camera_permission_denied_description,
            btnDialogDenied = I18nKonfig.General.gallery_btn_settings,
            customDeniedDialog = config.cameraCaptureConfig.permissionAndConfirmationConfig.customDeniedDialog,
            customSettingsDialog = config.cameraCaptureConfig.permissionAndConfirmationConfig.customSettingsDialog,
            cancelButtonText = I18nKonfig.Common.cancel_option,
            onCancelPermissionConfigIOS = config.cameraCaptureConfig.permissionAndConfirmationConfig.onCancelPermissionConfigIOS
        )
        
        if (dialogConfig.customSettingsDialog != null) {
            dialogConfig.customSettingsDialog.invoke({ state.openSettings() }, { state.dismissSettings() })
        } else {
            PhotosPermissionSheet(
                title = dialogConfig.titleDialogDenied,
                description = dialogConfig.descriptionDialogDenied,
                confirmationButtonText = dialogConfig.btnDialogDenied,
                cancelButtonText = dialogConfig.cancelButtonText,
                isSettingsDialog = true,
                onConfirm = { state.openSettings() },
                onCancel = dialogConfig.onCancelPermissionConfigIOS ?: { state.dismissSettings() }
            )
        }
    }

    if (state.showCropView && state.selectedPhotoForCrop != null) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            ImageCropView(
                photoResult = state.selectedPhotoForCrop!!,
                cropConfig = if (config.cameraCaptureConfig.cropConfig.enabled) {
                    config.cameraCaptureConfig.cropConfig
                } else if (config.enableCrop) {
                    CropConfig(enabled = true, circularCrop = true, squareCrop = true)
                } else {
                    config.cameraCaptureConfig.cropConfig
                },
                onAccept = { state.acceptCrop(it) },
                onCancel = { state.cancelCrop() },
                onSkip = { state.skipCrop() }
            )
        }
    }
}
