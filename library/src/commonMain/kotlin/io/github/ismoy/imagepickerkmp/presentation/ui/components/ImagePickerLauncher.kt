package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig

@Suppress("FunctionNaming")
@Composable
expect fun ImagePickerLauncher(
    config: ImagePickerConfig
)
