package io.github.ismoy.imagepickerkmp.domain.utils
import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.domain.config.CameraPermissionDialogConfig
import io.github.ismoy.imagepickerkmp.domain.config.PermissionConfig

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

