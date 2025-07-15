package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable

/**
 * Enum class que define todos los identificadores de strings utilizados en la librería.
 * Esto evita errores con textos hardcodeados y proporciona type safety.
 * 
 * Idiomas soportados:
 * - Inglés (en)
 * - Español (es) 
 * - Francés (fr)
 */
enum class StringResource {
    CAMERA_PERMISSION_REQUIRED,
    CAMERA_PERMISSION_DESCRIPTION,
    OPEN_SETTINGS,
    CAMERA_PERMISSION_DENIED,
    CAMERA_PERMISSION_DENIED_DESCRIPTION,
    GRANT_PERMISSION,
    CAMERA_PERMISSION_PERMANENTLY_DENIED,
    IMAGE_CONFIRMATION_TITLE,
    ACCEPT_BUTTON,
    RETRY_BUTTON,
    SELECT_OPTION_DIALOG_TITLE,
    TAKE_PHOTO_OPTION,
    SELECT_FROM_GALLERY_OPTION,
    CANCEL_OPTION,
    PREVIEW_IMAGE_DESCRIPTION,
    HD_QUALITY_DESCRIPTION,
    SD_QUALITY_DESCRIPTION,
    INVALID_CONTEXT_ERROR,
    PHOTO_CAPTURE_ERROR,
    GALLERY_SELECTION_ERROR,
    PERMISSION_ERROR
}

@Composable
internal expect fun stringResource(id: StringResource): String