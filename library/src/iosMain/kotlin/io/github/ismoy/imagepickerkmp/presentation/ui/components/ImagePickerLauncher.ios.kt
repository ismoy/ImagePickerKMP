package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.ismoy.imagepickerkmp.data.orchestrators.PhotoCaptureOrchestrator
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.CameraPermissionDialogConfig
import io.github.ismoy.imagepickerkmp.domain.config.CropConfig
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult



@Suppress("FunctionNaming","TrailingWhitespace")
@Composable
actual fun ImagePickerLauncher(
    config: ImagePickerConfig
) {
    var askCameraPermission by remember { mutableStateOf(true) }
    var launchCamera by remember { mutableStateOf(false) }
    
    var selectedPhotoForCrop by remember { mutableStateOf<PhotoResult?>(null) }
    var showCropView by remember { mutableStateOf(false) }
    
    val onCameraPermissionGranted = {
        askCameraPermission = false
        launchCamera = true
    }
    val onCameraPermissionDenied = {
        askCameraPermission = false
        config.onDismiss()
    }
    val onCameraFinished = { launchCamera = false }

    if (askCameraPermission) {
        handleCameraPermission(
            cameraCaptureConfig = config.cameraCaptureConfig,
            onGranted = onCameraPermissionGranted,
            onDenied = onCameraPermissionDenied
        )
    }

    if (launchCamera) {
        launchCameraInternal(
            onPhotoCaptured = { result ->
                val shouldShowCrop = config.cameraCaptureConfig.cropConfig.enabled || config.enableCrop
                if (shouldShowCrop) {
                    selectedPhotoForCrop = result
                    showCropView = true
                } else {
                    config.onPhotoCaptured(result)
                }
            },
            onError = config.onError,
            onDismiss = config.onDismiss,
            onFinish = onCameraFinished,
            compressionLevel = config.cameraCaptureConfig.compressionLevel,
            includeExif = config.cameraCaptureConfig.includeExif
        )
    }
    
    if (showCropView && selectedPhotoForCrop != null) {        
        ImageCropView(
            photoResult = selectedPhotoForCrop!!,
            cropConfig = if (config.cameraCaptureConfig.cropConfig.enabled) {
                config.cameraCaptureConfig.cropConfig
            } else if (config.enableCrop) {
                CropConfig(
                    enabled = true,
                    circularCrop = true, 
                    squareCrop = true
                )
            } else {
                config.cameraCaptureConfig.cropConfig
            },
            onAccept = { croppedResult ->
                config.onPhotoCaptured(croppedResult)
                showCropView = false
                selectedPhotoForCrop = null
            },
            onCancel = {
                showCropView = false
                selectedPhotoForCrop = null
                config.onDismiss()
            }
        )
    }
}



@Composable
private fun handleCameraPermission(
    cameraCaptureConfig: CameraCaptureConfig,
    onGranted: () -> Unit,
    onDenied: () -> Unit
) {
    val dialogConfig = CameraPermissionDialogConfig(
        titleDialogConfig = "Camera permission required",
        descriptionDialogConfig = "Camera permission is required to capture photos. Please grant it in settings",
        btnDialogConfig = "Grant permission",
        titleDialogDenied = "Camera permission denied",
        descriptionDialogDenied = "Camera permission is required to capture photos. Please grant the permissions",
        btnDialogDenied = "Open settings",
        customDeniedDialog = cameraCaptureConfig.permissionAndConfirmationConfig.customDeniedDialog,
        customSettingsDialog = cameraCaptureConfig.permissionAndConfirmationConfig.customSettingsDialog,
        cancelButtonText = cameraCaptureConfig.permissionAndConfirmationConfig.cancelButtonTextIOS,
        onCancelPermissionConfigIOS = cameraCaptureConfig.permissionAndConfirmationConfig.onCancelPermissionConfigIOS
    )
    RequestCameraPermission(
        dialogConfig = dialogConfig,
        onPermissionPermanentlyDenied = {},
        onResult = { granted: Boolean -> if (granted) onGranted() else onDenied() },
        customPermissionHandler = null
    )
}

@Composable
private fun launchCameraInternal(
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    onFinish: () -> Unit,
    compressionLevel: CompressionLevel? = null,
    includeExif: Boolean = false
) {
    LaunchedEffect(Unit) {
        PhotoCaptureOrchestrator.launchCamera(
            onPhotoCaptured = {
                onPhotoCaptured(it)
                onFinish()
            },
            onError = {
                onError(it)
                onFinish()
            },
            onDismiss = {
                onDismiss()
                onFinish()
            },
            compressionLevel = compressionLevel,
            includeExif = includeExif
        )
    }
}
