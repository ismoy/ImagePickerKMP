package io.github.ismoy.imagepickerkmp

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable

@Suppress("FunctionNaming")
@Composable
actual fun ImagePickerLauncher(
    context: Any?,
    config: ImagePickerConfig
){
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
        cameraCaptureConfig = config.cameraCaptureConfig.copy(
            preference = config.cameraCaptureConfig.preference
        )
    )
}
