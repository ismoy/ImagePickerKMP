package io.github.ismoy.imagepickerkmp.presentation.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import imagepickerkmp.library.generated.resources.Res
import imagepickerkmp.library.generated.resources.camera_permission_permanently_denied
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.CameraPreviewConfig
import io.github.ismoy.imagepickerkmp.domain.config.PermissionConfig
import io.github.ismoy.imagepickerkmp.domain.config.UiConfig
import io.github.ismoy.imagepickerkmp.domain.exceptions.PhotoCaptureException
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.presentation.ui.components.CameraCapturePreview
import io.github.ismoy.imagepickerkmp.presentation.ui.components.ImageConfirmationViewWithCustomButtons
import io.github.ismoy.imagepickerkmp.presentation.ui.components.ImageCropView
import io.github.ismoy.imagepickerkmp.presentation.ui.components.RequestCameraPermission
import io.github.ismoy.imagepickerkmp.presentation.ui.utils.defaultCameraPermissionDialogConfig
import io.github.ismoy.imagepickerkmp.presentation.ui.utils.rememberCameraManager
import io.github.ismoy.imagepickerkmp.presentation.ui.utils.rememberImagePickerViewModel
import org.jetbrains.compose.resources.stringResource

@Suppress("LongMethod", "LongParameterList")
@Composable
fun CameraCaptureView(
    activity: ComponentActivity,
    onPhotoResult: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit = {},
    cameraCaptureConfig: CameraCaptureConfig = CameraCaptureConfig(preference = CapturePhotoPreference.QUALITY),
    enableCrop: Boolean = false
) {
    val context = LocalContext.current
    var photoResult by remember { mutableStateOf<PhotoResult?>(null) }
    var showCropView by remember { mutableStateOf(false) }
    var hasPermission by remember { mutableStateOf(false) }

    val imagePickerViewModel = rememberImagePickerViewModel()
    val cameraManager = rememberCameraManager(context, activity)

    if (cameraManager == null) {
        onError(Exception("Camera dependencies not available. Please check camera permissions and device capabilities."))
        return
    }

    DisposableEffect(Unit) {
        onDispose { cameraManager.stopCamera() }
    }

    if (!hasPermission) {
        PermissionHandler(
            customPermissionHandler = cameraCaptureConfig.permissionAndConfirmationConfig.customPermissionHandler,
            customDeniedDialog = cameraCaptureConfig.permissionAndConfirmationConfig.customDeniedDialog,
            customSettingsDialog = cameraCaptureConfig.permissionAndConfirmationConfig.customSettingsDialog,
            onPermissionGranted = { hasPermission = true },
            onDismiss = onDismiss,
            onError = { exception ->
                imagePickerViewModel.onError(exception)
                onError(exception)
            }
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            // 1. Crop takes absolute priority — must be checked FIRST before confirmation
            enableCrop && showCropView && photoResult != null -> {
                ImageCropView(
                    photoResult = photoResult!!,
                    cropConfig = cameraCaptureConfig.cropConfig,
                    onAccept = { croppedResult: PhotoResult ->
                        showCropView = false
                        onPhotoResult(croppedResult)
                    },
                    onCancel = {
                        showCropView = false
                        photoResult = null
                        onDismiss()
                    }
                )
            }

            // 2. Camera preview — shown when no photo captured yet, or after crop cancel
            photoResult == null -> {
                CameraAndPreview(
                    cameraCaptureConfig = cameraCaptureConfig,
                    onPhotoResult = { result ->
                        photoResult = result
                        if (enableCrop) {
                            showCropView = true
                        } else {
                            if (cameraCaptureConfig.permissionAndConfirmationConfig.skipConfirmation) {
                                onPhotoResult(result)
                                photoResult = null
                            }
                        }
                    },
                    onError = { exception: Exception ->
                        imagePickerViewModel.onError(exception)
                        onError(exception)
                    }
                )
            }

            // 3. Confirmation — only reached when enableCrop=false and skipConfirmation=false
            photoResult != null && !enableCrop && !cameraCaptureConfig.permissionAndConfirmationConfig.skipConfirmation -> {
                ConfirmationView(
                    photoResult = photoResult!!,
                    onConfirm = { onPhotoResult(it) },
                    onRetry = { photoResult = null },
                    customConfirmationView = cameraCaptureConfig.permissionAndConfirmationConfig.customConfirmationView,
                    uiConfig = cameraCaptureConfig.uiConfig
                )
            }
        }
    }
}

@Composable
private fun PermissionHandler(
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customDeniedDialog: (@Composable (onRetry: () -> Unit, onDismiss: () -> Unit) -> Unit)? = null,
    customSettingsDialog: (@Composable (onOpenSettings: () -> Unit, onDismiss: () -> Unit) -> Unit)? = null,
    onPermissionGranted: () -> Unit,
    onDismiss: () -> Unit = {},
    onError: (Exception) -> Unit
) {
    val defaultConfig = PermissionConfig.createLocalizedComposable()
    val dialogConfig = defaultCameraPermissionDialogConfig().copy(
        customDeniedDialog = customDeniedDialog,
        customSettingsDialog = customSettingsDialog
    )
    val cameraPermissionPermanentlyDeniedMsg =
        stringResource(Res.string.camera_permission_permanently_denied)

    if (customPermissionHandler != null) {
        customPermissionHandler(defaultConfig)
    } else {
        RequestCameraPermission(
            dialogConfig = dialogConfig,
            onPermissionPermanentlyDenied = {
                // Cuando el permiso es denegado permanentemente, cerrar el picker
                // para que la UI no quede bloqueada esperando una acción que ya no vendrá
                onDismiss()
                onError(PhotoCaptureException(cameraPermissionPermanentlyDeniedMsg))
            },
            onResult = { _: Boolean -> onPermissionGranted() },
            customPermissionHandler = null
        )
    }
}

@Composable
private fun CameraAndPreview(
    cameraCaptureConfig: CameraCaptureConfig,
    onPhotoResult: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit
) {
    CameraCapturePreview(
        preference = cameraCaptureConfig.preference,
        onPhotoResult = onPhotoResult,
        onError = onError,
        previewConfig = CameraPreviewConfig(
            captureButtonSize = cameraCaptureConfig.captureButtonSize,
            uiConfig = cameraCaptureConfig.uiConfig,
            cameraCallbacks = cameraCaptureConfig.cameraCallbacks
        ),
        compressionLevel = cameraCaptureConfig.compressionLevel,
        includeExif = cameraCaptureConfig.includeExif,
        redactGpsData = cameraCaptureConfig.redactGpsData
    )
}

@Composable
private fun ConfirmationView(
    photoResult: PhotoResult,
    onConfirm: (PhotoResult) -> Unit,
    onRetry: () -> Unit,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    uiConfig: UiConfig = UiConfig()
) {
    ImageConfirmationViewWithCustomButtons(
        result = photoResult,
        onConfirm = onConfirm,
        onRetry = onRetry,
        customConfirmationView = customConfirmationView,
        uiConfig = uiConfig
    )
}
