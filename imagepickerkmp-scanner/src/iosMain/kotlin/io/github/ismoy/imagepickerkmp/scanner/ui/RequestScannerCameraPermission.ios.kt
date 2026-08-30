package io.github.ismoy.imagepickerkmp.scanner.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import io.github.ismoy.imagepickerkmp.core.CoreServices
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionStatus
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionType
import io.github.ismoy.imagepickerkmp.scanner.openAppSettings
import io.github.ismoy.imagepickerkmp.scanner.permission.ScannerPermissionConfig
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
internal actual fun RequestScannerCameraPermission(
    dialogConfig: ScannerPermissionConfig,
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
    val scope = rememberCoroutineScope()

    AppLifecycleObserver(
        onAppBecomeActive = {
            scope.launch {
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
            currentOnPermissionPermanentlyDenied()
            return@LaunchedEffect
        }

        when (permissionManager.checkPermission(PermissionType.Camera)) {
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
                isProcessingSettingsAction = false
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
                        openAppSettings(null)
                    }
                },
                {
                    showDialog = false
                    currentOnPermissionPermanentlyDenied()
                }
            )
        } else {
            ScannerPermissionSheet(
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
                        openAppSettings(null)
                    }
                },
                onCancel = {
                    showDialog = false
                    currentOnPermissionPermanentlyDenied()
                }
            )
        }
    }
}
