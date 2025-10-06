package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.ismoy.imagepickerkmp.domain.config.CameraPermissionDialogConfig
import io.github.ismoy.imagepickerkmp.domain.utils.AppLifecycleObserver
import io.github.ismoy.imagepickerkmp.domain.utils.openSettings
import io.github.ismoy.imagepickerkmp.domain.utils.requestCameraAccess
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVAuthorizationStatusRestricted
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType


@Composable
actual fun RequestCameraPermission(
    dialogConfig: CameraPermissionDialogConfig,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
) {
    var showDialog by remember { mutableStateOf(false) }
    var isPermissionDeniedPermanently by remember { mutableStateOf(false) }
    var isProcessingSettingsAction by remember { mutableStateOf(false) }
    var hasNavigatedToSettings by remember { mutableStateOf(false) }

    AppLifecycleObserver(
        onAppBecomeActive = {
            if (hasNavigatedToSettings && isProcessingSettingsAction) {
                val currentStatus = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
                if (currentStatus == AVAuthorizationStatusAuthorized) {
                    showDialog = false
                    isProcessingSettingsAction = false
                    hasNavigatedToSettings = false
                    onResult(true)
                } else {
                    isProcessingSettingsAction = false
                }
            }
        },
        onAppResignActive = {
            if (isProcessingSettingsAction) {
                hasNavigatedToSettings = true
            }
        }
    )

    LaunchedEffect(Unit) {
        val currentStatus = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
        when (currentStatus) {
            AVAuthorizationStatusAuthorized -> {
                onResult(true)
            }
            AVAuthorizationStatusNotDetermined -> {
                requestCameraAccess { granted ->
                    if (granted) {
                        onResult(true)
                    } else {
                        isPermissionDeniedPermanently = true
                        showDialog = true
                    }
                }
            }
            AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> {
                isPermissionDeniedPermanently = true
                showDialog = true
            }
        }
    }

    if (showDialog && isPermissionDeniedPermanently) {
        if (dialogConfig.customSettingsDialog != null) {
            dialogConfig.customSettingsDialog { 
                if (!isProcessingSettingsAction) {
                    isProcessingSettingsAction = true
                    openSettings()
                }
            }
        } else {
            CustomPermissionDialog(
                title = dialogConfig.titleDialogDenied,
                description = dialogConfig.descriptionDialogDenied,
                confirmationButtonText = dialogConfig.btnDialogDenied,
                onConfirm = {
                    if (!isProcessingSettingsAction) {
                        isProcessingSettingsAction = true
                        openSettings()
                    }
                }
            )
        }
        LaunchedEffect(Unit) {
            onPermissionPermanentlyDenied()
        }
    }
}
