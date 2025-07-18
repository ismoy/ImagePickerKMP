
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

@Suppress("LongMethod")
@Composable
fun CameraCaptureView(
    activity: ComponentActivity,
    onPhotoResult: (PhotoResult) -> Unit,
    onPhotosSelected: ((List<GalleryPhotoHandler.PhotoResult>) -> Unit)? = null,
    onError: (Exception) -> Unit,
    cameraCaptureConfig: CameraCaptureConfig = CameraCaptureConfig(preference = CapturePhotoPreference.QUALITY)
) {
    val context = LocalContext.current
    var photoResult by remember { mutableStateOf<PhotoResult?>(null) }
    var hasPermission by remember { mutableStateOf(false) }
    val cameraManager = remember { CameraXManager(context, activity) }

    DisposableEffect(Unit) {
        onDispose { cameraManager.stopCamera() }
    }

    if (!hasPermission) {
        PermissionHandler(
            customPermissionHandler = cameraCaptureConfig.permissionAndConfirmationConfig.customPermissionHandler,
            onPermissionGranted = { hasPermission = true },
            onError = onError
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            cameraCaptureConfig.galleryConfig.allowMultiple && onPhotosSelected != null -> {
                GalleryPickerLauncher(
                    context = activity,
                    onPhotosSelected = { results -> onPhotosSelected(results) },
                    onError = onError,
                    allowMultiple = true,
                    mimeTypes = cameraCaptureConfig.galleryConfig.mimeTypes
                )
            }
            photoResult == null -> {
                CameraAndPreview(
                    cameraManager = cameraManager,
                    cameraCaptureConfig = cameraCaptureConfig,
                    context = context,
                    onPhotoResult = { result ->
                        photoResult = result
                        playShutterSound()
                    },
                    onError = onError
                )
            }
            else -> {
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
    onPermissionGranted: () -> Unit,
    onError: (Exception) -> Unit
) {
    val defaultConfig = PermissionConfig.createLocalizedComposable()
    val dialogConfig = defaultCameraPermissionDialogConfig()
    if (customPermissionHandler != null) {
        customPermissionHandler(defaultConfig)
    } else {
        RequestCameraPermission(
            dialogConfig = dialogConfig,
            onPermissionPermanentlyDenied = {
                onError(PhotoCaptureException(getStringResource(StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED)))
            },
            onResult = { granted -> onPermissionGranted() },
            customPermissionHandler = null
        )
    }
}

@Composable
private fun CameraAndPreview(
    cameraManager: CameraXManager,
    cameraCaptureConfig: CameraCaptureConfig,
    context: android.content.Context,
    onPhotoResult: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit
) {
    CameraCapturePreview(
        cameraManager = cameraManager,
        preference = cameraCaptureConfig.preference,
        onPhotoResult = onPhotoResult,
        context = context,
        onError = onError,
        previewConfig = CameraPreviewConfig(
            captureButtonSize = cameraCaptureConfig.captureButtonSize,
            uiConfig = cameraCaptureConfig.uiConfig,
            cameraCallbacks = cameraCaptureConfig.cameraCallbacks
        )
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
