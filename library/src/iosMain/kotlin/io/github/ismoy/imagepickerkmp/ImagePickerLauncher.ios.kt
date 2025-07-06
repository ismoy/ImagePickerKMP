package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler.PhotoResult

@Composable
actual fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)?,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)?,
    preference: CapturePhotoPreference?

) {

    var permissionState by remember { mutableStateOf("checking") }

    if (permissionState == "checking") {
        val defaultConfig = PermissionConfig()
        if (customPermissionHandler != null) {
            customPermissionHandler(defaultConfig)
        } else {
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
                    onError(PhotoCaptureException("Camera permission permanently denied"))
                },
                onResult = { granted ->
                    if (granted) {
                        permissionState = "granted"
                    } else {
                        permissionState = "denied"
                        onError(PhotoCaptureException("Camera permission denied"))
                    }
                }
            )
        }
        return
    }

    LaunchedEffect(permissionState) {
        if (permissionState == "granted") {
            try {
                permissionState = "launched"
                PhotoCaptureOrchestrator.launchCamera(
                    onPhotoCaptured = { result ->
                        onPhotoCaptured(result)
                    },
                    onError = { error ->
                        println("ImagePickerLauncher: Error in camera: ${error.message}")
                        onError(error)
                    }
                )
            } catch (e: Exception) {
                println("ImagePickerLauncher: Exception in camera launch: ${e.message}")
                onError(PhotoCaptureException("Failed to launch camera: ${e.message}"))
            }
        }
    }
}