
package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import io.github.ismoy.imagepickerkmp.presentation.ui.screens.CameraCaptureView
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.presentation.resources.getStringResource
import io.github.ismoy.imagepickerkmp.presentation.resources.StringResource

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
        ),
        enableCrop = config.enableCrop
    )
}
