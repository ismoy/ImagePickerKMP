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
import io.github.ismoy.imagepickerkmp.StringResource
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
    // Debug: Probar localización
    LaunchedEffect(Unit) {
        testLocalization()
    }
    
    // Forzar textos en inglés para iOS
    val englishDialogTitle = "Select option"
    val englishTakePhotoText = "Take photo"
    val englishSelectFromGalleryText = "Select from gallery"
    val englishCancelText = "Cancel"
    
    var showDialog by remember { mutableStateOf(true) }
    var askCameraPermission by remember { mutableStateOf(false) }
    var launchCamera by remember { mutableStateOf(false) }
    var launchGallery by remember { mutableStateOf(false) }

    if (showDialog) {
        LaunchedEffect(Unit) {
            val rootVC = ViewControllerProvider.getRootViewController() ?: return@LaunchedEffect
            val alert = UIAlertController.alertControllerWithTitle(
                title = englishDialogTitle,
                message = null,
                preferredStyle = UIAlertControllerStyleActionSheet
            )
            alert.addAction(
                UIAlertAction.actionWithTitle(
                    title = englishTakePhotoText,
                    style = 0,
                    handler = {
                        showDialog = false
                        askCameraPermission = true
                    }
                )
            )
            alert.addAction(
                UIAlertAction.actionWithTitle(
                    title = englishSelectFromGalleryText,
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
                    title = englishCancelText,
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
        // Forzar textos en inglés para los diálogos de permisos
        RequestCameraPermission(
            titleDialogConfig = "Camera permission required",
            descriptionDialogConfig = "Camera permission is required to capture photos. Please grant it in settings",
            btnDialogConfig = "Grant permission",
            titleDialogDenied = "Camera permission denied",
            descriptionDialogDenied = "Camera permission is required to capture photos. Please grant the permissions",
            btnDialogDenied = "Open settings",
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