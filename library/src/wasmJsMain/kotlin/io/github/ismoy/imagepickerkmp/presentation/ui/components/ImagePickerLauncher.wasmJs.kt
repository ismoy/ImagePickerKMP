package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig

/**
 * WASM platform implementation of ImagePickerLauncher.
 * Note: Camera and gallery functionality is not available in WASM platform.
 * This is a stub implementation for compatibility.
 */
@Composable
actual fun ImagePickerLauncher(
    config: ImagePickerConfig
) {
    // Stub implementation - WASM doesn't support camera/gallery
    Text("ImagePicker not supported on WASM platform")
}
