package io.github.ismoy.imagepickerkmp

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Suppress("FunctionNaming")
@Composable
actual fun ImagePickerLauncher(
    config: ImagePickerConfig
){
    val context = LocalContext.current
    if (context !is ComponentActivity) {
        config.onError(Exception(getStringResource(StringResource.INVALID_CONTEXT_ERROR)))
        return
    }
    CameraCaptureView(
        activity = context,
        onPhotoResult = { result ->
            config.onPhotoCaptured(result)
        },
        onPhotosSelected = config.onPhotosSelected,
        onError = { exception ->
            config.onError(exception)
        },
        onDismiss = config.onDismiss,
        cameraCaptureConfig = config.cameraCaptureConfig.copy(
            preference = config.cameraCaptureConfig.preference
        )
    )
}
