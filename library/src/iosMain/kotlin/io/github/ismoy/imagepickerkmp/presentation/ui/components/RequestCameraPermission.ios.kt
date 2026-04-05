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

    // rememberUpdatedState garantiza que AppLifecycleObserver siempre usa los callbacks
    // más recientes aunque el composable se haya recompuesto — evita stale lambda crash
    val currentOnResult by rememberUpdatedState(onResult)
    val currentOnPermissionPermanentlyDenied by rememberUpdatedState(onPermissionPermanentlyDenied)

    AppLifecycleObserver(
        onAppBecomeActive = {
            val currentStatus = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
            when {
                // Caso 1: el usuario fue a Settings y concedió el permiso
                currentStatus == AVAuthorizationStatusAuthorized -> {
                    isProcessingSettingsAction = false
                    hasNavigatedToSettings = false
                    showDialog = false
                    currentOnResult(true)
                }
                // Caso 2: el usuario fue a Settings (via botón del dialog) y NO concedió el permiso
                hasNavigatedToSettings && isProcessingSettingsAction -> {
                    isProcessingSettingsAction = false
                    hasNavigatedToSettings = false
                    showDialog = false
                    // Cerrar picker — usuario decide cuándo volver a intentarlo
                    currentOnPermissionPermanentlyDenied()
                }
                // Caso 3: volvió de otra app sin haber ido a Settings — no hacer nada
                else -> Unit
            }
        },
        onAppResignActive = {
            // Si ya marcamos hasNavigatedToSettings=true directamente en onOpenSettings,
            // no es necesario hacer nada aquí — evita condición de carrera con dispatch_async
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
                // onOpenSettings — marcar navegación ANTES de abrir Settings
                // para evitar condición de carrera con dispatch_async de openSettings()
                {
                    if (!isProcessingSettingsAction) {
                        isProcessingSettingsAction = true
                        hasNavigatedToSettings = true
                        showDialog = false
                        openSettings()
                    }
                },
                // onDismiss — el usuario cerró el dialog sin ir a settings
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
