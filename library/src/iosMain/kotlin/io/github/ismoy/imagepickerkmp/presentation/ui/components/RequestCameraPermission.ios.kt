package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import io.github.ismoy.imagepickerkmp.domain.config.CameraPermissionDialogConfig
import io.github.ismoy.imagepickerkmp.domain.utils.AppLifecycleObserver
import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger
import io.github.ismoy.imagepickerkmp.domain.utils.openSettings
import io.github.ismoy.imagepickerkmp.domain.utils.requestCameraAccess
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVAuthorizationStatusRestricted
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType


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

    // rememberUpdatedState ensures AppLifecycleObserver always uses the latest callbacks
    // even if the composable has been recomposed — prevents stale lambda crash
    val currentOnResult by rememberUpdatedState(onResult)
    val currentOnPermissionPermanentlyDenied by rememberUpdatedState(onPermissionPermanentlyDenied)

    AppLifecycleObserver(
        onAppBecomeActive = {
            val currentStatus = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
            when {
                // Case 1: the user went to Settings and granted the permission
                currentStatus == AVAuthorizationStatusAuthorized -> {
                    isProcessingSettingsAction = false
                    hasNavigatedToSettings = false
                    showDialog = false
                    currentOnResult(true)
                }
                // Case 2: the user went to Settings (via dialog button) and did NOT grant the permission
                hasNavigatedToSettings && isProcessingSettingsAction -> {
                    isProcessingSettingsAction = false
                    hasNavigatedToSettings = false
                    showDialog = false
                    // Close picker — user decides when to try again
                    currentOnPermissionPermanentlyDenied()
                }
                // Case 3: returned from another app without having gone to Settings — do nothing
                else -> Unit
            }
        },
        onAppResignActive = {
            // If hasNavigatedToSettings=true was already set directly in onOpenSettings,
            // nothing needs to be done here — prevents race condition with dispatch_async
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
            DefaultLogger.logDebug("Camera hardware not available - likely running on iOS Simulator")
            onResult(false)
            return@LaunchedEffect
        }
        
        val currentStatus = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
        when (currentStatus) {
            AVAuthorizationStatusAuthorized -> {
                currentOnResult(true)
            }
            AVAuthorizationStatusNotDetermined -> {
                requestCameraAccess { granted ->
                    if (granted) {
                        currentOnResult(true)
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
            dialogConfig.customSettingsDialog.invoke(
                // onOpenSettings — mark navigation BEFORE opening Settings
                // to prevent race condition with dispatch_async from openSettings()
                {
                    if (!isProcessingSettingsAction) {
                        isProcessingSettingsAction = true
                        hasNavigatedToSettings = true
                        showDialog = false
                        openSettings()
                    }
                },
                // onDismiss — the user closed the dialog without going to settings
                {
                    showDialog = false
                    currentOnPermissionPermanentlyDenied()
                }
            )
        } else {
            CustomPermissionDialog(
                title = dialogConfig.titleDialogDenied,
                description = dialogConfig.descriptionDialogDenied,
                confirmationButtonText = dialogConfig.btnDialogDenied,
                cancelButtonText = dialogConfig.cancelButtonText ?: "Cancel",
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
