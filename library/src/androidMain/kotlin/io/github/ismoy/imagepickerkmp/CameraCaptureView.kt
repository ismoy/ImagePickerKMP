package io.github.ismoy.imagepickerkmp

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
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler.PhotoResult
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp

@Composable
fun CameraCaptureView(
    activity: ComponentActivity,
    preference: CapturePhotoPreference = CapturePhotoPreference.FAST,
    onPhotoResult: (PhotoResult) -> Unit,
    onPhotosSelected: ((List<GalleryPhotoHandler.PhotoResult>) -> Unit)? = null,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    buttonColor: Color? = null,
    iconColor: Color? = null,
    buttonSize: Dp? = null,
    layoutPosition: String? = null,
    flashIcon: ImageVector? = null,
    switchCameraIcon: ImageVector? = null,
    captureIcon: ImageVector? = null,
    galleryIcon: ImageVector? = null,
    onCameraReady: (() -> Unit)? = null,
    onCameraSwitch: (() -> Unit)? = null,
    onPermissionError: ((Exception) -> Unit)? = null,
    onGalleryOpened: (() -> Unit)? = null,
    allowMultiple: Boolean = false,
    mimeTypes: List<String> = listOf("image/*")
){
    val context = LocalContext.current
    var photoResult by remember { mutableStateOf<PhotoResult?>(null) }
    var hasPermission by remember { mutableStateOf(false) }
    val cameraManager = remember { CameraXManager(context, activity) }

    DisposableEffect(Unit) {
        onDispose {
            cameraManager.stopCamera()
        }
    }

    if (!hasPermission){
        val defaultConfig = PermissionConfig.createLocalizedComposable()
        if (customPermissionHandler != null){
            customPermissionHandler(defaultConfig)
        }
        else {
            RequestCameraPermission(
                titleDialogConfig = defaultConfig.titleDialogConfig,
                descriptionDialogConfig = defaultConfig.descriptionDialogConfig,
                btnDialogConfig = defaultConfig.btnDialogConfig,
                titleDialogDenied = defaultConfig.titleDialogDenied,
                descriptionDialogDenied = defaultConfig.descriptionDialogDenied,
                btnDialogDenied = defaultConfig.btnDialogDenied,
                customDeniedDialog = null,
                customSettingsDialog = null,
                onPermissionPermanentlyDenied = {
                    onError(PhotoCaptureException(getStringResource(StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED)))
                },
                onResult = {granted->
                    hasPermission = granted
                }
            )
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (allowMultiple && onPhotosSelected != null) {
            GalleryPickerLauncher(
                context = activity,
                onPhotosSelected = { results -> onPhotosSelected(results) },
                onError = onError,
                allowMultiple = true,
                mimeTypes = mimeTypes
            )
        } else if (photoResult == null) {
            CameraCapturePreview(
                cameraManager = cameraManager,
                preference = preference,
                onPhotoResult = { result ->
                    photoResult = result
                    playShutterSound()
                },
                context = context,
                onError = onError,
                buttonColor = buttonColor,
                iconColor = iconColor,
                buttonSize = buttonSize,
                layoutPosition = layoutPosition,
                flashIcon = flashIcon,
                switchCameraIcon = switchCameraIcon,
                captureIcon = captureIcon,
                galleryIcon = galleryIcon,
                onCameraReady = onCameraReady,
                onCameraSwitch = onCameraSwitch,
                onPermissionError = onPermissionError,
                onGalleryOpened = onGalleryOpened
            )
        } else {
            ImageConfirmationViewWithCustomButtons(
                result = photoResult!!,
                onConfirm = { onPhotoResult(it) },
                onRetry = { photoResult = null },
                customConfirmationView = customConfirmationView,
                buttonColor = buttonColor,
                iconColor = iconColor,
                buttonSize = buttonSize,
                layoutPosition = layoutPosition,
                flashIcon = flashIcon,
                switchCameraIcon = switchCameraIcon,
                captureIcon = captureIcon,
                galleryIcon = galleryIcon
            )
        }
    }
}