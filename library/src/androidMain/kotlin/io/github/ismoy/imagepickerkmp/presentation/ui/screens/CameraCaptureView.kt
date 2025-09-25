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
import io.github.ismoy.imagepickerkmp.data.camera.CameraXManager
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import io.github.ismoy.imagepickerkmp.domain.config.CameraPreviewConfig
import io.github.ismoy.imagepickerkmp.domain.config.CropConfig
import io.github.ismoy.imagepickerkmp.domain.config.PermissionConfig
import io.github.ismoy.imagepickerkmp.domain.config.UiConfig
import io.github.ismoy.imagepickerkmp.domain.exceptions.PhotoCaptureException
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.domain.utils.defaultCameraPermissionDialogConfig
import io.github.ismoy.imagepickerkmp.domain.utils.playShutterSound
import io.github.ismoy.imagepickerkmp.presentation.resources.StringResource
import io.github.ismoy.imagepickerkmp.presentation.resources.getStringResource
import io.github.ismoy.imagepickerkmp.presentation.ui.components.CameraCapturePreview
import io.github.ismoy.imagepickerkmp.presentation.ui.components.GalleryPickerLauncher
import io.github.ismoy.imagepickerkmp.presentation.ui.components.ImageConfirmationViewWithCustomButtons
import io.github.ismoy.imagepickerkmp.presentation.ui.components.ImageCropView
import io.github.ismoy.imagepickerkmp.presentation.ui.components.RequestCameraPermission
import io.github.ismoy.imagepickerkmp.presentation.viewModel.ImagePickerViewModel

@Suppress("LongMethod","LongParameterList")
@Composable
fun CameraCaptureView(
    activity: ComponentActivity,
    onPhotoResult: (PhotoResult) -> Unit,
    onPhotosSelected: ((List<GalleryPhotoResult>) -> Unit)? = null,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit = {},
    cameraCaptureConfig: CameraCaptureConfig = CameraCaptureConfig(preference = CapturePhotoPreference.QUALITY),
    enableCrop: Boolean = false
) {
    val context = LocalContext.current
    var photoResult by remember { mutableStateOf<PhotoResult?>(null) }
    var showCropView by remember { mutableStateOf(false) }
    var hasPermission by remember { mutableStateOf(false) }
    
    val imagePickerViewModel: ImagePickerViewModel = koinInject()
    
     val cameraManager: CameraXManager = koinInject { parametersOf(context, activity) }

    DisposableEffect(Unit) {
        onDispose { cameraManager.stopCamera() }
    }

    if (!hasPermission) {
        PermissionHandler(
            customPermissionHandler = cameraCaptureConfig.permissionAndConfirmationConfig.customPermissionHandler,
            customDeniedDialog = cameraCaptureConfig.permissionAndConfirmationConfig.customDeniedDialog,
            customSettingsDialog = cameraCaptureConfig.permissionAndConfirmationConfig.customSettingsDialog,
            onPermissionGranted = { hasPermission = true },
            onError = { exception ->
                imagePickerViewModel.onError(exception)
                onError(exception)
            }
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            enableCrop && showCropView && photoResult != null -> {
                ImageCropView(
                    photoResult = photoResult!!,
                    cropConfig = CropConfig(
                        enabled = true,
                        circularCrop = false,
                        squareCrop = true
                    ),
                    onAccept = { croppedResult: PhotoResult ->
                        showCropView = false
                        onPhotoResult(croppedResult)
                    },
                    onCancel = {
                        showCropView = false
                        photoResult = null
                    }
                )
            }
            cameraCaptureConfig.galleryConfig.allowMultiple && onPhotosSelected != null -> {
                GalleryPickerLauncher(
                    onPhotosSelected = { results: List<GalleryPhotoResult> -> onPhotosSelected(results) },
                    onError = { exception: Exception ->
                        imagePickerViewModel.onError(exception)
                        onError(exception)
                    },
                    onDismiss = onDismiss,
                    allowMultiple = true,
                    mimeTypes = cameraCaptureConfig.galleryConfig.mimeTypes
                )
            }
            photoResult == null -> {
                CameraAndPreview(
                    cameraCaptureConfig = cameraCaptureConfig,
                    onPhotoResult = { result ->
                        photoResult = result
                        if (enableCrop) {
                            showCropView = true
                        } else {
                            playShutterSound()
                        }
                    },
                    onError = { exception: Exception ->
                        imagePickerViewModel.onError(exception)
                        onError(exception)
                    }
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
    customDeniedDialog: (@Composable ((onRetry: () -> Unit) -> Unit))? = null,
    customSettingsDialog: (@Composable ((onOpenSettings: () -> Unit) -> Unit))? = null,
    onPermissionGranted: () -> Unit,
    onError: (Exception) -> Unit
) {
    val defaultConfig = PermissionConfig.createLocalizedComposable()
    val dialogConfig = defaultCameraPermissionDialogConfig().copy(
        customDeniedDialog = customDeniedDialog,
        customSettingsDialog = customSettingsDialog
    )
    if (customPermissionHandler != null) {
        customPermissionHandler(defaultConfig)
    } else {
        RequestCameraPermission(
            dialogConfig = dialogConfig,
            onPermissionPermanentlyDenied = {
                onError(PhotoCaptureException(getStringResource(StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED)))
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
        compressionLevel = cameraCaptureConfig.compressionLevel
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
