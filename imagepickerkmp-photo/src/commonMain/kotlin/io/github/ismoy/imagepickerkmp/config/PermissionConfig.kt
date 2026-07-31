package io.github.ismoy.imagepickerkmp.config

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.core.I18nKonfig

/**
 * Configuration for camera permission dialogs and messages.
 *
 * Provides customizable titles, descriptions, and button texts for permission request and denial dialogs.
 */
data class PermissionConfig(
    val titleDialogConfig: String,
    val descriptionDialogConfig: String,
    val btnDialogConfig: String,
    val titleDialogDenied: String,
    val descriptionDialogDenied: String,
    val btnDialogDenied: String,
    val btnCancel: String
) {
    /**
     * Companion object providing a method to create a localized [PermissionConfig] using Compose.
     */
    companion object {
        @Composable
        fun createLocalizedComposable(): PermissionConfig {
            return PermissionConfig(
                titleDialogConfig = I18nKonfig.General.camera_permission_required,
                descriptionDialogConfig = I18nKonfig.General.camera_permission_description,
                btnDialogConfig = I18nKonfig.General.open_settings,
                titleDialogDenied = I18nKonfig.General.camera_permission_denied,
                descriptionDialogDenied = I18nKonfig.General.camera_permission_denied_description,
                btnDialogDenied = I18nKonfig.General.grant_permission,
                btnCancel = I18nKonfig.Common.cancel_option
            )
        }
    }
}
