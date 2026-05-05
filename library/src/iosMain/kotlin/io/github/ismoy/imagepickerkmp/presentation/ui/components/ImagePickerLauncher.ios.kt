package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.ismoy.imagepickerkmp.data.orchestrators.PhotoCaptureOrchestrator
import io.github.ismoy.imagepickerkmp.domain.config.CameraPermissionDialogConfig
import io.github.ismoy.imagepickerkmp.domain.config.CropConfig
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
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
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType


@Suppress("FunctionNaming", "LongMethod", "ComplexMethod")
@Composable
actual fun ImagePickerLauncher(
    config: ImagePickerConfig
) {
    var selectedPhotoForCrop by remember { mutableStateOf<PhotoResult?>(null) }
    var showCropView by remember { mutableStateOf(false) }
    var cropCancelled by remember { mutableStateOf(false) }

    // Permiso denegado permanentemente — necesitamos mostrar dialog de Settings
    var showSettingsDialog by remember { mutableStateOf(false) }
    var isProcessingSettingsAction by remember { mutableStateOf(false) }
    var hasNavigatedToSettings by remember { mutableStateOf(false) }

    val currentConfig by rememberUpdatedState(config)

    // SideEffect runs AFTER the composition has been applied to the UI tree,
    // never during it. This is critical: onDismiss() → notifyDismiss() → activeMode=None
    // is a mutation of an external MutableState. If done during composition (in the
    // composable body or in onDispose) Compose/iOS throws CoroutineExceptionHandler.
    // SideEffect guarantees the tree has been committed before the callback is executed.
    if (cropCancelled && !showCropView) {
        SideEffect {
            cropCancelled = false
            currentConfig.onDismiss()
        }
    }

    val onCameraFinished = { showCropView = false; selectedPhotoForCrop = null }

    // Observador de lifecycle para detectar retorno desde Settings
    io.github.ismoy.imagepickerkmp.domain.utils.AppLifecycleObserver(
        onAppBecomeActive = {
            val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
            when {
                status == AVAuthorizationStatusAuthorized -> {
                    isProcessingSettingsAction = false
                    hasNavigatedToSettings = false
                    showSettingsDialog = false
                    // Permission was granted from Settings — relaunch camera
                    // No direct action here: the user will need to tap again.
                    currentConfig.onDismiss()
                }
                hasNavigatedToSettings && isProcessingSettingsAction -> {
                    isProcessingSettingsAction = false
                    hasNavigatedToSettings = false
                    showSettingsDialog = false
                    currentConfig.onDismiss()
                }
                else -> Unit
            }
        },
        onAppResignActive = {
            if (isProcessingSettingsAction && !hasNavigatedToSettings) {
                hasNavigatedToSettings = true
                showSettingsDialog = false
            }
        }
    )

    // SINGLE LaunchedEffect: checks permission and launches camera without an intermediate frame.
    // Previously there were 2 chained LaunchedEffects (RequestCameraPermission → launchCameraInternal)
    // which added ~2 frames of latency. Now everything happens in the same block.
    LaunchedEffect(Unit) {
        if (!UIImagePickerController.isSourceTypeAvailable(
                UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
            )
        ) {
            currentConfig.onError(Exception("Camera not available on this device"))
            currentConfig.onDismiss()
            return@LaunchedEffect
        }

        val cameraCaptureConfig = currentConfig.cameraCaptureConfig
        val compressionLevel = cameraCaptureConfig.compressionLevel
        val includeExif = cameraCaptureConfig.includeExif

        val onPhotoCapturedHandler: (PhotoResult) -> Unit = { result ->
            val shouldShowCrop = cameraCaptureConfig.cropConfig.enabled || currentConfig.enableCrop
            if (shouldShowCrop) {
                currentConfig.onCropPending()
                selectedPhotoForCrop = result
                showCropView = true
            } else {
                currentConfig.onPhotoCaptured(result)
            }
        }

        when (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)) {
            AVAuthorizationStatusAuthorized -> {
                // Permission already granted — launch camera directly without any dialog
                PhotoCaptureOrchestrator.launchCamera(
                    onPhotoCaptured = onPhotoCapturedHandler,
                    onError = { e -> currentConfig.onError(e); currentConfig.onDismiss() },
                    onDismiss = { currentConfig.onDismiss() },
                    compressionLevel = compressionLevel,
                    includeExif = includeExif
                )
            }
            AVAuthorizationStatusNotDetermined -> {
                // First time — request system permission and launch if granted
                requestCameraAccess { granted ->
                    if (granted) {
                        PhotoCaptureOrchestrator.launchCamera(
                            onPhotoCaptured = onPhotoCapturedHandler,
                            onError = { e -> currentConfig.onError(e); currentConfig.onDismiss() },
                            onDismiss = { currentConfig.onDismiss() },
                            compressionLevel = compressionLevel,
                            includeExif = includeExif
                        )
                    } else {
                        showSettingsDialog = true
                    }
                }
            }
            AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> {
                // Ya denegado permanentemente — mostrar dialog de Settings
                showSettingsDialog = true
            }
        }
    }

    // Dialog de permiso denegado (solo aparece si el permiso fue denegado)
    if (showSettingsDialog) {
        val dialogConfig = CameraPermissionDialogConfig(
            titleDialogConfig = "Camera permission required",
            descriptionDialogConfig = "Camera permission is required to capture photos. Please grant it in settings",
            btnDialogConfig = "Grant permission",
            titleDialogDenied = "Camera permission denied",
            descriptionDialogDenied = "Camera permission is required to capture photos. Please grant the permissions",
            btnDialogDenied = "Open settings",
            customDeniedDialog = currentConfig.cameraCaptureConfig.permissionAndConfirmationConfig.customDeniedDialog,
            customSettingsDialog = currentConfig.cameraCaptureConfig.permissionAndConfirmationConfig.customSettingsDialog,
            cancelButtonText = currentConfig.cameraCaptureConfig.permissionAndConfirmationConfig.cancelButtonTextIOS,
            onCancelPermissionConfigIOS = currentConfig.cameraCaptureConfig.permissionAndConfirmationConfig.onCancelPermissionConfigIOS
        )
        val onOpenSettings = {
            if (!isProcessingSettingsAction) {
                isProcessingSettingsAction = true
                hasNavigatedToSettings = true
                showSettingsDialog = false
                openSettings()
            }
        }
        val onCancelDialog = {
            showSettingsDialog = false
            currentConfig.onDismiss()
        }
        if (dialogConfig.customSettingsDialog != null) {
            dialogConfig.customSettingsDialog.invoke(onOpenSettings, onCancelDialog)
        } else {
            CustomPermissionDialog(
                title = dialogConfig.titleDialogDenied,
                description = dialogConfig.descriptionDialogDenied,
                confirmationButtonText = dialogConfig.btnDialogDenied,
                cancelButtonText = dialogConfig.cancelButtonText ?: "Cancel",
                onConfirm = onOpenSettings,
                onCancel = dialogConfig.onCancelPermissionConfigIOS ?: onCancelDialog
            )
        }
    }

    // Local immutable capture: prevents a recomposition of the Dialog's inner scope
    // from seeing selectedPhotoForCrop=null while the Dialog is still being unmounted,
    // which would cause NullPointerException on !! when cancelling the crop.
    val photoForCrop = selectedPhotoForCrop
    if (showCropView && photoForCrop != null) {
        // Dialog creates its own UIWindow on iOS, always on top
        // of the host Scaffold / NavHost — resolves the Z-order issue.
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            ImageCropView(
                photoResult = photoForCrop,
                cropConfig = if (currentConfig.cameraCaptureConfig.cropConfig.enabled) {
                    currentConfig.cameraCaptureConfig.cropConfig
                } else if (currentConfig.enableCrop) {
                    CropConfig(enabled = true, circularCrop = true, squareCrop = true)
                } else {
                    currentConfig.cameraCaptureConfig.cropConfig
                },
                onAccept = { croppedResult ->
                    currentConfig.onPhotoCaptured(croppedResult)
                    showCropView = false
                    selectedPhotoForCrop = null
                },
                onCancel = {
                    // First close the Dialog (showCropView=false),
                    // then mark cancellation. The parent's DisposableEffect
                    // will call onDismiss after full unmounting.
                    showCropView = false
                    selectedPhotoForCrop = null
                    cropCancelled = true
                }
            )
        }
    }
}
