package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import platform.Foundation.setValue
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleActionSheet

@Suppress("FunctionNaming")
@Composable
actual fun ImagePickerLauncher(
    context: Any?,
    config: ImagePickerConfig
) {
    var showDialog by remember { mutableStateOf(true) }
    var askCameraPermission by remember { mutableStateOf(false) }
    var launchCamera by remember { mutableStateOf(false) }
    var launchGallery by remember { mutableStateOf(false) }

    handleImagePickerState(
        showDialog = showDialog,
        askCameraPermission = askCameraPermission,
        launchCamera = launchCamera,
        launchGallery = launchGallery,
        config = config,
        onDismissDialog = { showDialog = false },
        onRequestCameraPermission = { askCameraPermission = true },
        onRequestGallery = { launchGallery = true },
        onCameraPermissionGranted = {
            askCameraPermission = false
            launchCamera = true
        },
        onCameraPermissionDenied = { askCameraPermission = false },
        onCameraFinished = { launchCamera = false },
        onGalleryFinished = { launchGallery = false }
    )
}
@Suppress("LongParameterList","LongMethod")
@Composable
private fun handleImagePickerState(
    showDialog: Boolean,
    askCameraPermission: Boolean,
    launchCamera: Boolean,
    launchGallery: Boolean,
    config: ImagePickerConfig,
    onDismissDialog: () -> Unit,
    onRequestCameraPermission: () -> Unit,
    onRequestGallery: () -> Unit,
    onCameraPermissionGranted: () -> Unit,
    onCameraPermissionDenied: () -> Unit,
    onCameraFinished: () -> Unit,
    onGalleryFinished: () -> Unit
) {
    if (showDialog) {
        showImagePickerDialog(
            onTakePhoto = {
                onDismissDialog()
                onRequestCameraPermission()
            },
            onSelectFromGallery = {
                onDismissDialog()
                onRequestGallery()
            },
            onCancel = onDismissDialog
        )
    }

    if (askCameraPermission) {
        handleCameraPermission(
            onGranted = onCameraPermissionGranted,
            onDenied = onCameraPermissionDenied
        )
    }

    if (launchCamera) {
        launchCameraInternal(
            onPhotoCaptured = config.onPhotoCaptured,
            onError = config.onError,
            onFinish = onCameraFinished
        )
    }

    if (launchGallery) {
        launchGalleryInternal(
            onPhotoSelected = { result ->
                config.onPhotosSelected?.invoke(listOf(result))
                config.onPhotoCaptured(
                    CameraPhotoHandler.PhotoResult(
                        uri = result.uri,
                        width = result.width,
                        height = result.height
                    )
                )
            },
            onError = config.onError,
            onFinish = onGalleryFinished
        )
    }
}

@Composable
private fun showImagePickerDialog(
    onTakePhoto: () -> Unit,
    onSelectFromGallery: () -> Unit,
    onCancel: () -> Unit
) {
    LaunchedEffect(Unit) {
        val rootVC = ViewControllerProvider.getRootViewController() ?: return@LaunchedEffect
        val alert = UIAlertController.alertControllerWithTitle(
            title = "Select option",
            message = null,
            preferredStyle = UIAlertControllerStyleActionSheet
        )
        alert.addAction(
            UIAlertAction.actionWithTitle("Take photo", 0) { onTakePhoto() }
        )
        alert.addAction(
            UIAlertAction.actionWithTitle("Select from gallery", 0) { onSelectFromGallery() }
        )
        val spacerAction = UIAlertAction.actionWithTitle(" ", 0, null)
        spacerAction.setValue(false, forKey = "enabled")
        alert.addAction(spacerAction)
        alert.addAction(
            UIAlertAction.actionWithTitle("Cancel", 1) { onCancel() }
        )
        rootVC.presentViewController(alert, animated = true, completion = null)
    }
}

@Composable
private fun handleCameraPermission(
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
        customDeniedDialog = null,
        customSettingsDialog = null
    )
    RequestCameraPermission(
        dialogConfig = dialogConfig,
        onPermissionPermanentlyDenied = {},
        onResult = { granted -> if (granted) onGranted() else onDenied() },
        customPermissionHandler = null
    )
}

@Composable
private fun launchCameraInternal(
    onPhotoCaptured: (CameraPhotoHandler.PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    onFinish: () -> Unit
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
            }
        )
    }
}

@Composable
private fun launchGalleryInternal(
    onPhotoSelected: (GalleryPhotoHandler.PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    onFinish: () -> Unit
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
            }
        )
    }
}
