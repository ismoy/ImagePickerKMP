
package io.github.ismoy.imagepickerkmp
import androidx.compose.runtime.Composable

@Composable
fun defaultCameraPermissionDialogConfig(): CameraPermissionDialogConfig {
    val defaultConfig = PermissionConfig.createLocalizedComposable()
    return CameraPermissionDialogConfig(
        titleDialogConfig = defaultConfig.titleDialogConfig,
        descriptionDialogConfig = defaultConfig.descriptionDialogConfig,
        btnDialogConfig = defaultConfig.btnDialogConfig,
        titleDialogDenied = defaultConfig.titleDialogDenied,
        descriptionDialogDenied = defaultConfig.descriptionDialogDenied,
        btnDialogDenied = defaultConfig.btnDialogDenied,
        customDeniedDialog = null,
        customSettingsDialog = null
    )
}

