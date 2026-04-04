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

    // SideEffect se ejecuta DESPUÉS de que la composición fue aplicada al árbol de UI,
    // nunca durante ella. Esto es crítico: onDismiss() → notifyDismiss() → activeMode=None
    // es una mutación de MutableState externo. Si se hace durante la composición (en el
    // cuerpo del composable o en onDispose) Compose/iOS lanza CoroutineExceptionHandler.
    // SideEffect garantiza que el árbol ya fue commiteado antes de ejecutar el callback.
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
                    // El permiso fue concedido desde Settings — relanzar cámara
                    // No hay acción directa aquí: el usuario tendrá que pulsar de nuevo.
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

    // UN SOLO LaunchedEffect: verifica permiso y lanza cámara sin frame intermedio.
    // Antes había 2 LaunchedEffect en cadena (RequestCameraPermission → launchCameraInternal)
    // lo que añadía ~2 frames de latencia. Ahora todo ocurre en el mismo bloque.
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
                // Permiso ya concedido — lanzar cámara directamente sin ningún dialog
                PhotoCaptureOrchestrator.launchCamera(
                    onPhotoCaptured = onPhotoCapturedHandler,
                    onError = { e -> currentConfig.onError(e); currentConfig.onDismiss() },
                    onDismiss = { currentConfig.onDismiss() },
                    compressionLevel = compressionLevel,
                    includeExif = includeExif
                )
            }
            AVAuthorizationStatusNotDetermined -> {
                // Primera vez — pedir permiso al sistema y lanzar si se concede
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

    // Captura local inmutable: evita que una recomposición del scope interno del Dialog
    // vea selectedPhotoForCrop=null mientras el Dialog aún está desmontándose,
    // lo que causaría NullPointerException en el !! al cancelar el crop.
    val photoForCrop = selectedPhotoForCrop
    if (showCropView && photoForCrop != null) {
        // Dialog crea una UIWindow propia en iOS que siempre está por encima
        // del Scaffold / NavHost del host — resuelve el problema de Z-order.
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
                    // Primero cerrar el Dialog (showCropView=false),
                    // luego marcar cancelación. El bloque DisposableEffect
                    // del padre llamará onDismiss tras el desmontaje completo.
                    showCropView = false
                    selectedPhotoForCrop = null
                    cropCancelled = true
                }
            )
        }
    }
}
