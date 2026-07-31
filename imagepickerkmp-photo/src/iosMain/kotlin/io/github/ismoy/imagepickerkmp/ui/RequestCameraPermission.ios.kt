package io.github.ismoy.imagepickerkmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import io.github.ismoy.imagepickerkmp.config.CameraPermissionDialogConfig
import io.github.ismoy.imagepickerkmp.core.CoreServices
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionStatus
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionType
import io.github.ismoy.imagepickerkmp.logger.PhotoLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType

@Composable
internal actual fun RequestCameraPermission(
    dialogConfig: CameraPermissionDialogConfig,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
) {
    var showDialog by remember { mutableStateOf(false) }
    var isPermissionDeniedPermanently by remember { mutableStateOf(false) }
    var isProcessingSettingsAction by remember { mutableStateOf(false) }
    var hasNavigatedToSettings by remember { mutableStateOf(false) }

    val permissionManager = remember { CoreServices.permissionManager() }

    val currentOnResult by rememberUpdatedState(onResult)
    val currentOnPermissionPermanentlyDenied by rememberUpdatedState(onPermissionPermanentlyDenied)

    AppLifecycleObserver(
        onAppBecomeActive = {
            CoroutineScope(Dispatchers.Main).launch {
                val status = permissionManager.checkPermission(PermissionType.Camera)
                when {
                    status is PermissionStatus.Granted -> {
                        isProcessingSettingsAction = false
                        hasNavigatedToSettings = false
                        showDialog = false
                        currentOnResult(true)
                    }
                    hasNavigatedToSettings && isProcessingSettingsAction -> {
                        isProcessingSettingsAction = false
                        hasNavigatedToSettings = false
                        showDialog = false
                        currentOnPermissionPermanentlyDenied()
                    }
                    else -> Unit
                }
            }
        },
        onAppResignActive = {
            if (isProcessingSettingsAction && !hasNavigatedToSettings) {
                hasNavigatedToSettings = true
                showDialog = false
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!UIImagePickerController.isSourceTypeAvailable(
            UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
        )) {
            PhotoLogger.debug("Camera hardware not available - likely running on iOS Simulator")
            onResult(false)
            return@LaunchedEffect
        }

        when (val status = permissionManager.checkPermission(PermissionType.Camera)) {
            is PermissionStatus.Granted -> currentOnResult(true)
            is PermissionStatus.Denied -> {
                val requested = permissionManager.requestPermission(PermissionType.Camera)
                if (requested is PermissionStatus.Granted) {
                    currentOnResult(true)
                } else {
                    isPermissionDeniedPermanently = true
                    showDialog = true
                }
            }
            is PermissionStatus.DeniedPermanently -> {
                isPermissionDeniedPermanently = true
                showDialog = true
            }
        }
    }

    if (showDialog && isPermissionDeniedPermanently) {
        if (dialogConfig.customSettingsDialog != null) {
            dialogConfig.customSettingsDialog.invoke(
                {
                    if (!isProcessingSettingsAction) {
                        isProcessingSettingsAction = true
                        hasNavigatedToSettings = true
                        showDialog = false
                        openSettings()
                    }
                },
                {
                    showDialog = false
                    currentOnPermissionPermanentlyDenied()
                }
            )
        } else {
            PhotosPermissionSheet(
                title = dialogConfig.titleDialogDenied,
                description = dialogConfig.descriptionDialogDenied,
                confirmationButtonText = dialogConfig.btnDialogDenied,
                cancelButtonText = dialogConfig.cancelButtonText,
                isSettingsDialog = true,
                onConfirm = {
                    if (!isProcessingSettingsAction) {
                        isProcessingSettingsAction = true
                        hasNavigatedToSettings = true
                        showDialog = false
                        openSettings()
                    }
                },
                onCancel = dialogConfig.onCancelPermissionConfigIOS ?: {
                    showDialog = false
                    currentOnPermissionPermanentlyDenied()
                }
            )
        }
    }
}
