package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.ismoy.imagepickerkmp.presentation.ui.screens.CameraCaptureView
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.domain.config.CropConfig
import io.github.ismoy.imagepickerkmp.presentation.ui.extensions.activity
import org.jetbrains.compose.resources.stringResource
import imagepickerkmp.library.generated.resources.Res
import imagepickerkmp.library.generated.resources.invalid_context_error

@Suppress("FunctionNaming")
@Composable
actual fun ImagePickerLauncher(
    config: ImagePickerConfig
) {
    val context = LocalContext.current
    val activity = context.activity
    if (activity !is ComponentActivity) {
        config.onError(Exception(stringResource(Res.string.invalid_context_error)))
        return
    }

    val effectiveCropConfig = if (config.cameraCaptureConfig.cropConfig.enabled) {
        config.cameraCaptureConfig.cropConfig
    } else if (config.enableCrop) {
        CropConfig(enabled = true, circularCrop = true, squareCrop = true)
    } else {
        config.cameraCaptureConfig.cropConfig
    }

    Dialog(
        onDismissRequest = { config.onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        CameraCaptureView(
            activity = activity,
            onPhotoResult = { result -> config.onPhotoCaptured(result) },
            onError = { exception -> config.onError(exception) },
            onDismiss = config.onDismiss,
            cameraCaptureConfig = config.cameraCaptureConfig.copy(cropConfig = effectiveCropConfig),
            enableCrop = config.cameraCaptureConfig.cropConfig.enabled || config.enableCrop
        )
    }
}
