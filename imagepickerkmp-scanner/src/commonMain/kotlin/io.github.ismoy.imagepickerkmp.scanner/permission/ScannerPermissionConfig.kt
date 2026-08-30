package io.github.ismoy.imagepickerkmp.scanner.permission

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.scanner.I18nKonfig

data class ScannerPermissionConfig(
    val titleDialogConfig: String = I18nKonfig.General.camera_permission_required,
    val descriptionDialogConfig: String = I18nKonfig.General.camera_permission_scanner_description,
    val btnDialogConfig: String = I18nKonfig.General.gallery_btn_settings,
    val titleDialogDenied: String = I18nKonfig.General.camera_permission_denied,
    val descriptionDialogDenied: String = I18nKonfig.General.camera_permission_denied_scanner_description,
    val btnDialogDenied: String = I18nKonfig.General.grant_permission,
    val cancelButtonText: String = I18nKonfig.Common.cancel_option,
    val customDeniedDialog: (@Composable (onConfirm: () -> Unit, onCancel: () -> Unit) -> Unit)? = null,
    val customSettingsDialog: (@Composable (onConfirm: () -> Unit, onCancel: () -> Unit) -> Unit)? = null
)
