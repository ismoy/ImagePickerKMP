package io.github.ismoy.imagepickerkmp.features.imagepicker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.CropConfig
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.domain.config.PermissionAndConfirmationConfig
import io.github.ismoy.imagepickerkmp.domain.config.UiConfig
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.features.imagepicker.config.ImagePickerKMPConfig
import io.github.ismoy.imagepickerkmp.features.imagepicker.model.PickerMode
import io.github.ismoy.imagepickerkmp.features.imagepicker.state.ImagePickerKMPState
import io.github.ismoy.imagepickerkmp.presentation.ui.components.GalleryPickerLauncher
import io.github.ismoy.imagepickerkmp.presentation.ui.components.ImagePickerLauncher

/**
 * Creates and remembers an [ImagePickerKMPState] that is automatically connected to the
 * composition lifecycle.
 *
 * No additional `Render()` call is required. The picker is self-managed and launches
 * automatically when [ImagePickerKMPState.launchCamera] or [ImagePickerKMPState.launchGallery]
 * is invoked.
 *
 * ### Basic usage
 * ```kotlin
 * val picker = rememberImagePickerKMP()
 *
 * Button(onClick = { picker.launchCamera() }) { Text("Camera") }
 * Button(onClick = { picker.launchGallery(allowMultiple = true) }) { Text("Gallery") }
 *
 * when (val result = picker.result) {
 *     is ImagePickerResult.Success -> result.photos.forEach { photo -> /* consume photo */ }
 *     is ImagePickerResult.Error   -> Text("Error: ${result.exception.message}")
 *     is ImagePickerResult.Dismissed -> { /* user cancelled */ }
 *     else -> Unit
 * }
 * ```
 *
 * ### Per-launch overrides
 * Any parameter passed directly to [ImagePickerKMPState.launchCamera] or
 * [ImagePickerKMPState.launchGallery] overrides the global [config] for that single invocation
 * only, without mutating the remembered configuration.
 *
 * @param config Global configuration applied to every launch unless overridden per-call.
 *   Defaults to [ImagePickerKMPConfig] with platform defaults for all options.
 *   See [ImagePickerKMPConfig] for the full list of configurable properties.
 *
 * @return An [ImagePickerKMPState] exposing [ImagePickerKMPState.result] as observable reactive
 *   state and the [ImagePickerKMPState.launchCamera] / [ImagePickerKMPState.launchGallery]
 *   launch methods.
 *
 * @see ImagePickerKMPState
 * @see ImagePickerKMPConfig
 */
@Composable
fun rememberImagePickerKMP(
    config: ImagePickerKMPConfig = ImagePickerKMPConfig()
): ImagePickerKMPState {
    val state = remember { ImagePickerKMPState(config) }
    val currentState = rememberUpdatedState(state)
    val defaultUiConfig = remember { UiConfig() }
    val defaultPermConfig = remember { PermissionAndConfirmationConfig() }
    val notifySuccessFn: (List<PhotoResult>) -> Unit = remember(state) {
        { photos -> currentState.value.notifySuccess(photos) }
    }
    val notifyCropPendingFn: () -> Unit = remember(state) {
        { currentState.value.notifyCropPending() }
    }
    val onDismissDefault: () -> Unit = remember(state) {
        { currentState.value.notifyDismiss() }
    }
    val onErrorDefault: (Exception) -> Unit = remember(state) {
        { e -> currentState.value.onError(e) }
    }
    val defaultCropConfig = remember { CropConfig() }
    val applyGlobalDefaults: (CameraCaptureConfig) -> CameraCaptureConfig = remember(config) {
        { cam ->
            cam.copy(
                uiConfig = if (cam.uiConfig == defaultUiConfig) config.uiConfig else cam.uiConfig,
                permissionAndConfirmationConfig = if (
                    cam.permissionAndConfirmationConfig == defaultPermConfig
                ) config.permissionAndConfirmationConfig else cam.permissionAndConfirmationConfig,
                cropConfig = if (cam.cropConfig == defaultCropConfig && config.cropConfig != defaultCropConfig) {
                    config.cropConfig
                } else {
                    cam.cropConfig
                }
            )
        }
    }

    @Suppress("DEPRECATION")
    when (val mode = state.activeMode) {
        is PickerMode.Camera -> {
            ImagePickerLauncher(
                config = ImagePickerConfig(
                    cameraCaptureConfig = applyGlobalDefaults(mode.cameraCaptureConfig),
                    enableCrop = mode.enableCrop,
                    onPhotoCaptured = { photo -> notifySuccessFn(listOf(photo)) },
                    onCropPending = notifyCropPendingFn,
                    onDismiss = mode.onDismiss ?: onDismissDefault,
                    onError = mode.onError ?: onErrorDefault
                )
            )
        }
        is PickerMode.Gallery -> {
            val galleryCamConfig: CameraCaptureConfig? = when {
                mode.cameraCaptureConfig != null -> applyGlobalDefaults(mode.cameraCaptureConfig)
                mode.enableCrop -> CameraCaptureConfig(cropConfig = config.cropConfig)
                else -> null
            }
            GalleryPickerLauncher(
                allowMultiple = mode.allowMultiple,
                mimeTypes = mode.mimeTypes,
                selectionLimit = mode.selectionLimit,
                enableCrop = mode.enableCrop,
                includeExif = mode.includeExif,
                mimeTypeMismatchMessage = mode.mimeTypeMismatchMessage,
                cameraCaptureConfig = galleryCamConfig,
                onPhotosSelected = notifySuccessFn,
                onCropPending = notifyCropPendingFn,
                onDismiss = mode.onDismiss ?: onDismissDefault,
                onError = mode.onError ?: onErrorDefault
            )
        }
        is PickerMode.None -> Unit
    }

    return state
}
