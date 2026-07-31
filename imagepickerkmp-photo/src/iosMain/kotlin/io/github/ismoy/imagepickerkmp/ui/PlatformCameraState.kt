package io.github.ismoy.imagepickerkmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import io.github.ismoy.imagepickerkmp.camera.PhotoCaptureOrchestrator
import io.github.ismoy.imagepickerkmp.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionManager
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionStatus
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionType
import io.github.ismoy.imagepickerkmp.picker.PhotoResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType

@Composable
internal fun rememberPlatformCameraState(
    config: ImagePickerConfig,
    permissionManager: PermissionManager
): PlatformCameraState {
    val scope = rememberCoroutineScope()
    return remember(config, permissionManager, scope) {
        PlatformCameraState(config, permissionManager, scope)
    }
}

@Stable
internal class PlatformCameraState(
    val config: ImagePickerConfig,
    private val permissionManager: PermissionManager,
    private val scope: CoroutineScope
) {
    var selectedPhotoForCrop by mutableStateOf<PhotoResult?>(null)
    var showCropView by mutableStateOf(false)
    var cropCancelled by mutableStateOf(false)

    var showSettingsDialog by mutableStateOf(false)
    var isProcessingSettingsAction by mutableStateOf(false)
    var hasNavigatedToSettings by mutableStateOf(false)

    fun onAppBecomeActive() {
        scope.launch {
            val status = permissionManager.checkPermission(PermissionType.Camera)
            when {
                status is PermissionStatus.Granted -> {
                    isProcessingSettingsAction = false
                    hasNavigatedToSettings = false
                    showSettingsDialog = false
                    config.onDismiss()
                }
                hasNavigatedToSettings && isProcessingSettingsAction -> {
                    isProcessingSettingsAction = false
                    hasNavigatedToSettings = false
                    showSettingsDialog = false
                    config.onDismiss()
                }
                else -> Unit
            }
        }
    }

    fun onAppResignActive() {
        if (isProcessingSettingsAction && !hasNavigatedToSettings) {
            hasNavigatedToSettings = true
            showSettingsDialog = false
        }
    }

    fun onCropCancelledEffectHandled() {
        cropCancelled = false
        config.onDismiss()
    }

    fun launchCameraFlow() {
        scope.launch {
        if (!UIImagePickerController.isSourceTypeAvailable(
                UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
            )
        ) {
            config.onError(Exception("Camera not available on this device"))
            config.onDismiss()
            return@launch
        }

        val cameraCaptureConfig = config.cameraCaptureConfig
        val compressionLevel = cameraCaptureConfig.compressionLevel
        val includeExif = cameraCaptureConfig.includeExif

        val onPhotoCapturedHandler: (PhotoResult) -> Unit = { result ->
            val shouldShowCrop = cameraCaptureConfig.cropConfig.enabled || config.enableCrop
            if (shouldShowCrop) {
                config.onCropPending()
                selectedPhotoForCrop = result
                showCropView = true
            } else {
                config.onPhotoCaptured(result)
            }
        }

        when (permissionManager.checkPermission(PermissionType.Camera)) {
            is PermissionStatus.Granted -> {
                PhotoCaptureOrchestrator.launchCamera(
                    onPhotoCaptured = onPhotoCapturedHandler,
                    onError = { e -> config.onError(e); config.onDismiss() },
                    onDismiss = { config.onDismiss() },
                    compressionLevel = compressionLevel,
                    includeExif = includeExif
                )
            }
            is PermissionStatus.Denied -> {
                val requested = permissionManager.requestPermission(PermissionType.Camera)
                if (requested is PermissionStatus.Granted) {
                    PhotoCaptureOrchestrator.launchCamera(
                        onPhotoCaptured = onPhotoCapturedHandler,
                        onError = { e -> config.onError(e); config.onDismiss() },
                        onDismiss = { config.onDismiss() },
                        compressionLevel = compressionLevel,
                        includeExif = includeExif
                    )
                } else {
                    showSettingsDialog = true
                }
            }
            is PermissionStatus.DeniedPermanently -> {
                showSettingsDialog = true
            }
        }
        }
    }

    fun acceptCrop(croppedResult: PhotoResult) {
        config.onPhotoCaptured(croppedResult)
        showCropView = false
        selectedPhotoForCrop = null
    }

    fun cancelCrop() {
        showCropView = false
        selectedPhotoForCrop = null
        cropCancelled = true
    }

    fun openSettings() {
        if (!isProcessingSettingsAction) {
            isProcessingSettingsAction = true
            hasNavigatedToSettings = true
            showSettingsDialog = false
            io.github.ismoy.imagepickerkmp.ui.openSettings()
        }
    }

    fun dismissSettings() {
        showSettingsDialog = false
        config.onDismiss()
    }
}

