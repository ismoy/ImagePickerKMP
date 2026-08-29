package io.github.ismoy.imagepickerkmp.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import io.github.ismoy.imagepickerkmp.camera.AndroidPhotoCaptureManager
import io.github.ismoy.imagepickerkmp.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.I18nKonfig.Errors.camera_unavailable_error
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionManager
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionStatus
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionType
import io.github.ismoy.imagepickerkmp.picker.ImagePickerConfigResolver
import io.github.ismoy.imagepickerkmp.picker.PhotoCaptureException
import io.github.ismoy.imagepickerkmp.picker.PhotoResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun rememberPlatformCameraState(
    config: ImagePickerConfig,
    permissionManager: PermissionManager,
    manager: AndroidPhotoCaptureManager
): PlatformCameraState {
    val scope = rememberCoroutineScope()
    return remember(config, permissionManager, manager, scope) {
        PlatformCameraState(config, permissionManager, manager, scope)
    }
}

@Stable
internal class PlatformCameraState(
    val config: ImagePickerConfig,
    private val permissionManager: PermissionManager,
    val manager: AndroidPhotoCaptureManager,
    private val scope: CoroutineScope
) {
    var showRationale by mutableStateOf(false)
    var showSettingsDialog by mutableStateOf(false)
    var waitingForSettings by mutableStateOf(false)
    var permissionsGranted by mutableStateOf(false)

    var photoForCrop by mutableStateOf<PhotoResult?>(null)
    var showCropView by mutableStateOf(false)
    var cropCancelled by mutableStateOf(false)
    
    val effectiveCamConfig: CameraCaptureConfig = ImagePickerConfigResolver.resolveEffectiveCropConfig(
        config.cameraCaptureConfig, config.enableCrop
    )

    fun onResume() {
        if (waitingForSettings) {
            scope.launch {
                val status = permissionManager.checkPermission(PermissionType.Camera)
                waitingForSettings = false
                showSettingsDialog = false
                if (status is PermissionStatus.Granted) {
                    permissionsGranted = true
                } else {
                    config.onDismiss()
                }
            }
        }
    }

    fun checkInitialPermission(launchPermission: (String) -> Unit) {
        scope.launch {
            when (permissionManager.checkPermission(PermissionType.Camera)) {
                is PermissionStatus.Granted -> permissionsGranted = true
                else -> launchPermission(android.Manifest.permission.CAMERA)
            }
        }
    }

    fun onPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            permissionsGranted = true
        } else {
            scope.launch {
                val status = permissionManager.checkPermission(PermissionType.Camera)
                if (status is PermissionStatus.DeniedPermanently) showSettingsDialog = true
                else showRationale = true
            }
        }
    }

    fun onCameraResult(success: Boolean) {
        manager.onCaptureResult(success, scope)
        if (!success) config.onDismiss()
    }

    fun startCamera(launchCamera: (Intent) -> Unit) {
        permissionsGranted = false
        manager.setPendingCallbacks(
            onPhotoResult = { photo ->
                val shouldCrop = effectiveCamConfig.cropConfig.enabled || config.enableCrop
                if (shouldCrop) {
                    config.onCropPending()
                    photoForCrop = photo
                    showCropView = true
                } else {
                    config.onPhotoCaptured(photo)
                }
            },
            onError = config.onError,
            compressionLevel = effectiveCamConfig.compressionLevel,
            includeExif = effectiveCamConfig.includeExif,
            redactGpsData = effectiveCamConfig.redactGpsData
        )
        // Launched from a LaunchedEffect, so anything thrown here would kill the consuming app.
        try {
            val (intent, _) = manager.buildCaptureIntent()
            launchCamera(intent)
        } catch (e: ActivityNotFoundException) {
            reportLaunchFailure(PhotoCaptureException(camera_unavailable_error, e), config.onError, config.onDismiss)
        } catch (e: Exception) {
            reportLaunchFailure(e, config.onError, config.onDismiss)
        }
    }

    fun acceptCrop(croppedResult: PhotoResult) {
        config.onPhotoCaptured(croppedResult)
        showCropView = false
        photoForCrop = null
    }

    fun skipCrop() {
        photoForCrop?.let(config.onPhotoCaptured)
        showCropView = false
        photoForCrop = null
    }

    fun cancelCrop() {
        showCropView = false
        photoForCrop = null
        cropCancelled = true
    }

    fun onCropCancelledEffectHandled() {
        cropCancelled = false
        config.onDismiss()
    }

    fun acceptRationale(launchPermission: (String) -> Unit) {
        showRationale = false
        launchPermission(android.Manifest.permission.CAMERA)
    }

    fun dismissRationale() {
        showRationale = false
        config.onDismiss()
    }

    fun acceptSettings(context: Context) {
        showSettingsDialog = false
        waitingForSettings = true
        openAppSettings(context)
    }

    fun dismissSettings() {
        showSettingsDialog = false
        config.onDismiss()
    }
}
