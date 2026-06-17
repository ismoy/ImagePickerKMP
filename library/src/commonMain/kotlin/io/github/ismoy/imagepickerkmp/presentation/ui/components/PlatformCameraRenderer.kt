package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig

/**
 * Internal composable that launches the platform-specific camera picker.
 *
 * This is an implementation detail used by
 * [rememberImagePickerKMP][io.github.ismoy.imagepickerkmp.features.imagepicker.ui.rememberImagePickerKMP].
 * It is not part of the public API.
 *
 * @param config Camera configuration including callbacks and capture settings.
 */
@Composable
internal expect fun PlatformCameraRenderer(
    config: ImagePickerConfig
)
