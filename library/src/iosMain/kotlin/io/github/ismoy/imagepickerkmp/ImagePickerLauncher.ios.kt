package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler.PhotoResult
import platform.Foundation.setValue
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleActionSheet
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import io.github.ismoy.imagepickerkmp.Constant.DESCRIPTION_DIALOG_CONFIG
import io.github.ismoy.imagepickerkmp.Constant.TITLE_DIALOG_CONFIG
import io.github.ismoy.imagepickerkmp.Constant.TITLE_DIALOG_DENIED
@Composable
actual fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onPhotosSelected: ((List<GalleryPhotoHandler.PhotoResult>) -> Unit)?,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)?,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?,
    preference: CapturePhotoPreference?,
    dialogTitle: String,
    takePhotoText: String,
    selectFromGalleryText: String,
    cancelText: String,
    buttonColor: Color?,
    iconColor: Color?,
    buttonSize: Dp?,
    layoutPosition: String?,
    flashIcon: ImageVector?,
    switchCameraIcon: ImageVector?,
    captureIcon: ImageVector?,
    galleryIcon: ImageVector?,
    onCameraReady: (() -> Unit)?,
    onCameraSwitch: (() -> Unit)?,
    onPermissionError: ((Exception) -> Unit)?,
    onGalleryOpened: (() -> Unit)?,
    allowMultiple: Boolean,
    mimeTypes: List<String>
) {
    var showDialog by remember { mutableStateOf(true) }
    var askCameraPermission by remember { mutableStateOf(false) }
    var launchCamera by remember { mutableStateOf(false) }
    var launchGallery by remember { mutableStateOf(false) }

    if (showDialog) {
        LaunchedEffect(Unit) {
            val rootVC = ViewControllerProvider.getRootViewController() ?: return@LaunchedEffect
            val alert = UIAlertController.alertControllerWithTitle(
                title = dialogTitle,
                message = null,
                preferredStyle = UIAlertControllerStyleActionSheet
            )
            alert.addAction(
                UIAlertAction.actionWithTitle(
                    title = takePhotoText,
                    style = 0,
                    handler = {
                        showDialog = false
                        askCameraPermission = true
                    }
                )
            )
            alert.addAction(
                UIAlertAction.actionWithTitle(
                    title = selectFromGalleryText,
                    style = 0,
                    handler = {
                        showDialog = false
                        launchGallery = true
                    }
                )
            )
            val spacerAction = UIAlertAction.actionWithTitle(
                title = " ",
                style = 0,
                handler = null
            )
            spacerAction.setValue(false, forKey = "enabled")
            alert.addAction(spacerAction)
            alert.addAction(
                UIAlertAction.actionWithTitle(
                    title = cancelText,
                    style = 1,
                    handler = {
                        showDialog = false
                    }
                )
            )
            rootVC.presentViewController(alert, animated = true, completion = null)
        }
    }

    if (askCameraPermission) {
        RequestCameraPermission(
            titleDialogConfig = TITLE_DIALOG_CONFIG,
            descriptionDialogConfig = DESCRIPTION_DIALOG_CONFIG,
            btnDialogConfig = "Permitir",
            titleDialogDenied = TITLE_DIALOG_DENIED,
            descriptionDialogDenied = "Por favor, habilita el acceso a la cámara en Ajustes.",
            btnDialogDenied = "Abrir Ajustes",
            customDeniedDialog = null,
            customSettingsDialog = null,
            onPermissionPermanentlyDenied = {},
            onResult = { granted ->
                askCameraPermission = false
                if (granted) {
                    launchCamera = true
                }
            },
            customPermissionHandler = null
        )
    }

    if (launchCamera) {
        LaunchedEffect(Unit) {
            PhotoCaptureOrchestrator.launchCamera(
                onPhotoCaptured = { result ->
                    onPhotoCaptured(result)
                },
                onError = { exception ->
                    onError(exception)
                }
            )
            launchCamera = false
        }
    }

    if (launchGallery) {
        LaunchedEffect(Unit) {
            GalleryPickerOrchestrator.launchGallery(
                onPhotoSelected = { result ->
                    onPhotosSelected?.invoke(listOf(result))
                    onPhotoCaptured(
                        PhotoResult(
                            uri = result.uri,
                            width = result.width,
                            height = result.height
                        )
                    )
                },
                onError = { exception ->
                    onError(exception)
                }
            )
            launchGallery = false
        }
    }
}