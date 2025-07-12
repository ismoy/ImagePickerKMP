package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable

data class PermissionConfig(
    val titleDialogConfig: String = "Camera permission required",
    val descriptionDialogConfig: String = "Camera permission is required to capture photos. Please grant it in settings",
    val btnDialogConfig: String = "Open settings",
    val titleDialogDenied: String = "Camera permission denied",
    val descriptionDialogDenied: String = "Camera permission is required to capture photos. Please grant the permissions",
    val btnDialogDenied: String = "Grant permission"
) {
    companion object {
        /**
         * Crea una configuración de permisos con strings traducidos según el idioma del dispositivo
         */
        fun createLocalized(): PermissionConfig {
            return PermissionConfig(
                titleDialogConfig = getStringResource(StringResource.CAMERA_PERMISSION_REQUIRED),
                descriptionDialogConfig = getStringResource(StringResource.CAMERA_PERMISSION_DESCRIPTION),
                btnDialogConfig = getStringResource(StringResource.OPEN_SETTINGS),
                titleDialogDenied = getStringResource(StringResource.CAMERA_PERMISSION_DENIED),
                descriptionDialogDenied = getStringResource(StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION),
                btnDialogDenied = getStringResource(StringResource.GRANT_PERMISSION)
            )
        }
        
        /**
         * Crea una configuración de permisos con strings traducidos usando el contexto de Compose
         * Esta función debe ser llamada dentro de un Composable
         */
        @Composable
        fun createLocalizedComposable(): PermissionConfig {
            return PermissionConfig(
                titleDialogConfig = stringResource(StringResource.CAMERA_PERMISSION_REQUIRED),
                descriptionDialogConfig = stringResource(StringResource.CAMERA_PERMISSION_DESCRIPTION),
                btnDialogConfig = stringResource(StringResource.OPEN_SETTINGS),
                titleDialogDenied = stringResource(StringResource.CAMERA_PERMISSION_DENIED),
                descriptionDialogDenied = stringResource(StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION),
                btnDialogDenied = stringResource(StringResource.GRANT_PERMISSION)
            )
        }
    }
}