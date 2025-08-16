package io.github.ismoy.imagepickerkmp.domain.config

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.presentation.resources.StringResource
import io.github.ismoy.imagepickerkmp.presentation.resources.stringResource

/**
 * Configuration for camera permission dialogs and messages.
 *
 * Provides customizable titles, descriptions, and button texts for permission request and denial dialogs.
 */
data class PermissionConfig(
    val titleDialogConfig: String = "Camera permission required",
    val descriptionDialogConfig: String = "Camera permission is required " +
            "to capture photos. Please grant it in settings",
    val btnDialogConfig: String = "Open settings",
    val titleDialogDenied: String = "Camera permission denied",
    val descriptionDialogDenied: String = "Camera permission is " +
            "required to capture photos. Please grant the permissions",
    val btnDialogDenied: String = "Grant permission"
) {
    /**
     * Companion object providing a method to create a localized [PermissionConfig] using Compose.
     */
    companion object {
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
