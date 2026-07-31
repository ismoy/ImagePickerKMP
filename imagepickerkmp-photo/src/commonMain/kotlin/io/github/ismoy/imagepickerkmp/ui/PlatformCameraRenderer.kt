package io.github.ismoy.imagepickerkmp.ui

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.config.ImagePickerConfig

@Composable
internal expect fun PlatformCameraRenderer(
    config: ImagePickerConfig
)
