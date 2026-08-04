package io.github.ismoy.imagepickerkmp.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import io.github.ismoy.imagepickerkmp.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.config.CropConfig
import io.github.ismoy.imagepickerkmp.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.config.PermissionAndConfirmationConfig
import io.github.ismoy.imagepickerkmp.ui.PlatformCameraRenderer
import io.github.ismoy.imagepickerkmp.ui.PlatformGalleryRenderer

@Composable
fun rememberImagePickerKMP(
    config: ImagePickerKMPConfig = ImagePickerKMPConfig()
): ImagePickerKMPState {
    val state = remember { ImagePickerKMPState(config) }
    val currentState = rememberUpdatedState(state)

    LaunchedEffect(Unit) {
        PlatformPrewarmer.prewarm()
    }

    val notifySuccessFn: (List<PhotoResult>) -> Unit = remember(state) {
        { photos -> currentState.value.notifySuccess(photos) }
    }
    val notifyCropPendingFn: () -> Unit = remember(state) {
        { currentState.value.notifyCropPending() }
    }

    val handleDismiss: () -> Unit = remember(state) {
        { currentState.value.dispatchDismiss() }
    }
    val handleError: (Exception) -> Unit = remember(state) {
        { e -> currentState.value.dispatchError(e) }
    }

    when (val mode = state.activeMode) {
        is PickerMode.Camera -> {
            val resolvedCam = ImagePickerConfigResolver.applyGlobalDefaults(
                mode.cameraCaptureConfig, config
            )
            PlatformCameraRenderer(
                config = ImagePickerConfig(
                    cameraCaptureConfig = resolvedCam,
                    enableCrop = mode.enableCrop,
                    onPhotoCaptured = { photo -> notifySuccessFn(listOf(photo)) },
                    onCropPending = notifyCropPendingFn,
                    onDismiss = handleDismiss,
                    onError = handleError
                )
            )
        }
        is PickerMode.Gallery -> {
            val galleryCamConfig: CameraCaptureConfig? = when {
                mode.cameraCaptureConfig != null -> ImagePickerConfigResolver.applyGlobalDefaults(
                    mode.cameraCaptureConfig, config
                )
                mode.enableCrop -> ImagePickerConfigResolver.applyGlobalDefaults(
                    CameraCaptureConfig(cropConfig = config.cropConfig),
                    config
                )
                else -> null
            }
            PlatformGalleryRenderer(
                allowMultiple = mode.allowMultiple,
                mimeTypes = mode.mimeTypes,
                selectionLimit = mode.selectionLimit,
                enableCrop = mode.enableCrop,
                includeExif = mode.includeExif,
                mimeTypeMismatchMessage = mode.mimeTypeMismatchMessage,
                cameraCaptureConfig = galleryCamConfig,
                onPhotosSelected = notifySuccessFn,
                onCropPending = notifyCropPendingFn,
                onDismiss = handleDismiss,
                onError = handleError
            )
        }
        is PickerMode.None -> Unit
    }

    return state
}

internal object ImagePickerConfigResolver {
    private val defaultPermConfig = PermissionAndConfirmationConfig()
    private val defaultCropConfig = CropConfig()

    fun applyGlobalDefaults(
        cam: CameraCaptureConfig,
        globalConfig: ImagePickerKMPConfig
    ): CameraCaptureConfig = cam.copy(
        permissionAndConfirmationConfig = if (cam.permissionAndConfirmationConfig == defaultPermConfig)
            globalConfig.permissionAndConfirmationConfig
        else
            cam.permissionAndConfirmationConfig,
        cropConfig = if (cam.cropConfig == defaultCropConfig && globalConfig.cropConfig != defaultCropConfig)
            globalConfig.cropConfig
        else
            cam.cropConfig
    )

    fun resolveEffectiveCropConfig(
        cameraCaptureConfig: CameraCaptureConfig,
        enableCrop: Boolean
    ): CameraCaptureConfig = when {
        cameraCaptureConfig.cropConfig.enabled -> cameraCaptureConfig
        enableCrop -> cameraCaptureConfig.copy(
            cropConfig = CropConfig(enabled = true, circularCrop = true, squareCrop = true)
        )
        else -> cameraCaptureConfig
    }
}
