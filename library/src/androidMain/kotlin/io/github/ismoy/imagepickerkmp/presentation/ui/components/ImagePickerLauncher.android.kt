
package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import io.github.ismoy.imagepickerkmp.presentation.ui.screens.CameraCaptureView
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.presentation.resources.getStringResource
import io.github.ismoy.imagepickerkmp.presentation.resources.StringResource
import io.github.ismoy.imagepickerkmp.presentation.ui.extensions.activity

@Suppress("FunctionNaming")
@Composable
actual fun ImagePickerLauncher(
    config: ImagePickerConfig
){
    val context = LocalContext.current
    val activity = context.activity
    if (activity !is ComponentActivity) {
        config.onError(Exception(getStringResource(StringResource.INVALID_CONTEXT_ERROR)))
        return
    }
    CameraCaptureView(
        activity = activity,
        onPhotoResult = { result ->
            config.onPhotoCaptured(result)
        },
        onPhotosSelected = config.onPhotosSelected,
        onError = { exception ->
            config.onError(exception)
        },
        onDismiss = config.onDismiss,
        cameraCaptureConfig = config.cameraCaptureConfig.copy(
            preference = config.cameraCaptureConfig.preference,
            includeExif = config.cameraCaptureConfig.includeExif  // ← FALTABA ESTO!
        ),
        enableCrop = config.enableCrop
    )
}
