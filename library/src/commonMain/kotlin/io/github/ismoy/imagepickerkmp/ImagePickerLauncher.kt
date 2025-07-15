
package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable

@Suppress("FunctionNaming")
@Composable
expect fun ImagePickerLauncher(
    context: Any?,
    config: ImagePickerConfig
)
