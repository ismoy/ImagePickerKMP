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
import io.github.ismoy.imagepickerkmp.Constant.PERMISSION_PERMANENTLY_DENIED

@Composable
fun CameraCaptureView(
    activity: ComponentActivity,
    preference: CapturePhotoPreference = CapturePhotoPreference.FAST,
    onPhotoResult: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null
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
        val defaultConfig = PermissionConfig()
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
                    onError(PhotoCaptureException(PERMISSION_PERMANENTLY_DENIED))
                },
                onResult = {granted->
                    hasPermission = granted
                }
            )
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (photoResult == null) {
            CameraCapturePreview(
                cameraManager = cameraManager,
                preference = preference,
                onPhotoResult = { result ->
                    photoResult = result
                    playShutterSound()
                },
                context = context,
                onError = onError
            )
        } else {
            ImageConfirmationViewWithCustomButtons(
                result = photoResult!!,
                onConfirm = { onPhotoResult(it) },
                onRetry = { photoResult = null },
                customConfirmationView = customConfirmationView
            )
        }
    }


}