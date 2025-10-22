package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.ismoy.imagepickerkmp.data.orchestrators.GalleryPickerOrchestrator
import io.github.ismoy.imagepickerkmp.data.orchestrators.PhotoCaptureOrchestrator
import io.github.ismoy.imagepickerkmp.domain.utils.ViewControllerProvider
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.CameraPermissionDialogConfig
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.domain.config.CropConfig
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import platform.Foundation.setValue
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleActionSheet


@Suppress("FunctionNaming","TrailingWhitespace")
@Composable
actual fun ImagePickerLauncher(
    config: ImagePickerConfig
) {
    var showDialog by remember { mutableStateOf(!config.directCameraLaunch) }
    var askCameraPermission by remember { mutableStateOf(config.directCameraLaunch) }
    var launchCamera by remember { mutableStateOf(false) }
    var launchGallery by remember { mutableStateOf(false) }
    
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
    val onGalleryFinished = { launchGallery = false }
    
    if (showDialog) {
        if (config.customPickerDialog != null) {
            config.customPickerDialog.invoke(
                {
                    showDialog = false
                    askCameraPermission = true
                },
                {
                    showDialog = false
                    launchGallery = true
                },
                {
                    showDialog = false
                    config.onDismiss()
                }
            )
        } else {
            showImagePickerDialog(
                config = config,
                onTakePhoto = {
                    showDialog = false
                    askCameraPermission = true
                },
                onSelectFromGallery = {
                    showDialog = false
                    launchGallery = true
                },
                onCancel = {
                    showDialog = false
                    config.onDismiss()
                }
            )
        }
    }

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
                if (config.enableCrop) {
                    selectedPhotoForCrop = result
                    showCropView = true
                } else {
                    config.onPhotoCaptured(result)
                }
            },
            onError = config.onError,
            onDismiss = config.onDismiss,
            onFinish = onCameraFinished,
            compressionLevel = config.cameraCaptureConfig.compressionLevel
        )
    }

    if (launchGallery) {
        launchGalleryInternal(
            onPhotoSelected = { result ->
                val photoResult = PhotoResult(
                    uri = result.uri,
                    width = result.width,
                    height = result.height,
                    fileName = result.fileName,
                    fileSize = result.fileSize
                )
                
                if (config.enableCrop) {
                    selectedPhotoForCrop = photoResult
                    showCropView = true
                } else {
                    config.onPhotosSelected?.invoke(listOf(result))
                    config.onPhotoCaptured(photoResult)
                }
            },
            onError = config.onError,
            onDismiss = config.onDismiss,
            onFinish = onGalleryFinished,
            compressionLevel = config.cameraCaptureConfig.compressionLevel
        )
    }
    
    if (showCropView && selectedPhotoForCrop != null) {
        val cropConfig = CropConfig(
            enabled = true,
            circularCrop = false,
            squareCrop = true,
            freeformCrop = true
        )
        
        ImageCropView(
            photoResult = selectedPhotoForCrop!!,
            cropConfig = cropConfig,
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
private fun showImagePickerDialog(
    config: ImagePickerConfig,
    onTakePhoto: () -> Unit,
    onSelectFromGallery: () -> Unit,
    onCancel: () -> Unit
) {
    LaunchedEffect(Unit) {
        val rootVC = ViewControllerProvider.getRootViewController() ?: return@LaunchedEffect
        val alert = UIAlertController.alertControllerWithTitle(
            title = config.dialogTitle,
            message = null,
            preferredStyle = UIAlertControllerStyleActionSheet
        )
        alert.addAction(
            UIAlertAction.actionWithTitle(config.takePhotoText, 0) { onTakePhoto() }
        )
        alert.addAction(
            UIAlertAction.actionWithTitle(config.selectFromGalleryText, 0) { onSelectFromGallery() }
        )
        val spacerAction = UIAlertAction.actionWithTitle(" ", 0, null)
        spacerAction.setValue(false, forKey = "enabled")
        alert.addAction(spacerAction)
        alert.addAction(
            UIAlertAction.actionWithTitle(config.cancelText, 1) { onCancel() }
        )
        rootVC.presentViewController(alert, animated = true, completion = null)
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
    compressionLevel: CompressionLevel? = null
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
            compressionLevel = compressionLevel
        )
    }
}

@Composable
private fun launchGalleryInternal(
    onPhotoSelected: (GalleryPhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    onFinish: () -> Unit,
    compressionLevel: CompressionLevel? = null
) {
    LaunchedEffect(Unit) {
        GalleryPickerOrchestrator.launchGallery(
            onPhotoSelected = {
                onPhotoSelected(it)
                onFinish()
            },
            onError = {
                onError(it)
                onFinish()
            },
            onDismiss = onDismiss,
            compressionLevel = compressionLevel
        )
    }
}
